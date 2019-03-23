export const FETCH_FEATURE_INFO = "FETCH_FEATURE_INFO";
export const MAP_DRAG_BOX = "MAP_DRAG_BOX";
export const ZOOM_TO_LAYER = "ZOOM_TO_LAYER";
export const ZOOM_TO_FIT = "ZOOM_TO_FIT";
export const TOGGLE_LAYER = "TOGGLE_LAYER";
export const TOGGLE_STYLE = "TOGGLE_STYLE";
export const ADD_LAYER = "ADD_LAYER";
export const REMOVE_LAYERS = "REMOVE_LAYERS";

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

export function deleteDragBox() {
  return {
    type: MAP_DRAG_BOX,
    payload: {
      box: null
    }
  };
}

export function requestZoomToLayer(layerId) {
  return {
    type: ZOOM_TO_LAYER,
    payload: {
      layerId
    }
  };
}

export function handledZoomToLayer() {
  return {
    type: ZOOM_TO_LAYER,
    payload: {
      layerId: null
    }
  };
}

export function requestZoomToFit() {
  return {
    type: ZOOM_TO_FIT,
    payload: {
      requestZoom: true
    }
  };
}

export function handledZoomToFit() {
  return {
    type: ZOOM_TO_FIT,
    payload: {
      requestZoom: false
    }
  };
}

export function toggleLayer(layerId) {
  return {
    type: TOGGLE_LAYER,
    payload: {
      layerId
    }
  };
}

export function requestToggleStyle() {
  return {
    type: TOGGLE_STYLE,
    payload: {
      requestToggleStyle: true
    }
  };
}

export function handledToggleStyle() {
  return {
    type: TOGGLE_STYLE,
    payload: {
      requestToggleStyle: false
    }
  };
}

export function addLayer(layer) {
  return {
    type: ADD_LAYER,
    payload: {
      layer
    }
  };
}

export function removeLayers() {
  return {
    type: REMOVE_LAYERS
  };
}
