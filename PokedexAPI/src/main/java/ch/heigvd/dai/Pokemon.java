package ch.heigvd.dai;

import java.util.*;

/**
 * @author Dani Tiago Faria dos Santos et Nicolas Duprat
 * @brief Classe représentant un Pokémon stocké dans le Pokédex.
 *
 * Cette classe contient les informations détaillées d'un Pokémon, telles que son numéro, son nom,
 * ses types, sa description, sa taille, son poids, ses options de genre, son statut de verrouillage
 * shiny et les régions où il peut être trouvé.
 */
public class Pokemon {
    private String number; // Identifiant unique (numéro de Pokédex)
    private String name; // Nom du Pokémon
    private List<String> types; // Types du Pokémon (ex: Feu, Eau, etc.)
    private String description; // Description du Pokémon
    private double size; // Taille du Pokémon en mètres
    private double weight; // Poids du Pokémon en kilogrammes
    private List<String> genderOptions; // Liste des options de genre (ex: Mâle, Femelle, Sans genre)
    private boolean shinyLock; // Indique si le Pokémon est verrouillé en shiny (impossible d'obtenir sa version shiny)
    private List<String> regions; // Régions où le Pokémon peut être trouvé

    /**
     * @brief Retourne le numéro du Pokémon.
     *
     * @return Le numéro du Pokémon.
     */
    public String getNumber() {
        return number;
    }

    /**
     * @brief Définit le numéro du Pokémon.
     *
     * @param number Le numéro du Pokémon.
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * @brief Retourne le nom du Pokémon.
     *
     * @return Le nom du Pokémon.
     */
    public String getName() {
        return name;
    }

    /**
     * @brief Définit le nom du Pokémon.
     *
     * @param name Le nom du Pokémon.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @brief Retourne la liste des types du Pokémon.
     *
     * @return La liste des types du Pokémon.
     */
    public List<String> getTypes() {
        return types;
    }

    /**
     * @brief Définit la liste des types du Pokémon.
     *
     * @param types La liste des types du Pokémon.
     */
    public void setTypes(List<String> types) {
        this.types = types;
    }

    /**
     * @brief Retourne la description du Pokémon.
     *
     * @return La description du Pokémon.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @brief Définit la description du Pokémon.
     *
     * @param description La description du Pokémon.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @brief Retourne la taille du Pokémon.
     *
     * @return La taille du Pokémon en mètres.
     */
    public double getSize() {
        return size;
    }

    /**
     * @brief Définit la taille du Pokémon.
     *
     * @param size La taille du Pokémon en mètres.
     */
    public void setSize(double size) {
        this.size = size;
    }

    /**
     * @brief Retourne le poids du Pokémon.
     *
     * @return Le poids du Pokémon en kilogrammes.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @brief Définit le poids du Pokémon.
     *
     * @param weight Le poids du Pokémon en kilogrammes.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * @brief Retourne la liste des options de genre du Pokémon.
     *
     * @return La liste des options de genre du Pokémon.
     */
    public List<String> getGenderOptions() {
        return genderOptions;
    }

    /**
     * @brief Définit la liste des options de genre du Pokémon.
     *
     * @param genderOptions La liste des options de genre du Pokémon.
     */
    public void setGenderOptions(List<String> genderOptions) {
        this.genderOptions = genderOptions;
    }

    /**
     * @brief Indique si le Pokémon est verrouillé en shiny.
     *
     * @return `true` si le Pokémon est verrouillé en shiny, `false` sinon.
     */
    public boolean isShinyLock() {
        return shinyLock;
    }

    /**
     * @brief Définit si le Pokémon est verrouillé en shiny.
     *
     * @param shinyLock `true` pour verrouiller le Pokémon en shiny, `false` sinon.
     */
    public void setShinyLock(boolean shinyLock) {
        this.shinyLock = shinyLock;
    }

    /**
     * @brief Retourne la liste des régions où le Pokémon peut être trouvé.
     *
     * @return La liste des régions où le Pokémon peut être trouvé.
     */
    public List<String> getRegions() {
        return regions;
    }

    /**
     * @brief Définit la liste des régions où le Pokémon peut être trouvé.
     *
     * @param regions La liste des régions où le Pokémon peut être trouvé.
     */
    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    /**
     * @brief Compare deux Pokémon pour vérifier s'ils sont égaux.
     *
     * Deux Pokémon sont considérés comme égaux s'ils ont le même numéro.
     *
     * @param o L'objet à comparer avec ce Pokémon.
     * @return `true` si les Pokémon sont égaux, `false` sinon.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pokemon pokemon = (Pokemon) o;
        return Objects.equals(number, pokemon.number);
    }

    /**
     * @brief Retourne le code de hachage du Pokémon.
     *
     * Le code de hachage est basé sur le numéro du Pokémon.
     *
     * @return Le code de hachage du Pokémon.
     */
    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}