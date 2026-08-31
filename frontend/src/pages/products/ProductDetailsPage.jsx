import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import api from '../../api/axios'

function ProductDetailsPage() {
  const { productId } = useParams()

  const [product, setProduct] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [quantity, setQuantity] = useState(1)
  const [adding, setAdding] = useState(false)
  const [message, setMessage] = useState('')

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const response = await api.get(
          `/api/products/${productId}`
        )

        setProduct(response.data)
      } catch (error) {
        setError('Failed to load product.')
      } finally {
        setLoading(false)
      }
    }

    fetchProduct()
  }, [productId])

  const handleAddToCart = async () => {
    setAdding(true)
    setMessage('')

    try {
      await api.post('/api/cart/items', {
        productId: product.id,
        quantity,
      })

      setMessage('Product added to cart.')
    } catch (error) {
      setMessage(
        error.response?.data?.message ||
        'Failed to add product to cart.'
      )
    } finally {
      setAdding(false)
    }
  }

  if (loading) {
    return <p>Loading product...</p>
  }

  if (error) {
    return <p>{error}</p>
  }

  if (!product) {
    return <p>Product not found.</p>
  }

  return (
    <div>
      <h1>{product.name}</h1>

      <p>{product.description}</p>

      <p>Category: {product.categoryName}</p>

      <p>Price: ₹{product.price}</p>

      <p>Stock: {product.stock}</p>

      {product.imageUrl &&
        product.imageUrl.startsWith('http') && (
          <img
            src={product.imageUrl}
            alt={product.name}
            width="300"
          />
        )}

      <div>
        <label htmlFor="quantity">
          Quantity:
        </label>

        <input
          id="quantity"
          type="number"
          min="1"
          max={product.stock}
          value={quantity}
          onChange={(event) =>
            setQuantity(Number(event.target.value))
          }
        />
      </div>

      <button
        type="button"
        onClick={handleAddToCart}
        disabled={adding || product.stock === 0}
      >
        {adding ? 'Adding...' : 'Add to Cart'}
      </button>

      {message && <p>{message}</p>}
    </div>
  )
}

export default ProductDetailsPage