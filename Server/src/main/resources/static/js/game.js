import TerrainTexture from "../gif/terrain.png";
import PlayerPinkTexture from "../gif/player1.png";
import PlayerCyanTexture from "../gif/player2.png";
import { API_ROOT } from "./configuration";

const tileW = 40;
const tileH = 40;
const mapW = 20;
const mapH = 20;

const tileImageW = 32;
const tileImageH = 32;

const gemImageW = 120;
const gemImageH = 120;

const avatarCanvasW = 100;
const avatarCanvasH = 100;

const terrainCanvasSize = 800;
const canvasPadding = 10;

const o = "o";
const f = "f";

let tileVersions = [
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
];

for (let i = 0; i < 19; i++) {
  tileVersions.push(tileVersions[0]);
}

let terrain = new Image();
terrain.src = TerrainTexture;
let playerImg1 = new Image();
playerImg1.src = PlayerCyanTexture;
let playerImg2 = new Image();
playerImg2.src = PlayerPinkTexture;

const TYPES = {
  FIRE: { index: 0, emoji: "🔥" },
  NORMAL: { index: 1, emoji: "🌱" },
  GRASS: { index: 2, emoji: "🤷" },
  WATER: { index: 3, emoji: "🌊" }
};

const ITEM_TYPES = {
  FIRE: { index: 0, color: "red" },
  GRASS: { index: 1, color: "green" },
  WATER: { index: 2, color: "blue" },
  OBSTACLE: { index: 3, color: "black" }
};

const ATTACK_TYPES = {
  FIRE: { index: 0 },
  GRASS: { index: 1 },
  WATER: { index: 2 }
};

const DIRECTIONS = {
  DOWN: 0,
  UP: 1,
  LEFT: 2,
  RIGHT: 3
};

const ARROW_KEYCODES = {
  LEFT: 37,
  UP: 38,
  RIGHT: 39,
  DOWN: 40
};

let keysDown = {
  [ARROW_KEYCODES.LEFT]: false,
  [ARROW_KEYCODES.UP]: false,
  [ARROW_KEYCODES.RIGT]: false,
  [ARROW_KEYCODES.DOWN]: false
};

class Draw {
  constructor({ ctx, terrain, tileVersions }) {
    this.ctx = ctx;
    this.terrain = terrain;
    this.tileVersions = tileVersions;
  }
  drawTile(x, y, type, item) {
    const version = this.tileVersions[y][x];
    this.drawTileOnCanvas({ x, y }, { type, version: version[0] });
    // this.drawTileOnCanvas(
    //   { x, y, offsetX: tileW },
    //   { type, version: version[1] }
    // );
    // this.drawTileOnCanvas(
    //   { x, y, offsetY: tileH },
    //   { type, version: version[2] }
    // );
    // this.drawTileOnCanvas(
    //   { x, y, offsetX: tileW, offsetY: tileH },
    //   { type, version: version[3] }
    // );
    this.drawItemOnCanvas({ x, y }, { type, item });
  }
  drawTileOnCanvas({ x, y, offsetX = 0, offsetY = 0 }, { type, version }) {
    this.ctx.drawImage(
      terrain,
      version * tileImageW,
      tileImageH * TYPES[type].index,
      tileImageW,
      tileImageH,
      x * tileW + canvasPadding, // * 2 + offsetX,
      y * tileH + canvasPadding, //* 2 + offsetY,
      tileW, // ovdje ide * 2
      tileH
    );
  }
  drawItemOnCanvas({ x, y }, { type, item }) {
    if (!item) return;
    // Drawing obstacle item
    if (item === "OBSTACLE") {
      const itemOffsetX = tileImageW * 7;
      const itemOffsetY = 0;
      this.ctx.drawImage(
        terrain,
        itemOffsetX,
        itemOffsetY + tileImageH * TYPES[type].index,
        tileImageW,
        tileImageH,
        x * tileW + canvasPadding, //  * 2 + tileW / 2,
        y * tileH + canvasPadding, //  * 2 + tileH / 2,
        tileW,
        tileH
      );
      return;
    }
    const itemType = ITEM_TYPES[item];
    const itemOffsetX = tileImageW * 7;
    const itemOffsetY = tileImageH * 4;
    this.ctx.drawImage(
      terrain,
      itemOffsetX,
      itemOffsetY + tileImageH * itemType.index,
      tileImageW,
      tileImageH,
      x * tileW + canvasPadding, // * 2 + tileW / 2,
      y * tileH + canvasPadding, // * 2 + tileH / 2,
      tileW,
      tileH
    );
    return;
    const radius = 10;
    this.ctx.beginPath();
    this.ctx.arc(
      x * tileW * 2 + tileW,
      y * tileH * 2 + tileH,
      radius,
      0,
      2 * Math.PI,
      false
    );
    this.ctx.fillStyle = itemType.color;
    this.ctx.fill();
  }
  drawObstacle(...args) {
    this.ctx.fillStyle = "#000";
    this.ctx.fillRect(...args);
  }
}

const PLAYER_1_COLOR = "#A8018C";
const PLAYER_2_COLOR = "#01A89C";

class Character {
  constructor(ctx, info, opponentInfo, direction = DIRECTIONS.UP) {
    this.ctx = ctx;
    this.lastAction = info.lastAction;
    this.otherPlayerPosition = {
      x: opponentInfo.x,
      y: opponentInfo.y
    };
    this._id = info._id;
    this.teamName = info.teamName ? info.teamName : info._id;
    this.avatarCtx = document
      .getElementById(`${this._id}-avatar`)
      .getContext("2d");
    this.direction = direction;
    this.update(info, opponentInfo, direction);
  }
  draw() {
    const underAttackOffset =
      this.info && this.info.healthChange < 0 ? 4 * tileImageW : 0;
    this.ctx.drawImage(
      this._id === "player1" ? playerImg1 : playerImg2,
      this.direction * tileImageW + underAttackOffset,
      this.type.index * tileImageH * 4,
      tileImageW,
      tileImageH * 2,
      this.x * tileW + canvasPadding, // * 2 + tileW / 2,
      this.y * tileH - tileH + canvasPadding, //  * 2,
      tileW,
      tileH * 2
    );
    // const centerX = this.x * tileW + tileW / 2; // * 2 + tileW;
    // const centerY = this.y * tileH + 10 ; // * 2 + 10;
    // this.drawMarker(this.ctx, centerX, centerY);
  }
  drawAdditionalEffects() {
    if (this.lastAction && this.lastAction.startsWith("r") && this.attackType) {
      this.drawRangeAttack();
    }
  }
  unitValue(value) {
    return value !== 0 ? value / Math.abs(value) : 0;
  }
  drawRangeAttack() {
    const diffX = this.otherPlayerPosition.x - this.x;
    const diffY = this.otherPlayerPosition.y - this.y;
    const directionVector = [this.unitValue(diffX), this.unitValue(diffY)];
    const rangeX = this.x + directionVector[0];
    const rangeY = this.y + directionVector[1];
    this.ctx.drawImage(
      terrain,
      tileImageW * 4,
      tileImageH * 4 + tileImageH * this.attackType.index,
      tileImageW,
      tileImageH,
      rangeX * tileW + canvasPadding,
      rangeY * tileH + canvasPadding,
      tileW,
      tileH
    );
  }
  drawMarker(context, centerX, centerY) {
    const color = this._id === "player1" ? PLAYER_1_COLOR : PLAYER_2_COLOR;
    context.beginPath();
    context.beginPath();
    context.moveTo(centerX, centerY);
    context.lineTo(centerX - 10, centerY - 15);
    context.lineTo(centerX + 10, centerY - 15);
    context.closePath();
    context.fillStyle = color;
    context.fill();
  }
  refresh() {
    this.draw();
  }
  update(info, opponentInfo, fallbackDirection = DIRECTIONS.UP) {
    this.oldX = this.x;
    this.oldY = this.y;
    this.x = info.x;
    this.y = info.y;
    if (info.active) {
      this.direction = this.calculateDirection(fallbackDirection);
    }
    this.type = TYPES[info.type === "NEUTRAL" ? "NORMAL" : info.type];
    this.attackType = ATTACK_TYPES[info.type];
    this.info = info;
    this.lastAction = info.lastAction;
    this.otherPlayerPosition = {
      x: opponentInfo.x,
      y: opponentInfo.y
    };
    this.setInfoBox();
  }
  calculateDirection(fallbackDirection) {
    if (typeof this.oldX !== "undefined" && typeof this.oldY !== "undefined") {
      const dX = this.x - this.oldX;
      const dY = this.y - this.oldY;
      if (dX > 0) {
        return DIRECTIONS.RIGHT;
      }
      if (dX < 0) {
        return DIRECTIONS.LEFT;
      }
      if (dY > 0) {
        return DIRECTIONS.DOWN;
      }
      if (dY < 0) {
        return DIRECTIONS.UP;
      }
    }
    // Nothing changed => Return the old direction if it existed
    if (this.direction) return this.direction;
    return fallbackDirection;
  }
  setInfoBox() {
    const div = document.querySelector(`.player.${this._id}`);
    if (this.info.active) {
      div.classList.add("active");
    } else {
      div.classList.remove("active");
    }
    div.querySelector("h2").innerHTML = `${this.teamName}`;
    const healthPercentage = this.info.health;
    const gems = this.info.morphItems.length > 0 ? `<li>Gems: ${this.info.morphItems.join(", ")}</li>` : '';
    div.querySelector(".info").innerHTML = `
      <ul>
        <li>
        <div class="health"><div class="fill" style="width: ${healthPercentage}%"></div></div>
        Health: ${this.info.health}
        </li>
        <li>Lives: ${this.info.lives}</li>
        ${gems}
      </ul>
    `;
    this.avatarCtx.clearRect(0, 0, avatarCanvasW, avatarCanvasH);
    this.avatarCtx.drawImage(
      this._id === "player1" ? playerImg1 : playerImg2,
      this.direction * tileImageW,
      this.type.index * tileImageH * 4,
      tileImageW,
      tileImageH * 2,
      50 - tileW / 2,
      10,
      tileW,
      tileH * 2
    );
    const centerX = 50;
    const centerY = 20;
    this.drawMarker(this.avatarCtx, centerX, centerY);
  }
}

function shuffle(array) {
  var currentIndex = array.length,
    temporaryValue,
    randomIndex;

  // While there remain elements to shuffle...
  while (0 !== currentIndex) {
    // Pick a remaining element...
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex -= 1;

    // And swap it with the current element.
    temporaryValue = array[currentIndex];
    array[currentIndex] = array[randomIndex];
    array[randomIndex] = temporaryValue;
  }

  return array;
}

function initializeTileVersions() {
  for (let i = 0; i < mapH; i++) {
    for (let j = 0; j < mapW; j++) {
      tileVersions[i][j] = shuffle([0, 1, 2, 3]);
    }
  }
}

export class Game {
  constructor(gameId) {
    this.ctx = document.getElementById("game").getContext("2d");
    this.gameId = gameId;
    this.drawInstance = null;
    this.map = null;
    this.firstRender = true;
    this.players = [];
    this.shouldDraw = true;
  }
  init() {
    $(document).ready(() => {
      $(window).on("keydown", function(e) {
        keysDown[e.keyCode] = true;
      });
      $(window).on("keyup", function(e) {
        keysDown[e.keyCode] = false;
      });
      $.ajax({
        url: `http://${API_ROOT}/game?gameId=${this.gameId}`,
        dataType: "json",
        success: result => {
          document.querySelector(".loading").classList.add("hidden");
          initializeTileVersions();
          this.drawInstance = new Draw({
            ctx: this.ctx,
            terrain,
            tileVersions
          });
          this.update(result);
          if (result.winner !== null) {
            this.showWinner(result.winner);
          }
          requestAnimationFrame(this.draw.bind(this));
        },
        error: error => {
          document.querySelector(".loading .loading-content").innerHTML =
            "<h1>OOOOOOOOMG, can't load game</h1>";
        }
      });
    });
  }
  isActive(player, game) {
    const nextPlayer = game.nextPlayer;
    return player.x != nextPlayer.x || player.y != nextPlayer.y;
  }
  update(game) {
    this.map = game.map.tiles;
    const info1 = {
      _id: "player1",
      active: this.isActive(game.player1, game),
      ...game.player1
    };
    const info2 = {
      _id: "player2",
      active: this.isActive(game.player2, game),
      ...game.player2
    };
    if (this.players.length) {
      this.players[0].update(
        info1,
        info2,
        this.calculateFallbackDirection(info1, info2)
      );
      this.players[1].update(
        info2,
        info1,
        this.calculateFallbackDirection(info1, info2)
      );
    } else {
      this.players = [
        new Character(this.ctx, info1, info2, DIRECTIONS.RIGHT),
        new Character(this.ctx, info2, info1, DIRECTIONS.LEFT)
      ];
    }
  }
  calculateFallbackDirection(first, second) {
    const dX = second.x - first.x;
    const dY = first.y - second.y;
    if (dX > 0) {
      return DIRECTIONS.RIGHT;
    }
    if (dX < 0) {
      return DIRECTIONS.LEFT;
    }
    if (dY > 0) {
      return DIRECTIONS.DOWN;
    }
    if (dY < 0) {
      return DIRECTIONS.UP;
    }
  }
  draw() {
    if (this.ctx === null) {
      return;
    }

    this.ctx.clearRect(0, 0, terrainCanvasSize + 2 * canvasPadding, terrainCanvasSize + 2 * canvasPadding);

    // let sec = Math.floor(Date.now() / 1000);
    // if (sec != currentSecond) {
    //   currentSecond = sec;
    //   frameLastSecond = frameCount;
    //   frameCount = 1;
    // } else {
    //   frameCount++;
    // }

    // rendering tiles from image
    for (let y = 0; y < mapH; y++) {
      for (let x = 0; x < mapW; x++) {
        switch (this.map[y][x].type) {
          case "NORMAL":
          case "WATER":
          case "FIRE":
          case "GRASS":
            this.drawInstance.drawTile(
              x,
              y,
              this.map[y][x].type,
              this.map[y][x].item
            );
            break;
          case "OBSTACLE":
            this.drawInstance.drawObstacle(
              x * tileW * 2,
              y * tileH * 2,
              tileW * 2,
              tileH * 2
            );
            break;
        }
      }
    }

    this.players.forEach(p => p.refresh());
    this.players.forEach(p => p.drawAdditionalEffects());

    // this.ctx.font = "bold 10pt sans-serif";
    // this.ctx.fillStyle = "#FF0000";
    // this.ctx.fillText("FPS: " + frameLastSecond, 10, 20);
    if (this.shouldDraw || this.firstRender)
      requestAnimationFrame(this.draw.bind(this));
    this.firstRender = false;
  }
  showWinner(winnerIndex) {
    this.shouldDraw = false;
    let text = "Game over";
    const el = document.querySelector(".finished");
    if (winnerIndex === 1 || winnerIndex === 2) {
      const player = this.players[winnerIndex - 1];
      text = `${player.teamName} won the game!`;
    } else {
      text = `Ladies and gentleman, it's a draw!`;
      el.querySelector("p").innerHTML = "";
    }
    el.querySelector("h1").innerHTML = text;
    el.classList.remove("hidden");
  }
}
