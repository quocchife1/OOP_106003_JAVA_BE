import React, { useState } from 'react';

export default function Inspection(){
  const [checklist, setChecklist] = useState([
    { key:'bed', label:'Giường', ok:true, note:''},
    { key:'wardrobe', label:'Tủ', ok:true, note:''},
    { key:'aircon', label:'Máy lạnh', ok:true, note:''},
  ]);
  const [damageFees, setDamageFees] = useState([]);

  const toggle = (key)=> setChecklist(prev=> prev.map(i=> i.key===key? { ...i, ok: !i.ok }: i));
  const noteChange = (key, val)=> setChecklist(prev=> prev.map(i=> i.key===key? { ...i, note: val }: i));

  const submit = ()=>{
    const damages = checklist.filter(i=> !i.ok || i.note).map(i=> ({ item: i.label, note: i.note, fee: 0 }));
    setDamageFees(damages);
    alert('Đã lưu biên bản');
  };

  return (
      <div className="container mx-auto px-6 py-8">
        <h1 className="text-2xl font-bold mb-6">Biên bản bàn giao/Trả phòng</h1>
        <div className="bg-white rounded-xl border p-6 max-w-3xl">
          {checklist.map(item=> (
            <div key={item.key} className="flex items-center gap-4 border-b py-3">
              <input type="checkbox" checked={item.ok} onChange={()=>toggle(item.key)} />
              <span className="w-40">{item.label}</span>
              <input className="flex-1 border rounded px-3 py-2" placeholder="Ghi chú" value={item.note} onChange={e=>noteChange(item.key, e.target.value)} />
            </div>
          ))}
          <div className="flex justify-end mt-4">
            <button className="px-4 py-2 rounded bg-indigo-600 text-white" onClick={submit}>Lưu biên bản</button>
          </div>
        </div>

        {damageFees.length>0 && (
          <div className="bg-white rounded-xl border p-6 mt-6 max-w-3xl">
            <h2 className="font-semibold mb-3">Bồi hoàn</h2>
            {damageFees.map((d, idx)=> (
              <div key={idx} className="flex justify-between border-b py-2">
                <span>{d.item} - {d.note}</span>
                <span>{new Intl.NumberFormat('vi-VN').format(d.fee)}đ</span>
              </div>
            ))}
          </div>
        )}
      </div>
  );
}
