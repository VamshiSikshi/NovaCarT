import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../api/axios'
import ProductCard from '../components/product/ProductCard'

function HomePage() {
  const navigate = useNavigate()

  const [products, setProducts] = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const loadCatalog = async () => {
      try {
        setLoading(true)
        setError('')

        const [productsResponse, categoriesResponse] =
          await Promise.all([
            api.get('/api/products?page=0&size=100'),
            api.get('/api/categories'),
          ])

        setProducts(productsResponse.data.content || [])
        setCategories(categoriesResponse.data || [])
      } catch (err) {
        console.error('Failed to load homepage catalog:', err)
        setError('Unable to load the catalog. Please try again.')
      } finally {
        setLoading(false)
      }
    }

    loadCatalog()
  }, [])

  const discountedProducts = useMemo(() => {
    return products
      .filter(
        (product) =>
          Number(product.originalPrice) > Number(product.price)
      )
      .slice(0, 8)
  }, [products])

  const featuredProducts = useMemo(() => {
    return products.slice(0, 8)
  }, [products])

  const handleShopNow = () => {
    navigate('/products')
  }

  if (loading) {
    return (
      <main className="home-page">
        <div className="catalog-state">
          <div className="loading-spinner" />
          <p>Loading NovaCart...</p>
        </div>
      </main>
    )
  }

  if (error) {
    return (
      <main className="home-page">
        <div className="catalog-state">
          <h2>Something went wrong</h2>
          <p>{error}</p>

          <button
            type="button"
            onClick={() => window.location.reload()}
            className="primary-button"
          >
            Try Again
          </button>
        </div>
      </main>
    )
  }

  return (
    <main className="home-page">

      {/* =========================
          HERO
      ========================= */}

      <section className="hero-section">
        <div className="hero-content">
          <span className="hero-eyebrow">
            WELCOME TO NOVACART
          </span>

          <h1>
            Everything you need.
            <span> All in one cart.</span>
          </h1>

          <p>
            Discover carefully selected products across technology,
            fashion, home, audio and everyday essentials.
          </p>

          <div className="hero-actions">
            <button
              type="button"
              className="primary-button"
              onClick={handleShopNow}
            >
              Shop Now
            </button>

            <a
              href="#deals"
              className="secondary-button"
            >
              Explore Deals
            </a>
          </div>
        </div>

        <div className="hero-visual">
          <div className="hero-card hero-card-main">
            <span>NEW</span>
            <strong>NovaCart</strong>
            <p>Smart shopping starts here.</p>
          </div>

          <div className="hero-floating-card">
            <span>UP TO</span>
            <strong>30% OFF</strong>
            <small>Selected products</small>
          </div>
        </div>
      </section>

      {/* =========================
          CATEGORIES
      ========================= */}

      <section className="home-section">
        <div className="section-heading">
          <div>
            <span className="section-eyebrow">
              SHOP BY CATEGORY
            </span>

            <h2>Find what you need</h2>
          </div>

          <Link to="/products" className="section-link">
            View all →
          </Link>
        </div>

        <div className="category-grid">
          {categories.map((category) => (
            <Link
              key={category.id}
              to={`/products?categoryId=${category.id}`}
              className="category-card"
            >
              <div className="category-icon">
                {getCategoryIcon(category.name)}
              </div>

              <div>
                <h3>{category.name}</h3>
                <p>{category.description}</p>
              </div>

              <span className="category-arrow">→</span>
            </Link>
          ))}
        </div>
      </section>

      {/* =========================
          DEALS
      ========================= */}

      {discountedProducts.length > 0 && (
        <section
          className="home-section deals-section"
          id="deals"
        >
          <div className="section-heading">
            <div>
              <span className="section-eyebrow">
                TODAY'S DEALS
              </span>

              <h2>Worth adding to your cart</h2>
            </div>

            <Link to="/products" className="section-link">
              See all deals →
            </Link>
          </div>

          <div className="product-grid">
            {discountedProducts.map((product) => (
              <ProductCard
                key={product.id}
                product={product}
              />
            ))}
          </div>
        </section>
      )}

      {/* =========================
          FEATURED PRODUCTS
      ========================= */}

      <section className="home-section">
        <div className="section-heading">
          <div>
            <span className="section-eyebrow">
              FEATURED
            </span>

            <h2>Popular picks</h2>
          </div>

          <Link to="/products" className="section-link">
            Browse products →
          </Link>
        </div>

        {featuredProducts.length > 0 ? (
          <div className="product-grid">
            {featuredProducts.map((product) => (
              <ProductCard
                key={product.id}
                product={product}
              />
            ))}
          </div>
        ) : (
          <div className="empty-catalog">
            <h3>No products available</h3>
            <p>Our catalog is being updated.</p>
          </div>
        )}
      </section>

      {/* =========================
          TRUST / CTA
      ========================= */}

      <section className="bottom-cta">
        <div>
          <span className="section-eyebrow">
            NOVACART
          </span>

          <h2>Ready to find your next favorite?</h2>

          <p>
            Explore the complete NovaCart collection.
          </p>
        </div>

        <Link
          to="/products"
          className="primary-button"
        >
          Explore Products
        </Link>
      </section>

    </main>
  )
}

function getCategoryIcon(name) {
  const normalizedName = name.toLowerCase()

  if (normalizedName.includes('computer')) return '⌨'
  if (normalizedName.includes('audio')) return '♫'
  if (normalizedName.includes('accessor')) return '⚡'
  if (normalizedName.includes('wearable')) return '◉'
  if (normalizedName.includes('home')) return '⌂'
  if (normalizedName.includes('fashion')) return '◇'
  if (normalizedName.includes('electronic')) return '▣'

  return '✦'
}

export default HomePage