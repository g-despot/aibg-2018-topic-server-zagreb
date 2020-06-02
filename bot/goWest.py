import requests

_game = None
_gameId = None
_playerIndex = None
_playerId = None
url = 'http://localhost:9080'

def get(url):
  r = requests.get(url)
  res = r.json()
  return res

def random_game(playerId):
  global _game, _gameId, _playerIndex
  res = get(url + '/train/random?playerId=' + str(playerId))
  _game = res['result']
  _gameId = _game['id']
  print("Game id: " + str(_gameId))
  _playerIndex = res['playerIndex']
  return res

def join(playerId, gameId):
  global _game, _gameId, _playerIndex
  res = get(url + '/game/play?playerId=' + str(playerId) + '&gameId=' + str(gameId))
  _game = res['result']
  _gameId = _game['id']
  # print("Game id: " + _gameId)
  _playerIndex = res['playerIndex']
  return res

def run():
  global _game, _playerIndex, _playerId, _gameId
  move = calculate(_game, _playerIndex)
  # After we send an action - we wait for response
  res = do_action(_playerId, _gameId, move)
  # Other player made their move - we send our move again
  run()

def calculate(game, playerIndex):
  iAmOne = playerIndex == 1;
  return 'sd' if iAmOne else 'wa';

def do_action(playerId, gameId, action):
  return get(url + '/doAction?playerId=' + str(playerId) + '&gameId=' + str(gameId) + '&action=' + action)

print("Enter player ID:")
_playerId = input()
print("Enter command:")
command = input()
if command == 'random':
  print(_playerId)
  random_game(_playerId)
  run()
elif command == 'join':
  print("Enter game id:")
  _gameId = input()
  join(_playerId, _gameId)
  run()
