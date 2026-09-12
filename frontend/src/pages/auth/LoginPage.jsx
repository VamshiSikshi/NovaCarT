import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const [identifier, setIdentifier] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const [success, setSuccess] = useState(
    location.state?.registered
      ? 'Account created successfully. Please sign in.'
      : ''
  )

  const handleSubmit = async (event) => {
    event.preventDefault()

    setError('')
    setSuccess('')

    const trimmedIdentifier = identifier.trim()

    if (!trimmedIdentifier) {
      setError('Username or email is required.')
      return
    }

    if (!password) {
      setError('Password is required.')
      return
    }

    setLoading(true)

    try {
      await login(trimmedIdentifier, password)

      navigate('/products')
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Login failed. Please check your credentials.'
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="auth-page">
      <div className="auth-shell">

        {/* LEFT BRAND PANEL */}
        <section className="auth-brand-panel">

          <div className="auth-brand-logo">
            Nova<span>Cart</span>
          </div>

          <div className="auth-brand-content">

            <p className="auth-brand-eyebrow">
              WELCOME TO NOVACART
            </p>

            <h1>
              Everything you
              <br />
              need,
              <span> in one place.</span>
            </h1>

            <p className="auth-brand-description">
              Discover products you love, manage your orders,
              and enjoy a simple shopping experience.
            </p>

            <div className="auth-benefits">

              <div className="auth-benefit">
                <div className="auth-benefit-icon">✓</div>

                <div>
                  <strong>Simple shopping</strong>
                  <span>Find what you need quickly</span>
                </div>
              </div>

              <div className="auth-benefit">
                <div className="auth-benefit-icon">✓</div>

                <div>
                  <strong>Secure checkout</strong>
                  <span>Your account stays protected</span>
                </div>
              </div>

              <div className="auth-benefit">
                <div className="auth-benefit-icon">✓</div>

                <div>
                  <strong>Easy orders</strong>
                  <span>Track your purchases effortlessly</span>
                </div>
              </div>

            </div>
          </div>

          <div className="auth-brand-footer">
            Shop smarter. Live better.
          </div>

        </section>

        {/* LOGIN PANEL */}
        <section className="auth-form-panel">

          <div className="auth-form-content">

            <div className="auth-form-heading">

              <p className="auth-eyebrow">
                ACCOUNT
              </p>

              <h2>
                Welcome back
              </h2>

              <p>
                Sign in to continue to your NovaCart account.
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

                <label htmlFor="identifier">
                  Username or Email
                </label>

                <input
                  type="text"
                  value={identifier}
                  onChange={(event) => {
                    setIdentifier(event.target.value)
                    setError('')
                  }}
                  placeholder="Enter your username or email"
                  autoComplete="username"
                  disabled={loading}
                />

              </div>

              <div className="auth-field">

                <div className="auth-label-row">

                  <label htmlFor="login-password">
                    Password
                  </label>

                  <Link
                    to="/forgot-password"
                    className="auth-forgot-link"
                  >
                    Forgot password?
                  </Link>

                </div>

                <input
                  id="login-password"
                  type="password"
                  value={password}
                  onChange={(event) => {
                    setPassword(event.target.value)
                    setError('')
                  }}
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  disabled={loading}
                />

              </div>

              <button
                type="submit"
                className="auth-submit"
                disabled={loading}
              >
                {loading ? 'Signing in...' : 'Sign in'}
              </button>

            </form>

            <div className="auth-divider">
              <span>New to NovaCart?</span>
            </div>

            <div className="auth-create-account">

              <span>
                Don't have an account?
              </span>

              <Link to="/register">
                Create an account
              </Link>

            </div>

          </div>

        </section>

      </div>
    </main>
  )
}

export default LoginPage