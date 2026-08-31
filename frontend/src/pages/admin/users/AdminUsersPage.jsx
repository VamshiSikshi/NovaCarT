import { useEffect, useState } from 'react'
import api from '../../../api/axios'
import { useAuth } from '../../../context/AuthContext'

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

  if (loading) {
    return <p>Loading users...</p>
  }

  return (
    <div>
      <h1>Admin Users</h1>

      {error && <p>{error}</p>}

      <div>
        <label>
          Role:{' '}
          <select
            value={roleFilter}
            onChange={handleFilterChange}
          >
            <option value="ALL">ALL</option>
            <option value="USER">USER</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </label>
      </div>

      {users.length === 0 ? (
        <p>No users found.</p>
      ) : (
        <div>
          {users.map((currentUser) => {
            const isCurrentUser =
              currentUser.username === user?.username

            return (
              <div key={currentUser.id}>
                <h2>
                  User #{currentUser.id}
                </h2>

                <p>
                  Username: {currentUser.username}
                </p>

                <p>
                  Email: {currentUser.email}
                </p>

                <p>
                  Current Role: {currentUser.role}
                </p>

                <label>
                  Change Role:{' '}

                  <select
                    value={currentUser.role}
                    disabled={
                      isCurrentUser ||
                      updatingUserId === currentUser.id
                    }
                    onChange={(event) =>
                      handleRoleChange(
                        currentUser.id,
                        event.target.value
                      )
                    }
                  >
                    <option value="USER">USER</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                </label>

                {isCurrentUser && (
                  <p>
                    You cannot change your own role.
                  </p>
                )}
              </div>
            )
          })}
        </div>
      )}

      <div>
        <button
          type="button"
          disabled={page === 0}
          onClick={() => setPage(page - 1)}
        >
          Previous
        </button>

        <span>
          {' '}
          Page {page + 1} of {totalPages}{' '}
        </span>

        <button
          type="button"
          disabled={
            page + 1 >= totalPages
          }
          onClick={() => setPage(page + 1)}
        >
          Next
        </button>
      </div>
    </div>
  )
}

export default AdminUsersPage