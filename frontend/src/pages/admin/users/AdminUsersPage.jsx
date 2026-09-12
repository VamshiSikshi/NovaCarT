import { useEffect, useState } from 'react'
import api from '../../../api/axios'
import { useAuth } from '../../../context/AuthContext'
import '../../../styles/admin/admin-users.css'

function AdminUsersPage() {
  const { user } = useAuth()

  const [users, setUsers] = useState([])
  const [roleFilter, setRoleFilter] = useState('ALL')
  const [page, setPage] = useState(0)

  const [totalPages, setTotalPages] = useState(0)

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [updatingUserId, setUpdatingUserId] = useState(null)

  useEffect(() => {
    const loadUsers = async () => {
      try {
        setLoading(true)
        setError('')

        const params = {
          page,
          size: 10,
          sort: 'id,desc',
        }

        if (roleFilter !== 'ALL') {
          params.role = roleFilter
        }

        const response = await api.get(
          '/api/admin/users',
          { params }
        )

        setUsers(response.data.content)
        setTotalPages(response.data.totalPages)
      } catch (error) {
        setError(
          error.response?.data?.message ||
          'Failed to load users.'
        )
      } finally {
        setLoading(false)
      }
    }

    loadUsers()
  }, [page, roleFilter])

  const handleRoleChange = async (userId, newRole) => {
    try {
      setUpdatingUserId(userId)
      setError('')

      const response = await api.patch(
        `/api/admin/users/${userId}/role`,
        {
          role: newRole,
        }
      )

      setUsers((previousUsers) =>
        previousUsers.map((currentUser) =>
          currentUser.id === userId
            ? response.data
            : currentUser
        )
      )
    } catch (error) {
      setError(
        error.response?.data?.message ||
        'Failed to update user role.'
      )
    } finally {
      setUpdatingUserId(null)
    }
  }

  const handleFilterChange = (event) => {
    setRoleFilter(event.target.value)
    setPage(0)
  }

  const getInitial = (username) => {
    return username?.charAt(0)?.toUpperCase() || 'U'
  }

  if (loading) {
    return (
      <div className="admin-page-state">
        <div className="admin-spinner"></div>
        <p>Loading users...</p>
      </div>
    )
  }

  return (
    <div className="admin-users-page">

      {/* PAGE HEADER */}

      <div className="admin-page-header">
        <div>
          <p className="admin-eyebrow">
            USER MANAGEMENT
          </p>

          <h1>Users</h1>

          <p className="admin-page-description">
            Manage user accounts and administrator access.
          </p>
        </div>

        <div className="admin-user-filter">
          <label htmlFor="role-filter">
            Role
          </label>

          <select
            id="role-filter"
            value={roleFilter}
            onChange={handleFilterChange}
          >
            <option value="ALL">All Users</option>
            <option value="USER">Users</option>
            <option value="ADMIN">Admins</option>
          </select>
        </div>
      </div>

      {/* ERROR */}

      {error && (
        <div className="admin-alert admin-alert-error">
          <span>!</span>
          {error}
        </div>
      )}

      {/* USERS TABLE */}

      <section className="admin-table-card admin-users-list">

        <div className="admin-table-header">
          <h2>User Accounts</h2>

          <span>
            {users.length} user
            {users.length !== 1 ? 's' : ''} on this page
          </span>
        </div>

        {users.length === 0 ? (
          <div className="admin-empty-state">
            <div className="admin-empty-icon">
              U
            </div>

            <strong>No users found</strong>

            <p>
              No users match the selected role filter.
            </p>
          </div>
        ) : (
          <div>

            {/* TABLE HEADER */}

            <div className="admin-user-table-header">
              <div>User</div>
              <div>Email</div>
              <div>Role</div>
              <div>Access</div>
            </div>

            {/* USER ROWS */}

            {users.map((currentUser) => {
              const isCurrentUser =
                currentUser.username === user?.username

              const isUpdating =
                updatingUserId === currentUser.id

              const roleClass =
                currentUser.role === 'ADMIN'
                  ? 'admin-role'
                  : 'user-role'

              return (
                <div
                  key={currentUser.id}
                  className="admin-user-row"
                >

                  {/* IDENTITY */}

                  <div className="admin-user-identity">

                    <div className="admin-user-avatar">
                      {getInitial(currentUser.username)}
                    </div>

                    <div>
                      <strong>
                        {currentUser.username}
                      </strong>

                      <span>
                        User #{currentUser.id}
                      </span>
                    </div>

                  </div>

                  {/* EMAIL */}

                  <div
                    className="admin-user-email"
                    title={currentUser.email}
                  >
                    {currentUser.email}
                  </div>

                  {/* ROLE */}

                  <div>
                    <span
                      className={`admin-user-role ${roleClass}`}
                    >
                      {currentUser.role}
                    </span>
                  </div>

                  {/* ACCESS */}

                  <div className="admin-user-access">

                    <select
                      value={currentUser.role}
                      disabled={
                        isCurrentUser ||
                        isUpdating
                      }
                      onChange={(event) =>
                        handleRoleChange(
                          currentUser.id,
                          event.target.value
                        )
                      }
                      aria-label={`Change role for ${currentUser.username}`}
                    >
                      <option value="USER">
                        USER
                      </option>

                      <option value="ADMIN">
                        ADMIN
                      </option>
                    </select>

                    {isCurrentUser && (
                      <span className="admin-user-protected">
                        Your account
                      </span>
                    )}

                    {isUpdating && (
                      <span className="admin-user-updating">
                        Updating...
                      </span>
                    )}

                  </div>

                </div>
              )
            })}

          </div>
        )}

      </section>

      {/* PAGINATION */}

      {totalPages > 1 && (
        <div className="admin-pagination">

          <button
            type="button"
            disabled={page === 0}
            onClick={() =>
              setPage((previousPage) =>
                previousPage - 1
              )
            }
          >
            Previous
          </button>

          <span>
            Page {page + 1} of {totalPages}
          </span>

          <button
            type="button"
            disabled={
              page + 1 >= totalPages
            }
            onClick={() =>
              setPage((previousPage) =>
                previousPage + 1
              )
            }
          >
            Next
          </button>

        </div>
      )}

    </div>
  )
}

export default AdminUsersPage