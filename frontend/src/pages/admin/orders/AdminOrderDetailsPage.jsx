import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import api from '../../../api/axios'

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
      <h1>Admin Order Details</h1>

      {error && <p>{error}</p>}

      {success && <p>{success}</p>}

      <h2>Order #{order.id}</h2>

      <p>
        User ID: {order.userId}
      </p>

      <p>
        Current Status: {order.status}
      </p>

      <p>
        Total: ₹{order.totalAmount}
      </p>

      <p>
        Created At: {order.createdAt}
      </p>

      <section>
        <h2>Update Order Status</h2>

        <select
          value={selectedStatus}
          onChange={(event) =>
            setSelectedStatus(event.target.value)
          }
          disabled={updating}
        >
          <option value="PENDING">PENDING</option>
          <option value="CONFIRMED">CONFIRMED</option>
          <option value="SHIPPED">SHIPPED</option>
          <option value="DELIVERED">DELIVERED</option>
          <option value="CANCELLED">CANCELLED</option>
        </select>

        <button
          type="button"
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
      </section>

      <section>
        <h2>Items</h2>

        {order.items.map((item) => (
          <div key={item.id}>
            <h3>{item.productName}</h3>

            <p>
              Product ID: {item.productId}
            </p>

            <p>
              Price: ₹{item.price}
            </p>

            <p>
              Quantity: {item.quantity}
            </p>

            <p>
              Subtotal: ₹{item.subtotal}
            </p>
          </div>
        ))}
      </section>

      <section>
        <h2>Shipping Address</h2>

        <p>
          <strong>{order.shippingFullName}</strong>
        </p>

        <p>{order.shippingPhone}</p>

        <p>{order.shippingAddressLine}</p>

        <p>
          {order.shippingCity}, {order.shippingState}
        </p>

        <p>
          {order.shippingPostalCode}, {order.shippingCountry}
        </p>
      </section>

      <Link to="/admin/orders">
        Back to Admin Orders
      </Link>
    </div>
  )
}

export default AdminOrderDetailsPage