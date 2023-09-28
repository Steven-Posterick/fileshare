import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import FileUpload from './containers/FileUpload';
import FileDownload from './containers/FileDownload';
import Layout from './component/Layout'

// TODO: Add a home page, and route to a share page rather than download page.
function App() {
    return (
        <Router>
            <Layout>
                <Routes>
                    <Route path="/" element={<FileUpload />} />
                    <Route path="/download/:fileId" element={<FileDownload />} />
                </Routes>
            </Layout>
        </Router>
    );
}

export default App;
