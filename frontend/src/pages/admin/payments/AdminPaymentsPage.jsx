import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../../../api/axios'

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

  if (loading) {
    return <p>Loading payments...</p>
  }

  if (error) {
    return <p>{error}</p>
  }

  return (
    <div>
      <h1>Admin Payments</h1>

      <div>
        <label>Status: </label>

        <select
          value={status}
          onChange={(event) => setStatus(event.target.value)}
        >
          <option value="">ALL</option>
          <option value="PENDING">PENDING</option>
          <option value="SUCCESS">SUCCESS</option>
          <option value="FAILED">FAILED</option>
        </select>
      </div>

      <div>
        <label>Method: </label>

        <select
          value={method}
          onChange={(event) => setMethod(event.target.value)}
        >
          <option value="">ALL</option>
          <option value="CARD">CARD</option>
          <option value="UPI">UPI</option>
          <option value="COD">COD</option>
        </select>
      </div>

      {payments.length === 0 ? (
        <p>No payments found.</p>
      ) : (
        <div>
          {payments.map((payment) => (
            <div key={payment.id}>
              <h2>Payment #{payment.id}</h2>

              <p>
                Order ID: {payment.orderId}
              </p>

              <p>
                Amount: ₹{payment.amount}
              </p>

              <p>
                Method: {payment.method}
              </p>

              <p>
                Status: {payment.status}
              </p>

              <p>
                Transaction ID:{' '}
                {payment.transactionId || 'N/A'}
              </p>

              <p>
                Created At: {payment.createdAt}
              </p>

              <Link to={`/admin/payments/${payment.id}`}>
                View Payment
              </Link>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default AdminPaymentsPage