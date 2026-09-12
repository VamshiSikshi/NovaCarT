import api from '../api/axios'

export const getMyProfile = async () => {
  const response = await api.get('/api/profile/me')
  return response.data
}

export const updateMyProfile = async (profileData) => {
  const response = await api.put('/api/profile/me', profileData)
  return response.data
}