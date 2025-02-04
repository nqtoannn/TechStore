import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../features/auth/AuthContext";
import "../styles/Header.css";
import { toast } from "react-toastify";
import icon from "../assets/logo.webp";
import { IoClose, IoSearchOutline } from "react-icons/io5";
import { FiShoppingBag, FiUser, FiLogOut } from "react-icons/fi";
import Container from "./Container";
import { Dialog, DialogTitle, DialogContent, DialogActions } from '@mui/material';



const Header = () => {
  const { isLoggedIn, logout } = useAuth();
  const navigate = useNavigate();
  const [openDialog,setOpenDialog] = useState(false);
  const [role, setRole] = useState(localStorage.getItem("role"));
  const [searchText, setSearchText] = useState("");

  const bottomNavigation = [
    { title: "Shop", link: "/Shop" },
    { title: "Products", link: "/productList" },
    { title: "Orders", link: "/list-order" },
    { title: "Cart", link: "/cart" },
    { title: "Blog", link: "/blog" },
    { title: "My Account", link: "/user-profile" },
  ];
  const bottomNavigationAdmin = [
    { title: "Product", link: "/manage/products" },
    { title: "Orders", link: "/manage/orders" },
    { title: "Employees", link: "/manage/employees" },
    { title: "Customers", link: "/manage/customers" },
    { title: "Revenue", link: "/manage/revenue" },
    { title: "Promotions", link: "/manage/promotions" },
    { title: "My Account", link: "/user-profile" },
  ];
  const bottomNavigationEmployee = [
    { title: "Home", link: "/" },
    { title: "Shop", link: "/Shop" },
    { title: "Cart", link: "/cart" },
    { title: "My Account", link: "/profile" },
    { title: "Blog", link: "/blog" },
    { title: "My Account", link: "/user-profile" },
  ];
  const bottomNavigationloginSingUp = [
    { title: "Login", link: "/login" },
    { title: "Sign Up", link: "/signup" },
  ];

  useEffect(() => {
    if (isLoggedIn) {
      setRole(localStorage.getItem("role"));
    }
  }, [isLoggedIn]);

  const handleOpenDialog = () => {
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  const handleLogout = () => {
    setOpenDialog(false);
    toast.success("Đăng xuất thành công!", {
      onClose: () => {
        navigate("/login");
      },
      toastStyle: {
        background: "#4CAF50",
        color: "white",
        border: "1px solid #388E3C",
        borderRadius: "8px",
        boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)",
      },
    });
    logout();
  };

  const handleKeyDown = (event) => {
    if (event.key === 'Enter') {
      navigate(`/productList?search=${event.target.value}`);
    }
  };

  return (
    <header>
    <div className="w-full  bg-whiteText sticky top-0 z-50">
        <div className="max-w-screen-xl mx-auto h-20 flex items-center justify-between px-4 lg:px-0">
          {/* Logo */}
          <Link to={"/shop"}>
            <img src={icon} alt="logo" className="w-44" />
          </Link>
          {/* Search Bar */}
          <div className="md:inline-flex max-w-3xl w-full relative">
            <input
              type="text"
              onChange={(e) => setSearchText(e.target.value)}
              value={searchText}
              placeholder="Search product"
              onKeyDown={handleKeyDown}
              className="w-full flex-1 rounded-full text-gray-900 text-lg placeholder:text-base placeholder: tracking-wide shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 placeholder: font-normal focus:ring-1 focus:ring-darkText sm:text-sm px-4 py-2"
            />
            {searchText ? (
              <IoClose
                className="absolute top-2.5 right-4 text-xl hover:text-red-500 cursor-pointer duration-200"
              />
            ) : (
              <IoSearchOutline className="absolute top-2.5 right-4 text-xl" />
            )}
          </div>
          {/* Menu Bar */}
          <div className="flex items-center gap-x-6 text-2xl">
            <Link to={"/user-profile"}>
              {/* {currentUser ? (
              <img
                src={currentUser?.avatar}
                alt="profileImg"
                className="w-10 h-10 rounded-full object-cover"
              />
            ) : (
              
            )} */}
                <FiUser className="hover:text-skyText duration-200 cursor-pointer" />
            </Link>
            <Link to={"/cart"} className="relative block">
              <FiShoppingBag className="hover:text-skyText duration-200 cursor-pointer" />
              <span className="inline-flex items-center justify-center bg-redText text-whiteText absolute -top-1 -right-2 text-[9px] rounded-full w-4 h-4">
                0
              </span>
            </Link>
            <Link className="relative block" onClick={handleOpenDialog}>
              <FiLogOut className="hover:text-skyText duration-200 cursor-pointer" />
            </Link>
          </div>
        </div>
      </div>
      {isLoggedIn ? (
        <>
          {role === "ADMIN" ? (
            <div className="w-full bg-slate-800 text-white">
              <Container className="py-2 max-w-4xl flex items-center gap-5 justify-between">
                {bottomNavigationAdmin.map(({ title, link }) => (
                  <Link
                    to={link}
                    key={title}
                    className="uppercase md:inline-flex text-sm font-semibold text-white/90 hover:text-white duration-200 relative overflow-hidden group"
                  >
                    {title}
                    <span className="inline-flex w-full h-[1px] bg-white absolute bottom-0 left-0 transform -translate-x-[105%] group-hover:translate-x-0 duration-300" />
                  </Link>
                ))}
              </Container>
            </div>
          ) : role === 'CUSTOMER' ? (
            <div className="w-full bg-slate-800 text-white">
              <Container className="py-2 max-w-4xl flex items-center gap-5 justify-between">
                {bottomNavigation.map(({ title, link }) => (
                  <Link
                    to={link}
                    key={title}
                    className="uppercase md:inline-flex text-sm font-semibold text-white/90 hover:text-white duration-200 relative overflow-hidden group"
                  >
                    {title}
                    <span className="inline-flex w-full h-[1px] bg-white absolute bottom-0 left-0 transform -translate-x-[105%] group-hover:translate-x-0 duration-300" />
                  </Link>
                ))}
              </Container>
            </div>
          ) : (
            <div className="w-full bg-slate-800 text-white">
            <Container className="py-2 max-w-4xl flex items-center gap-5 justify-between">
              {bottomNavigationEmployee.map(({ title, link }) => (
                <Link
                  to={link}
                  key={title}
                  className="uppercase md:inline-flex text-sm font-semibold text-white/90 hover:text-white duration-200 relative overflow-hidden group"
                >
                  {title}
                  <span className="inline-flex w-full h-[1px] bg-white absolute bottom-0 left-0 transform -translate-x-[105%] group-hover:translate-x-0 duration-300" />
                </Link>
              ))}
            </Container>
          </div>
          )}
        </>
      ): (
        <div className="w-full bg-slate-800 text-white">
        <Container className="py-2 max-w-4xl flex items-center gap-5 justify-between">
          {bottomNavigationloginSingUp.map(({ title, link }) => (
            <Link
              to={link}
              key={title}
              className="uppercase md:inline-flex text-sm font-semibold text-white/90 hover:text-white duration-200 relative overflow-hidden group"
            >
              {title}
              <span className="inline-flex w-full h-[1px] bg-white absolute bottom-0 left-0 transform -translate-x-[105%] group-hover:translate-x-0 duration-300" />
            </Link>
          ))}
        </Container>
      </div>
      )}
      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>Đăng xuất</DialogTitle>
        <DialogContent>Bạn có chắc chắn muốn đăng xuất khỏi tài khoản?</DialogContent>
        <DialogActions>
          <button className="bg-gray-500 text-white font-bold py-2 px-6 rounded hover:bg-gray-800"
          onClick={handleCloseDialog}>
            Hủy
          </button>
          <button className="bg-green-500 text-white font-bold py-2 px-6 rounded hover:bg-green-800"
          onClick={handleLogout}>
            Xác nhận
          </button>
        </DialogActions>
      </Dialog>      
    </header>
  );
};

export default Header;
