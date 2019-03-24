import { connect } from "react-redux";
import SpinnerComponent from "./SpinnerComponent";

const mapStateToProps = state => {
  return {
    isLoading: state.loadingSpinner.spinnerState
  };
};

const LoadingSpinner = connect(mapStateToProps)(SpinnerComponent);

export default LoadingSpinner;
