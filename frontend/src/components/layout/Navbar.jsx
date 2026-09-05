import { Link, NavLink } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

function Navbar() {
  const { isAuthenticated, user, logout } = useAuth()

  return (
    <header className="site-header">
      <nav className="site-navbar">
        <Link to="/" className="site-brand">
          Nova<span>Cart</span>
        </Link>

        <div className="site-nav-links">
          <NavLink
            to="/"
            className={({ isActive }) =>
              `site-nav-link ${isActive ? 'active' : ''}`
            }
          >
            Home
          </NavLink>

          <NavLink
            to="/products"
            className={({ isActive }) =>
              `site-nav-link ${isActive ? 'active' : ''}`
            }
          >
            Products
          </NavLink>

          {isAuthenticated && (
            <>
              <NavLink
                to="/cart"
                className={({ isActive }) =>
                  `site-nav-link ${isActive ? 'active' : ''}`
                }
              >
                Cart
              </NavLink>

              <NavLink
                to="/orders"
                className={({ isActive }) =>
                  `site-nav-link ${isActive ? 'active' : ''}`
                }
              >
                Orders
              </NavLink>
            </>
          )}

          {isAuthenticated && user?.role === 'ADMIN' && (
            <NavLink
              to="/admin"
              className={({ isActive }) =>
                `site-nav-link admin-link ${isActive ? 'active' : ''}`
              }
            >
              Admin
            </NavLink>
          )}
        </div>

        <div className="site-nav-actions">
          {!isAuthenticated ? (
            <>
              <Link to="/login" className="nav-login">
                Login
              </Link>

              <Link to="/register" className="nav-register">
                Register
              </Link>
            </>
          ) : (
            <>
              <span className="nav-user">
                Hi, {user?.username}
              </span>

              <button
                type="button"
                className="nav-logout"
                onClick={logout}
              >
                Logout
              </button>
            </>
          )}
        </div>
      </nav>
    </header>
  )
}

export default Navbar