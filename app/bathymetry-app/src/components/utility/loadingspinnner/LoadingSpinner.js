import React from "react";
import PropTypes from "prop-types";
import { ProgressSpinner } from "primereact/progressspinner";

function LoadingSpinner(props) {
  if (props.isLoading) {
    return (
      <div className="loading">
        <ProgressSpinner className="loading__spinner" />
      </div>
    );
  }
}

LoadingSpinner.propTypes = {
  isLoading: PropTypes.bool.isRequired
};

export default LoadingSpinner;
