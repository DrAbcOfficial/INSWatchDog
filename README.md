# INSWatchDog
 Add some functions to insurgency:sandstorm

 ## Message

send rollback message to player, there are some paraments for special replacement

|parament|usage|
|----|----|
|{bestplayer}|the highest score playerinfo|
|{maps}|map counts in this server|
|{time}|current time|
|{player}|player counts in this server|
|{difficult}| current AI difficulty|

## SyncPlayerList

sync players info for other modules

## LogWathcer

### GameMessage

send message when gamestart/gameover/roundwon/roundfailed

### SaidCommand

add some said command for player chat

all commands have to start with `!`

|default command|usage|
|----|----|
|help [Page]|List all available commands|
|me|Who am i|
|gamestate|Current game state|

will add more commands in future...

## DifficultTweak

automatic tweak ai difficult according to player counts

## SoloBotTweak

automatic tweak solobot counts according to player counts

## HeartBeatWatcher

monitoring game state via rcon, program will auto restart game if it is dead

(Only available on Linux or Windows platform)

## Usage

1. Download latest release or build from source
2. Download config file and edit it
3. Put config file with jar pack together
4. 

```
java -jar INSWatchDog.jar
```