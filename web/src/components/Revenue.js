import React, { useState, useEffect } from 'react';
import { Typography, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from '@mui/material';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
} from 'chart.js';
import { Bar } from 'react-chartjs-2';
import axios from 'axios';
import { format, addDays, addMonths } from 'date-fns'; 
import { getToken } from '../utils/getToken';
import { ProductElement } from ".";
import { nanoid } from "nanoid";

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend
);

const Revenue = () => {
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);
    const [open, setOpen] = useState(false);
    const [recentChartData, setRecentChartData] = useState(null);
    const [selectedChartData, setSelectedChartData] = useState(null);
    const token = getToken();
    const [productMostData, setProductMostData] = useState([]);

    useEffect(() => {
        const defaultStartDate = addDays(new Date(), -1);
        setStartDate(defaultStartDate);
        setEndDate(new Date());

        fetchDataForRecentChart();
        fetchDataProduct(); // Thêm dòng này để gọi API lấy dữ liệu sản phẩm bán chạy
    }, []);

    const fetchDataForRecentChart = async () => {
        try {
            const endDate = new Date();
            const startDate = addMonths(endDate, -3);
    
            const formattedStartDate = format(startDate, 'yyyy-MM-dd');
            const formattedEndDate = format(endDate, 'yyyy-MM-dd');
    
            const url = `http://localhost:8080/techstore/api/management/revenueProduct/between?startDate=${formattedStartDate}&endDate=${formattedEndDate}`;
    
            const response = await axios.get(url, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
    
            const productRevenue = response.data.data;
    
            const labels = productRevenue.map(item => `${item.year}-${item.month}`);
            const productData = productRevenue.map(item => item.totalRevenue);
    
            setRecentChartData({
                labels: labels,
                datasets: [
                    {
                        label: 'Doanh thu gần đây(VND)',
                        data: productData,
                        backgroundColor: '#FA790F',
                        borderColor: '#FA790F',
                        borderWidth: 1,
                        title: 'Doanh thu',
                    },
                ],
            });
        } catch (error) {
            console.error('Lỗi khi lấy dữ liệu', error);
        }
    };
    

    const fetchDataProduct = async () => {
        try {
            const response = await axios.get("http://localhost:8080/techstore/api/products/getMostPurchased");
            let products = response.data.data.filter(product => product.status === 'ACTIVE');
            setProductMostData(products);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    };

    const fetchDataForSelectedChart = async () => {
      if (startDate && endDate) {
          try {
              const formattedStartDate = format(startDate, 'yyyy-MM-dd');
              const formattedEndDate = format(endDate, 'yyyy-MM-dd');
  
              const url = `http://localhost:8080/techstore/api/management/revenueProductByDate/between?startDate=${formattedStartDate}&endDate=${formattedEndDate}`;
  
              const response = await axios.get(url, {
                  headers: {
                      Authorization: `Bearer ${token}`,
                  },
              });
              const productRevenue = response.data.data;
              const labels = productRevenue.map(item => format(new Date(item.date), 'dd/MM/yyyy'));
              const productData = productRevenue.map(item => item.totalRevenue);
              setSelectedChartData({
                  labels: labels,
                  datasets: [
                      {
                          label: 'Doanh thu theo ngày',
                          data: productData,
                          backgroundColor: 'rgba(75, 192, 192, 0.2)',
                          borderColor: 'rgba(75, 192, 192, 1)',
                          borderWidth: 1,
                      },
                  ],
              });
  
              setOpen(true);
          } catch (error) {
              console.error('Lỗi khi lấy dữ liệu', error);
          }
      }
  };  

    const handleClose = () => {
        setOpen(false);
    };

    return (
        <div className="flex flex-row h-[77vh] max-w-7xl mx-auto mt-5">
          {open && (
      <div className="fixed inset-0 flex items-center justify-center bg-gray-700 bg-opacity-75">
          <div className="bg-white p-6 rounded-lg w-[80%] max-w-4xl">
              <h3 className="text-lg font-semibold mb-4">
                  {startDate && endDate
                      ? `Biểu đồ doanh thu từ ${format(startDate, 'dd/MM/yyyy')} đến ${format(endDate, 'dd/MM/yyyy')}`
                      : 'Thông tin biểu đồ'}
              </h3>
              <div className="flex justify-center">
                  {selectedChartData && <Bar data={selectedChartData} />}
              </div>
              <div className="mt-4 text-right">
                  <button onClick={handleClose} className="text-blue-500">
                      Đóng
                  </button>
              </div>
          </div>
      </div>
    )}
  {/* Left Section - Revenue Statistics */}
  <div className="w-1/2 pr-5">
    <h5 className="text-center mb-2 text-lg font-semibold">Thống kê doanh thu</h5>
    <div className="w-full h-[40%]">
      {recentChartData && <Bar data={recentChartData} />}
    </div>
    <div>
      <h5 className="text-center mb-2 text-lg font-semibold">Tạo báo cáo doanh thu</h5>
      <div className="flex items-center space-x-4">
        {/* Start Date */}
        <div>
          <h6 className="font-semibold">Ngày bắt đầu</h6>
          <input
            type="date"
            value={startDate ? format(startDate, 'yyyy-MM-dd') : ''}
            onChange={(e) => setStartDate(new Date(e.target.value))}
            max={endDate ? format(endDate, 'yyyy-MM-dd') : ''}
            className="border border-gray-300 p-2 rounded"
          />
        </div>
        {/* End Date */}
        <div>
          <h6 className="font-semibold">Ngày kết thúc</h6>
          <input
            type="date"
            value={endDate ? format(endDate, 'yyyy-MM-dd') : ''}
            onChange={(e) => setEndDate(new Date(e.target.value))}
            max={format(new Date(), 'yyyy-MM-dd')}
            className="border border-gray-300 p-2 rounded"
          />
        </div>
      </div>
      {/* Confirm Button */}
      <button
        onClick={fetchDataForSelectedChart}
        className="bg-[#FB8423] hover:bg-[#e64a19] text-white font-bold py-2 px-4 rounded mt-4 ml-auto block"
      >
        Xác nhận
      </button>
    </div>

    {/* Dialog for Selected Chart */}
    
  </div>

  {/* Right Section - Best Selling Products */}
  <div className="w-1/2 pl-5 pb-5">
    <h5 className="text-center mb-2 text-lg font-semibold">Sản phẩm bán chạy</h5>
    <div className="grid grid-cols-2 gap-4 bg-transparent">
      {productMostData.length !== 0 &&
        productMostData.map((product) => (
          <ProductElement
            key={nanoid()}
            id={product.id}
            title={product.name}
            image={product.imageUrl}
            rating={product.rating}
            price={product.avrPrice}
            description={product.description}
            discount={product.discountPercent}
          />
        ))}
    </div>
  </div>
</div>

    );
};

export default Revenue;
