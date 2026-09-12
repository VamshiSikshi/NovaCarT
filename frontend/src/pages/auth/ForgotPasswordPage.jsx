import { useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../../api/axios'

function ForgotPasswordPage() {
  const [email, setEmail] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (event) => {
    event.preventDefault()

    setError('')
    setSuccess('')

    const trimmedEmail = email.trim()

    if (!trimmedEmail) {
      setError('Email is required.')
      return
    }

    setLoading(true)

    try {
      const response = await api.post(
        '/api/auth/forgot-password',
        {
          email: trimmedEmail,
        }
      )

      setSuccess(response.data)
      setEmail('')
    } catch (error) {
      if (error.response?.status === 400) {
        setError(
          error.response?.data?.message ||
            'Please enter a valid email address.'
        )
      } else {
        setError(
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
        <section className="auth-brand-panel auth-recovery-panel">

          <div className="auth-brand-logo">
            Nova<span>Cart</span>
          </div>

          <div className="auth-brand-content">

            <p className="auth-brand-eyebrow">
              ACCOUNT RECOVERY
            </p>

            <h1>
              Reset your
              <br />
              <span>password.</span>
            </h1>

            <p className="auth-brand-description">
              Don't worry. Getting back into your NovaCart
              account only takes a few simple steps.
            </p>

            <div className="auth-benefits">

              <div className="auth-benefit">
                <div className="auth-benefit-icon">
                  ✉
                </div>

                <div>
                  <strong>Quick & secure</strong>
                  <span>
                    Receive a secure reset link
                  </span>
                </div>
              </div>

              <div className="auth-benefit">
                <div className="auth-benefit-icon">
                  ✓
                </div>

                <div>
                  <strong>Private & protected</strong>
                  <span>
                    Your account stays secure
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
                    Regain access in just a few steps
                  </span>
                </div>
              </div>

            </div>

          </div>

          <div className="auth-brand-footer">
            Shop smarter. Live better.
          </div>

        </section>

        {/* FORM PANEL */}
        <section className="auth-form-panel">

          <div className="auth-form-content">

            <div className="auth-form-heading">

              <p className="auth-eyebrow">
                ACCOUNT RECOVERY
              </p>

              <h2>
                Forgot password?
              </h2>

              <p>
                Enter your email address and we'll send you
                a secure link to reset your password.
              </p>

            </div>

            {success && (
              <div
                className="auth-success"
                role="status"
              >
                {success}
              </div>
            )}

            {error && (
              <div
                className="auth-error"
                role="alert"
              >
                {error}
              </div>
            )}

            <form
              className="auth-form"
              onSubmit={handleSubmit}
            >

              <div className="auth-field">

                <label htmlFor="forgot-email">
                  Email address
                </label>

                <input
                  id="forgot-email"
                  type="email"
                  value={email}
                  onChange={(event) => {
                    setEmail(event.target.value)
                    setError('')
                    setSuccess('')
                  }}
                  placeholder="Enter your email address"
                  autoComplete="email"
                  disabled={loading}
                />

              </div>

              <button
                type="submit"
                className="auth-submit"
                disabled={loading}
              >
                {loading
                  ? 'Sending...'
                  : 'Send reset link'}
              </button>

            </form>

            <div className="auth-divider">
              <span>OR</span>
            </div>

            <Link
              to="/login"
              className="auth-back-link"
            >
              ← Back to Login
            </Link>

            <div className="auth-help-box">

              <div className="auth-help-icon">
                ?
              </div>

              <div>
                <strong>Need help?</strong>

                <p>
                  If you don't receive the email,
                  check your spam folder.
                </p>
              </div>

            </div>

          </div>

        </section>

      </div>
    </main>
  )
}

export default ForgotPasswordPage