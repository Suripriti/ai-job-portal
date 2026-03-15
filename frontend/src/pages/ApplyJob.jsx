import React, { useState } from "react";
import { Container, Typography, Button, Box } from "@mui/material";
import { useParams } from "react-router-dom";
import API from "../api/axios";

const ApplyJob = () => {

  const { jobId } = useParams();
  const [file, setFile] = useState(null);
  const [score, setScore] = useState(null);

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

      const res = await API.post(
        "/applications/apply",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data"
          }
        }
      );

      setScore(res.data.score);

    } catch (err) {
      console.error(err);
      alert("Failed to upload resume");
    }
  };

  return (
    <Container sx={{ mt: 5 }}>

      <Typography variant="h4" sx={{ mb: 3 }}>
        Upload Resume
      </Typography>

      <Box>

        <input
          type="file"
          onChange={(e) => setFile(e.target.files[0])}
        />

        <Button
          variant="contained"
          sx={{ mt: 2 }}
          onClick={handleUpload}
        >
          Calculate Score
        </Button>

      </Box>

      {score !== null && (
        <Typography variant="h5" sx={{ mt: 3 }}>
          Similarity Score: {score}
        </Typography>
      )}

    </Container>
  );
};

export default ApplyJob;