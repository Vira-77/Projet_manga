const axios = require("axios");
const { loadCatalogue } = require("./catalogueService");

let history = [];

async function chatWithAI(userMessage) {
    const catalogue = await loadCatalogue();

    // Sécurisation des genres même si ce sont des ObjectId non populés
    const formattedCatalogue = catalogue
        .map(m => {
            const genreNames = Array.isArray(m.genres)
                ? m.genres.map(g => typeof g === "string" ? g : g?.name || "Inconnu")
                : [];

            return `- ${m.title} (${genreNames.join(", ")})`;
        })
        .join("\n");

    const rules = `
Tu es une IA experte en manga.
❌ Interdit d'inventer un manga.
❌ Interdit de proposer un manga hors catalogue.
✔ Réponds de manière naturelle et amicale.
✔ Maximum 200 caractères.

Catalogue :
${formattedCatalogue}
`;

    const prompt = `
${rules}

Historique :
${history.map(h => `${h.role}: ${h.content}`).join("\n")}

Utilisateur : ${userMessage}
IA :
`;

    try {
        const response = await axios.post(
            `${process.env.OLLAMA_URL}/api/generate`,
            { model: "mistral", prompt, stream: false }
        );

        const aiText = response.data.response.trim();

        history.push({ role: "user", content: userMessage });
        history.push({ role: "assistant", content: aiText });

        return aiText;

    } catch (err) {
        console.error("Erreur IA :", err);
        return "Je n'arrive pas à répondre pour le moment. Réessaie !";
    }
}

module.exports = { chatWithAI };
