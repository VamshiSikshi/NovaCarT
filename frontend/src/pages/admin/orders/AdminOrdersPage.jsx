import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../../../api/axios'

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
    return <p>Loading admin orders...</p>
  }

  if (error) {
    return <p>{error}</p>
  }

  return (
    <div>
      <h1>Admin Orders</h1>

      <div>
        <label>Status: </label>

        <select
          value={status}
          onChange={(event) => setStatus(event.target.value)}
        >
          <option value="">ALL</option>
          <option value="PENDING">PENDING</option>
          <option value="CONFIRMED">CONFIRMED</option>
          <option value="SHIPPED">SHIPPED</option>
          <option value="DELIVERED">DELIVERED</option>
          <option value="CANCELLED">CANCELLED</option>
        </select>
      </div>

      {orders.length === 0 ? (
        <p>No orders found.</p>
      ) : (
        <div>
          {orders.map((order) => (
            <div key={order.orderId}>
              <h2>Order #{order.orderId}</h2>

              <p>
                Customer: {order.username}
              </p>

              <p>
                User ID: {order.userId}
              </p>

              <p>
                Status: {order.status}
              </p>

              <p>
                Total: ₹{order.totalAmount}
              </p>

              <p>
                Created At: {order.createdAt}
              </p>

              <Link to={`/admin/orders/${order.orderId}`}>
                View Order
              </Link>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default AdminOrdersPage