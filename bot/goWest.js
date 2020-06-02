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

// Ways of playing
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

// Gaming
const run = (resolve) => {
    let next = nextMove();
    console.log('Doing ' + next);
    doAction(next);
    resolve();
}

const nextMove = () => {
    let iAmOne = playerIndex == 1;
    return iAmOne ? 'd' : 'a';    
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

rl.question('Player ID: ', function (id) {
    playerId = parseInt(id);
    doCommand();
});
