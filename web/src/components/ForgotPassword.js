import React, { useState } from "react";
import { TextField, Button, Typography } from "@mui/material";
import { toast } from "react-toastify";
import { Link, useNavigate} from "react-router-dom";

function ForgotPassword() {; // Thêm state cho mật khẩu xác nhận
  const [email, setEmail] = useState("");
  const [errorMessage] = useState(""); // Thêm state cho thông báo lỗi
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();


    const apiUrl = 'http://localhost:8080/shoestore/api/account/forgotpassword';

    fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: email
      })
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Máy chủ không phản hồi. Vui lòng thực hiện lại sau ít phút');
        }
        return response.json();
      })
      .then(data => {
        console.log("Data register: ", data)
        console.log(JSON.stringify({
          
          email: email
        }))
        toast.success('Vui lòng kiểm tra email của bạn', {
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
        console.error('Có vấn đề khi phản hồi yêu cầu của bạn', error);
      });
  };

  return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <div style={{ flex: 1 }}>
        <img src="https://i.pinimg.com/564x/36/f0/e7/36f0e76abd4a79445d4914b5d9c72bf3.jpg" alt="Large" style={{ width: '67%', height: 'auto', paddingLeft: '120px' }} />
      </div>
      <div style={{ flex: 1, maxWidth: '400px', margin: '0 auto', textAlign: 'center', paddingRight: '140px', paddingLeft: '100px' }}>
        <div style={{ border: '1px solid #ccc', borderRadius: '5px', padding: '20px', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)' }}>
          <form onSubmit={handleSubmit}>
          <Typography variant="h4" style={{ marginBottom: '20px' }}>
              Quên mật khẩu?
              </Typography>
              <TextField
              label="Email"
              variant="outlined"
              fullWidth
              margin="normal"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            {errorMessage && (
              <Typography variant="body2" color="error" style={{ marginTop: '10px' }}>
                {errorMessage}
              </Typography>
            )}
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
              Xác nhận
            </Button>
          </form>
          <Typography variant="body2" style={{ marginTop: '10px' }}>
            Khi quên mật khẩu, bạn đồng ý cung cấp thông tin với chúng tôi <Link to="#" style={{ color: '#f4511e', textDecoration: 'none' }}>Chính sách người dùng</Link>
          </Typography>
        </div>
      </div>
    </div>
  );
}

export default ForgotPassword;
