import React from "react";
import PropTypes from "prop-types";

const Container = ({ children, className }) => {
  const newClassName = `max-w-screen-xl mx-auto py-10 px-4 lg:px-0 ${className || ""}`;
  
  return <div className={newClassName}>{children}</div>;
};

Container.propTypes = {
  children: PropTypes.node.isRequired,
  className: PropTypes.string,
};

export default Container;
