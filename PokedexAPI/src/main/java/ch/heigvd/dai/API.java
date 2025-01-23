package ch.heigvd.dai;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class API {
    Javalin app;
    int port ;
    Map<String, Pokemon> pokedex = new HashMap<>();
    Map<String, Trainer> trainers = new HashMap<>();



    API(int port) {
        this.app = Javalin.create();
        this.port = port;
    }

    void start() {
        app.start(port);

        // POST
        app.post("/pokemon", ctx -> {
            Pokemon newPokemon = ctx.bodyAsClass(Pokemon.class);
            if (pokedex.containsKey(newPokemon.getNumber())) {
                ctx.status(HttpStatus.CONFLICT).result("A Pokémon with this Pokédex number already exists.");
                return;
            }
            pokedex.put(newPokemon.getNumber(), newPokemon);
            ctx.status(HttpStatus.CREATED).json(newPokemon);
        });

        app.post("/trainer", ctx -> {
            Trainer trainer = ctx.bodyAsClass(Trainer.class);

            // Vérifiez si le nom du Trainer est déjà pris
            if (trainers.containsKey(trainer.getName())) {
                ctx.status(HttpStatus.CONFLICT).result("A Trainer with this name already exists.");
                return;
            }

            // Remplissez les Pokémon du Trainer avec les détails du Pokédex
            ArrayList<Pokemon> trainerPokemons = new ArrayList<>();
            for (Pokemon trainerPokemon : trainer.getPokemons()) {
                Pokemon pokedexPokemon = pokedex.get(trainerPokemon.getNumber());
                if (pokedexPokemon != null) {
                    trainerPokemons.add(pokedexPokemon);
                } else {
                    ctx.status(HttpStatus.BAD_REQUEST).result("Pokémon with number " + trainerPokemon.getNumber() + " not found in Pokédex.");
                    return;
                }
            }

            trainer.addPokemons(trainerPokemons); // Assignez les Pokémon complets au Trainer
            trainers.put(trainer.getName(), trainer);
            ctx.status(HttpStatus.CREATED).json(trainer);
        });

        app.post("/trainer/{name}/add-pokemons", ctx -> {
            String trainerName = ctx.pathParam("name");
            Trainer trainer = trainers.get(trainerName);

            if (trainer == null) {
                ctx.status(HttpStatus.NOT_FOUND).result("Trainer not found.");
                return;
            }

            // Récupérer les Pokémon à ajouter à partir du corps de la requête
            List<Pokemon> pokemonsToAdd = ctx.bodyAsClass(ArrayList.class);
            ArrayList<Pokemon> validatedPokemons = new ArrayList<>();

            for (Object obj : pokemonsToAdd) {
                Map<String, Object> pokemonMap = (Map<String, Object>) obj;
                String number = (String) pokemonMap.get("number");

                // Vérifier si le Pokémon existe dans le Pokédex
                Pokemon pokedexPokemon = pokedex.get(number);
                if (pokedexPokemon != null) {
                    validatedPokemons.add(pokedexPokemon);
                } else {
                    ctx.status(HttpStatus.BAD_REQUEST).result("Pokémon with number " + number + " not found in Pokédex.");
                    return;
                }
            }

            // Ajouter les Pokémon validés à l'équipe du Trainer
            trainer.addPokemons(validatedPokemons);
            ctx.status(HttpStatus.OK).json(trainer);
        });



        app.post("/pokemon/batch", ctx -> {
            List<Pokemon> newPokemons = ctx.bodyAsClass(ArrayList.class);
            List<Pokemon> addedPokemons = new ArrayList<>();

            for (Object obj : newPokemons) {
                Map<String, Object> map = (Map<String, Object>) obj;

                String number = (String) map.get("number");
                if (pokedex.containsKey(number)) {
                    continue; // Skip Pokémon with duplicate numbers
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
        app.get("/pokemon/{number}", ctx -> {
            String number = ctx.pathParam("number");
            Pokemon pokemon = pokedex.get(number);
            if (pokemon != null) {
                ctx.status(HttpStatus.OK).json(pokemon);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Pokémon not found");
            }
        });

        app.get("/trainer/{name}", ctx -> {
            String name = ctx.pathParam("name");
            Trainer trainer = trainers.get(name);
            if (trainer != null) {
                ctx.status(HttpStatus.OK).json(trainer);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Trainer not found");
            }
        });

        app.get("/trainer/{name}/pokemons", ctx -> {
            String name = ctx.pathParam("name");
            Trainer trainer = trainers.get(name);
            if (trainer != null) {
                ctx.status(HttpStatus.OK).json(trainer);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Trainer not found");
            }
        });

        app.get("/trainer", ctx -> ctx.status(HttpStatus.OK).json(trainers.values()));
        app.get("/pokemon", ctx -> ctx.status(HttpStatus.OK).json(pokedex.values()));

        // Endpoint pour générer une page HTML avec un tableau
        app.get("/pokemon-html", ctx -> {
            if (pokedex.isEmpty()) {
                ctx.status(HttpStatus.OK).result("No Pokémon found in the Pokédex.");
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
        });

        app.get("/trainer-html", ctx -> {
            if (trainers.isEmpty()) {
                ctx.status(HttpStatus.OK).result("No Trainers found.");
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
        });


        app.get("/trainer/{name}/team-html", ctx -> {
            String name = ctx.pathParam("name");
            Trainer trainer = trainers.get(name);

            if (trainer == null) {
                ctx.status(HttpStatus.NOT_FOUND).result("Trainer not found.");
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
        });


        // PATCH
        app.patch("/pokemon/{number}", ctx -> {
            String number = ctx.pathParam("number");
            Pokemon existingPokemon = pokedex.get(number);
            if (existingPokemon != null) {
                Pokemon updatedData = ctx.bodyAsClass(Pokemon.class);
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

        // DELETE
        app.delete("/pokemon/{number}", ctx -> {
            String number = ctx.pathParam("number");
            if (pokedex.remove(number) != null) {
                ctx.status(HttpStatus.NO_CONTENT);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Pokémon not found");
            }
        });
    }

}
