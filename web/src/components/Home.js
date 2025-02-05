import React, { useEffect, useState } from "react";
import {
  ProductElement,
} from ".";
import "./styles/Shop.css";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { nanoid } from "nanoid";
import { getToken } from "../utils/getToken";
import banner1 from "../assets/Banner1.png";
import banner2 from "../assets/Banner2.png";
import banner3 from "../assets/Banner3.png";
import banner4 from "../assets/Banner4.png";
import banner5 from "../assets/Banner5.png";
import testimonial1 from "../assets/testimonial-1.jpg"
import quotation from "../assets/quotes.svg"
import delivery from "../assets/delivery-truck.png"
import onlinesupport from "../assets/online.png"
import returnPolicy from "../assets/return.png"
import nextDayDeli from "../assets/nextdaydeli.jpg"


function Home() {
  const [productItemData, setProductItemData] = useState([]);
  const [productMostData, setProductMostData] = useState([]);
  const [searchProduct, setSearchProduct] = useState('');
  const [currentIndex, setCurrentIndex] = useState(0);
  const navigate = useNavigate();
  const token = getToken();
  useEffect(() => {
    fetchData();
  }, [searchProduct]);

  useEffect(() => {
    fetchDataProduct();
  }, []);

  const fetchData = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/techstore/api/products/findAll?page=0&size=5"
      );
      const data = response.data.data;
      console.log(data);
      
      const filteredProduct = data.products.filter(product => product.status === 'ACTIVE');
      console.log(filteredProduct);
      setProductItemData(filteredProduct);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  const fetchDataProduct = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/techstore/api/products/getMostPurchased"
      );
      let products = response.data.data.filter(product => product.status === 'ACTIVE');
      setProductMostData(products);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  const images = [
    banner1,
    banner2,
    banner3,
    banner4,
    banner5
];
  useEffect(() => {
    const interval = setInterval(() => {
        setCurrentIndex((prevIndex) => (prevIndex + 1) % images.length);
    }, 10000); // Change image every 10 seconds

    return () => clearInterval(interval);
  }, []);

  const handleNext = () => {
      setCurrentIndex((currentIndex + 1) % images.length);
  };

  const handlePrev = () => {
      setCurrentIndex((currentIndex - 1 + images.length) % images.length);
  };

  //Khai báo hàm xử lý
  const handleProductClick = (productId) => {
    navigate(`/product/${productId}`);
  };

  const handleAddToCart = async (productId) => {
    try {
      const customerId = Number(localStorage.getItem("accountId"));
      const addToCartResponse = await axios.post(
        `http://localhost:8080/shoestore/api/customer/addToCart`,
        {
          customerId: customerId,
          productItemId: productId,
          quantity: 1
        },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );
      navigate("/cart");
    } catch (error) {
      console.error("Error adding product to cart:", error);
    }
  };

  const handleSearchChange = (event) => {
    setSearchProduct(event.target.value);
  };

  const handleKeyDown = (event) => {
    if (event.key === 'Enter') {
      fetchData(searchProduct);
      navigate(`/productList?search=${searchProduct}`);
    }
  };
  

  return (
    <>
        <div className="container mx-auto mt-10" >
          <div className="flex justify-center items-center ">
            <div className="carousel-image-container">
                <img src={images[currentIndex]} alt={`Slide ${currentIndex + 1}`} className="carousel-image" />
            </div>
          </div>
          <div>
            <h2 style={{ textAlign: 'center', marginTop: '10px' }}>Sản phẩm </h2>
          </div>
          <div className="product-grid-container"
            style={{ backgroundColor: 'rgba(255, 255, 255, 0.0)', paddingLeft: '120px' , paddingRight: '120px'}}  
          >
            {productItemData.length !== 0 &&
              productItemData.map((product) => (
                <ProductElement
                  key={nanoid()}
                  id={product.id}
                  title={product.name}
                  image={product.imageUrl}
                  rating={product.rating}
                  sold={product.sold}
                  price={product.avrPrice}
                  discount={product.discountPercent}
                  description={product.description}
                  onProductClick={() => handleProductClick(product.productItemId)}
                  onAddToCart={() => handleAddToCart(product.productItemId)}
                />
              ))}
          </div>
          <div>
            <h2 style={{ textAlign: 'center', marginTop: '10px' }}>Sản phẩm bán chạy</h2>
          </div>
          <div className="product-grid-container"
            style={{ backgroundColor: 'rgba(255, 255, 255, 0.0)', paddingLeft: '120px' , paddingRight: '120px'}}  
          >
            {productMostData.length !== 0 &&
              productMostData.map((product) => (
                <ProductElement
                  key={nanoid()}
                  id={product.id}
                  title={product.name}
                  image={product.imageUrl}
                  rating={product.rating}
                  price={product.avrPrice}
                  discount={product.discountPercent}
                  description={product.description}
                  onProductClick={() => handleProductClick(product.productItemId)}
                  onAddToCart={() => handleAddToCart(product.productItemId)}
                />
              ))}
          </div>
          <div >
            <div class="testimonials-box">
              <div class="testimonial">
                <div>
                  <h2 style={{ textAlign: 'center', marginTop: '10px' }}>Từ chúng tôi</h2>
                </div>
                <div class="testimonial-card">
                  <img src={testimonial1} alt="alan doe" class="testimonial-banner" width="80" height="80"/>
                  <p class="testimonial-name">Alan Doe</p>
                  <p class="testimonial-title">CEO & Founder Invision</p>
                  <img src={quotation} alt="quotation" class="quotation-img" width="26"/>
                  <p class="testimonial-desc">
                    Mang đến cho người dùng những sản phẩm chất lượng đi đôi với giá thành ưu đãi!
                  </p>
                </div>
              </div>
              <div class="service"  flex ="1">
                <div>
                  <h2 style={{ textAlign: 'center', marginTop: '10px' }}>Dịch vụ của chúng tôi</h2>
                </div>
                <div class="service-container">
                  <a href="#" class="service-item">
                    <div class="service-icon">
                      <img src={delivery} alt="shipping"/>
                    </div>
                    <div class="service-content">
                      <h3 class="service-title">Ship toàn quốc</h3>
                      <p class="service-desc">Cho mọi đơn hàng</p>
                    </div>
                  </a>
                  <a href="#" class="service-item">
                    <div class="service-icon">
                      <img src={nextDayDeli} alt="nextday" width={64}/>
                    </div>
                    <div class="service-content">                  
                      <h3 class="service-title">Giao hàng trong ngày</h3>
                      <p class="service-desc">Cho đơn ở Hồ Chí Minh</p>                  
                    </div>                  
                  </a>
                  <a href="#" class="service-item">                  
                    <div class="service-icon">
                    <img src={onlinesupport} alt="onlinesupport" width={64}/>
                    </div>
                    <div class="service-content">
                      <h3 class="service-title">Hỗ trợ tốt nhất</h3>
                      <p class="service-desc">Từ: 8AM - 11PM</p>                  
                    </div>                  
                  </a>
                  <a href="#" class="service-item">
                    <div class="service-icon">
                      <img src={returnPolicy} alt="returnpolicy" width={64}/>
                    </div>                 
                    <div class="service-content">                  
                      <h3 class="service-title">Chính sách hoàn trả</h3>
                      <p class="service-desc">Hoàn trả dễ dàng</p>                  
                    </div>                  
                  </a>
                </div>
              </div>
            </div>
            <div class="blog">
      <div class="container">
                <div>
                  <h2 style={{ textAlign: 'center', marginBottom: '20px' }}>Tin tức </h2>
                </div>
        <div class="blog-container has-scrollbar">
          <div class="blog-card">
            <a href="#">
              <img src={banner1} alt="Clothes Retail KPIs 2021 Guide for Clothes Executives" width="150px" class="blog-banner"/>
            </a>
            <div class="blog-content">
              <a href="#" class="blog-category">Thời trang</a>
              <h3>
                <a href="#" class="blog-title">Đôi giày chạy mới nhất của nhà Nike. Giá chỉ từ 170$</a>
              </h3>
              <p class="blog-meta">
                By <cite>Mr KhanhTinh</cite> / <time datetime="2022-04-06">Apr 06, 2022</time>
              </p>
            </div>
          </div>
          <div class="blog-card">          
            <a href="#">
              <img src={banner2} alt="Curbside fashion Trends: How to Win the Pickup Battle."
                class="blog-banner" width="300"/>
            </a>          
            <div class="blog-content">         
              <a href="#" class="blog-category">Giày thể thao</a>         
              <h3>
                <a href="#" class="blog-title">BST mùa thu 2024. Những siêu phẩm được giảm giá tới 60%.</a>
              </h3>          
              <p class="blog-meta">
                By <cite>Mr MinhTriet</cite> / <time datetime="2022-01-18">Jan 18, 2022</time>
              </p>          
            </div>          
          </div>
          <div class="blog-card">
            <a href="#">
              <img src={banner3} alt="EBT vendors: Claim Your Share of SNAP Online Revenue."
                class="blog-banner" width="300"/>
            </a>          
            <div class="blog-content">         
              <a href="#" class="blog-category">Sneaker</a>          
              <h3>
                <a href="#" class="blog-title">Bộ sưu tập Jorrdan: Đẳng cấp của nhà vua được khẳng định </a>
              </h3>
              <p class="blog-meta">
                By <cite>Mr NgocThong</cite> / <time datetime="2022-02-10">Feb 10, 2024</time>
              </p>
            </div>
          </div>
          <div class="blog-card">          
            <a href="#">
              <img src={banner4} alt="Curbside fashion Trends: How to Win the Pickup Battle."
                class="blog-banner" width="300"/>
            </a>         
            <div class="blog-content">         
              <a href="#" class="blog-category">Sneaker</a>          
              <h3>
                <a href="#" class="blog-title">Nike Air Max: Một siêu phẩm, đôi giày chạy mạnh mẽ từ Nike.</a>
              </h3>          
              <p class="blog-meta">
                By <cite>Mr QuocToan</cite> / <time datetime="2022-03-15">Mar 15, 2024</time>
              </p>         
            </div>          
          </div>
        </div>
      </div>
    </div>
          </div>
      </div>  
    </>
  );
}

export default Home;
