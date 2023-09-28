import React from 'react';
import { AppBar, Toolbar, Typography, Button, IconButton } from '@mui/material';
import GitHubIcon from '@mui/icons-material/GitHub';
import { Link } from 'react-router-dom';

const Layout = ({ children }) => {
    return (
        <>
            <AppBar position="static">
                <Toolbar style={{ justifyContent: 'space-between' }}>
                    <Typography variant="h6">
                        Fileshare
                    </Typography>
                    <Button color="inherit" component={Link} to="/">Upload</Button>
                    <IconButton color="inherit" aria-label="github-link" onClick={() => window.open('https://github.com/Steven-Posterick/fileshare', '_blank')}>
                        <GitHubIcon />
                    </IconButton>
                </Toolbar>
            </AppBar>
            <main style={{ marginTop: '80px' }}>
                {children}
            </main>
        </>
    );
}

export default Layout;