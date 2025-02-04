import React, { useState, useEffect} from 'react';
import Box from '@mui/material/Box';
import { toast } from 'react-toastify';
import { getToken } from '../utils/getToken';
import { useNavigate } from 'react-router-dom';
import axios from "axios";

const AddProduct = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();  
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [newProduct, setNewProduct] = useState({
    productName: '',
    description: '',
    imageUrl: '',
    categoryId: '',
    brandId: '',
    attributes: {}, 
    productItems: [
      {
        productItemName: '',
        price: '',
        quantityInStock: '',
        imageUrl: ''
      }
    ]
  });
  const [attributesList, setAttributesList] = useState([{ key: '', value: '' }]);
  const token = getToken();
  const [productImg, setProductImg] = useState(null);
  const [productPreviewImg, setproductPreviewImg] = useState(null);

  useEffect(() => {
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

    fetchCategories();
    fetchBrands();
  }, []);

  const handleAddSubmit = async () => {
    try {
      setLoading(true); 
      const response = await fetch('http://localhost:8080/techstore/api/management/products/add', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(newProduct)
      });
      if (!response.ok) {
        throw new Error('Failed to add product');
      }
      const addedProduct = await response.json();
      const [productId, ...productItemIds] = addedProduct.data;
      const formData = new FormData();
      formData.append('file', productImg);
      formData.append('productId', productId);
      formData.append('namePath', "product");
      const uploadResponse = await fetch('http://localhost:8080/techstore/api/management/products/uploadImageProduct', {
        method: 'POST',
        body: formData,
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!uploadResponse.ok) {
        throw new Error('Failed to upload product image');
      }
      for (let i = 0; i < productItemIds.length; i++) {
        const item = newProduct.productItems[i];
        const imageFile = item.imageUrl;
        // const formData = new FormData();
        // formData.append('file', imageFile);
        // formData.append('productId', productId);
        // formData.append('namePath', "product");
        // const uploadResponse = await fetch('http://localhost:8080/techstore/api/management/products/uploadImageProduct', {
        //   method: 'POST',
        //   body: formData,
        //   headers: {
        //     'Authorization': `Bearer ${token}`
        //   }
        // });
  
        // if (!uploadResponse.ok) {
        //   throw new Error('Failed to upload product image');
        // }
        if (imageFile) {
          const formData = new FormData();
          formData.append('file', imageFile);
          formData.append('productItemId', productItemIds[i]);
          formData.append('namePath', "productItem");
  
          const uploadResponse = await fetch('http://localhost:8080/techstore/api/management/productItem/uploadImageProductItem', {
            method: 'POST',
            body: formData,
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });
  
          if (!uploadResponse.ok) {
            throw new Error('Failed to upload product item image');
          }
        }
      }
  
      toast.success('Thêm sản phẩm thành công', {
        toastStyle: {
          background: "#4CAF50",
          color: "white",
          border: "1px solid #388E3C",
          borderRadius: "8px",
          boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)", // Bóng đổ
        }
      });
      navigate("/manage/products");
  
    } catch (error) {
      console.error('Error adding product or uploading image:', error);
    } finally {
      setLoading(false); 
    }
  };

  const handleAddChange = (e) => {
    const { name, value } = e.target;
    setNewProduct((prev) => ({ ...prev, [name]: value }));
    console.log(newProduct);
  };
  
  const handleProductItemChange = (index, event) => {
    const { name, value } = event.target;
    const items = [...newProduct.productItems];
    items[index][name] = value;
    setNewProduct({ ...newProduct, productItems: items });
  };

  const handleAttributeChange = (index, event) => {
    const { name, value } = event.target;
    const updatedAttributes = [...attributesList];
    updatedAttributes[index][name] = value;
    setAttributesList(updatedAttributes);
    const updatedProductAttributes = {};
    updatedAttributes.forEach(attr => {
      if (attr.key) updatedProductAttributes[attr.key] = attr.value;
    });
    setNewProduct((prev) => ({ ...prev, attributes: updatedProductAttributes }));
  };

  const addProductItem = () => {
    setNewProduct((prev) => ({
      ...prev,
      productItems: [
        ...prev.productItems,
        {
          productItemName: '',
          price: '',
          quantityInStock: '',
          detailQuantity: '0,0,0,0,0,0,0',
          imageUrl: ''
        }
      ]
    }));
  };

  const addAttribute = () => {
    setAttributesList((prev) => [...prev, { key: '', value: '' }]);
  };
  
  const handleRemoveAttribute = (index) => {
    setAttributesList((prevAttributes) => prevAttributes.filter((_, i) => i !== index));
  };

  const handleImageChange = (index, e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        const items = [...newProduct.productItems];
        items[index].imagePreviewUrl = reader.result;
        items[index].imageUrl = file;
        setNewProduct({ ...newProduct, productItems: items });
      };
      reader.readAsDataURL(file);
    }
  };

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      setProductImg(file);
      setproductPreviewImg(URL.createObjectURL(file))
    }
  };

  return (
    <div className='max-w-7xl bg-gray-200 mx-auto my-12 rounded-md'>
      <div className="flex justify-between mb-2 px-12 py-6">
        <h4 className="text-2xl font-bold">Thêm sản phẩm mới</h4>
      </div>
      <div maxwidth="md" fullwidth>
        <div className="mb-4 mx-12">
          <label className="block text-base text-black" htmlFor="productName">Tên sản phẩm</label>
          <input
            type="text"
            id="productName"
            name="productName"
            className="w-full mt-1 rounded-md bg-white/5 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-black focus:ring-rose-500"
            value={newProduct.productName}
            onChange={handleAddChange}
            required
          />
        </div>
        <div className="mb-4 mx-12">
          <label className="block text-base text-black" htmlFor="description">Mô tả</label>
          <input
            type="text"
            id="description"
            name="description"
            className="w-full mt-1 rounded-md bg-white/5 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-black focus:ring-rose-500"
            value={newProduct.description}
            onChange={handleAddChange}
            required
          />
        </div>
        <div className='flex'>
        <div className="mb-4 mx-12 flex-1">
            <label htmlFor="categoryId" className="font-semibold">Phân loại: </label>
              <select
                id="categoryId"
                name="categoryId"
                onChange={(e) => handleAddChange}
                className="border border-gray-300 rounded p-2"
              >
                <option value="" >Chọn phân loại</option>
                {categories.map(category => (
                  <option key={category.id} value={category.id}>{category.categoryName}</option>
                ))}
              </select>
          </div>
          <div className="mb-4 mx-12 flex-1">
              <label htmlFor="brandId" className="font-semibold">Thương hiệu: </label>
              <select
                id="brandId"
                name="brandId"
                onChange={(e) => handleAddChange}
                className="border border-gray-300 rounded p-2"
              >
                <option value="" >Chọn thương hiệu</option>
                {brands.map(brand => (
                  <option key={brand.id} value={brand.id}>{brand.name}</option>
                ))}
              </select>
          </div>
        </div>
        
        <div className='mb-6 mx-12'>
          <div className='flex'>
            <h5 className="text-lg font-semibold">Thông số kỹ thuật</h5>
            <button onClick={addAttribute} className="w-40 ml-[830px] bg-green-400 hover:bg-green-700 text-white font-bold rounded-md">
              Thêm thông số
            </button>
          </div>
          
          <div className='flex'>
            <label className="block text-base text-black mr-[490px]" htmlFor="description">Tên thông số</label>
            <label className="block text-base text-black" htmlFor="description">Giá trị</label>
          </div>
          {attributesList.map((attribute, index) => (
            <Box key={index} mt={2} display="flex" gap={2}>
              <input
                type="text"
                name="key"
                className="w-full rounded-md bg-white/5 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-black focus:ring-rose-500"
                value={attribute.key}
                onChange={(e) => handleAttributeChange(index, e)}
                required
              />
              <input
                type="text"
                name="value"
                className="w-full  rounded-md bg-white/5 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-black focus:ring-rose-500"
                value={attribute.value}
                onChange={(e) => handleAttributeChange(index, e)}
                required
              />
              <button
                onClick={() => handleRemoveAttribute(index)}
                className="text-red-500 font-semibold"
              >
                Xóa
              </button>
            </Box>
          ))}
          
        </div>
        <div className='mb-6 mx-12'>
          <label className="block text-base text-black" htmlFor="qis">Tải lên hình ảnh</label>
          <input 
            type="file" 
            label="Tải lên hình ảnh" 
            onChange={handleImageUpload}
            className="block w-full text-sm border border-gray-300 rounded-lg  cursor-pointer bg-emerald-300 hover:bg-green-500 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent text-black font-bold py-2 px-4 transition-colors duration-200"
          />
          {productPreviewImg && <img src={productPreviewImg} alt="product" style={{ width: '200px' , marginTop: '20px' }} />}
        </div>
        
        <div className='mb-4 mx-24'>
          {newProduct.productItems.map((item, index) => (
            <div key={index} className='mt-2 rounded-md bg-slate-300 p-6 '>
              <h5 className="text-lg font-semibold">Phân loại sản phẩm {index+1}</h5>
              <label className="block text-base text-black" htmlFor="productItemName">Tên phân loại sản phẩm</label>
              <input
                type="text"
                name="productItemName"
                className="w-full mt-1 rounded-md bg-white/5 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-black focus:ring-rose-500"
                value={item.productItemName}
                onChange={(e) => handleProductItemChange(index, e)}
              />
              <label className="block text-base text-black" htmlFor="price">Giá phân loại</label>
              <input
                type="text"
                name="price"
                className="w-full mt-1 rounded-md bg-white/5 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-black focus:ring-rose-500"
                value={item.price}
                onChange={(e) => handleProductItemChange(index, e)}
                required
              />
              <label className="block text-base text-black" htmlFor="qis">Số lượng</label>
              <input
                type="number"
                name="quantityInStock"
                className="w-full mt-1 rounded-md bg-white/5 py-2 px-4 text-black outline-none shadow-sm ring-1 ring-inset ring-black focus:ring-rose-500"
                value={item.quantityInStock}
                onChange={(e) => handleProductItemChange(index, e)}
                required
              />
            <label className="block text-base text-black" htmlFor="qis">Tải lên hình ảnh</label>
            <input 
              type="file" 
              label="Tải lên hình ảnh" 
              onChange={(e) => handleImageChange(index, e)}
              className="block w-full text-sm border border-gray-300 rounded-lg  cursor-pointer bg-emerald-300 hover:bg-green-500 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent text-black font-bold py-2 px-4 transition-colors duration-200"
            />
            {item.imagePreviewUrl && <img src={item.imagePreviewUrl} alt="product" style={{ width: '30%', marginTop: '20px' }} />}
          </div>
          ))}
          <button onClick={addProductItem} className="w-2/6 ml-80 bg-rose-400 hover:bg-rose-700 text-white font-bold py-2 rounded-md mt-4">
            Thêm phân loại sản phẩm
          </button>
          <div className='flex pb-12 mt-8'>
          <button
            onClick={() => navigate("/manage/products")}
            className="w-full bg-gray-400 hover:bg-gray-700 text-white font-bold py-2 rounded-md mt-4 mx-12"
          >
            Hủy bỏ
          </button>
            <button onClick={handleAddSubmit} className="w-full bg-rose-500 hover:bg-rose-700 text-white font-bold py-2 rounded-md mt-4 mx-12">
              Lưu sản phẩm
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddProduct;