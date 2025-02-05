import React, { useEffect, useState } from 'react';
import Box from '@mui/material/Box';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import { getToken } from '../../utils/getToken';

export default function Step4({ getStylist }) {
  const [listOfStylist, setListOfStylist] = useState([]);
  const [selectedIndex, setSelectedIndex] = useState(0);


  const handleListItemClick = (userId, index) => {
    setSelectedIndex(userId);
    console.log("id stylist: ", index)
    getStylist(userId, userId);
  };
  
  useEffect(() => {
    const fetchData = async () => {
      const token = getToken();
      console.log("token: ", token)
      try {
        const response = await fetch('http://localhost:9000/api/v1/user/getAllEmployees', {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
  
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
  
        const results = await response.json();
        console.log('Full response:', results);
  
        if (results.data) {
          setListOfStylist(results.data);
          console.log('Updated listOfStylist:', results.data.data);
        } else {
          console.warn('Unexpected data structure:', results);
        }
      } catch (error) {
        console.error('Error fetching data:', error);
        alert('An error occurred while fetching data. Please try again later.');
      }
    };
  
    fetchData();
  }, []);
  
  
  
  

  return (
    <Box sx={{ width: '100%', minWidth: 400, bgcolor: 'background.paper' }}>
      <List component="nav" aria-label="secondary mailbox folder">
        {Array.isArray(listOfStylist) && listOfStylist.map((user, idx) => (
          <ListItemButton
            key={user.id}
            onClick={() => handleListItemClick(user.id, user.id)}
          >
            <ListItemText primary={user.fullName} />
          </ListItemButton>
        ))}
      </List>
    </Box>
  );
}
