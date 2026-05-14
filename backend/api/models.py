import secrets

from django.contrib.auth.models import AbstractUser
from django.db import models

class AppUser(AbstractUser):
	email = models.EmailField(unique=True)

	# username + email + password ya vienen de AbstractUser

	def __str__(self):
		return self.username

def generate_token():
	return secrets.token_hex(16)

class UserSession(models.Model):
	user = models.OneToOneField('AppUser', on_delete=models.CASCADE)
	token = models.CharField(max_length=64, unique=True, default=generate_token)
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
		on_delete=models.SET_NULL,
		null=True,
		blank=True
	)
	intensity = models.IntegerField()

	class Meta:
		unique_together = ("ingredient", "flavour")

	def __str__(self):
		return f"{self.ingredient.name} - {self.flavour.name if self.flavour else 'N/A'} ({self.intensity})"

class IngredientAromaRelation(models.Model):
	ingredient = models.ForeignKey(
		Ingredient,
		on_delete=models.CASCADE
	)
	aroma = models.ForeignKey(
		Aroma,
		on_delete=models.SET_NULL,
		null=True,
		blank=True
	)
	intensity = models.IntegerField()

	class Meta:
		unique_together = ("ingredient", "aroma")

	def __str__(self):
		return f"{self.ingredient.name} - {self.aroma.name if self.aroma else 'N/A'} ({self.intensity})"