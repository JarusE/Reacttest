import { useEffect, useState } from 'react';
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Grid,
  Paper,
  TextField,
  Typography,
} from '@mui/material';
import { ApiError } from '../api/users';

export default function UserForm({ user, onSave, saving }) {
  const [form, setForm] = useState({
    email: user.email,
    firstName: user.firstName,
    lastName: user.lastName,
  });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    setForm({
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
    });
    setErrors({});
  }, [user]);

  const handleChange = (field) => (event) => {
    setForm((prev) => ({ ...prev, [field]: event.target.value }));
    setErrors((prev) => ({ ...prev, [field]: '' }));
  };

  const validate = () => {
    const nextErrors = {};
    if (!form.email.trim()) nextErrors.email = 'Please enter an email';
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) nextErrors.email = 'Email is invalid';
    if (!form.firstName.trim()) nextErrors.firstName = 'Please enter a first name';
    if (!form.lastName.trim()) nextErrors.lastName = 'Please enter a last name';
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!validate()) return;

    try {
      await onSave(form);
      setErrors({});
    } catch (err) {
      if (err instanceof ApiError && Object.keys(err.fieldErrors).length > 0) {
        setErrors({ ...err.fieldErrors, form: err.message });
      } else {
        setErrors({ form: err.message });
      }
    }
  };

  const handleReset = () => {
    setForm({
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
    });
    setErrors({});
  };

  const isDirty =
    form.email !== user.email ||
    form.firstName !== user.firstName ||
    form.lastName !== user.lastName;

  return (
    <Paper sx={{ p: 3 }}>
      <Typography variant="h6" gutterBottom>
        Profile
      </Typography>

      {errors.form && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {errors.form}
        </Alert>
      )}

      <Box component="form" onSubmit={handleSubmit} noValidate>
        <Grid container spacing={2}>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              fullWidth
              label="First Name"
              value={form.firstName}
              onChange={handleChange('firstName')}
              error={Boolean(errors.firstName)}
              helperText={errors.firstName}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              fullWidth
              label="Last Name"
              value={form.lastName}
              onChange={handleChange('lastName')}
              error={Boolean(errors.lastName)}
              helperText={errors.lastName}
            />
          </Grid>
          <Grid size={{ xs: 12 }}>
            <TextField
              fullWidth
              label="Email"
              type="email"
              value={form.email}
              onChange={handleChange('email')}
              error={Boolean(errors.email)}
              helperText={errors.email}
            />
          </Grid>
        </Grid>

        <Box sx={{ display: 'flex', gap: 1, mt: 3 }}>
          <Button
            type="submit"
            variant="contained"
            startIcon={saving ? <CircularProgress size={18} color="inherit" /> : null}
            disabled={saving || !isDirty}
          >
            Save Profile
          </Button>
          <Button variant="text" onClick={handleReset} disabled={saving || !isDirty}>
            Reset
          </Button>
        </Box>
      </Box>
    </Paper>
  );
}
