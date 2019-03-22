import React from "react";
import PropTypes from "prop-types";
import { ProgressSpinner } from "primereact/progressspinner";
import "./Spinner.css";

function SpinnerComponent(props) {
  if (props.isLoading) {
    return (
      <div className="loading">
        <ProgressSpinner className="loading__spinner" />
      </div>
    );
  } else {
    return null;
  }
}

SpinnerComponent.propTypes = {
  isLoading: PropTypes.bool.isRequired
};

export default SpinnerComponent;
