<img height="300" src="https://i.imgur.com/odwLWJV.png" width="600" alt=""/>

## 🤔 What is this?

This is a group of games based on the Netflix series named in the same way (Squid Game). This is to demonstrate the operation of the GameAPI previously uploaded to my Github profile

## 🎲 How many games does it have?

In total there are 7 games, as in the official series:

- Red Light, Green Light:

  - Instruction: To win you must reached the goal

![](https://i.imgur.com/eQt3nV7.png)

- Sugar Honey Combs:

  - Instruction: Break 10 blocks with the Burning Needle to win!

![](https://i.imgur.com/RdXKsZW.png)

- Night Ambush:

  - Instruction: We have heard that various groups came together to kill players and raise the amount of money... survive

![](https://i.imgur.com/8OxrAXu.png)

- Tug Of War:

  - Instruction: Throw players from the other team into the void, remember that the people who don't fall will win!

![](https://i.imgur.com/5L0to5W.png)

- Marbles:

  - Instruction: Guess if the number is even or odd to get 20 marbles and win!

![](https://i.imgur.com/lUqYi7B.png)

- Hopscotch:

  - Instruction: In this game you have to reach the goal, remember that there are tempered crystals through which you can walk... the rest are fake

![](https://i.imgur.com/vNdUXfY.png)

- Squid Game:

  - Instruction: Wow, last game, speaking of last... the last person standing wins!

![](https://i.imgur.com/W4Yw2AP.png)

To observe in a better way, here is a video with the finished game:

[![Watch the video](https://i.imgur.com/w8D2Ty1.png)](https://youtu.be/Rr-WE7pSW_k)

## 🔨 Installation & Setup

- First, download the latest version [here](https://github.com/Josscoder/SquidGame/releases/latest)
- Second, go to the plugins folder of your server and put the previously downloaded file inside.
- Third, restart your server and you can start configuring,

```yml
developmentMode: false #when this is false, the game itself will not work, only developer settings, play sounds and get position are enabled
maxPlayers: 5
minPlayers: 2
maps: # These are all the maps we use
  waitingRoomMap:
    name: world # This will be the name of the world that will be the waiting room
    safeSpawn: 119:14:130 # This will be the center or the place where you always appear
    exitEntitySpawn: 122:12:132 # This is the position where the exit entity to the lobby will be
    pedestalCenterSpawn: 147:11:129 # This will be the position where you appear in the final stage, when the winner is on the pedestal
    pedestalOneSpawn: 156:14:129
    pedestalTwoSpawn: 156:13:133
    pedestalThreeSpawn: 156:12:125
  roomMap:
    name: SquidGameRoom
    safeSpawn: 0:101:0
  greenLightRedLightMap:
    name: RedLightGreenLight
    safeSpawn: 128:6:103
    goalCornerOne: 148:5:149
    goalCornerTwo: 108:25:158
    dollPosition: 128:6:152 # This will be the position in which the doll appears in the game red light green light
  sugarHoneycombsMap:
    name: SugarHoneyCombs
    safeSpawn: 128:4:128
    spawns: # These are the positions of all the rooms where the players appear to break the blocks
      '1': 323:4:152
      '2': 323:4:109
      '3': 286:4:109
      '4': 286:4:152
      '5': 304:4:130
  tugOfWarMap:
    name: TugOfWar
    safeSpawn: 128:4:128
    red:
      spawns: # These are the positions where the players appear in the game of Tug of War
        '1': 128:17:130
        '2': 129:17:130
        '3': 130:17:130
        '4': 127:17:130
        '5': 126:17:130
    blue:
      spawns:
        '1': 128:17:125
        '2': 129:17:125
        '3': 130:17:125
        '4': 127:17:125
        '5': 126:17:125
  marblesMap:
    name: Marbles
    safeSpawn: 128:5:128
    odd: # This will be the place where the player will stop to say that the number is odd, for this you have to get the upper corner and then the side corner at the bottom, covering the entire area
      cornerOne: 130:4:132
      cornerTwo: 126:9:136
    pair:
      cornerOne: 130:4:124
      cornerTwo: 126:9:120
  hopscotchMap:
    name: Hopscotch
    safeSpawn: 128:16:152
    crystal:
      number: 10 # This is the number of glass sections the map will have
      sections:
        '1':
          - 127:15:147 # This is the position of the first crystal
          - 129:15:147 # This is the position of the second crystal
        '2':
          - 127:15:143
          - 129:15:143
        '3':
          - 127:15:139
          - 129:15:139
        '4':
          - 127:15:135
          - 129:15:135
        '5':
          - 127:15:131
          - 129:15:131
        '6':
          - 127:15:127
          - 129:15:127
        '7':
          - 127:15:123
          - 129:15:123
        '8':
          - 127:15:119
          - 129:15:119
        '9':
          - 127:15:115
          - 129:15:115
        '10':
          - 127:15:111
          - 129:15:111
    goal: # This is the position of the goal, it is recorded just like the game of marbles
      cornerOne: 134:13:108
      cornerTwo: 120:23:100
```

If you have any questions, contact me through my discord Josscoder#9867 or on my [Twitter](https://twitter.com/Josscoder)

## 📜 LICENSE

This game is licensed under the [Apache License 2.0](https://github.com/Josscoder/SquidGame/blob/main/LICENSE), this game was completely created by Josscoder (Luciano Mejia)