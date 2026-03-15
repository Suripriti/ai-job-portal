import React, { useState } from "react";
import { Container, TextField, Button, Typography, Box } from "@mui/material";
import API from "../api/axios";
import { useNavigate } from "react-router-dom";

const CreateJob = () => {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [role, setRole] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!title || !description || !role) {
      alert("Please fill all fields");
      return;
    }

    try {
      await API.post("/jobs", {
        title,
        description,
        role,
      });

      alert("Job created successfully!");
      navigate("/jobs");

    } catch (err) {
      console.error(err);
      alert("Failed to create job");
    }
  };

  return (
    <Container maxWidth="sm">
      <Box sx={{ mt: 8 }}>
        <Typography variant="h4" sx={{ mb: 2 }}>
          Create Job
        </Typography>

        <Box component="form" onSubmit={handleSubmit}>

          <TextField
            fullWidth
            label="Title"
            margin="normal"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />

          <TextField
            fullWidth
            label="Description"
            margin="normal"
            multiline
            rows={4}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />

          <TextField
            fullWidth
            label="Role"
            margin="normal"
            value={role}
            onChange={(e) => setRole(e.target.value)}
          />

          <Button type="submit" variant="contained" sx={{ mt: 2 }}>
            Create Job
          </Button>

        </Box>
      </Box>
    </Container>
  );
};

export default CreateJob;