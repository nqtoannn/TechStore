import React, { useState, useEffect } from "react";
import { DataGrid } from "@mui/x-data-grid";
import {
  Dialog,
  TextField,
  DialogTitle,
  DialogContent,
  DialogActions,
  CircularProgress,
  MenuItem,
  IconButton,
} from "@mui/material";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { toast } from "react-toastify";
import { getToken } from "../utils/getToken";

const PromotionManagement = () => {
  const [promotionList, setPromotionList] = useState([]);
  const [selectedPromotion, setSelectedPromotion] = useState(null);
  const [openDetail, setOpenDetail] = useState(false);
  const [openAdd, setOpenAdd] = useState(false);
  const [productList, setProductList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [newPromotion, setNewPromotion] = useState({
    promotionName: "",
    startDate: "",
    endDate: "",
    userId: 2, // Assuming you have a fixed userId for now
    promotionList: [
      {
        productId: "",
        discountPercent: "",
      },
    ],
  });
  const token = getToken();

  const fetchPromotions = async () => {
    try {
      setLoading(true);
      const response = await fetch(
        "http://localhost:8080/techstore/api/management/promotion/getAll",
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      if (!response.ok) {
        throw new Error("Failed to fetch promotion data");
      }
      const data = await response.json();
      if (Array.isArray(data.data)) {
        setPromotionList(data.data.reverse());
      } else {
        throw new Error("Data format is incorrect");
      }
    } catch (error) {
      console.error("Error fetching promotion data:", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchProducts = async () => {
    try {
      const response = await fetch(
        "http://localhost:8080/techstore/api/products/findAll"
      );
      if (!response.ok) {
        throw new Error("Failed to fetch product data");
      }
      const data = await response.json();
      if (Array.isArray(data.data)) {
        setProductList(data.data);
      } else {
        throw new Error("Data format is incorrect");
      }
    } catch (error) {
      console.error("Error fetching product data:", error);
    }
  };

  const handleAddOpen = () => {
    setOpenAdd(true);
  };

  const handleAddClose = () => {
    setOpenAdd(false);
    setNewPromotion({
      promotionName: "",
      startDate: "",
      endDate: "",
      userId: 2,
      promotionList: [
        {
          productId: "",
          discountPercent: "",
        },
      ],
    });
  };

  const handleAddChange = (e) => {
    const { name, value } = e.target;
    setNewPromotion((prev) => ({ ...prev, [name]: value }));
  };

  const handlePromotionItemChange = (index, event) => {
    const { name, value } = event.target;
    const items = [...newPromotion.promotionList];
    items[index][name] = value;
    setNewPromotion({ ...newPromotion, promotionList: items });
  };

  const addPromotionItem = () => {
    setNewPromotion((prev) => ({
      ...prev,
      promotionList: [
        ...prev.promotionList,
        {
          productId: "",
          discountPercent: "",
        },
      ],
    }));
  };

  const handleAddSubmit = async () => {
    try {
      setLoading(true);
      const response = await fetch(
        "http://localhost:8080/techstore/api/management/promotion/add",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(newPromotion),
        }
      );
      if (!response.ok) {
        throw new Error("Failed to add promotion");
      }

      toast.success("Thêm khuyến mãi thành công", {
        toastStyle: {
          background: "#4CAF50",
          color: "white",
          border: "1px solid #388E3C",
          borderRadius: "8px",
          boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)",
        },
      });

      handleAddClose();
      fetchPromotions();
    } catch (error) {
      console.error("Error adding promotion:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPromotions();
    fetchProducts();
  }, []);

  const handlePromotionClick = (promotion) => {
    setSelectedPromotion(promotion);
    setOpenDetail(true);
  };

  const handleDetailClose = () => {
    setOpenDetail(false);
    setSelectedPromotion(null);
  };

  const promotionColumns = [
    { field: "id", headerName: "ID", width: 40 },
    {
      field: "promotionName",
      headerName: "Tên khuyến mãi",
      width: 600,
      editable: false,
    },
    {
      field: "startDate",
      headerName: "Ngày bắt đầu",
      width: 200,
      editable: false,
    },
    {
      field: "endDate",
      headerName: "Ngày kết thúc",
      width: 200,
      editable: false,
    },
    {
      field: "actions",
      headerName: "Chi tiết",
      width: 80,
      renderCell: (params) => (
        <IconButton onClick={() => handlePromotionClick(params.row)}>
          <VisibilityIcon color="info" />
        </IconButton>
      ),
    },
  ];

  return (
    <div className="max-w-7xl bg-gray-200 mx-auto my-12 h-[70vh] rounded-md">
      
      <div className="flex justify-between py-6 mx-6">
        <h4 className="text-2xl font-bold">Quản lý khuyến mãi</h4>
        <div className="flex justify-between">
        <button
          className="px-4 py-2 text-white text-base font-semibold bg-orange-500 hover:bg-orange-600 rounded"
          onClick={handleAddOpen}
        >
          Thêm khuyến mãi
        </button>
      </div>
      </div>
      <Dialog open={openAdd} onClose={handleAddClose} maxWidth="md" fullWidth>
        <DialogTitle>Thêm khuyến mãi mới</DialogTitle>
        <DialogContent>
          <TextField
            label="Tên khuyến mãi"
            name="promotionName"
            variant="outlined"
            fullWidth
            margin="normal"
            value={newPromotion.promotionName}
            onChange={handleAddChange}
          />
          <TextField
            label="Ngày bắt đầu"
            name="startDate"
            type="date"
            variant="outlined"
            fullWidth
            margin="normal"
            value={newPromotion.startDate}
            onChange={handleAddChange}
            InputLabelProps={{ shrink: true }}
          />
          <TextField
            label="Ngày kết thúc"
            name="endDate"
            type="date"
            variant="outlined"
            fullWidth
            margin="normal"
            value={newPromotion.endDate}
            onChange={handleAddChange}
            InputLabelProps={{ shrink: true }}
          />

          {newPromotion.promotionList.map((item, index) => (
            <div key={index} className="mt-4">
              <h6 className="text-xl font-medium">
                Sản phẩm khuyến mãi {index + 1}
              </h6>
              <TextField
                label="Sản phẩm"
                name="productId"
                select
                variant="outlined"
                fullWidth
                margin="normal"
                value={item.productId}
                onChange={(e) => handlePromotionItemChange(index, e)}
              >
                {productList.map((product) => (
                  <MenuItem key={product.id} value={product.id}>
                    {product.name}
                  </MenuItem>
                ))}
              </TextField>
              <TextField
                label="Phần trăm giảm giá"
                name="discountPercent"
                type="number"
                variant="outlined"
                fullWidth
                margin="normal"
                value={item.discountPercent}
                onChange={(e) => handlePromotionItemChange(index, e)}
              />
            </div>
          ))}

          <button
            className="mt-4 px-4 py-2 w-full text-white text-base font-semibold bg-orange-500 hover:bg-orange-600 rounded"
            onClick={addPromotionItem}
          >
            Thêm sản phẩm khuyến mãi
          </button>
        </DialogContent>

        <DialogActions>
          <button
            className="mt-4 px-4 py-2 text-white text-base font-semibold bg-gray-500 hover:bg-gray-600 rounded"
            onClick={handleAddClose}
          >
            Hủy
          </button>
          <button
            className={`mt-4 px-4 py-2 text-white text-base font-semibold rounded-md"
            ${
              loading ? "bg-blue-300" : "bg-green-500 hover:bg-green-600 rounded-md"
            }`}
            onClick={handleAddSubmit}
            disabled={loading}
          >
            {loading ? <CircularProgress size={24} /> : "Thêm khuyến mãi"}
          </button>
        </DialogActions>
      </Dialog>

      {loading ? (
        <div className="flex justify-center items-center h-[300px]">
          <CircularProgress />
        </div>
      ) : (
        <div className="px-6 pb-6">
          <DataGrid 
          rows={promotionList}
          columns={promotionColumns}
          pageSize={5}
          rowsPerPageOptions={[5, 10, 20]}
          autoHeight
          sx={{ mb: 3 }}
        />
        </div>
        
      )}

      <Dialog
        open={openDetail}
        onClose={handleDetailClose}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>Chi tiết khuyến mãi</DialogTitle>
        {selectedPromotion && (
          <DialogContent>
            <h6 className="text-xl font-bold">
              {selectedPromotion.promotionName}
            </h6>
            <p className="text-sm">
              <strong>Ngày bắt đầu:</strong> {selectedPromotion.startDate}
            </p>
            <p className="text-sm">
              <strong>Ngày kết thúc:</strong> {selectedPromotion.endDate}
            </p>
            <p className="text-sm font-semibold">Chi tiết khuyến mãi:</p>

            {selectedPromotion.promotionDetail.map((detail) => (
              <div key={detail.id} className="mt-2">
                <p className="text-sm">
                  <strong>Sản phẩm:</strong> {detail.product.name}
                </p>
                <p className="text-sm">
                  <strong>Mô tả:</strong> {detail.product.description}
                </p>
                <p className="text-sm">
                  <strong>Phân loại:</strong>{" "}
                  {detail.product.category.categoryName}
                </p>
                <p className="text-sm">
                  <strong>Giảm giá:</strong> {detail.discountPercent}%
                </p>
                <img
                  src={detail.product.imageUrl} 
                  alt="product"
                  className="w-1/3 mt-2"
                />
              </div>
            ))}
          </DialogContent>
        )}
        <DialogActions>
          <button
            className="text-blue-500 hover:text-blue-700 font-semibold"
            onClick={handleDetailClose}
          >
            Đóng
          </button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default PromotionManagement;
