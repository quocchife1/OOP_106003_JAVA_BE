import React, { useEffect, useState } from 'react';
import partnerApi from '../../api/partnerApi';

export default function PartnerDashboard() {
  const [stats, setStats] = useState({
    views: 0,
    activeListings: 0,
    pendingListings: 0,
    rejectedListings: 0,
    balance: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const posts = await partnerApi.getMyPosts();
      // Check data structure from API response
      const data = Array.isArray(posts) ? posts : (posts.data || posts.result || []);
      
      const active = data.filter(p => p.status === 'APPROVED' || p.status === 'ACTIVE').length;
      const pending = data.filter(p => p.status === 'PENDING_APPROVAL' || p.status === 'PENDING').length;
      const rejected = data.filter(p => p.status === 'REJECTED').length;
      const totalViews = data.reduce((acc, curr) => acc + (curr.views || 0), 0); // Tính tổng view thật
      
      setStats({
        views: totalViews,
        activeListings: active,
        pendingListings: pending,
        rejectedListings: rejected,
        balance: 1250000 // Placeholder - backend expansion needed for wallet
      });
    } catch (err) {
      console.error('Lỗi tải thống kê:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
        <div className="flex justify-center items-center h-64">
            <div className="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-indigo-600"></div>
        </div>
    );
  }

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex justify-between items-center">
        <div>
            <h1 className="text-2xl font-bold text-gray-800">Tổng quan hoạt động</h1>
            <p className="text-gray-500 text-sm mt-1">Thống kê hiệu quả tin đăng của bạn.</p>
        </div>
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-2xl shadow-sm border border-indigo-50 hover:shadow-md transition-all">
            <div className="flex items-center gap-3 mb-2">
                <span className="p-2 bg-indigo-50 text-indigo-600 rounded-lg text-xl">👀</span>
                <p className="text-gray-500 text-sm font-medium">Lượt xem tin</p>
            </div>
            <h3 className="text-3xl font-extrabold text-indigo-600 ml-1">{stats.views}</h3>
        </div>
        
        <div className="bg-white p-5 rounded-2xl shadow-sm border border-green-50 hover:shadow-md transition-all">
            <div className="flex items-center gap-3 mb-2">
                <span className="p-2 bg-green-50 text-green-600 rounded-lg text-xl">✅</span>
                <p className="text-gray-500 text-sm font-medium">Tin đang hiển thị</p>
            </div>
            <h3 className="text-3xl font-extrabold text-green-600 ml-1">{stats.activeListings}</h3>
        </div>

        <div className="bg-white p-5 rounded-2xl shadow-sm border border-yellow-50 hover:shadow-md transition-all">
            <div className="flex items-center gap-3 mb-2">
                <span className="p-2 bg-yellow-50 text-yellow-600 rounded-lg text-xl">⏳</span>
                <p className="text-gray-500 text-sm font-medium">Tin chờ duyệt</p>
            </div>
            <h3 className="text-3xl font-extrabold text-yellow-600 ml-1">{stats.pendingListings}</h3>
        </div>

        <div className="bg-white p-5 rounded-2xl shadow-sm border border-blue-50 hover:shadow-md transition-all">
            <div className="flex items-center gap-3 mb-2">
                <span className="p-2 bg-blue-50 text-blue-600 rounded-lg text-xl">💰</span>
                <p className="text-gray-500 text-sm font-medium">Số dư tài khoản</p>
            </div>
            <h3 className="text-3xl font-extrabold text-blue-600 ml-1">{stats.balance.toLocaleString()} đ</h3>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center justify-center text-gray-400 h-64 bg-gray-50/50">
            [Biểu đồ thống kê lượt xem theo tháng - Coming Soon]
        </div>
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
            <h3 className="font-bold text-gray-800 mb-4 text-lg">Thông báo mới</h3>
            <div className="space-y-4">
               {stats.pendingListings > 0 && (
                   <div className="flex gap-3 items-start p-3 bg-yellow-50 rounded-xl border border-yellow-100">
                       <span className="text-xl">⚠️</span>
                       <div>
                           <p className="text-sm font-bold text-yellow-800">Tin chờ duyệt</p>
                           <p className="text-xs text-yellow-700 mt-1">Bạn có {stats.pendingListings} tin đang chờ ban quản trị phê duyệt.</p>
                       </div>
                   </div>
               )}
               <div className="flex gap-3 items-start p-3 bg-gray-50 rounded-xl">
                   <span className="text-xl">🎉</span>
                   <div>
                       <p className="text-sm font-bold text-gray-700">Chào mừng đối tác</p>
                       <p className="text-xs text-gray-500 mt-1">Cảm ơn bạn đã đồng hành cùng UML Rental.</p>
                   </div>
               </div>
            </div>
        </div>
      </div>
    </div>
  );
}