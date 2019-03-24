export const TOGGLE_SPINNER = "TOGGLE_SPINNER";

export function toggleSpinner(newSpinnerState) {
  return {
    type: TOGGLE_SPINNER,
    payload: {
      spinnerState: newSpinnerState
    }
  };
}
