import { useEffect, useMemo, useState } from 'react'
import api from '../../../api/axios'

const emptyForm = {
  name: '',
  description: '',
  originalPrice: '',
  price: '',
  stock: '',
  imageUrl: '',
  categoryId: '',
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

  const loadProducts = async () => {
    try {
      setLoading(true)
      setError('')

      const response = await api.get(
        `/api/admin/products?page=${page}&size=10`
      )

      setProducts(response.data.content)
      setTotalPages(response.data.totalPages)
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
      const response = await api.get(
        '/api/admin/categories'
      )

      setCategories(response.data)
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to load categories.'
      )
    }
  }

  const filteredProducts = useMemo(() => {
    return products.filter((product) => {
      const matchesSearch =
        !search.trim() ||
        product.name
          ?.toLowerCase()
          .includes(search.trim().toLowerCase())

      const matchesCategory =
        !categoryFilter ||
        String(product.categoryId) ===
          String(categoryFilter)

      const matchesStock =
        stockFilter === 'all' ||
        (stockFilter === 'in-stock' &&
          product.stock > 0) ||
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
      originalPrice: product.originalPrice || '',
      price: product.price || '',
      stock: product.stock ?? '',
      imageUrl: product.imageUrl || '',
      categoryId: product.categoryId || '',
    })

    setError('')
    setSuccess('')
    setShowForm(true)
  }

  const closeForm = () => {
    setShowForm(false)
    setEditingProductId(null)
    setForm(emptyForm)
  }

  const saveProduct = async (event) => {
    event.preventDefault()

    if (
      Number(form.price) >
      Number(form.originalPrice)
    ) {
      setError(
        'Selling price cannot be greater than original price.'
      )
      return
    }

    try {
      setSaving(true)
      setError('')
      setSuccess('')

      const request = {
        name: form.name.trim(),
        description: form.description.trim(),
        originalPrice: Number(form.originalPrice),
        price: Number(form.price),
        stock: Number(form.stock),
        imageUrl: form.imageUrl.trim(),
        categoryId: Number(form.categoryId),
      }

      if (editingProductId) {
        await api.put(
          `/api/admin/products/${editingProductId}`,
          request
        )

        setSuccess(
          'Product updated successfully.'
        )
      } else {
        await api.post(
          '/api/admin/products',
          request
        )

        setSuccess(
          'Product created successfully.'
        )
      }

      closeForm()
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
      'Are you sure you want to delete this product?'
    )

    if (!confirmed) {
      return
    }

    try {
      setError('')
      setSuccess('')

      await api.delete(
        `/api/admin/products/${productId}`
      )

      setSuccess(
        'Product deleted successfully.'
      )

      await loadProducts()
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to delete product.'
      )
    }
  }

  const handleStockChange = (
    productId,
    value
  ) => {
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

    try {
      setError('')
      setSuccess('')

      await api.patch(
        `/api/admin/products/${productId}/stock?stock=${stock}`
      )

      setSuccess(
        'Stock updated successfully.'
      )

      setStockValues((previousValues) => {
        const updated = {
          ...previousValues,
        }

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

    if (
      !original ||
      original <= selling
    ) {
      return 0
    }

    return Math.round(
      ((original - selling) / original) * 100
    )
  }

  const getStockClass = (stock) => {
    if (stock === 0) {
      return 'stock-badge out'
    }

    if (stock <= 5) {
      return 'stock-badge low'
    }

    return 'stock-badge available'
  }

  const getStockLabel = (stock) => {
    if (stock === 0) {
      return 'Out of stock'
    }

    if (stock <= 5) {
      return 'Low stock'
    }

    return 'In stock'
  }

  const resetFilters = () => {
    setSearch('')
    setCategoryFilter('')
    setStockFilter('all')
  }

  if (loading) {
    return (
      <div className="admin-page-state">
        <div className="admin-spinner" />
        <p>Loading products...</p>
      </div>
    )
  }

  return (
    <div className="admin-page">
      <div className="admin-page-header">
        <div>
          <p className="admin-eyebrow">
            Catalog management
          </p>

          <h1>Products</h1>

          <p className="admin-page-description">
            Manage your product catalog,
            pricing and inventory.
          </p>
        </div>

        <button
          type="button"
          className="admin-primary-button"
          onClick={openCreateForm}
        >
          <span>+</span>
          Add Product
        </button>
      </div>

      {error && (
        <div className="admin-alert admin-alert-error">
          <span>!</span>
          {error}
        </div>
      )}

      {success && (
        <div className="admin-alert admin-alert-success">
          <span>✓</span>
          {success}
        </div>
      )}

      <div className="admin-toolbar">
        <div className="admin-search">
          <span>⌕</span>

          <input
            type="search"
            placeholder="Search products..."
            value={search}
            onChange={(event) =>
              setSearch(event.target.value)
            }
          />
        </div>

        <select
          value={categoryFilter}
          onChange={(event) =>
            setCategoryFilter(event.target.value)
          }
          className="admin-filter"
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

        <select
          value={stockFilter}
          onChange={(event) =>
            setStockFilter(event.target.value)
          }
          className="admin-filter"
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

        {(search ||
          categoryFilter ||
          stockFilter !== 'all') && (
          <button
            type="button"
            className="admin-clear-button"
            onClick={resetFilters}
          >
            Clear
          </button>
        )}
      </div>

      <div className="admin-table-card">
        <div className="admin-table-header">
          <div>
            <h2>Product Catalog</h2>
            <span>
              {filteredProducts.length} product
              {filteredProducts.length !== 1
                ? 's'
                : ''}
            </span>
          </div>
        </div>

        {filteredProducts.length === 0 ? (
          <div className="admin-empty-state">
            <div className="admin-empty-icon">
              □
            </div>

            <h3>No products found</h3>

            <p>
              Try changing your filters or add
              a new product.
            </p>

            <button
              type="button"
              className="admin-secondary-button"
              onClick={resetFilters}
            >
              Clear Filters
            </button>
          </div>
        ) : (
          <div className="admin-table-wrapper">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>Product</th>
                  <th>Category</th>
                  <th>Price</th>
                  <th>Stock</th>
                  <th>Update Stock</th>
                  <th>Actions</th>
                </tr>
              </thead>

              <tbody>
                {filteredProducts.map(
                  (product) => {
                    const discount =
                      calculateDiscount(
                        product.originalPrice,
                        product.price
                      )

                    return (
                      <tr key={product.id}>
                        <td>
                          <div className="admin-product-cell">
                            <div className="admin-product-image">
                              {product.imageUrl ? (
                                <img
                                  src={
                                    product.imageUrl
                                  }
                                  alt={
                                    product.name
                                  }
                                  onError={(
                                    event
                                  ) => {
                                    event.currentTarget.style.display =
                                      'none'
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

                            <div>
                              <strong>
                                {product.name}
                              </strong>

                              <small>
                                #{product.id}
                              </small>
                            </div>
                          </div>
                        </td>

                        <td>
                          <span className="admin-category-badge">
                            {product.categoryName}
                          </span>
                        </td>

                        <td>
                          <div className="admin-price">
                            <strong>
                              ₹
                              {Number(
                                product.price
                              ).toLocaleString(
                                'en-IN'
                              )}
                            </strong>

                            {Number(
                              product.originalPrice
                            ) >
                              Number(
                                product.price
                              ) && (
                              <div>
                                <del>
                                  ₹
                                  {Number(
                                    product.originalPrice
                                  ).toLocaleString(
                                    'en-IN'
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

                        <td>
                          <span
                            className={getStockClass(
                              product.stock
                            )}
                          >
                            {getStockLabel(
                              product.stock
                            )}
                          </span>

                          <small className="admin-stock-count">
                            {product.stock} units
                          </small>
                        </td>

                        <td>
                          <div className="admin-stock-editor">
                            <input
                              type="number"
                              min="0"
                              value={
                                stockValues[
                                  product.id
                                ] ??
                                product.stock
                              }
                              onChange={(event) =>
                                handleStockChange(
                                  product.id,
                                  event.target
                                    .value
                                )
                              }
                            />

                            <button
                              type="button"
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

                        <td>
                          <div className="admin-actions">
                            <button
                              type="button"
                              className="admin-action-button edit"
                              onClick={() =>
                                openEditForm(
                                  product
                                )
                              }
                            >
                              Edit
                            </button>

                            <button
                              type="button"
                              className="admin-action-button delete"
                              onClick={() =>
                                deleteProduct(
                                  product.id
                                )
                              }
                            >
                              Delete
                            </button>
                          </div>
                        </td>
                      </tr>
                    )
                  }
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {totalPages > 1 && (
        <div className="admin-pagination">
          <button
            type="button"
            disabled={page === 0}
            onClick={() =>
              setPage(
                (previousPage) =>
                  previousPage - 1
              )
            }
          >
            ← Previous
          </button>

          <span>
            Page <strong>{page + 1}</strong> of{' '}
            <strong>{totalPages}</strong>
          </span>

          <button
            type="button"
            disabled={
              page >= totalPages - 1
            }
            onClick={() =>
              setPage(
                (previousPage) =>
                  previousPage + 1
              )
            }
          >
            Next →
          </button>
        </div>
      )}

      {showForm && (
        <div
          className="admin-modal-overlay"
          onMouseDown={(event) => {
            if (
              event.target === event.currentTarget
            ) {
              closeForm()
            }
          }}
        >
          <section className="admin-modal">
            <div className="admin-modal-header">
              <div>
                <p className="admin-eyebrow">
                  Catalog
                </p>

                <h2>
                  {editingProductId
                    ? 'Edit Product'
                    : 'Create Product'}
                </h2>
              </div>

              <button
                type="button"
                className="admin-modal-close"
                onClick={closeForm}
                aria-label="Close"
              >
                ×
              </button>
            </div>

            <form
              className="admin-product-form"
              onSubmit={saveProduct}
            >
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
                  placeholder="Enter product name"
                />
              </div>

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
                  placeholder="Describe the product"
                />
              </div>

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
                      value={
                        form.originalPrice
                      }
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

              <div className="admin-form-row">
                <div className="admin-form-group">
                  <label htmlFor="product-category">
                    Category
                  </label>

                  <select
                    id="product-category"
                    name="categoryId"
                    value={
                      form.categoryId
                    }
                    onChange={handleChange}
                    required
                  >
                    <option value="">
                      Select Category
                    </option>

                    {categories.map(
                      (category) => (
                        <option
                          key={category.id}
                          value={
                            category.id
                          }
                        >
                          {category.name}
                        </option>
                      )
                    )}
                  </select>
                </div>

                <div className="admin-form-group">
                  <label htmlFor="product-stock">
                    Stock
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

              <div className="admin-form-group">
                <label htmlFor="product-image">
                  Image URL
                </label>

                <input
                  id="product-image"
                  type="url"
                  name="imageUrl"
                  value={form.imageUrl}
                  onChange={handleChange}
                  placeholder="https://example.com/image.jpg"
                />
              </div>

              <div className="admin-modal-actions">
                <button
                  type="button"
                  className="admin-secondary-button"
                  onClick={closeForm}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="admin-primary-button"
                  disabled={saving}
                >
                  {saving
                    ? 'Saving...'
                    : editingProductId
                      ? 'Update Product'
                      : 'Create Product'}
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