# api/services.py

from itertools import combinations
from api.models import Ingredient, IngredientFlavourRelation, IngredientAromaRelation
from api.compatibility import FLAVOUR_RULES, AROMA_RULES, FAMILY_RULES, FLAVOUR_AROMA_RULES


def get_ingredient_profile(ingredient):

    profile = {
        "id": ingredient.id,
        "name": ingredient.name,
        "family": ingredient.family,
        "flavours": [],
        "aromas": []
    }

    flavour_relations = IngredientFlavourRelation.objects.filter(ingredient=ingredient)

    for rel in flavour_relations:
        profile["flavours"].append({
            "name": rel.flavour.name.lower().strip(),
            "intensity": rel.intensity or 0
        })

    aroma_relations = IngredientAromaRelation.objects.filter(ingredient=ingredient)

    for rel in aroma_relations:
        profile["aromas"].append({
            "name": rel.aroma.name.lower().strip(),
            "intensity": rel.intensity or 0
        })

    return profile

def score_pair(profile1, profile2):

    flavour_score = 1.0
    aroma_score = 1.0
    flavour_aroma_score = 1.0
    family_score = 1.0

    f1_list = profile1.get("flavours", [])
    f2_list = profile2.get("flavours", [])

    a1_list = profile1.get("aromas", [])
    a2_list = profile2.get("aromas", [])

    if f1_list and f2_list:
        total = 0
        count = 0
        for f1 in f1_list:
            for f2 in f2_list:
                key = tuple(sorted((f1["name"], f2["name"])))
                rule = FLAVOUR_RULES.get(key, 1.0)
                intensity_factor = 1 + (f1["intensity"] * f2["intensity"]) / 100
                total += rule * intensity_factor
                count += 1
        flavour_score = total / count if count else 1.0

    if a1_list and a2_list:
        total = 0
        count = 0
        for a1 in a1_list:
            for a2 in a2_list:
                key = tuple(sorted((a1["name"], a2["name"])))
                rule = AROMA_RULES.get(key, 1.0)
                intensity_factor = 1 + (a1["intensity"] * a2["intensity"]) / 100
                total += rule * intensity_factor
                count += 1
        aroma_score = total / count if count else 1.0

    total = 0
    count = 0
    for f in f1_list:
        for a in a2_list:
            rule = FLAVOUR_AROMA_RULES.get((f["name"], a["name"]), 1.0)
            intensity_factor = 1 + (f["intensity"] * a["intensity"]) / 100
            total += rule * intensity_factor
            count += 1
    for f in f2_list:
        for a in a1_list:
            rule = FLAVOUR_AROMA_RULES.get((f["name"], a["name"]), 1.0)
            intensity_factor = 1 + (f["intensity"] * a["intensity"]) / 100
            total += rule * intensity_factor
            count += 1
    flavour_aroma_score = total / count if count else 1.0

    key_family = tuple(sorted((profile1.get("family", ""), profile2.get("family", ""))))
    family_score = FAMILY_RULES.get(key_family, 1.0)

    final_score = flavour_score * aroma_score * flavour_aroma_score * family_score
    return final_score


def score_combo(profiles):

    if not profiles or len(profiles) < 2:
        return 1.0

    scores = []
    for i, p1 in enumerate(profiles):
        for p2 in profiles[i+1:]:
            scores.append(score_pair(p1, p2))

    return sum(scores) / len(scores)


def generate_combos(base_profile, candidate_profiles, combo_size):

    combos = []
    for combo in combinations(candidate_profiles, combo_size):
        full_combo = [base_profile] + list(combo)

        # Calcular scores de pares y descartar si alguno <= 0
        if all(score_pair(p1, p2) > 0 for i, p1 in enumerate(full_combo)
               for p2 in full_combo[i+1:]):
            combos.append(full_combo)

    return combos


def get_top_combos(base_profile, candidate_profiles, combo_size, top_n=20):

    if combo_size == 1:
        scored = [(c, score_pair(base_profile, c)) for c in candidate_profiles]
        scored.sort(key=lambda x: x[1], reverse=True)
        return [[base_profile, s[0]] for s in scored[:top_n]]

    candidate_combos = generate_combos(base_profile, candidate_profiles, combo_size)

    scored_combos = [(combo, score_combo(combo)) for combo in candidate_combos]
    scored_combos.sort(key=lambda x: x[1], reverse=True)

    return [c[0] for c in scored_combos[:top_n]]

def get_candidates(base_ingredient, top_n_candidates=15, family_filter=None):

    all_ingredients = Ingredient.objects.exclude(id=base_ingredient.id)
    if family_filter:
        all_ingredients = all_ingredients.filter(family__in=family_filter)

    base_profile = get_ingredient_profile(base_ingredient)
    candidate_profiles = [(ing, get_ingredient_profile(ing)) for ing in all_ingredients]

    scored = []
    for ing, profile in candidate_profiles:
        pair_score = score_pair(base_profile, profile)
        scored.append((ing, profile, pair_score))

    scored.sort(key=lambda x: x[2], reverse=True)

    return [p[1] for p in scored[:top_n_candidates]]