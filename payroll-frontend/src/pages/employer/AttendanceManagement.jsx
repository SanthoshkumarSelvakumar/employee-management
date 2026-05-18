import { useEffect, useState } from 'react';
import { Card, Form, Select, DatePicker, TimePicker, Button, Typography, message, Table, Space, Tag } from 'antd';
import { ClockCircleOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import api from '../../api/axiosInstance';

const { Title, Text } = Typography;
const { Option } = Select;

function AttendanceManagement() {
  const [form] = Form.useForm();
  const [employees, setEmployees] = useState([]);
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [weekOf, setWeekOf] = useState(dayjs());

  useEffect(() => {
    loadEmployees();
  }, []);

  useEffect(() => {
    if (selectedEmployee) {
      loadAttendance();
    }
  }, [selectedEmployee, weekOf]);

  const loadEmployees = async () => {
    try {
      const response = await api.get('/employees', { params: { size: 100 } });
      setEmployees(response.data.content || []);
    } catch (error) {
      message.error('Failed to load employees');
    }
  };

  const loadAttendance = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/attendance/employee/${selectedEmployee}`, {
        params: { weekOf: weekOf.format('YYYY-MM-DD') },
      });
      setRecords(response.data);
    } catch (error) {
      setRecords([]);
    } finally {
      setLoading(false);
    }
  };

  const onFinish = async (values) => {
    try {
      setSubmitting(true);
      const payload = {
        employeeId: values.employeeId,
        date: values.date.format('YYYY-MM-DD'),
        checkIn: values.checkIn.format('HH:mm:ss'),
        checkOut: values.checkOut ? values.checkOut.format('HH:mm:ss') : null,
      };
      await api.post('/attendance', payload);
      message.success('Attendance recorded successfully');
      form.resetFields(['date', 'checkIn', 'checkOut']);
      if (selectedEmployee === values.employeeId) {
        loadAttendance();
      }
    } catch (error) {
      const msg = error.response?.data?.message || 'Failed to record attendance';
      message.error(msg);
    } finally {
      setSubmitting(false);
    }
  };

  const columns = [
    {
      title: 'Date',
      dataIndex: 'date',
      render: (date) => dayjs(date).format('ddd, MMM D'),
    },
    {
      title: 'Check In',
      dataIndex: 'checkIn',
      render: (time) => time || '--',
    },
    {
      title: 'Check Out',
      dataIndex: 'checkOut',
      render: (time) => time || '--',
    },
    {
      title: 'Hours Worked',
      dataIndex: 'hoursWorked',
      render: (hours) => hours ? <Tag color="blue">{hours}</Tag> : '--',
    },
  ];

  return (
    <div>
      <div className="page-header">
        <Title level={3}><ClockCircleOutlined /> Attendance Management</Title>
        <Text type="secondary">Record employee attendance</Text>
      </div>

      <Card title="Mark Attendance" style={{ marginBottom: 24 }}>
        <Form form={form} layout="vertical" onFinish={onFinish} style={{ maxWidth: 500 }}>
          <Form.Item
            name="employeeId"
            label="Employee"
            rules={[{ required: true, message: 'Select an employee' }]}
          >
            <Select
              placeholder="Select employee"
              showSearch
              optionFilterProp="children"
              onChange={(val) => setSelectedEmployee(val)}
            >
              {employees.map((emp) => (
                <Option key={emp.id} value={emp.id}>
                  {emp.firstName} {emp.lastName} ({emp.employeeCode})
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="date"
            label="Date"
            rules={[{ required: true, message: 'Select date' }]}
          >
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>

          <Space style={{ width: '100%' }} size="middle">
            <Form.Item
              name="checkIn"
              label="Check In"
              rules={[{ required: true, message: 'Check-in time is required' }]}
              style={{ flex: 1 }}
            >
              <TimePicker format="HH:mm" style={{ width: '100%' }} />
            </Form.Item>

            <Form.Item
              name="checkOut"
              label="Check Out"
              style={{ flex: 1 }}
            >
              <TimePicker format="HH:mm" style={{ width: '100%' }} />
            </Form.Item>
          </Space>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={submitting}>
              Save Attendance
            </Button>
          </Form.Item>
        </Form>
      </Card>

      {selectedEmployee && (
        <Card title="Weekly View">
          <div style={{ display: 'flex', alignItems: 'center', gap: 16, marginBottom: 16 }}>
            <Button onClick={() => setWeekOf(weekOf.subtract(7, 'day'))}>← Prev Week</Button>
            <DatePicker value={weekOf} onChange={(d) => d && setWeekOf(d)} picker="week" />
            <Button onClick={() => setWeekOf(weekOf.add(7, 'day'))}>Next Week →</Button>
          </div>
          <Table
            columns={columns}
            dataSource={records}
            rowKey="id"
            pagination={false}
            loading={loading}
            locale={{ emptyText: 'No records for this week' }}
          />
        </Card>
      )}
    </div>
  );
}

export default AttendanceManagement;
