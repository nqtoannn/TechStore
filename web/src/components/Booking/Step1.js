import React, {useEffect, useState} from 'react';
import Box from '@mui/material/Box';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import axios from 'axios';

export default function Step1({getBusinessId}) {
  const [selectedIndex, setSelectedIndex] = useState(null);
  const [listOfBusinesses, setListOfBusinesses] = useState([])
  const [selectedBusiness, setSelectedBusiness] = useState(null);
  const [list,setList] = useState([]);
  const handleListItemClick = (id, index) => {
      //this function sets business id into state
      getBusinessId(id, id);
      setSelectedIndex(id);
      console.log("index salon: ", index) 
  };

  useEffect(() => {
    // GET REQUEST TO POPULATE listOfBusinesses state array
    axios.get("http://localhost:9000/api/v1/appointment/salon/findAll")
      .then((results) => {
        // Once the data is retrieved successfully, update the state
        setListOfBusinesses(results.data.data);
        console.log("data: ", results.data.data)
      })
      .catch((error) => {
        console.error('Error fetching data:', error);
        alert('An error occurred while fetching data. Please try again later.');
      });
  }, []); 

  return (
    <Box sx={{ width: '100%', minWidth:400, bgcolor: 'background.paper' }}>
     <List component="nav" aria-label="secondary mailbox folder">
        {Array.isArray(listOfBusinesses) && listOfBusinesses.map((salon, idx) => (
          <ListItemButton
            key={salon.id}
            onClick={(event) => handleListItemClick(salon.id, salon.id)}
          >
            <ListItemText primary={salon.name} />
          </ListItemButton>
        ))}
      </List>
    </Box>
  );
}

