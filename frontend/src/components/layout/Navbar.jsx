import { useEffect, useState } from 'react'
import { Link, NavLink } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { getMyProfile } from '../../services/profileService'
import '../../styles/navbar.css'

function Navbar() {
  const { isAuthenticated, user, logout } = useAuth()

  const [profileImage, setProfileImage] = useState('')
  const [imageError, setImageError] = useState(false)

  useEffect(() => {
    if (!isAuthenticated) {
      setProfileImage('')
      setImageError(false)
      return
    }

    const loadProfileImage = async () => {
      try {
        const data = await getMyProfile()

        setProfileImage(data.profileImage || '')
        setImageError(false)
      } catch {
        setProfileImage('')
      }
    }

    loadProfileImage()
  }, [isAuthenticated])

  return (
    <header className="site-header">
      <nav className="site-navbar">

        {/* Brand */}
        <Link to="/" className="site-brand">
          Nova<span>Cart</span>
        </Link>

        {/* Main navigation */}
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

        {/* Right side */}
        <div className="site-nav-actions">

          {!isAuthenticated ? (
            <>
              <Link
                to="/login"
                className="nav-login"
              >
                Login
              </Link>

              <Link
                to="/register"
                className="nav-register"
              >
                Register
              </Link>
            </>
          ) : (
            <div className="navbar-account">

              {/* Profile */}
              <NavLink
                to="/profile"
                className={({ isActive }) =>
                  `navbar-profile-link ${isActive ? 'active' : ''}`
                }
                title="Profile"
                aria-label="Profile"
              >
                {profileImage && !imageError ? (
                  <img
                    src={profileImage}
                    alt="Profile"
                    className="navbar-profile-image"
                    onError={() => setImageError(true)}
                  />
                ) : (
                  <span className="navbar-profile-placeholder">
                    {user?.username?.charAt(0).toUpperCase() || '?'}
                  </span>
                )}
              </NavLink>

              {/* Username */}
              <span className="navbar-greeting">
                Hi, {user?.username}
              </span>

              {/* Settings */}
              <NavLink
                to="/settings"
                className={({ isActive }) =>
                  `navbar-settings-link ${isActive ? 'active' : ''}`
                }
                title="Settings"
                aria-label="Settings"
              >
                <svg
                  className="navbar-settings-icon"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                  aria-hidden="true"
                >
                  <path
                    d="M12 15.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7Z"
                    stroke="currentColor"
                    strokeWidth="1.8"
                  />

                  <path
                    d="M19.4 15a1.7 1.7 0 0 0 .2 1.9l.1.1-2.7 2.7-.1-.1a1.7 1.7 0 0 0-1.9-.2 1.7 1.7 0 0 0-1 1.6v.2h-4V21a1.7 1.7 0 0 0-1-1.6 1.7 1.7 0 0 0-1.9.2l-.1.1-2.7-2.7.1-.1A1.7 1.7 0 0 0 4.6 15 1.7 1.7 0 0 0 3 14h-.2v-4H3a1.7 1.7 0 0 0 1.6-1A1.7 1.7 0 0 0 4.4 7l-.1-.1L7 4.2l.1.1A1.7 1.7 0 0 0 9 4.5 1.7 1.7 0 0 0 10 3v-.2h4V3a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.9-.2l.1-.1 2.7 2.7-.1.1a1.7 1.7 0 0 0-.2 1.9 1.7 1.7 0 0 0 1.6 1h.2v4H21a1.7 1.7 0 0 0-1.6 1Z"
                    stroke="currentColor"
                    strokeWidth="1.5"
                    strokeLinejoin="round"
                  />
                </svg>
              </NavLink>

              {/* Logout */}
              <button
                type="button"
                className="navbar-logout-button"
                onClick={logout}
              >
                Logout
              </button>

            </div>
          )}

        </div>

      </nav>
    </header>
  )
}

export default Navbar