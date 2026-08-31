import { useAuth } from '../../context/AuthContext'

function AdminHeader() {
  const { user, logout } = useAuth()

  return (
    <header className="admin-header">
      <div className="admin-header-left">
        <div>
          <p className="admin-header-label">
            Administration
          </p>

          <h1>Dashboard</h1>
        </div>
      </div>

      <div className="admin-header-user">
        <div className="admin-user-avatar">
          {user?.username?.charAt(0)?.toUpperCase() || 'A'}
        </div>

        <div className="admin-user-info">
          <strong>{user?.username}</strong>
          <span>Administrator</span>
        </div>

        <button
          type="button"
          onClick={logout}
          className="admin-logout-button"
        >
          Logout
        </button>
      </div>
    </header>
  )
}

export default AdminHeader