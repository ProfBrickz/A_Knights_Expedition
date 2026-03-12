<html>
<head>
<title>TBAG Adventure</title>

<style>
.container {
    display: grid;
    grid-template-columns: 3fr 1fr;
    grid-template-rows: auto auto auto;
    gap: 10px;
}

.room {
    grid-column: 1;
    border: 1px solid black;
    padding: 10px;
}

.healthandcoins {
    grid-column: 2;
    border: 1px solid black;
    padding: 10px;
}

.log {
    grid-column: 1;
    border: 1px solid black;
    height: 200px;
    overflow-y: scroll;
    padding: 10px;
}

.inventory {
    grid-column: 2;
    border: 1px solid black;
    padding: 10px;
}

.command {
    grid-column: 1 / span 2;
    border: 1px solid black;
    padding: 10px;
}
</style>
</head>

<body>

<h1>TBAG Adventure</h1>

<div class="container">

<div class="room">
<p>${room.description}</p>
</div>

<div class="healthandcoins">
<h2>Health</h2>
${player.health}
<h2>Coins</h2>
</div>



<div class="log">
<h3>Game Log</h3>
${message}
</div>

<div class="inventory">
<h3>Inventory</h3>
<p>Empty</p>
</div>

<div class="command">
<form action="tbag" method="post">
<input type="text" name="command" placeholder="Enter command..." style="width:80%">
<button type="submit">Submit</button>
</form>
</div>

</div>

</body>
</html>