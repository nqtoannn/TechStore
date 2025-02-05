import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuth } from "./AuthContext";
import FooterTop from "../../components/FooterTop";
import axios from 'axios';

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  const { isLoggedIn, login } = useAuth();
  const [errMsg] = useState("");
  const [loading] = useState(false);

  const getGoogleLoginUrl = async () => {
    try {
      const response = await axios.get('http://localhost:8080/techstore/api/auth/login-url');
      const loginUrl = response.data; 
      window.location.href = loginUrl;
    } catch (error) {
      console.error('Error fetching Google login URL:', error);
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const apiUrl = "http://localhost:8080/techstore/api/auth/authenticate";

    fetch(apiUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: email,
        password: password,
      }),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        if (!data.access_token) {
          toast.error("Đăng nhập thất bại! Tài khoản của bạn đã bị khóa");
          return;
        }

        localStorage.setItem("userId", data.accountId);
        localStorage.setItem("accessToken", data.access_token);
        localStorage.setItem("refreshToken", data.refresh_token);
        localStorage.setItem("role", data.role);
        login();
        toast.success("Đăng nhập thành công!");
        if (data.role === "ADMIN") {
          navigate("/manage/products");
        } else if (data.role === "CUSTOMER") {
          navigate("/shop");
        } else {
          navigate("/manage/orders");
        }
      })
      .catch((error) => {
        console.error("There was a problem with your authentication:", error);
        toast.error(
          "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin tài khoản."
        );
      });
  };

  return (
    // {isLoggedIn ? (
    //   <div><Typography variant="body1" style={{ marginBottom: '20px' }}>
    //           You are already logged in.
    //         </Typography></div>
            
    //   ): (
        <div>
          <div className="flex items-center justify-center  h-[50vh] bg-gray-100">
            <div className="bg-gray-950 rounded-lg p-8 max-w-screen-lg w-full center">
              <form onSubmit={handleSubmit}>
                <div className="mb-6">
                  <h2 className="text-xl font-semibold text-white text-center">
                    Đăng nhập
                  </h2>
                  <p className="mt-2 text-sm text-gray-400 text-center">
                  Vui lòng điền đủ thông tin của bạn!
                  </p>
                </div>

                <div className="mb-4">
                  <label className="block text-sm text-white" htmlFor="email">
                    Địa chỉ Email
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
                    Mật khẩu
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

                {errMsg && (
                  <p className="bg-red-500 text-white text-center py-2 rounded-md mb-4">
                    {errMsg}
                  </p>
                )}

                <button
                  type="submit"
                  className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 rounded-md"
                >
                  {loading ? "Đăng nhập..." : "Đăng nhập"}
                </button>

                <div class="flex items-center justify-center my-2 px-64">
                    <div class="border-t border-gray-300 flex-grow"></div>
                    <span class="mx-2 text-gray-500"> Hoặc </span>
                    <div class="border-t border-gray-300 flex-grow"></div>
                </div>


                <button
                  className="w-full bg-cyan-500 hover:bg-cyan-700 text-white font-bold py-2 rounded-md"
                  onClick={getGoogleLoginUrl}
                >
                  Đăng nhập bằng Google
                </button>  

                <p className="mt-4 text-center text-sm text-gray-400">
                  Bạn chưa có tài khoản?{" "}
                  <Link to="/signup" className="text-indigo-400 hover:underline">
                    Đăng ký ngay!
                  </Link>
                </p>
              </form>
            </div>
          </div>
          <FooterTop />
    </div>
      // )}
    
  );
}

export default Login;
