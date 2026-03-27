import { BrowserRouter, Routes, Route } from 'react-router-dom';
import UserLayout from './components/layouts/UserLayout';
import RegisterPage from './components/pages/RegisterPage';
import LoginPage from './components/pages/LoginPage';
import { AuthProvider } from './components/AuthContext';
import HomePage from './components/pages/HomePage';
import BookingPage from './components/pages/BookingPage';
import BookingDetailPage from './components/pages/BookingDetailPage';
import BookingListPage from './components/pages/BookingListPage';
import VoucherPage from './components/pages/VoucherPage';
import LatestBookingDetailPage from './components/pages/LatestBookingDetailPage';
import MomoReturnPage from './components/pages/MomoReturnPage';
import VnpayReturnPage from './components/pages/VnpayReturnPage';
import ProtectedRoute from './components/ProtectedRoute';
import AdminProtectedRoute from './components/AdminProtectedRoute';
import AdminLayout from './components/layouts/AdminLayout';
import AdminDashboard from './components/pages/admin/AdminDashboard';
import PetTypeManagement from './components/pages/admin/PetTypeManagement';
import StaffManagement from './components/pages/admin/StaffManagement';
import ServiceManagement from './components/pages/admin/ServiceManagement';
import BookingManagement from './components/pages/admin/BookingManagement';
import RevenueManagement from './components/pages/admin/RevenueManagement';
import UserManagement from './components/pages/admin/UserManagement';
import ArticleManagement from './components/pages/admin/ArticleManagement';
import ArticleList from './components/pages/ArticleList'; 
import ArticleDetail from './components/pages/ArticleDetail';
import StatisticManagement from './components/pages/admin/StatisticManagement';
function About() {
    return <h1>About Page</h1>;
}

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    {/* Public Routes */}
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/login" element={<LoginPage />} />

                    {/* User Routes with Header & Footer */}
                    <Route path="/" element={<UserLayout><HomePage /></UserLayout>} />
                    <Route path="/about" element={<UserLayout><About /></UserLayout>} />
                    <Route path="/booking" element={
                        <UserLayout>
                            <ProtectedRoute>
                                <BookingPage />
                            </ProtectedRoute>
                        </UserLayout>
                    } />
                    <Route path="/articles" element={<UserLayout><ArticleList /></UserLayout>} />
                    <Route path="/articles/:id" element={<UserLayout><ArticleDetail /></UserLayout>} />
                    <Route path="/booking/details/:bookingId" element={
                        <UserLayout>
                            <ProtectedRoute>
                                <BookingDetailPage />
                            </ProtectedRoute>
                        </UserLayout>
                    } />
                    <Route path="/bookings" element={
                        <UserLayout>
                            <ProtectedRoute>
                                <BookingListPage />
                            </ProtectedRoute>
                        </UserLayout>
                    } />
                    <Route path="/booking/details/latest" element={
                        <UserLayout>
                            <ProtectedRoute>
                                <LatestBookingDetailPage />
                            </ProtectedRoute>
                        </UserLayout>
                    } />
                    <Route path="/vouchers" element={
                        <UserLayout>
                            <ProtectedRoute>
                                <VoucherPage />
                            </ProtectedRoute>
                        </UserLayout>
                    } />
                    <Route path="/payment/momo/return" element={<UserLayout><MomoReturnPage /></UserLayout>} />
                    <Route path="/payment/vnpay/return" element={<UserLayout><VnpayReturnPage /></UserLayout>} />

                    {/* Admin Routes - NO Header/Footer */}
                    <Route path="/admin" element={
                        <AdminProtectedRoute>
                            <AdminLayout />
                        </AdminProtectedRoute>
                    }>
                        <Route index element={<AdminDashboard />} />
                        <Route path="bookings" element={<BookingManagement />} />
                        <Route path="users" element={<UserManagement />} />
                        <Route path="services" element={<ServiceManagement/>} />
                        <Route path="pet-types" element={<PetTypeManagement />} />
                        <Route path="staff" element={<StaffManagement />} />
                        <Route path="revenue" element={<RevenueManagement />} />
                        <Route path="articles" element={<ArticleManagement />} />
                        <Route path="reports" element={<StatisticManagement />} />
                    </Route>
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    )
}

export default App;