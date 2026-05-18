import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './hooks/useAuth';
import MainLayout from './layouts/MainLayout';
import AuthLayout from './layouts/AuthLayout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import MyPayslips from './pages/employee/MyPayslips';
import MyProfile from './pages/employee/MyProfile';
import MySalary from './pages/employee/MySalary';
import MyAttendance from './pages/employee/MyAttendance';
import EmployeeList from './pages/employer/EmployeeList';
import EmployeeForm from './pages/employer/EmployeeForm';
import SalaryManagement from './pages/employer/SalaryManagement';
import DepartmentList from './pages/employer/DepartmentList';
import PayslipOverview from './pages/employer/PayslipOverview';
import AttendanceManagement from './pages/employer/AttendanceManagement';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  const { user } = useAuth();

  if (!user) {
    return (
      <Routes>
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<Login />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Route>
      </Routes>
    );
  }

  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/" element={<Dashboard />} />

        {/* Employee Routes */}
        <Route path="/my-payslips" element={
          <ProtectedRoute roles={['ROLE_EMPLOYEE']}>
            <MyPayslips />
          </ProtectedRoute>
        } />
        <Route path="/my-salary" element={
          <ProtectedRoute roles={['ROLE_EMPLOYEE']}>
            <MySalary />
          </ProtectedRoute>
        } />
        <Route path="/my-attendance" element={
          <ProtectedRoute roles={['ROLE_EMPLOYEE']}>
            <MyAttendance />
          </ProtectedRoute>
        } />
        <Route path="/my-profile" element={<MyProfile />} />

        {/* Employer Routes */}
        <Route path="/employees" element={
          <ProtectedRoute roles={['ROLE_EMPLOYER']}>
            <EmployeeList />
          </ProtectedRoute>
        } />
        <Route path="/employees/new" element={
          <ProtectedRoute roles={['ROLE_EMPLOYER']}>
            <EmployeeForm />
          </ProtectedRoute>
        } />
        <Route path="/employees/:id/salary" element={
          <ProtectedRoute roles={['ROLE_EMPLOYER']}>
            <SalaryManagement />
          </ProtectedRoute>
        } />
        <Route path="/departments" element={
          <ProtectedRoute roles={['ROLE_EMPLOYER']}>
            <DepartmentList />
          </ProtectedRoute>
        } />
        <Route path="/payslips" element={
          <ProtectedRoute roles={['ROLE_EMPLOYER']}>
            <PayslipOverview />
          </ProtectedRoute>
        } />
        <Route path="/attendance" element={
          <ProtectedRoute roles={['ROLE_EMPLOYER']}>
            <AttendanceManagement />
          </ProtectedRoute>
        } />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}

export default App;
