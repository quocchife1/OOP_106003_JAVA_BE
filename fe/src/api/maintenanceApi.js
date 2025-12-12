import axiosClient from './axiosClient';

const maintenanceApi = {
  createRequest: (formData) => {
    return axiosClient.post('/api/maintenance', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  // SỬA LẠI: Dùng API tự lấy thông tin từ Token
  getMyRequests: () => {
    return axiosClient.get('/api/maintenance/my-requests');
  },
  // Giữ lại hàm cũ để Admin dùng nếu cần
  getRequestsByTenant: (tenantId) => {
    return axiosClient.get(`/api/maintenance/tenant/${tenantId}`);
  }
};

export default maintenanceApi;