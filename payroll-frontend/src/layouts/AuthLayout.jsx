import { Outlet } from 'react-router-dom';
import '../styles/landing.css';

function AuthLayout() {
  return (
    <div className="landing-page">
      <nav className="landing-nav">
        <div className="logo">CORE_STRATEGY</div>
        <div className="nav-links">
          <a href="#about">About</a>
          <a href="#contact">Contact</a>
          <a href="#solutions">Solutions</a>
        </div>
      </nav>

      <section className="hero">
        <div className="hero-content">
          <h1>Scalable Infrastructure for Modern Enterprise.</h1>
          <p>Access your dashboard to manage secure deployments, review analytics, and streamline operational workflows.</p>
        </div>
        <Outlet />
      </section>

      <section id="about" className="about-section">
        <h2 className="section-title">Operational Excellence</h2>
        <div className="about-grid">
          <div className="card">
            <h3>Our Methodology</h3>
            <p>We leverage industry-standard frameworks to ensure data integrity and system resilience across all digital touchpoints.</p>
          </div>
          <div className="card">
            <h3>Our Vision</h3>
            <p>To provide organizations with the technical clarity required to navigate complex market environments with confidence.</p>
          </div>
        </div>
      </section>

      <footer id="contact" className="landing-footer">
        <div className="footer-content">
          <div className="contact-info">
            <h3>Contact Details</h3>
            <p>Email: inquiries@corestrategy.local</p>
            <p>Phone: +1 (555) 012-3456</p>
            <p>Address: 100 Innovation Way, Suite 500, Tech District</p>
          </div>
          <div>
            <p>&copy; 2026 Core Strategy Group. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default AuthLayout;
