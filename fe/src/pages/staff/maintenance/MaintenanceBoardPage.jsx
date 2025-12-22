import React, { useEffect, useMemo, useState } from 'react';
import maintenanceApi from '../../../api/maintenanceApi';

const columns = [
  { key: 'PENDING', title: 'Chờ xử lý' },
  { key: 'IN_PROGRESS', title: 'Đang xử lý' },
  { key: 'DONE', title: 'Hoàn tất' },
];

export default function MaintenanceBoardPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const grouped = useMemo(() => {
    const map = Object.fromEntries(columns.map((c) => [c.key, []]));
    for (const it of items) {
      const s = it.status || 'PENDING';
      if (!map[s]) map[s] = [];
      map[s].push(it);
    }
    return map;
  }, [items]);

  async function load() {
    setLoading(true);
    setError('');
    try {
      const res = await maintenanceApi.listAllForBoard();
      const payload = res;
      const arr = Array.isArray(payload) ? payload : payload?.content ?? [];
      setItems(arr);
    } catch (e) {
      setError(e?.response?.data?.message || e?.message || 'Tải dữ liệu thất bại');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function move(itemId, nextStatus) {
    try {
      await maintenanceApi.updateStatus(itemId, nextStatus);
      await load();
    } catch (e) {
      setError(e?.response?.data?.message || e?.message || 'Cập nhật thất bại');
    }
  }

  return (
    <div className="container mx-auto px-6 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Bảng bảo trì</h1>
        <button className="border rounded px-3 py-2" onClick={load} disabled={loading}>
          Làm mới
        </button>
      </div>

      {error ? <div className="mb-4 text-red-600">{error}</div> : null}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {columns.map((col) => (
          <div key={col.key} className="border rounded p-3 bg-white">
            <div className="font-semibold mb-3">{col.title}</div>
            <div className="space-y-3">
              {loading ? (
                <div className="text-sm text-gray-500">Đang tải...</div>
              ) : (grouped[col.key] || []).length === 0 ? (
                <div className="text-sm text-gray-500">Không có dữ liệu</div>
              ) : (
                (grouped[col.key] || []).map((it) => (
                  <div key={it.id} className="border rounded p-3">
                    <div className="text-sm font-medium">#{it.id} {it.requestCode || 'Yêu cầu'}</div>
                    <div className="text-xs text-gray-600 mt-1">Người thuê: {it.tenantName || it.tenant?.fullName || '-'}</div>
                    <div className="text-xs text-gray-600">Phòng: {it.roomNumber || '-'}</div>
                    <div className="text-xs text-gray-600">Mô tả: {it.description || '-'}</div>
                    <div className="text-xs text-gray-600">Chi phí: {it.cost ?? '-'}</div>
                    <div className="flex gap-2 mt-3">
                      {col.key !== 'PENDING' ? (
                        <button className="border rounded px-2 py-1 text-xs" onClick={() => move(it.id, 'PENDING')}>
                          Chuyển sang Chờ xử lý
                        </button>
                      ) : null}
                      {col.key !== 'IN_PROGRESS' ? (
                        <button className="border rounded px-2 py-1 text-xs" onClick={() => move(it.id, 'IN_PROGRESS')}>
                          Chuyển sang Đang xử lý
                        </button>
                      ) : null}
                      {col.key !== 'DONE' ? (
                        <button className="border rounded px-2 py-1 text-xs" onClick={() => move(it.id, 'DONE')}>
                          Chuyển sang Hoàn tất
                        </button>
                      ) : null}
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
