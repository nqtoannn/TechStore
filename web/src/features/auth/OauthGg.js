import React, {useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useLocation } from "react-router-dom";
import { useAuth } from "./AuthContext";
import FooterTop from "../../components/FooterTop";

function OauthGg() {
  const navigate = useNavigate();
  const { isLoggedIn, login } = useAuth();
  const [authResponse, setAuthResponse] = useState(null);
  const location = useLocation();

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const authResponseEncoded = queryParams.get("authResponse");
    
    if (authResponseEncoded) {
      const decodedResponse = JSON.parse(decodeURIComponent(authResponseEncoded));
      setAuthResponse(decodedResponse);
      localStorage.setItem("userId", decodedResponse.accountId);
      localStorage.setItem("accessToken", decodedResponse.access_token);
      localStorage.setItem("refreshToken", decodedResponse.refresh_token);
      localStorage.setItem("role", decodedResponse.role);
      console.log(decodedResponse);
      if(decodedResponse.role === "CUSTOMER" && decodedResponse.accessToken !== null) {
        navigate("/shop");
      }
    }
  }, [location]);

  return (
        <div>
          <div className="flex items-center justify-center  h-[40vh] bg-gray-100">
            <div className="bg-gray-950 rounded-lg p-8 max-w-screen-lg w-full center">
                <div className="mb-6">
                  <h2 className="text-xl font-semibold text-white text-center">
                    Đăng nhập
                  </h2>
                  <p className="mt-2 text-sm text-gray-400 text-center">
                  Đang điều hướng đến trang web của chúng tôi. Vui lòng đợi trong giây lát.
                  </p>
                </div>
            </div>
          </div>
          <FooterTop />
    </div>
  );
}

export default OauthGg;
