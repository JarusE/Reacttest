import { useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Chip,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import AddressFormDialog from './AddressFormDialog';
import ConfirmDialog from './ConfirmDialog';

export default function AddressList({ addresses, onAdd, onUpdate, onDelete, loading }) {
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingAddress, setEditingAddress] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [deleteTarget, setDeleteTarget] = useState(null);

  const openCreateDialog = () => {
    setEditingAddress(null);
    setDialogOpen(true);
    setError('');
  };

  const openEditDialog = (address) => {
    setEditingAddress(address);
    setDialogOpen(true);
    setError('');
  };

  const handleSubmit = async (formData) => {
    setSubmitting(true);
    try {
      if (editingAddress) {
        await onUpdate(editingAddress.id, formData);
      } else {
        await onAdd(formData);
      }
      setDialogOpen(false);
      setEditingAddress(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteConfirm = async () => {
    if (!deleteTarget) return;
    setSubmitting(true);
    try {
      await onDelete(deleteTarget.id);
      setDeleteTarget(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Paper sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h6">Addresses</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={openCreateDialog}
          disabled={loading || submitting}
        >
          Add Address
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      <TableContainer sx={{ overflowX: 'auto' }}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Street</TableCell>
              <TableCell>City</TableCell>
              <TableCell>State</TableCell>
              <TableCell>Zip</TableCell>
              <TableCell>Country</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {addresses.map((address) => (
              <TableRow key={address.id} hover>
                <TableCell>
                  {address.street}
                  {address.primary && (
                    <Chip label="Primary" size="small" color="primary" sx={{ ml: 1 }} />
                  )}
                </TableCell>
                <TableCell>{address.city}</TableCell>
                <TableCell>{address.state}</TableCell>
                <TableCell>{address.zipCode}</TableCell>
                <TableCell>{address.country}</TableCell>
                <TableCell align="right">
                  <IconButton
                    size="small"
                    onClick={() => openEditDialog(address)}
                    aria-label="Edit address"
                    disabled={loading || submitting}
                  >
                    <EditIcon fontSize="small" />
                  </IconButton>
                  <IconButton
                    size="small"
                    color="error"
                    onClick={() => setDeleteTarget(address)}
                    aria-label="Delete address"
                    disabled={loading || submitting}
                  >
                    <DeleteIcon fontSize="small" />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
            {addresses.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} align="center">
                  No addresses yet. Add one to get started.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <AddressFormDialog
        open={dialogOpen}
        onClose={() => {
          if (!submitting) {
            setDialogOpen(false);
            setEditingAddress(null);
          }
        }}
        onSubmit={handleSubmit}
        initialData={editingAddress}
        title={editingAddress ? 'Edit Address' : 'Add Address'}
        submitting={submitting}
      />

      <ConfirmDialog
        open={Boolean(deleteTarget)}
        title="Delete address"
        message={`Delete ${deleteTarget?.street || 'this address'}? This action cannot be undone.`}
        confirmLabel="Delete"
        loading={submitting}
        onConfirm={handleDeleteConfirm}
        onCancel={() => !submitting && setDeleteTarget(null)}
      />
    </Paper>
  );
}
