const socketService = require('../services/socketService');

// Obtenir les rooms Socket.io à rejoindre
exports.getSocketRooms = async (req, res) => {
    try {
        const userId = req.user.id;
        const userRole = req.user.role;
        
        const rooms = await socketService.getSocketRooms(userId, userRole);
        
        res.status(200).json({
            rooms,
            role: userRole
        });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

