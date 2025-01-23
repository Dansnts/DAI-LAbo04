package ch.heigvd.dai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dani Tiago Faria dos Santos et Nicolas Duprat
 * @brief Classe représentant un dresseur avec une équipe de Pokémon.
 *
 * Cette classe contient les informations d'un dresseur, telles que son nom et son équipe de Pokémon.
 * Elle permet d'ajouter, de supprimer et d'afficher les Pokémon associés au dresseur.
 */
public class Trainer {
    private String name; // Nom du dresseur
    private Set<Pokemon> pokemons; // Équipe de Pokémon du dresseur

    /**
     * @brief Constructeur par défaut.
     *
     * Initialise un dresseur avec une équipe de Pokémon vide.
     * Ce constructeur est nécessaire pour la désérialisation JSON avec Jackson.
     */
    public Trainer() {
        this.pokemons = new HashSet<>();
    }

    /**
     * @brief Constructeur avec un nom.
     *
     * Initialise un dresseur avec un nom spécifié et une équipe de Pokémon vide.
     *
     * @param name Le nom du dresseur.
     */
    public Trainer(String name) {
        this.name = name;
        this.pokemons = new HashSet<>();
    }

    /**
     * @brief Retourne le nom du dresseur.
     *
     * @return Le nom du dresseur.
     */
    public String getName() {
        return name;
    }

    /**
     * @brief Définit le nom du dresseur.
     *
     * @param name Le nom du dresseur.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @brief Retourne l'équipe de Pokémon du dresseur.
     *
     * @return Un ensemble de Pokémon associés au dresseur.
     */
    public Set<Pokemon> getPokemons() {
        return pokemons;
    }

    /**
     * @brief Ajoute des Pokémon à l'équipe du dresseur.
     *
     * Cette méthode ajoute une liste de Pokémon à l'équipe du dresseur, en évitant les doublons.
     *
     * @param pokes La liste des Pokémon à ajouter.
     */
    public void addPokemons(ArrayList<Pokemon> pokes) {
        for (Pokemon pokemon : pokes) {
            if (pokemon != null && !this.pokemons.contains(pokemon)) {
                this.pokemons.add(pokemon);
            }
        }
    }

    /**
     * @brief Affiche les Pokémon de l'équipe du dresseur.
     *
     * Si l'équipe est vide, un message indiquant qu'aucun Pokémon n'a été trouvé est affiché.
     */
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

    /**
     * @brief Retire un Pokémon de l'équipe du dresseur.
     *
     * @param pokemon Le Pokémon à retirer.
     */
    public void removePokemon(Pokemon pokemon) {
        pokemons.remove(pokemon);
    }
}