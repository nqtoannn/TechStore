import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { toast } from "react-toastify";
import { TextField, Button, Box, CircularProgress, Alert, Switch, Avatar, Typography, Grid } from '@mui/material';
import { getToken } from '../utils/getToken';
import userAvatar from '../assets/testimonial-1.jpg'; // Import the uploaded image
import { useNavigate } from 'react-router-dom';

const PersonalInfo = () => {
  const [isEditing, setIsEditing] = useState(false);
  const [userInfo, setUserInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [updateError, setUpdateError] = useState(null);
  const [updateSuccess, setUpdateSuccess] = useState(null);
  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');
  const token = getToken();

  useEffect(() => {
    console.log(userId);
    console.log(token);
    axios.get(`http://localhost:8080/techstore/api/customer/findById/${userId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
      .then(response => {
        const data = response.data;
        if (data.status === 'OK') {
          setUserInfo(data.data[0]);
        } else {
          setError('Failed to fetch user data');
        }
        setLoading(false);
      })
      .catch(error => {
        setError(error.message);
        setLoading(false);
      });
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUserInfo({
      ...userInfo,
      [name]: value
    });
  };

  const handleToggleChange = (name) => (e) => {
    setUserInfo({
      ...userInfo,
      [name]: e.target.checked
    });
  };

  const toggleEdit = () => {
    setIsEditing(!isEditing);
    setUpdateError(null); 
    setUpdateSuccess(null); 
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const updatedData = {
      id: userInfo.id,
      fullName: userInfo.fullName,
      phoneNumber: userInfo.phoneNumber,
    };

    console.log("updatedData: ", updatedData);

    fetch('http://localhost:8080/techstore/api/customer/updateUserProfile', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(updatedData)
    })
      .then(response => response.json())
      .then(data => {
        if (data.status === 'OK') {
          toast.success('Cập nhật thông tin thành công!', {
            onClose: () => { },
            toastStyle: {
              background: "#4CAF50",
              color: "white",
              border: "1px solid #388E3C",
              borderRadius: "8px",
              boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)",
            }
          });
          setUpdateSuccess('Cập nhật thông tin thành công!');
        } else {
          toast.error('Cập nhật thông tin thất bại', {
            onClose: () => { },
            toastStyle: {
              background: "#4CAF50", 
              color: "white", 
              border: "1px solid #388E3C", 
              borderRadius: "8px", 
              boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)", 
            }
          });
          setUpdateError('Cập nhật thông tin thất bại');
        }
        setIsEditing(false);
      })
      .catch(error => {
        setUpdateError("Lỗi : " + error.message);
      });
  };

  if (loading) return <CircularProgress />;
  if (error) return <Alert severity="error">Error: {error}</Alert>;

  return (
    userInfo && (
  
      <div className="flex bg-gray-100 rounded-lg p-6 mb-6 mx-auto mt-8 shadow-md min-h-[74vh] max-w-7xl ">
      <form onSubmit={handleSubmit} className="flex flex-col w-full">
        <div className="flex flex-wrap md:flex-nowrap gap-6">
          {/* Left side - Avatar and Actions */}
          <div className="flex flex-col items-center w-full md:w-1/3 bg-white p-6 rounded-lg shadow">
            <img
              src={userAvatar}
              alt="User Avatar"
              className="w-96 h-96 mb-4 rounded-full"
            />
            <p className="text-sm text-gray-500 mb-4">
              Allowed *.jpeg, *.jpg, *.png, *.gif max size of 3 Mb
            </p>
            <button
              onClick={() => navigate(`/auth/repassword/${userInfo.id}`)}
              className="w-full bg-yellow-500 hover:bg-yellow-600 text-white py-2 rounded-md font-semibold mb-4"
            >
              Đổi mật khẩu
            </button>

            {/* Switches for Banned and Email Verified */}
            <div className="flex items-center justify-between w-full mb-4">
              <label className="text-sm font-medium">Nhận email quảng cáo từ chúng tôi</label>
              <Switch
              checked={!userInfo.banned}
              onChange={handleToggleChange('banned')}
              color="primary"
            />
            </div>
            <div className="flex items-center justify-between w-full">
              <label className="text-sm font-medium">Đã xác nhận email</label>
              <Switch
              checked={!userInfo.banned}
              onChange={handleToggleChange('banned')}
              color="primary"
            />
            </div>
            <button className="mt-4 text-red-600 w-full bg-rose-100 hover:bg-rose-300 rounded-md py-2 font-semibold">
              Vô hiệu tài khoản
            </button>
          </div>
          <div className="flex-grow bg-white p-6 rounded-lg shadow">
            <p className='pb-6 font-bold'>Thông tin cá nhân</p>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <TextField
                label="ID"
                value={userInfo.id}
                InputProps={{ readOnly: true }}
                variant="outlined"
                fullWidth
              />
              <TextField
                label="Tên"
                name="fullName"
                value={userInfo.fullName}
                onChange={handleChange}
                InputProps={{ readOnly: !isEditing }}
                variant="outlined"
                fullWidth
              />
              <TextField
                label="Email"
                value={userInfo.email}
                InputProps={{ readOnly: true }}
                variant="outlined"
                fullWidth
              />
              <TextField
                label="Số điện thoại"
                name="phoneNumber"
                value={userInfo.phoneNumber}
                onChange={handleChange}
                InputProps={{ readOnly: !isEditing }}
                variant="outlined"
                fullWidth
              />
              

            </div>
            {isEditing ? (
              <div className="flex justify-evenly mt-4">
                <button
                  type="submit"
                  className="bg-green-500 hover:bg-green-600 text-white font-semibold py-2 px-4 rounded-md"
                >
                  Lưu thay đổi
                </button>
                <button
                  onClick={toggleEdit}
                  className="bg-gray-400 hover:bg-gray-500 text-white font-semibold py-2 px-4 rounded-md"
                >
                  Hủy
                </button>
              </div>
            ) : (
              <button
                onClick={toggleEdit}
                className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 rounded-md mt-4"
              >
                Chỉnh sửa thông tin
              </button>
            )}
          </div>
        </div>
      </form>
    </div>
    )
  );
};

export default PersonalInfo;
