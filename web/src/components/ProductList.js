import React, { useEffect, useState } from "react";
import { ProductElement } from ".";
import axios from "axios";
import { useNavigate, useSearchParams } from "react-router-dom";
import { nanoid } from "nanoid";

function ProductList() {
  const [productItemData, setProductItemData] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState('');
  const [minRating, setMinRating] = useState('');
  const [sort, setSort] = useState('');
  const [searchProduct, setSearchProduct] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const navigate = useNavigate();
  const [brands, setBrands] = useState([]);
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const searchQuery = searchParams.get('search') || '';
    setSearchProduct(searchQuery);
    fetchCategories();
    fetchBrands();
    fetchData(currentPage, searchQuery);
  }, [currentPage, searchParams]);

  const fetchCategories = async () => {
    try {
      const response = await axios.get("http://localhost:8080/techstore/api/category/findAll");
      setCategories(response.data.data.categoryList);
    } catch (error) {
      console.error("Error fetching categories:", error);
    }
  };

  const fetchBrands = async () => {
    try {
      const response = await axios.get("http://localhost:8080/techstore/api/brand/findAll");
      setBrands(response.data.data.brandList);
    } catch (error) {
      console.error("Error fetching brands:", error);
    }
  };

  const fetchData = async (page = 0, query = '') => {
    try {
      let url = `http://localhost:8080/techstore/api/products/findAll?page=${page}&size=20`;
      
      if (selectedCategoryId || minRating || sort || query) {
        url = `http://localhost:8080/techstore/api/products/filter?page=${page}&size=20`;
      }

      const params = {
        categoryId: selectedCategoryId || null,
        minRating: minRating || null,
        sortOrder: sort || null,
        searchQuery: query || null,
      };

      const response = await axios.get(url, { params });
      const data = response.data.data;
      const filteredProduct = data.products.filter(product => product.status === 'ACTIVE');

      setProductItemData(filteredProduct);
      setCurrentPage(data.currentPage);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  const handleProductClick = (productId) => {
    navigate(`/product/${productId}`);
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  return (
    <>
      <div className="max-w-7xl bg-zinc-100 mx-auto my-12 min-h-[70vh] rounded-md">
        <div>
          <h2 className="px-6  py-4 mt-[-20px] text-2xl font-bold">Danh sách sản phẩm</h2>
        </div>

        <div className=" mx-6 filter-container flex flex-col md:flex-row items-center justify-between p-2 bg-white rounded shadow-md mb-4">
          <div className="flex items-center mb-2 ml-4 md:mb-0">
            <label htmlFor="category-select" className="mr-2 font-semibold">Mặt hàng:</label>
            <select
              id="category-select"
              onChange={(e) => setSelectedCategoryId(e.target.value)}
              className="border border-gray-300 rounded p-2"
            >
              <option value="">Tất cả</option>
              {categories.map(category => (
                <option key={category.id} value={category.id}>{category.categoryName}</option>
              ))}
            </select>
          </div>

          <div className="flex items-center mb-2 md:mb-0">
            <label htmlFor="category-select" className="mr-2 font-semibold">Thương hiệu:</label>
            <select
              id="category-select"
              onChange={(e) => setSelectedCategoryId(e.target.value)}
              className="border border-gray-300 rounded p-2"
            >
              <option value="">Tất cả</option>
              {brands.map(brand => (
                <option key={brand.id} value={brand.id}>{brand.name}</option>
              ))}
            </select>
          </div>

          <div className="flex items-center mb-2 md:mb-0">
            <label htmlFor="rating-select" className="mr-2 font-semibold">Min Rating:</label>
            <select
              id="rating-select"
              value={minRating}
              onChange={(e) => setMinRating(e.target.value)}
              className="border border-gray-300 rounded p-2"
            >
              <option value="">Select Rating</option>
              {[1, 2, 3, 4, 5].map((rating) => (
                <option key={rating} value={rating}>
                  {Array(rating).fill('⭐').join('')} 
                </option>
              ))}
            </select>
          </div>

          <div className="flex items-center mb-2 md:mb-0">
            <label htmlFor="rating-select" className="mr-2 font-semibold">Sắp xếp:</label>
            <select
              id="sort-select"
              value={sort}
              onChange={(e) => setSort(e.target.value)}
              className="border border-gray-300 rounded p-2"
            >
              <option value="">Select Sort</option>
              <option key="desc" value="desc">
                Đã bán nhiều nhất
              </option>
              <option key="asc" value="asc">
                Đã bán ít nhất
              </option>
            </select>
          </div>

          <div className="flex space-x-4 mr-2">
            <button
              onClick={() => fetchData(0)}
              className="bg-blue-500 text-white rounded px-4 py-2 hover:bg-blue-600"
            >
              Áp dụng
            </button>
            <button
              onClick={() => {
                setSelectedCategoryId('');
                setMinRating('');
                setSort('');
                fetchData(0); 
              }}
              className="bg-gray-300 text-black rounded px-4 py-2 hover:bg-gray-400"
            >
              Hủy
            </button>
          </div>
        </div>


        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-3 bg-transparent p-10">
          {productItemData.length !== 0 &&
            productItemData.map((product) => (
              <ProductElement
                key={nanoid()}
                id={product.id}
                title={product.name}
                image={product.imageUrl}
                rating={product.rating}
                price={product.avrPrice}
                description={product.description}
                discount={product.discountPercent}
                onProductClick={() => handleProductClick(product.id)}
                sold={product.sold}
              />
            ))}
        </div>

        <div className="flex justify-center my-4 pb-6">
          <button
            disabled={currentPage === 0}
            onClick={() => handlePageChange(currentPage - 1)}
            className="mr-4 px-4 py-2 bg-green-300 rounded disabled:opacity-50"
          >
            Previous
          </button>
          <button
            disabled={true}
            className="px-4 py-2 mr-4 bg-orange-500 rounded disabled:opacity-50"
          >
            {currentPage + 1}
          </button>
          <button
            disabled={currentPage === totalPages - 1}
            onClick={() => handlePageChange(currentPage + 1)}
            className="px-4 py-2 bg-green-300 rounded disabled:opacity-50"
          >
            Next
          </button>
        </div>
      </div>
    </>
  );
}

export default ProductList;
