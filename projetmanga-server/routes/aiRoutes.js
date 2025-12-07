const express = require("express");
const router = express.Router();
const { chat } = require("../controllers/aiController");
const { verifyToken } = require("../middlewares/authMiddleware");

// Le chat nécessite une authentification
router.post("/chat", verifyToken, chat);

module.exports = router;
