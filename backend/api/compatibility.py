# Sabores disponibles
# "sweet", "salty", "umami", "sour", "bitter", "spicy", "fatty"

# Aromas / sensaciones disponibles
# "herbal", "citrus", "smoky", "anis", "floral", "nutty"

# Familias de ingredientes
# "red_meat", "poultry", "seafood", "vegetable", "fruit",  "dairy", "pork", "herbs", "spices"

# Compatibilidad de sabores (multiplicadores)
FLAVOUR_RULES = {
    ("sweet", "salty"): 1.5,
    ("sweet", "bitter"): 0.8,
    ("umami", "sour"): 1.5,
    ("umami", "umami"): 1.0,
    ("fatty", "spicy"): 1.2,
    ("bitter", "bitter"): 0.6,
    ("sweet", "umami"): 1.2,
    ("sour", "fatty"): 1.3,
    ("sweet", "sour"): 1.2,
}

# Compatibilidad de aromas / sensaciones
AROMA_RULES = {
    ("citrus", "citrus"): 1.5,
    ("herbal", "umami"): 1.3,
    ("smoky", "fatty"): 1.3,
    ("anis", "smoky"): 0.5,
    ("floral", "sweet"): 1.4,
    ("nutty", "fatty"): 1.3,
    ("herbal", "nutty"): 1.2,
}

# Compatibilidad de familias
FAMILY_RULES = {
    ("red_meat", "seafood"): 0.5,
    ("red_meat", "fish"): 0.5,
    ("red_meat", "vegetable"): 1.5,
    ("poultry", "seafood"): 1.2,
    ("dairy", "fish"): 0.8,
    ("fruit", "dairy"): 1.3,
    ("pork", "seafood"): 1.2,
    ("poultry", "fish"): 1.2,
    ("pork", "fish"): 1.2,
    ("poultry", "seafood"): 1.2,
    ("red_meat", "poultry"): 1.0,
    ("vegetable", "fruit"): 1.4,
    ("red_meat", "fruit"): 1.4,
    ("poultry", "fruit"): 1.4,
    ("pork", "fruit"): 1.4,

}

# Compatibilidad entre sabor y aroma
FLAVOUR_AROMA_RULES = {
    ("fatty", "nutty"): 1.3,
    ("umami", "herbal"): 1.3,
    ("sweet", "floral"): 1.4,
    ("sour", "citrus"): 1.5,
    ("bitter", "herbal"): 0.8,
}