import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../../api/axios'
import '../../styles/orders.css'

function OrdersPage() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const loadOrders = async () => {
      try {
        const response = await api.get('/api/orders')
        setOrders(response.data)
      } catch (error) {
        setError(
          error.response?.data?.message ||
            'Failed to load orders.'
        )
      } finally {
        setLoading(false)
      }
    }

    loadOrders()
  }, [])

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
      month: 'short',
      year: 'numeric',
    })
  }

  const getStatusClass = (status) => {
    const normalizedStatus = String(status || '')
      .toLowerCase()

    if (normalizedStatus === 'delivered') {
      return 'delivered'
    }

    if (normalizedStatus === 'cancelled') {
      return 'cancelled'
    }

    if (normalizedStatus === 'shipped') {
      return 'shipped'
    }

    if (normalizedStatus === 'confirmed') {
      return 'confirmed'
    }

    return 'pending'
  }

  if (loading) {
    return (
      <main className="orders-page">
        <div className="orders-container">
          <div className="orders-loading">
            <span className="orders-spinner" />
            <p>Loading your orders...</p>
          </div>
        </div>
      </main>
    )
  }

  if (error) {
    return (
      <main className="orders-page">
        <div className="orders-container">
          <div className="orders-error">
            <div className="orders-error-icon">!</div>

            <div>
              <h2>We couldn't load your orders</h2>
              <p>{error}</p>
            </div>
          </div>
        </div>
      </main>
    )
  }

  return (
    <main className="orders-page">
      <div className="orders-container">

        {/* HERO */}
        <section className="orders-hero">
          <div>
            <p className="orders-eyebrow">
              NOVACART / ORDERS
            </p>

            <h1>
              Your orders<span>.</span>
            </h1>

            <p className="orders-hero-description">
              Track your purchases and review your
              previous orders in one place.
            </p>
          </div>

          <div className="orders-count">
            <strong>{orders.length}</strong>
            <span>
              {orders.length === 1
                ? 'order'
                : 'orders'}
            </span>
          </div>
        </section>

        {orders.length === 0 ? (
          <section className="orders-empty">
            <div className="orders-empty-icon">
              <span>↗</span>
            </div>

            <p className="orders-eyebrow">
              YOUR ORDER HISTORY
            </p>

            <h2>
              Nothing here yet.
            </h2>

            <p>
              Once you place an order, it will appear
              here so you can easily track it.
            </p>

            <Link
              to="/products"
              className="orders-primary-button"
            >
              Start shopping
              <span>→</span>
            </Link>
          </section>
        ) : (
          <>
            {/* ORDER LIST HEADER */}
            <div className="orders-section-header">
              <div>
                <h2>Order history</h2>
                <p>
                  {orders.length}{' '}
                  {orders.length === 1
                    ? 'order'
                    : 'orders'}{' '}
                  placed
                </p>
              </div>

              <Link
                to="/products"
                className="orders-continue-link"
              >
                Continue shopping
                <span>→</span>
              </Link>
            </div>

            {/* ORDERS */}
            <section className="orders-list">
              {orders.map((order) => (
                <article
                  className="order-card"
                  key={order.id}
                >
                  <div className="order-card-top">

                    <div className="order-identity">
                      <p className="order-label">
                        ORDER
                      </p>

                      <h3>
                        #{order.id}
                      </h3>
                    </div>

                    <span
                      className={`order-status ${getStatusClass(
                        order.status
                      )}`}
                    >
                      <span className="order-status-dot" />
                      {order.status}
                    </span>
                  </div>

                  <div className="order-card-divider" />

                  <div className="order-card-info">

                    <div className="order-info-item">
                      <span className="order-info-label">
                        Placed on
                      </span>

                      <strong>
                        {formatDate(order.createdAt)}
                      </strong>
                    </div>

                    <div className="order-info-item">
                      <span className="order-info-label">
                        Total
                      </span>

                      <strong className="order-total">
                        ₹{formatCurrency(
                          order.totalAmount
                        )}
                      </strong>
                    </div>

                    <div className="order-info-item order-action">
                      <Link
                        to={`/orders/${order.id}`}
                        className="order-view-button"
                      >
                        View order
                        <span>→</span>
                      </Link>
                    </div>

                  </div>
                </article>
              ))}
            </section>

            {/* BOTTOM PROMO */}
            <section className="orders-explore">
              <div>
                <p className="orders-eyebrow">
                  KEEP EXPLORING
                </p>

                <h2>
                  Looking for something
                  <br />
                  new?
                </h2>
              </div>

              <Link
                to="/products"
                className="orders-explore-button"
              >
                Browse products
                <span>→</span>
              </Link>
            </section>
          </>
        )}
      </div>
    </main>
  )
}

export default OrdersPage