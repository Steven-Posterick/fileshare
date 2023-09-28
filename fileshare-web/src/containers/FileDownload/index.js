import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { navigateToDownload, getFileDetails } from '../../services/fileShareService';
import { Container, Typography, Button, CircularProgress, Box } from '@mui/material';

const FileDownload = () => {
    const { fileId } = useParams();

    const [fileDetails, setFileDetails] = useState({});
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchFileDetails = async () => {
            try {
                const data = await getFileDetails(fileId);
                setFileDetails(data);
                setIsLoading(false);
            } catch (error) {
                console.error('Error fetching file details:', error);
                setIsLoading(false);
            }
        };

        fetchFileDetails();
    }, [fileId]);

    const handleDownload = async () => {
        navigateToDownload(fileId);
    };

    if (isLoading) {
        return (
            <Container maxWidth="sm" style={{ textAlign: 'center', padding: '40px 0' }}>
                <CircularProgress />
                <Typography variant="h6" style={{ marginTop: '20px' }}>Loading File Details...</Typography>
            </Container>
        );
    }

    return (
        <Container maxWidth="sm">
            <Box
                display="flex"
                flexDirection="column"
                alignItems="center"
                padding="20px"
                border="1px solid #e0e0e0"  // Gray border
                borderRadius="8px"           // Rounded corners
            >
                <Typography variant="h5" gutterBottom>
                    Ready to Download
                </Typography>
                <Typography variant="h6" color="textSecondary" gutterBottom>
                    {fileDetails.fileName}
                </Typography>
                <Typography variant="body2" color="textSecondary" gutterBottom>
                    Expiry: {fileDetails.expirationDate}
                </Typography>
                {fileDetails.readsLeft && (
                    <Typography variant="body2" color="textSecondary" gutterBottom>
                        Reads Left: {fileDetails.readsLeft}
                    </Typography>
                )}
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleDownload}
                    size="large"
                    style={{ marginTop: '20px' }}
                >
                    Download
                </Button>
            </Box>
        </Container>
    );
}

export default FileDownload;
