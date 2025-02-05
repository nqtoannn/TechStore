import React, { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import { DataGrid } from '@mui/x-data-grid';
import { Button, Dialog, DialogTitle, DialogContent, DialogActions, Typography, TextField, MenuItem, CircularProgress } from '@mui/material';
import { IconButton } from '@mui/material';
import VisibilityIcon from '@mui/icons-material/Visibility';
import { useNavigate } from "react-router-dom";
import axios from 'axios';
import { getToken } from '../utils/getToken';


const CustomerManagement = () => {
    const navigate = useNavigate();
    const [customerList, setCustomerList] = useState([]);
    const [selectedCustomer, setSelectedCustomer] = useState(null);
    const [openDetail, setOpenDetail] = useState(false);
    const [filteredCustomerList, setFilteredCustomerList] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchKeyword, setSearchKeyword] = useState(''); // State to hold search keyword
    const [filterStatus, setFilterStatus] = useState('all'); // State to hold filter status // State to hold the image preview URL
    const token = getToken();
    const fetchCustomers = async () => {
        try {
            setLoading(true); // Show loading indicator
            const response = await fetch('http://localhost:8080/techstore/api/management/customer/findAll', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            if (!response.ok) {
                throw new Error('Failed to fetch Customer data');
            }
            const data = await response.json();
            if (Array.isArray(data.data)) {
                setCustomerList(data.data);
            } else {
                throw new Error('Data format is incorrect');
            }
        } catch (error) {
            console.error('Error fetching Customer data:', error);
        } finally {
            setLoading(false); // Hide loading indicator
        }
    };

    useEffect(() => {
        fetchCustomers();
    }, []);

    const handleButtonClick = (Customer) => {
        setSelectedCustomer(Customer);
        setOpenDetail(true);
    };

    const handleDetailClose = () => {
        setOpenDetail(false);
        setSelectedCustomer(null);
    };

    const handleSearchChange = (e) => {
        setSearchKeyword(e.target.value);
        // Filter Customers based on search keyword
        filterCustomers(e.target.value, filterStatus);
    };

    // Function to handle filter selection change
    const handleFilterChange = (e) => {
        setFilterStatus(e.target.value);
        // Filter Customers based on filter status
        filterCustomers(searchKeyword, e.target.value);
    };

    // Function to filter Customers based on search keyword and filter status
    const filterCustomers = (keyword, status) => {
        const filtered = customerList.filter(customer => {
            const matchesKeyword = customer.fullName.toLowerCase().includes(keyword.toLowerCase());
            const matchesStatus = status === '' || status === 'all' ? true : customer.status === status;
            return matchesKeyword && matchesStatus;
        });
        setFilteredCustomerList(filtered);
    };

    let headerName;
    if (filterStatus === 'ACTIVE') {
        headerName = 'Hành động';
    } else if (filterStatus === 'all') {
        headerName = 'Trạng thái';
    } else {
        headerName = 'Hành động';
    }




    const columns = [
        { field: 'id', headerName: 'ID', width: 90 },
        {
            field: 'fullName',
            headerName: 'Tên khách hàng',
            width: 150,
            editable: false,
        },
        {
            field: 'email',
            headerName: 'Email',
            width: 150,
            editable: false,
        },
        {
            field: 'address',
            headerName: 'Địa chỉ',
            width: 250,
            editable: false,
        },
        {
            field: 'phoneNumber',
            headerName: 'Số điện thoại',
            width: 150,
            editable: false,
        },
        {
            field: 'actions',
            headerName: 'Chi tiết',
            width: 150,
            renderCell: (params) => (
                <IconButton onClick={() => handleButtonClick(params.row)}>
                    <VisibilityIcon color="info" />
                </IconButton>
            ),
        },
        {
            field: 'actions2',
            headerName: headerName,
            width: 200,
            renderCell: (params) => {
                if (filterStatus === 'all') {
                    return (
                        <div>
                            {params.row.status === 'ACTIVE' ? (
                                <div style={{ fontSize: '16px', color: 'green', fontWeight: 'bold',  borderRadius: '5px', display: 'inline-block' }}>
                                    Đang hoạt động
                                </div>
                            ) : params.row.status === 'NOT_ACTIVE' ? (
                                <div style={{ fontSize: '16px', color: 'orange', fontWeight: 'bold', borderRadius: '5px', display: 'inline-block' }}>
                                    Đã nghỉ
                                </div>
                            ) : (
                                params.row.status 
                            )}
                        </div>
                    );
                }
            }
        }

    ];

    const dataToRender = (searchKeyword.trim() === '' && filterStatus === 'all') ? customerList : filteredCustomerList;
    console.log("data to render: ", dataToRender)

    return (
        <>
            {loading && ( 
                <Box
                    sx={{
                        height: 600,
                        width: '100%',
                        background: "#fff",
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        justifyContent: 'center'
                    }}
                >
                    <CircularProgress /> {/* Display CircularProgress while loading */}
                </Box>
            )}
            {!loading && (
                <div className='max-w-7xl bg-gray-200 mx-auto my-12 h-[70vh] rounded-md'
                >
                    <div className="flex justify-between mt-5 px-8 w-full">
                        <h4 className="text-2xl font-bold my-6">Danh sách khách hàng</h4>
                    </div>
                    <div className="flex justify-between  mb-2 pr-8">
                        <div className="flex items-center px-8">
                            <TextField
                                label="Tìm kiếm khách hàng"
                                variant="outlined"
                                size="small"
                                value={searchKeyword}
                                onChange={handleSearchChange}
                                className="mx-2 w-96"
                            />
                        </div>
                        <TextField
                            select
                            label="Lọc nhân viên"
                            variant="outlined"
                            size="small"
                            value={filterStatus}
                            onChange={handleFilterChange}
                            className="w-64"
                        >
                            <MenuItem value="all">Tất cả</MenuItem>
                            <MenuItem value="ACTIVE">Đang hoạt động</MenuItem>
                            <MenuItem value="DEACTIVE">Đã nghỉ</MenuItem>
                        </TextField>
                    </div>
                    <div className='px-8'>
                        <DataGrid
                            className='dataGrid'
                            rows={dataToRender}
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

                    <Dialog open={openDetail} onClose={handleDetailClose} PaperProps={{
                        style: {
                            width: '65%',
                            maxWidth: 'none',
                        },
                    }} >
                        <DialogTitle>Chi tiết thông tin</DialogTitle>
                        <DialogContent>
                            {selectedCustomer && (
                                <>
                                    <Typography variant="h6"> <strong>Họ và tên: {selectedCustomer.fullName}</strong></Typography>
                                    <Typography variant="body1"><strong>Email</strong> {selectedCustomer.email}</Typography>
                                    <Typography variant="body1"><strong>Số điện thoại: </strong> {selectedCustomer.phoneNumber}</Typography>
                                    <Typography variant="body1"><strong>Địa chỉ: </strong> {selectedCustomer.address}</Typography>
                                    <Typography variant="body1"><strong>Trạng thái: </strong> {selectedCustomer.status === 'ACTIVE' ? (
                                <div style={{ fontSize: '16px', color: 'green', fontWeight: 'bold', padding: '5px', borderRadius: '5px', display: 'inline-block' }}>
                                    Đang hoạt động
                                </div>
                            ) : selectedCustomer.status === 'NOT_ACTIVE' ? (
                                <div style={{ fontSize: '16px', color: 'orange', fontWeight: 'bold', padding: '5px', borderRadius: '5px', display: 'inline-block' }}>
                                    Đã nghỉ
                                </div>
                            ) : (
                                selectedCustomer.status 
                            )}</Typography>
                                    <Typography variant="body1"><strong>Ngày tham gia: </strong> {selectedCustomer.createAt} </Typography>
                                </>
                            )}
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={handleDetailClose} color="primary">Đóng</Button>
                        </DialogActions>
                    </Dialog>
                </div>
            )}
        </>
    );
}

export default CustomerManagement;
