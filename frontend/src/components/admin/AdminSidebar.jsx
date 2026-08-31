import { NavLink } from 'react-router-dom'

function AdminSidebar() {
  const navItems = [
    {
      label: 'Dashboard',
      path: '/admin',
      end: true,
    },
    {
      label: 'Products',
      path: '/admin/products',
    },
    {
      label: 'Categories',
      path: '/admin/categories',
    },
    {
      label: 'Orders',
      path: '/admin/orders',
    },
    {
      label: 'Payments',
      path: '/admin/payments',
    },
    {
      label: 'Users',
      path: '/admin/users',
    },
  ]

  return (
    <aside className="admin-sidebar">
      <div className="admin-sidebar-brand">
        <div className="admin-brand-mark">N</div>

        <div>
          <strong>NovaCart</strong>
          <span>Admin Panel</span>
        </div>
      </div>

      <div className="admin-sidebar-section">
        <span>MAIN MENU</span>
      </div>

      <nav className="admin-sidebar-nav">
        {navItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            end={item.end}
            className={({ isActive }) =>
              isActive
                ? 'admin-nav-link active'
                : 'admin-nav-link'
            }
          >
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="admin-sidebar-footer">
        <span>NovaCart</span>
        <small>Administration</small>
      </div>
    </aside>
  )
}

export default AdminSidebar