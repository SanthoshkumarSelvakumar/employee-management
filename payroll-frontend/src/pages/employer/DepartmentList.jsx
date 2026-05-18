import { useEffect, useState } from 'react';
import {
  Table, Card, Button, Space, Input, Typography, Modal, Form, message, Popconfirm,
} from 'antd';
import { PlusOutlined, BankOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import api from '../../api/axiosInstance';

const { Title, Text } = Typography;

function DepartmentList() {
  const [departments, setDepartments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingDept, setEditingDept] = useState(null);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm();

  useEffect(() => {
    loadDepartments();
  }, []);

  const loadDepartments = async () => {
    try {
      setLoading(true);
      const response = await api.get('/departments');
      setDepartments(response.data);
    } catch (error) {
      message.error('Failed to load departments');
    } finally {
      setLoading(false);
    }
  };

  const openModal = (dept = null) => {
    setEditingDept(dept);
    if (dept) {
      form.setFieldsValue({ name: dept.name, description: dept.description });
    } else {
      form.resetFields();
    }
    setModalVisible(true);
  };

  const handleSave = async (values) => {
    try {
      setSaving(true);
      if (editingDept) {
        await api.put(`/departments/${editingDept.id}`, values);
        message.success('Department updated');
      } else {
        await api.post('/departments', values);
        message.success('Department created');
      }
      setModalVisible(false);
      loadDepartments();
    } catch (error) {
      const msg = error.response?.data?.message || 'Operation failed';
      message.error(msg);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    try {
      await api.delete(`/departments/${id}`);
      message.success('Department deleted');
      loadDepartments();
    } catch (error) {
      const msg = error.response?.data?.message || 'Failed to delete department';
      message.error(msg);
    }
  };

  const columns = [
    {
      title: 'Name',
      dataIndex: 'name',
    },
    {
      title: 'Description',
      dataIndex: 'description',
      render: (val) => val || '-',
    },
    {
      title: 'Employees',
      dataIndex: 'employeeCount',
      align: 'center',
    },
    {
      title: 'Actions',
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => openModal(record)}
          >
            Edit
          </Button>
          <Popconfirm
            title="Delete this department?"
            description="This cannot be undone."
            onConfirm={() => handleDelete(record.id)}
            okText="Delete"
            cancelText="Cancel"
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              Delete
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div className="page-header">
        <Title level={3}><BankOutlined /> Departments</Title>
        <Text type="secondary">Manage company departments</Text>
      </div>

      <Card>
        <Space style={{ marginBottom: 16, width: '100%', justifyContent: 'flex-end' }}>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => openModal()}>
            Add Department
          </Button>
        </Space>

        <Table
          dataSource={departments}
          columns={columns}
          rowKey="id"
          loading={loading}
          pagination={false}
          locale={{ emptyText: 'No departments created yet' }}
        />
      </Card>

      <Modal
        title={editingDept ? 'Edit Department' : 'Create Department'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
      >
        <Form form={form} layout="vertical" onFinish={handleSave}>
          <Form.Item
            name="name"
            label="Department Name"
            rules={[{ required: true, message: 'Department name is required' }]}
          >
            <Input placeholder="e.g. Engineering" />
          </Form.Item>
          <Form.Item name="description" label="Description">
            <Input.TextArea rows={3} placeholder="Optional description" />
          </Form.Item>
          <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
            <Space>
              <Button onClick={() => setModalVisible(false)}>Cancel</Button>
              <Button type="primary" htmlType="submit" loading={saving}>
                {editingDept ? 'Update' : 'Create'}
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

export default DepartmentList;
