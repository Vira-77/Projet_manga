const { chatWithAI } = require("../services/aiService");

async function chat(req, res) {
    try {
        const userMessage = req.body.message;

        if (!userMessage) {
            return res.status(400).json({ error: "Message manquant." });
        }

        const response = await chatWithAI(userMessage);

        return res.json({ reply: response });

    } catch (error) {
        console.error("Erreur IA :", error);
        res.status(500).json({ error: "Erreur interne." });
    }
}

module.exports = { chat };
