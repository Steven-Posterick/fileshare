import React, { useState } from 'react';
import { uploadFile } from '../../services/fileShareService';
import {
    Button,
    FormHelperText,
    FormControl,
    OutlinedInput,
    TextField,
    Container,
    Typography,
    Select,
    MenuItem,
    InputLabel,
    InputAdornment,
    Checkbox,
    FormControlLabel
} from '@mui/material';
import CloudUpload from '@material-ui/icons/CloudUpload';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { useNavigate } from 'react-router-dom';


const FileUpload = () => {
    const [expiration, setExpiration] = useState("WEEK");
    const [burnAfter, setBurnAfter] = useState("");
    const [burnAfterEnabled, setBurnAfterEnabled] = useState(false);
    const [burnAfterError, setBurnAfterError] = useState("");
    const [fileError, setFileError] = useState("");
    const [selectedFile, setSelectedFile] = useState(null);
    const navigate = useNavigate();


    const handleFileChange = (event) => {
        setSelectedFile(event.target.files[0]);
    };

    const handleUpload = async () => {
        try {
            if (burnAfterEnabled && (burnAfter === undefined || burnAfter <= 0)) {
                setBurnAfterError("Must be greater than 0");
                return;
            } else {
                setBurnAfterError("");
            }

            // Check if a file is selected
            if (!selectedFile) {
                setFileError("Please select a file");
                return;
            } else {
                setFileError("");
            }

            // Pass selectedFile here
            const response = await uploadFile(selectedFile, expiration, burnAfter);
            console.log("Upload successful:", response.data);
            navigate(`/download/${response.data}`);
        } catch (error) {
            console.error("Error uploading file:", error);
        }
    };

    return (
        <Container maxWidth="sm">
            <Typography variant="h4" style={{ marginBottom: '20px', textAlign: 'center' }}>
                Upload File
            </Typography>

            <FormControl fullWidth variant="outlined" style={{ marginBottom: '20px' }}>
                <OutlinedInput
                    id="file-upload"
                    type="file"
                    onChange={handleFileChange}
                    endAdornment={
                        <InputAdornment position="end">
                            <CloudUpload />
                        </InputAdornment>
                    }
                    labelWidth={90}
                />
                {fileError && <FormHelperText error>{fileError}</FormHelperText>}
            </FormControl>

            <FormControl fullWidth variant="outlined" style={{ marginBottom: '20px' }}>
                <InputLabel id="expiration-label">File Expiration</InputLabel>
                <Select
                    labelId="expiration-label"
                    value={expiration}
                    onChange={e => setExpiration(e.target.value)}
                    label="File Expiration"
                >
                    <MenuItem value="HOUR">1 Hour</MenuItem>
                    <MenuItem value="DAY">1 Day</MenuItem>
                    <MenuItem value="WEEK">1 Week</MenuItem>
                </Select>
            </FormControl>

            <FormControlLabel
                control={
                    <Checkbox
                        checked={burnAfterEnabled}
                        onChange={() => setBurnAfterEnabled(!burnAfterEnabled)}
                        name="burnAfterEnabled"
                        color="primary"
                    />
                }
                label="Enable Burn After"
            />

            {burnAfterEnabled && (
                <TextField
                    type="number"
                    label="Burn After (number of downloads)"
                    fullWidth
                    value={burnAfter}
                    onChange={e => setBurnAfter(e.target.value)}
                    style={{ marginBottom: '20px' }}
                    placeholder="Enter number of allowed downloads"
                    error={burnAfterError !== ""}
                    helperText={burnAfterError}
                />
            )}

            <Button
                variant="contained"
                color="primary"
                startIcon={<CloudUploadIcon />}
                onClick={handleUpload}
                fullWidth
            >
                Upload
            </Button>
        </Container>
    );
}

export default FileUpload;
