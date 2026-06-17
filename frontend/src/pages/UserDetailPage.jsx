import { useCallback, useEffect, useState } from 'react';
import {
  Alert,
  Box,
  Breadcrumbs,
  Button,
  CircularProgress,
  Link,
  Snackbar,
  Stack,
  Typography,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { Link as RouterLink, useNavigate, useParams } from 'react-router-dom';
import {
  createAddress,
  deleteAddress,
  fetchUser,
  updateAddress,
  updateUser,
} from '../api/users';
import AddressList from '../components/AddressList';
import UserForm from '../components/UserForm';

export default function UserDetailPage() {
  const { userId } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [savingProfile, setSavingProfile] = useState(false);
  const [addressLoading, setAddressLoading] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

  const showSnackbar = (message, severity = 'success') => {
    setSnackbar({ open: true, message, severity });
  };

  const loadUser = useCallback(async (showPageLoader = true) => {
    if (showPageLoader) {
      setLoading(true);
    }
    setError('');
    try {
      const data = await fetchUser(userId);
      setUser(data);
    } catch (err) {
      setError(err.message);
    } finally {
      if (showPageLoader) {
        setLoading(false);
      }
    }
  }, [userId]);

  useEffect(() => {
    loadUser();
  }, [loadUser]);

  const handleSaveProfile = async (formData) => {
    setSavingProfile(true);
    try {
      const updated = await updateUser(userId, formData);
      setUser(updated);
      showSnackbar('Profile updated successfully.');
    } catch (err) {
      throw err;
    } finally {
      setSavingProfile(false);
    }
  };

  const handleAddAddress = async (formData) => {
    setAddressLoading(true);
    try {
      await createAddress(userId, formData);
      await loadUser(false);
      showSnackbar('Address added successfully.');
    } catch (err) {
      throw err;
    } finally {
      setAddressLoading(false);
    }
  };

  const handleUpdateAddress = async (addressId, formData) => {
    setAddressLoading(true);
    try {
      await updateAddress(userId, addressId, formData);
      await loadUser(false);
      showSnackbar('Address updated successfully.');
    } catch (err) {
      throw err;
    } finally {
      setAddressLoading(false);
    }
  };

  const handleDeleteAddress = async (addressId) => {
    setAddressLoading(true);
    try {
      await deleteAddress(userId, addressId);
      await loadUser(false);
      showSnackbar('Address deleted successfully.');
    } catch (err) {
      throw err;
    } finally {
      setAddressLoading(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error || !user) {
    return (
      <Box>
        <Alert severity="error" sx={{ mb: 2 }}>
          {error || 'User not found'}
        </Alert>
        <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/')}>
          Back to Users
        </Button>
      </Box>
    );
  }

  return (
    <Box>
      <Breadcrumbs sx={{ mb: 2 }}>
        <Link component={RouterLink} to="/" underline="hover" color="inherit">
          Users
        </Link>
        <Typography color="text.primary">
          {user.firstName} {user.lastName}
        </Typography>
      </Breadcrumbs>

      <Typography variant="h4" fontWeight={600} sx={{ mb: 3 }}>
        {user.firstName} {user.lastName}
      </Typography>

      <Stack spacing={3}>
        <UserForm user={user} onSave={handleSaveProfile} saving={savingProfile} />
        <AddressList
          addresses={user.addresses || []}
          onAdd={handleAddAddress}
          onUpdate={handleUpdateAddress}
          onDelete={handleDeleteAddress}
          loading={addressLoading}
        />
      </Stack>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert
          onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
          severity={snackbar.severity}
          variant="filled"
          sx={{ width: '100%' }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}
