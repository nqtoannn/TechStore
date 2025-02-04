import React from 'react';
import "./styles/404page.css";

const Forbidden = () => {

  return (
    <section class="page_404">
    <div class="container">
      <div class="row">	
      <div class="col-sm-12 ">
      <div class="col-sm-10 col-sm-offset-1  text-center">
      <div class="four_zero_four_bg">
        <h1 class="text-center " style={{color: "#FA790F"}}  >404</h1>
      
      
      </div>
      
      <div class="contant_box_404">
      <h3 class="h2" style={{marginTop: "60px"}}>
      Mất kết nối
      </h3>
      
      <p>Trang bạn truy cập hiện không có sẵn!</p>
      
      <a href="/shop" class="link_404">Về trang chủ</a>
    </div>
      </div>
      </div>
      </div>
    </div>
  </section>
  );
};

export default Forbidden;
