import React, { useEffect, useState } from 'react';
import partnerApi from '../../api/partnerApi';

export default function PostModeration(){
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selected, setSelected] = useState(null); // detail
  const [detailLoading, setDetailLoading] = useState(false);
  const [rejectReason, setRejectReason] = useState('');
  const [status, setStatus] = useState('PENDING_APPROVAL');
  const [q, setQ] = useState('');

  const fetchPosts = async ()=>{
    setLoading(true);
    try{
      const res = await partnerApi.getManagementPosts?.({ status, q, page: 0, size: 12 });
      const data = res?.data?.result || res?.data || [];
      setPosts(Array.isArray(data)? data : (data.content || []));
    }catch(e){ console.error('Lỗi tải tin chờ duyệt', e); setPosts([]); }
    finally{ setLoading(false); }
  };
  useEffect(()=>{ fetchPosts(); },[status]);

  const approve = async (id)=>{
    try{
      await partnerApi.approvePost?.(id);
      // After action, refetch to reflect current filter
      await fetchPosts();
      setSelected(null);
    }catch(e){ alert('Không thể duyệt tin'); }
  };
  const reject = async (id)=>{
    const reason = rejectReason || prompt('Nhập lý do từ chối:');
    try{
      await partnerApi.rejectPost?.(id, reason);
      await fetchPosts();
      setSelected(null);
    }catch(e){ alert('Không thể từ chối'); }
  };

  const openDetail = async (id)=>{
    setDetailLoading(true);
    setRejectReason('');
    try{
      const res = await partnerApi.getManagementPostById(id);
      const data = res?.data?.result || res?.data || res;
      setSelected(data);
    }catch(e){ console.error('Không tải được chi tiết', e); }
    finally{ setDetailLoading(false); }
  };

  return (
      <div className="container mx-auto px-6 py-8">
        <h1 className="text-2xl font-bold mb-6">Duyệt tin đối tác</h1>
        <div className="flex flex-col md:flex-row md:items-center gap-3 mb-6">
          <div className="inline-flex rounded-lg border overflow-hidden">
            {[
              { key: 'PENDING_APPROVAL', label: 'Pending' },
              { key: 'APPROVED', label: 'Approved' },
              { key: 'REJECTED', label: 'Rejected' }
            ].map(tab => (
              <button
                key={tab.key}
                className={`px-4 py-2 text-sm ${status===tab.key? 'bg-indigo-600 text-white' : 'bg-white hover:bg-gray-50'}`}
                onClick={()=> setStatus(tab.key)}
              >{tab.label}</button>
            ))}
          </div>
          <div className="flex items-center gap-2 md:ml-auto">
            <input
              type="text"
              value={q}
              onChange={e=> setQ(e.target.value)}
              onKeyDown={e=>{ if(e.key==='Enter'){ fetchPosts(); } }}
              placeholder="Tìm theo tiêu đề..."
              className="w-full md:w-64 border rounded-lg px-3 py-2"
            />
            <button className="px-4 py-2 rounded bg-gray-800 text-white" onClick={fetchPosts}>Tìm</button>
          </div>
        </div>
        {loading? <div>Tải dữ liệu...</div> : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {posts.map(post=> (
              <div key={post.id} className="bg-white rounded-xl border p-4 shadow-sm">
                <h3 className="font-semibold mb-1 line-clamp-2">{post.title}</h3>
                <p className="text-sm text-gray-600 mb-2 line-clamp-3">{post.description}</p>
                <div className="flex justify-between items-center">
                  <span className="text-indigo-600 font-bold">{new Intl.NumberFormat('vi-VN',{style:'currency',currency:'VND'}).format(post.price)}</span>
                  <div className="flex gap-2">
                    <button className="px-3 py-1 rounded bg-gray-200" onClick={()=>openDetail(post.id)}>Xem</button>
                    <button className="px-3 py-1 rounded bg-green-600 text-white" onClick={()=>approve(post.id)}>Duyệt</button>
                    <button className="px-3 py-1 rounded bg-red-600 text-white" onClick={()=>reject(post.id)}>Từ chối</button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {selected && (
          <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
            <div className="bg-white rounded-2xl w-full max-w-4xl p-6 shadow-2xl">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-bold">Kiểm tra nội dung tin #{selected.id}</h2>
                <button className="text-gray-500" onClick={()=>setSelected(null)}>✕</button>
              </div>
              {detailLoading ? (
                <div>Tải chi tiết...</div>
              ) : (
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                  <div className="lg:col-span-2">
                    <div className="h-64 bg-gray-100 rounded-xl overflow-hidden mb-3">
                      <img src={selected.imageUrls?.[0] ? `http://localhost:8080${selected.imageUrls[0]}` : 'https://placehold.co/800x400?text=No+Image'} alt="" className="w-full h-full object-cover"/>
                    </div>
                    <div className="flex gap-2 overflow-x-auto pb-2">
                      {(selected.imageUrls||[]).map((u,i)=> (
                        <img key={i} src={`http://localhost:8080${u}`} alt="thumb" className="w-24 h-24 object-cover rounded-lg border"/>
                      ))}
                    </div>
                    <h3 className="text-lg font-semibold mt-4">{selected.title}</h3>
                    <div className="text-sm text-gray-500 mb-2">{selected.address}</div>
                    <div className="grid grid-cols-3 gap-3 text-sm">
                      <div className="bg-gray-50 p-2 rounded">Giá: <b>{new Intl.NumberFormat('vi-VN',{style:'currency',currency:'VND'}).format(selected.price)}</b></div>
                      <div className="bg-gray-50 p-2 rounded">Diện tích: <b>{selected.area} m²</b></div>
                      <div className="bg-gray-50 p-2 rounded">Đối tác: <b>{selected.partnerName}</b></div>
                    </div>
                    <div className="mt-3 text-sm bg-gray-50 p-3 rounded">SĐT liên hệ: <b>{selected.partnerPhone || '—'}</b></div>
                    <div className="mt-4 p-3 bg-white border rounded-lg max-h-48 overflow-auto whitespace-pre-line">
                      {selected.description}
                    </div>
                  </div>
                  <div className="lg:col-span-1">
                    <label className="block text-sm font-medium mb-1">Lý do từ chối (nếu có)</label>
                    <textarea className="w-full border rounded-lg p-2 min-h-[120px]" value={rejectReason} onChange={e=>setRejectReason(e.target.value)} placeholder="Ví dụ: ảnh không rõ ràng, thông tin giá chưa hợp lệ"/>
                    <div className="flex flex-col gap-2 mt-4">
                      <button className="px-4 py-2 rounded bg-green-600 text-white" onClick={()=>approve(selected.id)}>Duyệt tin</button>
                      <button className="px-4 py-2 rounded bg-red-600 text-white" onClick={()=>reject(selected.id)}>Từ chối tin</button>
                      <button className="px-4 py-2 rounded bg-gray-200" onClick={()=>setSelected(null)}>Đóng</button>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
  );
}
