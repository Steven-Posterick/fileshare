import axios from 'axios';

const API_BASE = process.env.REACT_APP_FILE_SHARE_API_BASE;

export const uploadFile = async (file, expiration, burnAfter) => {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("expiration", expiration);
    formData.append("burnAfter", burnAfter);

    return axios.post(`${API_BASE}/files/upload`, formData);
}

export const navigateToDownload = (fileId) => {
    const fileDownloadUrl = `${API_BASE}/files/download/${fileId}`;
    window.open(fileDownloadUrl, '_blank');
}

export const getFileDetails = async (fileId) => {
    const response = await axios.get(`${API_BASE}/files/info/${fileId}`);
    return response.data;
};