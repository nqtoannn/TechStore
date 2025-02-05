import React, { useState, useEffect } from 'react';
import { DataGrid } from '@mui/x-data-grid';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button} from '@mui/material';
import axios from 'axios';
import { getToken } from '../utils/getToken';
import ReviewDialog from './ReviewDialog';


const ListOrder = () => {
    const [selectedRow, setSelectedRow] = useState(null);
    const [openDialog, setOpenDialog] = useState(false);
    const [rows, setRows] = useState([]);
    const [filterStatus, setFilterStatus] = useState('ALL');
    const [searchOrderId, setSearchOrderId] = useState('');
    const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
    const [confirmDialogAction, setConfirmDialogAction] = useState(null);
    const [reviewDialogOpen, setReviewDialogOpen] = useState(false);
    const [currentProductItemId, setCurrentProductItemId] = useState(null);    
    const userId = localStorage.getItem('userId');
    const token = getToken();
    const [isOpen, setIsOpen] = useState(false);

  const handleOpen = () => setIsOpen(true);
  const handleClose = () => setIsOpen(false);


    useEffect(() => {
        fecthOrderByCusomerId();
    }, [filterStatus, searchOrderId]);

    const fecthOrderByCusomerId = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/techstore/api/customer/orders/getAllOrdersByCustomerId/${userId}`, {
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
    const handleOpenReviewDialog = (productItemId) => {
        setCurrentProductItemId(productItemId);
        setReviewDialogOpen(true);
    };
    

    const handleStatusChange = (event) => {
        setFilterStatus(event.target.value);
    };

    const handleSearchChange = (event) => {
        setSearchOrderId(event.target.value);
    };

    const handleButtonClick = (rowData) => {
        setSelectedRow(rowData);
        setOpenDialog(true);
    };

    const handleCloseDialog = () => {
        setOpenDialog(false);
    };

    const handleUpdateStatusReject = () => {
        setConfirmDialogAction('reject');
        setConfirmDialogOpen(true);
    };

    const handleUpdateStatusSuccess = () => {
        setConfirmDialogAction('success');
        setConfirmDialogOpen(true);
    };

    const handleConfirmDialog = async () => {
        let updateData = null;

        if (confirmDialogAction === 'reject') {
            updateData = {
                orderId: selectedRow.id,
                statusCode: 8,
            };
        } else if (confirmDialogAction === 'success') {
            updateData = {
                orderId: selectedRow.id,
                statusCode: 4,
            };
        }

        if (updateData) {
            try {
                await axios.put('http://localhost:8080/techstore/api/customer/updateOrderStatus', updateData, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                fecthOrderByCusomerId();
                handleCloseDialog();
            } catch (error) {
                console.error('Error updating status:', error);
            }
        }

        setConfirmDialogOpen(false);
    };

    const ConfirmationDialog = ({ open, onClose, onConfirm, title, content }) => (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>{title}</DialogTitle>
            <DialogContent>
                <p>{content}</p>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} color="primary" variant='outlined'>
                    Hủy
                </Button>
                <Button onClick={onConfirm} style={{
                                        color: '#fff',
                                        marginRight: '15px',
                                        fontSize: '16px',
                                        backgroundColor: '#ff5722',
                                        '&:hover': {
                                            backgroundColor: '#f4511e',
                                        }
                                    }} variant='contained'>
                    Xác nhận
                </Button>
            </DialogActions>
        </Dialog>
    );


    const columns = [
        { 
            field: 'id', 
            headerName: 'Mã Đơn hàng', 
            width: 120 ,
            type: 'number',
        },
        {
            field: 'address',
            headerName: 'Địa chỉ giao hàng',
            width: 350,
            editable: false,
        },
        {
            field: 'orderDate',
            headerName: 'Ngày đặt',
            width: 100,
            editable: false,
        },
        {
            field: 'totalPrice',
            headerName: 'Tổng tiền (VNĐ)',
            width: 160,
            editable: false,
            type: 'number',
        },
        {
            field: 'paymentMethod',
            headerName: 'Thanh toán',
            width: 160,
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
            field: 'details',
            headerName: '',
            width: 120,
            renderCell: (params) => (
                <button  className="bg-green-500 text-white font-bold  px-6  rounded hover:bg-green-800 h-12"  onClick={() => handleButtonClick(params.row)}>Chi tiết</button>
            ),
        },
    ];

    return (
        <div className=" max-w-7xl bg-gray-200 mx-auto my-12 min-h-[70vh] rounded-md">
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
                        <option value="PROCESSING">Chờ xác nhận</option>
                        <option value="ACCEPTED">Đã chấp nhận</option>
                        <option value="CANCEL">Đã bị hủy</option>
                        <option value="DELIVERY">Đang vận chuyển</option>
                        <option value="SUCCESS">Thành công</option>
                        <option value="WAITING PAYMENT">Chờ thanh toán</option>
                    </select>
                </div>
            </div>

            <div className="px-8">
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
                    // rowHeight={100}
                />
            </div>

            <Dialog open={openDialog} className="flex justify-center" onClose={handleCloseDialog} >
                <DialogTitle className="font-extrabold text-2xl mt-2">Chi tiết đơn hàng</DialogTitle>
                <DialogContent>
                    {selectedRow && (
                        <div className="mt-2">
                            <p className="mb-4">
                                <span className="font-bold">Mã đơn hàng: </span>
                                <span className="text-green-600">{selectedRow.id}</span>
                            </p>
                            <p className="mb-4">
                                <span className="font-bold">Sản phẩm: </span>
                                <span className="text-green-600">{selectedRow.orderItems.map(item => item.productItemName).join(', ')}</span>
                            </p>
                            <p className="mb-4">
                                <span className="font-bold">Tổng tiền: </span>
                                <span className="text-green-600">{selectedRow.totalPrice.toLocaleString()} VNĐ</span>
                            </p>
                            <p className="mb-4">
                                <span className="font-bold">Phương thức thanh toán: </span>
                                <span className="text-green-600">{selectedRow.paymentMethod}</span>
                            </p>
                            <p className="mb-4">
                                <span className="font-bold">Trạng thái đơn hàng: </span>
                                <span className="text-green-600">{selectedRow.orderStatus}</span>
                            </p>
                            <p className="mb-4">
                                <span className="font-bold">Ngày đặt hàng: </span>
                                <span className="text-green-600">{selectedRow.orderDate}</span>
                            </p>
                            <p className="mb-4 font-bold">Các sản phẩm:</p>
                            {selectedRow.orderItems.map((item, index) => (
                                <div key={index} className="flex border-2 rounded-md border-gray-400 p-4 mb-4 bg-gray-200">
                                    <div className="flex-3 mr-20 ">
                                        <p className="mb-4 font-bold">Sản phẩm {index + 1}</p>
                                        <p className="mb-4">
                                            <span className="font-semibold">Tên sản phẩm: </span>
                                            <span className="text-green-600">{item.productItemName}</span>
                                        </p>
                                        <p className="mb-4">
                                            <p className="mb-4 font-semibold">Giá tiền: <span className="text-green-600">{item.price.toLocaleString()} VNĐ</span> </p>
                                        </p>
                                        <p className="mb-4">
                                            <span className="font-semibold">Số lượng: </span>
                                            <span className="text-green-600">{item.quantity}</span>
                                        </p>
                                    </div>
                                    <div className="flex-1 px-10">
                                        <img
                                            src={item.productItemUrl}
                                            alt={item.productItemUrl}
                                            className="max-w-full max-h-[150px] mt-2 rounded-md border-2 border-gray-400"
                                        />
                                        {selectedRow && selectedRow.orderStatus === "SUCCESS" && (
                                            <button
                                                onClick={() => handleOpenReviewDialog(item.productItemId)}
                                                className="mt-2 py-1 px-3 bg-orange-600 text-white rounded hover:bg-orange-500"
                                            >
                                                Đánh giá
                                            </button>
                                        )}
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                    {isOpen && (
                        <div
                        className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
                        onClick={handleClose}
                        >
                        <div
                            className="bg-white p-4 rounded shadow-lg relative"
                            onClick={(e) => e.stopPropagation()} 
                        >
                            <button
                            className="absolute top-2 right-2 text-gray-500 hover:text-black"
                            onClick={handleClose}
                            >
                            &#x2715;
                            </button>
                            <img
                            src={selectedRow.note}
                            alt="Hình ảnh giao hàng"
                            className="max-w-full max-h-[60vh] rounded"
                            />
                        </div>
                        </div>
                    )}
                </DialogContent>
                <DialogActions>
                    {selectedRow && selectedRow.orderStatus === "SUCCESS" && (
                        <button
                            onClick={handleOpen}
                            className="py-2 px-4 bg-green-400 text-white rounded hover:bg-green-700"
                        >
                            Xem hình ảnh giao hàng
                        </button>
                    )}
                     {selectedRow && selectedRow.orderStatus === "REVIEWED" && (
                    <button onClick={handleOpen}
                        className="py-2 px-4 bg-green-400 text-white rounded hover:bg-green-700"
                    >
                        Xem hình ảnh giao hàng
                    </button>
                    )}

                    {selectedRow && selectedRow.orderStatus === "DELIVERY" && (
                        <button
                            onClick={handleUpdateStatusSuccess}
                            className="py-2 px-4 bg-green-400 text-white rounded hover:bg-green-700"
                        >
                            Đã nhận hàng
                        </button>
                    )}
                    {selectedRow && selectedRow.orderStatus === "PROCESSING" && (
                        <button
                            onClick={handleUpdateStatusReject}
                            className="py-2 px-4 bg-gray-600 text-white rounded hover:bg-gray-400"
                        >
                            Hủy đơn hàng
                        </button>
                    )}  
                    <button onClick={handleCloseDialog} className="py-2 px-4 bg-gray-600 text-white rounded hover:bg-gray-400">
                        Đóng
                    </button>
                </DialogActions>
            </Dialog>

            <ReviewDialog
                open={reviewDialogOpen}
                onClose={() => setReviewDialogOpen(false)}
                productItemId={currentProductItemId}
            />

            <ConfirmationDialog
                open={confirmDialogOpen}
                onClose={() => setConfirmDialogOpen(false)}
                onConfirm={handleConfirmDialog}
                title="Xác nhận hành động"
                content={confirmDialogAction === 'reject' ? 'Bạn có chắc chắn muốn hủy đơn hàng này?' : 'Bạn có chắc chắn đã nhận được hàng?'}
            />
        </div>

    );
};

export default ListOrder;
