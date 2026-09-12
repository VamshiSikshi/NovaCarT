import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../../api/axios'
import ProductCard from '../../components/product/ProductCard'
import '../../styles/products.css'

function ProductsPage() {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [keyword, setKeyword] = useState('')
  const [search, setSearch] = useState('')

  const [sort, setSort] = useState('id,desc')

  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)

  const pageSize = 12

  useEffect(() => {
    const loadProducts = async () => {
      setLoading(true)
      setError('')

      try {
        const params = new URLSearchParams({
          page: page.toString(),
          size: pageSize.toString(),
          sort,
        })

        let endpoint = `/api/products?${params.toString()}`

        if (search.trim()) {
          params.set('keyword', search.trim())
          endpoint = `/api/products/search?${params.toString()}`
        }

        const response = await api.get(endpoint)

        setProducts(response.data.content || [])
        setTotalPages(response.data.totalPages || 0)
        setTotalElements(response.data.totalElements || 0)
      } catch (error) {
        setError(
          error.response?.data?.message ||
            'Unable to load products. Please try again.'
        )
      } finally {
        setLoading(false)
      }
    }

    loadProducts()
  }, [page, search, sort])

  const handleSearch = (event) => {
    event.preventDefault()

    setPage(0)
    setSearch(keyword.trim())
  }

  const clearSearch = () => {
    setKeyword('')
    setSearch('')
    setPage(0)
  }

  const handleSort = (event) => {
    setSort(event.target.value)
    setPage(0)
  }

  const goToPage = (pageNumber) => {
    if (pageNumber < 0 || pageNumber >= totalPages) {
      return
    }

    setPage(pageNumber)

    window.scrollTo({
      top: 0,
      behavior: 'smooth',
    })
  }

  const getVisiblePages = () => {
    if (totalPages <= 5) {
      return Array.from(
        { length: totalPages },
        (_, index) => index
      )
    }

    const pages = new Set([
      0,
      totalPages - 1,
      page,
      page - 1,
      page + 1,
    ])

    return Array.from(pages)
      .filter(
        (pageNumber) =>
          pageNumber >= 0 && pageNumber < totalPages
      )
      .sort((a, b) => a - b)
  }

  const visiblePages = getVisiblePages()

  return (
    <main className="products-page">
      <div className="products-container">

        {/* =========================================
            BREADCRUMB
            ========================================= */}

        <nav className="products-breadcrumb">
          <Link to="/">Home</Link>
          <span>/</span>
          <span>Shop</span>
        </nav>

        {/* =========================================
            HERO
            ========================================= */}

        <section className="products-hero">
          <div className="products-hero-content">
            <span className="products-eyebrow">
              NOVACART / SHOP
            </span>

            <h1>
              Shop everything
              <span> you need.</span>
            </h1>

            <p>
              Discover products for everyday life, work,
              and everything in between.
            </p>
          </div>

          <div className="products-hero-stat">
            <strong>{totalElements}</strong>
            <span>products</span>
          </div>
        </section>

        {/* =========================================
            SEARCH
            ========================================= */}

        <section className="products-search-section">
          <form
            className="products-search"
            onSubmit={handleSearch}
          >
            <span
              className="products-search-icon"
              aria-hidden="true"
            >
              ⌕
            </span>

            <input
              type="search"
              value={keyword}
              onChange={(event) =>
                setKeyword(event.target.value)
              }
              placeholder="Search for products..."
              aria-label="Search for products"
            />

            {keyword && (
              <button
                type="button"
                className="products-search-clear"
                onClick={clearSearch}
                aria-label="Clear search"
              >
                ×
              </button>
            )}

            <button
              type="submit"
              className="products-search-submit"
            >
              Search
            </button>
          </form>
        </section>

        {/* =========================================
            SHOP CONTENT
            ========================================= */}

        <section className="products-shop-layout">

          {/* =======================================
              SIDEBAR
              ======================================= */}

          <aside className="products-sidebar">
            <div className="products-sidebar-heading">
              <span>SHOP</span>

              {search && (
                <button
                  type="button"
                  onClick={clearSearch}
                >
                  Clear
                </button>
              )}
            </div>

            <div className="products-filter-section">
              <h3>Browse</h3>

              <div className="products-browse-item active">
                <span>All products</span>
                <strong>{totalElements}</strong>
              </div>

              <p className="products-filter-note">
                More shopping filters will be added
                here as the storefront grows.
              </p>
            </div>

            <div className="products-filter-divider" />

            <div className="products-filter-section">
              <h3>Availability</h3>

              <div className="products-filter-placeholder">
                <span className="filter-placeholder-icon">
                  ✓
                </span>

                <div>
                  <strong>In stock</strong>
                  <small>
                    Availability filtering
                    coming next
                  </small>
                </div>
              </div>
            </div>

            <div className="products-filter-divider" />

            <div className="products-filter-section">
              <h3>Shopping features</h3>

              <div className="products-coming-feature">
                <span>♡</span>
                Wishlist
              </div>

              <div className="products-coming-feature">
                <span>↔</span>
                Compare products
              </div>

              <div className="products-coming-feature">
                <span>⌁</span>
                Price filters
              </div>
            </div>
          </aside>

          {/* =======================================
              RESULTS
              ======================================= */}

          <div className="products-results">

            <div className="products-results-toolbar">
              <div className="products-results-heading">
                <div>
                  <h2>
                    {search
                      ? `Results for "${search}"`
                      : 'All products'}
                  </h2>

                  <p>
                    {totalElements}{' '}
                    {totalElements === 1
                      ? 'product'
                      : 'products'}
                  </p>
                </div>

                {search && (
                  <button
                    type="button"
                    className="products-active-search"
                    onClick={clearSearch}
                  >
                    <span>{search}</span>
                    <b>×</b>
                  </button>
                )}
              </div>

              <div className="products-sort">
                <label htmlFor="product-sort">
                  Sort by
                </label>

                <select
                  id="product-sort"
                  value={sort}
                  onChange={handleSort}
                >
                  <option value="id,desc">
                    Newest
                  </option>

                  <option value="name,asc">
                    Name: A to Z
                  </option>

                  <option value="price,asc">
                    Price: Low to High
                  </option>

                  <option value="price,desc">
                    Price: High to Low
                  </option>
                </select>
              </div>
            </div>

            {/* =====================================
                ERROR
                ===================================== */}

            {error && (
              <section className="products-state">
                <div className="products-state-icon">
                  !
                </div>

                <h3>
                  We couldn't load the products
                </h3>

                <p>{error}</p>

                <button
                  type="button"
                  onClick={() => {
                    setSearch(search)
                    setPage(page)
                  }}
                >
                  Try again
                </button>
              </section>
            )}

            {/* =====================================
                LOADING
                ===================================== */}

            {loading && !error && (
              <div className="products-grid">
                {Array.from({ length: 8 }).map(
                  (_, index) => (
                    <div
                      className="product-skeleton"
                      key={index}
                    >
                      <div className="skeleton-image" />

                      <div className="skeleton-content">
                        <span className="skeleton-small" />
                        <span className="skeleton-title" />
                        <span className="skeleton-description" />
                        <span className="skeleton-description short" />
                        <span className="skeleton-price" />
                      </div>
                    </div>
                  )
                )}
              </div>
            )}

            {/* =====================================
                PRODUCTS
                ===================================== */}

            {!loading &&
              !error &&
              products.length > 0 && (
                <>
                  <div className="products-grid">
                    {products.map((product) => (
                      <ProductCard
                        key={product.id}
                        product={product}
                      />
                    ))}
                  </div>

                  {/* PAGINATION */}

                  {totalPages > 1 && (
                    <nav
                      className="products-pagination"
                      aria-label="Product pagination"
                    >
                      <button
                        type="button"
                        className="pagination-arrow"
                        disabled={page === 0}
                        onClick={() =>
                          goToPage(page - 1)
                        }
                        aria-label="Previous page"
                      >
                        ←
                      </button>

                      {visiblePages.map(
                        (pageNumber, index) => {
                          const previousPage =
                            visiblePages[index - 1]

                          const showGap =
                            index > 0 &&
                            pageNumber -
                              previousPage >
                              1

                          return (
                            <span
                              className="pagination-item"
                              key={pageNumber}
                            >
                              {showGap && (
                                <span className="pagination-gap">
                                  …
                                </span>
                              )}

                              <button
                                type="button"
                                className={
                                  pageNumber === page
                                    ? 'active'
                                    : ''
                                }
                                onClick={() =>
                                  goToPage(pageNumber)
                                }
                                aria-label={`Page ${
                                  pageNumber + 1
                                }`}
                                aria-current={
                                  pageNumber === page
                                    ? 'page'
                                    : undefined
                                }
                              >
                                {pageNumber + 1}
                              </button>
                            </span>
                          )
                        }
                      )}

                      <button
                        type="button"
                        className="pagination-arrow"
                        disabled={
                          page === totalPages - 1
                        }
                        onClick={() =>
                          goToPage(page + 1)
                        }
                        aria-label="Next page"
                      >
                        →
                      </button>
                    </nav>
                  )}
                </>
              )}

            {/* =====================================
                EMPTY
                ===================================== */}

            {!loading &&
              !error &&
              products.length === 0 && (
                <section className="products-state">
                  <div className="products-state-icon">
                    ⌕
                  </div>

                  <h3>No products found</h3>

                  <p>
                    We couldn't find products matching
                    your search. Try another keyword.
                  </p>

                  {search && (
                    <button
                      type="button"
                      onClick={clearSearch}
                    >
                      View all products
                    </button>
                  )}
                </section>
              )}
          </div>
        </section>
      </div>
    </main>
  )
}

export default ProductsPage