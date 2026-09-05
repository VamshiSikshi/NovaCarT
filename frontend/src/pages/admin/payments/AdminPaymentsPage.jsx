import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../../../api/axios'
import '../../../styles/admin/admin-payments.css'

const formatCurrency = (amount) =>
  `₹${Number(amount || 0).toLocaleString('en-IN')}`

const formatDate = (date) => {
  if (!date) return '—'

  return new Date(date).toLocaleString('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true,
  })
}

const formatTransactionId = (transactionId) => {
  if (!transactionId) return 'N/A'

  if (transactionId.length <= 18) {
    return transactionId
  }

  return `${transactionId.slice(0, 10)}...${transactionId.slice(-6)}`
}

function AdminPaymentsPage() {
  const [payments, setPayments] = useState([])
  const [status, setStatus] = useState('')
  const [method, setMethod] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const loadPayments = async () => {
      try {
        setLoading(true)
        setError('')

        const params = new URLSearchParams()

        if (status) {
          params.append('status', status)
        }

        if (method) {
          params.append('method', method)
        }

        const queryString = params.toString()

        const url = queryString
          ? `/api/admin/payments?${queryString}`
          : '/api/admin/payments'

        const response = await api.get(url)

        setPayments(response.data.content)
      } catch (error) {
        setError(
          error.response?.data?.message ||
          'Failed to load payments.'
        )
      } finally {
        setLoading(false)
      }
    }

    loadPayments()
  }, [status, method])

  const getStatusClass = (paymentStatus) => {
    switch (paymentStatus) {
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
        <p>Loading payments...</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="admin-page">
        <div className="admin-alert admin-alert-error">
          <span>!</span>
          {error}
        </div>
      </div>
    )
  }

  return (
    <div className="admin-payments-page">

      {/* HEADER */}

      <div className="admin-page-header">
        <div>
          <p className="admin-eyebrow">
            Transactions
          </p>

          <h1>Payments</h1>

          <p className="admin-page-description">
            Monitor payment transactions and their status.
          </p>
        </div>
      </div>

      {/* FILTERS */}

      <div className="admin-toolbar">

        <div className="admin-payment-filters">

          <div className="admin-payment-filter">
            <label htmlFor="payment-status">
              Status
            </label>

            <select
              id="payment-status"
              value={status}
              onChange={(event) =>
                setStatus(event.target.value)
              }
            >
              <option value="">ALL</option>
              <option value="PENDING">PENDING</option>
              <option value="SUCCESS">SUCCESS</option>
              <option value="FAILED">FAILED</option>
            </select>
          </div>

          <div className="admin-payment-filter">
            <label htmlFor="payment-method">
              Method
            </label>

            <select
              id="payment-method"
              value={method}
              onChange={(event) =>
                setMethod(event.target.value)
              }
            >
              <option value="">ALL</option>
              <option value="CARD">CARD</option>
              <option value="UPI">UPI</option>
              <option value="COD">COD</option>
            </select>
          </div>

          {(status || method) && (
            <button
              type="button"
              className="admin-clear-button"
              onClick={() => {
                setStatus('')
                setMethod('')
              }}
            >
              Clear filters
            </button>
          )}

        </div>

      </div>

      {/* PAYMENTS */}

      <section className="admin-table-card admin-payments-list">

        <div className="admin-table-header">
          <h2>Payment Transactions</h2>

          <span>
            {payments.length} payment
            {payments.length !== 1 ? 's' : ''} found
          </span>
        </div>

        {payments.length === 0 ? (
          <div className="admin-empty-state">
            <div className="admin-empty-icon">
              ₹
            </div>

            <strong>No payments found</strong>

            <p>
              Try changing the selected filters.
            </p>
          </div>
        ) : (
          <div className="admin-payment-table">

            {/* TABLE HEADER */}

            <div className="admin-payment-table-header">
              <span>Payment</span>
              <span>Order</span>
              <span>Amount</span>
              <span>Method</span>
              <span>Status</span>
              <span>Transaction ID</span>
              <span>Created</span>
              <span />
            </div>

            {/* ROWS */}

            {payments.map((payment) => (
              <div
                className="admin-payment-row"
                key={payment.id}
              >
                <div className="admin-payment-id">
                  <strong>
                    #{payment.id}
                  </strong>
                </div>

                <div>
                  <Link
                    className="admin-payment-order"
                    to={`/admin/orders/${payment.orderId}`}
                  >
                    #{payment.orderId}
                  </Link>
                </div>

                <div className="admin-payment-amount">
                  {formatCurrency(payment.amount)}
                </div>

                <div>
                  <span className="admin-payment-method">
                    {payment.method}
                  </span>
                </div>

                <div>
                  <span
                    className={getStatusClass(
                      payment.status
                    )}
                  >
                    {payment.status}
                  </span>
                </div>

                <div
                  className="admin-payment-transaction"
                  title={payment.transactionId || 'N/A'}
                >
                  {formatTransactionId(payment.transactionId)}
                </div>

                <div className="admin-payment-date">
                  {formatDate(payment.createdAt)}
                </div>

                <div className="admin-payment-action">
                  <Link
                    to={`/admin/payments/${payment.id}`}
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

export default AdminPaymentsPage