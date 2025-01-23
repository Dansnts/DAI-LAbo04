package ch.heigvd.dai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Pokemon {
    private String number; // Identifiant unique (numéro de Pokédex)
    private String name;
    private List<String> types;
    private String description;
    private double size;
    private double weight;
    private List<String> genderOptions; // Liste des options de genre
    private boolean shinyLock; // Indique si le Pokémon est verrouillé en shiny
    private List<String> regions;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public List<String> getGenderOptions() {
        return genderOptions;
    }

    public void setGenderOptions(List<String> genderOptions) {
        this.genderOptions = genderOptions;
    }

    public boolean isShinyLock() {
        return shinyLock;
    }

    public void setShinyLock(boolean shinyLock) {
        this.shinyLock = shinyLock;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }


}
