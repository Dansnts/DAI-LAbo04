// URL de l'API
const API_URL = "http://localhost:8080/pokemon";

// Fonction pour récupérer les données de l'API
async function fetchPokemon() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        const data = await response.json();
        populateTable(data);
    } catch (error) {
        console.error("Error fetching Pokémon data:", error);
    }
}

// Fonction pour remplir le tableau
function populateTable(pokemonList) {
    const tableBody = document.getElementById("pokemon-table-body");

    // Vider le tableau avant d'ajouter des données
    tableBody.innerHTML = "";

    // Ajouter chaque Pokémon dans une ligne
    pokemonList.forEach((pokemon) => {
        const row = document.createElement("tr");

        const idCell = document.createElement("td");
        idCell.textContent = pokemon.id;
        row.appendChild(idCell);

        const nameCell = document.createElement("td");
        nameCell.textContent = pokemon.name;
        row.appendChild(nameCell);

        const typeCell = document.createElement("td");
        typeCell.textContent = pokemon.type;
        row.appendChild(typeCell);

        tableBody.appendChild(row);
    });
}

// Charger les données au chargement de la page
document.addEventListener("DOMContentLoaded", fetchPokemon);
