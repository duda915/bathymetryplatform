import { connect } from "react-redux";
import MessageComponent from './MessageComponent';

const mapStateToProps = state => {
  return {
    message: state.messaging.fetchMessage
  }
}

const Message = connect(mapStateToProps)(MessageComponent);

export default Message;