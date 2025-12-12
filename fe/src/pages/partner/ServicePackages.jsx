import React, { useEffect, useState } from 'react';
import partnerApi from '../../api/partnerApi';
import { useNavigate } from 'react-router-dom';

export default function ServicePackages() {
  const navigate = useNavigate();
  const [packages, setPackages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [purchasing, setPurchasing] = useState(null);

  useEffect(() => {
    const fetchPackages = async () => {
      try {
        const res = await partnerApi.getServicePackages();
        console.log('Service packages response:', res);
        const data = res?.data?.result || res?.data || res || [];
        console.log('Parsed packages:', data);
        setPackages(Array.isArray(data) ? data : []);
      } catch (e) {
        console.error('Lỗi tải gói dịch vụ', e);
        setPackages([]);
      } finally {
        setLoading(false);
      }
    };
    fetchPackages();
  }, []);

  const handlePurchase = async (pkg) => {
    // Simulate purchase: get a post to activate
    const postId = prompt('Nhập ID tin đăng cần kích hoạt (hoặc để trống để bỏ qua):');
    if (!postId) {
      alert('Bạn cần chọn một tin đăng để mua gói.');
      return;
    }
    setPurchasing(pkg.id);
    try {
      const res = await partnerApi.simulatePurchase(postId, pkg.id);
      alert('Mua gói thành công! Tin đã được kích hoạt.');
      navigate('/partner/my-listings');
    } catch (e) {
      console.error('Lỗi mua gói', e);
      alert('Lỗi: ' + (e.response?.data?.message || 'Không thể mua gói'));
    } finally {
      setPurchasing(null);
    }
  };

  if (loading) return <div className="text-center py-12">Đang tải...</div>;

  return (
    <div className="space-y-10 animate-fade-in">
      <div className="text-center max-w-2xl mx-auto space-y-4">
        <h1 className="text-4xl font-extrabold text-gray-900">Nâng cấp tài khoản</h1>
        <p className="text-gray-500 text-lg">Chọn gói dịch vụ phù hợp để tiếp cận khách hàng tiềm năng nhanh chóng hơn.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8 max-w-6xl mx-auto px-4">
        {packages.map((pkg, idx) => (
            <div key={pkg.id} className={`relative bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden transform transition-all duration-300 hover:-translate-y-2 hover:shadow-2xl ${idx === 1 ? 'ring-4 ring-indigo-100 scale-105 z-10' : ''}`}>
                {idx === 1 && (
                    <div className="absolute top-0 left-0 w-full bg-gradient-to-r from-indigo-500 to-purple-600 text-white text-center text-xs font-bold py-1.5 uppercase tracking-widest">
                        Khuyên dùng
                    </div>
                )}
                
                <div className="p-8 text-center pt-12">
                    <div className="w-16 h-16 mx-auto rounded-2xl bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-3xl shadow-lg text-white mb-6">
                        💎
                    </div>
                    <h3 className="text-xl font-bold text-gray-900">{pkg.name}</h3>
                    <div className="my-6 flex items-end justify-center gap-1 text-gray-900">
                        <span className="text-4xl font-extrabold">{pkg.price?.toLocaleString()}</span>
                        <span className="text-gray-500 font-medium mb-1">đ</span>
                    </div>
                    <button onClick={() => handlePurchase(pkg)} disabled={purchasing === pkg.id} className="w-full py-3 rounded-xl font-bold text-white shadow-lg transition-transform active:scale-95 bg-gradient-to-r from-indigo-500 to-purple-600 disabled:opacity-50">
                        {purchasing === pkg.id ? 'Đang xử lý...' : 'Mua ngay'}
                    </button>
                </div>
                
                <div className="bg-gray-50 p-8 border-t border-gray-100 h-full">
                    <ul className="space-y-4">
                        <li className="flex items-center gap-3 text-sm text-gray-600">
                            <div className="w-5 h-5 rounded-full bg-green-100 text-green-600 flex items-center justify-center text-xs">✓</div>
                            {pkg.description || 'Gói dịch vụ chất lượng'}
                        </li>
                        <li className="flex items-center gap-3 text-sm text-gray-600">
                            <div className="w-5 h-5 rounded-full bg-green-100 text-green-600 flex items-center justify-center text-xs">✓</div>
                            Thời hạn: {pkg.durationDays} ngày
                        </li>
                    </ul>
                </div>
            </div>
        ))}
      </div>
    </div>
  );
}