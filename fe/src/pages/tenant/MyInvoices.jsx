import React, { useEffect, useState } from 'react';
import invoiceApi from '../../api/invoiceApi';

export default function MyInvoices() {
  const [invoices, setInvoices] = useState([]);
  const [processingId, setProcessingId] = useState(null);

  useEffect(() => {
    const fetchInvoices = async () => {
      try {
        const res = await invoiceApi.getMyInvoices();
        setInvoices(res.data.result);
      } catch (error) {
        console.error(error);
      }
    };
    fetchInvoices();
  }, []);

  const handlePay = async (id) => {
    setProcessingId(id);
    try {
      await invoiceApi.payInvoice(id, false);
      alert('Thanh toán thành công! (Mô phỏng)');
      
      setInvoices(prev => prev.map(inv => inv.id === id ? { ...inv, status: 'PAID' } : inv));
    } catch (error) {
      alert('Lỗi thanh toán: ' + (error.response?.data?.message || 'Unknown'));
    } finally {
      setProcessingId(null);
    }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold text-gray-800">Hóa đơn của tôi</h2>
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left whitespace-nowrap">
            <thead className="bg-gray-50 text-gray-500 text-xs uppercase font-bold border-b border-gray-100">
              <tr>
                <th className="px-6 py-4">Kỳ hạn</th>
                <th className="px-6 py-4">Hạn đóng</th>
                <th className="px-6 py-4 text-right">Tổng tiền</th>
                <th className="px-6 py-4 text-center">Trạng thái</th>
                <th className="px-6 py-4 text-center">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 text-sm">
              {invoices.map((inv) => (
                <tr key={inv.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 font-medium text-gray-900">
                    Tháng {inv.month}/{inv.year}
                  </td>
                  <td className="px-6 py-4 text-gray-500">
                    {new Date(inv.dueDate).toLocaleDateString()}
                  </td>
                  <td className="px-6 py-4 text-right font-bold text-gray-900">
                    {inv.totalAmount.toLocaleString()} đ
                  </td>
                  <td className="px-6 py-4 text-center">
                    <span className={`px-3 py-1 rounded-full text-xs font-bold 
                      ${inv.status === 'PAID' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                      {inv.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-center">
                    {inv.status === 'UNPAID' ? (
                      <button 
                        onClick={() => handlePay(inv.id)}
                        disabled={processingId === inv.id}
                        className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg text-xs font-bold transition-all shadow-sm disabled:opacity-50">
                        {processingId === inv.id ? 'Đang xử lý...' : 'Thanh toán ngay'}
                      </button>
                    ) : (
                      <span className="text-green-600 text-xs font-bold">✓ Hoàn tất</span>
                    )}
                  </td>
                </tr>
              ))}
              {invoices.length === 0 && (
                <tr>
                  <td colSpan="5" className="px-6 py-8 text-center text-gray-500">Bạn không có hóa đơn nào.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}