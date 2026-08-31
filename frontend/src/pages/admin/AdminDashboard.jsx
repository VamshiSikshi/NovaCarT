import { useEffect, useState } from 'react'
import api from '../../api/axios'

function AdminDashboard() {
  const [dashboard, setDashboard] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        const response = await api.get('/api/admin/dashboard')

        setDashboard(response.data)
      } catch (error) {
        setError(
          error.response?.data?.message ||
          'Failed to load dashboard.'
        )
      } finally {
        setLoading(false)
      }
    }

    loadDashboard()
  }, [])

  if (loading) {
    return <p>Loading dashboard...</p>
  }

  if (error) {
    return <p>{error}</p>
  }

  if (!dashboard) {
    return <p>Dashboard data not found.</p>
  }

  return (
    <div>
      <h1>Admin Dashboard</h1>

      <section>
        <h2>Users</h2>

        <p>Total Users: {dashboard.totalUsers}</p>
        <p>Total Admins: {dashboard.totalAdmins}</p>
      </section>

      <section>
        <h2>Products</h2>

        <p>Total Products: {dashboard.totalProducts}</p>
        <p>Total Stock: {dashboard.totalStock}</p>
        <p>
          Low Stock Products: {dashboard.lowStockProducts}
        </p>
      </section>

      <section>
        <h2>Orders</h2>

        <p>Total Orders: {dashboard.totalOrders}</p>
        <p>Pending: {dashboard.pendingOrders}</p>
        <p>Confirmed: {dashboard.confirmedOrders}</p>
        <p>Shipped: {dashboard.shippedOrders}</p>
        <p>Delivered: {dashboard.deliveredOrders}</p>
        <p>Cancelled: {dashboard.cancelledOrders}</p>
      </section>

      <section>
        <h2>Payments</h2>

        <p>Total Payments: {dashboard.totalPayments}</p>
        <p>
          Successful Payments: {dashboard.successfulPayments}
        </p>
        <p>
          Total Revenue: ₹{dashboard.totalRevenue}
        </p>
      </section>
    </div>
  )
}

export default AdminDashboard