import { useEffect, useState } from 'react';
import {
  Box,
  Button,
  Checkbox,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  Grid,
  TextField,
} from '@mui/material';

const emptyAddress = {
  street: '',
  city: '',
  state: '',
  zipCode: '',
  country: '',
  primary: false,
};

export default function AddressFormDialog({ open, onClose, onSubmit, initialData, title, submitting }) {
  const [form, setForm] = useState(initialData || emptyAddress);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (open) {
      setForm(initialData || emptyAddress);
      setErrors({});
    }
  }, [open, initialData]);

  const handleChange = (field) => (event) => {
    const value = field === 'primary' ? event.target.checked : event.target.value;
    setForm((prev) => ({ ...prev, [field]: value }));
    setErrors((prev) => ({ ...prev, [field]: '' }));
  };

  const validate = () => {
    const nextErrors = {};
    ['street', 'city', 'state', 'zipCode', 'country'].forEach((field) => {
      if (!form[field]?.trim()) {
        nextErrors[field] = 'Required';
      }
    });
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = () => {
    if (!validate() || submitting) return;
    onSubmit(form);
  };

  const handleClose = () => {
    if (submitting) return;
    setForm(initialData || emptyAddress);
    setErrors({});
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <Grid container spacing={2} sx={{ mt: 0.5 }}>
          <Grid size={{ xs: 12 }}>
            <TextField
              fullWidth
              label="Street"
              value={form.street}
              onChange={handleChange('street')}
              error={Boolean(errors.street)}
              helperText={errors.street}
              disabled={submitting}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              fullWidth
              label="City"
              value={form.city}
              onChange={handleChange('city')}
              error={Boolean(errors.city)}
              helperText={errors.city}
              disabled={submitting}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              fullWidth
              label="State"
              value={form.state}
              onChange={handleChange('state')}
              error={Boolean(errors.state)}
              helperText={errors.state}
              disabled={submitting}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              fullWidth
              label="Zip Code"
              value={form.zipCode}
              onChange={handleChange('zipCode')}
              error={Boolean(errors.zipCode)}
              helperText={errors.zipCode}
              disabled={submitting}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              fullWidth
              label="Country"
              value={form.country}
              onChange={handleChange('country')}
              error={Boolean(errors.country)}
              helperText={errors.country}
              disabled={submitting}
            />
          </Grid>
          <Grid size={{ xs: 12 }}>
            <FormControlLabel
              control={
                <Checkbox
                  checked={form.primary}
                  onChange={handleChange('primary')}
                  disabled={submitting}
                />
              }
              label="Primary address"
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={handleClose} disabled={submitting}>
          Cancel
        </Button>
        <Button
          variant="contained"
          onClick={handleSubmit}
          disabled={submitting}
          startIcon={submitting ? <CircularProgress size={18} color="inherit" /> : null}
        >
          Save
        </Button>
      </DialogActions>
    </Dialog>
  );
}
