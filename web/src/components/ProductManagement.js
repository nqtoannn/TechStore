import React, { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import { DataGrid } from '@mui/x-data-grid';
import { Dialog, DialogContent, DialogActions, Typography, CircularProgress, IconButton } from '@mui/material';
import VisibilityIcon from '@mui/icons-material/Visibility';
import ToggleOnIcon from '@mui/icons-material/ToggleOn';
import ToggleOffIcon from '@mui/icons-material/ToggleOff';
import { toast } from 'react-toastify';
import { getToken } from '../utils/getToken';
import { useNavigate } from 'react-router-dom';


const ProductManagement = () => {
  const [productList, setProductList] = useState([]);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [openDetail, setOpenDetail] = useState(false);
  const [openAddCategory, setOpenAddCategory] = useState(false);
  const [openAddBrand, setOpenBrand] = useState(false);
  const [loading, setLoading] = useState(true);
  const [brandName, setBrandName] = useState('');
  const [categoryName, setCategoryName] = useState('');
  const navigate = useNavigate();
  const [searchKeyword, setSearchKeyword] = useState('');
  const [filterStatus, setFilterStatus] = useState('all');
  const [filteredProductList, setFilteredProductList] = useState([]);

  const token = getToken();

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/techstore/api/products/findAllWithAllStatus');
      if (!response.ok) {
        throw new Error('Failed to fetch product data');
      }
      const data = await response.json();
      if (Array.isArray(data.data)) {
        setProductList(data.data.reverse());
      } else {
        throw new Error('Data format is incorrect');
      }
    } catch (error) {
      console.error('Error fetching product data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusToggle = async (id, currentStatus) => {
    const newStatus = currentStatus === 'ACTIVE' ? 'DEACTIVE' : 'ACTIVE';
    const token = getToken();
  
    try {
      const response = await fetch(
        'http://localhost:8080/techstore/api/management/products/updateStatus', {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            id: id,
            status: newStatus,
          })
        }
      );
      
      if (response.ok) {
        console.log("Status updated successfully");
        toast.success('Thay đổi trạng thái sản phẩm thành công', {
          toastStyle: {
            background: "#4CAF50", // Màu nền
            color: "white", // Màu chữ
            border: "1px solid #388E3C", // Viền
            borderRadius: "8px", // Bo tròn viền
            boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)", // Bóng đổ
          }
        });
        fetchProducts();
      } else {
        console.error("Failed to update status:", response.status);
      }
    } catch (error) {
      console.error("Error updating status:", error);
    }
  };
  

  const handleDetailClose = () => {
    setOpenDetail(false);
    setSelectedProduct(null);
  };

  const handleAddCategoryClose = () => {
    setOpenAddCategory(false);
  };

  const handleAddCategoryOpen = () => {
    setOpenAddCategory(true);
  };
  const handleAddBrandClose = () => {
    setOpenBrand(false);
  };

  const handleAddBrandOpen = () => {
    setOpenBrand(true);
  };

  const handleSearchChange = (e) => {
    setSearchKeyword(e.target.value);
    filterProducts(e.target.value, filterStatus);
  };

  const handleFilterChange = (e) => {
    setFilterStatus(e.target.value);
    filterProducts(searchKeyword, e.target.value);
  };

  const handleAddCategory = async () => {
    try {
      setLoading(true); 
      const payload = { categoryName: categoryName };
      const response = await fetch('http://localhost:8080/techstore/api/category/add', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });
      if (!response.ok) {
        throw new Error('Failed to add product');
      }
      toast.success('Thêm phân loại thành công', {
        toastStyle: {
          background: "#4CAF50",
          color: "white",
          border: "1px solid #388E3C",
          borderRadius: "8px",
          boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)", 
        }
      });
      setOpenAddCategory(false);
  
    } catch (error) {
      console.error('Error adding product or uploading image:', error);
    } finally {
      setLoading(false); 
    }
  };

  const handleAddBrand = async () => {
    try {
      setLoading(true); 
      const payload = { brandName: brandName };
      const response = await fetch('http://localhost:8080/techstore/api/management/brand/add', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });
      if (!response.ok) {
        throw new Error('Failed to add product');
      }
      toast.success('Thêm thương hiệu thành công', {
        toastStyle: {
          background: "#4CAF50",
          color: "white",
          border: "1px solid #388E3C",
          borderRadius: "8px",
          boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)", 
        }
      });
      setOpenBrand(false);
  
    } catch (error) {
      console.error('Error adding product or uploading image:', error);
    } finally {
      setLoading(false); 
    }
  };

  const filterProducts = (keyword, status) => {
    const filtered = productList.flatMap(product => 
      product.productItems.filter(item => {
        const matchesKeyword = item.productItemName.toLowerCase().includes(keyword.toLowerCase());
        const matchesStatus = status === 'all' ? true : item.status === status;
        return matchesKeyword && matchesStatus;
      }).map(item => ({ ...item, name: product.name, category: product.category,  avrPrice: product.avrPrice, description: product.description }))
    );
    setFilteredProductList(filtered);
  };

  
  useEffect(() => {
    fetchProducts();
  }, []);

  const handleProductClick = (product) => {
    setSelectedProduct(product);
    setOpenDetail(true);
  };
  
  const productColumns = [
    { field: 'id', headerName: 'ID', width: 40 },
    {
      field: 'name',
      headerName: 'Tên sản phẩm',
      width: 300,
      editable: false,
    },
    {
      field: 'imageUrl',
      headerName: 'Hình ảnh',
      width: 90,
      editable: false,
      renderCell: (params) => (
        <img src={params.value} alt="product" className="h-full w-auto object-cover" />
      ),
    },
    {
      field: 'category',
      headerName: 'Phân loại',
      width: 130,
      editable: false,
    },
    {
      field: 'avrPrice',
      headerName: 'Giá (VND)',
      width: 200,
      editable: false,
    },
    {
      field: 'description',
      headerName: 'Mô tả',
      width: 260,
      editable: false,
    },
    {
      field: 'actions2',
      headerName: 'Trạng thái',
      width: 100,
      renderCell: (params) => (
        <IconButton onClick={() => handleStatusToggle(params.row.id, params.row.status)}>
          {params.row.status === 'ACTIVE' ? <ToggleOnIcon className="text-green-500" /> : <ToggleOffIcon className="text-red-500" />}
        </IconButton>
      ),
    },
    {
      field: 'actions',
      headerName: 'Chi tiết',
      width: 80,
      renderCell: (params) => (
        <IconButton onClick={() => handleProductClick(params.row)}>
          <VisibilityIcon className="text-blue-500" />
        </IconButton>
      ),
    },
  ];
  
  return (
    <div className='max-w-7xl bg-gray-200 mx-auto my-12 min-h-[70vh] rounded-md'>
      <div className="flex justify-between  mb-2 px-8 py-6">
        <h4 className="text-2xl font-bold ">Quản lý sản phẩm</h4>
        <div className="flex"><button 
          className="bg-orange-500 text-white text-base px-4 py-2 mx-8 rounded hover:bg-orange-600 transition duration-200 mr-2"
          onClick={handleAddCategoryOpen}
        >
          Thêm phân loại
        </button>
        <button 
          className="bg-orange-500 text-white text-base px-4 py-2 mx-8 rounded hover:bg-orange-600 transition duration-200 mr-2"
          onClick={handleAddBrandOpen}
        >
          Thêm nhãn hàng
        </button>
        <button 
          className="bg-orange-500 text-white text-base px-4 py-2 mx-8 rounded hover:bg-orange-600 transition duration-200 mr-2"
          onClick={() => navigate("/manage/products/addProduct")}
        >
          Thêm sản phẩm
        </button>
        </div>
        
       
      </div>
      
      <div className='flex justify-between mx-8 mb-8'>
        <input
          type="text"
          placeholder="Tìm kiếm sản phẩm"
          value={searchKeyword}
          onChange={handleSearchChange}
          className="w-full max-w-[800px] py-2 px-4 border border-gray-400 rounded-md focus:ring-indigo-500"
        />
        <div className="ml-8 min-w-[380px]">
          <select
            value={filterStatus}
            onChange={handleFilterChange}
            className="w-full py-2 px-4 border rounded-md"
          >
            <option value="ALL">Tất cả</option>
            <option value="ACTIVE">Đang kinh doanh</option>
            <option value="DEACTIVE">Ngừng kinh doanh</option>
          </select>
        </div>
      </div>
      
      {loading ? (
        <Box display="flex" justifyContent="center" alignItems="center" height="300px">
          <CircularProgress />
        </Box>
      ) : (
        <div className='px-8'>
          <DataGrid
            rows={filteredProductList.length > 0 ? filteredProductList : productList}
            columns={productColumns}
            pageSize={5}
            rowsPerPageOptions={[5, 10, 20]}
            autoHeight
            sx={{ mb: 3 }}
          />
        </div>
      )}
      
      <Dialog open={openDetail} onClose={handleDetailClose} maxWidth="md" fullWidth>
        <h4 className="font-semibold text-2xl ml-6 mt-4">Chi tiết sản phẩm</h4>
        {selectedProduct && (
          <DialogContent>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Typography variant="body1"><strong>Tên sản phẩm:</strong> {selectedProduct.name}</Typography>
                <div className="flex items-center justify-between mt-4">
                  <div className="flex items-center text-yellow-500">
                    {[...Array(5)].map((_, index) => (
                      <svg
                        key={index}
                        className={`w-5 h-5 ${
                          index < selectedProduct.rating ? "fill-current" : "text-gray-300"
                        }`}
                        xmlns="http://www.w3.org/2000/svg"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                      >
                        <path d="M12 17.27l6.18 3.73-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
                      </svg>
                    ))}
                    <div className="text-gray-500 text-sm">({selectedProduct.rated} đánh giá)</div>
                  </div>
                  <div className="text-gray-500 text-sm"> Đã bán {selectedProduct.sold} </div>
                </div>
                <Typography variant="body1"><strong>Mô tả:</strong> {selectedProduct.description}</Typography>
                <Typography variant="body1"><strong>Phân loại:</strong> {selectedProduct.category}</Typography>
                <Typography variant="body1"><strong>Thương hiệu:</strong> {selectedProduct.brand}</Typography>
                <Typography variant="body1"><strong>Giá:</strong> {selectedProduct.avrPrice} VND</Typography>
                <Typography variant="body1"><strong>Trạng thái:</strong> {selectedProduct.status === 'ACTIVE' ? 'Đang kinh doanh' : 'Ngừng kinh doanh'}</Typography>
                <h6 className="font-semibold mb-2">Thông số kỹ thuật</h6>
                <table className="w-full table-auto">
                  <tbody>
                    {Object.entries(selectedProduct.attributes).map(([key, value]) => (
                      <tr key={key}>
                        <td className="border px-4 py-2 font-semibold">{key}</td>
                        <td className="border px-4 py-2">{value}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                <img src={selectedProduct.imageUrl} alt="product" className="mt-4 w-full object-cover" />
              </div>
              <div>
              <h4 className="font-semibold text-xl mb-2">Các phân loại sản phẩm</h4>
                {selectedProduct.productItems.map((item) => (
                  <div key={item.id} className="mt-4 border border-gray-400 bg-gray-200 rounded-md p-4">
                    <Typography variant="body1"><strong>Tên:</strong> {item.productItemName}</Typography>
                    <Typography variant="body1"><strong>Giá:</strong> {item.price.toLocaleString()} VND</Typography>
                    <Typography variant="body1"><strong>Số lượng tồn kho:</strong> {item.quantityInStock}</Typography>
                    <Typography variant="body1"><strong>Trạng thái:</strong> {item.status === 'ACTIVE' ? 'Đang kinh doanh' : 'Ngừng kinh doanh'}</Typography>
                    <img src={item.imageUrl} alt={item.productItemName} className="mt-2 h-[150px] w-[150px] object-cover" />
                    {selectedProduct && selectedProduct.status === 'ACTIVE' && (
                      <button className="bg-orange-500 text-white px-4 py-2 rounded">Ngừng kinh doanh</button>
                    )}
                    {selectedProduct && selectedProduct.status === 'DEACTIVE' && (
                      <button className="bg-green-500 text-white px-4 py-2 rounded">Tiếp tục kinh doanh</button>
                    )}
                    <button className="bg-green-400 text-white px-4 py-2 ml-4 rounded">Chỉnh sửa</button>
                  </div>
                ))}
              </div>
            </div>
          </DialogContent>
        )}
        <DialogActions>
          {selectedProduct && selectedProduct.status === 'ACTIVE' && (
            <button onClick={() =>handleStatusToggle(selectedProduct.id,selectedProduct.status)} className="bg-orange-500 text-white px-4 py-2 rounded">Ngừng kinh doanh</button>
          )}
          {selectedProduct && selectedProduct.status === 'DEACTIVE' && (
            <button onClick={() =>handleStatusToggle(selectedProduct.id,selectedProduct.status)} className="bg-green-500 text-white px-4 py-2 rounded">Tiếp tục kinh doanh</button>
          )}
          <button  className="bg-green-400 text-white px-4 py-2 rounded">Chỉnh sửa</button>
          <button onClick={handleDetailClose} className="bg-gray-400 text-white px-4 py-2 rounded">Đóng</button>
        </DialogActions>
      </Dialog>
      <Dialog  open={openAddCategory} onClose={handleAddCategoryClose} maxWidth="md" fullWidth>
        <h4 className="font-semibold text-2xl ml-6 mt-4">Thêm phân loại mới</h4>
        <DialogContent>
        <div maxwidth="md" fullwidth>
        <div className="mb-4 mx-12">
          <label className="block text-base text-black" htmlFor="productName">Tên phân loại</label>
          <input
            type="text"
            id="productName"
            name="productName"
            value={categoryName}
            onChange={(e) => setCategoryName(e.target.value)}
            className="w-full mt-1 rounded-md bg-white/5 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-black focus:ring-rose-500"
            required
          />
        </div>
        </div>
        </DialogContent>
        <DialogActions>
          <button onClick={handleAddCategory} className="bg-green-400 text-white px-4 py-2 rounded">Thêm</button>
          <button onClick={handleAddCategoryClose} className="bg-gray-400 text-white px-4 py-2 rounded">Đóng</button>
        </DialogActions>
      </Dialog>


      <Dialog  open={openAddBrand} onClose={handleAddBrandClose} maxWidth="md" fullWidth>
        <h4 className="font-semibold text-2xl ml-6 mt-4">Thêm nhãn hàng mới</h4>
        <DialogContent>
        <div maxwidth="md" fullwidth>
        <div className="mb-4 mx-12">
          <label className="block text-base text-black" htmlFor="productName">Tên nhãn hàng</label>
          <input
            type="text"
            name="productName"
            value={brandName}
            onChange={(e) => setBrandName(e.target.value)}
            className="w-full mt-1 rounded-md bg-white/5 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-black focus:ring-rose-500"
            required
          />
        </div>
        </div>
        </DialogContent>
        <DialogActions>
          <button onClick={handleAddBrand} className="bg-green-400 text-white px-4 py-2 rounded">Thêm</button>
          <button onClick={handleAddBrandClose} className="bg-gray-400 text-white px-4 py-2 rounded">Đóng</button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default ProductManagement;
