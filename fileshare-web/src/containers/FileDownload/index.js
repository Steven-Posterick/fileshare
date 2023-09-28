import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { navigateToDownload, getFileDetails } from '../../services/fileShareService';
import { Container, Typography, Button, Paper } from '@mui/material';

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
        return <Typography>Loading...</Typography>;
    }

    return (
        <Container maxWidth="sm">
            <Paper elevation={3} style={{ padding: '20px', textAlign: 'center' }}>
                <Typography variant="h4" gutterBottom>
                    Download File
                </Typography>
                <Typography variant="h6" color="textSecondary" gutterBottom>
                    File ID: {fileId}
                </Typography>
                <Typography variant="body1" gutterBottom>
                    File Name: {fileDetails.fileName}
                </Typography>
                <Typography variant="body1" gutterBottom>
                    Expiration Date: {fileDetails.expirationDate}
                </Typography>
                {fileDetails.readsLeft && (
                    <Typography variant="body1" gutterBottom>
                        Reads Left: {fileDetails.readsLeft}
                    </Typography>
                )}
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleDownload}
                    size="large"
                >
                    Download
                </Button>
            </Paper>
        </Container>
    );
}

export default FileDownload;
