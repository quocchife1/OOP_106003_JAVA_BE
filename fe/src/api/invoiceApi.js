import axiosClient from './axiosClient';

const invoiceApi = {
  getMyInvoices: () => {
    return axiosClient.get('/api/invoices/my-invoices');
  },
  payInvoice: (id, isDirect = false) => {
    return axiosClient.post(`/api/invoices/${id}/pay`, null, {
      params: { direct: isDirect }
    });
  },

  // Staff/Finance
  listPaged: (params) => {
    return axiosClient.get('/api/invoices/paged', { params });
  },
  payInvoiceAsStaff: (id, isDirect = true) => {
    return axiosClient.post(`/api/invoices/${id}/pay`, null, {
      params: { direct: isDirect }
    });
  }
};

export default invoiceApi;