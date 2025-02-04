import React, { useState, useEffect } from 'react';
import { Typography, CircularProgress, Rating, Pagination } from '@mui/material';
import axios from 'axios';
import { getToken } from '../utils/getToken';
import user from "../assets/user.png";


const CommentSection = ({ productId }) => {
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const commentsPerPage = 3; 

  const token = getToken();

  useEffect(() => {
    fetchComments();
  }, []);

  const fetchComments = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/techstore/api/products/review/getAll/${productId}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      setComments(response.data.data);
    } catch (err) {
      setError('Failed to fetch comments');
    } finally {
      setLoading(false);
    }
  };

  // Pagination logic
  const handlePageChange = (event, value) => {
    setCurrentPage(value);
  };

  const displayedComments = comments.slice((currentPage - 1) * commentsPerPage, currentPage * commentsPerPage);

  if (loading) return <CircularProgress />;
  if (error) return <Typography color="error">{error}</Typography>;

  return (
    <div className="mt-4">
      <h5 className="text-xl font-semibold mb-4">Đánh giá sản phẩm</h5>
      
      {displayedComments.length > 0 ? (
        displayedComments.map((comment) => (
          <div className="mb-6 bg-white rounded-md p-4">
            <div className="flex items-center mb-2">
              <img src={user} alt="Profile" className="w-12 h-12 rounded-full mr-4" />
              <div>
                <p className="font-semibold text-base">{comment.customer}</p>
                <div className="flex items-center">
                <div className="flex items-center text-yellow-500">
                  {[...Array(5)].map((_, index) => (
                    <svg
                      key={index}
                      className={`w-5 h-5 ${
                        index < comment.ratingValue ? "fill-current" : "text-gray-300"
                      }`}
                      xmlns="http://www.w3.org/2000/svg"
                      viewBox="0 0 24 24"
                      fill="currentColor"
                    >
                      <path d="M12 17.27l6.18 3.73-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
                    </svg>
                  ))}
                </div>
                  <span className="text-gray-500 text-sm ml-2">{comment.createAt}</span>
                </div>
              </div>
            </div>
            <p className="text-sm mb-4">
              {comment.comment}
            </p>
            {comment.imageUrl !== "null" && (
                <img src={comment.imageUrl} alt="Review Image 1" className="w-24 h-24 rounded-lg object-cover" />
            )}
          </div>
        ))
      ) : (
        <p className="text-sm text-gray-500">Chưa có đánh giá nào.</p>
      )}

      {comments.length > commentsPerPage && (
        <div className="flex justify-center mt-4">
          <Pagination 
            count={Math.ceil(comments.length / commentsPerPage)} 
            onChange={handlePageChange} 
            color="primary" 
            siblingCount={1} 
            boundaryCount={2}
          />
        </div>
      )}
    </div>

  );
};

export default CommentSection;
