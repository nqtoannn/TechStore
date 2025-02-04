import React from 'react';
import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import HomeLayout from './pages/HomeLayout';
import OauthGg from './features/auth/OauthGg';
import Login from './features/auth/Login';
import Signup from './components/Signup';
import ProductManagement from './components/ProductManagement';
import Home from './components/Home';
import Cart from './components/Cart';
import ProductList from './components/ProductList';
import Product from './components/Product';
import CheckOut from './components/CheckOut';
import Revenue from './components/Revenue';
import ListOrder from './components/ListOrder';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'; 
import EmployeeManagement from './components/EmployeeManagement';
import CustomerManagement from './components/CustomerManagement';
import PersonalInfo from './components/PersonalInfor';
import HistoryOrders from './components/HistoryOrders';
import PaymentSuccess from './components/PaymentSuccess';
import Forbidden from './components/Forbidden';
import ProtectedRoute from './components/ProtectedRoute';
import RePassword from './components/RePassword';
import ForgotPassword from './components/ForgotPassword';
import PromotionManagement from './components/PromotionManagement';
import AddProduct from './components/AddProduct';
import NewsFeed from './components/NewFeeds';

const router = createBrowserRouter([
  {
    path: "/",
    element: <HomeLayout />,
    children: [
      { 
        path: "login", 
        element: <Login /> },
      { 
          path: "oauth", 
          element: < OauthGg/> },  
      { 
        path: "signup", 
        element: <Signup /> },
      { 
        path: "403", 
        element: <Forbidden /> },
      { 
        path: "product/:productId", 
        element: <Product /> },
      { 
        path: "blog", 
        element: <NewsFeed /> },
      { 
        path: "productList", 
        element: <ProductList /> },
      { 
          path: "productList?search=:query", 
          element: <ProductList /> },
      {
        path: "auth/repassword/:userId",
        element:  <RePassword />,
      },
      {
        path: "forgot-password",
        element: < ForgotPassword />,
      },
      {
        path: "user-profile",
        element: <ProtectedRoute allowedRoles={['CUSTOMER', 'ADMIN', 'EMPLOYEE']} element={<PersonalInfo />} />
      },  
      {
        path: "payment-success",
        element: <ProtectedRoute allowedRoles={['CUSTOMER']} element={<PaymentSuccess />} />
      },
      {
        path: "shop",
        element:  <Home />
      },
      {
        path: "cart",
        element: <ProtectedRoute allowedRoles={['CUSTOMER']} element={<Cart />} />
      },
  
      {
        path: "checkout",
        element: <ProtectedRoute allowedRoles={['CUSTOMER']} element={<CheckOut />} />
      },
      {
        path: "list-order",
        element: <ProtectedRoute allowedRoles={['CUSTOMER']} element={<ListOrder />} />
      },
      {
        path: "manage/products",
        element: <ProtectedRoute allowedRoles={['ADMIN']} element={<ProductManagement />} />
      },
      {
        path: "manage/products/addProduct",
        element: <ProtectedRoute allowedRoles={['ADMIN']} element={<AddProduct />} />
      },
      {
        path: "manage/promotions",
        element: <ProtectedRoute allowedRoles={['ADMIN']} element={<PromotionManagement />} />
      },
      {
        path: "manage/employees",
        element: <ProtectedRoute allowedRoles={['ADMIN']} element={<EmployeeManagement />} />
      },
      {
        path: "manage/customers",
        element: <ProtectedRoute allowedRoles={['ADMIN']} element={<CustomerManagement />} />
      },
      {
        path: "manage/orders",
        element: <ProtectedRoute allowedRoles={['ADMIN', 'EMPLOYEE']} element={<HistoryOrders />} />
      },
      {
        path: "manage/revenue",
        element: <ProtectedRoute allowedRoles={['ADMIN']} element={<Revenue />} />
      },
    ],
  },
]);

function App() {
  return (
    <>
      <RouterProvider router={router} />
      <ToastContainer position="top-center" autoClose={1500} />
    </>
  );
}

export default App;
