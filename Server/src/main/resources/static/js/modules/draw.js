class Draw {
    constructor({ ctx, terrain, tileVersions }) {
      this.ctx = ctx;
      this.terrain = terrain;
      this.tileVersions = tileVersions;
    }
    drawTile(x, y, type) {
      const version = this.tileVersions[y][x];
      this.drawTileOnCanvas({ x, y }, { type, version: version[0] });
      this.drawTileOnCanvas(
        { x, y, offsetX: tileW },
        { type, version: version[1] }
      );
      this.drawTileOnCanvas(
        { x, y, offsetY: tileH },
        { type, version: version[2] }
      );
      this.drawTileOnCanvas(
        { x, y, offsetX: tileW, offsetY: tileH },
        { type, version: version[3] }
      );
    }
    drawTileOnCanvas({ x, y, offsetX = 0, offsetY = 0 }, { type, version }) {
      this.ctx.drawImage(
        terrain,
        version * tileImageW,
        tileImageH * TYPES[type],
        tileImageW,
        tileImageH,
        x * tileW * 2 + offsetX,
        y * tileH * 2 + offsetY,
        tileW,
        tileH
      );
    }
    drawObstacle(...args) {
      ctx.fillStyle = "#000";
      ctx.fillRect(...args);
    }
  }

export default Draw;