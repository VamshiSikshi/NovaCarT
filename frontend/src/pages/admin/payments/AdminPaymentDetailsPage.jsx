import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import api from '../../../api/axios'
import '../../../styles/admin/admin-paymentsDetails.css'

function AdminPaymentDetailsPage() {
  const { id } = useParams()

  const [payment, setPayment] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const loadPayment = async () => {
      try {
        setLoading(true)
        setError('')

        const response = await api.get(
          `/api/admin/payments/${id}`
        )

        setPayment(response.data)
      } catch (error) {
        setError(
          error.response?.data?.message ||
          'Failed to load payment.'
        )
      } finally {
        setLoading(false)
      }
    }

    loadPayment()
  }, [id])

  const formatAmount = (amount) => {
    return Number(amount || 0).toLocaleString('en-IN')
  }

  const formatDate = (date) => {
    if (!date) {
      return 'N/A'
    }

    return new Date(date).toLocaleString('en-IN', {
      dateStyle: 'medium',
      timeStyle: 'short',
    })
  }

  const getStatusClass = (status) => {
    switch (status) {
      case 'SUCCESS':
        return 'admin-payment-status payment-success'

      case 'FAILED':
        return 'admin-payment-status payment-failed'

      case 'PENDING':
        return 'admin-payment-status payment-pending'

      default:
        return 'admin-payment-status'
    }
  }

  if (loading) {
    return (
      <div className="admin-page-state">
        <div className="admin-spinner" />
        <p>Loading payment...</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="admin-payment-details-page">

        <div className="admin-alert admin-alert-error">
          <span>!</span>
          {error}
        </div>

        <Link
          to="/admin/payments"
          className="admin-button secondary"
        >
          ← Back to Payments
        </Link>

      </div>
    )
  }

  if (!payment) {
    return (
      <div className="admin-payment-details-page">

        <div className="admin-empty-state">
          <div className="admin-empty-icon">
            ₹
          </div>

          <strong>Payment not found</strong>

          <p>
            The requested payment could not be found.
          </p>

          <Link
            to="/admin/payments"
            className="admin-button secondary"
          >
            Back to Payments
          </Link>
        </div>

      </div>
    )
  }

  return (
    <div className="admin-payment-details-page">

      {/* HEADER */}

      <div className="admin-page-header">

        <div>
          <Link
            to="/admin/payments"
            className="admin-back-link"
          >
            ← Back to Payments
          </Link>

          <p className="admin-eyebrow">
            Payment Transaction
          </p>

          <div className="admin-payment-details-title">
            <div>
              <h1>
                Payment #{payment.id}
              </h1>

              <p className="admin-page-description">
                Transaction details and payment information.
              </p>
            </div>
          </div>
        </div>

        <span
          className={getStatusClass(payment.status)}
        >
          {payment.status}
        </span>

      </div>

      {/* SUMMARY */}

      <section className="admin-payment-summary-grid">

        <div className="admin-stat-card admin-payment-summary-card">
          <span className="admin-stat-label">
            Payment Amount
          </span>

          <strong>
            ₹{formatAmount(payment.amount)}
          </strong>

          <p>
            Total transaction value
          </p>
        </div>

        <div className="admin-stat-card admin-payment-summary-card">
          <span className="admin-stat-label">
            Payment Method
          </span>

          <strong>
            {payment.method}
          </strong>

          <p>
            Selected payment method
          </p>
        </div>

        <div className="admin-stat-card admin-payment-summary-card">
          <span className="admin-stat-label">
            Order
          </span>

          <strong>
            #{payment.orderId}
          </strong>

          <p>
            Associated order
          </p>
        </div>

      </section>

      {/* PAYMENT INFORMATION */}

      <section className="admin-panel admin-payment-info-panel">

        <div className="admin-panel-header">
          <div>
            <h2>Payment Information</h2>

            <p>
              Core details of this payment.
            </p>
          </div>
        </div>

        <div className="admin-payment-info-grid">

          <div className="admin-payment-info-row">
            <span>Payment ID</span>
            <strong>#{payment.id}</strong>
          </div>

          <div className="admin-payment-info-row">
            <span>Order ID</span>

            <Link
              to={`/admin/orders/${payment.orderId}`}
              className="admin-payment-order"
            >
              #{payment.orderId}
            </Link>
          </div>

          <div className="admin-payment-info-row">
            <span>Amount</span>

            <strong>
              ₹{formatAmount(payment.amount)}
            </strong>
          </div>

          <div className="admin-payment-info-row">
            <span>Method</span>

            <span className="admin-payment-method">
              {payment.method}
            </span>
          </div>

          <div className="admin-payment-info-row">
            <span>Status</span>

            <span
              className={getStatusClass(payment.status)}
            >
              {payment.status}
            </span>
          </div>

          <div className="admin-payment-info-row">
            <span>Created At</span>

            <strong>
              {formatDate(payment.createdAt)}
            </strong>
          </div>

        </div>

      </section>

      {/* TRANSACTION INFORMATION */}

      <section className="admin-panel admin-payment-info-panel">

        <div className="admin-panel-header">
          <div>
            <h2>Transaction Information</h2>

            <p>
              Payment gateway transaction reference.
            </p>
          </div>
        </div>

        <div className="admin-payment-info-grid">

          <div className="admin-payment-info-row">
            <span>Transaction ID</span>

            <span className="admin-payment-transaction">
              {payment.transactionId || 'N/A'}
            </span>
          </div>

          <div className="admin-payment-info-row">
            <span>Payment Status</span>

            <span
              className={getStatusClass(payment.status)}
            >
              {payment.status}
            </span>
          </div>

        </div>

      </section>

      {/* ORDER REFERENCE */}

      <section className="admin-panel">

        <div className="admin-panel-header">
          <div>
            <h2>Order Reference</h2>

            <p>
              View the complete order associated with
              this payment.
            </p>
          </div>
        </div>

        <div className="admin-payment-order-panel">

          <div>
            <strong>
              Order #{payment.orderId}
            </strong>

            <span>
              Open the order to view customer, items,
              shipping and status information.
            </span>
          </div>

          <Link
            to={`/admin/orders/${payment.orderId}`}
            className="admin-button primary"
          >
            View Order →
          </Link>

        </div>

      </section>

    </div>
  )
}

export default AdminPaymentDetailsPage