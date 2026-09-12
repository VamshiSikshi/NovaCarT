import { useEffect, useMemo, useState } from 'react'
import api from '../../../api/axios'
import '../../../styles/admin/admin-products.css'

const emptyForm = {
  name: '',
  description: '',
  originalPrice: '',
  price: '',
  stock: '',
  imageUrl: '',
  categoryId: '',
}

const Icon = ({ name, size = 18, strokeWidth = 1.8 }) => {
  const common = {
    width: size,
    height: size,
    viewBox: '0 0 24 24',
    fill: 'none',
    stroke: 'currentColor',
    strokeWidth,
    strokeLinecap: 'round',
    strokeLinejoin: 'round',
    'aria-hidden': true,
  }

  const paths = {
    search: (
      <>
        <circle cx="11" cy="11" r="7" />
        <path d="m20 20-4-4" />
      </>
    ),

    plus: (
      <>
        <path d="M12 5v14" />
        <path d="M5 12h14" />
      </>
    ),

    close: (
      <>
        <path d="m6 6 12 12" />
        <path d="m18 6-12 12" />
      </>
    ),

    chevronDown: <path d="m6 9 6 6 6-6" />,

    package: (
      <>
        <path d="m21 8-9-5-9 5 9 5 9-5Z" />
        <path d="M3 8v8l9 5 9-5V8" />
        <path d="M12 13v8" />
      </>
    ),

    tag: (
      <>
        <path d="M20.6 13.4 13.4 20.6a2 2 0 0 1-2.8 0L3.4 13.4a2 2 0 0 1 0-2.8l7.2-7.2H18a2 2 0 0 1 2 2v7.4Z" />
        <circle cx="15.5" cy="8.5" r="1" />
      </>
    ),

    edit: (
      <>
        <path d="M12 20h9" />
        <path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L8 18l-4 1 1-4Z" />
      </>
    ),

    trash: (
      <>
        <path d="M4 7h16" />
        <path d="M10 11v6" />
        <path d="M14 11v6" />
        <path d="M6 7l1 14h10l1-14" />
        <path d="M9 7V4h6v3" />
      </>
    ),

    check: <path d="m5 12 4 4L19 6" />,

    alert: (
      <>
        <circle cx="12" cy="12" r="9" />
        <path d="M12 8v4" />
        <path d="M12 16h.01" />
      </>
    ),

    box: (
      <>
        <rect x="4" y="4" width="16" height="16" rx="2" />
        <path d="M8 9h8M8 13h8M8 17h5" />
      </>
    ),

    arrowLeft: (
      <>
        <path d="m15 18-6-6 6-6" />
        <path d="M9 12h10" />
      </>
    ),

    arrowRight: (
      <>
        <path d="m9 18 6-6-6-6" />
        <path d="M15 12H5" />
      </>
    ),
  }

  return <svg {...common}>{paths[name]}</svg>
}

function AdminProductsPage() {
  const [products, setProducts] = useState([])
  const [categories, setCategories] = useState([])

  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const [showForm, setShowForm] = useState(false)
  const [editingProductId, setEditingProductId] = useState(null)

  const [form, setForm] = useState(emptyForm)

  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  const [search, setSearch] = useState('')
  const [categoryFilter, setCategoryFilter] = useState('')
  const [stockFilter, setStockFilter] = useState('all')

  const [stockValues, setStockValues] = useState({})

  useEffect(() => {
    loadCategories()
  }, [])

  useEffect(() => {
    loadProducts()
  }, [page])

  useEffect(() => {
    if (!showForm) return

    const handleEscape = (event) => {
      if (event.key === 'Escape' && !saving) {
        closeForm()
      }
    }

    document.addEventListener('keydown', handleEscape)

    return () => {
      document.removeEventListener('keydown', handleEscape)
    }
  }, [showForm, saving])

  const loadProducts = async () => {
    try {
      setLoading(true)
      setError('')

      const response = await api.get(
        `/api/admin/products?page=${page}&size=10`
      )

      setProducts(response.data.content || [])
      setTotalPages(response.data.totalPages || 0)
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to load products.'
      )
    } finally {
      setLoading(false)
    }
  }

  const loadCategories = async () => {
    try {
      const response = await api.get('/api/admin/categories')

      setCategories(response.data || [])
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to load categories.'
      )
    }
  }

  const filteredProducts = useMemo(() => {
    const normalizedSearch = search.trim().toLowerCase()

    return products.filter((product) => {
      const matchesSearch =
        !normalizedSearch ||
        product.name?.toLowerCase().includes(normalizedSearch)

      const matchesCategory =
        !categoryFilter ||
        String(product.categoryId) === String(categoryFilter)

      const matchesStock =
        stockFilter === 'all' ||
        (stockFilter === 'in-stock' && product.stock > 5) ||
        (stockFilter === 'low-stock' &&
          product.stock > 0 &&
          product.stock <= 5) ||
        (stockFilter === 'out-of-stock' &&
          product.stock === 0)

      return (
        matchesSearch &&
        matchesCategory &&
        matchesStock
      )
    })
  }, [
    products,
    search,
    categoryFilter,
    stockFilter,
  ])

  const handleChange = (event) => {
    const { name, value } = event.target

    setForm((previousForm) => ({
      ...previousForm,
      [name]: value,
    }))
  }

  const openCreateForm = () => {
    setEditingProductId(null)
    setForm(emptyForm)
    setError('')
    setSuccess('')
    setShowForm(true)
  }

  const openEditForm = (product) => {
    setEditingProductId(product.id)

    setForm({
      name: product.name || '',
      description: product.description || '',
      originalPrice: product.originalPrice ?? '',
      price: product.price ?? '',
      stock: product.stock ?? '',
      imageUrl: product.imageUrl || '',
      categoryId: product.categoryId || '',
    })

    setError('')
    setSuccess('')
    setShowForm(true)
  }

  const closeForm = () => {
    if (saving) return

    setShowForm(false)
    setEditingProductId(null)
    setForm(emptyForm)
  }

  const saveProduct = async (event) => {
    event.preventDefault()

    const originalPrice = Number(form.originalPrice)
    const sellingPrice = Number(form.price)
    const stock = Number(form.stock)

    if (sellingPrice > originalPrice) {
      setError(
        'Selling price cannot be greater than original price.'
      )
      return
    }

    if (stock < 0 || !Number.isInteger(stock)) {
      setError('Stock must be a non-negative whole number.')
      return
    }

    try {
      setSaving(true)
      setError('')
      setSuccess('')

      const request = {
        name: form.name.trim(),
        description: form.description.trim(),
        originalPrice,
        price: sellingPrice,
        stock,
        imageUrl: form.imageUrl.trim(),
        categoryId: Number(form.categoryId),
      }

      if (editingProductId) {
        await api.put(
          `/api/admin/products/${editingProductId}`,
          request
        )

        setSuccess('Product updated successfully.')
      } else {
        await api.post(
          '/api/admin/products',
          request
        )

        setSuccess('Product created successfully.')
      }

      setShowForm(false)
      setEditingProductId(null)
      setForm(emptyForm)

      await loadProducts()
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to save product.'
      )
    } finally {
      setSaving(false)
    }
  }

  const deleteProduct = async (productId) => {
    const confirmed = window.confirm(
      'Delete this product? This action cannot be undone.'
    )

    if (!confirmed) return

    try {
      setError('')
      setSuccess('')

      await api.delete(
        `/api/admin/products/${productId}`
      )

      setSuccess('Product deleted successfully.')

      await loadProducts()
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to delete product.'
      )
    }
  }

  const handleStockChange = (productId, value) => {
    setStockValues((previousValues) => ({
      ...previousValues,
      [productId]: value,
    }))
  }

  const updateStock = async (
    productId,
    currentStock
  ) => {
    const value =
      stockValues[productId] ?? currentStock

    if (value === '') {
      setError('Stock cannot be empty.')
      return
    }

    const stock = Number(value)

    if (!Number.isInteger(stock) || stock < 0) {
      setError(
        'Stock must be a non-negative whole number.'
      )
      return
    }

    if (stock === Number(currentStock)) {
      return
    }

    try {
      setError('')
      setSuccess('')

      await api.patch(
        `/api/admin/products/${productId}/stock?stock=${stock}`
      )

      setSuccess('Stock updated successfully.')

      setStockValues((previousValues) => {
        const updated = { ...previousValues }
        delete updated[productId]
        return updated
      })

      await loadProducts()
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to update stock.'
      )
    }
  }

  const calculateDiscount = (
    originalPrice,
    price
  ) => {
    const original = Number(originalPrice)
    const selling = Number(price)

    if (!original || original <= selling) {
      return 0
    }

    return Math.round(
      ((original - selling) / original) * 100
    )
  }

  const getStockState = (stock) => {
    if (stock === 0) {
      return {
        className: 'out',
        label: 'Out of stock',
      }
    }

    if (stock <= 5) {
      return {
        className: 'low',
        label: 'Low stock',
      }
    }

    return {
      className: 'available',
      label: 'In stock',
    }
  }

  const resetFilters = () => {
    setSearch('')
    setCategoryFilter('')
    setStockFilter('all')
    setPage(0)
  }

  const hasFilters =
    search ||
    categoryFilter ||
    stockFilter !== 'all'

  const formatPrice = (value) =>
    Number(value).toLocaleString('en-IN')

  if (loading) {
    return (
      <div className="admin-page-state">
        <div className="admin-spinner" />
        <p>Loading product catalog...</p>
      </div>
    )
  }

  return (
    <div className="admin-page admin-products-page">

      {/* PAGE HEADER */}

      <header className="admin-page-header">
        <div className="admin-page-heading">
          <p className="admin-eyebrow">
            Catalog management
          </p>

          <h1>Products</h1>

          <p className="admin-page-description">
            Manage your product catalog, pricing and inventory.
          </p>
        </div>

        <button
          type="button"
          className="admin-primary-button admin-add-product"
          onClick={openCreateForm}
        >
          <Icon name="plus" size={16} />
          <span>Add Product</span>
        </button>
      </header>


      {/* ALERT */}

      {error && (
        <div
          className="admin-alert admin-alert-error"
          role="alert"
        >
          <span className="admin-alert-icon">
            <Icon name="alert" size={15} />
          </span>

          <span>{error}</span>

          <button
            type="button"
            className="admin-alert-close"
            onClick={() => setError('')}
            aria-label="Dismiss error"
          >
            <Icon name="close" size={14} />
          </button>
        </div>
      )}

      {success && (
        <div
          className="admin-alert admin-alert-success"
          role="status"
        >
          <span className="admin-alert-icon">
            <Icon name="check" size={15} />
          </span>

          <span>{success}</span>

          <button
            type="button"
            className="admin-alert-close"
            onClick={() => setSuccess('')}
            aria-label="Dismiss message"
          >
            <Icon name="close" size={14} />
          </button>
        </div>
      )}


      {/* FILTER BAR */}

      <section className="admin-product-toolbar">

        <div className="admin-product-search">
          <Icon name="search" size={17} />

          <input
            type="search"
            placeholder="Search by product name..."
            value={search}
            onChange={(event) => {
              setSearch(event.target.value)
              setPage(0)
            }}
            aria-label="Search products"
          />

          {search && (
            <button
              type="button"
              className="admin-search-clear"
              onClick={() => {
                setSearch('')
                setPage(0)
              }}
              aria-label="Clear search"
            >
              <Icon name="close" size={14} />
            </button>
          )}
        </div>

        <div className="admin-product-filters">

          <div className="admin-select-wrap">
            <select
              value={categoryFilter}
              onChange={(event) => {
                setCategoryFilter(event.target.value)
                setPage(0)
              }}
              className="admin-filter"
              aria-label="Filter by category"
            >
              <option value="">
                All Categories
              </option>

              {categories.map((category) => (
                <option
                  key={category.id}
                  value={category.id}
                >
                  {category.name}
                </option>
              ))}
            </select>

            <Icon name="chevronDown" size={14} />
          </div>

          <div className="admin-select-wrap">
            <select
              value={stockFilter}
              onChange={(event) => {
                setStockFilter(event.target.value)
                setPage(0)
              }}
              className="admin-filter"
              aria-label="Filter by stock"
            >
              <option value="all">
                All Stock
              </option>

              <option value="in-stock">
                In Stock
              </option>

              <option value="low-stock">
                Low Stock
              </option>

              <option value="out-of-stock">
                Out of Stock
              </option>
            </select>

            <Icon name="chevronDown" size={14} />
          </div>

          {hasFilters && (
            <button
              type="button"
              className="admin-clear-button"
              onClick={resetFilters}
            >
              Clear
            </button>
          )}
        </div>
      </section>


      {/* PRODUCT CATALOG */}

      <section className="admin-table-card">

        <div className="admin-table-header">

          <div>
            <div className="admin-table-title-row">
              <h2>Product Catalog</h2>

              <span className="admin-catalog-count">
                {filteredProducts.length}
              </span>
            </div>

            <p>
              {hasFilters
                ? 'Filtered products from the current page'
                : 'Manage products, inventory and pricing'}
            </p>
          </div>

          <div className="admin-table-meta">
            <span>
              Page {page + 1} of {Math.max(totalPages, 1)}
            </span>
          </div>

        </div>


        {filteredProducts.length === 0 ? (

          <div className="admin-empty-state">

            <div className="admin-empty-icon">
              <Icon name="package" size={22} />
            </div>

            <h3>No products found</h3>

            <p>
              There are no products matching your current
              search or filters.
            </p>

            {hasFilters && (
              <button
                type="button"
                className="admin-secondary-button"
                onClick={resetFilters}
              >
                Clear filters
              </button>
            )}

          </div>

        ) : (

          <div className="admin-table-wrapper">

            <table className="admin-table">

              <thead>
                <tr>
                  <th>Product</th>
                  <th>Category</th>
                  <th>Price</th>
                  <th>Inventory</th>
                  <th>Update stock</th>
                  <th>Actions</th>
                </tr>
              </thead>

              <tbody>

                {filteredProducts.map((product) => {
                  const stockState =
                    getStockState(product.stock)

                  const discount =
                    calculateDiscount(
                      product.originalPrice,
                      product.price
                    )

                  const currentStockValue =
                    stockValues[product.id] ??
                    product.stock

                  const stockChanged =
                    Number(currentStockValue) !==
                    Number(product.stock)

                  return (
                    <tr key={product.id}>

                      {/* PRODUCT */}

                      <td>
                        <div className="admin-product-cell">

                          <div className="admin-product-image">

                            {product.imageUrl ? (
                              <img
                                src={product.imageUrl}
                                alt=""
                                loading="lazy"
                                onError={(event) => {
                                  event.currentTarget.style.display =
                                    'none'
                                  event.currentTarget.parentElement.classList.add(
                                    'image-fallback'
                                  )
                                }}
                              />
                            ) : (
                              <span>
                                {product.name
                                  ?.charAt(0)
                                  .toUpperCase()}
                              </span>
                            )}

                          </div>

                          <div className="admin-product-info">

                            <strong
                              title={product.name}
                            >
                              {product.name}
                            </strong>

                            <span>
                              Product #{product.id}
                            </span>

                          </div>

                        </div>
                      </td>


                      {/* CATEGORY */}

                      <td>
                        <span className="admin-category-badge">
                          <Icon name="tag" size={12} />
                          {product.categoryName ||
                            'Uncategorized'}
                        </span>
                      </td>


                      {/* PRICE */}

                      <td>

                        <div className="admin-price">

                          <strong>
                            ₹{formatPrice(product.price)}
                          </strong>

                          {Number(
                            product.originalPrice
                          ) > Number(product.price) && (
                            <div className="admin-price-secondary">
                              <del>
                                ₹
                                {formatPrice(
                                  product.originalPrice
                                )}
                              </del>

                              {discount > 0 && (
                                <span>
                                  {discount}% OFF
                                </span>
                              )}
                            </div>
                          )}

                        </div>

                      </td>


                      {/* INVENTORY */}

                      <td>

                        <div className="admin-inventory">

                          <span
                            className={`stock-badge ${stockState.className}`}
                          >
                            {stockState.label}
                          </span>

                          <span className="admin-stock-count">
                            {product.stock} units
                          </span>

                        </div>

                      </td>


                      {/* STOCK EDITOR */}

                      <td>

                        <div className="admin-stock-editor">

                          <input
                            type="number"
                            min="0"
                            step="1"
                            value={currentStockValue}
                            onChange={(event) =>
                              handleStockChange(
                                product.id,
                                event.target.value
                              )
                            }
                            aria-label={`Update stock for ${product.name}`}
                          />

                          <button
                            type="button"
                            className={
                              stockChanged
                                ? 'is-dirty'
                                : ''
                            }
                            disabled={!stockChanged}
                            onClick={() =>
                              updateStock(
                                product.id,
                                product.stock
                              )
                            }
                          >
                            Save
                          </button>

                        </div>

                      </td>


                      {/* ACTIONS */}

                      <td>

                        <div className="admin-actions">

                          <button
                            type="button"
                            className="admin-action-button edit"
                            onClick={() => openEditForm(product)}
                            aria-label={`Edit ${product.name}`}
                            title="Edit product"
                          >
                            <Icon name="edit" size={15} />
                            <span>Edit</span>
                          </button>

                          <button
                            type="button"
                            className="admin-action-button delete"
                            onClick={() => deleteProduct(product.id)}
                            aria-label={`Delete ${product.name}`}
                            title="Delete product"
                          >
                            <Icon name="trash" size={15} />
                          </button>

                        </div>

                      </td>

                    </tr>
                  )
                })}

              </tbody>

            </table>

          </div>
        )}

      </section>


      {/* PAGINATION */}

      {totalPages > 1 && (
        <nav className="admin-pagination" aria-label="Product pagination">

          <button
            type="button"
            disabled={page === 0}
            onClick={() =>
              setPage((previousPage) => previousPage - 1)
            }
          >
            <Icon name="arrowLeft" size={14} />
            Previous
          </button>

          <div className="admin-pagination-info">
            <span>Page</span>
            <strong>{page + 1}</strong>
            <span>of</span>
            <strong>{totalPages}</strong>
          </div>

          <button
            type="button"
            disabled={page >= totalPages - 1}
            onClick={() =>
              setPage((previousPage) => previousPage + 1)
            }
          >
            Next
            <Icon name="arrowRight" size={14} />
          </button>

        </nav>
      )}


      {/* PRODUCT MODAL */}

      {showForm && (
        <div
          className="admin-modal-overlay"
          onMouseDown={(event) => {
            if (
              event.target === event.currentTarget &&
              !saving
            ) {
              closeForm()
            }
          }}
        >

          <section
            className="admin-modal admin-product-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="product-modal-title"
          >

            <div className="admin-modal-header">

              <div>
                <p className="admin-eyebrow">
                  Catalog
                </p>

                <h2 id="product-modal-title">
                  {editingProductId
                    ? 'Edit Product'
                    : 'Create Product'}
                </h2>

                <p>
                  {editingProductId
                    ? 'Update product information and inventory.'
                    : 'Add a new product to your catalog.'}
                </p>
              </div>

              <button
                type="button"
                className="admin-modal-close"
                onClick={closeForm}
                disabled={saving}
                aria-label="Close product form"
              >
                <Icon name="close" size={17} />
              </button>

            </div>


            <form
              className="admin-product-form"
              onSubmit={saveProduct}
            >

              {/* NAME */}

              <div className="admin-form-group">

                <label htmlFor="product-name">
                  Product Name
                </label>

                <input
                  id="product-name"
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  required
                  maxLength={100}
                  placeholder="e.g. NovaFit Adjustable Dumbbell"
                  autoComplete="off"
                />

              </div>


              {/* DESCRIPTION */}

              <div className="admin-form-group">

                <label htmlFor="product-description">
                  Description
                </label>

                <textarea
                  id="product-description"
                  name="description"
                  value={form.description}
                  onChange={handleChange}
                  maxLength={500}
                  rows={4}
                  placeholder="Describe the product, key features and specifications..."
                />

                <span className="admin-field-hint">
                  {form.description.length}/500
                </span>

              </div>


              {/* PRICE */}

              <div className="admin-form-row">

                <div className="admin-form-group">

                  <label htmlFor="original-price">
                    Original Price
                  </label>

                  <div className="admin-input-prefix">
                    <span>₹</span>

                    <input
                      id="original-price"
                      type="number"
                      name="originalPrice"
                      value={form.originalPrice}
                      onChange={handleChange}
                      min="0.01"
                      step="0.01"
                      required
                      placeholder="0.00"
                    />
                  </div>

                </div>


                <div className="admin-form-group">

                  <label htmlFor="selling-price">
                    Selling Price
                  </label>

                  <div className="admin-input-prefix">
                    <span>₹</span>

                    <input
                      id="selling-price"
                      type="number"
                      name="price"
                      value={form.price}
                      onChange={handleChange}
                      min="0.01"
                      step="0.01"
                      required
                      placeholder="0.00"
                    />
                  </div>

                </div>

              </div>


              {/* CATEGORY + STOCK */}

              <div className="admin-form-row">

                <div className="admin-form-group">

                  <label htmlFor="product-category">
                    Category
                  </label>

                  <div className="admin-form-select">

                    <select
                      id="product-category"
                      name="categoryId"
                      value={form.categoryId}
                      onChange={handleChange}
                      required
                    >
                      <option value="">
                        Select category
                      </option>

                      {categories.map((category) => (
                        <option
                          key={category.id}
                          value={category.id}
                        >
                          {category.name}
                        </option>
                      ))}
                    </select>

                    <Icon
                      name="chevronDown"
                      size={14}
                    />

                  </div>

                </div>


                <div className="admin-form-group">

                  <label htmlFor="product-stock">
                    Stock quantity
                  </label>

                  <input
                    id="product-stock"
                    type="number"
                    name="stock"
                    value={form.stock}
                    onChange={handleChange}
                    min="0"
                    step="1"
                    required
                    placeholder="0"
                  />

                </div>

              </div>


              {/* IMAGE */}

              <div className="admin-form-group">

                <label htmlFor="product-image">
                  Product Image URL
                </label>

                <input
                  id="product-image"
                  type="url"
                  name="imageUrl"
                  value={form.imageUrl}
                  onChange={handleChange}
                  placeholder="https://example.com/product-image.jpg"
                />

                {form.imageUrl && (
                  <div className="admin-image-preview">

                    <img
                      src={form.imageUrl}
                      alt="Product preview"
                      onError={(event) => {
                        event.currentTarget.style.display =
                          'none'
                        event.currentTarget.parentElement.classList.add(
                          'preview-error'
                        )
                      }}
                    />

                    <div className="admin-image-preview-info">
                      <strong>Image preview</strong>
                      <span>
                        This image will appear in your catalog.
                      </span>
                    </div>

                  </div>
                )}

              </div>


              {/* ACTIONS */}

              <div className="admin-modal-actions">

                <button
                  type="button"
                  className="admin-secondary-button"
                  onClick={closeForm}
                  disabled={saving}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="admin-primary-button"
                  disabled={saving}
                >
                  {saving ? (
                    <>
                      <span className="admin-button-spinner" />
                      Saving...
                    </>
                  ) : (
                    <>
                      <Icon
                        name={
                          editingProductId
                            ? 'check'
                            : 'plus'
                        }
                        size={15}
                      />

                      {editingProductId
                        ? 'Update Product'
                        : 'Create Product'}
                    </>
                  )}
                </button>

              </div>

            </form>

          </section>

        </div>
      )}

    </div>
  )
}

export default AdminProductsPage