Plaintext
# PairLab Kitchen
Food Pairing App is a mobile application developed as a final project for my studies in Multiplatform Application Design.

The main idea behind the project is to help users discover ingredient combinations based on flavour compatibility, aromas and ingredient families. The system generates pairing suggestions using a custom scoring algorithm built on top of predefined compatibility rules, and then leverages Artificial Intelligence to turn those combinations into fully structured culinary recipes.

## Project Overview
The project is divided into two main parts:

### Backend — Django REST API
The backend is responsible for:

- Managing users and authentication
- Handling ingredient, flavour and aroma data
- Generating pairing suggestions
- Calculating compatibility scores between ingredients
- Integrating with Google Gemini AI to generate custom recipes based on selected ingredients

The API includes endpoints for:

- User registration and login
- Session management with token authentication
- Ingredient and family retrieval
- Pairing generation based on filters and combo size
- AI-powered recipe generation from selected ingredient pairings

The compatibility engine evaluates flavours, aromas, ingredient families and intensity values to generate the final pairing score.

### Android Application
The Android app consumes the REST API and provides the user interface for the project.

Main features include:

- Ingredient search
- Ingredient family filters
- Pairing generation
- AI Recipe view for generated combinations
- Token persistence using SharedPreferences
- Responsive UI with dropdowns and popup filters

## Features
- User registration and authentication
- Token-based session system
- Ingredient filtering by family
- Pairing suggestions based on compatibility rules
- Intensity-based scoring system
- Smart recipe generation powered by Google Gemini AI
- Cloud-hosted production backend connected to the Android mobile interface

## Tech Stack

### Backend
- Python
- Django REST Framework
- Google GenAI (Gemini API)
- PythonAnywhere (Cloud Hosting)

### Frontend
- Android (Java)
- Retrofit 2 & OkHttp3

### Database
- SQLite

## Production Architecture & APK Download
The backend of this project is currently deployed and running live in production on PythonAnywhere. 

If you want to test the application directly on a physical Android device or emulator without setting up the environment, you can download the ready-to-install app package from the Releases section of this repository.

## Running the Project Locally
If you prefer to run the project locally on your machine, please switch to the local version branch:

```bash
git checkout local-version-branch
(Replace local-version-branch with the exact name of your local branch).
```
Inside that branch, follow these steps:

Start the backend server by opening a terminal in the folder where manage.py is located and running:

```bash
python manage.py runserver
```
Once the local backend is running, open the Android project inside Android Studio (Frontend folder).

Start an Android Emulator from Device Manager and launch the application using the Run App option.

The local branch project is already configured to redirect the network traffic to your local environment.

Testing the Application
Register a new user

Login into the app

Select a base ingredient

Choose combo size

Apply optional family filters

Generate pairing suggestions based on the algorithm

Select a pairing combo and request the AI to generate a custom recipe!

## Proyect status
The project is completed and fully operational in production.
