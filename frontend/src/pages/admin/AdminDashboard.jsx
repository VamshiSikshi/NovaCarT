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
    return (
      <div className="admin-state">
        <p>Loading dashboard...</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="admin-state admin-state-error">
        <p>{error}</p>
      </div>
    )
  }

  if (!dashboard) {
    return (
      <div className="admin-state">
        <p>Dashboard data not found.</p>
      </div>
    )
  }

  const formatCurrency = (value) =>
    Number(value || 0).toLocaleString('en-IN')

  return (
    <div className="admin-dashboard">

      {/* PAGE HEADER */}
      <div className="admin-page-heading">
        <div>
          <p className="admin-page-eyebrow">OVERVIEW</p>
          <h2>Dashboard</h2>
          <p className="admin-page-description">
            Monitor your NovaCart store at a glance.
          </p>
        </div>
      </div>

      {/* KPI CARDS */}
      <section className="admin-kpi-grid">

        <div className="admin-kpi-card">
          <div className="admin-kpi-top">
            <span className="admin-kpi-label">Total Users</span>
            <span className="admin-kpi-icon">U</span>
          </div>

          <strong className="admin-kpi-value">
            {dashboard.totalUsers}
          </strong>

          <span className="admin-kpi-meta">
            {dashboard.totalAdmins} administrator
            {dashboard.totalAdmins !== 1 ? 's' : ''}
          </span>
        </div>

        <div className="admin-kpi-card">
          <div className="admin-kpi-top">
            <span className="admin-kpi-label">Total Products</span>
            <span className="admin-kpi-icon">P</span>
          </div>

          <strong className="admin-kpi-value">
            {dashboard.totalProducts}
          </strong>

          <span className="admin-kpi-meta">
            {dashboard.totalStock.toLocaleString('en-IN')} units in stock
          </span>
        </div>

        <div className="admin-kpi-card">
          <div className="admin-kpi-top">
            <span className="admin-kpi-label">Total Orders</span>
            <span className="admin-kpi-icon">O</span>
          </div>

          <strong className="admin-kpi-value">
            {dashboard.totalOrders}
          </strong>

          <span className="admin-kpi-meta">
            {dashboard.pendingOrders} pending orders
          </span>
        </div>

        <div className="admin-kpi-card admin-kpi-card-accent">
          <div className="admin-kpi-top">
            <span className="admin-kpi-label">Total Revenue</span>
            <span className="admin-kpi-icon">₹</span>
          </div>

          <strong className="admin-kpi-value">
            ₹{formatCurrency(dashboard.totalRevenue)}
          </strong>

          <span className="admin-kpi-meta">
            {dashboard.successfulPayments} successful payments
          </span>
        </div>

      </section>

      {/* LOWER DASHBOARD */}
      <section className="admin-dashboard-grid">

        {/* ORDER STATUS */}
        <div className="admin-panel">
          <div className="admin-panel-header">
            <div>
              <h3>Order Status</h3>
              <p>Current order distribution</p>
            </div>

            <span className="admin-panel-total">
              {dashboard.totalOrders} total
            </span>
          </div>

          <div className="order-status-list">

            <div className="order-status-item">
              <span>
                <span className="status-dot pending" />
                Pending
              </span>
              <strong>{dashboard.pendingOrders}</strong>
            </div>

            <div className="order-status-item">
              <span>
                <span className="status-dot confirmed" />
                Confirmed
              </span>
              <strong>{dashboard.confirmedOrders}</strong>
            </div>

            <div className="order-status-item">
              <span>
                <span className="status-dot shipped" />
                Shipped
              </span>
              <strong>{dashboard.shippedOrders}</strong>
            </div>

            <div className="order-status-item">
              <span>
                <span className="status-dot delivered" />
                Delivered
              </span>
              <strong>{dashboard.deliveredOrders}</strong>
            </div>

            <div className="order-status-item">
              <span>
                <span className="status-dot cancelled" />
                Cancelled
              </span>
              <strong>{dashboard.cancelledOrders}</strong>
            </div>

          </div>
        </div>

        {/* INVENTORY */}
        <div className="admin-panel">
          <div className="admin-panel-header">
            <div>
              <h3>Inventory</h3>
              <p>Current catalog stock</p>
            </div>
          </div>

          <div className="inventory-stat">
            <span>Total Stock</span>
            <strong>
              {dashboard.totalStock.toLocaleString('en-IN')}
            </strong>
          </div>

          <div className="inventory-stat">
            <span>Products</span>
            <strong>{dashboard.totalProducts}</strong>
          </div>

          <div className="inventory-stat">
            <span>Low Stock</span>
            <strong
              className={
                dashboard.lowStockProducts > 0
                  ? 'inventory-warning'
                  : 'inventory-success'
              }
            >
              {dashboard.lowStockProducts}
            </strong>
          </div>
        </div>

      </section>

      {/* PAYMENT SUMMARY */}
      <section className="admin-panel admin-payment-summary">

        <div className="admin-panel-header">
          <div>
            <h3>Payment Summary</h3>
            <p>Overview of payment activity</p>
          </div>
        </div>

        <div className="payment-summary-grid">

          <div>
            <span>Total Payments</span>
            <strong>{dashboard.totalPayments}</strong>
          </div>

          <div>
            <span>Successful Payments</span>
            <strong>{dashboard.successfulPayments}</strong>
          </div>

          <div>
            <span>Total Revenue</span>
            <strong>
              ₹{formatCurrency(dashboard.totalRevenue)}
            </strong>
          </div>

        </div>

      </section>

    </div>
  )
}

export default AdminDashboard