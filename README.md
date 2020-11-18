# INSWatchDog
 Add some functions to insurgency:sandstorm
 
 chat commands, roll message, difficult tweak by players number, solo mode bot tweak, game dummy death bug cheack or etc.

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

all commands must be starting with `!`

|default command|usage|
|----|----|
|help [Page]|List all available commands|
|me|Who am i|
|gamestats|Current game state|
|rtv|Vote for changing random map|

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

## Build for yourself
1. Download or clone source from github

2. Build project
   1. IDEA
      1. Download a IDEA
      2. Open project in IDEA
      3. Build -> Build Artifacts -> Build
      4. Grab jar in out/INSWatchDog_main_jar/INSWatchDog.main.jar
   2. Gradle
      1. Open root directory in Shell or CMD
      2. `./gradlew makeJar` (Linux) or `gradlew.bat makeJar` (Win)
      3. Grab some cookies or drink a cup of tea and waiting....
      4. BUILD SUCCESSFUL in 1y6M21d18h46m3s
      5. Got your jar in root directory
