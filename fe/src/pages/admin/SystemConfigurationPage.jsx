import React, { useEffect, useState } from 'react';
import systemConfigApi from '../../api/systemConfigApi';

export default function SystemConfigurationPage() {
  const [config, setConfig] = useState(null);
  const [electricPrice, setElectricPrice] = useState('');
  const [waterPrice, setWaterPrice] = useState('');
  const [lateFeePerDay, setLateFeePerDay] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [saved, setSaved] = useState(false);

  async function load() {
    setLoading(true);
    setError('');
    setSaved(false);
    try {
      const res = await systemConfigApi.get();
      const payload = res;
      setConfig(payload);
      setElectricPrice(payload?.electricPricePerUnit ?? '');
      setWaterPrice(payload?.waterPricePerUnit ?? '');
      setLateFeePerDay(payload?.lateFeePerDay ?? '');
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

  async function save() {
    setLoading(true);
    setError('');
    setSaved(false);
    try {
      const res = await systemConfigApi.upsert({
        electricPricePerUnit: electricPrice === '' ? null : Number(electricPrice),
        waterPricePerUnit: waterPrice === '' ? null : Number(waterPrice),
        lateFeePerDay: lateFeePerDay === '' ? null : Number(lateFeePerDay),
      });
      const payload = res;
      setConfig(payload);
      setSaved(true);
    } catch (e) {
      setError(e?.response?.data?.message || e?.message || 'Save failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="container mx-auto px-6 py-8">
      <h1 className="text-2xl font-bold mb-6">Cấu hình hệ thống</h1>

      {error ? <div className="mb-4 text-red-600">{error}</div> : null}
      {saved ? <div className="mb-4 text-green-700">Đã lưu</div> : null}

      <div className="border rounded p-4 max-w-xl">
        <div className="grid grid-cols-1 gap-4">
          <div>
            <label className="block text-sm font-medium mb-1">Giá điện (đ/kWh)</label>
            <input type="number" className="w-full border rounded px-3 py-2" value={electricPrice} onChange={(e) => setElectricPrice(e.target.value)} placeholder="VD: 3500" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Giá nước (đ/m³)</label>
            <input type="number" className="w-full border rounded px-3 py-2" value={waterPrice} onChange={(e) => setWaterPrice(e.target.value)} placeholder="VD: 15000" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Phí trễ hạn (đ/ngày)</label>
            <input type="number" className="w-full border rounded px-3 py-2" value={lateFeePerDay} onChange={(e) => setLateFeePerDay(e.target.value)} placeholder="VD: 20000" />
          </div>
          <div className="flex gap-2">
            <button className="border rounded px-3 py-2" onClick={save} disabled={loading}>
              Lưu
            </button>
            <button className="border rounded px-3 py-2" onClick={load} disabled={loading}>
              Tải lại
            </button>
          </div>
        </div>

        <div className="mt-6">
          <div className="text-sm text-gray-600 mb-2">Dữ liệu thô</div>
          <pre className="text-xs whitespace-pre-wrap">{JSON.stringify(config, null, 2)}</pre>
        </div>
      </div>
    </div>
  );
}
