package ch.heigvd.dai;

import java.util.ArrayList;

public class Trainer {
    private String name; // Champ privé pour respecter les bonnes pratiques
    private ArrayList<Pokemon> pokemons;

    // Constructeur par défaut (nécessaire pour Jackson)
    public Trainer() {
        this.pokemons = new ArrayList<>();
    }

    // Constructeur avec un argument (optionnel, mais utile)
    public Trainer(String name) {
        this.name = name;
        this.pokemons = new ArrayList<>();
    }

    // Getter pour `name` (nécessaire pour Jackson)
    public String getName() {
        return name;
    }

    // Setter pour `name` (nécessaire pour Jackson)
    public void setName(String name) {
        this.name = name;
    }

    // Getter pour `pokemons` (nécessaire si vous voulez retourner les Pokémon associés)
    public ArrayList<Pokemon> getPokemons() {
        return pokemons;
    }

    // Setter pour `pokemons` (nécessaire pour Jackson si vous voulez recevoir des Pokémon)
    public void addPokemons(ArrayList<Pokemon> pokemons) {
        for (Pokemon pokemon : pokemons) {
            if(!this.pokemons.contains(pokemon)) {
                this.pokemons.add(pokemon);
            }
        }
    }

    // Méthode pour afficher les Pokémon
    public void showPokemons() {
        if (!pokemons.isEmpty()) {
            System.out.println(name + " pokemons: ");
            int no = 1;
            for (Pokemon pokemon : pokemons) {
                System.out.println(no + ": " + pokemon.getName() + " #" + pokemon.getNumber());
                no++;
            }
        } else {
            System.out.println("No pokemons found for the trainer " + name);
        }
    }

    // Méthode pour retirer un Pokémon
    public void removePokemon(Pokemon pokemon) {
        pokemons.remove(pokemon);
    }
}
