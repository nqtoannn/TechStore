import React from "react";
import { Link } from "react-router-dom";
import "./styles/ProductElement.css";

const ProductElement = ({ id, title, image, discount, price, rating, sold }) => {
    return (
        <div className="cursor-pointer">
            <div className="bg-gray-200 p-4 rounded-lg">
                <div className="flex flex-col">
                <div className="relative">
                    <Link to={`/product/${id}`} onClick={() => window.scrollTo(0, 0)}>
                    <img
                        className="w-full h-60 object-cover rounded-lg"
                        src={image}
                        alt="product"
                    />
                    </Link>
                    {discount !== 0 && (
                    <div className="absolute top-2 right-2 bg-red-500 text-white text-xs font-semibold py-1 px-2 rounded">
                        -{discount}%
                    </div>
                    )}
                </div>
                <div className="mt-2">
                    <Link to={`/product/${id}`} onClick={() => window.scrollTo(0, 0)}>
                    <h3 className="text-lg font-medium line-clamp-1">{title}</h3>
                    </Link>
                    <div className="flex items-center justify-between mt-2">

                    <span className="text-lg font-medium text-orange-400 line-clamp-1">{price} VND</span>
                    </div>
                    <div className="flex items-center justify-between mt-1">
                    <div className="flex items-center text-yellow-500">
                        {[...Array(5)].map((_, index) => (
                        <svg
                            key={index}
                            className={`w-4 h-4 ${index < rating ? 'fill-current' : 'text-gray-300'}`}
                            xmlns="http://www.w3.org/2000/svg"
                            viewBox="0 0 24 24"
                            fill="currentColor"
                        >
                            <path d="M12 17.27l6.18 3.73-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
                        </svg>
                        ))}
                    </div>
                    <span className="text-gray-500 text-sm">{sold} sold</span>
                    </div>
                </div>
                </div>
            </div>
        </div>
    );
};

export default ProductElement;
