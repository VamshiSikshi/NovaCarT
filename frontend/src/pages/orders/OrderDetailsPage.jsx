import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import api from '../../api/axios'

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

  if (loading) {
    return <p>Loading order...</p>
  }

  if (error && !order) {
    return <p>{error}</p>
  }

  if (!order) {
    return <p>Order not found.</p>
  }

  return (
    <div>
      <h1>Order Details</h1>

      {error && <p>{error}</p>}

      <section>
        <h2>Order #{order.id}</h2>

        <p>Status: {order.status}</p>

        <p>
          Total: ₹{order.totalAmount}
        </p>

        <p>
          Created At: {order.createdAt}
        </p>
      </section>

      <section>
        <h2>Items</h2>

        {order.items.map((item) => (
          <div key={item.id}>
            <h3>{item.productName}</h3>

            <p>Price: ₹{item.price}</p>

            <p>Quantity: {item.quantity}</p>

            <p>Subtotal: ₹{item.subtotal}</p>
          </div>
        ))}
      </section>

      <section>
        <h2>Shipping Address</h2>

        <p>
          <strong>
            {order.shippingFullName}
          </strong>
        </p>

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
      </section>

      <section>
        <h2>Payment</h2>

        {payment ? (
          <>
            <p>Method: {payment.method}</p>

            <p>Status: {payment.status}</p>

            <p>
              Amount: ₹{payment.amount}
            </p>

            {payment.transactionId && (
              <p>
                Transaction ID:{' '}
                {payment.transactionId}
              </p>
            )}
          </>
        ) : (
          <p>No payment information found.</p>
        )}
      </section>

      {order.status === 'PENDING' && (
        <button
          type="button"
          disabled={cancelling}
          onClick={cancelOrder}
        >
          {cancelling
            ? 'Cancelling...'
            : 'Cancel Order'}
        </button>
      )}

      <br />

      <button
        type="button"
        onClick={() => navigate('/orders')}
      >
        Back to Orders
      </button>
    </div>
  )
}

export default OrderDetailsPage