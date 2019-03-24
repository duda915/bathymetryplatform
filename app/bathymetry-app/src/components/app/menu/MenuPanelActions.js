export const SHOW_PANEL = "SHOW_PANEL";

export function showPanel(show) {
  return {
    type: SHOW_PANEL,
    payload: {
      show
    }
  };
}
