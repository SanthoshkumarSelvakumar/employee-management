import { useEffect, useState } from 'react';
import { Table, Card, Button, Tag, Select, Typography, Space, message } from 'antd';
import { DownloadOutlined, FileTextOutlined } from '@ant-design/icons';
import api from '../../api/axiosInstance';

const { Title, Text } = Typography;
const { Option } = Select;

function MyPayslips() {
  const [payslips, setPayslips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [year, setYear] = useState(new Date().getFullYear());
  const [downloading, setDownloading] = useState(null);

  useEffect(() => {
    loadPayslips();
  }, [year]);

  const loadPayslips = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/payslips/year/${year}`);
      setPayslips(response.data);
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
      message.success('Payslip downloaded successfully');
    } catch (error) {
      message.error('Failed to download payslip');
    } finally {
      setDownloading(null);
    }
  };

  const months = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December',
  ];

  const columns = [
    {
      title: 'Month',
      dataIndex: 'payPeriodMonth',
      render: (month) => months[month - 1],
    },
    {
      title: 'Basic Salary',
      dataIndex: 'basicSalary',
      render: (val) => `₹ ${Number(val).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`,
    },
    {
      title: 'Total Earnings',
      dataIndex: 'totalEarnings',
      render: (val) => (
        <Text type="success">₹ {Number(val).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</Text>
      ),
    },
    {
      title: 'Total Deductions',
      dataIndex: 'totalDeductions',
      render: (val) => (
        <Text type="danger">₹ {Number(val).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</Text>
      ),
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
          type="primary"
          icon={<DownloadOutlined />}
          size="small"
          loading={downloading === record.id}
          onClick={() => handleDownload(record.id)}
        >
          Download
        </Button>
      ),
    },
  ];

  const currentYear = new Date().getFullYear();
  const years = Array.from({ length: 5 }, (_, i) => currentYear - i);

  return (
    <div>
      <div className="page-header">
        <Title level={3}><FileTextOutlined /> My Payslips</Title>
        <Text type="secondary">View and download your monthly payslips</Text>
      </div>

      <Card>
        <Space style={{ marginBottom: 16 }}>
          <Text>Year:</Text>
          <Select value={year} onChange={setYear} style={{ width: 120 }}>
            {years.map((y) => (
              <Option key={y} value={y}>{y}</Option>
            ))}
          </Select>
        </Space>

        <Table
          dataSource={payslips}
          columns={columns}
          rowKey="id"
          loading={loading}
          pagination={false}
          locale={{ emptyText: 'No payslips available for this year' }}
        />
      </Card>
    </div>
  );
}

export default MyPayslips;
