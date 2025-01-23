# Ajout d'un pokémon

``` bash
curl -X POST http://localhost:7000/pokemon \
-H "Content-Type: application/json" \
-d '{
    "number": "001",
    "name": "Bulbasaur",
    "types": ["Grass", "Poison"],
    "description": "A strange seed was planted on its back at birth.",
    "size": 0.7,
    "weight": 6.9,
    "genderOptions": ["Male", "Female"],
    "shinyLock": false,
    "regions": ["Kanto"]
}'

```

# Ajout de plusieurs pokémon

```bash
curl -X POST http://localhost:7000/pokemon/batch \
-H "Content-Type: application/json" \
-d '[
    {
        "number": "001",
        "name": "Bulbasaur",
        "types": ["Grass", "Poison"],
        "description": "A strange seed was planted on its back at birth.",
        "size": 0.7,
        "weight": 6.9,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "002",
        "name": "Ivysaur",
        "types": ["Grass", "Poison"],
        "description": "When the bulb on its back grows large, it appears to lose the ability to stand on its hind legs.",
        "size": 1.0,
        "weight": 13.0,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "003",
        "name": "Venusaur",
        "types": ["Grass", "Poison"],
        "description": "Its plant blooms when it is absorbing solar energy. It stays on the move to seek sunlight.",
        "size": 2.0,
        "weight": 100.0,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "004",
        "name": "Charmander",
        "types": ["Fire"],
        "description": "It has a preference for hot things. When it rains, steam is said to spout from the tip of its tail.",
        "size": 0.6,
        "weight": 8.5,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "005",
        "name": "Charmeleon",
        "types": ["Fire"],
        "description": "When it swings its burning tail, it elevates the temperature to unbearably high levels.",
        "size": 1.1,
        "weight": 19.0,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "006",
        "name": "Charizard",
        "types": ["Fire", "Flying"],
        "description": "It spits fire that is hot enough to melt boulders. It may cause forest fires by blowing flames.",
        "size": 1.7,
        "weight": 90.5,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "007",
        "name": "Squirtle",
        "types": ["Water"],
        "description": "When it retracts its long neck into its shell, it squirts out water with vigorous force.",
        "size": 0.5,
        "weight": 9.0,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "008",
        "name": "Wartortle",
        "types": ["Water"],
        "description": "It is recognized as a symbol of longevity. If its shell has algae on it, that Wartortle is very old.",
        "size": 1.0,
        "weight": 22.5,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "009",
        "name": "Blastoise",
        "types": ["Water"],
        "description": "It crushes its foe under its heavy body to cause fainting. In a pinch, it will withdraw inside its shell.",
        "size": 1.6,
        "weight": 85.5,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    }
]'

```


# Ajout d'un dresseur

``` bash
curl -X POST https://dai.servecounterstrike.com/trainer \ 
-H "Content-Type: application/json" \
-d '{
    "name": "Red"
}'

```


# Ajout de pokémon à un dresseur

``` bash
curl -X POST https://dai.servecounterstrike.com/trainer/Red/add-pokemons \ 
-H "Content-Type: application/json" \
-d '[
    {"number": "003"},
    {"number": "006"},
    {"number": "009"}
]'



```


# Renommer un Pokémon
``` bash
curl -X PATCH http://localhost:7000/pokemon/025 \
-H "Content-Type: application/json" \
-d '{
  "name": "Piqachou"
}'

```

# Renommer un dresseur
``` bash
curl -X PATCH http://localhost:7000/Trainer/Red \
-H "Content-Type: application/json" \
-d '{
  "name": "SouljaBoy"
}'

```
