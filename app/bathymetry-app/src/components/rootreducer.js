import loadingSpinner from './utility/loading/SpinnerReducers';
import messaging from './utility/messaging/MessageReducers';
import login from './login/LoginReducer';
import map from './app/map/MapReducer'
import { combineReducers } from 'redux';

export const rootReducer = combineReducers({
  loadingSpinner,
  messaging,
  login,
  map
});

