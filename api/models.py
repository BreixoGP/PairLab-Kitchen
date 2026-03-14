import secrets

from django.contrib.auth.models import AbstractUser
from django.db import models

class AppUser(AbstractUser):
	email = models.EmailField(unique=True)

	# Eliminamos grupos y permisos
	# username + email + password ya vienen de AbstractUser

	def __str__(self):
		return self.username

class UserSession(models.Model):
	user = models.OneToOneField(
		'AppUser',  # cada usuario tiene solo una sesión
		on_delete=models.CASCADE
	)
	token = models.CharField(max_length=64, unique=True, default=secrets.token_hex(16))
	created_at = models.DateTimeField(auto_now_add=True)

class Ingredient(models.Model):

	name = models.CharField(max_length=100)

	family = models.CharField(max_length=50)

	def __str__(self):
		return self.name


class Flavour(models.Model):

	name = models.CharField(max_length=50)

	def __str__(self):
		return self.name


class Aroma(models.Model):

	name = models.CharField(max_length=50)

	def __str__(self):
		return self.name


class IngredientFlavourRelation(models.Model):

	ingredient = models.ForeignKey(
		Ingredient,
		on_delete=models.CASCADE
	)

	flavour = models.ForeignKey(
		Flavour,
		on_delete=models.CASCADE
	)

	intensity = models.IntegerField()

	class Meta:
		unique_together = ("ingredient", "flavour")

	def __str__(self):
		return f"{self.ingredient.name} - {self.flavour.name} ({self.intensity})"


class IngredientAromaRelation(models.Model):

	ingredient = models.ForeignKey(
		Ingredient,
		on_delete=models.CASCADE
	)

	aroma = models.ForeignKey(
		Aroma,
		on_delete=models.CASCADE
	)

	intensity = models.IntegerField()

	class Meta:
		unique_together = ("ingredient", "aroma")

	def __str__(self):
		return f"{self.ingredient.name} - {self.aroma.name} ({self.intensity})"