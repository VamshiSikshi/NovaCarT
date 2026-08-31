import { Link } from 'react-router-dom'

function ProductCard({ product }) {
  const originalPrice = Number(product.originalPrice)
  const price = Number(product.price)

  const hasDiscount =
    originalPrice > 0 &&
    price > 0 &&
    originalPrice > price

  const discountPercentage = hasDiscount
    ? Math.round(((originalPrice - price) / originalPrice) * 100)
    : 0

  const fallbackImage = `https://placehold.co/600x600?text=${encodeURIComponent(
    product.name
  )}`

  return (
    <article className="product-card">
      <Link
        to={`/products/${product.id}`}
        className="product-card-image-link"
      >
        <div className="product-card-image">
          {hasDiscount && (
            <span className="product-discount">
              {discountPercentage}% OFF
            </span>
          )}

          <img
            src={product.imageUrl || fallbackImage}
            alt={product.name}
            onError={(event) => {
              event.currentTarget.src = fallbackImage
            }}
          />
        </div>
      </Link>

      <div className="product-card-content">
        <span className="product-category">
          {product.categoryName}
        </span>

        <Link
          to={`/products/${product.id}`}
          className="product-card-title"
        >
          {product.name}
        </Link>

        <p className="product-description">
          {product.description}
        </p>

        <div className="product-price-row">
          <span className="product-price">
            ₹{price.toLocaleString('en-IN')}
          </span>

          {hasDiscount && (
            <span className="product-original-price">
              ₹{originalPrice.toLocaleString('en-IN')}
            </span>
          )}
        </div>

        <div className="product-card-footer">
          <span
            className={
              product.stock > 0
                ? 'product-stock'
                : 'product-stock out-of-stock'
            }
          >
            {product.stock > 0
              ? `${product.stock} in stock`
              : 'Out of stock'}
          </span>

          <Link
            to={`/products/${product.id}`}
            className="product-view-link"
          >
            View Details →
          </Link>
        </div>
      </div>
    </article>
  )
}

export default ProductCard
