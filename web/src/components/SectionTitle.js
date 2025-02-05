import React from 'react'

const SectionTitle = ({title, path}) => {
  return (
    <div className="section-title-div">
      <h1 className="section-title-title">{title}</h1>
      <p className="section-title-path">{path}</p>
    </div>
  )
}

export default SectionTitle