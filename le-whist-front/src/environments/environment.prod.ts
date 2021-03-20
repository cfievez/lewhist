export const environment = {
  production: true,
  webSocketURL: 'wss://' +  new URL(window.location.href).host + '/socket/websocket'
};
