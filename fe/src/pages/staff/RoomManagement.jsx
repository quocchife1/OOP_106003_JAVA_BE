import React, { useEffect, useState } from 'react';
import roomApi from '../../api/roomApi';

export default function RoomManagement() {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    const fetchRooms = async () => {
      setLoading(true);
      try {
        const res = await roomApi.getAllRooms();
        const data = Array.isArray(res) ? res : (res?.content || []);
        setRooms(Array.isArray(data) ? data : []);
      } catch (e) {
        console.error('Lỗi tải danh sách phòng', e);
        setRooms([]);
      } finally {
        setLoading(false);
      }
    };
    fetchRooms();
  }, []);

  const filtered = rooms.filter(r => statusFilter==='ALL' || (r.status===statusFilter));

  const updateStatus = async (roomId, status) => {
    try {
      await roomApi.updateRoomStatus?.(roomId, status);
      setRooms(prev => prev.map(r => r.id === roomId ? { ...r, status } : r));
    } catch (e) {
      console.error('Không thể cập nhật trạng thái phòng', e);
      alert('Không thể cập nhật trạng thái phòng');
    }
  };

  return (
      <div className="container mx-auto px-6 py-8">
        <h1 className="text-2xl font-bold mb-6">Quản lý phòng</h1>
        <div className="flex items-center gap-3 mb-4">
          <label className="text-sm text-gray-600">Lọc trạng thái:</label>
          <select className="border rounded px-3 py-2" value={statusFilter} onChange={e=>setStatusFilter(e.target.value)}>
            <option value="ALL">Tất cả</option>
            <option value="AVAILABLE">Trống</option>
            <option value="OCCUPIED">Đang thuê</option>
            <option value="MAINTENANCE">Bảo trì</option>
            <option value="RESERVED">Đã đặt</option>
          </select>
        </div>
        {loading ? (
          <div>Tải dữ liệu...</div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
            {filtered.map(room => (
              <div key={room.id} className="bg-white rounded-xl border p-4 shadow-sm">
                <div className="flex justify-between items-center mb-2">
                  <h3 className="font-semibold">{room.name || room.code}</h3>
                  <span className="text-xs px-2 py-1 rounded bg-gray-100">{room.status}</span>
                </div>
                <p className="text-sm text-gray-600">Giá: {new Intl.NumberFormat('vi-VN').format(room.price || room.monthlyPrice)}đ</p>
                <div className="grid grid-cols-2 gap-2 mt-3">
                  {['AVAILABLE','OCCUPIED','MAINTENANCE','RESERVED'].map(st => (
                    <button key={st} className={`text-xs px-2 py-2 rounded border ${room.status===st? 'bg-indigo-600 text-white':'bg-white'}`} onClick={()=>updateStatus(room.id, st)}>
                      {st}
                    </button>
                  ))}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
  );
}
