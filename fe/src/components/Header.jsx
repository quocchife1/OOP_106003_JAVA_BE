import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '../features/auth/authSlice';
import { CURRENT_SEASON } from './SeasonalEffects';

export default function Header() {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { user } = useSelector((state) => state.auth);
  const [showDropdown, setShowDropdown] = useState(false);

  const isActive = (path) => location.pathname === path;

  const handleLogout = () => {
    dispatch(logout());
    setShowDropdown(false);
    navigate('/login');
  };

  const displayUsername = user?.username || 'User';
  const displayChar = displayUsername.charAt(0).toUpperCase();

  return (
    <header className="bg-white/90 backdrop-blur-md sticky top-0 z-50 border-b border-gray-100 relative transition-all duration-300">
      <div className="container mx-auto px-6 h-20 flex items-center justify-between">

        {/* Logo */}
        <Link to="/" className="relative text-2xl font-extrabold text-indigo-600 tracking-tight flex items-center gap-2 group">
          {CURRENT_SEASON === 'CHRISTMAS' && (
            <span className="absolute -top-3 -left-2 text-2xl transform -rotate-12 filter drop-shadow-sm group-hover:rotate-0 transition-transform cursor-default">🎅</span>
          )}
          <span className="text-3xl">🏠</span>
          <span>UML Rental</span>
        </Link>

        {/* Menu Desktop */}
        <nav className="hidden md:flex items-center space-x-10">
          <Link
            to="/"
            className={`relative font-medium transition-colors duration-200 ${isActive('/') ? 'text-indigo-600' : 'text-gray-500 hover:text-indigo-600'}`}
          >
            Trang chủ
            {CURRENT_SEASON === 'CHRISTMAS' && isActive('/') && <span className="absolute -top-3 -right-3 text-xs animate-bounce">🎄</span>}
          </Link>
          <Link
            to="/partner-posts"
            className={`relative font-medium transition-colors duration-200 ${isActive('/partner-posts') ? 'text-indigo-600' : 'text-gray-500 hover:text-indigo-600'}`}
          >
            Các đơn vị khác
          </Link>
          <Link
            to="/about"
            className={`relative font-medium transition-colors duration-200 ${isActive('/about') ? 'text-indigo-600' : 'text-gray-500 hover:text-indigo-600'}`}
          >
            Về chúng tôi
            {CURRENT_SEASON === 'CHRISTMAS' && isActive('/about') && <span className="absolute -top-3 -right-3 text-xs animate-bounce">🎁</span>}
          </Link>
        </nav>

        {/* Auth Actions */}
        <div className="flex items-center space-x-4">
          {user ? (
            // --- ĐÃ ĐĂNG NHẬP ---
            <div className="relative">
              <button
                onClick={() => setShowDropdown(!showDropdown)}
                className="flex items-center gap-2 focus:outline-none bg-white hover:bg-indigo-50 px-3 py-1.5 rounded-full border border-gray-200 transition-all shadow-sm"
              >
                <div className="w-8 h-8 rounded-full bg-indigo-600 text-white flex items-center justify-center font-bold text-sm">
                  {displayChar}
                </div>
                <div className="hidden md:flex flex-col items-start text-left">
                  <span className="font-bold text-gray-700 text-sm leading-tight max-w-[150px] truncate">
                    {displayUsername}
                  </span>
                </div>
                <svg className={`w-4 h-4 text-gray-400 transition-transform ${showDropdown ? 'rotate-180' : ''}`} fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path></svg>
              </button>

              {/* Dropdown Menu */}
              {showDropdown && (
                <div className="absolute right-0 mt-2 w-64 bg-white rounded-xl shadow-xl py-2 border border-gray-100 z-50 animate-fade-in-up origin-top-right">
                  <div className="px-4 py-3 border-b border-gray-50 bg-gray-50/50">
                    <p className="text-xs text-gray-400 uppercase font-bold tracking-wider">Tài khoản</p>
                    <p className="text-sm font-bold text-indigo-600 truncate mt-1">{displayUsername}</p>
                    <p className="text-xs text-gray-500 mt-0.5">{user.role}</p>
                  </div>

                  <div className="py-1">
                    {/* Mục chung */}
                    <Link
                      to="/profile"
                      className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition-colors gap-2"
                      onClick={() => setShowDropdown(false)}
                    >
                      <span>👤</span> Hồ sơ cá nhân
                    </Link>

                    {/* --- MENU QUẢN LÝ (ADMIN/EMPLOYEE/MANAGER/RECEPTIONIST) --- */}
                    {['ADMIN', 'EMPLOYEE', 'MANAGER', 'RECEPTIONIST'].includes(user.role) && (
                      <>
                        <Link
                          to="/staff/rooms"
                          className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition-colors gap-2"
                          onClick={() => setShowDropdown(false)}
                        >
                          <span>🏢</span> Quản lý phòng
                        </Link>
                        <Link
                          to="/staff/bookings"
                          className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition-colors gap-2"
                          onClick={() => setShowDropdown(false)}
                        >
                          <span>📅</span> Đặt chỗ
                        </Link>
                        <Link
                          to="/staff/contracts/create"
                          className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition-colors gap-2"
                          onClick={() => setShowDropdown(false)}
                        >
                          <span>📝</span> Tạo hợp đồng
                        </Link>
                        <Link
                          to="/staff/inspection"
                          className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition-colors gap-2"
                          onClick={() => setShowDropdown(false)}
                        >
                          <span>🧰</span> Biên bản bàn giao
                        </Link>
                        <Link
                          to="/staff/posts/moderation"
                          className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition-colors gap-2"
                          onClick={() => setShowDropdown(false)}
                        >
                          <span>📰</span> Duyệt tin đối tác
                        </Link>
                      </>
                    )}

                    {/* --- MENU SINH VIÊN (TENANT) --- */}
                    {user.role === 'TENANT' && (
                      <Link
                        to="/tenant/dashboard"
                        className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition-colors gap-2"
                        onClick={() => setShowDropdown(false)}
                      >
                        <span>🎓</span> Cổng thông tin Sinh viên
                      </Link>
                    )}

                    {/* --- MENU ĐỐI TÁC (PARTNER) --- */}
                    {user.role === 'PARTNER' && (
                      <Link
                        to="/partner/dashboard"
                        className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition-colors gap-2"
                        onClick={() => setShowDropdown(false)}
                      >
                        <span>🏢</span> Kênh Đối tác
                      </Link>
                    )}

                    {/* --- MENU KHÁCH (GUEST) --- */}
                    {user.role === 'GUEST' && (
                      <Link
                        to="/guest/my-reservations"
                        className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition-colors gap-2"
                        onClick={() => setShowDropdown(false)}
                      >
                        <span>📅</span> Lịch sử đặt phòng
                      </Link>
                    )}
                  </div>

                  <div className="border-t border-gray-100 my-1"></div>
                  <button
                    onClick={handleLogout}
                    className="flex w-full items-center px-4 py-2 text-sm text-red-600 hover:bg-red-50 font-medium transition-colors gap-2"
                  >
                    <span>🚪</span> Đăng xuất
                  </button>
                </div>
              )}
            </div>
          ) : (
            // --- CHƯA ĐĂNG NHẬP ---
            <>
              <Link to="/login" className="hidden md:block text-gray-500 hover:text-indigo-600 font-medium transition-colors">
                Đăng nhập
              </Link>
              <Link to="/register" className="relative bg-indigo-600 text-white px-5 py-2.5 rounded-full font-semibold hover:bg-indigo-700 transition-all shadow-lg text-sm">
                Đăng ký ngay
              </Link>
            </>
          )}
        </div>

        {/* Decoration */}
        {CURRENT_SEASON === 'CHRISTMAS' && (
          <div className="absolute top-0 right-10 text-3xl transform origin-top animate-[swing_3s_ease-in-out_infinite] hidden lg:block opacity-80 pointer-events-none">
            🔔
          </div>
        )}
      </div>
    </header>
  );
}