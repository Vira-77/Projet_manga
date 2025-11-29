//on utilise le modele openai pour que la discussion soit plus fluide
// et pour profiter de sa connaissance des oeuvre hors db
const OpenAI = require("openai");
const openai = new OpenAI({ apiKey: process.env.OPENAI_KEY });