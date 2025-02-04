import React, { useEffect, useState } from 'react';
import Box from '@mui/material/Box';
import { MobileDatePicker } from '@mui/x-date-pickers/MobileDatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import TextField from '@mui/material/TextField';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import dayjs from 'dayjs';
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore';

dayjs.extend(isSameOrBefore);

export default function Step3({ getTime, getDate, selectedServiceName, selectedServiceLength, selectedServiceId }) {
  const [timeValue, setTimeValue] = useState(dayjs());
  const [dateValue, setDateValue] = useState(dayjs());
  const [times, setTimes] = useState([]);
  const [selectedTime, setSelectedTime] = useState('');
  const [selectedDate, setSelectedDate] = useState(dayjs());

  const generateTimes = () => {
    const startTime = dayjs().hour(9).minute(0).second(0);
    const endTime = dayjs().hour(20).minute(0).second(0);
    const timeSlots = [];
    let currentTime = startTime.clone();

    while (currentTime.isSameOrBefore(endTime)) {
      timeSlots.push(currentTime.format('HH:mm:ss'));
      currentTime = currentTime.add(30, 'minute');
    }

    return timeSlots;
  };

  const handleChangeTime = (event) => {
    setSelectedTime(event.target.value);
    getTime(event.target.value);
  };

  const handleChangeDate = (newDate) => {
    setSelectedDate(newDate);
    setDateValue(newDate);
    getDate(newDate.format("YYYY-MM-DD"));
  };

  useEffect(() => {
    const availableTimes = generateTimes();
    setTimes(availableTimes);
  }, []);

  return (
    <Box sx={{ width: '100%', minWidth: 400, bgcolor: 'background.paper' }}>
      <br />
      <FormControl fullWidth>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <MobileDatePicker
            minDate={dayjs()}
            label="Select Date"
            inputFormat="YYYY/MM/DD"
            value={dateValue}
            onChange={handleChangeDate}
            renderInput={(params) => <TextField {...params} />}
          />
        </LocalizationProvider>
      </FormControl>
      <br />
      <br />
      {times.length > 1 ? (
        <FormControl fullWidth>
          <InputLabel id="demo-simple-select-label">Time</InputLabel>
          <Select
            labelId="demo-simple-select-label"
            id="demo-simple-select"
            value={selectedTime}
            label="Time"
            onChange={handleChangeTime}
          >
            {times.map((time) => (
              <MenuItem key={time} value={time}>{time}</MenuItem>
            ))}
          </Select>
        </FormControl>
      ) : (
        <h3>Closed</h3>
      )}
    </Box>
  );
}
