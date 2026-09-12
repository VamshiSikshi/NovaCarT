import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function AdminRoute() {
  const { isAuthenticated, user, loading } = useAuth()

  if (loading) {
    return <p>Loading...</p>
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  if (user?.role !== 'ADMIN') {
    return <Navigate to="/products" replace />
  }

  return <Outlet />
}

export default AdminRoute