<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
<head>
  <title>A Knight's Expedition</title>

  <style>
  	* {
  		box-sizing: border-box;
  	}

    body {
      background: #ddd;
      font-family: sans-serif;
    }

    .container {
      display: grid;
      grid-template-columns: 3fr 1fr;
      grid-template-rows: auto 1fr auto;
      gap: 10px;
      height: 95vh;
      padding: 10px;
    }

    .container > div {
      background: #eee;
      border: 4px solid black;
      padding: 10px;
      box-sizing: border-box;
    }

    .top-bar {
      grid-column: 1;
      grid-row: 1;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 12px;
    }

    .health {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .bar {
      width: 200px;
      height: 20px;
      border: 3px solid black;
      background: red;
    }

    .fill {
      height: 100%;
      background: green;
    }

    .location {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 6px;
    }

    .location-name {
      font-weight: bold;
    }

    .location-image {
      width: 150px;
      object-fit: cover;
      border: 3px solid black;
      background: #ccc;
    }

    #log {
      grid-column: 1;
      grid-row: 2;
      overflow-y: auto;

      pre {
      	text-wrap: balance;
      }
    }

    .inventory {
      grid-column: 2;
      grid-row: 1 / 3;
      overflow-y: auto;

      ul {
      	margin: 0;
      	padding-left: 0;
        list-style: none;
      }
    }
    
    .inventory-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 4px 0;
    }

    .item-icon {
      width: 50px;
      object-fit: contain;
      image-rendering: auto;
    }

    .input {
      grid-column: 1;
      grid-row: 3;
    }

    .search {
      grid-column: 2;
      grid-row: 3;
    }
  </style>
  <script>
  	window.addEventListener('load', () => {
        let logDiv = document.getElementById('log');
        logDiv.scrollTop = logDiv.scrollHeight;

		let commandInput = document.getElementById('command');
		commandInput.focus();
		commandInput.select();
      });
  </script>
</head>

<body>

<form action="${pageContext.request.contextPath}/tbag" method="post">

  <div class="container">

    <div class="top-bar">
      <div class="health player">
        <span>Player</span>
        <div class="bar">
          <div class="fill" style="width: ${playerHealth}%"></div>
        </div>
      </div>

      <div class="health enemy">
        <span>Enemy</span>
        <div class="bar">
          <div class="fill" style="width: ${enemyHealth}%"></div>
        </div>
      </div>

      <div class="location">
        <div class="location-name">${player.room.name}</div>
        <c:choose>
           <c:when test="${not empty player.room.assetName}">
			<img class="location-image" src="${pageContext.request.contextPath}/assets/locations/${player.room.assetName}" alt="${player.room.assetName}" />
		  </c:when>
		  <c:otherwise>
			<img class="location-image" src="${pageContext.request.contextPath}/assets/locations/Backrooms.gif" alt="${player.room.assetName}" />
		  </c:otherwise>
        </c:choose>
      </div>
    </div>

    <div id="log">
  		<pre>${fn:escapeXml(dialog)}</pre>
	</div>

    <div class="inventory">
    	<ul>
    		<c:forEach var="item" items="${player.inventory.items.values()}">
        		<li class="inventory-item">
              <c:choose>
                <c:when test="${not empty item.assetName}">
                  <img class="item-icon" src="${pageContext.request.contextPath}/assets/items/${item.assetName}" alt="${item.name}" />
                </c:when>
                <c:otherwise>
                  <img class="item-icon" src="${pageContext.request.contextPath}/assets/items/fixIt.png" alt="${item.name}" />
                </c:otherwise>
              </c:choose>
              <span>${item.amount} x ${item.name}</span>
            </li>
     		</c:forEach>
    	</ul>
    </div>

    <div class="input">
  		<input id="command" type="text" name="command" placeholder="Enter command..." />
  		<button type="submit">Submit</button>
	</div>

    
    <div class="search">
      <input id="search" type="text" name="search" placeholder="Search item name..." />
      <button type="submit">Submit</button>
    </div>

  </div>

</form>

</body>
</html>
