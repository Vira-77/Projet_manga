const { getIo } = require('./socket');

// Notification: nouveau chapitre ajouté à un manga
function notifyNewChapter(mangaId, chapter) {
  try {
    const io = getIo();
    const room = `manga:${mangaId}`;

    io.to(room).emit('chapter:new', {
      mangaId,
      chapter,
    });
  } catch (err) {
    console.error('Erreur émission notif nouveau chapitre:', err.message);
  }
}

// Notification: mise à jour de chapitre
function notifyChapterUpdated(mangaId, chapter) {
  try {
    const io = getIo();
    const room = `manga:${mangaId}`;

    io.to(room).emit('chapter:updated', {
      mangaId,
      chapter,
    });
  } catch (err) {
    console.error('Erreur émission notif maj chapitre:', err.message);
  }
}

// Notification: mise à jour de statut d'un manga (ex: approuvé, publié)
function notifyMangaStatus(mangaId, statusPayload) {
  try {
    const io = getIo();
    const room = `manga:${mangaId}`;

    io.to(room).emit('manga:status', {
      mangaId,
      ...statusPayload,
    });
  } catch (err) {
    console.error('Erreur émission notif statut manga:', err.message);
  }
}

// Notification: réponse IA prête
function notifyAIResponse(userId, messageId, response) {
  try {
    const io = getIo();
    const room = `user:${userId}`;

    io.to(room).emit('ai:response', {
      messageId,
      response,
      timestamp: new Date().toISOString(),
    });
    
    console.log(`Notification IA envoyée à l'utilisateur ${userId} pour le message ${messageId}`);
  } catch (err) {
    console.error('Erreur émission notif réponse IA:', err.message);
  }
}

module.exports = {
  notifyNewChapter,
  notifyChapterUpdated,
  notifyMangaStatus,
  notifyAIResponse,
};