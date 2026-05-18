import { useEffect, useState } from 'react';
import { Card, Descriptions, Typography, Spin, Tag } from 'antd';
import { DollarOutlined } from '@ant-design/icons';
import api from '../../api/axiosInstance';

const { Title, Text } = Typography;

function MySalary() {
  const [salary, setSalary] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadSalary();
  }, []);

  const loadSalary = async () => {
    try {
      const response = await api.get('/profile/salary');
      setSalary(response.data);
    } catch (error) {
      setSalary(null);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <Spin size="large" style={{ display: 'block', margin: '100px auto' }} />;
  }

  if (!salary) {
    return (
      <div>
        <div className="page-header">
          <Title level={3}><DollarOutlined /> My Salary</Title>
        </div>
        <Card>
          <Text type="secondary">No salary structure assigned yet. Please contact your employer.</Text>
        </Card>
      </div>
    );
  }

  return (
    <div>
      <div className="page-header">
        <Title level={3}><DollarOutlined /> My Salary</Title>
        <Text type="secondary">Current salary structure</Text>
      </div>

      <Card>
        <Descriptions bordered column={{ xs: 1, sm: 2 }} title="Earnings">
          <Descriptions.Item label="Basic Salary">₹{salary.basicSalary?.toLocaleString()}</Descriptions.Item>
          <Descriptions.Item label="HRA">₹{salary.hra?.toLocaleString()}</Descriptions.Item>
          <Descriptions.Item label="Allowances">₹{salary.allowances?.toLocaleString()}</Descriptions.Item>
          <Descriptions.Item label="Total Earnings">
            <Tag color="green">₹{salary.totalEarnings?.toLocaleString()}</Tag>
          </Descriptions.Item>
        </Descriptions>

        <Descriptions bordered column={{ xs: 1, sm: 2 }} title="Deductions" style={{ marginTop: 24 }}>
          <Descriptions.Item label="PF Deduction">₹{salary.pfDeduction?.toLocaleString()}</Descriptions.Item>
          <Descriptions.Item label="Tax Deduction">₹{salary.taxDeduction?.toLocaleString()}</Descriptions.Item>
          <Descriptions.Item label="Insurance">₹{salary.insuranceDeduction?.toLocaleString()}</Descriptions.Item>
          <Descriptions.Item label="Total Deductions">
            <Tag color="red">₹{salary.totalDeductions?.toLocaleString()}</Tag>
          </Descriptions.Item>
        </Descriptions>

        <Descriptions bordered column={1} style={{ marginTop: 24 }}>
          <Descriptions.Item label="Net Pay">
            <Title level={4} style={{ margin: 0, color: '#1677ff' }}>₹{salary.netPay?.toLocaleString()}</Title>
          </Descriptions.Item>
          <Descriptions.Item label="Effective From">{salary.effectiveFrom}</Descriptions.Item>
        </Descriptions>
      </Card>
    </div>
  );
}

export default MySalary;
