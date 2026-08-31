import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import api from '../../../api/axios'

function AdminPaymentDetailsPage() {
  const { id } = useParams()

  const [payment, setPayment] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const loadPayment = async () => {
      try {
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

  if (loading) {
    return <p>Loading payment...</p>
  }

  if (error) {
    return <p>{error}</p>
  }

  if (!payment) {
    return <p>Payment not found.</p>
  }

  return (
    <div>
      <h1>Payment Details</h1>

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

      <Link to="/admin/payments">
        Back to Payments
      </Link>
    </div>
  )
}

export default AdminPaymentDetailsPage