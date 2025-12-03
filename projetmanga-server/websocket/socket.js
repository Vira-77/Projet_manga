const { Server } = require('socket.io');

let ioInstance = null;

// Initialise Socket.io avec gestion de rooms
function initSocket(server) {
  if (ioInstance) {
    return ioInstance;
  }

  ioInstance = new Server(server, {
    cors: {
      origin: process.env.WS_ORIGIN || '*',
      methods: ['GET', 'POST'],
      credentials: true,
    },
    transports: ['websocket', 'polling'],
  });

  ioInstance.on('connection', (socket) => {
    console.log('Client Socket.io connecté:', socket.id);

    // Le client peut rejoindre une ou plusieurs rooms
    socket.on('join', (rooms = []) => {
      if (!Array.isArray(rooms)) rooms = [rooms];
      rooms.forEach((room) => {
        const roomName = String(room);
        socket.join(roomName);
        console.log(`Client ${socket.id} a rejoint la room: ${roomName}`);
      });
    });

    // Quitter une ou plusieurs rooms
    socket.on('leave', (rooms = []) => {
      if (!Array.isArray(rooms)) rooms = [rooms];
      rooms.forEach((room) => {
        const roomName = String(room);
        socket.leave(roomName);
        console.log(`Client ${socket.id} a quitté la room: ${roomName}`);
      });
    });

    socket.on('disconnect', (reason) => {
      console.log('Client Socket.io déconnecté:', socket.id, 'Raison:', reason);
    });

    socket.on('error', (error) => {
      console.error('Erreur Socket.io:', error);
    });
  });

  return ioInstance;
}

function getIo() {
  if (!ioInstance) {
    throw new Error("Socket.io n'est pas initialisé. Appelez initSocket(server) d'abord.");
  }
  return ioInstance;
}

module.exports = {
  initSocket,
  getIo,
};