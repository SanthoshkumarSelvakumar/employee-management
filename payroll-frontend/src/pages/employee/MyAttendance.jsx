import { useEffect, useState } from 'react';
import { Card, Table, Typography, DatePicker, Tag, Spin } from 'antd';
import { ClockCircleOutlined, LeftOutlined, RightOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import api from '../../api/axiosInstance';

const { Title, Text } = Typography;

function MyAttendance() {
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [weekOf, setWeekOf] = useState(dayjs());

  useEffect(() => {
    loadAttendance();
  }, [weekOf]);

  const loadAttendance = async () => {
    try {
      setLoading(true);
      const response = await api.get('/attendance/my', {
        params: { weekOf: weekOf.format('YYYY-MM-DD') },
      });
      setRecords(response.data);
    } catch (error) {
      setRecords([]);
    } finally {
      setLoading(false);
    }
  };

  const getWeekRange = () => {
    const start = weekOf.startOf('week').add(1, 'day'); // Monday
    const end = start.add(6, 'day'); // Sunday
    return `${start.format('MMM D')} - ${end.format('MMM D, YYYY')}`;
  };

  const columns = [
    {
      title: 'Day',
      dataIndex: 'date',
      render: (date) => dayjs(date).format('ddd, MMM D'),
    },
    {
      title: 'Check In',
      dataIndex: 'checkIn',
      render: (time) => time || <Tag color="default">--</Tag>,
    },
    {
      title: 'Check Out',
      dataIndex: 'checkOut',
      render: (time) => time || <Tag color="default">--</Tag>,
    },
    {
      title: 'Hours Worked',
      dataIndex: 'hoursWorked',
      render: (hours) => hours ? <Tag color="blue">{hours}</Tag> : <Tag color="default">--</Tag>,
    },
  ];

  return (
    <div>
      <div className="page-header">
        <Title level={3}><ClockCircleOutlined /> My Attendance</Title>
        <Text type="secondary">Weekly attendance view</Text>
      </div>

      <Card>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16, marginBottom: 24 }}>
          <LeftOutlined
            style={{ cursor: 'pointer', fontSize: 18 }}
            onClick={() => setWeekOf(weekOf.subtract(7, 'day'))}
          />
          <DatePicker
            value={weekOf}
            onChange={(date) => date && setWeekOf(date)}
            picker="week"
          />
          <RightOutlined
            style={{ cursor: 'pointer', fontSize: 18 }}
            onClick={() => setWeekOf(weekOf.add(7, 'day'))}
          />
          <Text strong>{getWeekRange()}</Text>
        </div>

        <Table
          columns={columns}
          dataSource={records}
          rowKey="id"
          pagination={false}
          loading={loading}
          locale={{ emptyText: 'No attendance records for this week' }}
        />
      </Card>
    </div>
  );
}

export default MyAttendance;
