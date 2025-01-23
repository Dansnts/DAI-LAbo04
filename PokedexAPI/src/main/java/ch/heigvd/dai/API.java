package ch.heigvd.dai;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

import java.util.*;


/**
 * @author: Dani Tiago Faria dos Santos and Nicolas duprat
 * @brief Classe principale de l'API pour gérer les Pokémon et les dresseurs.
 * <p>
 * Cette classe utilise Javalin pour créer une API RESTful permettant de gérer
 * un Pokédex (liste de Pokémon) et une liste de dresseurs.
 */
public class API {
    private final long TTL = 300000; // Durée de vie du cache en millisecondes (5 minutes)
    Javalin app; // Instance de Javalin pour gérer les routes HTTP
    int port; // Port sur lequel l'API écoute
    Map<String, Pokemon> pokedex = new HashMap<>(); // Pokédex (numéro -> Pokémon)
    Map<String, Trainer> trainers = new HashMap<>(); // Liste des dresseurs (nom -> Dresseur)
    Cache<String, Pokemon> pokemonCache = new Cache<>(); // Cache pour les Pokémon
    Cache<String, Trainer> trainerCache = new Cache<>(); // Cache pour les dresseurs
    Cache<String, String> htmlCache = new Cache<>(); // Cache pour les pages HTML
    Cache<String, Collection> collectionsCache = new Cache<>(); // Cache pour les collections

    /**
     * @param port Le port sur lequel l'API doit écouter.
     * @brief Constructeur de l'API.
     */
    API(int port) {
        this.app = Javalin.create();
        this.port = port;
    }

    /**
     * @brief Démarre l'API et configure les routes.
     */
    void start() {
        app.start(port);

        // POST
        /**
         * @brief Ajoute un Pokémon au Pokédex.
         *
         * Route : POST /pokemon
         * Body : Un objet Pokémon au format JSON.
         *
         * Si un Pokémon avec le même numéro existe déjà, renvoie une erreur 409 (CONFLICT).
         * Sinon, ajoute le Pokémon au Pokédex et renvoie une réponse 201 (CREATED).
         */
        app.post("/pokemon", ctx -> {
            Pokemon newPokemon = ctx.bodyAsClass(Pokemon.class);
            if (pokedex.containsKey(newPokemon.getNumber())) {
                ctx.status(HttpStatus.CONFLICT).result("A Pokémon with this Pokédex number already exists.");
                return;
            }
            pokedex.put(newPokemon.getNumber(), newPokemon);
            ctx.status(HttpStatus.CREATED).json(newPokemon);
        });

        /**
         * @brief Ajoute un dresseur à la liste des dresseurs.
         *
         * Route : POST /trainer
         * Body : Un objet Trainer au format JSON.
         *
         * Si un dresseur avec le même nom existe déjà, renvoie une erreur 409 (CONFLICT).
         * Sinon, ajoute le dresseur à la liste et renvoie une réponse 201 (CREATED).
         */
        app.post("/trainer", ctx -> {
            Trainer trainer = ctx.bodyAsClass(Trainer.class);

            // Vérifie si le nom du dresseur est déjà pris
            if (trainers.containsKey(trainer.getName())) {
                ctx.status(HttpStatus.CONFLICT).result("A Trainer with this name already exists.");
                return;
            }

            // Remplit les Pokémon du dresseur avec les détails du Pokédex
            ArrayList<Pokemon> trainerPokemons = new ArrayList<>();
            for (Pokemon trainerPokemon : trainer.getPokemons()) {
                Pokemon pokedexPokemon = pokedex.get(trainerPokemon.getNumber());
                if (pokedexPokemon != null && pokedexPokemon.getName() != null) {
                    trainerPokemons.add(pokedexPokemon);
                } else {
                    ctx.status(HttpStatus.BAD_REQUEST).result("Pokémon with number " + trainerPokemon.getNumber() + " not found in Pokédex.");
                    return;
                }
            }

            trainer.getPokemons().clear();
            trainer.addPokemons(trainerPokemons); // Assigne les Pokémon complets au dresseur
            trainers.put(trainer.getName(), trainer);
            ctx.status(HttpStatus.CREATED).json(trainer);
        });

        /**
         * @brief Ajoute des Pokémon à l'équipe d'un dresseur spécifique.
         *
         * Route : POST /trainer/{name}/add-pokemons
         * Body : Une liste de Pokémon au format JSON.
         *
         * Si le dresseur n'existe pas, renvoie une erreur 404 (NOT FOUND).
         * Si un Pokémon n'existe pas dans le Pokédex, renvoie une erreur 400 (BAD REQUEST).
         * Sinon, ajoute les Pokémon à l'équipe du dresseur et renvoie une réponse 200 (OK).
         */
        app.post("/trainer/{name}/add-pokemons", ctx -> {
            String trainerName = ctx.pathParam("name");
            Trainer trainer = trainers.get(trainerName);

            if (trainer == null) {
                ctx.status(HttpStatus.NOT_FOUND).result("Trainer not found.");
                return;
            }

            // Récupère les Pokémon à ajouter à partir du corps de la requête
            List<Pokemon> pokemonsToAdd = ctx.bodyAsClass(ArrayList.class);
            ArrayList<Pokemon> validatedPokemons = new ArrayList<>();

            for (Object obj : pokemonsToAdd) {
                Map<String, Object> pokemonMap = (Map<String, Object>) obj;
                String number = (String) pokemonMap.get("number");

                // Vérifie si le Pokémon existe dans le Pokédex
                Pokemon pokedexPokemon = pokedex.get(number);
                if (pokedexPokemon != null) {
                    validatedPokemons.add(pokedexPokemon);
                } else {
                    ctx.status(HttpStatus.BAD_REQUEST).result("Pokémon with number " + number + " not found in Pokédex.");
                    return;
                }
            }

            // Ajoute les Pokémon validés à l'équipe du dresseur
            trainer.addPokemons(validatedPokemons);
            ctx.status(HttpStatus.OK).json(trainer);
        });

        /**
         * @brief Ajoute un lot de Pokémon au Pokédex.
         *
         * Route : POST /pokemon/batch
         * Body : Une liste de Pokémon au format JSON.
         *
         * Ignore les Pokémon avec des numéros déjà existants dans le Pokédex.
         * Renvoie une réponse 201 (CREATED) avec la liste des Pokémon ajoutés.
         */
        app.post("/pokemon/batch", ctx -> {
            List<Pokemon> newPokemons = ctx.bodyAsClass(ArrayList.class);
            List<Pokemon> addedPokemons = new ArrayList<>();

            for (Object obj : newPokemons) {
                Map<String, Object> map = (Map<String, Object>) obj;

                String number = (String) map.get("number");
                if (pokedex.containsKey(number)) {
                    continue; // Ignore les Pokémon avec des numéros déjà existants
                }

                Pokemon pokemon = new Pokemon();
                pokemon.setNumber(number);
                pokemon.setName((String) map.get("name"));
                pokemon.setTypes((List<String>) map.get("types"));
                pokemon.setDescription((String) map.get("description"));
                pokemon.setSize((Double) map.get("size"));
                pokemon.setWeight((Double) map.get("weight"));
                pokemon.setGenderOptions((List<String>) map.get("genderOptions"));
                pokemon.setShinyLock((Boolean) map.get("shinyLock"));
                pokemon.setRegions((List<String>) map.get("regions"));

                pokedex.put(number, pokemon);
                addedPokemons.add(pokemon);
            }

            ctx.status(HttpStatus.CREATED).json(addedPokemons);
        });

        // GET
        /**
         * @brief Renvoie un message de bienvenue.
         *
         * Route : GET /
         *
         * Renvoie une réponse 200 (OK) avec un message de bienvenue.
         */
        app.get("/", ctx -> {
            String welcomeMessage = "Bienvenue DAI:2024-2025!";
            ctx.status(HttpStatus.OK).json(Map.of("message", welcomeMessage));
        });

        /**
         * @brief Récupère un Pokémon spécifique par son numéro.
         *
         * Route : GET /pokemon/{number}
         *
         * Si le Pokémon est trouvé, renvoie une réponse 200 (OK) avec le Pokémon.
         * Sinon, renvoie une erreur 404 (NOT FOUND).
         */
        app.get("/pokemon/{number}", ctx -> {
            String number = ctx.pathParam("number");
            Pokemon pokemon = pokemonCache.get(number);

            if (pokemon == null) {
                pokemon = pokedex.get(number);
                if (pokemon != null) {
                    pokemonCache.set(number, pokemon, TTL);
                }
            }

            if (pokemon != null) {
                ctx.status(HttpStatus.OK).json(pokemon);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Pokémon not found");
            }
        });


        /**
         * @brief Récupère un dresseur spécifique par son nom.
         *
         * Route : GET /trainer/{name}
         *
         * Si le dresseur est trouvé dans le cache, il est renvoyé directement.
         * Sinon, le dresseur est recherché dans la liste des dresseurs et mis en cache.
         * Si le dresseur n'est pas trouvé, renvoie une erreur 404 (NOT FOUND).
         */
        app.get("/trainer/{name}", ctx -> {
            String name = ctx.pathParam("name");
            Trainer trainer;
            Trainer cacheEntry = trainerCache.get(name);

            if (cacheEntry != null) {
                trainer = cacheEntry;
            } else {
                trainer = trainers.get(name);

                if (trainer != null) {
                    trainerCache.set(name, trainer, TTL);
                }
            }

            if (trainer != null) {
                ctx.status(HttpStatus.OK).json(trainer);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Trainer not found");
            }
        });

/**
 * @brief Récupère les Pokémon d'un dresseur spécifique.
 *
 * Route : GET /trainer/{name}/pokemons
 *
 * Si le dresseur est trouvé dans le cache, ses Pokémon sont renvoyés directement.
 * Sinon, le dresseur est recherché dans la liste des dresseurs et mis en cache.
 * Si le dresseur n'est pas trouvé, renvoie une erreur 404 (NOT FOUND).
 */
        app.get("/trainer/{name}/pokemons", ctx -> {
            String name = ctx.pathParam("name");
            Trainer trainer;
            Trainer cacheEntry = trainerCache.get(name);

            if (cacheEntry != null) {
                trainer = cacheEntry;
            } else {
                trainer = trainers.get(name);

                if (trainer != null) {
                    trainerCache.set(name, trainer, TTL);
                }
            }

            if (trainer != null) {
                ctx.status(HttpStatus.OK).json(trainer);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Trainer not found");
            }
        });

/**
 * @brief Récupère tous les dresseurs enregistrés.
 *
 * Route : GET /trainer
 *
 * Si la liste des dresseurs est trouvée dans le cache, elle est renvoyée directement.
 * Sinon, la liste est récupérée et mise en cache.
 * Renvoie une réponse 200 (OK) avec la liste des dresseurs.
 */
        app.get("/trainer", ctx -> {
            Collection collections;
            Collection cacheEntries = collectionsCache.get("trainer");

            if (cacheEntries != null) {
                collections = cacheEntries;
            } else {
                collections = trainers.values();
                collectionsCache.set("trainer", collections, TTL);
            }

            ctx.status(HttpStatus.OK).json(collections);
        });

/**
 * @brief Récupère tous les Pokémon enregistrés dans le Pokédex.
 *
 * Route : GET /pokemon
 *
 * Si la liste des Pokémon est trouvée dans le cache, elle est renvoyée directement.
 * Sinon, la liste est récupérée et mise en cache.
 * Renvoie une réponse 200 (OK) avec la liste des Pokémon.
 */
        app.get("/pokemon", ctx -> {
            Collection collections;
            Collection cacheEntries = collectionsCache.get("pokemon");

            if (cacheEntries != null) {
                collections = cacheEntries;
            } else {
                collections = pokedex.values();
                collectionsCache.set("pokemon", collections, TTL);
            }
            ctx.status(HttpStatus.OK).json(collections);
        });

/**
 * @brief Génère une page HTML pour afficher le Pokédex.
 *
 * Route : GET /pokemon-html
 *
 * Si le Pokédex est vide, renvoie un message indiquant qu'aucun Pokémon n'a été trouvé.
 * Si la page HTML est déjà en cache, elle est renvoyée directement.
 * Sinon, la page HTML est générée dynamiquement et mise en cache.
 * Renvoie une réponse 200 (OK) avec la page HTML.
 */
        app.get("/pokemon-html", ctx -> {
            if (pokedex.isEmpty()) {
                ctx.status(HttpStatus.OK).result("No Pokémon found in the Pokédex.");
                return;
            }

            if (htmlCache.get("pokemon-html") != null) {
                ctx.html(htmlCache.get("pokemon-html"));
                return;
            }

            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<!DOCTYPE html>");
            htmlBuilder.append("<html lang='en'>");
            htmlBuilder.append("<head>");
            htmlBuilder.append("<meta charset='UTF-8'>");
            htmlBuilder.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            htmlBuilder.append("<title>Pokédex</title>");
            htmlBuilder.append("<style>");
            htmlBuilder.append("table { width: 100%; border-collapse: collapse; }");
            htmlBuilder.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            htmlBuilder.append("th { background-color: #f4f4f4; }");
            htmlBuilder.append("img { max-width: 150px; max-height: 150px; }");
            htmlBuilder.append("</style>");
            htmlBuilder.append("</head>");
            htmlBuilder.append("<body>");
            htmlBuilder.append("<h1>Pokédex</h1>");
            htmlBuilder.append("<table>");
            htmlBuilder.append("<thead>");
            htmlBuilder.append("<tr>");
            htmlBuilder.append("<th>Number</th>");
            htmlBuilder.append("<th>Name</th>");
            htmlBuilder.append("<th>Image</th>");
            htmlBuilder.append("<th>Types</th>");
            htmlBuilder.append("<th>Description</th>");
            htmlBuilder.append("<th>Size</th>");
            htmlBuilder.append("<th>Weight</th>");
            htmlBuilder.append("<th>Gender Options</th>");
            htmlBuilder.append("<th>Shiny Lock</th>");
            htmlBuilder.append("<th>Regions</th>");
            htmlBuilder.append("</tr>");
            htmlBuilder.append("</thead>");
            htmlBuilder.append("<tbody>");

            for (Pokemon pokemon : pokedex.values()) {
                htmlBuilder.append("<tr>");
                htmlBuilder.append("<td>").append(pokemon.getNumber()).append("</td>");
                htmlBuilder.append("<td>").append(pokemon.getName()).append("</td>");
                htmlBuilder.append("<td><img src='https://img.pokemondb.net/artwork/large/").append(pokemon.getName().toLowerCase()).append(".jpg' alt='Image of ").append(pokemon.getName()).append("'></td>");
                htmlBuilder.append("<td>").append(String.join(", ", pokemon.getTypes())).append("</td>");
                htmlBuilder.append("<td>").append(pokemon.getDescription()).append("</td>");
                htmlBuilder.append("<td>").append(pokemon.getSize()).append(" m</td>");
                htmlBuilder.append("<td>").append(pokemon.getWeight()).append(" kg</td>");
                htmlBuilder.append("<td>").append(String.join(", ", pokemon.getGenderOptions())).append("</td>");
                htmlBuilder.append("<td>").append(pokemon.isShinyLock() ? "Yes" : "No").append("</td>");
                htmlBuilder.append("<td>").append(String.join(", ", pokemon.getRegions())).append("</td>");
                htmlBuilder.append("</tr>");
            }

            htmlBuilder.append("</tbody>");
            htmlBuilder.append("</table>");
            htmlBuilder.append("</body>");
            htmlBuilder.append("</html>");

            ctx.html(htmlBuilder.toString());
            htmlCache.set("pokemon-html", htmlBuilder.toString(), TTL);
        });

/**
 * @brief Génère une page HTML pour afficher la liste des dresseurs.
 *
 * Route : GET /trainer-html
 *
 * Si la liste des dresseurs est vide, renvoie un message indiquant qu'aucun dresseur n'a été trouvé.
 * Si la page HTML est déjà en cache, elle est renvoyée directement.
 * Sinon, la page HTML est générée dynamiquement et mise en cache.
 * Renvoie une réponse 200 (OK) avec la page HTML.
 */
        app.get("/trainer-html", ctx -> {
            if (trainers.isEmpty()) {
                ctx.status(HttpStatus.OK).result("No Trainers found.");
                return;
            }

            if (htmlCache.get("trainer-html") != null) {
                ctx.html(htmlCache.get("trainer-html"));
                return;
            }

            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<!DOCTYPE html>");
            htmlBuilder.append("<html lang='en'>");
            htmlBuilder.append("<head>");
            htmlBuilder.append("<meta charset='UTF-8'>");
            htmlBuilder.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            htmlBuilder.append("<title>Trainers</title>");
            htmlBuilder.append("<style>");
            htmlBuilder.append("table { width: 100%; border-collapse: collapse; }");
            htmlBuilder.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            htmlBuilder.append("th { background-color: #f4f4f4; }");
            htmlBuilder.append("</style>");
            htmlBuilder.append("</head>");
            htmlBuilder.append("<body>");
            htmlBuilder.append("<h1>Trainers</h1>");
            htmlBuilder.append("<table>");
            htmlBuilder.append("<thead>");
            htmlBuilder.append("<tr>");
            htmlBuilder.append("<th>Name</th>");
            htmlBuilder.append("<th>Pokémon</th>");
            htmlBuilder.append("</tr>");
            htmlBuilder.append("</thead>");
            htmlBuilder.append("<tbody>");

            for (Trainer trainer : trainers.values()) {
                htmlBuilder.append("<tr>");
                htmlBuilder.append("<td>").append(trainer.getName()).append("</td>");
                htmlBuilder.append("<td>");

                if (trainer.getPokemons() != null && !trainer.getPokemons().isEmpty()) {
                    htmlBuilder.append("<ul>");
                    for (Pokemon pokemon : trainer.getPokemons()) {
                        htmlBuilder.append("<li>");
                        htmlBuilder.append(pokemon.getName())
                                .append(" (#")
                                .append(pokemon.getNumber())
                                .append(")");
                        htmlBuilder.append("</li>");
                    }
                    htmlBuilder.append("</ul>");
                } else {
                    htmlBuilder.append("No Pokémon");
                }

                htmlBuilder.append("</td>");
                htmlBuilder.append("</tr>");
            }

            htmlBuilder.append("</tbody>");
            htmlBuilder.append("</table>");
            htmlBuilder.append("</body>");
            htmlBuilder.append("</html>");

            ctx.html(htmlBuilder.toString());
            htmlCache.set("trainer-html", htmlBuilder.toString(), TTL);
        });

/**
 * @brief Génère une page HTML pour afficher l'équipe d'un dresseur spécifique.
 *
 * Route : GET /trainer/{name}/team-html
 *
 * Si le dresseur n'est pas trouvé, renvoie une erreur 404 (NOT FOUND).
 * Si la page HTML est déjà en cache, elle est renvoyée directement.
 * Sinon, la page HTML est générée dynamiquement et mise en cache.
 * Renvoie une réponse 200 (OK) avec la page HTML.
 */
        app.get("/trainer/{name}/team-html", ctx -> {
            String name = ctx.pathParam("name");
            Trainer trainer = trainers.get(name);

            if (trainer == null) {
                ctx.status(HttpStatus.NOT_FOUND).result("Trainer not found.");
                return;
            }

            if (htmlCache.get("team-html") != null) {
                ctx.status(HttpStatus.OK).json(htmlCache.get("team-html"));
                return;
            }

            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<!DOCTYPE html>");
            htmlBuilder.append("<html lang='en'>");
            htmlBuilder.append("<head>");
            htmlBuilder.append("<meta charset='UTF-8'>");
            htmlBuilder.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            htmlBuilder.append("<title>Trainer Team</title>");
            htmlBuilder.append("<style>");
            htmlBuilder.append("table { width: 100%; border-collapse: collapse; }");
            htmlBuilder.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            htmlBuilder.append("th { background-color: #f4f4f4; }");
            htmlBuilder.append("</style>");
            htmlBuilder.append("</head>");
            htmlBuilder.append("<body>");
            htmlBuilder.append("<h1>Team of ").append(trainer.getName()).append("</h1>");

            if (trainer.getPokemons() == null || trainer.getPokemons().isEmpty()) {
                htmlBuilder.append("<p>No Pokémon found in ").append(trainer.getName()).append("'s team.</p>");
            } else {
                htmlBuilder.append("<table>");
                htmlBuilder.append("<thead>");
                htmlBuilder.append("<tr>");
                htmlBuilder.append("<th>Number</th>");
                htmlBuilder.append("<th>Name</th>");
                htmlBuilder.append("<th>Types</th>");
                htmlBuilder.append("<th>Description</th>");
                htmlBuilder.append("<th>Size</th>");
                htmlBuilder.append("<th>Weight</th>");
                htmlBuilder.append("<th>Gender Options</th>");
                htmlBuilder.append("<th>Shiny Lock</th>");
                htmlBuilder.append("<th>Regions</th>");
                htmlBuilder.append("</tr>");
                htmlBuilder.append("</thead>");
                htmlBuilder.append("<tbody>");

                for (Pokemon pokemon : trainer.getPokemons()) {
                    htmlBuilder.append("<tr>");
                    htmlBuilder.append("<td>").append(pokemon.getNumber()).append("</td>");
                    htmlBuilder.append("<td>").append(pokemon.getName()).append("</td>");
                    htmlBuilder.append("<td>").append(String.join(", ", pokemon.getTypes())).append("</td>");
                    htmlBuilder.append("<td>").append(pokemon.getDescription()).append("</td>");
                    htmlBuilder.append("<td>").append(pokemon.getSize()).append(" m</td>");
                    htmlBuilder.append("<td>").append(pokemon.getWeight()).append(" kg</td>");
                    htmlBuilder.append("<td>").append(String.join(", ", pokemon.getGenderOptions())).append("</td>");
                    htmlBuilder.append("<td>").append(pokemon.isShinyLock() ? "Yes" : "No").append("</td>");
                    htmlBuilder.append("<td>").append(String.join(", ", pokemon.getRegions())).append("</td>");
                    htmlBuilder.append("</tr>");
                }

                htmlBuilder.append("</tbody>");
                htmlBuilder.append("</table>");
            }

            htmlBuilder.append("</body>");
            htmlBuilder.append("</html>");

            ctx.html(htmlBuilder.toString());
            htmlCache.set("team-html", htmlBuilder.toString(), TTL);
        });


        /**
         * @brief Met à jour un Pokémon spécifique dans le Pokédex.
         *
         * Route : PATCH /pokemon/{number}
         *
         * Si le Pokémon existe, ses attributs sont mis à jour avec les données fournies dans le corps de la requête.
         * Si le numéro du Pokémon est modifié, vérifie qu'aucun autre Pokémon n'utilise déjà ce numéro.
         * Renvoie une réponse 200 (OK) avec le Pokémon mis à jour.
         * Si le Pokémon n'est pas trouvé, renvoie une erreur 404 (NOT FOUND).
         * Si un conflit survient (numéro déjà utilisé), renvoie une erreur 409 (CONFLICT).
         */
        app.patch("/pokemon/{number}", ctx -> {
            String number = ctx.pathParam("number");
            Pokemon existingPokemon = pokedex.get(number);
            if (existingPokemon != null) {
                Pokemon updatedData = ctx.bodyAsClass(Pokemon.class);
                if (updatedData.getNumber() != null && !updatedData.getNumber().equals(number)) {
                    String newNumber = updatedData.getNumber();
                    if (pokedex.get(newNumber) == null) {
                        existingPokemon.setNumber(newNumber);
                        pokedex.put(newNumber, existingPokemon);
                        pokedex.remove(number);
                    } else {
                        ctx.status(HttpStatus.CONFLICT).result("Pokemon already exists.");
                    }
                }
                if (updatedData.getName() != null) existingPokemon.setName(updatedData.getName());
                if (updatedData.getTypes() != null) existingPokemon.setTypes(updatedData.getTypes());
                if (updatedData.getDescription() != null) existingPokemon.setDescription(updatedData.getDescription());
                if (updatedData.getSize() != 0) existingPokemon.setSize(updatedData.getSize());
                if (updatedData.getWeight() != 0) existingPokemon.setWeight(updatedData.getWeight());
                if (updatedData.getGenderOptions() != null)
                    existingPokemon.setGenderOptions(updatedData.getGenderOptions());
                existingPokemon.setShinyLock(updatedData.isShinyLock());
                if (updatedData.getRegions() != null) existingPokemon.setRegions(updatedData.getRegions());
                ctx.status(HttpStatus.OK).json(existingPokemon);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Pokémon not found");
            }
        });

/**
 * @brief Met à jour le nom d'un dresseur spécifique.
 *
 * Route : PATCH /trainer/{name}
 *
 * Si le dresseur existe, son nom est mis à jour avec les données fournies dans le corps de la requête.
 * Si le nouveau nom est déjà utilisé par un autre dresseur, renvoie une erreur 409 (CONFLICT).
 * Renvoie une réponse 200 (OK) avec un message de succès.
 * Si le dresseur n'est pas trouvé, renvoie une erreur 404 (NOT FOUND).
 */
        app.patch("/trainer/{name}", ctx -> {
            String trainerName = ctx.pathParam("name");
            Trainer existingTrainer = trainers.get(trainerName);

            if (existingTrainer != null) {
                Trainer updatedData = ctx.bodyAsClass(Trainer.class);

                if (updatedData.getName() != null && !updatedData.getName().equals(trainerName)) {
                    String newName = updatedData.getName();
                    if (trainers.get(newName) == null) {
                        existingTrainer.setName(newName);
                        trainers.put(newName, existingTrainer);
                        trainers.remove(trainerName);
                    } else {
                        ctx.status(HttpStatus.CONFLICT).result("Trainer with this name already exists.");
                        return;
                    }
                }

                ctx.status(HttpStatus.OK).result("Trainer updated successfully.");
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Trainer not found.");
            }
        });

/**
 * @brief Met à jour l'équipe d'un dresseur spécifique.
 *
 * Route : PATCH /trainer/{name}/pokemons
 *
 * Si le dresseur existe, son équipe est remplacée par les Pokémon fournis dans le corps de la requête.
 * Chaque Pokémon doit exister dans le Pokédex.
 * Renvoie une réponse 200 (OK) avec le dresseur mis à jour.
 * Si le dresseur n'est pas trouvé, renvoie une erreur 404 (NOT FOUND).
 * Si un Pokémon n'existe pas dans le Pokédex, renvoie une erreur 400 (BAD REQUEST).
 */
        app.patch("/trainer/{name}/pokemons", ctx -> {
            String trainerName = ctx.pathParam("name");
            Trainer existingTrainer = trainers.get(trainerName);

            if (existingTrainer != null) {
                List<Pokemon> pokemonsToAdd = ctx.bodyAsClass(ArrayList.class);
                ArrayList<Pokemon> validatedPokemons = new ArrayList<>();
                existingTrainer.getPokemons().clear();

                for (Object obj : pokemonsToAdd) {
                    Map<String, Object> pokemonMap = (Map<String, Object>) obj;
                    String number = (String) pokemonMap.get("number");

                    // Vérifie si le Pokémon existe dans le Pokédex
                    Pokemon pokedexPokemon = pokedex.get(number);
                    if (pokedexPokemon != null) {
                        validatedPokemons.add(pokedexPokemon);
                    } else {
                        ctx.status(HttpStatus.BAD_REQUEST).result("Pokémon with number " + number + " not found in Pokédex.");
                        return;
                    }
                }

                existingTrainer.addPokemons(validatedPokemons);
                ctx.status(HttpStatus.OK).json(existingTrainer);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Trainer not found");
            }
        });

/**
 * @brief Supprime un Pokémon spécifique du Pokédex.
 *
 * Route : DELETE /pokemon/{number}
 *
 * Si le Pokémon existe, il est supprimé du Pokédex.
 * Renvoie une réponse 204 (NO CONTENT) en cas de succès.
 * Si le Pokémon n'est pas trouvé, renvoie une erreur 404 (NOT FOUND).
 */
        app.delete("/pokemon/{number}", ctx -> {
            String number = ctx.pathParam("number");
            if (pokedex.remove(number) != null) {
                ctx.status(HttpStatus.NO_CONTENT);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Pokémon not found");
            }
        });

/**
 * @brief Supprime un dresseur spécifique.
 *
 * Route : DELETE /trainer/{name}
 *
 * Si le dresseur existe, il est supprimé de la liste des dresseurs.
 * Renvoie une réponse 204 (NO CONTENT) en cas de succès.
 * Si le dresseur n'est pas trouvé, renvoie une erreur 404 (NOT FOUND).
 */
        app.delete("/trainer/{name}", ctx -> {
            String name = ctx.pathParam("name");
            if (trainers.remove(name) != null) {
                ctx.status(HttpStatus.NO_CONTENT);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Trainer not found");
            }
        });
    }
}