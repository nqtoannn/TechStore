import React from "react";
import { Link } from "react-router-dom";
import { Button, Typography } from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle'; // Import CheckCircleIcon from Material-UI
import './PaymentSuccess.css'; // Import CSS file for styling




const PaymentSuccess = () => {
    return (
        <div className="payment-success-container">
            <CheckCircleIcon className="checkmark-icon" /> {/* Add CheckCircleIcon */}
            <Typography variant="h4" gutterBottom>
                Thanh toán thành công!
            </Typography>
            <Typography variant="body1" gutterBottom>
                Cảm ơn bạn đã đặt hàng.
            </Typography>
            <Link to="/shop">
                <Button variant="contained" className="back-to-home-btn">
                    Quay về trang chủ
                </Button>
            </Link>
        </div>
    );
};

export default PaymentSuccess;
