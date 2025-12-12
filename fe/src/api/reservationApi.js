import axiosClient from './axiosClient';

const reservationApi = {
  createReservation: (data) => {
    return axiosClient.post('/api/reservations', data);
  },
  getAllReservations: (params) => {
    // Staff view: list by status or paged
    return axiosClient.get('/api/reservations/status/PENDING_CONFIRMATION', { params });
  },
  getMyReservations: (params) => {
    return axiosClient.get('/api/reservations/my-reservations', { params });
  },
  cancelReservation: (id) => {
    return axiosClient.delete(`/api/reservations/${id}`);
  },
  updateStatus: (id, status) => {
    if (status === 'APPROVED' || status === 'RESERVED') {
      return axiosClient.put(`/api/reservations/${id}/confirm`);
    }
    // no generic endpoint for other statuses here
    return Promise.reject(new Error('Unsupported status update'));
  },
  rejectReservation: (id, reason) => {
    // Use cancel for rejection; backend can distinguish by role or reason later
    return axiosClient.delete(`/api/reservations/${id}`, { params: { reason } });
  }
};

export default reservationApi;