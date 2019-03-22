import React, { Component } from "react";
import { Growl } from "primereact/growl";
import PropTypes from "prop-types";

export default class MessageComponent extends Component {
  componentDidUpdate() {
    this.showGrowl();
  }

  showGrowl = () => {
    this.growl.show({
      severity: this.props.message.severity,
      summary: this.props.message.summary,
      detail: this.props.message.detail,
      closable: false
    });
  };

  render() {
    return (
      <div>
        <Growl ref={ref => (this.growl = ref)} />
      </div>
    );
  }
}

MessageComponent.propTypes = {
  message: PropTypes.shape({
    severity: PropTypes.oneOf(["info", "error", "warning", "success"])
      .isRequired,
    summary: PropTypes.string.isRequired,
    detail: PropTypes.string.isRequired
  })
};
