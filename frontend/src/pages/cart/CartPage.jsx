import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../../api/axios'
import '../../styles/cart.css'

function CartPage() {
  const navigate = useNavigate()

  const [cart, setCart] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [updatingItem, setUpdatingItem] = useState(null)
  const [removingItem, setRemovingItem] = useState(null)

  useEffect(() => {
    const fetchCart = async () => {
      try {
        const response = await api.get('/api/cart')
        setCart(response.data)
      } catch (error) {
        setError(
          error.response?.data?.message ||
            'Unable to load your cart.'
        )
      } finally {
        setLoading(false)
      }
    }

    fetchCart()
  }, [])

  const updateQuantity = async (itemId, quantity) => {
    if (quantity < 1) {
      return
    }

    setUpdatingItem(itemId)
    setError('')

    try {
      const response = await api.put(
        `/api/cart/items/${itemId}`,
        {
          quantity,
        }
      )

      setCart(response.data)
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to update quantity.'
      )
    } finally {
      setUpdatingItem(null)
    }
  }

  const removeItem = async (itemId) => {
    setRemovingItem(itemId)
    setError('')

    try {
      const response = await api.delete(
        `/api/cart/items/${itemId}`
      )

      setCart(response.data)
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to remove item.'
      )
    } finally {
      setRemovingItem(null)
    }
  }

  const formatCurrency = (value) =>
    Number(value || 0).toLocaleString('en-IN')

  if (loading) {
    return (
      <main className="cart-page">
        <div className="cart-container">
          <div className="cart-loading">
            <span className="cart-loading-spinner" />
            <p>Loading your cart...</p>
          </div>
        </div>
      </main>
    )
  }

  if (!cart) {
    return (
      <main className="cart-page">
        <div className="cart-container">
          <div className="cart-error-state">
            <div className="cart-state-icon">!</div>
            <h1>Something went wrong</h1>
            <p>{error || 'Cart not found.'}</p>

            <Link
              to="/products"
              className="cart-primary-button"
            >
              Continue shopping
            </Link>
          </div>
        </div>
      </main>
    )
  }

  const itemCount = cart.items.reduce(
    (total, item) => total + item.quantity,
    0
  )

  const isEmpty = cart.items.length === 0

  return (
    <main className="cart-page">
      <div className="cart-container">

        {/* Header */}
        <header className="cart-page-header">
          <div>
            <span className="cart-eyebrow">
              NOVACART / CART
            </span>

            <h1>Your shopping cart.</h1>

            {!isEmpty && (
              <p>
                {itemCount}{' '}
                {itemCount === 1 ? 'item' : 'items'} ready
                for checkout.
              </p>
            )}
          </div>

          {!isEmpty && (
            <Link
              to="/products"
              className="cart-continue-link"
            >
              ← Continue shopping
            </Link>
          )}
        </header>

        {/* Error */}
        {error && (
          <div className="cart-alert">
            <span>!</span>
            <p>{error}</p>

            <button
              type="button"
              onClick={() => setError('')}
            >
              ×
            </button>
          </div>
        )}

        {/* Empty Cart */}
        {isEmpty ? (
          <section className="cart-empty">
            <div className="cart-empty-icon">
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="1.7"
              >
                <path d="M6 8h12l1 12H5L6 8Z" />
                <path d="M9 8a3 3 0 0 1 6 0" />
              </svg>
            </div>

            <span className="cart-eyebrow">
              YOUR CART IS EMPTY
            </span>

            <h2>Nothing here yet.</h2>

            <p>
              Discover something you love and add it
              to your cart.
            </p>

            <Link
              to="/products"
              className="cart-primary-button"
            >
              Explore products
              <span>→</span>
            </Link>
          </section>
        ) : (
          <div className="cart-layout">

            {/* Cart Items */}
            <section className="cart-items-section">
              <div className="cart-section-heading">
                <div>
                  <span className="cart-section-number">
                    01
                  </span>

                  <h2>Items in your cart</h2>
                </div>

                <span className="cart-item-count">
                  {itemCount}{' '}
                  {itemCount === 1 ? 'item' : 'items'}
                </span>
              </div>

              <div className="cart-items">
                {cart.items.map((item) => {
                  const fallbackImage =
                    `https://placehold.co/500x500?text=${encodeURIComponent(
                      item.productName
                    )}`

                  const imageUrl =
                    item.imageUrl || fallbackImage

                  const isUpdating =
                    updatingItem === item.id

                  const isRemoving =
                    removingItem === item.id

                  return (
                    <article
                      className={`cart-item ${
                        isRemoving
                          ? 'cart-item-removing'
                          : ''
                      }`}
                      key={item.id}
                    >
                      {/* Product Image */}
                      <Link
                        to={`/products/${
                          item.productId || ''
                        }`}
                        className="cart-item-image"
                      >
                        <img
                          src={imageUrl}
                          alt={item.productName}
                          onError={(event) => {
                            event.currentTarget.src =
                              fallbackImage
                          }}
                        />
                      </Link>

                      {/* Product Information */}
                      <div className="cart-item-info">
                        <Link
                          to={`/products/${
                            item.productId || ''
                          }`}
                          className="cart-item-name"
                        >
                          {item.productName}
                        </Link>

                        <span className="cart-item-unit-price">
                          ₹{formatCurrency(item.price)}{' '}
                          <span>per item</span>
                        </span>

                        <div className="cart-item-actions">

                          {/* Quantity */}
                          <div className="cart-quantity">
                            <button
                              type="button"
                              aria-label="Decrease quantity"
                              disabled={
                                item.quantity <= 1 ||
                                isUpdating ||
                                isRemoving
                              }
                              onClick={() =>
                                updateQuantity(
                                  item.id,
                                  item.quantity - 1
                                )
                              }
                            >
                              −
                            </button>

                            <span>
                              {isUpdating
                                ? '...'
                                : item.quantity}
                            </span>

                            <button
                              type="button"
                              aria-label="Increase quantity"
                              disabled={
                                isUpdating ||
                                isRemoving
                              }
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

                          <button
                            type="button"
                            className="cart-remove-button"
                            disabled={
                              isUpdating ||
                              isRemoving
                            }
                            onClick={() =>
                              removeItem(item.id)
                            }
                          >
                            {isRemoving
                              ? 'Removing...'
                              : 'Remove'}
                          </button>
                        </div>
                      </div>

                      {/* Item Total */}
                      <div className="cart-item-total">
                        <span>Total</span>

                        <strong>
                          ₹{formatCurrency(item.subtotal)}
                        </strong>
                      </div>
                    </article>
                  )
                })}
              </div>

              {/* Continue Shopping */}
              <Link
                to="/products"
                className="cart-back-shopping"
              >
                <span>←</span>
                Continue shopping
              </Link>
            </section>

            {/* Order Summary */}
            <aside className="cart-summary">

              <div className="cart-summary-top">
                <span className="cart-section-number">
                  02
                </span>

                <h2>Order summary</h2>
              </div>

              <div className="cart-summary-lines">
                <div>
                  <span>
                    Subtotal
                  </span>

                  <strong>
                    ₹{formatCurrency(cart.total)}
                  </strong>
                </div>

                <div>
                  <span>Shipping</span>

                  <strong className="cart-free">
                    FREE
                  </strong>
                </div>
              </div>

              <div className="cart-summary-divider" />

              <div className="cart-summary-total">
                <div>
                  <span>Total</span>
                  <small>Inclusive of applicable taxes</small>
                </div>

                <strong>
                  ₹{formatCurrency(cart.total)}
                </strong>
              </div>

              <button
                type="button"
                className="cart-checkout-button"
                onClick={() => navigate('/checkout')}
              >
                <span>Proceed to checkout</span>
                <span>→</span>
              </button>

              <div className="cart-trust-list">
                <div className="cart-trust-item">
                  <span className="cart-trust-icon">
                    ✓
                  </span>

                  <div>
                    <strong>Secure checkout</strong>
                    <small>
                      Your payment information is protected.
                    </small>
                  </div>
                </div>

                <div className="cart-trust-item">
                  <span className="cart-trust-icon">
                    ↻
                  </span>

                  <div>
                    <strong>Easy ordering</strong>
                    <small>
                      Simple and transparent shopping.
                    </small>
                  </div>
                </div>

                <div className="cart-trust-item">
                  <span className="cart-trust-icon">
                    ◇
                  </span>

                  <div>
                    <strong>Quality products</strong>
                    <small>
                      Carefully selected for NovaCart.
                    </small>
                  </div>
                </div>
              </div>
            </aside>
          </div>
        )}
      </div>
    </main>
  )
}

export default CartPage