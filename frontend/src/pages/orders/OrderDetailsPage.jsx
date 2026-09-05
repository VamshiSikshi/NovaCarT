import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import api from '../../api/axios'
import '../../styles/order-details.css'

function OrderDetailsPage() {
  const { orderId } = useParams()
  const navigate = useNavigate()

  const [order, setOrder] = useState(null)
  const [payment, setPayment] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [cancelling, setCancelling] = useState(false)

  useEffect(() => {
    const loadOrder = async () => {
      try {
        const orderResponse = await api.get(
          `/api/orders/${orderId}`
        )

        setOrder(orderResponse.data)

        try {
          const paymentResponse = await api.get(
            `/api/orders/${orderId}/payment`
          )

          setPayment(paymentResponse.data)
        } catch {
          setPayment(null)
        }
      } catch (error) {
        setError(
          error.response?.data?.message ||
            'Failed to load order.'
        )
      } finally {
        setLoading(false)
      }
    }

    loadOrder()
  }, [orderId])

  const cancelOrder = async () => {
    try {
      setCancelling(true)
      setError('')

      const response = await api.patch(
        `/api/orders/${orderId}/cancel`
      )

      setOrder(response.data)
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to cancel order.'
      )
    } finally {
      setCancelling(false)
    }
  }

  const formatCurrency = (value) => {
    return Number(value || 0).toLocaleString('en-IN')
  }

  const formatDate = (value) => {
    if (!value) return '—'

    const date = new Date(value)

    if (Number.isNaN(date.getTime())) {
      return value
    }

    return date.toLocaleDateString('en-IN', {
      day: '2-digit',
      month: 'long',
      year: 'numeric',
    })
  }

  const formatDateTime = (value) => {
    if (!value) return '—'

    const date = new Date(value)

    if (Number.isNaN(date.getTime())) {
      return value
    }

    return date.toLocaleString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    })
  }

  const getStatusClass = (status) => {
    switch (String(status || '').toUpperCase()) {
      case 'DELIVERED':
        return 'delivered'

      case 'SHIPPED':
        return 'shipped'

      case 'CONFIRMED':
        return 'confirmed'

      case 'CANCELLED':
        return 'cancelled'

      default:
        return 'pending'
    }
  }

  const getStatusLabel = (status) => {
    switch (String(status || '').toUpperCase()) {
      case 'PENDING':
        return 'Order placed'

      case 'CONFIRMED':
        return 'Order confirmed'

      case 'SHIPPED':
        return 'Shipped'

      case 'DELIVERED':
        return 'Delivered'

      case 'CANCELLED':
        return 'Cancelled'

      default:
        return status || 'Pending'
    }
  }

  const getProgressStep = (status) => {
    switch (String(status || '').toUpperCase()) {
      case 'PENDING':
        return 1

      case 'CONFIRMED':
        return 2

      case 'SHIPPED':
        return 3

      case 'DELIVERED':
        return 4

      default:
        return 0
    }
  }

  if (loading) {
    return (
      <main className="order-details-page">
        <div className="order-details-container">
          <div className="order-details-loading">
            <span className="order-details-spinner" />
            <p>Loading your order...</p>
          </div>
        </div>
      </main>
    )
  }

  if (error && !order) {
    return (
      <main className="order-details-page">
        <div className="order-details-container">
          <div className="order-details-error">
            <div className="order-details-error-icon">
              !
            </div>

            <div>
              <h2>We couldn't load this order</h2>
              <p>{error}</p>

              <button
                type="button"
                onClick={() => navigate('/orders')}
              >
                Back to orders
              </button>
            </div>
          </div>
        </div>
      </main>
    )
  }

  if (!order) {
    return (
      <main className="order-details-page">
        <div className="order-details-container">
          <div className="order-details-error">
            <div>
              <h2>Order not found</h2>
              <p>
                We couldn't find the order you're looking for.
              </p>

              <button
                type="button"
                onClick={() => navigate('/orders')}
              >
                Back to orders
              </button>
            </div>
          </div>
        </div>
      </main>
    )
  }

  const statusClass = getStatusClass(order.status)
  const progressStep = getProgressStep(order.status)

  const isCancelled =
    String(order.status).toUpperCase() === 'CANCELLED'

  return (
    <main className="order-details-page">
      <div className="order-details-container">

        {/* =================================================
            BREADCRUMB
           ================================================= */}

        <div className="order-breadcrumb">
          <Link to="/orders">Orders</Link>
          <span>/</span>
          <span>Order #{order.id}</span>
        </div>

        {/* =================================================
            HERO
           ================================================= */}

        <section className="order-details-hero">
          <div>
            <p className="order-details-eyebrow">
              NOVACART / ORDER
            </p>

            <h1>
              Order #{order.id}<span>.</span>
            </h1>

            <p className="order-details-date">
              Placed on {formatDate(order.createdAt)}
            </p>
          </div>

          <div
            className={`order-main-status ${statusClass}`}
          >
            <span className="order-main-status-dot" />
            {getStatusLabel(order.status)}
          </div>
        </section>

        {error && (
          <div className="order-inline-error">
            <span>!</span>
            <p>{error}</p>
          </div>
        )}

        {/* =================================================
            DELIVERY PROGRESS
           ================================================= */}

        {!isCancelled ? (
          <section className="order-progress-card">
            <div className="order-progress-header">
              <div>
                <p className="order-details-eyebrow">
                  ORDER STATUS
                </p>

                <h2>
                  {getStatusLabel(order.status)}
                </h2>
              </div>

              <span className="order-progress-date">
                {formatDate(order.createdAt)}
              </span>
            </div>

            <div className="order-progress">

              <div className="order-progress-line">
                <div
                  className="order-progress-line-active"
                  style={{
                    width: `${Math.max(
                      0,
                      ((progressStep - 1) / 3) * 100
                    )}%`,
                  }}
                />
              </div>

              {[
                {
                  number: '01',
                  title: 'Placed',
                },
                {
                  number: '02',
                  title: 'Confirmed',
                },
                {
                  number: '03',
                  title: 'Shipped',
                },
                {
                  number: '04',
                  title: 'Delivered',
                },
              ].map((step, index) => {
                const stepNumber = index + 1
                const active =
                  stepNumber <= progressStep

                return (
                  <div
                    className={`order-progress-step ${
                      active ? 'active' : ''
                    }`}
                    key={step.number}
                  >
                    <div className="order-progress-number">
                      {active ? '✓' : step.number}
                    </div>

                    <span>{step.title}</span>
                  </div>
                )
              })}
            </div>
          </section>
        ) : (
          <section className="order-cancelled-banner">
            <div className="order-cancelled-icon">
              ×
            </div>

            <div>
              <strong>This order was cancelled</strong>
              <p>
                This order will not be processed or
                delivered.
              </p>
            </div>
          </section>
        )}

        {/* =================================================
            MAIN GRID
           ================================================= */}

        <div className="order-details-grid">

          {/* LEFT */}
          <div className="order-details-main">

            {/* ITEMS */}

            <section className="order-detail-card">
              <div className="order-card-heading">
                <div>
                  <p className="order-details-eyebrow">
                    ORDER ITEMS
                  </p>

                  <h2>
                    {order.items.length}{' '}
                    {order.items.length === 1
                      ? 'item'
                      : 'items'}
                  </h2>
                </div>
              </div>

              <div className="order-items">
                {order.items.map((item) => (
                  <article
                    className="order-item"
                    key={item.id}
                  >
                    <div className="order-item-image">
                      <span>Nova</span>
                    </div>

                    <div className="order-item-content">
                      <h3>{item.productName}</h3>

                      <p>
                        ₹{formatCurrency(item.price)}{' '}
                        per item
                      </p>

                      <span className="order-item-quantity">
                        Quantity × {item.quantity}
                      </span>
                    </div>

                    <strong className="order-item-total">
                      ₹{formatCurrency(item.subtotal)}
                    </strong>
                  </article>
                ))}
              </div>
            </section>

            {/* SHIPPING */}

            <section className="order-detail-card">
              <div className="order-card-heading">
                <div>
                  <p className="order-details-eyebrow">
                    DELIVERY
                  </p>

                  <h2>Shipping address</h2>
                </div>

                <div className="order-card-heading-icon">
                  ↗
                </div>
              </div>

              <div className="shipping-address">
                <strong>
                  {order.shippingFullName}
                </strong>

                <p>{order.shippingPhone}</p>

                <p>{order.shippingAddressLine}</p>

                <p>
                  {order.shippingCity},{' '}
                  {order.shippingState}
                </p>

                <p>
                  {order.shippingPostalCode},{' '}
                  {order.shippingCountry}
                </p>
              </div>
            </section>

            {/* PAYMENT */}

            <section className="order-detail-card">
              <div className="order-card-heading">
                <div>
                  <p className="order-details-eyebrow">
                    PAYMENT
                  </p>

                  <h2>Payment information</h2>
                </div>

                <div className="payment-secure-badge">
                  Secure
                </div>
              </div>

              {payment ? (
                <div className="payment-information">

                  <div className="payment-row">
                    <span>Payment method</span>
                    <strong>
                      {payment.method}
                    </strong>
                  </div>

                  <div className="payment-row">
                    <span>Payment status</span>

                    <strong
                      className={`payment-status ${String(
                        payment.status || ''
                      ).toLowerCase()}`}
                    >
                      {payment.status}
                    </strong>
                  </div>

                  <div className="payment-row">
                    <span>Amount paid</span>

                    <strong>
                      ₹{formatCurrency(payment.amount)}
                    </strong>
                  </div>

                  {payment.transactionId && (
                    <div className="payment-row">
                      <span>Transaction ID</span>

                      <strong className="transaction-id">
                        {payment.transactionId}
                      </strong>
                    </div>
                  )}

                  {payment.createdAt && (
                    <div className="payment-row">
                      <span>Payment date</span>

                      <strong>
                        {formatDateTime(
                          payment.createdAt
                        )}
                      </strong>
                    </div>
                  )}

                </div>
              ) : (
                <div className="payment-empty">
                  <span>—</span>
                  <p>
                    No payment information is available
                    for this order.
                  </p>
                </div>
              )}
            </section>
          </div>

          {/* RIGHT */}
          <aside className="order-details-sidebar">

            {/* SUMMARY */}

            <section className="order-summary-card">
              <p className="order-details-eyebrow">
                ORDER SUMMARY
              </p>

              <h2>Your order</h2>

              <div className="summary-divider" />

              <div className="summary-row">
                <span>Items</span>

                <strong>
                  {order.items.length}
                </strong>
              </div>

              <div className="summary-row">
                <span>Subtotal</span>

                <strong>
                  ₹{formatCurrency(order.totalAmount)}
                </strong>
              </div>

              <div className="summary-row">
                <span>Delivery</span>

                <strong className="free">
                  FREE
                </strong>
              </div>

              <div className="summary-divider" />

              <div className="summary-total">
                <div>
                  <span>Total</span>
                  <small>
                    Inclusive of applicable taxes
                  </small>
                </div>

                <strong>
                  ₹{formatCurrency(order.totalAmount)}
                </strong>
              </div>

              <div className="summary-protection">
                <span>✓</span>

                <div>
                  <strong>Secure order</strong>
                  <p>
                    Your order information is protected.
                  </p>
                </div>
              </div>

              <div className="summary-protection">
                <span>✓</span>

                <div>
                  <strong>Easy tracking</strong>
                  <p>
                    Follow your order status here.
                  </p>
                </div>
              </div>
            </section>

            {/* ACTIONS */}

            <section className="order-actions-card">

              {order.status === 'PENDING' && (
                <button
                  type="button"
                  className="cancel-order-button"
                  disabled={cancelling}
                  onClick={cancelOrder}
                >
                  {cancelling
                    ? 'Cancelling order...'
                    : 'Cancel order'}
                </button>
              )}

              <button
                type="button"
                className="back-orders-button"
                onClick={() => navigate('/orders')}
              >
                <span>←</span>
                Back to orders
              </button>
            </section>
          </aside>
        </div>

        {/* =================================================
            FOOTER CTA
           ================================================= */}

        <section className="order-details-explore">
          <div>
            <p className="order-details-eyebrow">
              KEEP EXPLORING
            </p>

            <h2>
              Find something
              <br />
              new.
            </h2>
          </div>

          <Link
            to="/products"
            className="order-explore-button"
          >
            Browse products
            <span>→</span>
          </Link>
        </section>
      </div>
    </main>
  )
}

export default OrderDetailsPage