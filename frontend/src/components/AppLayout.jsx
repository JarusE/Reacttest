import { AppBar, Box, Container, Toolbar, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

export default function AppLayout({ children }) {
  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar position="static" color="transparent">
        <Toolbar sx={{ px: { xs: 2, sm: 3 } }}>
          <Typography
            component={RouterLink}
            to="/"
            variant="h6"
            sx={{
              color: 'text.primary',
              textDecoration: 'none',
              fontWeight: 700,
              letterSpacing: '-0.02em',
            }}
          >
            Admin dashboard
          </Typography>
        </Toolbar>
      </AppBar>
      <Container maxWidth="lg" sx={{ py: 4, px: { xs: 2, sm: 3 } }}>
        {children}
      </Container>
    </Box>
  );
}
