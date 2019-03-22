import { SHOW_MESSAGE } from "./MessageActions";
import { combineReducers } from "redux";

const initialMessage = {
  severity: "info",
  summary: "",
  detail: ""
};

function fetchMessage(state = initialMessage, action) {
  switch (action.type) {
    case SHOW_MESSAGE:
      return action.payload;
    default:
      return state;
  }
}

const messagging = combineReducers({ fetchMessage });

export default messagging;
