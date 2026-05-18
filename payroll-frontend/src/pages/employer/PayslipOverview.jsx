import { useEffect, useState } from 'react';
import { Table, Card, Typography, Tag, Button, Space, Select, message } from 'antd';
import { FileTextOutlined, DownloadOutlined } from '@ant-design/icons';
import api from '../../api/axiosInstance';

const { Title, Text } = Typography;
const { Option } = Select;

function PayslipOverview() {
  const [payslips, setPayslips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [downloading, setDownloading] = useState(null);

  useEffect(() => {
    loadPayslips();
  }, [pagination.current]);

  const loadPayslips = async () => {
    try {
      setLoading(true);
      const response = await api.get('/payslips', {
        params: { page: pagination.current - 1, size: pagination.pageSize },
      });
      setPayslips(response.data.content || []);
      setPagination((prev) => ({ ...prev, total: response.data.totalElements || 0 }));
    } catch (error) {
      message.error('Failed to load payslips');
    } finally {
      setLoading(false);
    }
  };

  const handleDownload = async (payslipId) => {
    try {
      setDownloading(payslipId);
      const response = await api.get(`/payslips/${payslipId}/download`, {
        responseType: 'blob',
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `payslip_${payslipId}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      message.error('Failed to download payslip');
    } finally {
      setDownloading(null);
    }
  };

  const handleGeneratePayslips = async () => {
    const now = new Date();
    const month = now.getMonth() + 1;
    const year = now.getFullYear();
    try {
      const response = await api.post(`/payslips/generate?month=${month}&year=${year}`);
      const { generated, skipped, total } = response.data;
      if (generated > 0) {
        message.success(`Generated ${generated} payslip(s) for ${month}/${year}`);
      } else if (total === 0) {
        message.warning('No active employees found.');
      } else {
        message.warning(`No payslips generated. ${skipped} employee(s) skipped — ensure salary structures are assigned.`);
      }
      loadPayslips();
    } catch (error) {
      message.error('Failed to generate payslips');
    }
  };

  const months = [
    'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
    'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec',
  ];

  const columns = [
    {
      title: 'Employee',
      dataIndex: 'employeeName',
    },
    {
      title: 'Code',
      dataIndex: 'employeeCode',
      width: 120,
    },
    {
      title: 'Period',
      render: (_, record) => `${months[record.payPeriodMonth - 1]} ${record.payPeriodYear}`,
    },
    {
      title: 'Department',
      dataIndex: 'department',
      render: (val) => val || '-',
    },
    {
      title: 'Net Pay',
      dataIndex: 'netPay',
      render: (val) => (
        <Text strong>₹ {Number(val).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</Text>
      ),
    },
    {
      title: 'Status',
      dataIndex: 'status',
      render: (status) => (
        <Tag color={status === 'GENERATED' ? 'green' : 'red'}>{status}</Tag>
      ),
    },
    {
      title: 'Action',
      render: (_, record) => (
        <Button
          type="link"
          icon={<DownloadOutlined />}
          loading={downloading === record.id}
          onClick={() => handleDownload(record.id)}
        >
          PDF
        </Button>
      ),
    },
  ];

  return (
    <div>
      <div className="page-header">
        <Title level={3}><FileTextOutlined /> Payslip Overview</Title>
        <Text type="secondary">View and manage all generated payslips</Text>
      </div>

      <Card>
        <Space style={{ marginBottom: 16, width: '100%', justifyContent: 'flex-end' }}>
          <Button type="primary" onClick={handleGeneratePayslips}>
            Generate This Month's Payslips
          </Button>
        </Space>

        <Table
          dataSource={payslips}
          columns={columns}
          rowKey="id"
          loading={loading}
          pagination={{
            current: pagination.current,
            pageSize: pagination.pageSize,
            total: pagination.total,
            showSizeChanger: false,
            onChange: (page) => setPagination((prev) => ({ ...prev, current: page })),
          }}
        />
      </Card>
    </div>
  );
}

export default PayslipOverview;
