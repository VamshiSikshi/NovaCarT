import { useEffect, useState } from 'react'
import api from '../../api/axios'

function CartPage() {
  const [cart, setCart] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const updateQuantity = async (itemId, quantity) => {
    try {
      const response = await api.put(
        `/api/cart/items/${itemId}`,
        {
          quantity,
        }
      )

      setCart(response.data)
      setError('')
    } catch (error) {
      setError(
        error.response?.data?.message ||
        'Failed to update quantity.'
      )
    }
  }

  const removeItem = async (itemId) => {
    try {
      const response = await api.delete(
        `/api/cart/items/${itemId}`
      )

      setCart(response.data)
      setError('')
    } catch (error) {
      setError(
        error.response?.data?.message ||
        'Failed to remove item.'
      )
    }
  }

  useEffect(() => {
    const fetchCart = async () => {
      try {
        const response = await api.get('/api/cart')

        setCart(response.data)
      } catch (error) {
        setError('Failed to load cart.')
      } finally {
        setLoading(false)
      }
    }

    fetchCart()
  }, [])

  if (loading) {
    return <p>Loading cart...</p>
  }

  if (error) {
    return <p>{error}</p>
  }

  if (!cart) {
    return <p>Cart not found.</p>
  }

  return (
    <div>
      <h1>My Cart</h1>

      {cart.items.length === 0 ? (
        <p>Your cart is empty.</p>
      ) : (
        <div>
          {cart.items.map((item) => (
            <div key={item.id}>
              <h2>{item.productName}</h2>

              <p>Price: ₹{item.price}</p>

              <div>
                <button
                  type="button"
                  disabled={item.quantity <= 1}
                  onClick={() =>
                    updateQuantity(
                      item.id,
                      item.quantity - 1
                    )
                  }
                >
                  -
                </button>

                <span>
                  {' '}
                  {item.quantity}
                  {' '}
                </span>

                <button
                  type="button"
                  onClick={() =>
                    updateQuantity(
                      item.id,
                      item.quantity + 1
                    )
                  }
                >
                  +
                </button>
              </div>

              <p>Subtotal: ₹{item.subtotal}</p>

              <button
                    type="button"
                    onClick={() => removeItem(item.id)}
                  >
                    Remove
              </button>
            </div>
          ))}
        </div>
      )}

      <h2>Total: ₹{cart.total}</h2>
    </div>
  )
}

export default CartPage