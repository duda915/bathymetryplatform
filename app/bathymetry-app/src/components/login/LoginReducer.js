import { LOGGED_IN } from "./LoginActions";
import { combineReducers } from "redux";

function loginState(state = false, action) {
  switch (action.type) {
    case LOGGED_IN:
      return action.payload.loginState;
    default:
      return state;
  }
}

const login = combineReducers({loginState});
export default login;