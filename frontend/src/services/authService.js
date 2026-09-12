import api from '../api/axios'

export const changePassword = async (passwordData) => {
  const response = await api.put(
    '/api/auth/change-password',
    passwordData
  )

  return response.data
}