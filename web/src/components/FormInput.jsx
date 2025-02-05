  import React from 'react';

const FormInput = ({ label, name, type, defaultValue, size }) => {
  return (
    <div className='form-control'>
      <label htmlFor={name} className='label'>
        <span className='label-text capitalize'>{label}</span>
      </label>
      <div className="input-container">
        <input
          type={type}
          name={name}
          defaultValue={defaultValue}
          className={`input input-bordered`}
          placeholder="Search here..."
        />
      </div>
    </div>
  );
};

export default FormInput;