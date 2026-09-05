import { useLocation } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

const pageNames = {
  '/admin': 'Dashboard',
  '/admin/products': 'Products',
  '/admin/categories': 'Categories',
  '/admin/orders': 'Orders',
  '/admin/payments': 'Payments',
  '/admin/users': 'Users',
}

function AdminHeader({ onMenuClick }) {
  const location = useLocation()
  const { user, logout } = useAuth()

  const pageTitle =
    pageNames[location.pathname] ||
    (location.pathname.includes('/orders/') ? 'Order Details' :
      location.pathname.includes('/payments/') ? 'Payment Details' :
        'Admin')

  const initial =
    user?.username?.trim()?.charAt(0)?.toUpperCase() || 'A'

  return (
    <header className="admin-header">
      <div className="admin-header-left">
        <button
          type="button"
          className="admin-mobile-menu"
          aria-label="Open navigation"
          onClick={onMenuClick}
        >
          <span />
          <span />
          <span />
        </button>

        <div className="admin-header-page">
          <div className="admin-breadcrumb">
            <span>NovaCart</span>
            <span className="admin-breadcrumb-separator">/</span>
            <strong>{pageTitle}</strong>
          </div>

          <h1>{pageTitle}</h1>
        </div>
      </div>

      <div className="admin-header-right">
        <label className="admin-search">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <circle cx="11" cy="11" r="6.5" />
            <path d="m16 16 4.5 4.5" />
          </svg>

          <input
            type="search"
            placeholder="Search orders, products..."
            aria-label="Search admin panel"
          />

          <kbd>⌘ K</kbd>
        </label>

        <button
          type="button"
          className="admin-icon-button"
          aria-label="Notifications"
        >
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M18 9a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9" />
            <path d="M10 21h4" />
          </svg>
          <span className="admin-notification-dot" />
        </button>

        <div className="admin-header-divider" />

        <div className="admin-user-menu">
          <div className="admin-header-avatar">{initial}</div>

          <div className="admin-user-info">
            <strong>{user?.username || 'Administrator'}</strong>
            <span>Administrator</span>
          </div>

          <span className="admin-user-chevron">⌄</span>
        </div>

        <button
          type="button"
          className="admin-logout-button"
          onClick={logout}
        >
          Logout
        </button>
      </div>
    </header>
  )
}

export default AdminHeader
