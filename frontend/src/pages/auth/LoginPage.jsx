import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

function LoginPage() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (event) => {
    event.preventDefault()

    setError('')
    setLoading(true)

    try {
      await login(username, password)
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
      <section className="auth-shell">
        <div className="auth-showcase">
          <div className="auth-showcase-content">
            <Link to="/" className="auth-brand">
              Nova<span>Cart</span>
            </Link>

            <div className="auth-showcase-copy">
              <p className="auth-eyebrow">WELCOME TO NOVACART</p>

              <h1>
                Everything you need,
                <span> in one place.</span>
              </h1>

              <p>
                Discover products you love, manage your orders,
                and enjoy a simple shopping experience.
              </p>
            </div>

            <div className="auth-showcase-footer">
              <span>Simple shopping.</span>
              <span>Secure checkout.</span>
              <span>Easy orders.</span>
            </div>
          </div>
        </div>

        <div className="auth-form-panel">
          <div className="auth-form-wrapper">
            <div className="auth-heading">
              <p className="auth-form-eyebrow">ACCOUNT</p>

              <h2>Welcome back</h2>

              <p>
                Sign in to continue to your NovaCart account.
              </p>
            </div>

            <form className="auth-form" onSubmit={handleSubmit}>
              <div className="auth-field">
                <label htmlFor="username">Username</label>

                <input
                  id="username"
                  type="text"
                  value={username}
                  onChange={(event) => setUsername(event.target.value)}
                  placeholder="Enter your username"
                  autoComplete="username"
                  required
                />
              </div>

              <div className="auth-field">
                <div className="auth-label-row">
                  <label htmlFor="password">Password</label>
                </div>

                <input
                  id="password"
                  type="password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  required
                />
              </div>

              {error && (
                <div className="auth-error" role="alert">
                  {error}
                </div>
              )}

              <button
                type="submit"
                className="auth-submit"
                disabled={loading}
              >
                {loading ? 'Signing in...' : 'Sign in'}
              </button>
            </form>

            <p className="auth-switch">
              Don't have an account?{' '}
              <Link to="/register">Create an account</Link>
            </p>
          </div>
        </div>
      </section>
    </main>
  )
}

export default LoginPage