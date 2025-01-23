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
    {
        "number": "010",
        "name": "Caterpie",
        "types": ["Bug"],
        "description": "For protection, it releases a horrible stench from the antennae on its head to drive away enemies.",
        "size": 0.3,
        "weight": 2.9,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "011",
        "name": "Metapod",
        "types": ["Bug"],
        "description": "This Pokémon is vulnerable to attack while its shell is soft, exposing its weak and tender body.",
        "size": 0.7,
        "weight": 9.9,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "012",
        "name": "Butterfree",
        "types": ["Bug", "Flying"],
        "description": "In battle, it flaps its wings at high speed to release highly toxic dust into the air.",
        "size": 1.1,
        "weight": 32.0,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "013",
        "name": "Weedle",
        "types": ["Bug", "Poison"],
        "description": "Its poison stinger is very powerful. Its bright-colored body is intended to warn off its enemies.",
        "size": 0.3,
        "weight": 3.2,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "014",
        "name": "Kakuna",
        "types": ["Bug", "Poison"],
        "description": "While awaiting evolution, it hides from predators under leaves and in nooks of branches.",
        "size": 0.6,
        "weight": 10.0,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "015",
        "name": "Beedrill",
        "types": ["Bug", "Poison"],
        "description": "It has three poisonous stingers on its forelegs and its tail. They are used to jab its enemy repeatedly.",
        "size": 1.0,
        "weight": 29.5,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "016",
        "name": "Pidgey",
        "types": ["Normal", "Flying"],
        "description": "A common sight in forests and woods. It flaps its wings at ground level to kick up blinding sand.",
        "size": 0.3,
        "weight": 1.8,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "017",
        "name": "Pidgeotto",
        "types": ["Normal", "Flying"],
        "description": "Very protective of its sprawling territorial area, this Pokémon will fiercely peck at any intruder.",
        "size": 1.1,
        "weight": 30.0,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "018",
        "name": "Pidgeot",
        "types": ["Normal", "Flying"],
        "description": "This Pokémon flies at Mach 2 speed, seeking prey. Its large talons are feared as wicked weapons.",
        "size": 1.5,
        "weight": 39.5,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "019",
        "name": "Rattata",
        "types": ["Normal"],
        "description": "Bites anything when it attacks. Small and very quick, it is a common sight in many places.",
        "size": 0.3,
        "weight": 3.5,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "020",
        "name": "Raticate",
        "types": ["Normal"],
        "description": "It uses its whiskers to maintain its balance. It apparently slows down if they are cut off.",
        "size": 0.7,
        "weight": 18.5,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "021",
        "name": "Spearow",
        "types": ["Normal", "Flying"],
        "description": "Eats bugs in grassy areas. It has to flap its short wings at high speed to stay airborne.",
        "size": 0.3,
        "weight": 2.0,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "022",
        "name": "Fearow",
        "types": ["Normal", "Flying"],
        "description": "With its huge and magnificent wings, it can keep aloft without ever having to land for rest.",
        "size": 1.2,
        "weight": 38.0,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "023",
        "name": "Ekans",
        "types": ["Poison"],
        "description": "It sneaks through grass without making a sound and strikes unsuspecting prey from behind.",
        "size": 2.0,
        "weight": 6.9,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "024",
        "name": "Arbok",
        "types": ["Poison"],
        "description": "The frightening patterns on its belly have been studied. Six variations have been confirmed.",
        "size": 3.5,
        "weight": 65.0,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "025",
        "name": "Pikachu",
        "types": ["Electric"],
        "description": "When several of these Pokémon gather, their electricity could build and cause lightning storms.",
        "size": 0.4,
        "weight": 6.0,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "026",
        "name": "Raichu",
        "types": ["Electric"],
        "description": "Its long tail serves as a ground to protect itself from its own high-voltage power.",
        "size": 0.8,
        "weight": 30.0,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "83",
        "name": "Farfetch'd",
        "types": ["Normal", "Flying"],
        "description": "The sprig of green onions it holds is its weapon. It is used much like a metal sword.",
        "size": 0.8,
        "weight": 15.0,
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
