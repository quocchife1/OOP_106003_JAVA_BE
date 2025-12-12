import React, { useEffect, useState } from 'react';
import contractApi from '../../api/contractApi';

export default function MyContracts() {
  const [contracts, setContracts] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [selectedContractId, setSelectedContractId] = useState(null);
  const [checkoutDate, setCheckoutDate] = useState('');
  const [reason, setReason] = useState('');

  useEffect(() => {
    const fetchContracts = async () => {
      try {
        const res = await contractApi.getMyContracts();
        // Normalize shapes: axiosClient may return the data array directly, a Page object, or a wrapper
        let data = [];
        if (Array.isArray(res)) data = res;
        else if (res && res.content) data = res.content;
        else if (res && res.data) data = res.data.result || res.data || [];
        else data = res || [];
        setContracts(data);
      } catch (error) {
        console.error(error);
      }
    };
    fetchContracts();
  }, []);

  const handleDownload = async (id, code) => {
    try {
      const response = await contractApi.downloadContract(id);
      // axiosClient returns full response for blob types; normalize
      const blob = response && response.data ? response.data : response;
      const url = window.URL.createObjectURL(new Blob([blob]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `HopDong_${code}.docx`);
      document.body.appendChild(link);
      link.click();
    } catch (error) {
      alert("Không thể tải file hợp đồng.");
    }
  };

  const openCheckoutModal = (id) => {
    setSelectedContractId(id);
    setShowModal(true);
  };

  const submitCheckout = async (e) => {
    e.preventDefault();
    try {
      await contractApi.requestCheckout(selectedContractId, {
        requestDate: checkoutDate, 
        reason: reason
      });
      alert('Gửi yêu cầu trả phòng thành công!');
      setShowModal(false);
      setReason('');
      setCheckoutDate('');
    } catch (error) {
      alert(error.response?.data?.message || 'Gửi yêu cầu thất bại');
    }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold text-gray-800">Hợp đồng thuê phòng</h2>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {contracts.map(contract => (
          <div key={contract.id} className="bg-white p-6 rounded-xl shadow-sm border border-gray-200 hover:border-indigo-300 transition-all">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="text-lg font-bold text-gray-900">Mã: {contract.contractCode}</h3>
                <p className="text-gray-500">Phòng: {contract.roomCode || 'N/A'}</p>
              </div>
              <span className={`px-3 py-1 rounded-full text-xs font-bold ${contract.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700'}`}>
                {contract.status}
              </span>
            </div>
            
            <div className="space-y-2 text-sm text-gray-700 mb-6 bg-gray-50 p-4 rounded-lg">
              <div className="flex justify-between">
                <span>Ngày bắt đầu:</span>
                <span className="font-medium">{new Date(contract.startDate).toLocaleDateString()}</span>
              </div>
              <div className="flex justify-between">
                <span>Ngày kết thúc:</span>
                <span className="font-medium">{new Date(contract.endDate).toLocaleDateString()}</span>
              </div>
              <div className="flex justify-between">
                <span>Tiền cọc:</span>
                <span className="font-medium text-indigo-600">{contract.depositAmount?.toLocaleString()} đ</span>
              </div>
            </div>

            <div className="flex gap-3">
              <button 
                onClick={() => handleDownload(contract.id, contract.contractCode)}
                className="flex-1 bg-white border border-gray-300 text-gray-700 py-2 rounded-lg font-medium hover:bg-gray-50 transition-colors text-sm flex items-center justify-center gap-2">
                <span>📥</span> Tải về
              </button>
              {contract.status === 'ACTIVE' && (
                <button 
                  onClick={() => openCheckoutModal(contract.id)}
                  className="flex-1 bg-red-50 text-red-600 border border-red-100 py-2 rounded-lg font-medium hover:bg-red-100 transition-colors text-sm">
                  Trả phòng
                </button>
              )}
            </div>
          </div>
        ))}
        {contracts.length === 0 && <p className="text-gray-500 col-span-2 text-center py-10">Bạn chưa có hợp đồng nào.</p>}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white p-6 rounded-xl w-full max-w-md shadow-2xl animate-fade-in-up">
            <h3 className="text-xl font-bold mb-4 text-gray-800">Yêu cầu trả phòng</h3>
            <form onSubmit={submitCheckout} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Ngày dự kiến trả</label>
                <input 
                  type="date" 
                  required 
                  className="w-full border-gray-300 rounded-lg shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
                  value={checkoutDate}
                  onChange={(e) => setCheckoutDate(e.target.value)}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Lý do</label>
                <textarea 
                  required 
                  rows="3" 
                  className="w-full border-gray-300 rounded-lg shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
                  placeholder="Ví dụ: Chuyển công tác, hết hạn hợp đồng..."
                  value={reason}
                  onChange={(e) => setReason(e.target.value)}
                ></textarea>
              </div>
              <div className="flex justify-end gap-3 mt-6">
                <button type="button" onClick={() => setShowModal(false)} className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg font-medium">Đóng</button>
                <button type="submit" className="px-4 py-2 bg-red-600 text-white hover:bg-red-700 rounded-lg font-medium shadow-sm">Gửi yêu cầu</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}