import { useEffect, useState } from 'react'
import api from '../../../api/axios'

const emptyForm = {
  name: '',
  description: '',
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

    try {
      setSaving(true)
      setError('')
      setSuccess('')

      const request = {
        name: form.name,
        description: form.description,
        price: Number(form.price),
        stock: Number(form.stock),
        imageUrl: form.imageUrl,
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

  const updateStock = async (productId, currentStock) => {
    const value =
      stockValues[productId] ?? currentStock

    if (value === '') {
      setError('Stock cannot be empty.')
      return
    }

    const stock = Number(value)

    if (stock < 0) {
      setError('Stock cannot be negative.')
      return
    }

    try {
      setError('')
      setSuccess('')

      await api.patch(
        `/api/admin/products/${productId}/stock?stock=${stock}`
      )

      setSuccess('Stock updated successfully.')

      await loadProducts()
    } catch (error) {
      setError(
        error.response?.data?.message ||
        'Failed to update stock.'
      )
    }
  }

  if (loading) {
    return <p>Loading products...</p>
  }

  return (
    <div>
      <h1>Admin Products</h1>

      {error && <p>{error}</p>}

      {success && <p>{success}</p>}

      <button
        type="button"
        onClick={openCreateForm}
      >
        Add Product
      </button>

      {showForm && (
        <section>
          <h2>
            {editingProductId
              ? 'Edit Product'
              : 'Create Product'}
          </h2>

          <form onSubmit={saveProduct}>
            <div>
              <label>Product Name</label>

              <input
                name="name"
                value={form.name}
                onChange={handleChange}
                required
                maxLength={100}
              />
            </div>

            <div>
              <label>Description</label>

              <textarea
                name="description"
                value={form.description}
                onChange={handleChange}
                maxLength={500}
              />
            </div>

            <div>
              <label>Price</label>

              <input
                type="number"
                name="price"
                value={form.price}
                onChange={handleChange}
                min="0.01"
                step="0.01"
                required
              />
            </div>

            <div>
              <label>Stock</label>

              <input
                type="number"
                name="stock"
                value={form.stock}
                onChange={handleChange}
                min="0"
                required
              />
            </div>

            <div>
              <label>Image URL</label>

              <input
                type="text"
                name="imageUrl"
                value={form.imageUrl}
                onChange={handleChange}
              />
            </div>

            <div>
              <label>Category</label>

              <select
                name="categoryId"
                value={form.categoryId}
                onChange={handleChange}
                required
              >
                <option value="">
                  Select Category
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
            </div>

            <button
              type="submit"
              disabled={saving}
            >
              {saving
                ? 'Saving...'
                : editingProductId
                  ? 'Update Product'
                  : 'Create Product'}
            </button>

            <button
              type="button"
              onClick={closeForm}
            >
              Cancel
            </button>
          </form>
        </section>
      )}

      <section>
        <h2>Products</h2>

        {products.length === 0 ? (
          <p>No products found.</p>
        ) : (
          products.map((product) => (
            <article key={product.id}>
              <h3>
                #{product.id} - {product.name}
              </h3>

              {product.imageUrl && (
                <img
                  src={product.imageUrl}
                  alt={product.name}
                  width="120"
                />
              )}

              <p>
                Description: {product.description}
              </p>

              <p>
                Price: ₹{product.price}
              </p>

              <p>
                Category: {product.categoryName}
              </p>

              <p>
                Stock: {product.stock}
              </p>

              <div>
                <label>
                  Update Stock:
                </label>

                <input
                  type="number"
                  min="0"
                  value={
                    stockValues[product.id] ??
                    product.stock
                  }
                  onChange={(event) =>
                    handleStockChange(
                      product.id,
                      event.target.value
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
                  Update Stock
                </button>
              </div>

              <button
                type="button"
                onClick={() =>
                  openEditForm(product)
                }
              >
                Edit
              </button>

              <button
                type="button"
                onClick={() =>
                  deleteProduct(product.id)
                }
              >
                Delete
              </button>
            </article>
          ))
        )}
      </section>

      {totalPages > 1 && (
        <section>
          <button
            type="button"
            disabled={page === 0}
            onClick={() =>
              setPage((previousPage) =>
                previousPage - 1
              )
            }
          >
            Previous
          </button>

          <span>
            Page {page + 1} of {totalPages}
          </span>

          <button
            type="button"
            disabled={page >= totalPages - 1}
            onClick={() =>
              setPage((previousPage) =>
                previousPage + 1
              )
            }
          >
            Next
          </button>
        </section>
      )}
    </div>
  )
}

export default AdminProductsPage