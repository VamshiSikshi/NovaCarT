import { useEffect, useState } from 'react'
import api from '../../../api/axios'
import '../../../styles/admin/admin-categories.css'

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
    if (saving) return

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

      setShowForm(false)
      setEditingCategoryId(null)
      setForm({ ...emptyForm })

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
      <div className="categories-page-state">
        <span className="categories-spinner" />
        <p>Loading categories...</p>
      </div>
    )
  }

  return (
    <div className="categories-page">

      {/* =========================
          HEADER
         ========================= */}

      <header className="categories-header">

        <div className="categories-heading">

          <p className="categories-eyebrow">
            CATALOG / ORGANIZATION
          </p>

          <h1>
            Categories<span>.</span>
          </h1>

          <p className="categories-description">
            Organize your product catalog into clear,
            manageable collections.
          </p>

        </div>

        <button
          type="button"
          className="categories-add-button"
          onClick={openCreateForm}
        >
          <span className="categories-add-icon">
            +
          </span>

          Add category
        </button>

      </header>


      {/* =========================
          ALERTS
         ========================= */}

      {error && (
        <div className="categories-alert categories-alert-error">
          <span>!</span>
          <p>{error}</p>
        </div>
      )}

      {success && (
        <div className="categories-alert categories-alert-success">
          <span>✓</span>
          <p>{success}</p>
        </div>
      )}


      {/* =========================
          CONTENT
         ========================= */}

      <section className="categories-content">

        <div className="categories-content-header">

          <div>
            <p className="categories-section-label">
              COLLECTIONS
            </p>

            <h2>Category catalog</h2>
          </div>

          <div className="categories-count">
            <strong>
              {categories.length}
            </strong>

            <span>
              {categories.length === 1
                ? 'category'
                : 'categories'}
            </span>
          </div>

        </div>


        {categories.length === 0 ? (
          <div className="categories-empty">

            <div className="categories-empty-mark">
              <span />
              <span />
              <span />
            </div>

            <h3>Your catalog is empty</h3>

            <p>
              Create your first category to start
              organizing your products.
            </p>

            <button
              type="button"
              className="categories-add-button"
              onClick={openCreateForm}
            >
              <span className="categories-add-icon">
                +
              </span>

              Create category
            </button>

          </div>
        ) : (
          <div className="categories-table-wrapper">

            <table className="categories-table">

              <thead>
                <tr>
                  <th className="categories-id-column">
                    ID
                  </th>

                  <th>
                    Category
                  </th>

                  <th>
                    Description
                  </th>

                  <th className="categories-actions-column">
                    Actions
                  </th>
                </tr>
              </thead>

              <tbody>

                {categories.map((category, index) => (
                  <tr
                    key={category.id}
                    style={{
                      animationDelay: `${index * 45}ms`,
                    }}
                  >

                    <td>
                      <span className="categories-id">
                        #{String(category.id).padStart(
                          2,
                          '0'
                        )}
                      </span>
                    </td>

                    <td>
                      <div className="category-name-cell">

                        <div className="category-avatar">
                          {category.name
                            ?.charAt(0)
                            ?.toUpperCase() || 'C'}
                        </div>

                        <div>
                          <strong>
                            {category.name}
                          </strong>

                          <span>
                            Product collection
                          </span>
                        </div>

                      </div>
                    </td>

                    <td>
                      <p className="category-description">
                        {category.description ||
                          'No description added'}
                      </p>
                    </td>

                    <td>
                      <div className="category-actions">

                        <button
                          type="button"
                          className="category-edit-button"
                          onClick={() =>
                            openEditForm(category)
                          }
                        >
                          <span>↗</span>
                          Edit
                        </button>

                        <button
                          type="button"
                          className="category-delete-button"
                          onClick={() =>
                            deleteCategory(
                              category.id
                            )
                          }
                          aria-label={`Delete ${category.name}`}
                        >
                          ×
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
          FOOTER INFO
         ========================= */}

      {categories.length > 0 && (
        <div className="categories-footer-note">

          <div className="categories-footer-icon">
            ✓
          </div>

          <div>
            <strong>
              Catalog organization
            </strong>

            <p>
              Categories help customers discover
              products faster.
            </p>
          </div>

        </div>
      )}


      {/* =========================
          MODAL
         ========================= */}

      {showForm && (
        <div
          className="categories-modal-overlay"
          onMouseDown={(event) => {
            if (
              event.target === event.currentTarget &&
              !saving
            ) {
              closeForm()
            }
          }}
        >

          <div
            className="categories-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="category-modal-title"
          >

            <div className="categories-modal-top">

              <div>

                <p className="categories-eyebrow">
                  CATALOG / CATEGORY
                </p>

                <h2 id="category-modal-title">
                  {editingCategoryId
                    ? 'Edit category'
                    : 'Create category'}
                  <span>.</span>
                </h2>

                <p>
                  {editingCategoryId
                    ? 'Update the information for this category.'
                    : 'Add a new collection to your catalog.'}
                </p>

              </div>

              <button
                type="button"
                className="categories-modal-close"
                onClick={closeForm}
                disabled={saving}
                aria-label="Close"
              >
                ×
              </button>

            </div>


            <form
              className="categories-form"
              onSubmit={saveCategory}
            >

              <div className="categories-form-group">

                <label htmlFor="category-name">
                  Category name
                </label>

                <input
                  id="category-name"
                  name="name"
                  type="text"
                  value={form.name}
                  onChange={handleChange}
                  maxLength={50}
                  required
                  autoFocus
                  placeholder="e.g. Electronics"
                />

                <span>
                  Give your category a clear,
                  recognizable name.
                </span>

              </div>


              <div className="categories-form-group">

                <div className="categories-label-row">

                  <label htmlFor="category-description">
                    Description
                  </label>

                  <span>
                    {form.description.length}/500
                  </span>

                </div>

                <textarea
                  id="category-description"
                  name="description"
                  value={form.description}
                  onChange={handleChange}
                  maxLength={500}
                  rows={5}
                  placeholder="Describe what kind of products belong in this category..."
                />

              </div>


              <div className="categories-modal-footer">

                <button
                  type="button"
                  className="categories-cancel-button"
                  onClick={closeForm}
                  disabled={saving}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="categories-save-button"
                  disabled={saving}
                >
                  {saving ? (
                    <>
                      <span className="categories-button-spinner" />
                      Saving...
                    </>
                  ) : (
                    <>
                      {editingCategoryId
                        ? 'Save changes'
                        : 'Create category'}

                      <span>→</span>
                    </>
                  )}
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