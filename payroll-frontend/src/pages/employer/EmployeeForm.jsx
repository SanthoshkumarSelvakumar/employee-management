import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Form, Input, Select, DatePicker, Button, Typography, message, Space, Modal } from 'antd';
import { UserAddOutlined } from '@ant-design/icons';
import api from '../../api/axiosInstance';

const { Title, Text } = Typography;
const { Option } = Select;

function EmployeeForm() {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [departments, setDepartments] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    loadDepartments();
  }, []);

  const loadDepartments = async () => {
    try {
      const response = await api.get('/departments');
      setDepartments(response.data);
    } catch (error) {
      message.error('Failed to load departments');
    }
  };

  const onFinish = async (values) => {
    try {
      setLoading(true);
      const payload = {
        ...values,
        dateOfJoining: values.dateOfJoining.format('YYYY-MM-DD'),
      };
      const response = await api.post('/employees', payload);
      const tempPassword = response.data.temporaryPassword;
      Modal.success({
        title: 'Employee Created Successfully',
        content: (
          <div>
            <p>Share these credentials with the employee:</p>
            <p><strong>Email:</strong> {values.email}</p>
            <p><strong>Temporary Password:</strong> <code>{tempPassword}</code></p>
            <p style={{ marginTop: 12, color: '#666' }}>The employee should change their password after first login.</p>
          </div>
        ),
        onOk: () => navigate('/employees'),
      });
    } catch (error) {
      const msg = error.response?.data?.message || 'Failed to create employee';
      message.error(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="page-header">
        <Title level={3}><UserAddOutlined /> Add New Employee</Title>
        <Text type="secondary">Create a new employee account</Text>
      </div>

      <Card style={{ maxWidth: 600 }}>
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
        >
          <Space style={{ width: '100%' }} direction="vertical" size="small">
            <Form.Item
              name="firstName"
              label="First Name"
              rules={[{ required: true, message: 'First name is required' }]}
            >
              <Input placeholder="Enter first name" />
            </Form.Item>

            <Form.Item
              name="lastName"
              label="Last Name"
              rules={[{ required: true, message: 'Last name is required' }]}
            >
              <Input placeholder="Enter last name" />
            </Form.Item>

            <Form.Item
              name="email"
              label="Email"
              rules={[
                { required: true, message: 'Email is required' },
                { type: 'email', message: 'Enter a valid email' },
              ]}
            >
              <Input placeholder="Enter email address" />
            </Form.Item>

            <Form.Item
              name="departmentId"
              label="Department"
              rules={[{ required: true, message: 'Department is required' }]}
            >
              <Select placeholder="Select department">
                {departments.map((dept) => (
                  <Option key={dept.id} value={dept.id}>{dept.name}</Option>
                ))}
              </Select>
            </Form.Item>

            <Form.Item
              name="designation"
              label="Designation"
            >
              <Input placeholder="e.g. Software Engineer" />
            </Form.Item>

            <Form.Item
              name="dateOfJoining"
              label="Date of Joining"
              rules={[{ required: true, message: 'Date of joining is required' }]}
            >
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
          </Space>

          <Form.Item style={{ marginTop: 24 }}>
            <Space>
              <Button type="primary" htmlType="submit" loading={loading}>
                Create Employee
              </Button>
              <Button onClick={() => navigate('/employees')}>
                Cancel
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}

export default EmployeeForm;
