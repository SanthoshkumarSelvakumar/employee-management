import { useEffect, useState } from 'react';
import { Row, Col, Card, Statistic, Typography, Table, Tag, Spin } from 'antd';
import {
  TeamOutlined,
  BankOutlined,
  FileTextOutlined,
  DollarOutlined,
} from '@ant-design/icons';
import { useAuth } from '../hooks/useAuth';
import api from '../api/axiosInstance';

const { Title, Text } = Typography;

function Dashboard() {
  const { user } = useAuth();
  const isEmployer = user?.role === 'ROLE_EMPLOYER';
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({});
  const [recentPayslips, setRecentPayslips] = useState([]);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      if (isEmployer) {
        const [statsRes, payslipsRes] = await Promise.all([
          api.get('/profile/dashboard'),
          api.get('/payslips', { params: { size: 5 } }),
        ]);
        setStats(statsRes.data);
        setRecentPayslips(payslipsRes.data.content || []);
      } else {
        const [profileRes, payslipsRes] = await Promise.all([
          api.get('/profile'),
          api.get('/payslips', { params: { size: 5 } }),
        ]);
        setStats({ profile: profileRes.data });
        setRecentPayslips(payslipsRes.data.content || []);
      }
    } catch (error) {
      console.error('Failed to load dashboard data', error);
    } finally {
      setLoading(false);
    }
  };

  const payslipColumns = [
    {
      title: 'Period',
      render: (_, record) => `${record.payPeriodMonth}/${record.payPeriodYear}`,
    },
    ...(isEmployer ? [{
      title: 'Employee',
      dataIndex: 'employeeName',
    }] : []),
    {
      title: 'Net Pay',
      dataIndex: 'netPay',
      render: (val) => `₹ ${Number(val).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      render: (status) => (
        <Tag color={status === 'GENERATED' ? 'green' : 'red'}>{status}</Tag>
      ),
    },
  ];

  if (loading) {
    return <Spin size="large" style={{ display: 'block', margin: '100px auto' }} />;
  }

  return (
    <div>
      <div className="page-header">
        <Title level={3}>
          {isEmployer ? 'Employer Dashboard' : `Welcome, ${stats.profile?.firstName || 'Employee'}`}
        </Title>
        <Text type="secondary">
          {isEmployer ? 'Overview of your payroll system' : 'Your payroll summary'}
        </Text>
      </div>

      {isEmployer && (
        <Row gutter={[16, 16]} className="stats-row">
          <Col xs={24} sm={12} md={6}>
            <Card>
              <Statistic
                title="Total Employees"
                value={stats.totalEmployees || 0}
                prefix={<TeamOutlined />}
                valueStyle={{ color: '#1677ff' }}
              />
            </Card>
          </Col>
        </Row>
      )}

      <Card title="Recent Payslips" style={{ marginTop: 16 }}>
        <Table
          dataSource={recentPayslips}
          columns={payslipColumns}
          rowKey="id"
          pagination={false}
          size="middle"
          locale={{ emptyText: 'No payslips generated yet' }}
        />
      </Card>
    </div>
  );
}

export default Dashboard;
