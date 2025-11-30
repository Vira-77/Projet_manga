const axios = require("axios");
const { loadCatalogue } = require("./catalogueService");

let history = [];

async function chatWithAI(userMessage) {
    const catalogue = await loadCatalogue();

    const rules = `
Tu es une IA experte en manga.
Il est INTERDIT proposer une œuvre non listée dans le catalogue ou d'inventer une oeuvre.
Si rien n’est pertinent, ou si le catalogue est vide dis-le clairement.
Réponds de manière naturelle et friendly.
la reponse ne doit pas depasser 200 caracteres.
voici le
Catalogue (obligatoire) :
${catalogue.map(m => `- ${m.title} (${m.genres.join(", ")})`).join("\n")}
`;

    const prompt = `
${rules}

Historique :
${history.map(h => `${h.role}: ${h.content}`).join("\n")}

Utilisateur : ${userMessage}
 IA :
`;

    const response = await axios.post(
        `${process.env.OLLAMA_URL}/api/generate`,
        { model: "mistral", prompt, stream: false }
    );

    const aiText = response.data.response.trim();

    history.push({ role: "user", content: userMessage });
    history.push({ role: "assistant", content: aiText });

    return aiText;
}

module.exports = { chatWithAI };
