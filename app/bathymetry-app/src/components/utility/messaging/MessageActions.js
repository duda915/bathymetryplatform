export const SHOW_MESSAGE = 'SHOW_MESSAGE';

export function showMessage(severity, summary, detail) {
  return {
    type: SHOW_MESSAGE,
    payload: {
      severity,
      summary,
      detail
    }
  }
}