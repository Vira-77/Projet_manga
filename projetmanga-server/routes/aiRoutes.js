const express = require("express");
const router = express.Router();
const axios = require("axios");

// Route de test IA
router.get("/test", async (req, res) => {
    try {
        const response = await axios.post(process.env.OLLAMA_URL + "/api/generate", {
            model: "mistral",
            prompt: "Bonjour! Peux-tu me répondre ?"
        });

        res.json(response.data);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
