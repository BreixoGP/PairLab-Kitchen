# api/services.py

from itertools import combinations
from api.models import Ingredient, IngredientFlavourRelation, IngredientAromaRelation
from api.compatibility import FLAVOUR_RULES, AROMA_RULES, FAMILY_RULES, FLAVOUR_AROMA_RULES

# 🔹 Obtener el perfil de un ingrediente: sabores y aromas + intensidades
def get_ingredient_profile(ingredient):
    """
    Devuelve un diccionario con la lista de sabores y aromas del ingrediente.
    Cada elemento incluye 'name' y 'intensity'.
    Esto se puede cachear en la app para evitar múltiples queries.
    """
    profile = {
        "flavours": [],
        "aromas": [],
        "family": ingredient.family
    }

    # sabores
    for rel in IngredientFlavourRelation.objects.filter(ingredient=ingredient):
        profile["flavours"].append({
            "name": rel.flavour.name,
            "intensity": rel.intensity
        })

    # aromas
    for rel in IngredientAromaRelation.objects.filter(ingredient=ingredient):
        profile["aromas"].append({
            "name": rel.aroma.name,
            "intensity": rel.intensity
        })

    return profile

# 🔹 Calcular compatibilidad entre dos perfiles de ingredientes
def score_pair(profile1, profile2):
    """
    Calcula el score entre dos ingredientes usando:
    - FLAVOUR_RULES
    - AROMA_RULES
    - FAMILY_RULES
    - FLAVOUR_AROMA_RULES
    Multiplica la regla por la intensidad de cada sabor/aroma.
    """
    score = 1.0

    # sabor ↔ sabor
    for f1 in profile1["flavours"]:
        for f2 in profile2["flavours"]:
            key = tuple(sorted((f1["name"], f2["name"])))
            rule = FLAVOUR_RULES.get(key, 1.0)
            intensity_factor = 1 + (f1["intensity"] * f2["intensity"]) / 100
            score *= rule * intensity_factor

    # aroma ↔ aroma
    for a1 in profile1["aromas"]:
        for a2 in profile2["aromas"]:
            key = tuple(sorted((a1["name"], a2["name"])))
            rule = AROMA_RULES.get(key, 1.0)
            intensity_factor = 1 + (a1["intensity"] * a2["intensity"]) / 100
            score *= rule * intensity_factor

    # familia ↔ familia
    key_family = tuple(sorted((profile1["family"], profile2["family"])))
    score *= FAMILY_RULES.get(key_family, 1.0)

    # sabor ↔ aroma
    for f in profile1["flavours"]:
        for a in profile2["aromas"]:
            rule = FLAVOUR_AROMA_RULES.get((f["name"], a["name"]), 1.0)
            intensity_factor = 1 + (f["intensity"] * a["intensity"]) / 100
            score *= rule * intensity_factor

    for f in profile2["flavours"]:
        for a in profile1["aromas"]:
            rule = FLAVOUR_AROMA_RULES.get((f["name"], a["name"]), 1.0)
            intensity_factor = 1 + (f["intensity"] * a["intensity"]) / 100
            score *= rule * intensity_factor

    return score

# 🔹 Score de un combo de varios ingredientes
def score_combo(profiles):
    """
    profiles: lista de perfiles de ingredientes
    Calcula el score total del combo multiplicando todos los pares.
    """
    total_score = 1.0
    for i, p1 in enumerate(profiles):
        for p2 in profiles[i+1:]:
            total_score *= score_pair(p1, p2)
    return total_score

# 🔹 Generar combos válidos a partir de la lista de candidatos
def generate_combos(base_profile, candidate_profiles, combo_size):
    """
    Combina candidatos de tamaño combo_size y añade el ingrediente base.
    Solo se incluyen combos donde todos los pares tienen score > 0
    """
    combos = []
    for combo in combinations(candidate_profiles, combo_size):
        if all(score_pair(c1, c2) > 0 for c1, c2 in combinations(combo, 2)):
            full_combo = [base_profile] + list(combo)
            combos.append(full_combo)
    return combos

# 🔹 Obtener los top N combos ordenados por score
def get_top_combos(base_profile, candidate_profiles, combo_size, top_n=20):
    """
    Devuelve los combos mejor puntuados.
    Para combo_size=1, combina solo base + 1 candidato.
    """
    if combo_size == 1:
        scored = [(c, score_pair(base_profile, c)) for c in candidate_profiles]
        scored.sort(key=lambda x: x[1], reverse=True)
        return [[base_profile, s[0]] for s in scored[:top_n]]

    candidate_combos = generate_combos(base_profile, candidate_profiles, combo_size)
    scored_combos = [(combo, score_combo(combo)) for combo in candidate_combos]
    scored_combos.sort(key=lambda x: x[1], reverse=True)
    return [c[0] for c in scored_combos[:top_n]]

# 🔹 Obtener candidatos para un ingrediente base
def get_candidates(base_ingredient, top_n_candidates=15, family_filter=None):
    """
    Devuelve los ingredientes candidatos ordenados por compatibilidad con el base.
    """
    all_ingredients = Ingredient.objects.exclude(id=base_ingredient.id)
    if family_filter:
        all_ingredients = all_ingredients.filter(family__in=family_filter)

    # pre-cacheamos perfiles para no hacer queries repetidas
    base_profile = get_ingredient_profile(base_ingredient)
    candidate_profiles = [(ing, get_ingredient_profile(ing)) for ing in all_ingredients]

    scored = []
    for ing, profile in candidate_profiles:
        pair_score = score_pair(base_profile, profile)
        scored.append((ing, profile, pair_score))

    # ordenamos por score
    scored.sort(key=lambda x: x[2], reverse=True)

    # devolvemos solo los perfiles
    return [p[1] for p in scored[:top_n_candidates]]