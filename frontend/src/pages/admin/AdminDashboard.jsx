import { useEffect, useMemo, useState } from 'react'
import api from '../../api/axios'
import '../../styles/admin/admin-dashboard.css'

function UsersIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
      <circle cx="9" cy="7" r="4" />
      <path d="M22 21v-2a4 4 0 0 0-3-3.87" />
      <path d="M16 3.13a4 4 0 0 1 0 7.75" />
    </svg>
  )
}

function ProductsIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <path d="m21 16-9 5-9-5V8l9-5 9 5v8Z" />
      <path d="m3.3 8 8.7 5 8.7-5" />
      <path d="M12 13v8" />
    </svg>
  )
}

function OrdersIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <rect x="5" y="3" width="14" height="18" rx="2" />
      <path d="M9 8h6M9 12h6M9 16h4" />
    </svg>
  )
}

function RevenueIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <path d="M12 2v20" />
      <path d="M17 5.5A4.5 4.5 0 0 0 12.5 3C10 3 8 4.4 8 6.5S9.8 10 12.5 10s4.5 1.5 4.5 3.5S15 17 12.5 17A5.5 5.5 0 0 1 7 14" />
    </svg>
  )
}

function CheckIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <path d="m5 12 4 4L19 6" />
    </svg>
  )
}

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

  const formatNumber = (value) =>
    Number(value || 0).toLocaleString('en-IN')

  const formatCurrency = (value) =>
    `₹${formatNumber(value)}`

  const orderStatuses = useMemo(() => {
    if (!dashboard) {
      return []
    }

    return [
      {
        label: 'Pending',
        value: Number(dashboard.pendingOrders || 0),
        type: 'pending',
      },
      {
        label: 'Confirmed',
        value: Number(dashboard.confirmedOrders || 0),
        type: 'confirmed',
      },
      {
        label: 'Shipped',
        value: Number(dashboard.shippedOrders || 0),
        type: 'shipped',
      },
      {
        label: 'Delivered',
        value: Number(dashboard.deliveredOrders || 0),
        type: 'delivered',
      },
      {
        label: 'Cancelled',
        value: Number(dashboard.cancelledOrders || 0),
        type: 'cancelled',
      },
    ]
  }, [dashboard])

  const maxOrders = Math.max(
    ...orderStatuses.map((status) => status.value),
    1
  )

  const getPercentage = (value) => {
    if (!dashboard?.totalOrders) {
      return 0
    }

    return Math.round(
      (Number(value || 0) / Number(dashboard.totalOrders)) * 100
    )
  }

  if (loading) {
    return (
      <div className="admin-state">
        <div className="admin-loading">
          <span className="admin-loading-spinner" />
          <p>Loading dashboard...</p>
        </div>
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

  return (
    <div className="admin-dashboard">

      {/* =====================================================
          PAGE INTRO
      ===================================================== */}

      <section className="admin-dashboard-heading">

        <div>
          <span className="admin-section-kicker">
            Overview
          </span>

          <h2>Dashboard</h2>

          <p>
            Monitor your NovaCart store at a glance.
          </p>
        </div>

        <div className="admin-live-badge">
          <span />
          Live data
        </div>

      </section>


      {/* =====================================================
          KPI CARDS
      ===================================================== */}

      <section className="admin-kpi-grid">

        {/* USERS */}

        <article className="admin-kpi-card">

          <div className="admin-kpi-top">

            <span className="admin-kpi-label">
              Total Users
            </span>

            <span className="admin-kpi-icon">
              <UsersIcon />
            </span>

          </div>

          <strong className="admin-kpi-value">
            {formatNumber(dashboard.totalUsers)}
          </strong>

          <div className="admin-kpi-footer">

            <span>
              {dashboard.totalAdmins} administrator
              {dashboard.totalAdmins !== 1 ? 's' : ''}
            </span>

            <span className="admin-kpi-arrow">
              ↗
            </span>

          </div>

        </article>


        {/* PRODUCTS */}

        <article className="admin-kpi-card">

          <div className="admin-kpi-top">

            <span className="admin-kpi-label">
              Total Products
            </span>

            <span className="admin-kpi-icon">
              <ProductsIcon />
            </span>

          </div>

          <strong className="admin-kpi-value">
            {formatNumber(dashboard.totalProducts)}
          </strong>

          <div className="admin-kpi-footer">

            <span>
              {formatNumber(dashboard.totalStock)} units in stock
            </span>

            <span className="admin-kpi-arrow">
              ↗
            </span>

          </div>

        </article>


        {/* ORDERS */}

        <article className="admin-kpi-card">

          <div className="admin-kpi-top">

            <span className="admin-kpi-label">
              Total Orders
            </span>

            <span className="admin-kpi-icon">
              <OrdersIcon />
            </span>

          </div>

          <strong className="admin-kpi-value">
            {formatNumber(dashboard.totalOrders)}
          </strong>

          <div className="admin-kpi-footer">

            <span>
              {formatNumber(dashboard.pendingOrders)} pending orders
            </span>

            <span className="admin-kpi-arrow">
              ↗
            </span>

          </div>

        </article>


        {/* REVENUE */}

        <article className="admin-kpi-card admin-kpi-card-revenue">

          <div className="admin-kpi-top">

            <span className="admin-kpi-label">
              Total Revenue
            </span>

            <span className="admin-kpi-icon">
              <RevenueIcon />
            </span>

          </div>

          <strong className="admin-kpi-value">
            {formatCurrency(dashboard.totalRevenue)}
          </strong>

          <div className="admin-kpi-footer">

            <span>
              {formatNumber(dashboard.successfulPayments)} successful payments
            </span>

            <span className="admin-kpi-arrow">
              ↗
            </span>

          </div>

        </article>

      </section>


      {/* =====================================================
          ORDER STATUS + INVENTORY
      ===================================================== */}

      <section className="admin-dashboard-main-grid">

        {/* ORDER STATUS */}

        <article className="admin-dashboard-panel">

          <div className="admin-panel-heading">

            <div>

              <span className="admin-panel-kicker">
                Orders
              </span>

              <h3>
                Order status
              </h3>

              <p>
                Current distribution across your orders.
              </p>

            </div>

            <span className="admin-panel-count">
              {formatNumber(dashboard.totalOrders)} Total Orders
            </span>

          </div>


          <div className="admin-order-status-list">

            {orderStatuses.map((status) => {

              const percentage = getPercentage(status.value)

              const barWidth =
                status.value === 0
                  ? 0
                  : Math.max(
                      5,
                      Math.round(
                        (status.value / maxOrders) * 100
                      )
                    )

              return (
                <div
                  className="admin-order-status-row"
                  key={status.type}
                >

                  <div className="admin-order-status-label">

                    <span
                      className={`admin-status-dot ${status.type}`}
                    />

                    <span>
                      {status.label}
                    </span>

                  </div>


                  <div className="admin-order-status-track">

                    <span
                      className={`admin-order-status-bar ${status.type}`}
                      style={{
                        width: `${barWidth}%`,
                      }}
                    />

                  </div>


                  <strong>
                    {status.value}
                  </strong>


                  <span className="admin-order-status-percent">
                    {percentage}%
                  </span>

                </div>
              )
            })}

          </div>

        </article>


        {/* INVENTORY */}

        <article className="admin-dashboard-panel">

          <div className="admin-panel-heading">

            <div>

              <span className="admin-panel-kicker">
                Catalog
              </span>

              <h3>
                Inventory overview
              </h3>

              <p>
                Current catalog stock overview.
              </p>

            </div>

          </div>


          <div className="admin-inventory-list">

            <div className="admin-inventory-row">

              <span>
                Total Stock
              </span>

              <strong>
                {formatNumber(dashboard.totalStock)}
              </strong>

            </div>


            <div className="admin-inventory-row">

              <span>
                Products
              </span>

              <strong>
                {formatNumber(dashboard.totalProducts)}
              </strong>

            </div>


            <div className="admin-inventory-row">

              <span>
                Low Stock
              </span>

              <strong
                className={
                  dashboard.lowStockProducts > 0
                    ? 'admin-value-warning'
                    : 'admin-value-success'
                }
              >
                {formatNumber(dashboard.lowStockProducts)}
              </strong>

            </div>

          </div>


          <div className="admin-inventory-alert">

            <span className="admin-inventory-alert-dot">
              <CheckIcon />
            </span>

            <div>

              <strong>
                {dashboard.lowStockProducts > 0
                  ? 'Inventory needs attention'
                  : 'Inventory looks healthy'}
              </strong>

              <span>
                {dashboard.lowStockProducts > 0
                  ? `${formatNumber(dashboard.lowStockProducts)} products currently flagged as low stock.`
                  : 'No products currently flagged as low stock.'}
              </span>

            </div>

          </div>

        </article>

      </section>


      {/* =====================================================
          PAYMENT SUMMARY
      ===================================================== */}

      <section className="admin-dashboard-panel admin-payment-panel">

        <div className="admin-panel-heading">

          <div>

            <span className="admin-panel-kicker">
              Payments
            </span>

            <h3>
              Payment summary
            </h3>

            <p>
              Overview of payment activity.
            </p>

          </div>

        </div>


        <div className="admin-payment-grid">

          <div>

            <span>
              Total Payments
            </span>

            <strong>
              {formatNumber(dashboard.totalPayments)}
            </strong>

          </div>


          <div>

            <span>
              Successful Payments
            </span>

            <strong>
              {formatNumber(dashboard.successfulPayments)}
            </strong>

          </div>


          <div className="admin-payment-revenue">

            <span>
              Total Revenue
            </span>

            <strong>
              {formatCurrency(dashboard.totalRevenue)}
            </strong>

          </div>

        </div>

      </section>

    </div>
  )
}

export default AdminDashboard