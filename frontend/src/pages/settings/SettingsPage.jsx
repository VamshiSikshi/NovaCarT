import { useState } from 'react'
import '../../styles/settings.css'
import { changePassword } from '../../services/authService'

function SettingsPage() {
  const [form, setForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  })

  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const handleChange = (event) => {
    const { name, value } = event.target

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }))

    setError('')
    setSuccess('')
  }

  const handleSubmit = async (event) => {
    event.preventDefault()

    setError('')
    setSuccess('')

    if (form.newPassword !== form.confirmPassword) {
      setError('New password and confirm password must match.')
      return
    }

    setSaving(true)

    try {
      await changePassword(form)

      setForm({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
      })

      setSuccess('Password changed successfully.')
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to change password.'
      )
    } finally {
      setSaving(false)
    }
  }

  return (
    <main className="settings-page">
      <div className="settings-container">

        <div className="settings-heading">
          <p className="settings-eyebrow">
            ACCOUNT
          </p>

          <h1>
            Settings
          </h1>

          <p>
            Manage your account security and preferences.
          </p>
        </div>

        {error && (
          <div
            className="settings-error"
            role="alert"
          >
            {error}
          </div>
        )}

        {success && (
          <div
            className="settings-success"
            role="status"
          >
            {success}
          </div>
        )}

        <section className="settings-section">

          <div className="settings-section-header">
            <h2>
              Change Password
            </h2>

            <p>
              Update your password to keep your account secure.
            </p>
          </div>

          <form
            className="settings-form"
            onSubmit={handleSubmit}
          >

            <div className="settings-field">
              <label htmlFor="currentPassword">
                Current password
              </label>

              <input
                id="currentPassword"
                name="currentPassword"
                type="password"
                value={form.currentPassword}
                onChange={handleChange}
                disabled={saving}
                required
              />
            </div>

            <div className="settings-field">
              <label htmlFor="newPassword">
                New password
              </label>

              <input
                id="newPassword"
                name="newPassword"
                type="password"
                value={form.newPassword}
                onChange={handleChange}
                disabled={saving}
                required
              />
            </div>

            <div className="settings-field">
              <label htmlFor="confirmPassword">
                Confirm new password
              </label>

              <input
                id="confirmPassword"
                name="confirmPassword"
                type="password"
                value={form.confirmPassword}
                onChange={handleChange}
                disabled={saving}
                required
              />
            </div>

            <div className="settings-actions">
              <button
                type="submit"
                className="settings-save-button"
                disabled={saving}
              >
                {saving ? 'Changing...' : 'Change password'}
              </button>
            </div>

          </form>

        </section>

      </div>
    </main>
  )
}

export default SettingsPage