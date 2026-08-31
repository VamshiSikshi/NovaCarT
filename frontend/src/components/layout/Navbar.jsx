import { Link } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

function Navbar() {
  const { isAuthenticated, user, logout } = useAuth()

  return (
    <nav>
      <Link to="/">NovaCart</Link>

      <Link to="/products">Products</Link>

      {isAuthenticated && (
        <>
          <Link to="/cart">Cart</Link>
          <Link to="/orders">Orders</Link>
        </>
      )}

      {isAuthenticated && user?.role === 'ADMIN' && (
        <>
          <Link to="/admin">Admin</Link>
          <Link to="/admin/products">
            Admin Products
          </Link>
          <Link to="/admin/categories">
            Admin Categories
          </Link>
          <Link to="/admin/users">
            Admin Users
          </Link>
          <Link to="/admin/orders">
            Admin Orders
          </Link>
          <Link to="/admin/payments">
            Admin Payments
          </Link>
        </>
      )}

      {!isAuthenticated ? (
        <>
          <Link to="/login">Login</Link>
          <Link to="/register">Register</Link>
        </>
      ) : (
        <>
          <span>
            Hi, {user?.username}
          </span>

          <button type="button" onClick={logout}>
            Logout
          </button>
        </>
      )}
    </nav>
  )
}

export default Navbar