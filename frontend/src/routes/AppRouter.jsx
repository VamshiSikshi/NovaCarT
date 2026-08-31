import { Routes, Route } from 'react-router-dom'

import AppLayout from '../components/layout/AppLayout'
import AdminLayout from '../components/admin/AdminLayout'

import ProtectedRoute from './ProtectedRoute'
import AdminRoute from './AdminRoute'

import HomePage from '../pages/HomePage'
import LoginPage from '../pages/auth/LoginPage'
import RegisterPage from '../pages/auth/RegisterPage'

import ProductsPage from '../pages/products/ProductsPage'
import ProductDetailsPage from '../pages/products/ProductDetailsPage'

import CartPage from '../pages/cart/CartPage'
import CheckoutPage from '../pages/checkout/CheckoutPage'
import OrdersPage from '../pages/orders/OrdersPage'
import OrderDetailsPage from '../pages/orders/OrderDetailsPage'

import AdminDashboard from '../pages/admin/AdminDashboard'
import AdminProductsPage from '../pages/admin/products/AdminProductsPage'
import AdminCategoriesPage from '../pages/admin/categories/AdminCategoriesPage'
import AdminOrdersPage from '../pages/admin/orders/AdminOrdersPage'
import AdminOrderDetailsPage from '../pages/admin/orders/AdminOrderDetailsPage'
import AdminPaymentsPage from '../pages/admin/payments/AdminPaymentsPage'
import AdminPaymentDetailsPage from '../pages/admin/payments/AdminPaymentDetailsPage'
import AdminUsersPage from '../pages/admin/users/AdminUsersPage'

function AppRouter() {
  return (
    <Routes>

      {/* =========================
          CUSTOMER APPLICATION
          ========================= */}

      <Route element={<AppLayout />}>

        <Route
          path="/"
          element={<HomePage />}
        />

        <Route
          path="/login"
          element={<LoginPage />}
        />

        <Route
          path="/register"
          element={<RegisterPage />}
        />

        <Route
          path="/products"
          element={<ProductsPage />}
        />

        <Route
          path="/products/:productId"
          element={<ProductDetailsPage />}
        />

        <Route element={<ProtectedRoute />}>

          <Route
            path="/cart"
            element={<CartPage />}
          />

          <Route
            path="/checkout"
            element={<CheckoutPage />}
          />

          <Route
            path="/orders"
            element={<OrdersPage />}
          />

          <Route
            path="/orders/:orderId"
            element={<OrderDetailsPage />}
          />

        </Route>

      </Route>


      {/* =========================
          ADMIN APPLICATION
          ========================= */}

      <Route element={<AdminRoute />}>

        <Route element={<AdminLayout />}>

          <Route
            path="/admin"
            element={<AdminDashboard />}
          />

          <Route
            path="/admin/products"
            element={<AdminProductsPage />}
          />

          <Route
            path="/admin/categories"
            element={<AdminCategoriesPage />}
          />

          <Route
            path="/admin/orders"
            element={<AdminOrdersPage />}
          />

          <Route
            path="/admin/orders/:orderId"
            element={<AdminOrderDetailsPage />}
          />

          <Route
            path="/admin/payments"
            element={<AdminPaymentsPage />}
          />

          <Route
            path="/admin/payments/:id"
            element={<AdminPaymentDetailsPage />}
          />

          <Route
            path="/admin/users"
            element={<AdminUsersPage />}
          />

        </Route>

      </Route>

    </Routes>
  )
}

export default AppRouter