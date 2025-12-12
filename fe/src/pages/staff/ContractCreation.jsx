import React, { useEffect, useState } from 'react';
import contractApi from '../../api/contractApi';
import roomApi from '../../api/roomApi';

export default function ContractCreation(){
  const [rooms, setRooms] = useState([]);
  const [form, setForm] = useState({
    branchCode:'', roomNumber:'',
    tenantFullName:'', tenantPhoneNumber:'', tenantEmail:'', tenantAddress:'',
    tenantCccd:'', studentId:'', university:'', deposit:'', startDate:''
  });
  const [creating, setCreating] = useState(false);
  const [created, setCreated] = useState(null); // ContractResponse
  const [uploading, setUploading] = useState(false);

  useEffect(()=>{
    const load = async ()=>{
      try{
        const res = await roomApi.getAvailableRooms?.();
        const data = res?.data?.result || res?.data || [];
        setRooms(Array.isArray(data)? data : (data.content || []));
      }catch(e){ console.error('Lỗi tải phòng', e); }
    };
    load();
  },[]);

  const handleCreate = async ()=>{
    setCreating(true);
    setCreated(null);
    try{
      const payload = {
        branchCode: form.branchCode,
        roomNumber: form.roomNumber,
        tenantFullName: form.tenantFullName,
        tenantPhoneNumber: form.tenantPhoneNumber,
        tenantEmail: form.tenantEmail,
        tenantAddress: form.tenantAddress,
        tenantCccd: form.tenantCccd,
        studentId: form.studentId,
        university: form.university,
        deposit: form.deposit ? Number(form.deposit) : 0,
        startDate: form.startDate || null
      };
      const res = await contractApi.createContract(payload);
      const data = res?.data?.result || res?.data || res;
      setCreated(data);
    }catch(e){ console.error(e); alert('Không thể tạo hợp đồng'); }
    finally{ setCreating(false); }
  };

  const handleDownload = async ()=>{
    if(!created?.id){ return; }
    try{
      const res = await contractApi.downloadContract(created.id);
      const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `contract_${created.id}.docx`;
      a.click();
      window.URL.revokeObjectURL(url);
    }catch(e){ console.error(e); alert('Không thể tải hợp đồng'); }
  };

  const handleUploadSigned = async (e)=>{
    if(!created?.id){ return; }
    const file = e.target.files?.[0];
    if(!file) return;
    setUploading(true);
    try{
      const res = await contractApi.uploadSigned(created.id, file);
      const data = res?.data?.result || res?.data || res;
      setCreated(data);
      alert('Đã tải hợp đồng đã ký');
    }catch(err){ console.error(err); alert('Không thể tải lên hợp đồng đã ký'); }
    finally{ setUploading(false); }
  };

  return (
      <div className="container mx-auto px-6 py-8">
        <h1 className="text-2xl font-bold mb-6">Tạo hợp đồng</h1>
        <div className="bg-white rounded-xl border p-6 max-w-3xl">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm mb-1">Chi nhánh</label>
              <input className="w-full border rounded px-3 py-2" placeholder="CN01" value={form.branchCode} onChange={e=>setForm({ ...form, branchCode: e.target.value })} />
            </div>
            <div>
              <label className="block text-sm mb-1">Số phòng</label>
              <input className="w-full border rounded px-3 py-2" placeholder="P101" value={form.roomNumber} onChange={e=>setForm({ ...form, roomNumber: e.target.value })} />
            </div>
          </div>

          <h3 className="font-semibold mt-6 mb-2">Thông tin khách hàng</h3>
          <div className="grid grid-cols-2 gap-4">
            <input className="border rounded px-3 py-2" placeholder="Họ tên" value={form.tenantFullName} onChange={e=>setForm({ ...form, tenantFullName: e.target.value })} />
            <input className="border rounded px-3 py-2" placeholder="Số điện thoại" value={form.tenantPhoneNumber} onChange={e=>setForm({ ...form, tenantPhoneNumber: e.target.value })} />
            <input className="border rounded px-3 py-2" placeholder="Email" value={form.tenantEmail} onChange={e=>setForm({ ...form, tenantEmail: e.target.value })} />
            <input className="border rounded px-3 py-2" placeholder="Địa chỉ" value={form.tenantAddress} onChange={e=>setForm({ ...form, tenantAddress: e.target.value })} />
            <input className="border rounded px-3 py-2" placeholder="CCCD" value={form.tenantCccd} onChange={e=>setForm({ ...form, tenantCccd: e.target.value })} />
            <input className="border rounded px-3 py-2" placeholder="Mã SV" value={form.studentId} onChange={e=>setForm({ ...form, studentId: e.target.value })} />
            <input className="border rounded px-3 py-2" placeholder="Trường/Đơn vị" value={form.university} onChange={e=>setForm({ ...form, university: e.target.value })} />
          </div>

          <h3 className="font-semibold mt-6 mb-2">Thông tin hợp đồng</h3>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm mb-1">Tiền cọc</label>
              <input type="number" className="w-full border rounded px-3 py-2" value={form.deposit} onChange={e=>setForm({ ...form, deposit: e.target.value })} />
            </div>
            <div>
              <label className="block text-sm mb-1">Ngày bắt đầu</label>
              <input type="date" className="w-full border rounded px-3 py-2" value={form.startDate} onChange={e=>setForm({ ...form, startDate: e.target.value })} />
            </div>
          </div>

          <div className="flex flex-wrap gap-3 justify-end mt-6">
            <button disabled={creating} className="px-4 py-2 rounded bg-indigo-600 text-white" onClick={handleCreate}>Lưu & Tạo hợp đồng</button>
            <button disabled={!created?.id} className="px-4 py-2 rounded bg-emerald-600 text-white disabled:opacity-50" onClick={handleDownload}>Tải hợp đồng (DOCX)</button>
            <label className={`px-4 py-2 rounded bg-purple-600 text-white cursor-pointer ${!created?.id ? 'opacity-50 cursor-not-allowed' : ''}`}>
              Tải lên hợp đồng đã ký
              <input type="file" accept="image/*,application/pdf" className="hidden" disabled={!created?.id || uploading} onChange={handleUploadSigned} />
            </label>
          </div>
        </div>
      </div>
  );
}
