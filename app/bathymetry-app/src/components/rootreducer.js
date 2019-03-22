import loadingSpinner from './utility/loading/SpinnerReducers';
import messaging from './utility/messaging/MessageReducers';
import { combineReducers } from 'redux';

export const rootReducer = combineReducers({
  loadingSpinner,
  messaging
});

