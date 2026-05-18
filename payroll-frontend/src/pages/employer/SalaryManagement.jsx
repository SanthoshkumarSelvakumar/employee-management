import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Card, Form, InputNumber, Button, Typography, message, Space,
  Descriptions, Tag, Divider, Spin, Alert,
} from 'antd';
import { DollarOutlined, ArrowLeftOutlined } from '@ant-design/icons';
import api from '../../api/axiosInstance';

const { Title, Text } = Typography;

function SalaryManagement() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [employee, setEmployee] = useState(null);
  const [currentSalary, setCurrentSalary] = useState(null);
  const [salaryHistory, setSalaryHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    loadData();
  }, [id]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [empRes, salaryRes] = await Promise.all([
        api.get(`/employees/${id}`),
        api.get(`/employees/${id}/salary`),
      ]);
      setEmployee(empRes.data);
      setSalaryHistory(salaryRes.data);

      const active = salaryRes.data.find((s) => s.active);
      setCurrentSalary(active || null);

      // Pre-fill form with current salary if exists
      if (active) {
        form.setFieldsValue({
          basicSalary: active.basicSalary,
          hra: active.hra,
          allowances: active.allowances,
          pfDeduction: active.pfDeduction,
          taxDeduction: active.taxDeduction,
          insuranceDeduction: active.insuranceDeduction,
        });
      }
    } catch (error) {
      message.error('Failed to load salary data');
    } finally {
      setLoading(false);
    }
  };

  const onFinish = async (values) => {
    try {
      setSaving(true);
      await api.put(`/employees/${id}/salary`, values);
      message.success('Salary updated for next month');
      loadData();
    } catch (error) {
      const msg = error.response?.data?.message || 'Failed to update salary';
      message.error(msg);
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <Spin size="large" style={{ display: 'block', margin: '100px auto' }} />;
  }

  const pendingSalaries = salaryHistory.filter((s) => !s.active);

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/employees')}>
          Back to Employees
        </Button>
      </Space>

      <div className="page-header">
        <Title level={3}>
          <DollarOutlined /> Salary Management — {employee?.firstName} {employee?.lastName}
        </Title>
        <Text type="secondary">
          Employee Code: {employee?.employeeCode} | Department: {employee?.departmentName}
        </Text>
      </div>

      {currentSalary && (
        <Card title="Current Active Salary" style={{ marginBottom: 24 }}>
          <Descriptions bordered size="small" column={{ xs: 1, sm: 2, md: 3 }}>
            <Descriptions.Item label="Basic Salary">
              ₹ {Number(currentSalary.basicSalary).toLocaleString('en-IN')}
            </Descriptions.Item>
            <Descriptions.Item label="HRA">
              ₹ {Number(currentSalary.hra).toLocaleString('en-IN')}
            </Descriptions.Item>
            <Descriptions.Item label="Allowances">
              ₹ {Number(currentSalary.allowances).toLocaleString('en-IN')}
            </Descriptions.Item>
            <Descriptions.Item label="PF Deduction">
              ₹ {Number(currentSalary.pfDeduction).toLocaleString('en-IN')}
            </Descriptions.Item>
            <Descriptions.Item label="Tax">
              ₹ {Number(currentSalary.taxDeduction).toLocaleString('en-IN')}
            </Descriptions.Item>
            <Descriptions.Item label="Insurance">
              ₹ {Number(currentSalary.insuranceDeduction).toLocaleString('en-IN')}
            </Descriptions.Item>
            <Descriptions.Item label="Net Pay">
              <Text strong style={{ color: '#52c41a', fontSize: 16 }}>
                ₹ {Number(currentSalary.netPay).toLocaleString('en-IN')}
              </Text>
            </Descriptions.Item>
            <Descriptions.Item label="Effective From">
              {currentSalary.effectiveFrom}
            </Descriptions.Item>
          </Descriptions>
        </Card>
      )}

      {pendingSalaries.length > 0 && (
        <Alert
          type="info"
          showIcon
          message="Pending salary revision"
          description={`A salary revision is scheduled to take effect from ${pendingSalaries[0].effectiveFrom}`}
          style={{ marginBottom: 24 }}
        />
      )}

      <Card title="Set Salary for Next Month">
        <Alert
          type="warning"
          showIcon
          message="Important"
          description="Changes made here will only take effect from the 1st of next month. The current month's payslip will remain unchanged."
          style={{ marginBottom: 24 }}
        />

        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          style={{ maxWidth: 600 }}
        >
          <Divider orientation="left">Earnings</Divider>

          <Form.Item
            name="basicSalary"
            label="Basic Salary"
            rules={[{ required: true, message: 'Required' }]}
          >
            <InputNumber
              style={{ width: '100%' }}
              min={0}
              precision={2}
              prefix="₹"
              placeholder="Enter basic salary"
            />
          </Form.Item>

          <Form.Item
            name="hra"
            label="House Rent Allowance (HRA)"
            rules={[{ required: true, message: 'Required' }]}
          >
            <InputNumber style={{ width: '100%' }} min={0} precision={2} prefix="₹" />
          </Form.Item>

          <Form.Item
            name="allowances"
            label="Other Allowances"
            rules={[{ required: true, message: 'Required' }]}
          >
            <InputNumber style={{ width: '100%' }} min={0} precision={2} prefix="₹" />
          </Form.Item>

          <Divider orientation="left">Deductions</Divider>

          <Form.Item
            name="pfDeduction"
            label="Provident Fund (PF)"
            rules={[{ required: true, message: 'Required' }]}
          >
            <InputNumber style={{ width: '100%' }} min={0} precision={2} prefix="₹" />
          </Form.Item>

          <Form.Item
            name="taxDeduction"
            label="Tax Deduction"
            rules={[{ required: true, message: 'Required' }]}
          >
            <InputNumber style={{ width: '100%' }} min={0} precision={2} prefix="₹" />
          </Form.Item>

          <Form.Item
            name="insuranceDeduction"
            label="Insurance"
            rules={[{ required: true, message: 'Required' }]}
          >
            <InputNumber style={{ width: '100%' }} min={0} precision={2} prefix="₹" />
          </Form.Item>

          <Form.Item style={{ marginTop: 24 }}>
            <Button type="primary" htmlType="submit" loading={saving} size="large">
              Save Salary for Next Month
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}

export default SalaryManagement;
