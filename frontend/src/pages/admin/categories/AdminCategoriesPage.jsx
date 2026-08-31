import { useEffect, useState } from 'react'
import api from '../../../api/axios'

const emptyForm = {
  name: '',
  description: '',
}

function AdminCategoriesPage() {
  const [categories, setCategories] = useState([])

  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const [showForm, setShowForm] = useState(false)
  const [editingCategoryId, setEditingCategoryId] =
    useState(null)

  const [form, setForm] = useState(emptyForm)

  useEffect(() => {
    loadCategories()
  }, [])

  const loadCategories = async () => {
    try {
      setLoading(true)
      setError('')

      const response = await api.get(
        '/api/admin/categories'
      )

      setCategories(response.data)
    } catch (error) {
      setError(
        error.response?.data?.message ||
        'Failed to load categories.'
      )
    } finally {
      setLoading(false)
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
    setEditingCategoryId(null)
    setForm({ ...emptyForm })
    setError('')
    setSuccess('')
    setShowForm(true)
  }

  const openEditForm = (category) => {
    setEditingCategoryId(category.id)

    setForm({
      name: category.name || '',
      description: category.description || '',
    })

    setError('')
    setSuccess('')
    setShowForm(true)
  }

  const closeForm = () => {
    setShowForm(false)
    setEditingCategoryId(null)
    setForm({ ...emptyForm })
  }

  const saveCategory = async (event) => {
    event.preventDefault()

    try {
      setSaving(true)
      setError('')
      setSuccess('')

      if (editingCategoryId) {
        await api.put(
          `/api/admin/categories/${editingCategoryId}`,
          form
        )

        setSuccess(
          'Category updated successfully.'
        )
      } else {
        await api.post(
          '/api/admin/categories',
          form
        )

        setSuccess(
          'Category created successfully.'
        )
      }

      closeForm()
      await loadCategories()
    } catch (error) {
      setError(
        error.response?.data?.message ||
        'Failed to save category.'
      )
    } finally {
      setSaving(false)
    }
  }

  const deleteCategory = async (categoryId) => {
    const confirmed = window.confirm(
      'Are you sure you want to delete this category?'
    )

    if (!confirmed) {
      return
    }

    try {
      setError('')
      setSuccess('')

      await api.delete(
        `/api/admin/categories/${categoryId}`
      )

      setSuccess(
        'Category deleted successfully.'
      )

      await loadCategories()
    } catch (error) {
      setError(
        error.response?.data?.message ||
        'Failed to delete category.'
      )
    }
  }

  if (loading) {
    return (
      <div className="admin-page-state">
        <div className="admin-spinner"></div>
        <p>Loading categories...</p>
      </div>
    )
  }

  return (
    <div className="admin-page">

      {/* =========================
          PAGE HEADER
          ========================= */}

      <div className="admin-page-header">

        <div>
          <p className="admin-eyebrow">
            Catalog management
          </p>

          <h1>Categories</h1>

          <p className="admin-page-description">
            Manage your product categories.
          </p>
        </div>

        <button
          type="button"
          className="admin-primary-button"
          onClick={openCreateForm}
        >
          + Add Category
        </button>

      </div>


      {/* =========================
          ALERTS
          ========================= */}

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


      {/* =========================
          CATEGORY TABLE
          ========================= */}

      <section className="admin-table-card">

        <div className="admin-table-header">
          <div>
            <h2>Category Catalog</h2>

            <span>
              {categories.length}{' '}
              {categories.length === 1
                ? 'category'
                : 'categories'}
            </span>
          </div>
        </div>

        {categories.length === 0 ? (
          <div className="admin-empty-state">

            <div className="admin-empty-icon">
              C
            </div>

            <h3>No categories found</h3>

            <p>
              Create your first category to organize
              your products.
            </p>

            <button
              type="button"
              className="admin-primary-button"
              onClick={openCreateForm}
            >
              + Add Category
            </button>

          </div>
        ) : (
          <div className="admin-table-wrapper">

            <table className="admin-table">

              <thead>
                <tr>
                  <th>ID</th>
                  <th>Category</th>
                  <th>Description</th>
                  <th>Actions</th>
                </tr>
              </thead>

              <tbody>

                {categories.map((category) => (
                  <tr key={category.id}>

                    <td>
                      #{category.id}
                    </td>

                    <td>
                      <strong>
                        {category.name}
                      </strong>
                    </td>

                    <td>
                      {category.description ||
                        'No description'}
                    </td>

                    <td>

                      <div className="admin-actions">

                        <button
                          type="button"
                          className="admin-action-button edit"
                          onClick={() =>
                            openEditForm(category)
                          }
                        >
                          Edit
                        </button>

                        <button
                          type="button"
                          className="admin-action-button delete"
                          onClick={() =>
                            deleteCategory(
                              category.id
                            )
                          }
                        >
                          Delete
                        </button>

                      </div>

                    </td>

                  </tr>
                ))}

              </tbody>

            </table>

          </div>
        )}

      </section>


      {/* =========================
          CREATE / EDIT MODAL
          ========================= */}

      {showForm && (
        <div className="admin-modal-overlay">

          <div className="admin-modal">

            <div className="admin-modal-header">

              <div>
                <p className="admin-eyebrow">
                  Catalog management
                </p>

                <h2>
                  {editingCategoryId
                    ? 'Edit Category'
                    : 'Create Category'}
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
              onSubmit={saveCategory}
            >

              <div className="admin-form-group">

                <label htmlFor="category-name">
                  Category Name
                </label>

                <input
                  id="category-name"
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  maxLength={50}
                  required
                  autoFocus
                  placeholder="Enter category name"
                />

              </div>


              <div className="admin-form-group">

                <label htmlFor="category-description">
                  Description
                </label>

                <textarea
                  id="category-description"
                  name="description"
                  value={form.description}
                  onChange={handleChange}
                  maxLength={500}
                  rows={5}
                  placeholder="Enter category description"
                />

              </div>


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
                  {saving
                    ? 'Saving...'
                    : editingCategoryId
                      ? 'Update Category'
                      : 'Create Category'}
                </button>

              </div>

            </form>

          </div>

        </div>
      )}

    </div>
  )
}

export default AdminCategoriesPage