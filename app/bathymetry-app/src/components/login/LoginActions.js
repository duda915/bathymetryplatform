export const LOGGED_IN = "LOGGED_IN";

export function  changeLoginState(state) {
  return {
    type: LOGGED_IN,
    payload: {
      loginState: state
    }
  }
}