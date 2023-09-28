import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import FileUpload from './containers/FileUpload';
import FileDownload from './containers/FileDownload';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<FileUpload />} />
                <Route path="/download/:fileId" element={<FileDownload />} />
            </Routes>
        </Router>
    );
}

export default App;
