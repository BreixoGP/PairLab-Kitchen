import json
import os
from google import genai
from google.genai import types

api_key = os.environ.get("GEMINI_API_KEY")
client = genai.Client(api_key=api_key)

def generate_ai_recipe(ingredients_list):
    """
    Se conecta con la API de Google Gemini 1.5 Flash.
    Recibe una lista de strings con ingredientes validados y
    devuelve un diccionario Python con la receta estructurada.
    """

    ingredients_str = ", ".join(ingredients_list)
    system_instruction = (
        "You are a Michelin-star chef expert in Food Pairing and traditional gastronomy. "
        "Your task is to create a realistic, logical, and coherent recipe based on the ingredients provided. "
        "Follow these strict culinary rules:\n"
        "1. Realism: The recipe must be feasible and make culinary sense. Do not invent bizarre combinations; "
        "if the ingredients are unusual together (like chicken and octopus), find a classic technique to bridge them "
        "(e.g., a surf and turf stew, rice dish, or distinct components that complement each other).\n"
        "2. Epic Title: Keep the recipe name inspiring and high-end, but accurate to the cooking style.\n"
        "3. Accuracy: Ensure the cooking times and steps are precise and safe for a home cook.\n\n"
        "It is MANDATORY that your response is exclusively a valid JSON object with this exact structure:\n"
        "{\n"
        "  \"title\": \"Epic yet realistic recipe name in English\",\n"
        "  \"extra_ingredients\": \"List of basic pantry ingredients needed or 'None'\",\n"
        "  \"steps\": \"Numbered and summarized preparation steps separated by periods.\"\n"
        "}"
    )

    prompt_usuario = f"Generate a coherent, high-end recipe using these ingredients: {ingredients_str}"

    response = client.models.generate_content(
        model='gemini-2.5-flash',
        contents=prompt_usuario,
        config=types.GenerateContentConfig(
            system_instruction=system_instruction,
            response_mime_type="application/json",
            temperature=0.3,
        ),
    )
    return json.loads(response.text)