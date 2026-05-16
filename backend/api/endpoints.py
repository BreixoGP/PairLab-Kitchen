import json
from django.contrib.auth.hashers import make_password, check_password
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from api.models import AppUser, UserSession, Ingredient
from api.services import get_ingredient_profile, get_candidates, get_top_combos, score_pair


@csrf_exempt
@csrf_exempt
def register(request):

	if request.method != 'POST':
		return JsonResponse({"error": "HTTP method not allowed"}, status=405)

	try:
		body = json.loads(request.body)
	except json.JSONDecodeError:
		return JsonResponse({"error": "Invalid JSON"}, status=400)

	username = body.get("username", "").strip()
	email = body.get("email", "").strip()
	password = body.get("password", "").strip()

	if not username or not email or not password:
		return JsonResponse({"error": "Missing fields"}, status=400)

	if len(username) < 3:
		return JsonResponse({"error": "Username too short"}, status=400)

	if len(username) > 30:
		return JsonResponse({"error": "Username too long"}, status=400)

	if "@" not in email:
		return JsonResponse({"error": "Invalid Email"}, status=400)

	if len(password) < 4:
		return JsonResponse({"error": "Password must be at least 4 characters"}, status=400)

	if AppUser.objects.filter(username=username).exists():
		return JsonResponse({"error": "Username already exists"}, status=409)

	if AppUser.objects.filter(email=email).exists():
		return JsonResponse({"error": "Email already in use"}, status=409)

	user = AppUser(
		username=username,
		email=email
	)

	user.set_password(password)
	user.save()

	return JsonResponse({
		"message": "User created successfully"
	}, status=201)

@csrf_exempt
def login(request):
	if request.method != "POST":
		return JsonResponse({"error": "HTTP method not allowed"}, status=405)

	try:
		body = json.loads(request.body)
	except json.JSONDecodeError:
		return JsonResponse({"error": "Invalid JSON"}, status=400)

	username = body.get("username")
	password = body.get("password")

	if not username or not password:
		return JsonResponse({"error": "Missing fields"}, status=400)

	try:
		user = AppUser.objects.get(username=username)
	except AppUser.DoesNotExist:
		return JsonResponse({"error": "User not found"}, status=404)

	if not user.check_password(password):
		return JsonResponse({"error": "Incorrect password"}, status=401)

	UserSession.objects.filter(user=user).delete()

	session = UserSession.objects.create(user=user)

	return JsonResponse({
		"message": "Login successful",
		"token": session.token,
		"user_id": user.pk,
		"username": user.username
	}, status=200)

@csrf_exempt
def logout(request):
	user = authenticate_request(request)
	if not user:
		return JsonResponse({"error": "Unauthorized"}, status=401)

	UserSession.objects.filter(user=user).delete()

	return JsonResponse({"message": "Logout successful"}, status=200)
@csrf_exempt
def user_detail(request, id):

	user = authenticate_request(request)

	if not user:
		return JsonResponse({"error": "Unauthorized"}, status=401)

	if user.pk != id:
		return JsonResponse({"error": "You cannot access another user"}, status=403)

	if request.method == 'GET':
		return JsonResponse({
			"username": user.username,
			"email": user.email
		}, status=200)

	elif request.method == 'PUT':

		try:
			body = json.loads(request.body)
		except json.JSONDecodeError:
			return JsonResponse({"error": "Invalid JSON"}, status=400)

		if not body:
			return JsonResponse({"message": "No changes detected"}, status=200)

		changed = False

		username = body.get("username", None)
		email = body.get("email", None)
		old_password = body.get("old_password", None)
		new_password = body.get("new_password", None)

		if username is not None:
			username = username.strip()

			if username:
				if len(username) > 150:
					return JsonResponse({"error": "Username too long"}, status=400)

				if AppUser.objects.filter(username=username).exclude(pk=user.pk).exists():
					return JsonResponse({"error": "Username already exists"}, status=409)

				if username != user.username:
					user.username = username
					changed = True

		if email is not None:
			email = email.strip()

			if email:
				if AppUser.objects.filter(email=email).exclude(pk=user.pk).exists():
					return JsonResponse({"error": "Email already in use"}, status=409)

				if email != user.email:
					user.email = email
					changed = True

		if old_password is not None or new_password is not None:

			if not old_password or not new_password:
				return JsonResponse({"error": "Both old_password and new_password are required"}, status=400)

			old_password = old_password.strip()
			new_password = new_password.strip()

			if not old_password or not new_password:
				return JsonResponse({"error": "Password fields cannot be empty"}, status=400)

			if not user.check_password(old_password):
				return JsonResponse({"error": "Incorrect password"}, status=401)

			if len(new_password) < 4:
				return JsonResponse({"error": "Password must be at least 4 characters"}, status=400)

			user.set_password(new_password)
			changed = True

		if not changed:
			return JsonResponse({"message": "No changes detected"}, status=200)

		user.save()

		return JsonResponse({"message": "User updated successfully"}, status=200)

	elif request.method == 'DELETE':

		user.delete()

		return JsonResponse({"message": "User deleted successfully"}, status=200)

	else:

		return JsonResponse({"error": "HTTP method not allowed"}, status=405)
def authenticate_request(request):
	auth_header = request.headers.get("Authorization")
	if not auth_header or not auth_header.startswith("Token "):
		return None

	token = auth_header.split(" ")[1]
	try:
		session = UserSession.objects.get(token=token)
		return session.user
	except UserSession.DoesNotExist:
		return None


@csrf_exempt
def ingredient_families(request):
	user = authenticate_request(request)
	if not user:
		return JsonResponse({"error": "Unauthorized"}, status=401)

	families = Ingredient.objects.values_list("family", flat=True).distinct()
	return JsonResponse({"families": list(families)}, status=200)

@csrf_exempt
def ingredients_list(request):
	user = authenticate_request(request)
	if not user:
		return JsonResponse({"error": "Unauthorized"}, status=401)

	if request.method != 'GET':
		return JsonResponse({"error": "HTTP method not allowed"}, status=405)

	search_query = request.GET.get("search", None)
	family_filter = request.GET.get("family", None)

	ingredients_qs = Ingredient.objects.all()

	if search_query:
		ingredients_qs = ingredients_qs.filter(name__icontains=search_query)

	if family_filter:
		families = [f.strip() for f in family_filter.split(",")]
		ingredients_qs = ingredients_qs.filter(family__in=families)

	data = []

	for ing in ingredients_qs:
		profile = get_ingredient_profile(ing)

		data.append({
			"id": profile["id"],
			"name": profile["name"],
			"family": profile["family"],
			"flavours": profile["flavours"],
			"aromas": profile["aromas"]
		})

	return JsonResponse({"ingredients": data}, status=200)

@csrf_exempt
def pairings_list(request):
	user = authenticate_request(request)
	if not user:
		return JsonResponse({"error": "Unauthorized"}, status=401)

	if request.method != "POST":
		return JsonResponse({"error": "HTTP method not allowed"}, status=405)

	try:
		body = json.loads(request.body)
	except json.JSONDecodeError:
		return JsonResponse({"error": "Invalid JSON"}, status=400)

	ingredient_id = body.get("ingredient_id")
	combo_size = body.get("combo_size", 1)
	family_filter = body.get("family_filter", None)

	if not ingredient_id:
		return JsonResponse({"error": "No ingredient provided"}, status=400)

	try:
		base_ingredient = Ingredient.objects.get(pk=ingredient_id)
	except Ingredient.DoesNotExist:
		return JsonResponse({"error": "Ingredient not found"}, status=404)


	base_profile = get_ingredient_profile(base_ingredient)

	candidate_profiles = get_candidates(
		base_ingredient,
		family_filter=family_filter
	)

	combos = get_top_combos(base_profile, candidate_profiles, combo_size)

	results = []

	for combo in combos:
		combo_data = [
			{
				"id": ing["id"],
				"name": ing["name"]
			}
			for ing in combo
		]

		score = 1.0
		for i, ing1 in enumerate(combo):
			for ing2 in combo[i+1:]:
				score *= score_pair(ing1, ing2)

		results.append({
			"combo": combo_data,
			"score": score
		})

	return JsonResponse({"results": results}, status=200)