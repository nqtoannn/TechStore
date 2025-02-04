import React, { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import { DataGrid } from '@mui/x-data-grid';
import { Button, Dialog, DialogTitle, DialogContent, DialogActions, Typography, TextField, MenuItem, CircularProgress } from '@mui/material';
import { IconButton } from '@mui/material';
import ToggleOnIcon from '@mui/icons-material/ToggleOn';
import ToggleOffIcon from '@mui/icons-material/ToggleOff';
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import VisibilityIcon from '@mui/icons-material/Visibility';
import axios from 'axios';
import { getToken } from '../utils/getToken';


const EmployeeManagement = () => {
    const [employeeList, setEmployeeList] = useState([]);
    const [selectedEmployee, setSelectedEmployee] = useState(null);
    const [openDetail, setOpenDetail] = useState(false);
    const [openAdd, setOpenAdd] = useState(false);
    const [filterEmployeeList, setFilterEmployeeList] = useState([]);
    const [openConfirm, setOpenConfirm] = useState(false);
    const [loading, setLoading] = useState(true);
    const [statusValue, setStatusValue] = useState("");
    const [newEmployee, setNewEmployee] = useState({
        fullName: '',
        email: '',
        address: '',
        phoneNumber: ''

    });
    const [imageFile, setImageFile] = useState(null); // State to hold the image file
    const [imagePreviewUrl, setImagePreviewUrl] = useState('');
    const [searchKeyword, setSearchKeyword] = useState(''); // State to hold search keyword
    const [filterStatus, setFilterStatus] = useState('all'); // State to hold filter status // State to hold the image preview URL
    const token = getToken();
    const fetchEmployees = async () => {
        try {
            console.log("Tai lai");
            setLoading(true); // Hiển thị chỉ báo loading
            const response = await axios.get('http://localhost:8080/techstore/api/management/employee/findAll', {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            if (response.status !== 200) {
                throw new Error('Failed to fetch Employee data');
            }

            const data = response.data.data;
            if (Array.isArray(data)) {
                setEmployeeList(data.reverse());
            } else {
                throw new Error('Data format is incorrect');
            }
        } catch (error) {
            console.error('Error fetching Employee data:', error);
        } finally {
            setLoading(false); 
        }
    };



    useEffect(() => {
        fetchEmployees();
    }, []);

    const handleButtonClick = (Employee) => {
        setSelectedEmployee(Employee);
        setOpenDetail(true);
    };

    const handleDetailClose = () => {
        setOpenDetail(false);
        setSelectedEmployee(null);
    };

    const handleAddOpen = () => {
        setOpenAdd(true);
    };

    const handleAddClose = () => {
        setOpenAdd(false);
        setNewEmployee({
            fullName: '',
            email: '',
            address: '',
            phoneNumber: ''
        });
        setImageFile(null); // Reset the image file
        setImagePreviewUrl(''); // Reset the image preview URL
    };

    const handleAddChange = (e) => {
        const { name, value } = e.target;
        setNewEmployee((prev) => ({ ...prev, [name]: value }));
    };

    const handleConfirmOpen = (Employee) => {
        setSelectedEmployee(Employee);
        Employee.status === 'ACTIVE' ? setStatusValue("ngưng hoạt động") : setStatusValue(" tiếp tục hoạt động")
        setOpenConfirm(true);
    };

    const handleConfirmClose = () => {
        setOpenConfirm(false);
    };

    const handleUpdateStatus = async () => {
        try {
            var status = selectedEmployee.status;
            if (status === 'ACTIVE') {
                status = 'DEACTIVE'
            }
            else status = 'ACTIVE';
            console.log("status employee: ", status)
            console.log("id employee: ", selectedEmployee.id)
            console.log(JSON.stringify({
                id: selectedEmployee.id,
                status: status
            }));
            const response = await axios.put(
                'http://localhost:8080/techstore/api/management/employee/updateStatusUser',
                {
                    id: selectedEmployee.id,
                    status: status
                },
                {
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${token}`
                    }
                }
            );


            if (response.status !== 200) {
                throw new Error('Failed to update Employee status');
            }
            toast.success('Cập nhật trạng thái nhân viên thành công', {
                toastStyle: {
                    // Tùy chỉnh style của thông báo
                    background: "#4CAF50", // Màu nền
                    color: "white", // Màu chữ
                    border: "1px solid #388E3C", // Viền
                    borderRadius: "8px", // Bo tròn viền
                    boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)", // Bóng đổ
                }
            });
            handleConfirmClose();
            handleDetailClose();
            setFilterStatus("all");
            fetchEmployees();
        } catch (error) {
            console.error('Error updating Employee status:', error);
        } finally {
            setLoading(false); // Hide loading indicator
        }
    };

    const handleAddSubmit = async () => {
        try {
            setLoading(true); // Show loading indicator
            // Add the new Employee
            const response = await axios.post(
                'http://localhost:8080/techstore/api/management/addEmployee',
                newEmployee,
                {
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${token}`
                    }
                }
            );
            console.log("new Employee: ", newEmployee);
            if (response.status !== 200) {
                throw new Error('Failed to add Employee');
            }

            // Get the added Employee ID from the response
            toast.success('Thêm tài khoản nhân viên thành công', {
                toastStyle: {
                    // Tùy chỉnh style của thông báo
                    background: "#4CAF50", // Màu nền
                    color: "white", // Màu chữ
                    border: "1px solid #388E3C", // Viền
                    borderRadius: "8px", // Bo tròn viền
                    boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)", // Bóng đổ
                }
            });
            handleAddClose();
            fetchEmployees(); // Re-fetch the Employee list
        } catch (error) {
            console.error('Error adding Employee', error);
        } finally {
            setLoading(false); // Hide loading indicator
        }
    };

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setImagePreviewUrl(reader.result); // Set the preview URL
            };
            reader.readAsDataURL(file);
            setImageFile(file); // Save the file to the state
        }
    };

    const handleSearchChange = (e) => {
        setSearchKeyword(e.target.value);
        filterEmployees(e.target.value, filterStatus);
    };

    const handleFilterChange = (e) => {
        setFilterStatus(e.target.value);
        filterEmployees(searchKeyword, e.target.value);
    };
    const filterEmployees = (keyword, status) => {
        const filtered = employeeList.filter(employee => {
            const matchesKeyword = employee.fullName.toLowerCase().includes(keyword.toLowerCase());
            const matchesStatus = status === '' || status === 'all' ? true : employee.status === status;
            return matchesKeyword && matchesStatus;
        });
        setFilterEmployeeList(filtered);
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
        { field: 'id', headerName: 'ID', width: 100 },
        {
            field: 'fullName',
            headerName: 'Tên nhân viên',
            width: 250,
            editable: false,
        },
        {
            field: 'email',
            headerName: 'Email',
            width: 250,
            editable: false,
        },
        
        {
            field: 'phoneNumber',
            headerName: 'Số điện thoại',
            width: 160,
            editable: false,
        },
        {
            field: 'details',
            headerName: 'Chi tiết',
            description: 'This column has a value getter and is not sortable.',
            sortable: false,
            width: 200,
            valueGetter: (value, row) => `${row.firstName || ''} ${row.lastName || ''}`,
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
        {
            field: 'actions2',
            headerName: headerName,
            width: 200,
            renderCell: (params) => {
                if (filterStatus === 'all') {
                    return (
                        <div>
                            {params.row.status === 'ACTIVE' ? (
                                <div style={{ fontSize: '16px', color: 'green', fontWeight: 'bold', padding: '5px', borderRadius: '5px', display: 'inline-block' }}>
                                    Đang hoạt động
                                </div>
                            ) : params.row.status === 'DEACTIVE' ? (
                                <div style={{ fontSize: '16px', color: 'orange', fontWeight: 'bold', padding: '5px', borderRadius: '5px', display: 'inline-block' }}>
                                    Đã nghỉ
                                </div>
                            ) : (
                                params.row.status // Nếu không phù hợp với bất kỳ điều kiện nào, hiển thị giá trị status gốc
                            )}
                        </div>
                    );
                } else {
                    return (
                        <IconButton onClick={() => handleConfirmOpen(params.row)}>
                            {filterStatus === 'ACTIVE' ? (
                                <ToggleOnIcon color="success" style={{ fontSize: '50px' }} />
                            ) : (
                                <ToggleOffIcon color="warning" style={{ fontSize: '50px' }} />
                            )}
                        </IconButton>
                    );
                }
            },
        }

    ];

    const dataToRender = (searchKeyword.trim() === '' && filterStatus === 'all') ? employeeList : filterEmployeeList;
    console.log("data to render: ", dataToRender)
    return (
        <>
            {loading && (
    <div className="h-[600px] w-full bg-white flex flex-col items-center justify-center">
        <CircularProgress /> 
    </div>
)}
{!loading && (
    <div className=" max-w-7xl bg-gray-200 mx-auto my-12 h-[70vh] rounded-md">
        <div className="flex justify-between mt-5 px-8 w-full">
            <h4 className="text-2xl font-bold my-6">Quản lý nhân viên</h4>
        </div>
        <div className="flex justify-between  mb-2 pr-8">
            <div className="flex items-center">
                <button 
                    className="bg-orange-500 text-white text-base px-4 py-2 mx-8 rounded hover:bg-orange-600 transition duration-200 mr-2"
                    onClick={handleAddOpen}
                >
                    Thêm tài khoản nhân viên
                </button>
                <TextField
                    label="Tìm kiếm nhân viên"
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
        <div className="px-8">
            <DataGrid
                className="dataGrid"
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

        <Dialog open={openDetail} onClose={handleDetailClose} PaperProps={{ style: { width: '65%', maxWidth: 'none' } }}>
            <DialogTitle>Chi tiết thông tin</DialogTitle>
            <DialogContent>
                {selectedEmployee && (
                    <>
                        <h6 className="font-bold">Họ và tên: {selectedEmployee.fullName}</h6>
                        <p className="font-medium">Email: {selectedEmployee.email}</p>
                        <p className="font-medium">Số điện thoại: {selectedEmployee.phoneNumber}</p>
                        <Typography variant="body1"><strong>Trạng thái: </strong> {selectedEmployee.status === 'ACTIVE' ? (
                                <div style={{ fontSize: '16px', color: 'green', fontWeight: 'bold', padding: '5px', borderRadius: '5px', display: 'inline-block' }}>
                                    Đang hoạt động
                                </div>
                            ) : selectedEmployee.status === 'NOT_ACTIVE' ? (
                                <div style={{ fontSize: '16px', color: 'orange', fontWeight: 'bold', padding: '5px', borderRadius: '5px', display: 'inline-block' }}>
                                    Đã nghỉ
                                </div>
                            ) : (
                                selectedEmployee.status 
                            )}</Typography>
                        <button 
                            className="bg-red-500 text-white px-4 py-2 rounded mt-5 mx-auto block"
                            onClick={() => handleConfirmOpen(selectedEmployee)}
                        >
                            {selectedEmployee.status === 'ACTIVE' ? 'Khóa tài khoản' : 'Mở khóa tài khoản'}
                        </button>
                    </>
                )}
            </DialogContent>
            <DialogActions>
                <Button onClick={handleDetailClose} color="primary">Close</Button>
            </DialogActions>
        </Dialog>

        <Dialog open={openConfirm} onClose={handleConfirmClose}>
            <DialogTitle>Xác nhận</DialogTitle>
            <DialogContent>
                <p>Bạn có chắc chắn muốn {statusValue} không?</p>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleConfirmClose} color="primary">Hủy</Button>
                <Button onClick={handleUpdateStatus} color="primary">Xác nhận</Button>
            </DialogActions>
        </Dialog>

        <Dialog open={openAdd} onClose={handleAddClose}>
            <DialogTitle>Thêm tài khoản mới</DialogTitle>
            <DialogContent>
                <TextField
                    margin="dense"
                    name="fullName"
                    label="Tên nhân viên"
                    type="text"
                    fullWidth
                    value={newEmployee.fullName}
                    onChange={handleAddChange}
                />
                <TextField
                    margin="dense"
                    name="email"
                    label="Email"
                    type="text"
                    fullWidth
                    value={newEmployee.email}
                    onChange={handleAddChange}
                />
                <TextField
                    margin="dense"
                    name="phoneNumber"
                    label="Số điện thoại"
                    type="number"
                    fullWidth
                    value={newEmployee.phoneNumber}
                    onChange={handleAddChange}
                />
                <TextField
                    margin="dense"
                    name="address"
                    label="Địa chỉ"
                    type="text"
                    fullWidth
                    value={newEmployee.address}
                    onChange={handleAddChange}
                />
                <input
                    accept="image/*"
                    className="hidden"
                    id="raised-button-file"
                    type="file"
                    onChange={handleImageChange}
                />
                {imagePreviewUrl && (
                    <img src={imagePreviewUrl} alt="Employee Preview" className="w-full mt-2" />
                )}
            </DialogContent>
            <DialogActions>
                <Button onClick={handleAddClose} color="primary">Hủy</Button>
                <Button onClick={handleAddSubmit} color="primary" className="mb-2">Thêm tài khoản</Button>
            </DialogActions>
        </Dialog>
    </div>
)}

        </>
    );
}

export default EmployeeManagement;
