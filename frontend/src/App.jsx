import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Jobs from "./pages/Jobs";
import CreateJob from "./pages/CreateJob";
import Navbar from "./components/Navbar";
import ApplyJob from "./pages/ApplyJob";
const App = () => {
  const isLoggedIn = !!localStorage.getItem("token"); // check token

  return (
    <>
      {isLoggedIn && <Navbar />}
      <Routes>
        <Route path="/" element={isLoggedIn ? <Jobs /> : <Navigate to="/login" />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/jobs" element={isLoggedIn ? <Jobs /> : <Navigate to="/login" />} />
        <Route path="/create-job" element={isLoggedIn ? <CreateJob /> : <Navigate to="/login" />} />
        <Route path="/apply/:jobId" element={<ApplyJob />} />
      </Routes>
    </>
  );
};

export default App;