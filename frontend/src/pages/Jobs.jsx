import React, { useEffect, useState } from "react";
import { Container, Typography, Card, CardContent, Grid, Button } from "@mui/material";
import API from "../api/axios";
import { useNavigate } from "react-router-dom";

const Jobs = () => {
  const [jobs, setJobs] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchJobs = async () => {
      try {
        const res = await API.get("/jobs");
        setJobs(res.data);
      } catch (err) {
        console.error(err);
        alert("Failed to fetch jobs");
      }
    };

    fetchJobs();
  }, []);

  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h4" sx={{ mb: 3 }}>
        All Jobs
      </Typography>

      <Grid container spacing={2}>
        {jobs.map((job) => (
          <Grid item xs={12} md={6} key={job.id}>
            <Card>
              <CardContent>

                <Typography variant="h6">
                  {job.title}
                </Typography>

                <Typography variant="body2" sx={{ mt: 1 }}>
                  {job.description}
                </Typography>

                <Typography variant="caption" sx={{ display: "block", mt: 1 }}>
                  Role: {job.role}
                </Typography>

                <Button
                  variant="contained"
                  sx={{ mt: 2 }}
                  onClick={() => navigate(`/apply/${job.id}`)}
                >
                  Upload Resume
                </Button>

              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Container>
  );
};

export default Jobs;