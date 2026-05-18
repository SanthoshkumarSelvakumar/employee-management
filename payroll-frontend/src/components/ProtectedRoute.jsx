import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { Result, Button } from 'antd';

function ProtectedRoute({ children, roles }) {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (roles && !roles.includes(user.role)) {
    return (
      <Result
        status="403"
        title="403"
        subTitle="You do not have permission to access this page."
        extra={<Button type="primary" href="/">Go to Dashboard</Button>}
      />
    );
  }

  return children;
}

export default ProtectedRoute;
