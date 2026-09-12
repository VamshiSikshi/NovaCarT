import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import api from '../../api/axios'
import '../../styles/product-details.css'

function ProductDetailsPage() {
  const { productId } = useParams()
  const navigate = useNavigate()

  const [product, setProduct] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [quantity, setQuantity] = useState(1)
  const [adding, setAdding] = useState(false)
  const [buying, setBuying] = useState(false)

  const [message, setMessage] = useState('')
  const [messageType, setMessageType] = useState('success')

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true)
        setError('')

        const response = await api.get(
          `/api/products/${productId}`
        )

        setProduct(response.data)
      } catch (error) {
        setError(
          error.response?.data?.message ||
            'Unable to load this product.'
        )
      } finally {
        setLoading(false)
      }
    }

    fetchProduct()
  }, [productId])

  const showMessage = (text, type = 'success') => {
    setMessage(text)
    setMessageType(type)
  }

  const increaseQuantity = () => {
    setQuantity((current) => current + 1)
  }

  const decreaseQuantity = () => {
    setQuantity((current) => Math.max(1, current - 1))
  }

  const handleQuantityChange = (event) => {
    const value = Number(event.target.value)

    if (!Number.isFinite(value)) {
      return
    }

    setQuantity(Math.max(1, value))
  }

  const handleAddToCart = async () => {
    setAdding(true)
    setMessage('')

    try {
      await api.post('/api/cart/items', {
        productId: product.id,
        quantity,
      })

      showMessage('Product added to your cart.')
    } catch (error) {
      showMessage(
        error.response?.data?.message ||
          'Unable to add this product to cart.',
        'error'
      )
    } finally {
      setAdding(false)
    }
  }

  const handleBuyNow = async () => {
    setBuying(true)
    setMessage('')

    try {
      await api.post('/api/cart/items', {
        productId: product.id,
        quantity,
      })

      navigate('/checkout')
    } catch (error) {
      showMessage(
        error.response?.data?.message ||
          'Unable to continue to checkout.',
        'error'
      )

      setBuying(false)
    }
  }

  if (loading) {
    return (
      <main className="product-details-page">
        <div className="product-details-shell">
          <div className="product-details-skeleton">
            <div className="skeleton-image" />

            <div className="skeleton-content">
              <span />
              <span />
              <span />
              <span />
              <span />
            </div>
          </div>
        </div>
      </main>
    )
  }

  if (error) {
    return (
      <main className="product-details-page">
        <div className="product-details-shell">
          <div className="product-error-state">
            <div className="error-icon">!</div>

            <p className="product-eyebrow">
              PRODUCT ERROR
            </p>

            <h1>We couldn't load this product.</h1>

            <p>{error}</p>

            <Link
              to="/products"
              className="product-secondary-button"
            >
              ← Back to products
            </Link>
          </div>
        </div>
      </main>
    )
  }

  if (!product) {
    return (
      <main className="product-details-page">
        <div className="product-details-shell">
          <div className="product-error-state">
            <div className="error-icon">?</div>

            <p className="product-eyebrow">
              PRODUCT NOT FOUND
            </p>

            <h1>This product doesn't exist.</h1>

            <Link
              to="/products"
              className="product-secondary-button"
            >
              Browse products
            </Link>
          </div>
        </div>
      </main>
    )
  }

  const price = Number(product.price || 0)
  const originalPrice = Number(product.originalPrice || 0)

  const hasDiscount =
    originalPrice > 0 &&
    originalPrice > price

  const discountPercentage = hasDiscount
    ? Math.round(
        ((originalPrice - price) / originalPrice) * 100
      )
    : 0

  const fallbackImage = `https://placehold.co/900x900/f5f2fb/7135e8?text=${encodeURIComponent(
    product.name
  )}`

  const imageUrl =
    product.imageUrl &&
    product.imageUrl.startsWith('http')
      ? product.imageUrl
      : fallbackImage

  return (
    <main className="product-details-page">
      <div className="product-details-shell">

        {/* Breadcrumb */}
        <nav className="product-breadcrumb">
          <Link to="/">Home</Link>
          <span>/</span>
          <Link to="/products">Products</Link>
          <span>/</span>
          <span>{product.name}</span>
        </nav>

        {/* Main Product */}
        <section className="product-main">

          {/* Product Image */}
          <div className="product-visual-column">
            <div className="product-image-area">

              {hasDiscount && (
                <span className="product-sale-badge">
                  {discountPercentage}% OFF
                </span>
              )}

              <span className="product-image-number">
                01
              </span>

              <img
                className="product-main-image"
                src={imageUrl}
                alt={product.name}
                onError={(event) => {
                  event.currentTarget.src = fallbackImage
                }}
              />

              <div className="product-image-caption">
                <span>NovaCart</span>
                <span>Product view</span>
              </div>
            </div>

            <p className="product-image-hint">
              Hover to explore product
            </p>
          </div>

          {/* Product Information */}
          <div className="product-info">

            <div className="product-info-topline">
              <span className="product-category-label">
                {product.categoryName || 'Collection'}
              </span>

              <span className="product-sku">
                SKU #{product.id}
              </span>
            </div>

            <h1 className="product-title">
              {product.name}
            </h1>

            <p className="product-short-description">
              {product.description}
            </p>

            {/* Price */}
            <div className="product-price-section">
              <div className="product-price-line">
                <span className="product-current-price">
                  ₹{price.toLocaleString('en-IN')}
                </span>

                {hasDiscount && (
                  <span className="product-original-price">
                    ₹{originalPrice.toLocaleString('en-IN')}
                  </span>
                )}

                {hasDiscount && (
                  <span className="product-discount-label">
                    Save {discountPercentage}%
                  </span>
                )}
              </div>

              <p className="product-price-note">
                Inclusive of all applicable taxes
              </p>
            </div>

            <div className="product-divider" />

            {/* Availability */}
            <div className="product-availability">
              <span className="availability-icon">
                ✓
              </span>

              <div>
                <strong>
                  {product.stock > 0
                    ? 'Available'
                    : 'Currently unavailable'}
                </strong>

                <span>
                  {product.stock > 0
                    ? 'Ready to order'
                    : 'Check back later'}
                </span>
              </div>
            </div>

            {/* Quantity */}
            {product.stock > 0 && (
              <div className="product-purchase-row">
                <div className="quantity-section">
                  <span className="quantity-label">
                    Quantity
                  </span>

                  <div className="quantity-control">
                    <button
                      type="button"
                      onClick={decreaseQuantity}
                      disabled={quantity <= 1}
                      aria-label="Decrease quantity"
                    >
                      −
                    </button>

                    <input
                      type="number"
                      min="1"
                      value={quantity}
                      onChange={handleQuantityChange}
                      aria-label="Product quantity"
                    />

                    <button
                      type="button"
                      onClick={increaseQuantity}
                      aria-label="Increase quantity"
                    >
                      +
                    </button>
                  </div>
                </div>
              </div>
            )}

            {/* Actions */}
            <div className="product-actions">

              <button
                type="button"
                className="add-cart-button"
                onClick={handleAddToCart}
                disabled={
                  adding ||
                  buying ||
                  product.stock === 0
                }
              >
                <span>
                  {adding
                    ? 'Adding...'
                    : 'Add to Cart'}
                </span>

                <span className="action-arrow">
                  →
                </span>
              </button>

              <button
                type="button"
                className="buy-now-button"
                onClick={handleBuyNow}
                disabled={
                  adding ||
                  buying ||
                  product.stock === 0
                }
              >
                <span>
                  {buying
                    ? 'Opening checkout...'
                    : 'Buy Now'}
                </span>

                <span className="action-arrow">
                  ↗
                </span>
              </button>
            </div>

            {/* Feedback */}
            {message && (
              <div
                className={`product-message ${messageType}`}
              >
                <span>
                  {messageType === 'success'
                    ? '✓'
                    : '!'}
                </span>

                {message}
              </div>
            )}

            {/* Trust Cards */}
            <div className="product-trust-grid">

              <div className="product-trust-card">
                <span className="trust-icon">✓</span>

                <div>
                  <strong>Quality products</strong>
                  <span>
                    Carefully selected for NovaCart
                  </span>
                </div>
              </div>

              <div className="product-trust-card">
                <span className="trust-icon">↻</span>

                <div>
                  <strong>Easy ordering</strong>
                  <span>
                    Simple shopping experience
                  </span>
                </div>
              </div>

              <div className="product-trust-card">
                <span className="trust-icon">◇</span>

                <div>
                  <strong>Secure checkout</strong>
                  <span>
                    Protected payment flow
                  </span>
                </div>
              </div>

            </div>
          </div>
        </section>

        {/* Details */}
        <section className="product-details-section">

          <div className="details-heading">
            <div>
              <p className="product-eyebrow">
                01 / DETAILS
              </p>

              <h2>
                Everything you need
                <br />
                to know.
              </h2>
            </div>

            <div className="details-introduction">
              <p>
                {product.description}
              </p>

              <p>
                Discover more from the NovaCart
                collection and find products designed
                around the way you live, work and shop.
              </p>
            </div>
          </div>

          <div className="product-specification-card">

            <div className="specification-header">
              <div>
                <span>PRODUCT INFORMATION</span>
                <h3>Product details</h3>
              </div>

              <span className="specification-number">
                #{product.id}
              </span>
            </div>

            <div className="specification-grid">

              <div className="specification-item">
                <span>Product</span>
                <strong>{product.name}</strong>
              </div>

              <div className="specification-item">
                <span>Category</span>
                <strong>
                  {product.categoryName || '—'}
                </strong>
              </div>

              <div className="specification-item">
                <span>Current price</span>
                <strong>
                  ₹{price.toLocaleString('en-IN')}
                </strong>
              </div>

              <div className="specification-item">
                <span>Availability</span>
                <strong>
                  {product.stock > 0
                    ? 'Available'
                    : 'Unavailable'}
                </strong>
              </div>

            </div>

            <div className="description-block">
              <span>DESCRIPTION</span>

              <p>{product.description}</p>
            </div>
          </div>
        </section>

        {/* Continue Shopping */}
        <section className="product-bottom-cta">
          <div>
            <p className="product-eyebrow">
              NOVACART COLLECTION
            </p>

            <h2>
              Find something else
              <br />
              you'll love.
            </h2>
          </div>

          <Link
            to="/products"
            className="product-explore-button"
          >
            <span>Explore products</span>
            <span>→</span>
          </Link>
        </section>

      </div>
    </main>
  )
}

export default ProductDetailsPage