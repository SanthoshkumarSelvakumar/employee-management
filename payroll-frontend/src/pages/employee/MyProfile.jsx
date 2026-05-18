import { useEffect, useState } from 'react';
import { Card, Descriptions, Typography, Spin, Button, Form, Input, message, Divider } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import { useAuth } from '../../hooks/useAuth';
import api from '../../api/axiosInstance';

const { Title, Text } = Typography;

function MyProfile() {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [changingPassword, setChangingPassword] = useState(false);
  const [form] = Form.useForm();

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      const response = await api.get('/profile');
      setProfile(response.data);
    } catch (error) {
      // Employer won't have an employee profile
      setProfile(null);
    } finally {
      setLoading(false);
    }
  };

  const handlePasswordChange = async (values) => {
    try {
      setChangingPassword(true);
      await api.put('/auth/change-password', values);
      message.success('Password changed successfully');
      form.resetFields();
    } catch (error) {
      const msg = error.response?.data?.message || 'Failed to change password';
      message.error(msg);
    } finally {
      setChangingPassword(false);
    }
  };

  if (loading) {
    return <Spin size="large" style={{ display: 'block', margin: '100px auto' }} />;
  }

  return (
    <div>
      <div className="page-header">
        <Title level={3}><UserOutlined /> My Profile</Title>
        <Text type="secondary">Your personal and employment details</Text>
      </div>

      {profile && (
        <Card style={{ marginBottom: 24 }}>
          <Descriptions bordered column={{ xs: 1, sm: 2 }}>
            <Descriptions.Item label="Employee Code">{profile.employeeCode}</Descriptions.Item>
            <Descriptions.Item label="Email">{profile.email}</Descriptions.Item>
            <Descriptions.Item label="Name">{profile.firstName} {profile.lastName}</Descriptions.Item>
            <Descriptions.Item label="Department">{profile.departmentName || 'N/A'}</Descriptions.Item>
            <Descriptions.Item label="Designation">{profile.designation || 'N/A'}</Descriptions.Item>
            <Descriptions.Item label="Date of Joining">{profile.dateOfJoining}</Descriptions.Item>
            <Descriptions.Item label="Status">
              <span style={{ color: profile.status === 'ACTIVE' ? '#52c41a' : '#ff4d4f' }}>
                {profile.status}
              </span>
            </Descriptions.Item>
          </Descriptions>
        </Card>
      )}

      <Card title="Change Password">
        <Form
          form={form}
          layout="vertical"
          onFinish={handlePasswordChange}
          style={{ maxWidth: 400 }}
        >
          <Form.Item
            name="currentPassword"
            label="Current Password"
            rules={[{ required: true, message: 'Please enter current password' }]}
          >
            <Input.Password />
          </Form.Item>

          <Form.Item
            name="newPassword"
            label="New Password"
            rules={[
              { required: true, message: 'Please enter new password' },
              { min: 8, message: 'Password must be at least 8 characters' },
            ]}
          >
            <Input.Password />
          </Form.Item>

          <Form.Item
            name="confirmPassword"
            label="Confirm Password"
            dependencies={['newPassword']}
            rules={[
              { required: true, message: 'Please confirm your password' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('Passwords do not match'));
                },
              }),
            ]}
          >
            <Input.Password />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={changingPassword}>
              Change Password
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}

export default MyProfile;
