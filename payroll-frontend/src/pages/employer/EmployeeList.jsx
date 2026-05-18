import { useEffect, useState } from 'react';
import { Table, Card, Button, Space, Input, Typography, Tag, Popconfirm, message } from 'antd';
import { PlusOutlined, SearchOutlined, TeamOutlined, DollarOutlined, DeleteOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosInstance';

const { Title, Text } = Typography;

function EmployeeList() {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const navigate = useNavigate();

  useEffect(() => {
    loadEmployees();
  }, [pagination.current, search]);

  const loadEmployees = async () => {
    try {
      setLoading(true);
      const params = {
        page: pagination.current - 1,
        size: pagination.pageSize,
      };
      if (search) params.search = search;

      const response = await api.get('/employees', { params });
      setEmployees(response.data.content || []);
      setPagination((prev) => ({ ...prev, total: response.data.totalElements || 0 }));
    } catch (error) {
      message.error('Failed to load employees');
    } finally {
      setLoading(false);
    }
  };

  const handleDeactivate = async (id) => {
    try {
      await api.delete(`/employees/${id}`);
      message.success('Employee deactivated');
      loadEmployees();
    } catch (error) {
      message.error('Failed to deactivate employee');
    }
  };

  const columns = [
    {
      title: 'Code',
      dataIndex: 'employeeCode',
      width: 120,
    },
    {
      title: 'Name',
      render: (_, record) => `${record.firstName} ${record.lastName}`,
    },
    {
      title: 'Email',
      dataIndex: 'email',
    },
    {
      title: 'Department',
      dataIndex: 'departmentName',
      render: (val) => val || 'Unassigned',
    },
    {
      title: 'Designation',
      dataIndex: 'designation',
      render: (val) => val || '-',
    },
    {
      title: 'Status',
      dataIndex: 'status',
      render: (status) => (
        <Tag color={status === 'ACTIVE' ? 'green' : 'red'}>{status}</Tag>
      ),
    },
    {
      title: 'Actions',
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            icon={<DollarOutlined />}
            onClick={() => navigate(`/employees/${record.id}/salary`)}
          >
            Salary
          </Button>
          <Popconfirm
            title="Deactivate this employee?"
            onConfirm={() => handleDeactivate(record.id)}
            okText="Yes"
            cancelText="No"
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              Deactivate
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div className="page-header">
        <Title level={3}><TeamOutlined /> Employees</Title>
        <Text type="secondary">Manage your employees</Text>
      </div>

      <Card>
        <Space style={{ marginBottom: 16, width: '100%', justifyContent: 'space-between' }} wrap>
          <Input
            placeholder="Search by name or code..."
            prefix={<SearchOutlined />}
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setPagination((prev) => ({ ...prev, current: 1 }));
            }}
            style={{ width: 300 }}
            allowClear
          />
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => navigate('/employees/new')}
          >
            Add Employee
          </Button>
        </Space>

        <Table
          dataSource={employees}
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

export default EmployeeList;
