Food Pairing App - Under development

Project Type: Final Project – Multiplatform Application Design Course

This project is a Food Pairing Application designed as a final project for my studies in Multiplatform Application Design. The main goal is to provide a platform where users can explore ingredient pairings based on flavors, aromas, and ingredient families.

The application consists of two main components:

REST API (Django)

Serves all data related to users, ingredients, flavors, aromas, and their pairings.

Handles user registration, authentication, and session management using token-based authentication.

Computes ingredient pairing scores based on flavor, aroma, and family compatibility rules, taking intensity into account.

Provides endpoints for:

User registration, login, logout, and profile management

Retrieving ingredient lists and families

Calculating top pairings for a selected base ingredient

Android Application

Consumes the Django REST API to provide a user-friendly interface.

Users can search and filter ingredients by name or family.

Displays suggested ingredient pairings based on compatibility scores.

Saves session tokens locally (SharedPreferences) to avoid repeated logins.

Features

User registration and login with unique email and username.

Token-based session management to allow login from different devices while invalidating old sessions.

Ingredient browsing with optional filters by name and family.

Pairing suggestions using flavor, aroma, family rules, and intensity-based scoring.

Responsive Android UI with dropdowns and search functionality for ingredients.

Tech Stack

Backend: Django REST Framework, Python

Frontend: Android (Kotlin or Java, depending on implementation)

Database: SQLite / PostgreSQL (depending on deployment)

Usage

Clone the repository:

git clone https://github.com/BreixoGP/FoodPairingApp.git

Set up the Django backend and run migrations.

Launch the Android app and connect to the backend API.

Register a user, explore ingredients, and generate pairing suggestions.
