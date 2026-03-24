import React, { useState } from "react";
import { Container, Typography, Button, Box } from "@mui/material";
import { useParams } from "react-router-dom";
import API from "../api/axios";

const ApplyJob = () => {

  const { jobId } = useParams();
  const [file, setFile] = useState(null);
  const [score, setScore] = useState(null);

  // ✅ File validation function
  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];

    if (!selectedFile) return;

    const allowedTypes = [
      "application/pdf",
      "application/msword",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    ];

    if (!allowedTypes.includes(selectedFile.type)) {
      alert("❌ Please upload a valid file (PDF or DOC/DOCX only)");
      setFile(null);
      e.target.value = null; // reset input
      return;
    }

    setFile(selectedFile);
  };

  const handleUpload = async () => {

    if (!file) {
      alert("Please upload resume");
      return;
    }

    const formData = new FormData();
    formData.append("jobId", jobId);
    formData.append("userId", localStorage.getItem("userId"));
    formData.append("resumeFile", file);

    try {

      const res = await API.post("/applications/apply", formData);

      setScore(res.data.score);

    } catch (err) {
      console.error(err);

      if (err.response?.status === 400) {
        alert(err.response.data?.message || "You have already applied to this job.");
      } else {
        alert("Failed to upload resume");
      }
    }
  };

  return (
    <Container sx={{ mt: 5 }}>

      <Typography variant="h4" sx={{ mb: 3 }}>
        Upload Resume
      </Typography>

      <Box>

        {/* ✅ File Input */}
        <input
          type="file"
          accept=".pdf,.doc,.docx"
          onChange={handleFileChange}
        />

        {/* ✅ Upload Button */}
        <Button
          variant="contained"
          sx={{ mt: 2 }}
          onClick={handleUpload}
        >
          Calculate Score
        </Button>

      </Box>

      {/* ✅ Show Score */}
      {score !== null && (
        <Typography variant="h5" sx={{ mt: 3 }}>
          Similarity Score: {score}
        </Typography>
      )}

    </Container>
  );
};

export default ApplyJob;