import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, Box, CircularProgress, Rating } from '@mui/material';
import { toast } from 'react-toastify';
import { getToken } from '../utils/getToken';

const ReviewDialog = ({ open, onClose, productItemId }) => {
  const [comment, setComment] = useState('');
  const [imageUrl, setImageUrl] = useState('');
  const [ratingValue, setRatingValue] = useState(0);
  const [imageFile, setImageFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const userId = localStorage.getItem('userId');
  const token = getToken();

  const handleSubmit = async () => {
    try {
      setLoading(true);
      const reviewData = {
        userId: userId, 
        comment,
        imageUrl : 'null',
        productItemId,
        ratingValue
      };
      console.log(reviewData)
      const response = await fetch('http://localhost:8080/techstore/api/customer/addReview', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(reviewData)
      });

      if (!response.ok) {
        throw new Error('Failed to add review');
      } 

      const addedReview = await response.json();
      const reviewId = addedReview.data;

      const formData = new FormData();
      formData.append('file', imageFile);
      formData.append('reviewId', reviewId);
      formData.append('namePath', "review");
      const uploadResponse = await fetch('http://localhost:8080/techstore/api/customer/uploadReviewImg', {
        method: 'PUT',
        body: formData,
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      toast.success('Đánh giá sản phẩm thành công', {
        toastStyle: {
          background: "#4CAF50",
          color: "white",
          border: "1px solid #388E3C",
          borderRadius: "8px",
          boxShadow: "0 2px 4px rgba(0, 0, 0, 0.2)",
        }
      });

      onClose(); 
    } catch (error) {
      console.error('Error adding review:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImageUrl(reader.result);
        setImageFile(file);
      };
      reader.readAsDataURL(file);
    }
  };
  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Đánh giá sản phẩm</DialogTitle>
      <DialogContent>
        <Box mb={2}>
          <Rating
            name="rating"
            value={ratingValue}
            onChange={(event, newValue) => setRatingValue(newValue)}
          />
        </Box>
        <TextField
          label="Bình luận"
          name="comment"
          variant="outlined"
          fullWidth
          multiline
          rows={4}
          margin="normal"
          value={comment}
          onChange={(e) => setComment(e.target.value)}
        />
        <Button variant="contained" component="label" fullWidth style={{
                    color: '#fff',
                    fontSize: '14px',
                    backgroundColor: '#6ADC94',
                    '&:hover': {
                        backgroundColor: '#f4511e',
                    }
                }}>
          Tải lên hình ảnh
          <input type="file" hidden onChange={handleImageChange} />
        </Button>
        {imageUrl && <img src={imageUrl} alt="uploaded" style={{ width: '50%', marginTop: '20px' }} />}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="primary">Hủy</Button>
        <Button onClick={handleSubmit} color="primary" disabled={loading}>
          {loading ? <CircularProgress size={24} /> : 'Gửi đánh giá'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ReviewDialog;
