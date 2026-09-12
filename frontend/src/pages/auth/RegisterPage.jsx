import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../../api/axios'

function RegisterPage() {
  const navigate = useNavigate()

  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  })

  const [errors, setErrors] = useState({})
  const [submitError, setSubmitError] = useState('')
  const [loading, setLoading] = useState(false)

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
  }

  const validate = () => {
    const newErrors = {}

    const username = formData.username.trim()
    const email = formData.email.trim()

    if (!username) {
      newErrors.username = 'Username is required.'
    } else if (username.length < 3 || username.length > 50) {
      newErrors.username =
        'Username must be between 3 and 50 characters.'
    } else if (!/^[a-zA-Z0-9_.]+$/.test(username)) {
      newErrors.username =
        'Use only letters, numbers, underscores, and dots.'
    }

    if (!email) {
      newErrors.email = 'Email is required.'
    } else if (email.length > 100) {
      newErrors.email =
        'Email must not exceed 100 characters.'
    } else if (
      !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
    ) {
      newErrors.email =
        'Please enter a valid email address.'
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

    if (!validate()) {
      return
    }

    setLoading(true)

    try {
      await api.post('/api/users', {
        username: formData.username.trim(),
        email: formData.email.trim(),
        password: formData.password,
      })

      navigate('/login', {
        state: {
          registered: true,
          username: formData.username.trim(),
        },
      })
    } catch (error) {
      const status = error.response?.status
      const message = error.response?.data?.message

      if (status === 409) {
        if (message?.toLowerCase().includes('username')) {
          setErrors({
            username: 'This username is already taken.',
          })
        } else if (message?.toLowerCase().includes('email')) {
          setErrors({
            email: 'An account with this email already exists.',
          })
        } else {
          setSubmitError(
            message || 'This account already exists.'
          )
        }
      } else if (status === 400) {
        setSubmitError(
          message || 'Please check your information and try again.'
        )
      } else {
        setSubmitError(
          'Something went wrong. Please try again later.'
        )
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="auth-page">
      <div className="auth-shell">

        {/* LEFT BRAND PANEL */}
        <section className="auth-brand-panel auth-register-panel">

          <div className="auth-brand-logo">
            Nova<span>Cart</span>
          </div>

          <div className="auth-brand-content">

            <p className="auth-brand-eyebrow">
              JOIN NOVACART
            </p>

            <h1>
              Your style,
              <br />
              <span>your way.</span>
            </h1>

            <p className="auth-brand-description">
              Create your NovaCart account and discover
              products made for your everyday lifestyle.
            </p>

            <div className="auth-benefits">

              <div className="auth-benefit">
                <div className="auth-benefit-icon">
                  ✓
                </div>

                <div>
                  <strong>Easy shopping</strong>
                  <span>
                    Discover products in one place
                  </span>
                </div>
              </div>

              <div className="auth-benefit">
                <div className="auth-benefit-icon">
                  ◇
                </div>

                <div>
                  <strong>Secure account</strong>
                  <span>
                    Your information stays protected
                  </span>
                </div>
              </div>

              <div className="auth-benefit">
                <div className="auth-benefit-icon">
                  →
                </div>

                <div>
                  <strong>Track your orders</strong>
                  <span>
                    Manage your purchases effortlessly
                  </span>
                </div>
              </div>

            </div>

          </div>

          <div className="auth-brand-footer">
            Shop smarter. Live better.
          </div>

        </section>

        {/* REGISTER PANEL */}
        <section className="auth-form-panel">

          <div className="auth-form-content">

            <div className="auth-form-heading">

              <p className="auth-eyebrow">
                CREATE ACCOUNT
              </p>

              <h2>
                Join NovaCart
              </h2>

              <p>
                Create your account and start shopping today.
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

            <form
              className="auth-form register-form"
              onSubmit={handleSubmit}
              noValidate
            >

              {/* USERNAME */}
              <div className="auth-field">

                <label htmlFor="register-username">
                  Username
                </label>

                <input
                  id="register-username"
                  name="username"
                  type="text"
                  value={formData.username}
                  onChange={handleChange}
                  placeholder="Choose a username"
                  autoComplete="username"
                  disabled={loading}
                  aria-invalid={Boolean(errors.username)}
                />

                {errors.username && (
                  <p className="auth-field-error">
                    {errors.username}
                  </p>
                )}

              </div>

              {/* EMAIL */}
              <div className="auth-field">

                <label htmlFor="register-email">
                  Email address
                </label>

                <input
                  id="register-email"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="Enter your email address"
                  autoComplete="email"
                  disabled={loading}
                  aria-invalid={Boolean(errors.email)}
                />

                {errors.email && (
                  <p className="auth-field-error">
                    {errors.email}
                  </p>
                )}

              </div>

              {/* PASSWORD */}
              <div className="auth-field">

                <label htmlFor="register-password">
                  Password
                </label>

                <input
                  id="register-password"
                  name="password"
                  type="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="Create a password"
                  autoComplete="new-password"
                  disabled={loading}
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

                <label htmlFor="register-confirm-password">
                  Confirm password
                </label>

                <input
                  id="register-confirm-password"
                  name="confirmPassword"
                  type="password"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  placeholder="Confirm your password"
                  autoComplete="new-password"
                  disabled={loading}
                  aria-invalid={Boolean(errors.confirmPassword)}
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
                disabled={loading}
              >
                {loading
                  ? 'Creating account...'
                  : 'Create account'}
              </button>

            </form>

            <div className="auth-divider">
              <span>Already have an account?</span>
            </div>

            <div className="auth-create-account">

              <span>
                Welcome back.
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

export default RegisterPage