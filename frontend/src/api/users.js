const API_BASE = '/api/users';

export class ApiError extends Error {
  constructor(message, fieldErrors = {}) {
    super(message);
    this.name = 'ApiError';
    this.fieldErrors = fieldErrors;
  }
}

async function handleResponse(response) {
  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Request failed' }));
    const fieldErrors = {};
    let message = error.message;

    if (!message) {
      const entries = Object.entries(error);
      if (entries.length > 0) {
        entries.forEach(([field, msg]) => {
          fieldErrors[field] = msg;
        });
        message = Object.values(fieldErrors).join(', ');
      } else {
        message = 'Request failed';
      }
    }

    throw new ApiError(message, fieldErrors);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export async function fetchUsers() {
  const response = await fetch(API_BASE);
  return handleResponse(response);
}

export async function fetchUser(userId) {
  const response = await fetch(`${API_BASE}/${userId}`);
  return handleResponse(response);
}

export async function updateUser(userId, data) {
  const response = await fetch(`${API_BASE}/${userId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  return handleResponse(response);
}

export async function createAddress(userId, data) {
  const response = await fetch(`${API_BASE}/${userId}/addresses`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  return handleResponse(response);
}

export async function updateAddress(userId, addressId, data) {
  const response = await fetch(`${API_BASE}/${userId}/addresses/${addressId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  return handleResponse(response);
}

export async function deleteAddress(userId, addressId) {
  const response = await fetch(`${API_BASE}/${userId}/addresses/${addressId}`, {
    method: 'DELETE',
  });
  return handleResponse(response);
}
