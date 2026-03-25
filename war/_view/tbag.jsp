<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
<head>
  <title>A Knight's Expedition</title>

  <style>
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

    .log {
      grid-column: 1;
      grid-row: 2;
      overflow-y: auto;
    }

    .inventory {
      grid-column: 2;
      grid-row: 1 / 3;
      overflow-y: auto;

      ul {
      	margin: 0;
      	padding-left: 20px;
      }
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
        const log = document.querySelector('.log');
        log.scrollTop = log.scrollHeight;
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
    </div>

    <div class="log">
  		<pre>${fn:escapeXml(dialog)}</pre>
	</div>

    <div class="inventory">
    	<ul>
    		<c:forEach var="item" items="${player.inventory.items.values()}">
        		<li>${item.amount} x ${item.name}</li>
     		</c:forEach>
    	</ul>
    </div>

    <div class="input">
  		<input type="text" name="command" placeholder="Enter command..." />
  		<button type="submit">Submit</button>
  		<input name="dialog" type="hidden" value="${fn:escapeXml(dialog)}" />
	</div>

    
    <div class="search">
      <input type="text" name="search" placeholder="Search item name..." />
      <button type="submit">Submit</button>
      <input name="dialog" type="hidden" value="${fn:escapeXml(dialog)}" />
    </div>

  </div>

</form>

</body>
</html>
