import axiosClient from './axiosClient';

const contractApi = {
  createContract: (payload) => {
    // FE simplified payload; backend expects ContractCreateRequest
    return axiosClient.post('/api/contracts', payload);
  },
  getMyContracts: () => {
    return axiosClient.get('/api/contracts/my-contracts');
  },
  downloadContract: (id) => {
    return axiosClient.get(`/api/contracts/${id}/download`, {
      responseType: 'blob',
    });
  },
  uploadSigned: (id, file) => {
    const form = new FormData();
    form.append('file', file);
    return axiosClient.post(`/api/contracts/${id}/upload-signed`, form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  },
  requestCheckout: (id, data) => {
    // API Checkout Request: POST /api/contracts/{id}/checkout-request
    // Payload: { requestDate, reason }
    return axiosClient.post(`/api/contracts/${id}/checkout-request`, data);
  }
};

export default contractApi;