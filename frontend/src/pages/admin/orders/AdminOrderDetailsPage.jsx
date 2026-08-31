import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import api from '../../../api/axios'

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
      setError(
        'Please select a different status.'
      )
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

      setSuccess(
        'Order status updated successfully.'
      )
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
        <p>Loading order...</p>
      </div>
    )
  }

  if (error && !order) {
    return (
      <div className="admin-state admin-state-error">
        <p>{error}</p>
      </div>
    )
  }

  if (!order) {
    return (
      <div className="admin-state">
        <p>Order not found.</p>
      </div>
    )
  }

  return (
    <div className="admin-order-details-page">
      <div className="admin-page-heading">
        <p className="admin-page-eyebrow">
          ORDER MANAGEMENT
        </p>

        <div className="admin-order-details-title">
          <div>
            <h2>Order #{order.id}</h2>

            <p className="admin-page-description">
              Review order information and update its
              current status.
            </p>
          </div>

          <span
            className={`admin-status-badge admin-status-large ${getStatusClass(
              order.status
            )}`}
          >
            {order.status}
          </span>
        </div>
      </div>

      {error && (
        <div className="admin-alert admin-alert-error">
          {error}
        </div>
      )}

      {success && (
        <div className="admin-alert admin-alert-success">
          {success}
        </div>
      )}

      <section className="admin-order-summary-grid">
        <div className="admin-panel admin-order-summary-card">
          <span className="admin-summary-label">
            Customer
          </span>

          <strong>
            User #{order.userId}
          </strong>

          <p>
            Customer account
          </p>
        </div>

        <div className="admin-panel admin-order-summary-card">
          <span className="admin-summary-label">
            Order Total
          </span>

          <strong>
            {formatCurrency(order.totalAmount)}
          </strong>

          <p>
            {order.items?.length || 0}{' '}
            {order.items?.length === 1
              ? 'item'
              : 'items'}
          </p>
        </div>

        <div className="admin-panel admin-order-summary-card">
          <span className="admin-summary-label">
            Created
          </span>

          <strong>
            {formatDate(order.createdAt)}
          </strong>

          <p>
            Order #{order.id}
          </p>
        </div>
      </section>

      <section className="admin-panel admin-status-panel">
        <div className="admin-panel-header">
          <div>
            <h3>Update Order Status</h3>

            <p>
              Change the order's current fulfillment
              status.
            </p>
          </div>
        </div>

        <div className="admin-status-form">
          <div>
            <label htmlFor="selected-status">
              Status
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
          </div>

          <button
            type="button"
            className="admin-primary-button"
            onClick={updateOrderStatus}
            disabled={
              updating ||
              selectedStatus === order.status
            }
          >
            {updating
              ? 'Updating...'
              : 'Update Status'}
          </button>
        </div>
      </section>

      <section className="admin-panel admin-items-panel">
        <div className="admin-panel-header">
          <div>
            <h3>Order Items</h3>

            <p>
              Products included in this order.
            </p>
          </div>

          <span className="admin-panel-total">
            {order.items?.length || 0} items
          </span>
        </div>

        <div className="admin-order-items">
          {order.items.map((item) => (
            <div
              className="admin-order-item"
              key={item.id}
            >
              <div className="admin-order-item-main">
                <strong>
                  {item.productName}
                </strong>

                <span>
                  Product ID: {item.productId}
                </span>
              </div>

              <div className="admin-order-item-detail">
                <span>Price</span>
                <strong>
                  {formatCurrency(item.price)}
                </strong>
              </div>

              <div className="admin-order-item-detail">
                <span>Quantity</span>
                <strong>
                  {item.quantity}
                </strong>
              </div>

              <div className="admin-order-item-detail">
                <span>Subtotal</span>
                <strong>
                  {formatCurrency(item.subtotal)}
                </strong>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section className="admin-panel admin-shipping-panel">
        <div className="admin-panel-header">
          <div>
            <h3>Shipping Address</h3>

            <p>
              Delivery information for this order.
            </p>
          </div>
        </div>

        <div className="admin-shipping-address">
          <strong>
            {order.shippingFullName}
          </strong>

          <span>
            {order.shippingPhone}
          </span>

          <span>
            {order.shippingAddressLine}
          </span>

          <span>
            {order.shippingCity},{' '}
            {order.shippingState}
          </span>

          <span>
            {order.shippingPostalCode},{' '}
            {order.shippingCountry}
          </span>
        </div>
      </section>

      <Link
        to="/admin/orders"
        className="admin-back-link"
      >
        ← Back to Orders
      </Link>
    </div>
  )
}

export default AdminOrderDetailsPage