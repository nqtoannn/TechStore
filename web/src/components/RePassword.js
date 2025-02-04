import React, { useState } from "react";
import { TextField, Button, Typography } from "@mui/material";
import { toast } from "react-toastify";
import { Link, useNavigate, useParams } from "react-router-dom";
import FooterTop from "./FooterTop";

function RePassword() {
  const { userId } = useParams();
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState(""); // Thêm state cho mật khẩu xác nhận
  const [email, setEmail] = useState("");
  const [errorMessage, setErrorMessage] = useState(""); // Thêm state cho thông báo lỗi
  const navigate = useNavigate();
  const [loading] = useState(false);

  const defaultRole = "CUSTOMER"; // Default role

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (password !== confirmPassword) {
      setErrorMessage("Mật khẩu không khớp. Vui lòng thử lại.");
      return;
    }

    const apiUrl = 'http://localhost:8080/techstore/api/account/resetPassword';

    fetch(apiUrl, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        password: password,
        userId: userId,
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
        toast.success('Đặt lại mật khẩu thành công!', {
          onClose: () => {
            navigate('/user-profile');
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
      {/* <div style={{ flex: 1, maxWidth: '400px', margin: '0 auto', textAlign: 'center', paddingRight: '140px', paddingLeft: '100px' }}>
        <div style={{ border: '1px solid #ccc', borderRadius: '5px', padding: '20px', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)' }}>
          <form onSubmit={handleSubmit}>
          <Typography variant="h4" style={{ marginBottom: '20px' }}>
              Đặt lại mật khẩu
              </Typography>
            <TextField
              label="Mật khẩu"
              variant="outlined"
              type="password"
              fullWidth
              margin="normal"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <TextField
              label="Nhập lại mật khẩu"
              variant="outlined"
              type="password"
              fullWidth
              margin="normal"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
            />
            {errorMessage && (
              <Typography variant="body2" color="error" style={{ marginTop: '10px' }}>
                {errorMessage}
              </Typography>
            )}
            <input type="hidden" value={defaultRole} />
            <Button
              variant="contained"
              color="primary"
              type="submit"
              fullWidth
              style={{ marginTop: '10px', backgroundColor: '#ff5722',
              '&:hover': {
                  backgroundColor: '#f4511e',
              } }}  
            >
              Đặt lại mật khẩu
            </Button>
          </form>
          <Typography variant="body2" style={{ marginTop: '10px' }}>
            Khi đặt lại mật khẩu, bạn đồng ý cung cấp thông tin với chúng tôi <Link to="#" style={{ color: '#f4511e', textDecoration: 'none' }}>Chính sách người dùng</Link>
          </Typography>
        </div>
      </div> */}
      <div className="flex items-center justify-center  h-[50vh] bg-gray-100">
            <div className="bg-gray-950 rounded-lg p-8 max-w-screen-lg w-full center">
              <form onSubmit={handleSubmit}>
                <div className="mb-6">
                  <h2 className="text-xl font-semibold text-white text-center">
                    Đặt lại mật khẩu
                  </h2>
                  <p className="mt-2 text-sm text-gray-400 text-center">
                  Vui lòng điền đủ thông tin của bạn!
                  </p>
                </div>

                <div className="mb-4">
                  <label className="block text-sm text-white" htmlFor="password">
                    Mật khẩu mới
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
                  <label className="block text-sm text-white" htmlFor="confirmPassword">
                    Nhập lại mật khẩu
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

                <button
                  type="submit"
                  className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 rounded-md"
                >
                  {loading ? "Đổi mật khẩu..." : "Đổi mật khẩu"}
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
  );
}

export default RePassword;
