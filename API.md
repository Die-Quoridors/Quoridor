# Die HTTP API von diesem Spiel

## GET /info
```
{
    "wsPort": Int // der port des ws servers
}
```

## POST /game
Anfrage: 
```
{
    "playerCount": Int // die Anzahl der Spieler in dieser Runde
    "wallLimit": Int // die Anzahl der Waende in dieser Runde
}
```

Antwort:
```
{
    "gameId": String // Die GameId des Spieles das erstellt wurde
}
```

## GET /game/:gameId
```
{
    "strictPlayer": Boolean // true: der Spieler kann sich nur selber bewegen / false: jeder kann jeden bewegen
    "playerCount": Int // die Anzahl der Spieler in dieser Runde
    "wallLimit": Int // die Anzahl der Waende in dieser Runde
    "currentPlayers": Int // die Anzahl der aktuellen Spieler in der Runde
}
```

## DELETE /game/:gameId
```

```

# Die WebSocket API von diesem Spiel

## Aufbau jedes Pakets

Die Pakete werden in JSON kodiert.

#### Aufbau des Paketes
```
{
    "event": String // die Event Id
    "data": JSONObject // die Daten die dieses Event übermittelt
}
```
In den einzelnen Event Abschnitten wird der Inhalt des `data` Feldes beschrieben.

## Events

### gameJoin
Client -> Server
```
{
    "game": String // Die GameId der Spieles dem beigetreten werden soll
}
```

### gameLeave
Server -> Client
```
{
    "player": Int // Die SpielerId des Spielers der das Spiel verlassen hat
}
```

### gameInit
Server -> Client
```
{
    "player": Int // der Index des Spielers
    "playerCount": Int // die Anzahl der Spieler in dieser Runde
    "strictPlayer": Boolean // true: der Spieler kann sich nur selber bewegen / false: jeder kann jeden bewegen
}
```

### nextPlayer
Server -> Client
```
{
    "player": Int // der Index des Spieler der jetzt dran ist
}
```

### playerMove
Server <-> Client
```
{
    "player": Int // der Index des Spielers
    "x": Int // die neue X Koordinate
    "y": int // die neue Y Koordinate
}
```

### wallPlace
Server <-> Client
```
{
    "player": Int // der Index des Spielers
    "x": Int // die X Koordinate der Wand
    "y": int // die Y Koordinate der Wand
    "rotation": String // die Drehung der Wand ['Vertical', 'Horizontal']
}
```