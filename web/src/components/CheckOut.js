import React, { useState, useEffect } from "react";
import { Typography, Button, Grid, Box, TextField, Select, MenuItem, Dialog, DialogTitle, DialogContent, DialogActions, FormControl, InputLabel } from '@mui/material';
import { useLocation, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { getToken } from "../utils/getToken";
import axios from "axios";

const Checkout = () => {
    const location = useLocation();
    const { selectedItems, totalPrice } = location.state;
    const navigate = useNavigate();
    const [userData, setUserData] = useState(null);
    const [paymentMethod, setPaymentMethod] = useState("");
    const [listPaymentMethod, setListPaymentMethod] = useState([]);
    const [shippingMethod, setShippingMethod] = useState('');
    const [shippingFee, setShippingFee] = useState(0);
    const [paymentSuccess, setPaymentSuccess] = useState(false);
    const [openDialog, setOpenDialog] = useState(false);
    const [address, setAddress] = useState("");
    const [fullName, setFullName] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [addressList, setAddressList] = useState([]);
    const [newAddress, setNewAddress] = useState("");
    const [newPhoneNumber, setNewPhoneNumber] = useState("");
    const [addingNewAddress, setAddingNewAddress] = useState(false);
    const [openAddAddressDialog, setOpenAddAddressDialog] = useState(false);
    const [openDialogPayment, setOpenDialogPayment] = useState(false);
    const [paymentUrl, setPaymentUrl] = useState('');
    const token = getToken();


    const shippingMethods = [
        { value: "Nhanh", label: "Nhanh" },
        { value: "Hỏa tốc", label: "Hỏa tốc" },
    ];

    const handlePaymentMethodChange = (event) => {
        setPaymentMethod(event.target.value);
    };

    const handleShippingMethodChange = (event) => {
        setShippingMethod(event.target.value);
    };

    const handleAddressChange = (event) => {
        const selectedAddress = event.target.value;
        setAddress(selectedAddress);
        const selectedAddressDetails = addressList.find(addr => addr.address === selectedAddress);
        if (selectedAddressDetails) {
            setPhoneNumber(selectedAddressDetails.phoneNumber);
        }
    };

    const handleNewAddressChange = (event) => {
        setNewAddress(event.target.value);
    };

    const handleNewPhoneNumberChange = (event) => {
        setNewPhoneNumber(event.target.value);
    };

    const handleAddAddressDialogOpen = () => {
        setOpenAddAddressDialog(true);
    };

    const handleAddAddressDialogClose = () => {
        setOpenAddAddressDialog(false);
    };

    const handlePaymentClick = () => {
        window.open(paymentUrl, '_blank'); // Mở URL thanh toán trong một tab mới
        navigate('/list-order'); // Chuyển hướng đến /list-order
    };

    const handleCancelClick = () => {
        navigate('/list-order'); // Chuyển hướng đến /list-order
    };

    useEffect(() => {
        if (shippingMethod === 'Nhanh') {
            setShippingFee(15000);
        } else if (shippingMethod === 'Hỏa tốc') {
            setShippingFee(30000);
        } else {
            setShippingFee(0);
        }
    }, [shippingMethod]);

    const totalAmount = totalPrice + shippingFee;

    useEffect(() => {
        const fetchUserData = async () => {
            const userId = localStorage.getItem('userId');
            try {
                const response = await axios.get(`http://localhost:8080/techstore/api/customer/findById/${userId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                if (response.data.status === "OK") {
                    setUserData(response.data.data[0]);
                    setAddress(response.data.data[0].address);
                    setPhoneNumber(response.data.data[0].phoneNumber);
                    setFullName(response.data.data[0].fullName);
                }
            } catch (error) {
                console.error("Error fetching user data:", error);
            }
        };
        fetchUserData();
    }, []);

    useEffect(() => {
        const fetchPaymentMethod = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/techstore/api/payment/getAll`, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                if (response.data.status === "OK") {
                    setListPaymentMethod(response.data.data);
                }
            } catch (error) {
                console.error("Error fetching payment methods:", error);
            }
        };
        fetchPaymentMethod();
    }, []);

    useEffect(() => {
        const fetchAddresses = async () => {
            const userId = localStorage.getItem('userId');
            try {
                const response = await axios.get(`http://localhost:8080/techstore/api/address/getAll/${userId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                if (response.data.status === "OK") {
                    setAddressList(response.data.data);
                }
            } catch (error) {
                console.error("Error fetching addresses:", error);
            }
        };
        fetchAddresses();
    }, []);

    const handleCheckout = () => {
        if (paymentMethod === "") {
            toast.error('Vui lòng chọn phương thức thanh toán', {
                onClose: () => { },
                toastStyle: {
                    background: "#4CAF50",
                    color: "white",
                    border: "1px solid #388E3C",
                    borderRadius: "8px",
                    boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)",
                }
            });
        } else if (shippingMethod === "") {
            toast.error('Vui lòng chọn phương thức vận chuyển', {
                onClose: () => { },
                toastStyle: {
                    background: "#4CAF50",
                    color: "white",
                    border: "1px solid #388E3C",
                    borderRadius: "8px",
                    boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)",
                }
            });
        } else setOpenDialog(true);
    };

    const handleCloseDialog = () => {
        setOpenDialog(false);
    };

    const handleConfirmCheckout = async () => {
        const userId = localStorage.getItem('userId');
        if (userId) {
            try {
                const orderPayload = {
                    customerId: userId,
                    payId: paymentMethod,
                    totalPrice: totalAmount,
                    orderDate: new Date().toISOString().slice(0, 10),
                    address: fullName +", "+phoneNumber+", "+address,
                    orderItemList: selectedItems.map((item) => ({
                        productItemId: item.productItemId,
                        price: item.price,
                        quantity: item.quantity
                    })),
                };
                const response = await axios.post(
                    "http://localhost:8080/techstore/api/customer/order",
                    orderPayload,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    }
                );
                if (response.data.status === "OK") {
                    if(response.data.data!==null){
                        setPaymentUrl(response.data.data); 
                        setOpenDialogPayment(true);
                    }else{
                       setPaymentSuccess(true);
                        navigate('/payment-success'); 
                    }
                } else {
                    toast.error(`Có lỗi khi đặt hàng: ${response.data.message}`, {
                        onClose: () => { },
                        toastStyle: {
                            background: "#4CAF50",
                             color: "white",
                            border: "1px solid #388E3C",
                            borderRadius: "8px",
                            boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)",
                        }
                    });
                }
            } catch (error) {
                console.error("Có lỗi khi đặt hàng:", error);
                toast.error('Có lỗi khi đặt hàng, vui lòng thử lại sau.', {
                    onClose: () => { },
                    toastStyle: {
                        background: "#4CAF50",
                        color: "white",
                        border: "1px solid #388E3C",
                        borderRadius: "8px",
                        boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)",
                    }
                });
            }
        }
    };

    const handleAddNewAddress = async () => {
        const userId = localStorage.getItem('userId');
        if (userId && newAddress && newPhoneNumber) {
            try {
                const newAddressPayload = {
                    address: newAddress,
                    phoneNumber: newPhoneNumber,
                    userId: userId
                };
                const response = await axios.post(
                    "http://localhost:8080/techstore/api/address/addAddress",
                    newAddressPayload,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    }
                );
                if (response.data.status === "OK") {
                    setAddressList([...addressList, response.data.data]);
                    setOpenAddAddressDialog(false);
                    setNewAddress("");
                    setNewPhoneNumber("");
                   
                } else {
                    toast.error(`Adding address failed: ${response.data.message}`, {
                        onClose: () => { },
                        toastStyle: {
                            background: "#F44336",
                            color: "white",
                            border: "1px solid #D32F2F",
                            borderRadius: "8px",
                            boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)",
                        }
                    });
                }
            } catch (error) {
                console.error("Error adding new address:", error);
                toast.error('An error occurred while adding the new address. Please try again later.', {
                    onClose: () => { },
                    toastStyle: {
                        background: "#F44336",
                        color: "white",
                        border: "1px solid #D32F2F",
                        borderRadius: "8px",
                        boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)",
                    }
                });
            }
        } else {
            toast.error('Please provide both address and phone number for the new address.', {
                onClose: () => { },
                toastStyle: {
                    background: "#F44336",
                    color: "white",
                    border: "1px solid #D32F2F",
                    borderRadius: "8px",
                    boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)",
                }
            });
        }
    };

    return (
    <div className="max-w-7xl mx-auto mt-5 mb-5">
    <h4 className="text-2xl font-semibold mb-7">Thanh toán</h4>
    {selectedItems.length === 0 ? (
        <Typography variant="body1">Bạn chưa chọn sản phẩm để thanh toán!</Typography>
    ) : (
        <div className="flex justify-between gap-5">
            <div className="flex-3 max-w-3xl">
                <Grid container spacing={1}>
                    {selectedItems.map((item, index) => (
                        <Grid item xs={12} key={index}>
                            <Box
                                key={item.id}
                                className="bg-green-100 mb-1 flex flex-col sm:flex-row gap-5 border-b border-gray-300 rounded-lg p-3"
                            >
                                <img
                                    className="object-fill m-2"
                                    src={`${item.imgUrl}`}
                                    alt={`${item.productItemName}`}
                                    style={{ maxWidth: '200px', maxHeight: '200px' }}
                                />
                                <div className="flex-2">
                                    <h6 className="text-lg font-bold capitalize">{item.productName}</h6>
                                    <p className="font-light text-sm mt-2">Phân Loại: {item.productItemName}</p>
                                    <p className="font-bold text-orange-500 mt-2">Đơn giá: {item.price.toLocaleString()} VND</p>
                                    <p className="font-bold mt-2">Số lượng: {item.quantity}</p>
                                    
                                </div>
                            </Box>
                        </Grid>
                    ))}
                </Grid>
               
            </div>
            <div className="flex-1">
                {userData && (
                    <div className="mb-5 p-5 rounded-lg border border-gray-300 bg-green-100">
                        <h4 className="text-2xl font-semibold mb-7">Thông tin giao hàng</h4>
                        <div className="flex flex-col gap-5">
                            <Box>
                                <label className="block text-base text-black" htmlFor="fullName">
                                    Tên người nhận
                                </label>
                                <input
                                    type="email"
                                    id="fullName"
                                    className="block w-full mt-1 rounded-md bg-zinc-50 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-white/10 focus:ring-indigo-500"
                                    value={fullName}
                                    onChange={(e) => setFullName(e.target.value)}
                                    required
                                />
                            </Box>
                            <Box>
                                <label className="block text-base text-black" htmlFor="phoneNumber">
                                    Số điện thoại
                                </label>
                                <input
                                    type="text"
                                    id="phoneNumber"
                                    className="block w-full mt-1 rounded-md bg-zinc-50 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-white/10 focus:ring-indigo-500"
                                    value={phoneNumber}
                                    onChange={(e) => setPhoneNumber(e.target.value)}
                                    required
                                />
                            </Box>
                            
                            <Box>
                                <label className="block text-base text-black" htmlFor="phoneNumber">
                                    Địa chỉ
                                </label>
                                <select 
                                    value={address} 
                                    onChange={handleAddressChange}
                                    className="block w-full mt-1 rounded-md bg-zinc-50 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-white/10 focus:ring-indigo-500"
                                >
                                    <option key={0} value={null} disabled>Chọn địa chỉ</option>
                                    {addressList.map((addr) => (
                                        <option key={addr.id} value={addr.address}>
                                            {addr.address}
                                        </option>
                                    ))}
                                </select>
                            </Box>
                            
                            <button
                            className="bg-green-500 text-white font-bold py-2 px-6 rounded hover:bg-green-800"
                            onClick={handleAddAddressDialogOpen}
                            >
                                Thêm địa chỉ
                            </button>
                        </div>
                    </div>
                )}
                <div className="mt-5 mb-10 p-5 rounded-lg border border-gray-300 bg-green-100">
                    <h6 className="text-lg font-bold capitalize">Phương thức thanh toán</h6>
                    <div className="w-full mb-5">
                        <select
                            value={paymentMethod}
                            onChange={handlePaymentMethodChange}
                            className="block w-full mt-1 rounded-md bg-zinc-50 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-white/10 focus:ring-indigo-500"
                        >
                            <option value="" disabled>Chọn phương thức thanh toán</option>
                            {listPaymentMethod.map((method) => (
                                <option key={method.id} value={method.id}>
                                    {method.name}
                                </option>
                            ))}
                        </select>
                    </div>
                    <h6 className="text-lg font-bold capitalize">Phương thức vận chuyển</h6>
                    <div className="w-full mb-5">
                        <select
                            value={shippingMethod}
                            onChange={handleShippingMethodChange}
                            className="block w-full mt-1 rounded-md bg-zinc-50 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-white/10 focus:ring-indigo-500"
                        >
                            <option value="" disabled>Chọn phương thức vận chuyển</option>
                            {shippingMethods.map((method) => (
                                <option key={method.value} value={method.value}>
                                    {method.label}
                                </option>
                            ))}
                        </select>
                    </div>
                    
                    <div className="flex justify-between items-center mt-4  ">
                        <h6 className="text-lg font-bold capitalize">Tổng cộng: {totalAmount.toLocaleString()} VNĐ</h6>
                        <button
                            onClick={handleCheckout}
                            className="bg-green-500 text-white font-bold py-2 px-6 rounded hover:bg-green-800"
                        >
                            Thanh toán
                        </button>
                    </div>
                </div>  
            </div>
        </div>
    )}
        <Dialog open={openDialog} onClose={handleCloseDialog}>
            <DialogTitle>Xác nhận thanh toán</DialogTitle>
            <DialogContent>Bạn có chắc chắn muốn tiến hành thanh toán?</DialogContent>
            <DialogActions>
                <button onClick={handleCloseDialog} className="bg-gray-500 text-white font-bold py-2 px-6 rounded hover:bg-gray-800">
                    Hủy
                </button>
                <button onClick={handleConfirmCheckout} className="bg-green-500 text-white font-bold py-2 px-6 rounded hover:bg-green-800">
                    Xác nhận
                </button>
            </DialogActions>
        </Dialog>
        <Dialog open={openAddAddressDialog} onClose={handleAddAddressDialogClose}>
            <DialogTitle>Thêm địa chỉ mới</DialogTitle>
            <DialogContent>
                <label className="block text-base text-black" htmlFor="newAddress">
                    Địa chỉ giao hàng
                </label>
                <input
                    type="text"
                    id="newAddress"
                    className="block w-96 mt-1 rounded-md bg-zinc-50 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-white/10 focus:ring-indigo-500"
                    value={newAddress}
                    onChange={handleNewAddressChange}
                    required
                />
                
                <label className="block text-base text-black" htmlFor="newPhoneNumber">
                    Số điện thoại
                </label>
                <input
                    type="text"
                    id="newPhoneNumber"
                    className="block w-full mt-1 rounded-md bg-zinc-50 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-white/10 focus:ring-indigo-500"
                    value={newPhoneNumber}
                    onChange={handleNewPhoneNumberChange}
                    required
                />
               
            </DialogContent>
            <DialogActions>
                <button onClick={handleAddAddressDialogClose} className="bg-gray-500 text-white font-bold py-2 px-6 rounded hover:bg-gray-800">
                    Hủy
                </button>
                <button onClick={handleAddNewAddress} className="bg-green-500 text-white font-bold py-2 px-6 rounded hover:bg-green-800">
                    Thêm địa chỉ
                </button>
            </DialogActions>
        </Dialog>
        <Dialog open={openDialogPayment}>
            <DialogTitle>Thông báo</DialogTitle>
            <DialogContent>Bạn đã đặt hàng thành công. Vui lòng thanh toán đơn hàng của bạn tại đây.</DialogContent>
            <DialogActions>
                <Button onClick={handleCancelClick} className="text-primary">
                    Hủy
                </Button>
                <Button onClick={handlePaymentClick} className="text-white text-lg bg-orange-500 hover:bg-orange-600 mr-4">
                    Thanh toán
                </Button>
            </DialogActions>
        </Dialog>
    </div>

    );
};

export default Checkout;
