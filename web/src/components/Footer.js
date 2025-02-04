import React from "react";
import { Link } from "react-router-dom";
import {
  BsInstagram,
  BsFacebook,
  BsTwitter,
  BsLinkedin,
} from "react-icons/bs";import { useSelector } from "react-redux";

const Footer = () => {
  const loginState = useSelector((state) => state.auth.isLoggedIn);


  const copyrightStyles = {
    color: "#ffffff",
    fontSize: "2xl",
    fontWeight: "normal",
  };

  return (
    <footer style={{ padding: "1rem", backgroundColor: "#1e293b" }}>
      <nav style={{ display: "flex", justifyContent: "center", marginBottom: "0.5rem" }}>
      </nav>
      <aside style={{ textAlign: "center" }}>
        <p style={copyrightStyles}>
          Copyright © 2024 - All right reserved! 
        </p>
      </aside>
      <div style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
      <div style={{ display: "flex", alignItems: "center", margin: "0 10px" }}>
        <BsInstagram /><div style={{ marginLeft: "5px" }}>Instagram</div>
      </div>
      <div style={{ display: "flex", alignItems: "center", margin: "0 10px" }}>
        <BsFacebook /><div style={{ marginLeft: "5px" }}>Facebook</div>
      </div>
      <div style={{ display: "flex", alignItems: "center", margin: "0 10px" }}>
        <BsTwitter /><div style={{ marginLeft: "5px" }}>Twitter</div>
      </div>
      <div style={{ display: "flex", alignItems: "center", margin: "0 10px" }}>
        <BsLinkedin /><div style={{ marginLeft: "5px" }}>LinkedIn</div>
      </div>
    </div>
    </footer>
  );
};

export default Footer;
