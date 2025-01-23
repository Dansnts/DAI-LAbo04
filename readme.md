# Projet : Configuration de la Machine Virtuelle et Application Web

## Table des Matières

- [Projet : Configuration de la Machine Virtuelle et Application Web](#projet--configuration-de-la-machine-virtuelle-et-application-web)
  - [Table des Matières](#table-des-matières)
    - [Installation et Configuration de la Machine Virtuelle](#installation-et-configuration-de-la-machine-virtuelle)
    - [Configuration de la Zone DNS](#configuration-de-la-zone-dns)
      - [Exemple:](#exemple)
    - [Déploiement et Exécution avec Docker Compose](#déploiement-et-exécution-avec-docker-compose)
    - [Build Maven](#build-maven)
    - [Résilience des Structures de Données](#résilience-des-structures-de-données)
    - [API et Interaction avec l'Application Web](#api-et-interaction-avec-lapplication-web)
      - [Pokémon](#pokémon)
        - [Ajout d'un pokémon](#ajout-dun-pokémon)
        - [Ajout de plusieurs pokémon](#ajout-de-plusieurs-pokémon)
        - [Renommer un Pokémon](#renommer-un-pokémon)
      - [Dresseur](#dresseur)
        - [Ajout d'un dresseur](#ajout-dun-dresseur)
        - [Ajout de pokémon à un dresseur](#ajout-de-pokémon-à-un-dresseur)
        - [Renommer un dresseur](#renommer-un-dresseur)

---

### Installation et Configuration de la Machine Virtuelle

1. **Prérequis** :
   - Une Instance chez un fournisseur comme Azure ou AWS.
   - Une image Linux préconfigurée de votre machine virtuelle (Ubuntu 24.02, Debian 12 ou mieux encore Alpine Linux).

2. **Instructions** :
   - Créer la VM chez votre fourniseurs avec les spécs nécessaires avec votre image Linux. (Ici 1 Coeur et 2GB de RAM devraient suffir)
   ![alt text](/readmePictures/VM1.png)

   - Configurez les réseaux pour utiliser une interface NAT et une interface réseau privé. Si par exemple vous voulez utiliser un autre port autre que le 22 pour le SSH ou avoir un port différent sur Traefik tout en gardant tout sur le même nom de domaine.
   ![alt text](/readmePictures/FW.png)
   - Démarrez la machine virtuelle.
   - Se connecter dessus en SSH avec le bon username/mot de passe ou bien directement avec votre clé SSH
  
3. **Connexion** :
   - Utilisez la commande suivante pour accéder à votre VM :
    ```bash
    ssh -i votre_cle_ssh.pem user@adresse_ip_vm
    ```



 4. **Mise à niveau et installation de Docker & Docker compose**  
    Copier simplement ces commandes et executer les dans votre terminal.

    ```bash
    # Mettre à jour les paquets existants
    sudo apt update && sudo apt upgrade -y

    # Installer les dépendances nécessaires
    sudo apt install -y apt-transport-https ca-certificates curl software-properties-common

    # Ajouter la clé GPG officielle de Docker
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

    # Ajouter le dépôt Docker
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

    # Mettre à jour les paquets et installer Docker
    sudo apt update && sudo apt install -y docker-ce docker-ce-cli containerd.io

    # Vérifier que Docker est bien installé
    sudo docker --version

    # Ajouter l'utilisateur actuel au groupe Docker (optionnel)
    sudo usermod -aG docker $USER

    # Installer Docker Compose
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

    # Appliquer les permissions nécessaires
    sudo chmod +x /usr/local/bin/docker-compose
    ```

    Puis vérifier que tout fonctionne avec :

    ```bash
    # Vérifier que Docker Compose est bien installé
    docker-compose --version
    docker run hello-world
    ```

---

### Configuration de la Zone DNS

1. Ajoutez les enregistrements DNS suivants dans votre fournisseur DNS :
   - **Nom de domaine** : `www.votre-domaine.com`
   - **Type** : `A`
   - **Adresse IP** : Adresse IP publique de la VM.

2. Validez la configuration en utilisant cette commande :
```bash
dig www.votre-domaine.com
```
   Le retour doit inclure l'adresse IP configurée. 
   #### Exemple:
``` SH
dig www.dai.servecounterstrike.com 

; <<>> DiG 9.18.30 <<>> www.dai.servecounterstrike.com
;; global options: +cmd
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NXDOMAIN, id: 56345
;; flags: qr rd ra; QUERY: 1, ANSWER: 0, AUTHORITY: 1, ADDITIONAL: 1

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags:; udp: 65494
;; QUESTION SECTION:
;www.dai.servecounterstrike.com.	IN	A

;; AUTHORITY SECTION:
servecounterstrike.com.	60	IN	SOA	nf1.no-ip.com. hostmaster.no-ip.com. 2006809829 90 120 604800 60

;; Query time: 19 msec
;; SERVER: 127.0.0.53#53(127.0.0.53) (UDP)
;; WHEN: Thu Jan 23 19:16:47 CET 2025
;; MSG SIZE  rcvd: 116
```

---

### Déploiement et Exécution avec Docker Compose
Pour information ce projet utilise docker-compose, ainsi il n'est pas disponnible de faire un pull d'une simple image, mais si vous le souhaiter vous pouver build grâce au Docker file mis a dispossition dans `PokedexAPI/docker` et l'expoiter comme bon vous semble, mais la partie Traefik ne sera pas disponnible.

1. **Clonez le dépôt** :
   ```bash
   git clone https://github.com/Dansnts/DAI-Labo04
   cd PokedexAPI/docker
   ```

2. **Déplacer les fichiers sur votre VM** :
   ```bash
   scp ./* utilisateur@adresse-ip-distante:/chemin/vers/destination
   ```

3. **Construisez et déployez avec Docker Compose** :
   ```bash
   cd chemin/vers/destination
   docker-compose up --build
   ```

4. **Accéder à l'application** :
   - Accédez à l'application à l'adresse suivante (Selon les ports que vous avez décider si vous avez modifié le fichers Docker): `https://www.mon-domaine-trop-cool.com`

---



### Build Maven

1. **Build avec Maven** :
   - Assurez-vous d'avoir Maven installé.
   - Exécutez :
    ```bash
    mvn clean package
    ```

---


### Résilience des Structures de Données
  - Notre code utilise des `ConcurrentHashMap` en Java pour cela.

---


### API et Interaction avec l'Application Web

#### Pokémon

##### Ajout d'un pokémon

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

##### Ajout de plusieurs pokémon

```bash
curl -X POST http://localhost:7000/pokemon/batch \
-H "Content-Type: application/json" \
-d '[
    {
        "number": "1",
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
        "number": "2",
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
        "number": "3",
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
        "number": "4",
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
        "number": "5",
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
        "number": "6",
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
        "number": "7",
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
        "number": "8",
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
        "number": "9",
        "name": "Blastoise",
        "types": ["Water"],
        "description": "It crushes its foe under its heavy body to cause fainting. In a pinch, it will withdraw inside its shell.",
        "size": 1.6,
        "weight": 85.5,
        "genderOptions": ["Male", "Female"],
        "shinyLock": false,
        "regions": ["Kanto"]
    },
    {
        "number": "10",
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
        "number": "11",
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
        "number": "12",
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
        "number": "13",
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
        "number": "14",
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
        "number": "15",
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
        "number": "17",
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
        "number": "18",
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
        "number": "19",
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
        "number": "20",
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
        "number": "21",
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
        "number": "22",
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
        "number": "23",
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
        "number": "24",
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
        "number": "25",
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
        "number": "26",
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
        "name": "Farfetchd"
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

##### Renommer un Pokémon
``` bash
curl -X PATCH http://localhost:7000/pokemon/025 \
-H "Content-Type: application/json" \
-d '{
  "name": "Piqachou"
}'

```

#### Dresseur


##### Ajout d'un dresseur

``` bash
curl -X POST https://dai.servecounterstrike.com/trainer \ 
-H "Content-Type: application/json" \
-d '{
    "name": "Red"
}'

```

##### Ajout de pokémon à un dresseur

``` bash
curl -X POST https://dai.servecounterstrike.com/trainer/Red/add-pokemons \ 
-H "Content-Type: application/json" \
-d '[
    {"number": "003"},
    {"number": "006"},
    {"number": "009"}
]'

```


##### Renommer un dresseur
``` bash
curl -X PATCH http://localhost:7000/Trainer/Red \
-H "Content-Type: application/json" \
-d '{
  "name": "SouljaBoy"
}'

```
