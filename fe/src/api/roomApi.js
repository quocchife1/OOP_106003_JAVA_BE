import axiosClient from './axiosClient';

const roomApi = {
  // GET /api/rooms
  getAllRooms: (params) => {
    return axiosClient.get('/api/rooms', { params });
  },
  // GET /api/rooms/{id}
  getById: (id) => {
    return axiosClient.get(`/api/rooms/${id}`);
  },

  // GET /api/rooms/code/{roomCode}
  getByCode: (roomCode) => {
    return axiosClient.get(`/api/rooms/code/${roomCode}`);
  },

  // GET /api/rooms/branch/{branchCode}
  getByBranch: (branchCode) => {
    return axiosClient.get(`/api/rooms/branch/${branchCode}`);
  },

  // GET /api/rooms/status/{status}
  getByStatus: (status) => {
    return axiosClient.get(`/api/rooms/status/${status}`);
  },

  // GET all rooms by status AVAILABLE for homepage
  getAvailableRooms: () => {
    return axiosClient.get('/api/rooms/status/AVAILABLE');
  },

  // Admin: update room status manually
  updateRoomStatus: (roomId, status) => {
    // PUT /api/rooms/{id}/status
    return axiosClient.put(`/api/rooms/${roomId}/status`, { status });
  },

  // Upload ảnh: POST /api/rooms/{roomId}/images
  uploadImages: (roomId, formData) => {
    return axiosClient.post(`/api/rooms/${roomId}/images`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }
};

export default roomApi;