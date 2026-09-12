import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../../../api/axios'
import '../../../styles/admin/admin-orders.css'

const formatCurrency = (amount) =>
  `₹${Number(amount || 0).toLocaleString('en-IN')}`

const formatDate = (date) => {
  if (!date) return '—'

  return new Date(date).toLocaleString('en-IN', {
    dateStyle: 'medium',
    timeStyle: 'short',
  })
}

const getStatusClass = (status) => {
  switch (status) {
    case 'PENDING':
      return 'status-pending'
    case 'CONFIRMED':
      return 'status-confirmed'
    case 'SHIPPED':
      return 'status-shipped'
    case 'DELIVERED':
      return 'status-delivered'
    case 'CANCELLED':
      return 'status-cancelled'
    default:
      return ''
  }
}

function AdminOrdersPage() {
  const [orders, setOrders] = useState([])
  const [status, setStatus] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const loadOrders = async () => {
      try {
        setLoading(true)
        setError('')

        const url = status
          ? `/api/admin/orders?status=${status}`
          : '/api/admin/orders'

        const response = await api.get(url)

        setOrders(response.data.content)
      } catch (error) {
        setError(
          error.response?.data?.message ||
          'Failed to load admin orders.'
        )
      } finally {
        setLoading(false)
      }
    }

    loadOrders()
  }, [status])

  if (loading) {
    return (
      <div className="admin-state">
        <p>Loading admin orders...</p>
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

  return (
    <div className="admin-orders-page">
      <div className="admin-page-heading">
        <p className="admin-page-eyebrow">
          ORDER MANAGEMENT
        </p>

        <h2>Orders</h2>

        <p className="admin-page-description">
          Monitor customer orders and manage their status.
        </p>
      </div>

      <section className="admin-panel admin-orders-toolbar">
        <div>
          <span className="admin-toolbar-label">
            Filter orders
          </span>

          <p>
            View orders by their current status.
          </p>
        </div>

        <div className="admin-filter-control">
          <label htmlFor="order-status">
            Status
          </label>

          <select
            id="order-status"
            value={status}
            onChange={(event) =>
              setStatus(event.target.value)
            }
          >
            <option value="">ALL</option>
            <option value="PENDING">PENDING</option>
            <option value="CONFIRMED">CONFIRMED</option>
            <option value="SHIPPED">SHIPPED</option>
            <option value="DELIVERED">DELIVERED</option>
            <option value="CANCELLED">CANCELLED</option>
          </select>
        </div>
      </section>

      <section className="admin-panel admin-orders-list">
        <div className="admin-panel-header">
          <div>
            <h3>Order List</h3>

            <p>
              {orders.length}{' '}
              {orders.length === 1
                ? 'order'
                : 'orders'}{' '}
              found
            </p>
          </div>
        </div>

        {orders.length === 0 ? (
          <div className="admin-empty-state">
            <strong>No orders found</strong>

            <p>
              There are no orders matching the selected
              status.
            </p>
          </div>
        ) : (
          <div className="admin-order-table">
            <div className="admin-order-table-header">
              <span>Order</span>
              <span>Customer</span>
              <span>Status</span>
              <span>Total</span>
              <span>Created</span>
              <span></span>
            </div>

            {orders.map((order) => (
              <div
                className="admin-order-row"
                key={order.orderId}
              >
                <div className="admin-order-id">
                  <strong>
                    #{order.orderId}
                  </strong>
                </div>

                <div className="admin-order-customer">
                  <strong>
                    {order.username}
                  </strong>

                  <span>
                    User ID: {order.userId}
                  </span>
                </div>

                <div>
                  <span
                    className={`admin-status-badge ${getStatusClass(
                      order.status
                    )}`}
                  >
                    {order.status}
                  </span>
                </div>

                <div className="admin-order-total">
                  {formatCurrency(order.totalAmount)}
                </div>

                <div className="admin-order-date">
                  {formatDate(order.createdAt)}
                </div>

                <div className="admin-order-action">
                  <Link
                    to={`/admin/orders/${order.orderId}`}
                    className="admin-action-button"
                  >
                    View
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  )
}

export default AdminOrdersPage