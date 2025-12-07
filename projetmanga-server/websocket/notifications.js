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

module.exports = {
  notifyNewChapter,
  notifyChapterUpdated,
  notifyMangaStatus,
};