import { useState } from 'react'
import { Link, useSearchParams, useNavigate } from 'react-router-dom'
import api from '../../api/axios'

function ResetPasswordPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()

  const token = searchParams.get('token')

  const [formData, setFormData] = useState({
    password: '',
    confirmPassword: '',
  })

  const [errors, setErrors] = useState({})
  const [submitError, setSubmitError] = useState('')
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState('')

  const handleChange = (event) => {
    const { name, value } = event.target

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }))

    setErrors((previous) => ({
      ...previous,
      [name]: '',
    }))

    setSubmitError('')
    setSuccess('')
  }

  const validate = () => {
    const newErrors = {}

    if (!token) {
      setSubmitError(
        'This password reset link is invalid or incomplete.'
      )
      return false
    }

    if (!formData.password) {
      newErrors.password = 'Password is required.'
    } else if (
      formData.password.length < 8 ||
      formData.password.length > 100
    ) {
      newErrors.password =
        'Password must be between 8 and 100 characters.'
    }

    if (!formData.confirmPassword) {
      newErrors.confirmPassword =
        'Please confirm your password.'
    } else if (
      formData.password !== formData.confirmPassword
    ) {
      newErrors.confirmPassword =
        'Passwords do not match.'
    }

    setErrors(newErrors)

    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (event) => {
    event.preventDefault()

    setSubmitError('')
    setSuccess('')

    if (!validate()) {
      return
    }

    setLoading(true)

    try {
      await api.post('/api/auth/reset-password', {
        token,
        newPassword: formData.password,
      })

      setSuccess(
        'Your password has been changed successfully.'
      )

      setFormData({
        password: '',
        confirmPassword: '',
      })

      setTimeout(() => {
        navigate('/login')
      }, 1500)
    } catch (error) {
      setSubmitError(
        error.response?.data?.message ||
          'This reset link is invalid or expired.'
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="auth-page">
      <div className="auth-shell">

        {/* LEFT PANEL */}
        <section className="auth-brand-panel auth-register-panel">

          <div className="auth-brand-logo">
            Nova<span>Cart</span>
          </div>

          <div className="auth-brand-content">

            <p className="auth-brand-eyebrow">
              ACCOUNT SECURITY
            </p>

            <h1>
              Create a
              <br />
              <span>new password.</span>
            </h1>

            <p className="auth-brand-description">
              Choose a strong password to keep your
              NovaCart account secure.
            </p>

            <div className="auth-benefits">

              <div className="auth-benefit">
                <div className="auth-benefit-icon">
                  ✓
                </div>

                <div>
                  <strong>Secure password</strong>
                  <span>
                    Use at least 8 characters
                  </span>
                </div>
              </div>

              <div className="auth-benefit">
                <div className="auth-benefit-icon">
                  ◇
                </div>

                <div>
                  <strong>Protected account</strong>
                  <span>
                    Keep your account secure
                  </span>
                </div>
              </div>

              <div className="auth-benefit">
                <div className="auth-benefit-icon">
                  →
                </div>

                <div>
                  <strong>Back to shopping</strong>
                  <span>
                    Sign in and continue shopping
                  </span>
                </div>
              </div>

            </div>

          </div>

        </section>

        {/* FORM PANEL */}
        <section className="auth-form-panel">

          <div className="auth-form-content">

            <div className="auth-form-heading">

              <p className="auth-eyebrow">
                RESET PASSWORD
              </p>

              <h2>
                Create new password
              </h2>

              <p>
                Enter a new password for your
                NovaCart account.
              </p>

            </div>

            {submitError && (
              <div
                className="auth-error"
                role="alert"
              >
                {submitError}
              </div>
            )}

            {success && (
              <div
                className="auth-success"
                role="status"
              >
                {success}
              </div>
            )}

            <form
              className="auth-form register-form"
              onSubmit={handleSubmit}
              noValidate
            >

              {/* PASSWORD */}
              <div className="auth-field">

                <label htmlFor="reset-password">
                  New password
                </label>

                <input
                  id="reset-password"
                  name="password"
                  type="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="Enter your new password"
                  autoComplete="new-password"
                  disabled={loading || Boolean(success)}
                  aria-invalid={Boolean(errors.password)}
                />

                {errors.password && (
                  <p className="auth-field-error">
                    {errors.password}
                  </p>
                )}

              </div>

              {/* CONFIRM PASSWORD */}
              <div className="auth-field">

                <label htmlFor="reset-confirm-password">
                  Confirm new password
                </label>

                <input
                  id="reset-confirm-password"
                  name="confirmPassword"
                  type="password"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  placeholder="Confirm your new password"
                  autoComplete="new-password"
                  disabled={loading || Boolean(success)}
                  aria-invalid={Boolean(
                    errors.confirmPassword
                  )}
                />

                {errors.confirmPassword && (
                  <p className="auth-field-error">
                    {errors.confirmPassword}
                  </p>
                )}

              </div>

              <button
                type="submit"
                className="auth-submit"
                disabled={loading || Boolean(success)}
              >
                {loading
                  ? 'Changing password...'
                  : 'Change password'}
              </button>

            </form>

            <div className="auth-divider">
              <span>Remember your password?</span>
            </div>

            <div className="auth-create-account">

              <span>
                Go back to your account.
              </span>

              <Link to="/login">
                Sign in
              </Link>

            </div>

          </div>

        </section>

      </div>
    </main>
  )
}

export default ResetPasswordPage