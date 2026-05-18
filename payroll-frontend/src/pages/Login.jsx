import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Form, Input, Button, Typography, message, Space } from 'antd';
import { MailOutlined, LockOutlined, DollarOutlined } from '@ant-design/icons';
import { useAuth } from '../hooks/useAuth';

const { Title, Text } = Typography;

function Login() {
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const onFinish = async (values) => {
    setLoading(true);
    try {
      await login(values.email, values.password);
      message.success('Login successful');
      navigate('/');
    } catch (error) {
      const msg = error.response?.data?.message || 'Invalid credentials';
      message.error(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card
      style={{
        borderRadius: 12,
        boxShadow: '0 20px 60px rgba(0, 0, 0, 0.15)',
      }}
      bodyStyle={{ padding: '40px 32px' }}
    >
      <Space direction="vertical" size="middle" style={{ width: '100%', textAlign: 'center', marginBottom: 32 }}>
        <DollarOutlined style={{ fontSize: 48, color: '#1677ff' }} />
        <Title level={3} style={{ margin: 0 }}>Payroll System</Title>
        <Text type="secondary">Sign in to your account</Text>
      </Space>

      <Form
        name="login"
        onFinish={onFinish}
        layout="vertical"
        size="large"
      >
        <Form.Item
          name="email"
          rules={[
            { required: true, message: 'Please enter your email' },
            { type: 'email', message: 'Please enter a valid email' },
          ]}
        >
          <Input
            prefix={<MailOutlined style={{ color: '#bfbfbf' }} />}
            placeholder="Email address"
          />
        </Form.Item>

        <Form.Item
          name="password"
          rules={[{ required: true, message: 'Please enter your password' }]}
        >
          <Input.Password
            prefix={<LockOutlined style={{ color: '#bfbfbf' }} />}
            placeholder="Password"
          />
        </Form.Item>

        <Form.Item style={{ marginBottom: 0 }}>
          <Button
            type="primary"
            htmlType="submit"
            loading={loading}
            block
            style={{ height: 44, borderRadius: 8 }}
          >
            Sign In
          </Button>
        </Form.Item>
      </Form>
    </Card>
  );
}

export default Login;
