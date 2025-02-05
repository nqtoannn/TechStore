import React, { useEffect, useState } from 'react';
import Box from '@mui/material/Box';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import axios from 'axios';

export default function Step2({ getService }) {
  const [listOfServices, setListOfServices] = useState([]);
  const [selectedIndex, setSelectedIndex] = useState(0);

  const handleListItemClick = (serviceId, index) => {
    setSelectedIndex(serviceId);
    console.log("index: ", index)
    getService(serviceId, serviceId);
  };

  useEffect(() => {
    axios.get('http://localhost:9000/api/v1/hairservice/findAll')
      .then((results) => {
        setListOfServices(results.data.data);
        console.log(listOfServices);
        console.log(results.data.data);
      })
      .catch((error) => {
        console.error('Error fetching data:', error);
        alert('An error occurred while fetching data. Please try again later.');
      });
  }, []);
  

  return (
    <Box sx={{ width: '100%', minWidth: 400, bgcolor: 'background.paper' }}>
      <List component="nav" aria-label="secondary mailbox folder">
        {Array.isArray(listOfServices) && listOfServices.map((service, idx) => (
          <ListItemButton
            key={service.id}
            onClick={() => handleListItemClick(service.id, service.id)}
          >
            <ListItemText primary={service.serviceName} />
          </ListItemButton>
        ))}
      </List>
    </Box>
  );
}
