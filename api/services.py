# api/services.py

from itertools import combinations, product
from api.models import Ingredient, IngredientFlavourRelation, IngredientAromaRelation
from api.compatibility import FLAVOUR_RULES, AROMA_RULES, FAMILY_RULES, FLAVOUR_AROMA_RULES

def get_ingredient_variants(ingredient):
    variants = {}
    for f in IngredientFlavourRelation.objects.filter(ingredient=ingredient):
        method = f.cooking_method or "default"
        if method not in variants:
            variants[method] = []
        variants[method].append({
            "flavor": f.flavor.name,
            "intensity": f.intensity
        })
    return variants

def score_pair(ing1, ing2):
    score = 1.0
    for f1 in ing1.flavours.all():
        for f2 in ing2.flavours.all():
            key = tuple(sorted((f1.name, f2.name)))
            score *= FLAVOUR_RULES.get(key, 1.0)
    for a1 in ing1.aromas.all():
        for a2 in ing2.aromas.all():
            key = tuple(sorted((a1.name, a2.name)))
            score *= AROMA_RULES.get(key, 1.0)
    key_family = tuple(sorted((ing1.family, ing2.family)))
    score *= FAMILY_RULES.get(key_family, 1.0)
    for f in ing1.flavours.all():
        for a in ing2.aromas.all():
            score *= FLAVOUR_AROMA_RULES.get((f.name, a.name), 1.0)
    for f in ing2.flavours.all():
        for a in ing1.aromas.all():
            score *= FLAVOUR_AROMA_RULES.get((f.name, a.name), 1.0)
    return score

def score_combo(ingredients):
    total_score = 1.0
    for i, ing1 in enumerate(ingredients):
        for ing2 in ingredients[i+1:]:
            total_score *= score_pair(ing1, ing2)
    return total_score

def generate_combos(base_ingredient, candidates, combo_size):
    combos = []
    for combo in combinations(candidates, combo_size):
        if all(score_pair(i1, i2) > 0 for i1, i2 in combinations(combo, 2)):
            full_combo = [base_ingredient] + list(combo)
            combos.append(full_combo)
    return combos

def get_top_combos(base_ingredient, candidates, combo_size, top_n=20):
    if combo_size == 1:
        scored = [(c, score_pair(base_ingredient, c)) for c in candidates]
        scored.sort(key=lambda x: x[1], reverse=True)
        return [[base_ingredient, s[0]] for s in scored[:top_n]]
    candidate_combos = generate_combos(base_ingredient, candidates, combo_size)
    scored_combos = [(combo, score_combo(combo)) for combo in candidate_combos]
    scored_combos.sort(key=lambda x: x[1], reverse=True)
    return [c[0] for c in scored_combos[:top_n]]

def get_candidates(base_ingredient, top_n_candidates=15, family_filter=None):
    all_ingredients = Ingredient.objects.exclude(id=base_ingredient.id)
    if family_filter:
        all_ingredients = all_ingredients.filter(family__in=family_filter)
    scored = [(ing, score_pair(base_ingredient, ing)) for ing in all_ingredients]
    scored.sort(key=lambda x: x[1], reverse=True)
    return [s[0] for s in scored[:top_n_candidates]]