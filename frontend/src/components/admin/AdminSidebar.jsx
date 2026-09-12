import { NavLink } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

function Icon({ name }) {
  const common = {
    width: 18,
    height: 18,
    viewBox: '0 0 24 24',
    fill: 'none',
    stroke: 'currentColor',
    strokeWidth: 1.8,
    strokeLinecap: 'round',
    strokeLinejoin: 'round',
    'aria-hidden': true,
  }

  const icons = {
    dashboard: (
      <>
        <rect x="3" y="3" width="7" height="7" rx="1.5" />
        <rect x="14" y="3" width="7" height="7" rx="1.5" />
        <rect x="3" y="14" width="7" height="7" rx="1.5" />
        <rect x="14" y="14" width="7" height="7" rx="1.5" />
      </>
    ),
    products: (
      <>
        <path d="m12 3 8 4.5v9L12 21l-8-4.5v-9L12 3Z" />
        <path d="m4.5 7.8 7.5 4.4 7.5-4.4" />
        <path d="M12 12.2V21" />
      </>
    ),
    categories: (
      <>
        <rect x="4" y="4" width="6" height="6" rx="1" />
        <rect x="14" y="4" width="6" height="6" rx="1" />
        <rect x="4" y="14" width="6" height="6" rx="1" />
        <rect x="14" y="14" width="6" height="6" rx="1" />
      </>
    ),
    orders: (
      <>
        <rect x="4" y="3" width="16" height="18" rx="2" />
        <path d="M8 8h8M8 12h8M8 16h5" />
      </>
    ),
    payments: (
      <>
        <rect x="3" y="5" width="18" height="14" rx="2" />
        <path d="M3 9h18M7 15h3" />
      </>
    ),
    users: (
      <>
        <circle cx="12" cy="8" r="3.5" />
        <path d="M5 20c.8-3.3 3.1-5 7-5s6.2 1.7 7 5" />
      </>
    ),
    logout: (
      <>
        <path d="M10 17l5-5-5-5" />
        <path d="M15 12H3" />
        <path d="M21 5v14" />
      </>
    ),
  }

  return <svg {...common}>{icons[name]}</svg>
}

function AdminSidebar({ isOpen, onClose }) {
  const { logout } = useAuth()

  const navItems = [
    { label: 'Dashboard', path: '/admin', end: true, icon: 'dashboard' },
    { label: 'Products', path: '/admin/products', icon: 'products' },
    { label: 'Categories', path: '/admin/categories', icon: 'categories' },
    { label: 'Orders', path: '/admin/orders', icon: 'orders' },
    { label: 'Payments', path: '/admin/payments', icon: 'payments' },
    { label: 'Users', path: '/admin/users', icon: 'users' },
  ]

  const handleLogout = () => {
    onClose()
    logout()
  }

  return (
    <aside className={`admin-sidebar ${isOpen ? 'open' : ''}`}>
      <div className="admin-sidebar-top">
        <div className="admin-sidebar-brand-row">
          <NavLink
            to="/admin"
            className="admin-sidebar-brand"
            onClick={onClose}
          >
            <span className="admin-brand-symbol">N</span>

            <span className="admin-brand-copy">
              <strong>NovaCart</strong>
              <small>Admin Panel</small>
            </span>
          </NavLink>

          <button
            type="button"
            className="admin-sidebar-close"
            aria-label="Close navigation"
            onClick={onClose}
          >
            ×
          </button>
        </div>

        <div className="admin-workspace-switcher">
          <span className="admin-workspace-icon">
            <Icon name="dashboard" />
          </span>

          <span className="admin-workspace-copy">
            <strong>Main Store</strong>
            <small>Administration</small>
          </span>

          <span className="admin-workspace-chevron">⌄</span>
        </div>
      </div>

      <div className="admin-sidebar-body">
        <div className="admin-sidebar-section">Main</div>

        <nav className="admin-sidebar-nav" aria-label="Admin navigation">
          {navItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              end={item.end}
              onClick={onClose}
              className={({ isActive }) =>
                `admin-nav-link ${isActive ? 'active' : ''}`
              }
            >
              <span className="admin-nav-icon">
                <Icon name={item.icon} />
              </span>

              <span className="admin-nav-label">{item.label}</span>
              <span className="admin-nav-active-indicator" />
            </NavLink>
          ))}
        </nav>
      </div>

      <div className="admin-sidebar-bottom">
        <div className="admin-sidebar-status">
          <span className="admin-sidebar-status-icon">
            <span />
          </span>

          <span className="admin-sidebar-status-copy">
            <strong>System Healthy</strong>
            <small>All services are running</small>
          </span>
        </div>

        <button
          type="button"
          className="admin-sidebar-logout"
          onClick={handleLogout}
        >
          <span className="admin-nav-icon">
            <Icon name="logout" />
          </span>
          <span>Logout</span>
        </button>

        <div className="admin-sidebar-meta">
          <span>NovaCart</span>
          <span>v1.0</span>
        </div>
      </div>
    </aside>
  )
}

export default AdminSidebar
