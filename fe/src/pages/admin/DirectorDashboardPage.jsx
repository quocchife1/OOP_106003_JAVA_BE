import React, { useEffect, useState } from 'react';
import dashboardApi from '../../api/dashboardApi';

export default function DirectorDashboardPage() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function load() {
    setLoading(true);
    setError('');
    try {
      const res = await dashboardApi.getDirectorDashboard();
      setData(res);
    } catch (e) {
      setError(e?.response?.data?.message || e?.message || 'Load failed');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const payload = data;

  return (
    <div className="container mx-auto px-6 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Bảng điều khiển giám đốc</h1>
        <button className="border rounded px-3 py-2" onClick={load} disabled={loading}>
          Làm mới
        </button>
      </div>

      {error ? <div className="mb-4 text-red-600">{error}</div> : null}

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="border rounded p-4">
          <div className="text-sm text-gray-600">Doanh thu</div>
          <div className="text-xl font-semibold">{loading ? '...' : payload?.revenue ?? '-'}</div>
        </div>
        <div className="border rounded p-4">
          <div className="text-sm text-gray-600">Tỷ lệ lấp đầy</div>
          <div className="text-xl font-semibold">{loading ? '...' : payload?.occupancyRate ?? payload?.occupancy ?? '-'}</div>
        </div>
        <div className="border rounded p-4">
          <div className="text-sm text-gray-600">Công nợ</div>
          <div className="text-xl font-semibold">{loading ? '...' : payload?.outstandingDebt ?? payload?.debt ?? '-'}</div>
        </div>
        <div className="border rounded p-4">
          <div className="text-sm text-gray-600">Bảo trì đang mở</div>
          <div className="text-xl font-semibold">{loading ? '...' : payload?.openMaintenance ?? payload?.maintenanceOpen ?? '-'}</div>
        </div>
      </div>

      <div className="border rounded p-4 mt-6">
        <div className="text-sm text-gray-600 mb-2">Dữ liệu thô</div>
        <pre className="text-xs whitespace-pre-wrap">{JSON.stringify(payload, null, 2)}</pre>
      </div>
    </div>
  );
}
