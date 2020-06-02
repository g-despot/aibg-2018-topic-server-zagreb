
import qs from 'qs';

import "./popper.min";
import "./bootstrap.min";
import { Game } from "./game";
import { WebsocketHandler } from  "./ws";
import { API_ROOT } from './configuration';

const queryArgs = qs.parse(
  window.location.search.substring(1)
);
const gameId = queryArgs.gameId;
if (gameId) {
  console.info(`Connecting to game ${ gameId }`);
  const game = new Game(gameId);
  game.init();
  new WebsocketHandler('ws://' + API_ROOT + '/streaming?gameId=' + gameId, game);
}
