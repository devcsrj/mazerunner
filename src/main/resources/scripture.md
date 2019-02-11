Greetings, **mazerunner**.

In front of you is a massive door blocking the entrance to the maze that hides 
the most coveted prize - the Championship Trophy. The door only admits entry 
to those who speak its language, _Websockets_:

```
ws(s)://<server>/maze/move
```

To gain entrance to the maze, runners must identify themselves by presenting 
their identity as `x-runner-tag` HTTP header in the initial handshake:

```
x-runner-tag    :   id:name
```

The runner holds freewill to provide any `id` or `name`.

To navigate around the maze, connected clients shall send coordinates in the form 
of `(x,y)`.

```
> (-1,-1)
< (32,0)[(32,1)]
```

By sending `(-1,-1)`, the server responds with runner's current location `(32,0)`, 
and the neighboring room `(32,1)`. By sending `(32,1)`:

```
> (32,1)
< (32,1)[(32,0),(32,2)]
```

The runner is moved to `(32,1)`, whose neighboring rooms are `(32,0)`, `(32,2)`.

Sending coordinates that are unreachable from the current room, such as `(32,3)` 
will impale the runner, thus disconnecting the session. At this point, the runner 
has to re-enter.

Navigate the maze and reach the room containing the trophy. [X marks the spot](/maze/goal)

Mazerunner, begin!

---
### FAQ

#### What should I use as `id` and `name`?

The `id` could be anything, as long as it is unique to your team alone. Keep it private! 
The `name` is used for the leaderboard. 

Tip: Keep the `name` short, eg.: University acronym

#### I'm getting `bad handshake` when connecting to the websocket url

Make sure you're using the correct protocol: `wss` if the server is on `https` 
or `ws` on `http`

#### STOMP?

No, the websocket server does not use a subprotocol such as `STOMP`. Only pure text websocket.

#### I can't see my character in the UI

The [maze](/maze) is merely an animated representation of the movements within the 
maze. As long as your solution can communicate with the websocket endpoint, the activity 
in the UI does not affect your score.

#### How are the solutions scored?

If only one participant reaches the trophy room by the end of the competition, s/he claims 
the championship title.

Otherwise, a series of _warp rounds_ shall occur. The maze will be warped, after which all 
runners shall re-run in the maze. The runner which takes the **least number of moves** to 
reach the trophy room wins the round.

The warp round will occur at least 5 times. This implies that the runner who wins the majority 
of the rounds will claim the championship title.

#### So, time to finish doesn't count?

It will, if and only if you're the only runner that reached the trophy room. This means that 
the runner with fewer number of moves can contest your throne.
