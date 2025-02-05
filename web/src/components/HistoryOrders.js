import React, { useState, useEffect } from 'react';
import { DataGrid } from '@mui/x-data-grid';
import { IconButton, Dialog, DialogTitle, DialogContent, DialogActions} from '@mui/material';
import VisibilityIcon from '@mui/icons-material/Visibility';
import axios from 'axios';
import { getToken } from '../utils/getToken';

function HistoryOrders() {
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const [confirmDialogType, setConfirmDialogType] = useState('');
  const [rows, setRows] = useState([]);
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [searchOrderId, setSearchOrderId] = useState('');
  const token = getToken();
  const [isOpen, setIsOpen] = useState(false);

  const handleOpen = () => setIsOpen(true);
  const handleClose = () => setIsOpen(false);

  useEffect(() => {
    fetchOrders();
  }, [filterStatus, searchOrderId]);

  const fetchOrders = async () => {
    try {
      const response = await axios.get('http://localhost:8080/techstore/api/management/order/findAll', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      let filteredOrders = response.data.data;
      filteredOrders.reverse();
      if (searchOrderId === '') {
        if (filterStatus === 'ALL') {
          setRows(filteredOrders);
          return;
        }
      } else {
        if (filterStatus === 'ALL') {
          filteredOrders = filteredOrders.filter(order => order.id.toString() === searchOrderId);
          setRows(filteredOrders);
          return;
        }
      }
      if (filterStatus) {
        filteredOrders = filteredOrders.filter(order => order.orderStatus === filterStatus);
      }

      if (searchOrderId) {
        filteredOrders = filteredOrders.filter(order => order.id.toString() === searchOrderId);
      }

      setRows(filteredOrders);
    } catch (error) {
      console.error('Error fetching orders:', error);
    }
  };

  const handleButtonClick = (orderData) => {
    setSelectedOrder(orderData);
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  const handleStatusChange = (event) => {
    setFilterStatus(event.target.value);
  };

  const handleSearchChange = (event) => {
    setSearchOrderId(event.target.value);
  };

  const openConfirmDialog = (type) => {
    setConfirmDialogType(type);
    setConfirmDialogOpen(true);
  };

  const handleConfirmClose = () => {
    setConfirmDialogOpen(false);
  };

  const handleUpdateStatus = async () => {
    if (!selectedOrder) {
      console.error('No order selected to update status.');
      return;
    }

    const updateData = {
      orderId: selectedOrder.id,
      statusCode: confirmDialogType === 'accept' ? 2 : (confirmDialogType === 'delivery' ? 3 : 8)
    };

    console.log("update data: ", updateData)

    try {
      const response = await axios.put('http://localhost:8080/techstore/api/customer/updateOrderStatus', updateData, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      console.log('Order status updated successfully:', response.data);
      fetchOrders();
      handleCloseDialog();
      handleConfirmClose();
    } catch (error) {
      console.error('Error updating order status:', error);
    }
  };

  const columns = [
    { field: 'id', headerName: 'Mã đơn hàng', width: 130 },
    {
      field: 'totalPrice',
      headerName: 'Tổng tiền (VNĐ)',
      type: 'number',
      width: 130,
      editable: false,
    },
    {
      field: 'paymentMethod',
      headerName: 'Phương thức thanh toán',
      width: 180,
      editable: false,
    },
    {
      field: 'orderStatus',
      headerName: 'Trạng thái',
      width: 150,
      renderCell: (params) => {
          
              return (
                  <div>
                      {params.row.orderStatus === 'PROCESSING' ? (
                          <div style={{ fontSize: '16px', color: 'orange', fontWeight: 'bold', padding: '5px', borderRadius: '5px', display: 'inline-block' }}>
                              Chờ xác nhận
                          </div>
                      ) : params.row.orderStatus === 'ACCEPTED' ? (
                          <div style={{ fontSize: '16px', color: 'green', fontWeight: 'bold', padding: '5px', borderRadius: '5px', display: 'inline-block' }}>
                              Đã xác nhận
                          </div>
                      ) : params.row.orderStatus === 'DELIVERY' ? (
                          <div style={{ fontSize: '16px', color: 'green', fontWeight: 'bold', padding: '5px', borderRadius: '5px', display: 'inline-block' }}>
                              Đang vận chuyển
                          </div>
                      ) : params.row.orderStatus === 'SUCCESS' ? (
                          <div style={{ fontSize: '16px', color: 'green', fontWeight: 'bold', padding: '5px', borderRadius: '5px', display: 'inline-block' }}>
                              Đã hoàn thành
                          </div>
                      ): params.row.orderStatus === 'REVIEWED' ? (
                          <div style={{ fontSize: '16px', color: 'green', fontWeight: 'bold', padding: '5px', borderRadius: '5px', display: 'inline-block' }}>
                              Đã hoàn thành
                          </div>
                      ) : params.row.orderStatus === 'WAITING PAYMENT' ? (
                          <div style={{ fontSize: '16px', color: 'green', fontWeight: 'bold', padding: '5px', borderRadius: '5px', display: 'inline-block' }}>
                              Chờ thanh toán
                          </div>
                      ): params.row.orderStatus === 'PAID' ? (
                          <div style={{ fontSize: '16px', color: 'green', fontWeight: 'bold', padding: '5px', borderRadius: '5px', display: 'inline-block' }}>
                              Đã thanh toán
                          </div>
                      ) : params.row.orderStatus === 'CANCEL' ? (
                          <div style={{ fontSize: '16px', color: 'red', fontWeight: 'bold', padding: '5px', borderRadius: '5px', display: 'inline-block' }}>
                              Đã hủy
                          </div>
                      ) : (
                          params.row.orderStatus // Nếu không phù hợp với bất kỳ điều kiện nào, hiển thị giá trị status gốc
                      )}
                  </div>
              );
  }},
    {
      field: 'orderDate',
      headerName: 'Ngày đặt hàng',
      width: 150,
      editable: false,
    },
    {
      field: 'address',
      headerName: 'Địa chỉ giao hàng',
      width: 330,
      editable: false,
    },
    {
      field: 'details',
      headerName: 'Chi tiết',
      sortable: false,
      width: 60,
      renderCell: (params) => (
        <IconButton
          sx={{ height: 25, width: 30 }}
          color="primary"
          variant="contained"
          onClick={() => handleButtonClick(params.row)}
        >
          <VisibilityIcon />
        </IconButton>
      ),
    },
  ];

  return (
    
    <div className="max-w-7xl bg-gray-200 mx-auto my-12 h-[70vh] rounded-md">
      <div className="flex justify-between mt-5 px-8">
          <h4 className="text-2xl font-bold mt-6">Danh sách đơn hàng</h4>
      </div>

      <div className="flex justify-between mt-5 mb-2 px-8">
        <input
            type="text"
            placeholder="Tìm kiếm đơn hàng"
            value={searchOrderId}
            onChange={handleSearchChange}
            className="w-full max-w-[800px] py-2 px-4 border border-b-slate-900 rounded-md focus:ring-indigo-500"
        />

        <div className="ml-8 min-w-[250px]">
          <select
              value={filterStatus}
              onChange={handleStatusChange}
              className="w-full py-2 px-4 border rounded-md"
          >
              <option value="ALL">Tất cả</option>
              <option value="PROCESSING">Chờ duyệt</option>
              <option value="ACCEPTED">Đã chấp nhận</option>
              <option value="CANCEL">Đã bị hủy</option>
              <option value="DELIVERY">Đang vận chuyển</option>
              <option value="SUCCESS">Thành công</option>
              <option value="WAITING PAYMENT">Chờ thanh toán</option>
          </select>
        </div>
      </div>

      <div className="px-8">
      {/* DataGrid */}
        <DataGrid
          rows={rows}
          columns={columns}
          initialState={{
            pagination: {
              paginationModel: {
                pageSize: 10,
              },
            },
          }}
          pageSizeOptions={[5]}
          checkboxSelection
          disableRowSelectionOnClick
        />
      </div>
     
      <Dialog open={openDialog} onClose={handleCloseDialog} fullWidth maxWidth="md">
        <DialogTitle className="font-bold text-xl">Chi tiết đơn hàng</DialogTitle>
        <DialogContent>
          {selectedOrder && (
            <>
              <p>
                <strong>Mã đơn hàng: </strong>
                <span className="text-blue-500">{selectedOrder.id}</span>
              </p>
              <p>
                <strong>Tổng tiền: </strong>
                <span className="text-blue-500">{selectedOrder.totalPrice} VNĐ</span>
              </p>
              <p>
                <strong>Phương thức thanh toán: </strong>
                <span className="text-blue-500">{selectedOrder.paymentMethod}</span>
              </p>
              <p>
                <strong>Trạng thái đơn hàng: </strong>
                <span className="text-blue-500">{selectedOrder.orderStatus}</span>
              </p>
              <p>
                <strong>Ngày đặt hàng: </strong>
                <span className="text-blue-500">{selectedOrder.orderDate}</span>
              </p>

              <div>
                <strong>Chi tiết sản phẩm:</strong>
                <ul>
                  {selectedOrder.orderItems.map((item, index) => (
                    <div key={index} className="flex border-2 border-gray-300 p-4 mb-4 bg-green-100">
                      <div className="flex-1 m-4">
                        <p><strong>Sản phẩm {index + 1}:</strong></p>
                        <p><strong>Tên sản phẩm: </strong><span className="text-green-600">{item.productItemName}</span></p>
                        <p><strong>Giá tiền: </strong><span className="text-green-600">{item.price} VNĐ</span></p>
                        <p><strong>Số lượng: </strong><span className="text-green-600">{item.quantity}</span></p>
                      </div>
                      <div className="flex-1">
                        <img src={item.productItemUrl} alt={item.productItemName} className="max-w-full max-h-40 mt-2 rounded border-2 border-gray-300" />
                      </div>
                    </div>
                  ))}
                </ul>
              </div>
            </>
          )}
           {isOpen && (
        <div
          className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
          onClick={handleClose}
        >
          <div
            className="bg-white p-4 rounded shadow-lg relative"
            onClick={(e) => e.stopPropagation()} // Ngăn chặn đóng khi nhấn vào nội dung dialog
          >
            <button
              className="absolute top-2 right-2 text-gray-500 hover:text-black"
              onClick={handleClose}
            >
              &#x2715;
            </button>
            <img
              src={selectedOrder.note}
              alt="Hình ảnh giao hàng"
              className="max-w-full max-h-[60vh] rounded"
            />
          </div>
        </div>
      )}
        </DialogContent>
        <DialogActions>
            {selectedOrder && selectedOrder.orderStatus === "SUCCESS" && (
                    <button onClick={handleOpen}
                        className="py-2 px-4 bg-green-400 text-white rounded hover:bg-green-700"
                    >
                        Xem hình ảnh giao hàng
                    </button>
                )}
            {selectedOrder && selectedOrder.orderStatus === "REVIEWED" && (
                    <button onClick={handleOpen}
                        className="py-2 px-4 bg-green-400 text-white rounded hover:bg-green-700"
                    >
                        Xem hình ảnh giao hàng
                    </button>
                )}
          <button onClick={handleCloseDialog} className="bg-blue-500 text-white px-4 py-2 rounded">Đóng</button>
          {selectedOrder && (selectedOrder.orderStatus === 'PROCESSING' || selectedOrder.orderStatus === 'WAITING PAYMENT') && (
            <>
              <button onClick={() => openConfirmDialog('cancel')} className="bg-red-500 text-white px-4 py-2 rounded">Hủy đơn hàng</button>
              <button onClick={() => openConfirmDialog('accept')} className="bg-blue-500 text-white px-4 py-2 rounded">Chấp nhận đơn hàng</button>
            </>
          )}
          {selectedOrder && (selectedOrder.orderStatus === 'ACCEPTED' || selectedOrder.orderStatus === 'PAID') && (
            <button onClick={() => openConfirmDialog('delivery')} className="bg-orange-500 text-white px-4 py-2 rounded">Đi tới vận chuyển</button>
          )}
          {selectedOrder && (selectedOrder.orderStatus === 'DELIVERY') && (
            <button onClick={() => openConfirmDialog('delivery')} className="bg-green-500 text-white px-4 py-2 rounded">Đã giao hàng</button>
          )}
          
        </DialogActions>
      </Dialog>
      
      <Dialog open={confirmDialogOpen} onClose={handleConfirmClose}>
        <DialogTitle>Xác nhận</DialogTitle>
        <DialogContent>
          {confirmDialogType === 'accept' ? 'Bạn có chắc chắn muốn chấp nhận đơn hàng này?' : confirmDialogType === 'delivery' ? "Xác nhận bàn giao cho đơn vị vận chuyển?" : 'cancel' ? "Bạn có chắc chắn muốn hủy đơn hàng này?" : ""}
        </DialogContent>
        <DialogActions>
          <button onClick={handleConfirmClose} className="bg-gray-500 text-white px-4 py-2 rounded">Đóng</button>
          <button onClick={handleUpdateStatus} className="bg-blue-500 text-white px-4 py-2 rounded">Xác nhận</button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

export default HistoryOrders;
