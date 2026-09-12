import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import api from '../../../api/axios'
import '../../../styles/admin/admin-ordersDetails.css'

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

function AdminOrderDetailsPage() {
  const { orderId } = useParams()

  const [order, setOrder] = useState(null)
  const [selectedStatus, setSelectedStatus] = useState('')
  const [loading, setLoading] = useState(true)
  const [updating, setUpdating] = useState(false)

  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const loadOrder = async () => {
    try {
      setLoading(true)
      setError('')

      const response = await api.get(
        `/api/admin/orders/${orderId}`
      )

      setOrder(response.data)
      setSelectedStatus(response.data.status)
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to load order.'
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadOrder()
  }, [orderId])

  const updateOrderStatus = async () => {
    if (!selectedStatus) {
      setError('Please select a status.')
      return
    }

    if (selectedStatus === order.status) {
      setError('Please select a different status.')
      return
    }

    try {
      setUpdating(true)
      setError('')
      setSuccess('')

      const response = await api.patch(
        `/api/orders/${orderId}/status`,
        {
          status: selectedStatus,
        }
      )

      setOrder(response.data)
      setSelectedStatus(response.data.status)

      setSuccess('Order status updated successfully.')
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to update order status.'
      )
    } finally {
      setUpdating(false)
    }
  }

  if (loading) {
    return (
      <div className="admin-state">
        <div className="admin-loading-spinner" />
        <p>Loading order...</p>
      </div>
    )
  }

  if (error && !order) {
    return (
      <div className="admin-state admin-state-error">
        <strong>Unable to load order</strong>
        <p>{error}</p>
      </div>
    )
  }

  if (!order) {
    return (
      <div className="admin-state">
        <strong>Order not found</strong>
        <p>The requested order could not be found.</p>
      </div>
    )
  }

  return (
    <div className="admin-order-details-page">

      {/* Header */}
      <header className="admin-order-details-header">
        <div className="admin-order-header-main">
          <Link
            to="/admin/orders"
            className="admin-order-back"
          >
            ← Orders
          </Link>

          <div className="admin-order-title-row">
            <div>
              <p className="admin-page-eyebrow">
                ORDER MANAGEMENT
              </p>

              <h1>
                Order <span>#{order.id}</span>
              </h1>

              <p className="admin-order-subtitle">
                Review order details, customer information,
                products and fulfillment status.
              </p>
            </div>

            <span
              className={`admin-status-badge admin-status-large ${getStatusClass(
                order.status
              )}`}
            >
              <span className="admin-status-dot" />
              {order.status}
            </span>
          </div>
        </div>
      </header>

      {/* Alerts */}
      {error && (
        <div className="admin-order-alert admin-order-alert-error">
          <span>!</span>
          <p>{error}</p>
          <button
            type="button"
            onClick={() => setError('')}
          >
            ×
          </button>
        </div>
      )}

      {success && (
        <div className="admin-order-alert admin-order-alert-success">
          <span>✓</span>
          <p>{success}</p>
          <button
            type="button"
            onClick={() => setSuccess('')}
          >
            ×
          </button>
        </div>
      )}

      {/* Summary */}
      <section className="admin-order-overview">

        <div className="admin-order-overview-card">
          <div className="admin-overview-icon customer">
            C
          </div>

          <div>
            <span>Customer</span>
            <strong>
              User #{order.userId}
            </strong>
            <small>Customer account</small>
          </div>
        </div>

        <div className="admin-order-overview-card">
          <div className="admin-overview-icon amount">
            ₹
          </div>

          <div>
            <span>Order total</span>
            <strong>
              {formatCurrency(order.totalAmount)}
            </strong>
            <small>
              {order.items?.length || 0}{' '}
              {order.items?.length === 1
                ? 'product'
                : 'products'}
            </small>
          </div>
        </div>

        <div className="admin-order-overview-card">
          <div className="admin-overview-icon date">
            #
          </div>

          <div>
            <span>Order date</span>
            <strong>
              {formatDate(order.createdAt)}
            </strong>
            <small>
              Order #{order.id}
            </small>
          </div>
        </div>

      </section>

      {/* Main grid */}
      <div className="admin-order-content-grid">

        {/* Left column */}
        <main className="admin-order-main-column">

          {/* Products */}
          <section className="admin-order-section">
            <div className="admin-section-heading">
              <div>
                <p className="admin-section-eyebrow">
                  PURCHASE
                </p>

                <h2>Order items</h2>

                <p>
                  Products included in this order.
                </p>
              </div>

              <span className="admin-section-count">
                {order.items?.length || 0}
              </span>
            </div>

            <div className="admin-order-items">

              {order.items.map((item, index) => (
                <article
                  className="admin-order-item"
                  key={item.id}
                >
                  <div className="admin-item-number">
                    {String(index + 1).padStart(2, '0')}
                  </div>

                  <div className="admin-item-info">
                    <strong>
                      {item.productName}
                    </strong>

                    <span>
                      Product ID #{item.productId}
                    </span>
                  </div>

                  <div className="admin-item-meta">
                    <span>Unit price</span>
                    <strong>
                      {formatCurrency(item.price)}
                    </strong>
                  </div>

                  <div className="admin-item-meta">
                    <span>Quantity</span>
                    <strong>
                      × {item.quantity}
                    </strong>
                  </div>

                  <div className="admin-item-subtotal">
                    <span>Subtotal</span>
                    <strong>
                      {formatCurrency(item.subtotal)}
                    </strong>
                  </div>
                </article>
              ))}

            </div>

            <div className="admin-order-total-row">
              <span>Order total</span>

              <strong>
                {formatCurrency(order.totalAmount)}
              </strong>
            </div>
          </section>

          {/* Shipping */}
          <section className="admin-order-section">
            <div className="admin-section-heading">
              <div>
                <p className="admin-section-eyebrow">
                  DELIVERY
                </p>

                <h2>Shipping address</h2>

                <p>
                  Delivery information provided by the customer.
                </p>
              </div>
            </div>

            <div className="admin-shipping-card">
              <div className="admin-shipping-avatar">
                {order.shippingFullName
                  ?.charAt(0)
                  ?.toUpperCase() || 'U'}
              </div>

              <div className="admin-shipping-content">
                <strong>
                  {order.shippingFullName}
                </strong>

                <span>
                  {order.shippingPhone}
                </span>

                <address>
                  {order.shippingAddressLine}
                  <br />
                  {order.shippingCity},{' '}
                  {order.shippingState}
                  <br />
                  {order.shippingPostalCode},{' '}
                  {order.shippingCountry}
                </address>
              </div>
            </div>
          </section>

        </main>

        {/* Right column */}
        <aside className="admin-order-side-column">

          {/* Status */}
          <section className="admin-order-section admin-status-section">
            <div className="admin-section-heading">
              <div>
                <p className="admin-section-eyebrow">
                  FULFILLMENT
                </p>

                <h2>Order status</h2>

                <p>
                  Update the current fulfillment state.
                </p>
              </div>
            </div>

            <div className="admin-current-status">
              <span
                className={`admin-status-badge ${getStatusClass(
                  order.status
                )}`}
              >
                <span className="admin-status-dot" />
                {order.status}
              </span>
            </div>

            <div className="admin-status-update">
              <label htmlFor="selected-status">
                Change status
              </label>

              <select
                id="selected-status"
                value={selectedStatus}
                onChange={(event) =>
                  setSelectedStatus(event.target.value)
                }
                disabled={updating}
              >
                <option value="PENDING">
                  PENDING
                </option>

                <option value="CONFIRMED">
                  CONFIRMED
                </option>

                <option value="SHIPPED">
                  SHIPPED
                </option>

                <option value="DELIVERED">
                  DELIVERED
                </option>

                <option value="CANCELLED">
                  CANCELLED
                </option>
              </select>

              <button
                type="button"
                className="admin-order-update-button"
                onClick={updateOrderStatus}
                disabled={
                  updating ||
                  selectedStatus === order.status
                }
              >
                {updating
                  ? 'Updating...'
                  : 'Update status'}

                {!updating && <span>→</span>}
              </button>
            </div>

            <div className="admin-status-flow">
              <div
                className={
                  order.status === 'CANCELLED'
                    ? 'cancelled'
                    : 'completed'
                }
              >
                <span />
                <label>Pending</label>
              </div>

              <div
                className={
                  ['CONFIRMED', 'SHIPPED', 'DELIVERED'].includes(
                    order.status
                  )
                    ? 'completed'
                    : ''
                }
              >
                <span />
                <label>Confirmed</label>
              </div>

              <div
                className={
                  ['SHIPPED', 'DELIVERED'].includes(
                    order.status
                  )
                    ? 'completed'
                    : ''
                }
              >
                <span />
                <label>Shipped</label>
              </div>

              <div
                className={
                  order.status === 'DELIVERED'
                    ? 'completed'
                    : ''
                }
              >
                <span />
                <label>Delivered</label>
              </div>
            </div>
          </section>

          {/* Order information */}
          <section className="admin-order-section admin-order-info-section">
            <div className="admin-section-heading">
              <div>
                <p className="admin-section-eyebrow">
                  INFORMATION
                </p>

                <h2>Order information</h2>
              </div>
            </div>

            <div className="admin-info-list">
              <div>
                <span>Order ID</span>
                <strong>#{order.id}</strong>
              </div>

              <div>
                <span>Customer ID</span>
                <strong>#{order.userId}</strong>
              </div>

              <div>
                <span>Products</span>
                <strong>
                  {order.items?.length || 0}
                </strong>
              </div>

              <div>
                <span>Created</span>
                <strong>
                  {formatDate(order.createdAt)}
                </strong>
              </div>
            </div>
          </section>

        </aside>
      </div>

      <Link
        to="/admin/orders"
        className="admin-order-bottom-link"
      >
        ← Back to all orders
      </Link>
    </div>
  )
}

export default AdminOrderDetailsPage