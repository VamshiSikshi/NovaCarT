import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../../api/axios'

function CheckoutPage() {
  const [cart, setCart] = useState(null)
  const [addresses, setAddresses] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showAddressForm, setShowAddressForm] = useState(false)
  const [selectedAddressId, setSelectedAddressId] = useState(null)
  const [placingOrder, setPlacingOrder] = useState(false)
  const [paymentMethod, setPaymentMethod] = useState('COD')
  const [payment, setPayment] = useState(null)
  const [processingPayment, setProcessingPayment] = useState(false)
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
      setError('Please select a shipping address.')
      return
    }

    try {
      setPlacingOrder(true)
      setError('')

      // Step 1: Create order
      const orderResponse = await api.post('/api/orders', {
        addressId: selectedAddressId,
        items: cart.items.map((item) => ({
          productId: item.productId,
          quantity: item.quantity,
        })),
      })

      const order = orderResponse.data

      // Step 2: Create payment
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

  if (loading) {
    return <p>Loading checkout...</p>
  }

  if (!cart) {
    return <p>Cart not found.</p>
  }

  if (cart.items.length === 0) {
    return <p>Your cart is empty.</p>
  }

  return (
    <div>
      <h1>Checkout</h1>

      {error && <p>{error}</p>}

      <section>
        <h2>Order Summary</h2>

        {cart.items.map((item) => (
          <div key={item.id}>
            <h3>{item.productName}</h3>

            <p>Price: ₹{item.price}</p>

            <p>Quantity: {item.quantity}</p>

            <p>Subtotal: ₹{item.subtotal}</p>
          </div>
        ))}

        <h2>Total: ₹{cart.total}</h2>
      </section>

      <section>
        <h2>Shipping Address</h2>

        {addresses.length === 0 ? (
          <p>No saved addresses found.</p>
        ) : (
          addresses.map((address) => (
            <div key={address.id}>
              <label>
                <input
                  type="radio"
                  name="shippingAddress"
                  value={address.id}
                  checked={
                    selectedAddressId === address.id
                  }
                  onChange={() =>
                    setSelectedAddressId(address.id)
                  }
                />

                <strong>{address.fullName}</strong>
              </label>

              <p>{address.phone}</p>

              <p>{address.addressLine}</p>

              <p>
                {address.city}, {address.state}
              </p>

              <p>
                {address.postalCode}, {address.country}
              </p>

              <p>Type: {address.type}</p>
            </div>
          ))
        )}

        <button
          type="button"
          onClick={() =>
            setShowAddressForm(!showAddressForm)
          }
        >
          {showAddressForm
            ? 'Cancel'
            : 'Add New Address'}
        </button>

        {showAddressForm && (
          <form onSubmit={createAddress}>
            <div>
              <label>Full Name</label>

              <input
                name="fullName"
                value={form.fullName}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label>Phone</label>

              <input
                name="phone"
                value={form.phone}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label>Address</label>

              <input
                name="addressLine"
                value={form.addressLine}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label>City</label>

              <input
                name="city"
                value={form.city}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label>State</label>

              <input
                name="state"
                value={form.state}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label>Postal Code</label>

              <input
                name="postalCode"
                value={form.postalCode}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label>Country</label>

              <input
                name="country"
                value={form.country}
                onChange={handleChange}
                required
              />
            </div>

            <div>
              <label>Address Type</label>

              <select
                name="type"
                value={form.type}
                onChange={handleChange}
              >
                <option value="HOME">HOME</option>
                <option value="WORK">WORK</option>
                <option value="OTHER">OTHER</option>
              </select>
            </div>

            <button type="submit">
              Save Address
            </button>
          </form>
        )}
      </section>

      {!payment && addresses.length > 0 && (
        <section>
          <h2>Payment Method</h2>

          <label>
            <input
              type="radio"
              name="paymentMethod"
              value="COD"
              checked={paymentMethod === 'COD'}
              onChange={(event) =>
                setPaymentMethod(event.target.value)
              }
            />
            Cash on Delivery
          </label>

          <br />

          <label>
            <input
              type="radio"
              name="paymentMethod"
              value="UPI"
              checked={paymentMethod === 'UPI'}
              onChange={(event) =>
                setPaymentMethod(event.target.value)
              }
            />
            UPI
          </label>

          <br />

          <label>
            <input
              type="radio"
              name="paymentMethod"
              value="CARD"
              checked={paymentMethod === 'CARD'}
              onChange={(event) =>
                setPaymentMethod(event.target.value)
              }
            />
            Card
          </label>

          <br />

          <button
            type="button"
            disabled={!selectedAddressId || placingOrder}
            onClick={placeOrder}
          >
            {placingOrder
              ? 'Creating Order...'
              : 'Continue to Payment'}
          </button>
        </section>
      )}

       {payment && (
         <section>
           <h2>Payment</h2>

           <p>Order ID: {payment.orderId}</p>

           <p>Amount: ₹{payment.amount}</p>

           <p>Method: {payment.method}</p>

           <p>Status: {payment.status}</p>

           {payment.status === 'PENDING' && (
             <div>
               <button
                 type="button"
                 disabled={processingPayment}
                 onClick={() => processPayment('SUCCESS')}
               >
                 {processingPayment
                   ? 'Processing...'
                   : 'Pay Now'}
               </button>

               <button
                 type="button"
                 disabled={processingPayment}
                 onClick={() => processPayment('FAILED')}
               >
                 Simulate Failed Payment
               </button>
             </div>
           )}

           {payment.status === 'SUCCESS' && (
             <div>
               <p>Payment successful.</p>

               <p>
                 Transaction ID: {payment.transactionId}
               </p>
             </div>
           )}

           {payment.status === 'FAILED' && (
             <p>Payment failed. Please try again.</p>
           )}
         </section>
       )}

    </div>
  )
}

export default CheckoutPage