import axios from "axios";
import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getToken } from "../utils/getToken";
import { useDispatch } from 'react-redux';
import { addToCart } from '../features/cart/cartSlice';
import CommentSection from "./CommentSection";

function ProductDetail() {
    const { productId } = useParams();
    const [product, setProduct] = useState(null);
    const [selectedImage, setSelectedImage] = useState("");
    const [selectedProductItem, setSelectedProductItem] = useState(null);
    const [availableQuantity, setAvailableQuantity] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const dispatch = useDispatch();
    const [discountPercent, setdiscountPercent] = useState(0);
    const navigate = useNavigate();

    const token = getToken();
    const userId = localStorage.getItem('userId');

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/techstore/api/products/findById/${productId}`);
            if (response.data && response.data.data) {
                setProduct(response.data.data);
                setSelectedImage(response.data.data.imageUrl);
                setdiscountPercent(response.data.data.discountPercent);
            } else {
                console.error('Unexpected response structure:', response.data);
            }
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };

    const priceAfterDiscount = selectedProductItem
        ? selectedProductItem.price * (1 - discountPercent / 100)
        : null;

    const handleAddToCart = async () => {
        if (!selectedProductItem) {
            alert("Vui lòng chọn mẫu.");
            return;
        }
        try {
            const productToAdd = {
                product_item_id: selectedProductItem.id,
                quantity: quantity,
                customerId: userId
            };
    
            console.log('Request payload:', productToAdd);
            console.log('Token:', token);
    
            const response = await axios.post(`http://localhost:8080/techstore/api/customer/addToCart`, productToAdd, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
    
            if (response.status === 200) {
                dispatch(addToCart(productToAdd));
                console.log(`Product added to cart: ${selectedProductItem.id}`);
            } else {
                console.error('Unexpected response:', response);
            }
    
        } catch (error) {
            console.error("Error adding product to cart:", error.response ? error.response.data : error.message);
        }
    };

    const handleBuyNow = () => {
        if (!selectedProductItem) {
            alert("Vui lòng chọn mẫu.");
            return;
        }

        const productToBuy = {
            productItemId: selectedProductItem.id,
            quantity: quantity,
            customerId: userId,
            productName: product.name,  
            price: selectedProductItem.price,
            imgUrl: selectedProductItem.imageUrl,
            productItemName: selectedProductItem.productItemName,
        };

        navigate("/checkout", { state: { selectedItems: [productToBuy], totalPrice: selectedProductItem.price * quantity } });
    };

    const handleImageClick = (imageUrl) => {
        setSelectedImage(imageUrl);
    };

    if (!product) {
        return <div>Loading...</div>;
    }

    return (
        <>
            <div className="bg-gray-200 mt-6 mb-6 pb-6 max-w-screen-xl mx-auto rounded-md">
                <div className="flex flex-col lg:flex-row">
                <div className="flex flex-col items-center lg:mr-6">
                  <div className="w-[400px] h-[400px] mt-12 ml-12 flex">
                    <img
                    className="w-[400px] h-[400px] object-contain "
                    src={selectedImage}
                    alt={product.name}
                    />
                  </div>
                
                {/* Smaller Images */}
                <div className="flex justify-center space-x-4 mt-4">
                    <img
                    className="w-24 h-24 object-cover cursor-pointer"
                    src={product.imageUrl}
                    alt={product.name}
                    onClick={() => handleImageClick(product.imageUrl)}
                    />
                    {product.productItems.map((item) => (
                    <img
                        key={item.id}
                        className="w-24 h-24 object-cover cursor-pointer"
                        src={item.imageUrl}
                        alt={item.productItemName}
                        onClick={() => handleImageClick(item.imageUrl)}
                    />
                    ))}
                </div>
            </div>


    <div className="flex flex-col lg:w-1/2 ml-12">
      <h3 className="capitalize text-left text-2xl font-normal mt-12">
        {product.name}
      </h3>
      <hr className="my-2"/>

      <div className="flex items-center justify-between">
        <div className="flex items-center text-yellow-500">
          {[...Array(5)].map((_, index) => (
            <svg
              key={index}
              className={`w-5 h-5 ${
                index < product.rating ? "fill-current" : "text-gray-300"
              }`}
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
            >
              <path d="M12 17.27l6.18 3.73-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
            </svg>
          ))}
          <div className="text-gray-500 text-sm">({product.rated} đánh giá)</div>
        </div>
        <div className="text-gray-500 text-sm"> Đã bán {product.sold} </div>
      </div>

      <div className="text-3xl text-orange-500 mt-8">
        {selectedProductItem ? (
          <>
            {discountPercent > 0 && (
              <span className="line-through text-gray-400">
                {selectedProductItem.price.toLocaleString()} VNĐ
              </span>
            )}
            &nbsp;
            {priceAfterDiscount.toLocaleString()} VNĐ
          </>
        ) : (
          `${product.avrPrice} VNĐ`
        )}
      </div>
      {discountPercent > 0 && (
        <div className="text-xl text-orange-500 mt-2">
          Giảm giá: {discountPercent}%
        </div>
      )}

      <div className="text-lg text-left mt-4">{product.description}</div>
      <div className="mt-6">
        <h6 className="font-semibold">Chọn mẫu:</h6>
        {product.productItems.map((item) => (
          <button
            key={item.id}
            className={`px-4 py-2 mt-2 mr-2 border rounded-md ${
              selectedProductItem && selectedProductItem.id === item.id
                ? "bg-green-200 border-green-400 text-orange-500"
                : "bg-green-100 border-green-200 text-orange-500"
            }`}
            onClick={() => {
              setSelectedProductItem(item);
              setSelectedImage(item.imageUrl);
              setAvailableQuantity(item.quantityInStock);
            }}
          >
            {item.productItemName}
          </button>
        ))}
      </div>

      <div className="mt-6 flex items-center">
        <h6 className="font-semibold mr-2">Số Lượng:</h6>
        <div className="flex items-center">
          <button
            className="px-3 py-1 bg-green-100 border border-green-400 rounded-md"
            onClick={() => setQuantity((prev) => Math.max(prev - 1, 1))}
          >
            -
          </button>
          <span className="px-3">{quantity}</span>
          <button
            className="px-3 py-1 bg-green-100 border border-green-400 rounded-md"
            onClick={() => setQuantity((prev) => prev + 1)}
          >
            +
          </button>
        </div>
        <span className="ml-4">
          {selectedProductItem
            ? `${availableQuantity} sản phẩm có sẵn`
            : `Tổng ${product.productItems.reduce(
                (total, item) => total + item.quantityInStock,
                0
              )} sản phẩm có sẵn`}
        </span>
      </div>

      <div className="mt-6">
        <button
          className="w-60 h-12 ml-6 bg-green-500 text-white rounded-md text-lg font-semibold mr-4 hover:bg-green-600"
          onClick={handleAddToCart}
        >
          Thêm vào giỏ hàng
        </button>
        <button
          className="w-60 h-12 ml-12 bg-orange-500 text-white rounded-md text-lg font-semibold hover:bg-orange-600"
          onClick={handleBuyNow}
        >
          Mua ngay
        </button>
      </div>
    </div>
  </div>

  {/* Comment and Attributes Section */}
  <div className="w-4/5 mt-8 mx-auto flex">
    {/* Comments column */}
    <div className="w-3/5 pr-4">
      <CommentSection productId={productId} />
    </div>
    {/* Attributes column */}
    <div className="w-2/5 bg-white p-4 border rounded-md">
      <h6 className="font-semibold mb-2">Thông số kỹ thuật</h6>
      <table className="w-full table-auto">
        <tbody>
          {Object.entries(product.attributes).map(([key, value]) => (
            <tr key={key}>
              <td className="border px-4 py-2 font-semibold">{key}</td>
              <td className="border px-4 py-2">{value}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  </div>
</div>

  
        </>
    );
}

export default ProductDetail;
