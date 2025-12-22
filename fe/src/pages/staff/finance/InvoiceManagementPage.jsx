import React, { useEffect, useMemo, useState } from 'react';
import invoiceApi from '../../../api/invoiceApi';

export default function InvoiceManagementPage() {
  const [month, setMonth] = useState(() => {
    const d = new Date();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    return `${d.getFullYear()}-${m}`;
  });
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [status, setStatus] = useState('');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const params = useMemo(() => {
    const [y, m] = month.split('-');
    return {
      year: y ? Number(y) : undefined,
      month: m ? Number(m) : undefined,
      page,
      size,
      status: status || undefined,
    };
  }, [month, page, size, status]);

  async function load() {
    setLoading(true);
    setError('');
    try {
      const res = await invoiceApi.listPaged(params);
      setData(res);
    } catch (e) {
      setError(e?.response?.data?.message || e?.message || 'Tải dữ liệu thất bại');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [month, page, size, status]);

  async function markPaid(invoiceId, direct) {
    try {
      await invoiceApi.payInvoiceAsStaff(invoiceId, direct);
      await load();
    } catch (e) {
      setError(e?.response?.data?.message || e?.message || 'Cập nhật thanh toán thất bại');
    }
  }

  const pageObj = data;
  const items = Array.isArray(pageObj?.content) ? pageObj.content : [];
  const totalPages = pageObj?.totalPages ?? 0;

  return (
    <div className="container mx-auto px-6 py-8">
      <h1 className="text-2xl font-bold mb-6">Quản lý hóa đơn</h1>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div>
          <label className="block text-sm font-medium mb-1">Tháng</label>
          <input
            type="month"
            className="w-full border rounded px-3 py-2"
            value={month}
            onChange={(e) => {
              setMonth(e.target.value);
              setPage(0);
            }}
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Trạng thái</label>
          <select
            className="w-full border rounded px-3 py-2"
            value={status}
            onChange={(e) => {
              setStatus(e.target.value);
              setPage(0);
            }}
          >
            <option value="">Tất cả</option>
            <option value="UNPAID">Chưa thanh toán</option>
            <option value="PAID">Đã thanh toán</option>
            <option value="OVERDUE">Quá hạn</option>
          </select>
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Số dòng/trang</label>
          <select className="w-full border rounded px-3 py-2" value={size} onChange={(e) => setSize(Number(e.target.value))}>
            {[10, 20, 50].map((n) => (
              <option key={n} value={n}>
                {n}
              </option>
            ))}
          </select>
        </div>
        <div className="flex items-end">
          <button className="w-full border rounded px-3 py-2" onClick={load} disabled={loading}>
            Làm mới
          </button>
        </div>
      </div>

      {error ? <div className="mb-4 text-red-600">{error}</div> : null}

      <div className="overflow-x-auto border rounded">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50">
            <tr>
              <th className="text-left px-3 py-2">ID</th>
              <th className="text-left px-3 py-2">Hợp đồng</th>
              <th className="text-left px-3 py-2">Hạn thanh toán</th>
              <th className="text-left px-3 py-2">Số tiền</th>
              <th className="text-left px-3 py-2">Trạng thái</th>
              <th className="text-left px-3 py-2">Ngày thanh toán</th>
              <th className="text-left px-3 py-2">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={7} className="px-3 py-6 text-center">
                  Đang tải...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={7} className="px-3 py-6 text-center text-gray-500">
                  Không có hóa đơn
                </td>
              </tr>
            ) : (
              items.map((inv) => (
                <tr key={inv.id ?? inv.invoiceId} className="border-t">
                  <td className="px-3 py-2">{inv.id ?? inv.invoiceId}</td>
                  <td className="px-3 py-2">{inv.contractId ?? '-'}</td>
                  <td className="px-3 py-2">{inv.dueDate ?? '-'}</td>
                  <td className="px-3 py-2">{inv.amount ?? '-'}</td>
                  <td className="px-3 py-2">{inv.status ?? '-'}</td>
                  <td className="px-3 py-2">{inv.paidDate ?? '-'}</td>
                  <td className="px-3 py-2">
                    <div className="flex gap-2">
                      <button
                        className="border rounded px-2 py-1"
                        onClick={() => markPaid(inv.id ?? inv.invoiceId, true)}
                      >
                        Xác nhận đã thu (tiền mặt)
                      </button>
                      <button
                        className="border rounded px-2 py-1"
                        onClick={() => markPaid(inv.id ?? inv.invoiceId, false)}
                      >
                        Xác nhận đã thu (online)
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <div className="flex items-center justify-between mt-4">
        <div className="text-sm text-gray-600">
          Trang {page + 1} / {Math.max(1, totalPages || 1)}
        </div>
        <div className="flex gap-2">
          <button className="border rounded px-3 py-1" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page <= 0}>
            Trước
          </button>
          <button
            className="border rounded px-3 py-1"
            onClick={() => setPage((p) => p + 1)}
            disabled={totalPages ? page + 1 >= totalPages : items.length < size}
          >
            Sau
          </button>
        </div>
      </div>
    </div>
  );
}
