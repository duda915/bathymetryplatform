export const SEND_MAP_COMMAND = "SEND_MAP_COMMAND";
export const TOGGLE_LAYER = "TOGGLE_LAYER";
export const ADD_LAYERS = "ADD_LAYERS";
export const REMOVE_LAYERS = "REMOVE_LAYERS";

export function sendMapCommand({ commandType, commandPayload }) {
  return {
    type: SEND_MAP_COMMAND,
    payload: {
      commandType,
      commandPayload
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

export function addLayers(layers) {
  return {
    type: ADD_LAYERS,
    payload: {
      layers
    }
  };
}

export function removeLayers() {
  return {
    type: REMOVE_LAYERS
  };
}
