import React, { useState, useEffect } from "react";
import axios from "axios";
import { Dialog, DialogTitle, DialogContent, DialogActions } from '@mui/material';
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { getToken } from "../utils/getToken";
import { Link } from "react-router-dom";

const Cart = () => {
  const [cartItems, setCartItems] = useState([]);
  const [selectedItems, setSelectedItems] = useState([]);
  const [totalPrice, setTotalPrice] = useState(0);
  const [openDialog, setOpenDialog] = useState(false);
  const [itemIdToRemove, setItemIdToRemove] = useState(null);

  const navigate = useNavigate();

  const userId = localStorage.getItem('userId');
  const token = getToken();

  

  useEffect(() => {
    const fetchData = async () => {
      try {
        const token = getToken();
        const cartResponse = await axios.get(
          `http://localhost:8080/techstore/api/customer/findAllCartItems/${userId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`
            }
          }
        );
        const items = cartResponse.data.data;
        console.log("cartItems: ", items);

        setCartItems(items);

        const total = items.reduce((acc, item) => acc + item.productItemModel.price * item.quantity, 0);
        setTotalPrice(total);
      } catch (error) {
        console.error("Error fetching cart items:", error);
      }
    };

    fetchData();
  }, [userId]);

  const handleSelectItem = (itemId) => {
    if (cartItems.length === 0) {
      setTotalPrice(0);
      return;
    } else {
      setSelectedItems(prevSelectedItems =>
        prevSelectedItems.includes(itemId)
          ? prevSelectedItems.filter(id => id !== itemId)
          : [...prevSelectedItems, itemId]
      );
    }
  };

  const handleImageClick = (itemId) => {
    navigate(`/product/${itemId}`);
  };

  const handleCheckout = async () => {
    const selectedItemsData = cartItems.filter(item => selectedItems.includes(item.id)).map(item => ({
      id: item.id,
      productItemId: item.productItemModel.id,
      productName: item.productName,
      price: item.productItemModel.price,
      quantity: item.quantity,
      imgUrl: item.productItemModel.imageUrl,
      productItemName: item.productItemModel.productItemName,
    }));
    if (selectedItemsData.length === 0) {
      setTotalPrice(0);
      toast.error('Bạn chưa chọn sản phẩm để thanh toán', {
        onClose: () => { },
        toastStyle: {
          background: "#4CAF50",
          color: "white", 
          border: "1px solid #388E3C", 
          borderRadius: "8px",
          boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)", 
        }
      });
      return;
    } else {
      navigate("/checkout", { state: { selectedItems: selectedItemsData, totalPrice } });
    }
  };

  const handleRemove = async (itemId) => {
    setItemIdToRemove(itemId);
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  const handleConfirmRemove = async () => {
    try {
      await axios.delete(`http://localhost:8080/techstore/api/customer/deleteCartItem/${itemIdToRemove}`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      const updatedCartItems = cartItems.filter(item => item.id !== itemIdToRemove);
      setCartItems(updatedCartItems);

      const newTotalPrice = updatedCartItems.reduce((acc, item) => acc + item.productItemModel.price * item.quantity, 0);
      setTotalPrice(newTotalPrice);
    } catch (error) {
      console.error("Error removing cart item:", error);
    }
    setOpenDialog(false);
  };

  const updateItemQuantity = (itemId, increment) => {
    const updatedCartItems = cartItems.map(item => {
      if (item.id === itemId) {
        const newQuantity = item.quantity + increment;
        if (newQuantity > 0) {
          return { ...item, quantity: newQuantity };
        }
      }
      return item;
    }).filter(item => item.quantity > 0);

    setCartItems(updatedCartItems);

    const newTotalPrice = updatedCartItems.reduce((acc, item) => acc + item.productItemModel.price * item.quantity, 0);
    setTotalPrice(newTotalPrice);
  };

  useEffect(() => {
    if (cartItems.length === 0) {
      setTotalPrice(0);
      return;
    }
    const selectedItemsTotalPrice = cartItems
      .filter(item => selectedItems.includes(item.id))
      .reduce((acc, item) => acc + item.productItemModel.price * item.quantity, 0);
    setTotalPrice(selectedItemsTotalPrice);
  }, [selectedItems, cartItems]);

  return (
    <div className="flex flex-col  h-[77vh] max-w-7xl mx-auto mt-5">
  <h4 className="text-2xl font-semibold mb-7">Giỏ Hàng</h4>

  {cartItems.length === 0 ? (
    <div className="text-center flex-grow">
      <h6 className="text-xl font-medium">Giỏ hàng của bạn đang trống.</h6>
      <img
        src="https://i.pinimg.com/564x/47/07/f4/4707f4138db3ff7930a081dc17974fd8.jpg"
        className="mx-auto mt-4"
        alt="Empty cart"
        width={450}
        height={410}
      />
      <Link to="/shop">
        <button className="mt-4 w-40 py-2 bg-orange-500 text-white text-sm font-semibold rounded hover:bg-orange-600">
          Tiếp tục mua hàng
        </button>
      </Link>
    </div>
  ) : (
    <>
      {/* Header Row */}
      <div className="grid grid-cols-3 text-center font-semibold text-lg mb-4">
        <div>Sản phẩm</div>
        <div>Số lượng</div>
        <div>Thành tiền</div>
      </div>

      {/* Cart Items */}
      <div className="grid grid-cols-1 gap-4">
        {cartItems.map((item) => (
          <div
            key={item.id}
            className="flex flex-col sm:flex-row bg-gray-100 p-4 rounded-lg border border-gray-300"
          >
            <div className="flex justify-center items-center mb-4 sm:mb-0 sm:mr-4">
              <input
                type="checkbox"
                checked={selectedItems.includes(item.id)}
                onChange={() => handleSelectItem(item.id)}
                className="cursor-pointer"
              />
            </div>
            <img
              src={item.productItemModel.imageUrl}
              alt={item.productName}
              className="w-24 h-24 object-cover cursor-pointer"
              onClick={() => handleImageClick(item.productId)}
            />
            <div className="flex-grow flex flex-col justify-between ml-4">
              <h6 className="text-lg font-bold capitalize">{item.productName}</h6>
              <p className="font-light text-sm">Phân Loại: {item.productItemModel.productItemName}</p>
              <div className="flex justify-between items-center">
                <p className="font-bold text-orange-500">{item.productItemModel.price.toLocaleString()} VND</p>
                <div className="flex items-center">
                  <button
                    className="bg-green-100 text-black font-bold px-2 py-1 rounded hover:bg-green-200"
                    onClick={() => updateItemQuantity(item.id, -1)}
                  >
                    -
                  </button>
                  <span className="mx-2">{item.quantity}</span>
                  <button
                    className="bg-green-100 text-black font-bold px-2 py-1 rounded hover:bg-green-200"
                    onClick={() => updateItemQuantity(item.id, 1)}
                  >
                    +
                  </button>
                </div>
                <p className="font-bold mr-16">{(item.productItemModel.price * item.quantity).toLocaleString()} VND</p>
              </div>
            </div>

            {/* Xóa Button, centered vertically */}
            <div className="flex items-center justify-center ml-4 sm:ml-0 sm:flex-col">
              <button
                className="bg-rose-500 text-white font-bold py-2 px-4 rounded hover:bg-red-600"
                onClick={() => handleRemove(item.id)}
              >
                Xóa
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Spacer to push total to bottom */}
      <div className="flex-grow"></div>

      {/* Total Section - Luôn ở dưới */}
      <div className="bg-gray-100 p-4 mb-12 rounded-lg border border-gray-300 flex justify-between items-center">
        <h6 className="text-xl font-bold">Tổng cộng:</h6>
        <p className="text-xl font-bold">{totalPrice.toLocaleString()} VND</p>
        <button
          className="bg-green-500 text-white font-bold py-2 px-6 rounded hover:bg-green-800"
          onClick={handleCheckout}
        >
          Thanh toán
        </button>
      </div>

      {/* Confirmation Dialog */}
      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>Xác nhận xóa</DialogTitle>
        <DialogContent>Bạn có chắc chắn muốn xóa sản phẩm khỏi giỏ hàng?</DialogContent>
        <DialogActions>
          <button className="bg-gray-500 text-white font-bold py-2 px-6 rounded hover:bg-gray-800"
          onClick={handleCloseDialog}>
            Hủy
          </button>
          <button className="bg-green-500 text-white font-bold py-2 px-6 rounded hover:bg-green-800"
          onClick={handleConfirmRemove}>
            Xác nhận
          </button>
        </DialogActions>
      </Dialog>
    </>
  )}
</div>


  );
};

export default Cart;
