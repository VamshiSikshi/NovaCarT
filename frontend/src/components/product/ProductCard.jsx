import { Link } from 'react-router-dom'

function ProductCard({ product }) {
  return (
    <div>
      <h2>{product.name}</h2>

      <p>{product.description}</p>

      <p>Category: {product.categoryName}</p>

      <p>Price: ₹{product.price}</p>

      <p>Stock: {product.stock}</p>

      <Link to={`/products/${product.id}`}>
        View Details
      </Link>
    </div>
  )
}

export default ProductCard