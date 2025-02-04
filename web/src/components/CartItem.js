import { useDispatch } from "react-redux";
import { Box, Typography, TextField, Button } from "@mui/material";
import { removeItem, updateCartAmount } from "../features/cart/cartSlice";

const CartItem = ({ cartItem }) => {
  const { id, title, price, image, amount } = cartItem;
  const dispatch = useDispatch();

  return (
    <Box
      key={id}
      sx={{
        marginBottom: 12,
        display: "flex",
        flexDirection: { xs: "column", sm: "row" },
        gap: 4,
        borderBottom: "1px solid",
        borderColor: "divider",
        paddingBottom: 6,
        "&:last-child": {
          borderBottom: 0,
        },
      }}
    >
      {/* IMAGE */}
      <img
        src={`${image}`}
        alt={title}
        style={{
          height: 196,
          width: 196,
          borderRadius: 8,
          objectFit: "cover",
        }}
      />
      {/* INFO */}
      <Box sx={{ marginLeft: { sm: 16 }, width: { sm: 48 } }}>
        {/* TITLE */}
        <Typography variant="subtitle1" sx={{ textTransform: "capitalize", fontWeight: "medium" }}>
          {title}
        </Typography>
        
      </Box>
      <Box sx={{ marginLeft: { sm: 12 } }}>
        
        <Button
          className="mt-2"
          variant="text"
          color="warning"
          onClick={() => dispatch(removeItem(id))}
          sx={{ textTransform: "none", color: "text.secondary" }}
        >
          remove
        </Button>
      </Box>
      {/* PRICE */}
      <Typography variant="subtitle1" sx={{ marginLeft: "auto", fontWeight: "medium", color: "text.secondary" }}>
        ${(price * amount).toFixed(2)}
      </Typography>
    </Box>
  );
};

export default CartItem;