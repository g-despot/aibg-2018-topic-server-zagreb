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
            }

            resolve();
        });
    }).end();
}

const newGame = (resolve) => {
    http.request(url + '/game/new?playerId=' + playerId, (res) => {
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
                gameId = parseInt(response.result);
            }
            resolve();
        });
    }).end();
}

const joinGame = (resolve) => {
    http.request(url + '/game/join?playerId=' + playerId + '&gameId=' + gameId, (res) => {

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
    });
}

const play = (resolve) => {
    let next = nextMove();
    console.log('Doing ' + next);
    doAction(next);
    resolve();
}

const exit = () => {
    rl.close();
    return true;
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

    if (absDx > absDy) {
        return dx > 0 ? 'd' : 'a';
    } else {
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
                    console.log(game.player1.id == playerId ? game.player1 : game.player2);
                    console.log(">> Calculating next move <<")
                    let next = nextMove();
                    console.log('Doing ' + next);
                    setTimeout(() => doAction(next), 1000);
                }
            });
        }).end();
}

// Helper objs
const commands = {
    'random': randomGame,
    'new': newGame,
    'join': joinGame,
    'exit': exit,
    'play': play
};

// Invoke
rl.question('Player ID: ', function (id) {
    playerId = parseInt(id);
    doCommand();
});
