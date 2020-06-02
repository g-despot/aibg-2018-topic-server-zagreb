const http = require('http');

const readline = require('readline');
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

const url = "http://localhost:9080";
var playerId = null;
var gameId = null;
var game = null;
var playerIndex = null;

async function doCommand() {
    rl.question('Input a command: ', async function (command) {
        let fn = commands[command];
        if (!fn) {
            console.log('Unknown command.');
        } else {
            await new Promise(fn);
        }
        doCommand();
    })
}

// Commands
const randomGame = (resolve) => {
    http.request(url + '/train/random?playerId=' + playerId, (res) => {
        let response = "";
        res.on('data', (obj) => {
            response += obj;
        });
        res.on('end', () => {
            response = JSON.parse(response);

            if (!response.success) {
                console.log("Error has occured: " + response);
                console.log('Message: ' + response.message);
            } else {
                game = response.result;
                gameId = game.id;
                playerIndex = response.playerIndex;
                console.log("Visit> http://localhost:9080/?gameId=" + game.id);
            }
            resolve();
        });
    }).end();
}

const join = (resolve) => {
    rl.question('Input a game id: ', async function (id) {
        gameId = id;
        http.request(url + '/game/play?playerId=' + playerId + '&gameId=' + gameId, (res) => {
            let response = "";
            res.on('data', (obj) => {
                response += obj;
            });
            res.on('end', () => {
                response = JSON.parse(response);

                if (!response.success) {
                    console.log("Error has occured: " + response);
                    console.log('Message: ' + response.message);
                } else {
                    game = response.result;
                    gameId = game.id;
                    playerIndex = response.playerIndex;
                }
                resolve();
            });
        }).end();
    });
}

const run = (resolve) => {
    let next = nextMove();
    console.log('Doing ' + next);
    doAction(next);
    resolve();
}

// Gaming

const nextMove = () => {
    let iAmOne = playerIndex == 1;
    
    let me = iAmOne ? game.player1 : game.player2;
    let opponent = iAmOne ? game.player2 : game.player1;

    let dx = opponent.x - me.x;
    let absDx = Math.abs(dx);
    let dy = opponent.y - me.y;
    let absDy = Math.abs(dy);

    console.log("dx=" + dx + ", " + "|dx|=" + absDx + ", " + "dy=" + dy + ", " + "|dy|=" + absDy);

    if (me.morphItems.length) {
        if (me.morphItems.includes('WATER') && me.type !== 'WATER') {
            return 'mw';
        }
        if (me.morphItems.includes('FIRE') && me.type !== 'FIRE') {
            return 'mf';
        }
        if (me.morphItems.includes('GRASS') && me.type !== 'GRASS') {
            return 'mg';
        }
    }
    const canAttack = opponent.x === me.x || opponent.y === me.y;
    if (absDx > absDy) {
        if (canAttack) {
            return dx > 0 ? 'rd' : 'ra';
        }
        return dx > 0 ? 'd' : 'a';
    } else {
        if (canAttack) {
            return dy > 0 ? 'rs' : 'rw';
        }
        return dy > 0 ? 's' : 'w';
    }
}

const doAction = (action) => {
    http.request(url + '/doAction?playerId=' + playerId + '&gameId=' + gameId + '&action=' + action,
        (res) => {
            let response = "";

            res.on('data', (obj) => {
                response += obj;
            });
            res.on('end', () => {
                response = JSON.parse(response);

                if (!response.success) {
                    console.log("Error has occured: " + response);
                    console.log('Message: ' + response.message);
                } else {
                    game = response.result;
                    let next = nextMove();
                    console.log('Doing ' + next);
                    // Throttle for better viewing experience
                    setTimeout(() => doAction(next), 100);
                }
            });
        }).end();
}

// Helper objs
const commands = {
    'random': randomGame,
    'join': join,
    'run': run
};

// Invoke
rl.question('Player ID: ', function (id) {
    playerId = parseInt(id);
    doCommand();
});
