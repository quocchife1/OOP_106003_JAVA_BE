import React, { useEffect, useState } from 'react';
import reservationApi from '../../api/reservationApi';

export default function BookingManagement(){
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchRequests = async ()=>{
    setLoading(true);
    try{
      const res = await reservationApi.getAllReservations?.();
      const data = res?.data?.result || res?.data || [];
      setRequests(Array.isArray(data)? data : (data.content || []));
    }catch(e){
      console.error('Lỗi tải yêu cầu đặt', e);
      setRequests([]);
    }finally{ setLoading(false); }
  };

  useEffect(()=>{ fetchRequests(); },[]);

  const approve = async (id)=>{
    try{
      await reservationApi.updateStatus?.(id,'APPROVED');
      setRequests(prev=> prev.map(r=> r.id===id? { ...r, status:'APPROVED'}: r));
    }catch(e){ alert('Không thể duyệt'); }
  };
  const reject = async (id)=>{
    const reason = prompt('Nhập lý do từ chối:');
    try{
      await reservationApi.rejectReservation?.(id, reason);
      setRequests(prev=> prev.map(r=> r.id===id? { ...r, status:'REJECTED', rejectReason: reason}: r));
    }catch(e){ alert('Không thể từ chối'); }
  };

  return (
      <div className="container mx-auto px-6 py-8">
        <h1 className="text-2xl font-bold mb-6">Quản lý Đặt chỗ</h1>
        {loading? <div>Tải dữ liệu...</div> : (
          <div className="bg-white rounded-xl border p-4">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left border-b">
                  <th className="py-2">Mã</th>
                  <th>Khách</th>
                  <th>Phòng</th>
                  <th>Ngày</th>
                  <th>Trạng thái</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {requests.map(r=> (
                  <tr key={r.id} className="border-b">
                    <td className="py-2">#{r.id}</td>
                    <td>{r.tenantName || r.studentName}</td>
                    <td>{r.roomName}</td>
                    <td>{new Date(r.createdAt).toLocaleString('vi-VN')}</td>
                    <td><span className="px-2 py-1 rounded bg-gray-100">{r.status}</span></td>
                    <td className="text-right">
                      <button className="px-3 py-1 rounded bg-green-600 text-white mr-2" onClick={()=>approve(r.id)}>Duyệt</button>
                      <button className="px-3 py-1 rounded bg-red-600 text-white" onClick={()=>reject(r.id)}>Từ chối</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
  );
}
