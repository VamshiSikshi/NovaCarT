import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../../api/axios'
import '../../styles/checkout.css'

function CheckoutPage() {
  const [cart, setCart] = useState(null)
  const [addresses, setAddresses] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [showAddressForm, setShowAddressForm] =
    useState(false)
  const [selectedAddressId, setSelectedAddressId] =
    useState(null)

  const [placingOrder, setPlacingOrder] =
    useState(false)

  const [paymentMethod, setPaymentMethod] =
    useState('COD')

  const [payment, setPayment] = useState(null)
  const [processingPayment, setProcessingPayment] =
    useState(false)

  const navigate = useNavigate()

  const [form, setForm] = useState({
    fullName: '',
    phone: '',
    addressLine: '',
    city: '',
    state: '',
    postalCode: '',
    country: 'India',
    type: 'HOME',
  })

  useEffect(() => {
    const loadCheckoutData = async () => {
      try {
        const [cartResponse, addressResponse] =
          await Promise.all([
            api.get('/api/cart'),
            api.get('/api/addresses'),
          ])

        setCart(cartResponse.data)
        setAddresses(addressResponse.data)

        if (addressResponse.data.length > 0) {
          setSelectedAddressId(
            addressResponse.data[0].id
          )
        }
      } catch (error) {
        setError(
          error.response?.data?.message ||
            'Failed to load checkout data.'
        )
      } finally {
        setLoading(false)
      }
    }

    loadCheckoutData()
  }, [])

  const handleChange = (event) => {
    const { name, value } = event.target

    setForm((previousForm) => ({
      ...previousForm,
      [name]: value,
    }))
  }

  const createAddress = async (event) => {
    event.preventDefault()

    try {
      const response = await api.post(
        '/api/addresses',
        form
      )

      setAddresses((previousAddresses) => [
        ...previousAddresses,
        response.data,
      ])

      setSelectedAddressId(response.data.id)

      setForm({
        fullName: '',
        phone: '',
        addressLine: '',
        city: '',
        state: '',
        postalCode: '',
        country: 'India',
        type: 'HOME',
      })

      setShowAddressForm(false)
      setError('')
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to create address.'
      )
    }
  }

  const placeOrder = async () => {
    if (!selectedAddressId) {
      setError(
        'Please select a shipping address.'
      )
      return
    }

    try {
      setPlacingOrder(true)
      setError('')

      const orderResponse = await api.post(
        '/api/orders',
        {
          addressId: selectedAddressId,
          items: cart.items.map((item) => ({
            productId: item.productId,
            quantity: item.quantity,
          })),
        }
      )

      const order = orderResponse.data

      const paymentResponse = await api.post(
        `/api/orders/${order.id}/payment`,
        {
          method: paymentMethod,
        }
      )

      setPayment(paymentResponse.data)
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to place order.'
      )
    } finally {
      setPlacingOrder(false)
    }
  }

  const processPayment = async (status) => {
    if (!payment) {
      return
    }

    try {
      setProcessingPayment(true)
      setError('')

      const response = await api.patch(
        `/api/orders/${payment.orderId}/payment`,
        {
          status,
        }
      )

      if (response.data.status === 'SUCCESS') {
        navigate(`/orders/${payment.orderId}`)
      } else {
        setPayment(response.data)
      }
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to process payment.'
      )
    } finally {
      setProcessingPayment(false)
    }
  }

  const formatCurrency = (value) =>
    Number(value || 0).toLocaleString('en-IN')

  if (loading) {
    return (
      <main className="checkout-page">
        <div className="checkout-container">
          <div className="checkout-loading">
            <span className="checkout-spinner" />
            <p>Preparing your checkout...</p>
          </div>
        </div>
      </main>
    )
  }

  if (!cart) {
    return (
      <main className="checkout-page">
        <div className="checkout-container">
          <div className="checkout-error-state">
            <h1>Checkout unavailable</h1>
            <p>
              {error || 'Cart not found.'}
            </p>

            <button
              type="button"
              onClick={() => navigate('/cart')}
            >
              Return to cart
            </button>
          </div>
        </div>
      </main>
    )
  }

  if (cart.items.length === 0) {
    return (
      <main className="checkout-page">
        <div className="checkout-container">
          <div className="checkout-error-state">
            <h1>Your cart is empty</h1>
            <p>
              Add something to your cart before
              continuing to checkout.
            </p>

            <button
              type="button"
              onClick={() => navigate('/products')}
            >
              Browse products
            </button>
          </div>
        </div>
      </main>
    )
  }

  return (
    <main className="checkout-page">
      <div className="checkout-container">

        {/* HEADER */}
        <header className="checkout-header">
          <div>
            <span className="checkout-eyebrow">
              NOVACART / CHECKOUT
            </span>

            <h1>Complete your order.</h1>

            <p>
              Review your details and choose how
              you'd like to pay.
            </p>
          </div>

          <button
            type="button"
            className="checkout-back"
            onClick={() => navigate('/cart')}
          >
            ← Back to cart
          </button>
        </header>

        {/* ERROR */}
        {error && (
          <div className="checkout-alert">
            <span>!</span>
            <p>{error}</p>

            <button
              type="button"
              onClick={() => setError('')}
            >
              ×
            </button>
          </div>
        )}

        {/* PAYMENT RESULT */}
        {payment ? (
          <section className="payment-panel">

            <div className="payment-panel-header">
              <span className="checkout-eyebrow">
                PAYMENT
              </span>

              <h2>Review your payment</h2>

              <p>
                Your order has been created. Complete
                the payment to finish your purchase.
              </p>
            </div>

            <div className="payment-info-grid">
              <div>
                <span>Order</span>
                <strong>
                  #{payment.orderId}
                </strong>
              </div>

              <div>
                <span>Amount</span>
                <strong>
                  ₹{formatCurrency(payment.amount)}
                </strong>
              </div>

              <div>
                <span>Method</span>
                <strong>
                  {payment.method}
                </strong>
              </div>

              <div>
                <span>Status</span>
                <strong
                  className={`payment-status ${String(
                    payment.status
                  ).toLowerCase()}`}
                >
                  {payment.status}
                </strong>
              </div>
            </div>

            {payment.status === 'PENDING' && (
              <div className="payment-actions">

                <button
                  type="button"
                  className="checkout-primary-button"
                  disabled={processingPayment}
                  onClick={() =>
                    processPayment('SUCCESS')
                  }
                >
                  <span>
                    {processingPayment
                      ? 'Processing...'
                      : `Pay ₹${formatCurrency(
                          payment.amount
                        )}`}
                  </span>

                  <span>→</span>
                </button>

                <button
                  type="button"
                  className="payment-failed-button"
                  disabled={processingPayment}
                  onClick={() =>
                    processPayment('FAILED')
                  }
                >
                  Simulate failed payment
                </button>
              </div>
            )}

            {payment.status === 'SUCCESS' && (
              <div className="payment-success">
                <span>✓</span>

                <div>
                  <strong>
                    Payment successful
                  </strong>

                  <p>
                    Transaction ID:{' '}
                    {payment.transactionId}
                  </p>
                </div>
              </div>
            )}

            {payment.status === 'FAILED' && (
              <div className="payment-failed">
                <strong>
                  Payment failed
                </strong>

                <p>
                  Please try again with the
                  selected payment method.
                </p>
              </div>
            )}
          </section>
        ) : (
          <div className="checkout-layout">

            {/* LEFT */}
            <div className="checkout-main">

              {/* ADDRESS */}
              <section className="checkout-section">
                <div className="checkout-section-heading">
                  <div className="checkout-step">
                    01
                  </div>

                  <div>
                    <h2>
                      Shipping address
                    </h2>

                    <p>
                      Where should we deliver your
                      order?
                    </p>
                  </div>
                </div>

                {addresses.length > 0 ? (
                  <div className="address-grid">
                    {addresses.map((address) => (
                      <label
                        key={address.id}
                        className={`address-card ${
                          selectedAddressId ===
                          address.id
                            ? 'selected'
                            : ''
                        }`}
                      >
                        <input
                          type="radio"
                          name="shippingAddress"
                          checked={
                            selectedAddressId ===
                            address.id
                          }
                          onChange={() =>
                            setSelectedAddressId(
                              address.id
                            )
                          }
                        />

                        <div className="address-radio">
                          <span />
                        </div>

                        <div className="address-content">
                          <div className="address-title">
                            <strong>
                              {address.fullName}
                            </strong>

                            <span>
                              {address.type}
                            </span>
                          </div>

                          <p>
                            {address.addressLine}
                          </p>

                          <p>
                            {address.city},{' '}
                            {address.state}{' '}
                            {address.postalCode}
                          </p>

                          <p>
                            {address.country}
                          </p>

                          <span className="address-phone">
                            {address.phone}
                          </span>
                        </div>
                      </label>
                    ))}
                  </div>
                ) : (
                  <div className="no-address">
                    <span>+</span>

                    <div>
                      <strong>
                        No saved address
                      </strong>

                      <p>
                        Add an address to continue.
                      </p>
                    </div>
                  </div>
                )}

                <button
                  type="button"
                  className="add-address-button"
                  onClick={() =>
                    setShowAddressForm(
                      !showAddressForm
                    )
                  }
                >
                  <span>
                    {showAddressForm
                      ? '−'
                      : '+'}
                  </span>

                  {showAddressForm
                    ? 'Cancel'
                    : 'Add a new address'}
                </button>

                {showAddressForm && (
                  <form
                    className="address-form"
                    onSubmit={createAddress}
                  >
                    <div className="form-grid">

                      <div className="form-field">
                        <label>
                          Full name
                        </label>

                        <input
                          name="fullName"
                          value={form.fullName}
                          onChange={handleChange}
                          placeholder="Your full name"
                          required
                        />
                      </div>

                      <div className="form-field">
                        <label>
                          Phone
                        </label>

                        <input
                          name="phone"
                          value={form.phone}
                          onChange={handleChange}
                          placeholder="Phone number"
                          required
                        />
                      </div>

                      <div className="form-field full">
                        <label>
                          Address
                        </label>

                        <input
                          name="addressLine"
                          value={form.addressLine}
                          onChange={handleChange}
                          placeholder="House number, street, area"
                          required
                        />
                      </div>

                      <div className="form-field">
                        <label>
                          City
                        </label>

                        <input
                          name="city"
                          value={form.city}
                          onChange={handleChange}
                          placeholder="City"
                          required
                        />
                      </div>

                      <div className="form-field">
                        <label>
                          State
                        </label>

                        <input
                          name="state"
                          value={form.state}
                          onChange={handleChange}
                          placeholder="State"
                          required
                        />
                      </div>

                      <div className="form-field">
                        <label>
                          Postal code
                        </label>

                        <input
                          name="postalCode"
                          value={form.postalCode}
                          onChange={handleChange}
                          placeholder="Postal code"
                          required
                        />
                      </div>

                      <div className="form-field">
                        <label>
                          Address type
                        </label>

                        <select
                          name="type"
                          value={form.type}
                          onChange={handleChange}
                        >
                          <option value="HOME">
                            Home
                          </option>

                          <option value="WORK">
                            Work
                          </option>

                          <option value="OTHER">
                            Other
                          </option>
                        </select>
                      </div>

                      <div className="form-field">
                        <label>
                          Country
                        </label>

                        <input
                          name="country"
                          value={form.country}
                          onChange={handleChange}
                          required
                        />
                      </div>
                    </div>

                    <button
                      type="submit"
                      className="save-address-button"
                    >
                      Save address
                    </button>
                  </form>
                )}
              </section>

              {/* PAYMENT */}
              <section className="checkout-section">
                <div className="checkout-section-heading">
                  <div className="checkout-step">
                    02
                  </div>

                  <div>
                    <h2>
                      Payment method
                    </h2>

                    <p>
                      Choose your preferred payment
                      method.
                    </p>
                  </div>
                </div>

                <div className="payment-methods">

                  <label
                    className={`payment-method ${
                      paymentMethod === 'COD'
                        ? 'selected'
                        : ''
                    }`}
                  >
                    <input
                      type="radio"
                      name="paymentMethod"
                      value="COD"
                      checked={
                        paymentMethod === 'COD'
                      }
                      onChange={(event) =>
                        setPaymentMethod(
                          event.target.value
                        )
                      }
                    />

                    <span className="payment-method-icon">
                      ₹
                    </span>

                    <span className="payment-method-content">
                      <strong>
                        Cash on Delivery
                      </strong>

                      <small>
                        Pay when your order arrives
                      </small>
                    </span>

                    <span className="payment-radio" />
                  </label>

                  <label
                    className={`payment-method ${
                      paymentMethod === 'UPI'
                        ? 'selected'
                        : ''
                    }`}
                  >
                    <input
                      type="radio"
                      name="paymentMethod"
                      value="UPI"
                      checked={
                        paymentMethod === 'UPI'
                      }
                      onChange={(event) =>
                        setPaymentMethod(
                          event.target.value
                        )
                      }
                    />

                    <span className="payment-method-icon">
                      U
                    </span>

                    <span className="payment-method-content">
                      <strong>
                        UPI
                      </strong>

                      <small>
                        Pay securely using UPI
                      </small>
                    </span>

                    <span className="payment-radio" />
                  </label>

                  <label
                    className={`payment-method ${
                      paymentMethod === 'CARD'
                        ? 'selected'
                        : ''
                    }`}
                  >
                    <input
                      type="radio"
                      name="paymentMethod"
                      value="CARD"
                      checked={
                        paymentMethod === 'CARD'
                      }
                      onChange={(event) =>
                        setPaymentMethod(
                          event.target.value
                        )
                      }
                    />

                    <span className="payment-method-icon">
                      ▣
                    </span>

                    <span className="payment-method-content">
                      <strong>
                        Credit / Debit Card
                      </strong>

                      <small>
                        Visa, Mastercard and more
                      </small>
                    </span>

                    <span className="payment-radio" />
                  </label>

                </div>
              </section>
            </div>

            {/* RIGHT SUMMARY */}
            <aside className="checkout-summary">

              <div className="summary-heading">
                <span className="checkout-eyebrow">
                  YOUR ORDER
                </span>

                <h2>
                  Order summary
                </h2>
              </div>

              <div className="summary-products">
                {cart.items.map((item) => (
                  <div
                    className="summary-product"
                    key={item.id}
                  >
                    <div className="summary-product-image">
                      {item.imageUrl ? (
                        <img
                          src={item.imageUrl}
                          alt={item.productName}
                        />
                      ) : (
                        <span>
                          Nova
                        </span>
                      )}

                      <small>
                        {item.quantity}
                      </small>
                    </div>

                    <div className="summary-product-info">
                      <strong>
                        {item.productName}
                      </strong>

                      <span>
                        ₹
                        {formatCurrency(
                          item.price
                        )}{' '}
                        × {item.quantity}
                      </span>
                    </div>

                    <strong>
                      ₹
                      {formatCurrency(
                        item.subtotal
                      )}
                    </strong>
                  </div>
                ))}
              </div>

              <div className="summary-divider" />

              <div className="summary-lines">
                <div>
                  <span>Subtotal</span>

                  <strong>
                    ₹
                    {formatCurrency(
                      cart.total
                    )}
                  </strong>
                </div>

                <div>
                  <span>Delivery</span>

                  <strong className="free">
                    FREE
                  </strong>
                </div>
              </div>

              <div className="summary-total">
                <div>
                  <strong>
                    Total
                  </strong>

                  <small>
                    Inclusive of applicable taxes
                  </small>
                </div>

                <strong>
                  ₹
                  {formatCurrency(
                    cart.total
                  )}
                </strong>
              </div>

              <button
                type="button"
                className="checkout-primary-button"
                disabled={
                  !selectedAddressId ||
                  placingOrder
                }
                onClick={placeOrder}
              >
                <span>
                  {placingOrder
                    ? 'Creating order...'
                    : 'Continue to payment'}
                </span>

                <span>→</span>
              </button>

              <div className="checkout-security">
                <span>✓</span>

                <div>
                  <strong>
                    Secure checkout
                  </strong>

                  <small>
                    Your order information is
                    protected.
                  </small>
                </div>
              </div>

            </aside>
          </div>
        )}
      </div>
    </main>
  )
}

export default CheckoutPage