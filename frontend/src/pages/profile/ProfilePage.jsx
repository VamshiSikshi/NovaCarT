import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import '../../styles/profile.css'
import {
  getMyProfile,
  updateMyProfile,
} from '../../services/profileService'

function ProfilePage() {
  const { user } = useAuth()
  const navigate = useNavigate()

  const [profile, setProfile] = useState({
    firstName: '',
    lastName: '',
    phone: '',
    bio: '',
    profileImage: '',
  })

  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [imageError, setImageError] = useState(false)

  useEffect(() => {
    const loadProfile = async () => {
      setError('')

      try {
        const data = await getMyProfile()

        setProfile({
          firstName: data.firstName || '',
          lastName: data.lastName || '',
          phone: data.phone || '',
          bio: data.bio || '',
          profileImage: data.profileImage || '',
        })

        setImageError(false)
      } catch (error) {
        setError(
          error.response?.data?.message ||
            'Failed to load profile.'
        )
      } finally {
        setLoading(false)
      }
    }

    loadProfile()
  }, [])

  const handleChange = (event) => {
    const { name, value } = event.target

    setProfile((previous) => ({
      ...previous,
      [name]: value,
    }))

    if (name === 'profileImage') {
      setImageError(false)
    }

    setError('')
    setSuccess('')
  }

  const handleSubmit = async (event) => {
    event.preventDefault()

    setError('')
    setSuccess('')

    setSaving(true)

    try {
      const updatedProfile = await updateMyProfile(profile)

      setProfile({
        firstName: updatedProfile.firstName || '',
        lastName: updatedProfile.lastName || '',
        phone: updatedProfile.phone || '',
        bio: updatedProfile.bio || '',
        profileImage: updatedProfile.profileImage || '',
      })

      setImageError(false)

      setSuccess('Profile updated successfully.')
    } catch (error) {
      setError(
        error.response?.data?.message ||
          'Failed to update profile.'
      )
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <main className="profile-page">
        <div className="profile-container">
          <p>Loading profile...</p>
        </div>
      </main>
    )
  }

  return (
    <main className="profile-page">

      <div className="profile-container">

        <div className="profile-heading">
          <p className="profile-eyebrow">
            ACCOUNT
          </p>

          <h1>
            My Profile
          </h1>

          <p>
            Manage your personal information and profile details.
          </p>
        </div>

        {error && (
          <div
            className="profile-error"
            role="alert"
          >
            {error}
          </div>
        )}

        {success && (
          <div
            className="profile-success"
            role="status"
          >
            {success}
          </div>
        )}

        <form
          className="profile-form"
          onSubmit={handleSubmit}
        >
            <div className="profile-avatar-section">

              {profile.profileImage && !imageError ? (
                <img
                  src={profile.profileImage}
                  alt="Profile"
                  className="profile-avatar"
                  onError={() => setImageError(true)}
                />
              ) : (
                <div className="profile-avatar profile-avatar-placeholder">
                  {user?.username?.charAt(0).toUpperCase() || '?'}
                </div>
              )}

              <div className="profile-avatar-info">
                <h2>{user?.username}</h2>
                <p>Update your profile information below.</p>
              </div>

            </div>

          <div className="profile-account-info">

            <div>
              <span>Username</span>
              <strong>{user?.username || '—'}</strong>
            </div>

            <div>
              <span>Email</span>
              <strong>{user?.email || '—'}</strong>
            </div>

          </div>

          <div className="profile-fields">

            <div className="profile-field">
              <label htmlFor="firstName">
                First name
              </label>

              <input
                id="firstName"
                name="firstName"
                type="text"
                value={profile.firstName}
                onChange={handleChange}
                maxLength={50}
                disabled={saving}
                placeholder="Enter your first name"
              />
            </div>

            <div className="profile-field">
              <label htmlFor="lastName">
                Last name
              </label>

              <input
                id="lastName"
                name="lastName"
                type="text"
                value={profile.lastName}
                onChange={handleChange}
                maxLength={50}
                disabled={saving}
                placeholder="Enter your last name"
              />
            </div>

            <div className="profile-field">
              <label htmlFor="phone">
                Phone
              </label>

              <input
                id="phone"
                name="phone"
                type="tel"
                value={profile.phone}
                onChange={handleChange}
                maxLength={15}
                disabled={saving}
                placeholder="Enter your phone number"
              />
            </div>

            <div className="profile-field profile-field-full">
              <label htmlFor="bio">
                Bio
              </label>

              <textarea
                id="bio"
                name="bio"
                value={profile.bio}
                onChange={handleChange}
                maxLength={500}
                disabled={saving}
                placeholder="Tell us a little about yourself"
                rows={5}
              />
            </div>

            <div className="profile-field profile-field-full">
              <label htmlFor="profileImage">
                Profile image URL
              </label>

              <input
                id="profileImage"
                name="profileImage"
                type="url"
                value={profile.profileImage}
                onChange={handleChange}
                maxLength={500}
                disabled={saving}
                placeholder="https://example.com/profile.jpg"
              />
            </div>

          </div>

          <div className="profile-actions">

            <button
              type="submit"
              className="profile-save-button"
              disabled={saving}
            >
              {saving ? 'Saving...' : 'Save changes'}
            </button>

            <button
              type="button"
              className="profile-back-button"
              onClick={() => navigate('/products')}
              disabled={saving}
            >
              Back to products
            </button>

          </div>

        </form>

      </div>

    </main>
  )
}

export default ProfilePage