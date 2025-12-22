import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';

// --- Shared Components ---
import ProtectedRoute from './components/ProtectedRoute';
import MainLayout from './components/MainLayout';

// --- Public Pages ---
import HomePage from './pages/HomePage';
import AboutPage from './pages/AboutPage';
import RoomDetailPage from './pages/RoomDetailPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProfilePage from './pages/ProfilePage';

// --- Tenant Pages (Sinh viên) ---
import TenantLayout from './components/TenantLayout';
import TenantDashboard from './pages/tenant/TenantDashboard';
import MyContracts from './pages/tenant/MyContracts';
import MyInvoices from './pages/tenant/MyInvoices';
import MaintenanceRequests from './pages/tenant/MaintenanceRequests';
import MyReservations from './pages/tenant/MyReservations';

// --- Partner Pages (Chủ trọ) ---
import PartnerLayout from './components/PartnerLayout';
import PartnerDashboard from './pages/partner/PartnerDashboard';
import MyListings from './pages/partner/MyListings';
import CreateListing from './pages/partner/CreateListing';
import EditListing from './pages/partner/EditListing';
import ServicePackages from './pages/partner/ServicePackages';
import PartnerPostsList from './pages/public/PartnerPostsList';
import PartnerPostDetail from './pages/public/PartnerPostDetail';
// --- Staff Pages (Internal) ---
import RoomManagement from './pages/staff/RoomManagement';
import BookingManagement from './pages/staff/BookingManagement';
import ContractCreation from './pages/staff/ContractCreation';
import Inspection from './pages/staff/Inspection';
import PostModeration from './pages/staff/PostModeration';

// --- Finance & Technical ---
import InvoiceManagementPage from './pages/staff/finance/InvoiceManagementPage';
import FinancialReportsPage from './pages/staff/finance/FinancialReportsPage';
import MaintenanceBoardPage from './pages/staff/maintenance/MaintenanceBoardPage';

// --- Admin & Director ---
import DirectorDashboardPage from './pages/admin/DirectorDashboardPage';
import UserEmployeeManagementPage from './pages/admin/UserEmployeeManagementPage';
import SystemConfigurationPage from './pages/admin/SystemConfigurationPage';
import AuditLogsPage from './pages/admin/AuditLogsPage';

const appRoutes = [
  // ==============================
  // 1. PUBLIC ROUTES
  // ==============================
  { path: '/', element: <HomePage /> },
  { path: '/about', element: <AboutPage /> },
  { path: '/rooms/:id', element: <RoomDetailPage /> },
  { path: '/partner-posts', element: (
      <MainLayout>
        <div className="min-h-[60vh]">
          <PartnerPostsList />
        </div>
      </MainLayout>
    ) },
  { path: '/partner-posts/:id', element: <PartnerPostDetail /> },
  { path: '/login', element: <LoginPage /> },
  { path: '/register', element: <RegisterPage /> },
  
  // ==============================
  // 2. USER SHARED ROUTES
  // ==============================
  { 
    path: '/profile', 
    element: (
      <ProtectedRoute>
        <MainLayout>
           <div className="container mx-auto px-6 py-10">
              <ProfilePage />
           </div>
        </MainLayout>
      </ProtectedRoute>
    ) 
  },

  // ==============================
  // 3. GUEST PORTAL (Khách vãng lai)
  // ==============================
  {
    path: '/guest',
    element: (
      <ProtectedRoute allowedRoles={['GUEST']}>
        <MainLayout>
            {/* Wrapper layout cho Guest */}
            <div className="min-h-[60vh]">
                <Outlet />
            </div>
        </MainLayout>
      </ProtectedRoute>
    ),
    children: [
        { 
            path: 'my-reservations', 
            element: (
                <div className="container mx-auto px-6 py-10">
                    <MyReservations isGuestView={true} /> 
                </div>
            )
        },
        // Fallback cho guest
        { path: '', element: <Navigate to="my-reservations" replace /> }
    ]
  },

  // ==============================
  // 4. TENANT PORTAL (Sinh viên thuê phòng)
  // ==============================
  {
    path: '/tenant',
    element: (
      <ProtectedRoute allowedRoles={['TENANT']}>
        <TenantLayout />
      </ProtectedRoute>
    ),
    children: [
      { path: 'dashboard', element: <TenantDashboard /> },
      { path: 'contracts', element: <MyContracts /> },
      { path: 'invoices', element: <MyInvoices /> },
      { path: 'maintenance', element: <MaintenanceRequests /> },
      { path: 'reservations', element: <MyReservations /> },
      { path: '', element: <Navigate to="dashboard" replace /> }
    ]
  },

  // ==============================
  // 5. PARTNER PORTAL (Chủ trọ đối tác) - MỚI
  // ==============================
  {
    path: '/partner',
    element: (
      <ProtectedRoute allowedRoles={['PARTNER']}>
        <MainLayout>
          <PartnerLayout />
        </MainLayout>
      </ProtectedRoute>
    ),
    children: [
      { path: 'dashboard', element: <PartnerDashboard /> },
      { path: 'my-listings', element: <MyListings /> },
      { path: 'create-listing', element: <CreateListing /> },
      { path: 'edit-listing/:id', element: <EditListing /> },
      { path: 'packages', element: <ServicePackages /> },
      { path: '', element: <Navigate to="dashboard" replace /> }
    ]
  },

  // ==============================
  // 6. FALLBACK
  // ==============================
  { path: '*', element: <Navigate to='/' replace /> }
  ,
  // ==============================
  // 7. STAFF PORTAL (Internal)
  // ==============================
  {
    path: '/staff',
    element: (
      <ProtectedRoute allowedRoles={['ADMIN','MANAGER','RECEPTIONIST','ACCOUNTANT','MAINTENANCE','SECURITY']}> 
        <MainLayout>
          <div className="min-h-[60vh]">
            <Outlet />
          </div>
        </MainLayout>
      </ProtectedRoute>
    ),
    children: [
      { path: 'finance/invoices', element: <InvoiceManagementPage /> },
      { path: 'finance/reports', element: <FinancialReportsPage /> },
      { path: 'maintenance/board', element: <MaintenanceBoardPage /> },
      { path: 'rooms', element: <RoomManagement /> },
      { path: 'bookings', element: <BookingManagement /> },
      { path: 'contracts/create', element: <ContractCreation /> },
      { path: 'inspection', element: <Inspection /> },
      { path: 'posts/moderation', element: <PostModeration /> },
      { path: '', element: <Navigate to='rooms' replace /> }
    ]
  }
  ,
  // ==============================
  // 8. ADMIN & DIRECTOR PORTAL
  // ==============================
  {
    path: '/admin',
    element: (
      <ProtectedRoute allowedRoles={['ADMIN','MANAGER']}>
        <MainLayout>
          <div className="min-h-[60vh]">
            <Outlet />
          </div>
        </MainLayout>
      </ProtectedRoute>
    ),
    children: [
      { path: 'dashboard', element: <DirectorDashboardPage /> },
      { path: 'users', element: <UserEmployeeManagementPage /> },
      { path: 'config', element: <SystemConfigurationPage /> },
      { path: 'audit-logs', element: <AuditLogsPage /> },
      { path: '', element: <Navigate to='dashboard' replace /> }
    ]
  }
];

export default appRoutes;