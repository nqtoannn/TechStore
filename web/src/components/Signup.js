import React, { useState } from "react";
import { toast } from "react-toastify";
import { Link, useNavigate } from "react-router-dom";
import FooterTop from "./FooterTop";


function Signup() {
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState(""); 
  const [email, setEmail] = useState("");
  const [errorMessage, setErrorMessage] = useState(""); 
  const navigate = useNavigate();
  const [errMsg] = useState("");
  const defaultRole = "CUSTOMER"; 
  const [loading] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (password !== confirmPassword) {
      setErrorMessage("Password does not match!");
      return;
    }

    const apiUrl = 'http://localhost:8080/techstore/api/auth/register';

    fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        password: password,
        role: defaultRole,
        email: email
      })
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then(data => {
        console.log("Data register: ", data)
        console.log(JSON.stringify({
          password: password,
          role: defaultRole,
          email: email
        }))
        toast.success('Compelete registration. Please check your email to confirm.', {
          onClose: () => {
            navigate('/login');
          },
          toastStyle: {
            background: "#4CAF50",
            color: "white",
            border: "1px solid #388E3C",
            borderRadius: "8px",
            boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)",
          }
        });
      })
      .catch(error => {
        console.error('There was a problem with your registration:', error);
      });
  };

  return (
    <div>
      <div className="flex items-center justify-center  h-[60vh] bg-gray-100">
        <div className="bg-gray-950 rounded-lg p-8 max-w-screen-lg w-full center">
          <form onSubmit={handleSubmit}>
            <div className="mb-6">
              <h2 className="text-xl font-semibold text-white text-center">
                Registration Form
              </h2>
              <p className="mt-2 text-sm text-gray-400 text-center">
              You need to provide required information to get register with us.
              </p>
            </div>

            <div className="mb-4">
              <label className="block text-sm text-white" htmlFor="email">
                Email
              </label>
              <input
                type="email"
                id="email"
                className="block w-full mt-1 rounded-md bg-white/5 py-2 px-4 text-white outline-none shadow-sm ring-1 ring-inset ring-white/10 focus:ring-indigo-500"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="mb-4">
              <label className="block text-sm text-white" htmlFor="password">
                Password
              </label>
              <input
                type="password"
                id="password"
                className="block w-full mt-1 rounded-md bg-white/5 py-2 px-4 text-white outline-none shadow-sm ring-1 ring-inset ring-white/10 focus:ring-indigo-500"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <div className="mb-4">
              <label className="block text-sm text-white" htmlFor="password">
                Re-Enter Password
              </label>
              <input
                type="password"
                id="confirmPassword"
                className="block w-full mt-1 rounded-md bg-white/5 py-2 px-4 text-white outline-none shadow-sm ring-1 ring-inset ring-white/10 focus:ring-indigo-500"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
            </div>

            {errMsg && (
              <p className="bg-red-500 text-white text-center py-2 rounded-md mb-4">
                {errMsg}
              </p>
            )}

            <button
              type="submit"
              className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 rounded-md"
            >
              {loading ? "Sign Up" : "Sign Up"}
            </button>

            <p className="mt-4 text-center text-sm text-gray-400">
              Already have an account?{" "}
              <Link to="/login" className="text-indigo-400 hover:underline">
                Login
              </Link>
            </p>
          </form>
        </div>
      </div>
      <FooterTop />
    </div>
    
  );
}

export default Signup;
