import { Outlet } from 'react-router-dom';
import { Layout, Row, Col } from 'antd';

const { Content } = Layout;

function AuthLayout() {
  return (
    <Layout style={{ minHeight: '100vh', background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
      <Content>
        <Row justify="center" align="middle" style={{ minHeight: '100vh' }}>
          <Col xs={22} sm={16} md={12} lg={8} xl={6}>
            <Outlet />
          </Col>
        </Row>
      </Content>
    </Layout>
  );
}

export default AuthLayout;
