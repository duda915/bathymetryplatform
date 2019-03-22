import loadingSpinner from './utility/loading/SpinnerReducers';
import messaging from './utility/messaging/MessageReducers';
import login from './login/LoginReducer';
import { combineReducers } from 'redux';

export const rootReducer = combineReducers({
  loadingSpinner,
  messaging,
  login
});

