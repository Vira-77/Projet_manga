const { chatWithAI } = require("../services/aiService");
const { notifyAIResponse } = require("../websocket/notifications");

async function chat(req, res) {
    try {
        const userMessage = req.body.message;
        const userId = req.user?.id || req.body.userId || "default";
        const messageId = req.body.messageId || Date.now().toString();

        if (!userMessage) {
            return res.status(400).json({ error: "Message manquant." });
        }

        // Répondre immédiatement avec le messageId
        res.json({ 
            reply: "", 
            messageId: messageId,
            status: "processing" 
        });

        // Traiter la réponse en arrière-plan
        try {
            const response = await chatWithAI(userMessage, userId);
            
            // Envoyer la notification WebSocket
            notifyAIResponse(userId, messageId, response);
        } catch (error) {
            console.error("Erreur lors du traitement IA :", error);
            notifyAIResponse(userId, messageId, "Je n'arrive pas à répondre pour le moment. Réessaie !");
        }

    } catch (error) {
        console.error("Erreur IA :", error);
        if (!res.headersSent) {
            res.status(500).json({ error: "Erreur interne." });
        }
    }
}

module.exports = { chat };
