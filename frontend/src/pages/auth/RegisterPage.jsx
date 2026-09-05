import { Link } from 'react-router-dom'

function RegisterPage() {
  return (
    <main className="auth-page">
      <section className="auth-shell">
        <div className="auth-showcase">
          <div className="auth-showcase-content">
            <Link to="/" className="auth-brand">
              Nova<span>Cart</span>
            </Link>

            <div className="auth-showcase-copy">
              <p className="auth-eyebrow">JOIN NOVACART</p>

              <h1>
                Start your
                <span> shopping journey.</span>
              </h1>

              <p>
                Create your NovaCart account and get a
                simple, secure way to manage your shopping
                and orders.
              </p>
            </div>

            <div className="auth-showcase-footer">
              <span>Discover products.</span>
              <span>Manage orders.</span>
              <span>Shop with ease.</span>
            </div>
          </div>
        </div>

        <div className="auth-form-panel">
          <div className="auth-form-wrapper">
            <div className="auth-heading">
              <p className="auth-form-eyebrow">CREATE ACCOUNT</p>

              <h2>Create your account</h2>

              <p>
                Enter your details to get started with NovaCart.
              </p>
            </div>

            <form className="auth-form">
              <div className="auth-field">
                <label htmlFor="register-username">
                  Username
                </label>

                <input
                  id="register-username"
                  type="text"
                  placeholder="Choose a username"
                  autoComplete="username"
                />
              </div>

              <div className="auth-field">
                <label htmlFor="register-email">
                  Email address
                </label>

                <input
                  id="register-email"
                  type="email"
                  placeholder="Enter your email"
                  autoComplete="email"
                />
              </div>

              <div className="auth-field">
                <label htmlFor="register-password">
                  Password
                </label>

                <input
                  id="register-password"
                  type="password"
                  placeholder="Create a password"
                  autoComplete="new-password"
                />
              </div>

              <div className="auth-field">
                <label htmlFor="register-confirm-password">
                  Confirm password
                </label>

                <input
                  id="register-confirm-password"
                  type="password"
                  placeholder="Confirm your password"
                  autoComplete="new-password"
                />
              </div>

              <button
                type="button"
                className="auth-submit"
              >
                Create account
              </button>
            </form>

            <p className="auth-switch">
              Already have an account?{' '}
              <Link to="/login">Sign in</Link>
            </p>
          </div>
        </div>
      </section>
    </main>
  )
}

export default RegisterPage