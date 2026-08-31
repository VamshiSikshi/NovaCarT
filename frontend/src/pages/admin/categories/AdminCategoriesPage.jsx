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
  const [editingCategoryId, setEditingCategoryId] = useState(null)

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
    setForm(emptyForm)
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
    setForm(emptyForm)
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
    return <p>Loading categories...</p>
  }

  return (
    <div>
      <h1>Admin Categories</h1>

      {error && <p>{error}</p>}

      {success && <p>{success}</p>}

      <button
        type="button"
        onClick={openCreateForm}
      >
        Add Category
      </button>

      {showForm && (
        <section>
          <h2>
            {editingCategoryId
              ? 'Edit Category'
              : 'Create Category'}
          </h2>

          <form onSubmit={saveCategory}>
            <div>
              <label>Category Name</label>

              <input
                name="name"
                value={form.name}
                onChange={handleChange}
                maxLength={50}
                required
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

            <button
              type="submit"
              disabled={saving}
            >
              {saving
                ? 'Saving...'
                : editingCategoryId
                  ? 'Update Category'
                  : 'Create Category'}
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
        <h2>Categories</h2>

        {categories.length === 0 ? (
          <p>No categories found.</p>
        ) : (
          categories.map((category) => (
            <article key={category.id}>
              <h3>
                #{category.id} - {category.name}
              </h3>

              <p>
                Description:{' '}
                {category.description ||
                  'No description'}
              </p>

              <button
                type="button"
                onClick={() =>
                  openEditForm(category)
                }
              >
                Edit
              </button>

              <button
                type="button"
                onClick={() =>
                  deleteCategory(category.id)
                }
              >
                Delete
              </button>
            </article>
          ))
        )}
      </section>
    </div>
  )
}

export default AdminCategoriesPage