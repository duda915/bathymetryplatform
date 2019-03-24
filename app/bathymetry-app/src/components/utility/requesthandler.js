import { showMessage } from "./messaging/MessageActions";
import { toggleSpinner } from "./loading/SpinnerActions";
import { store } from "../store";

export function handleRequest({
  requestPromise,
  onSuccess,
  onError,
  onSuccessMessage,
  onErrorMessage
}) {
  store.dispatch(toggleSpinner(true));

  requestPromise
    .then(response => {
      if (onSuccess) {
        onSuccess(response);
      }

      if (onSuccessMessage) {
        store.dispatch(
          showMessage("success", "Success", onSuccessMessage(response))
        );
      }
    })
    .catch(error => {
      if (onError) {
        onError(error);
      }

      if (onErrorMessage) {
        store.dispatch(showMessage("error", "Error", onErrorMessage(error)));
      }
    })
    .finally(() => store.dispatch(toggleSpinner(false)));
}
