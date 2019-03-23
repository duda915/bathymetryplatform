export const FETCH_FEATURE_INFO = "FETCH_FEATURE_INFO";
export const MAP_DRAG_BOX = "MAP_DRAG_BOX";

export function fetchFeatureInfo(url) {
  return {
    type: FETCH_FEATURE_INFO,
    payload: {
      featureInfoUrl: url
    }
  };
}

export function registerDragBox(box) {
  return {
    type: MAP_DRAG_BOX,
    payload: {
      box
    }
  };
}
