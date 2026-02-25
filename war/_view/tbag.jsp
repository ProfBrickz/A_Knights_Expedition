<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
	<head>
		<title>TBAG Game</title>
	</head>

	<body>
			<c:if test="${empty room}">
                <div>
                Welcome to the Matrix
                </div>
            </c:if>
			<div style="width: 500px; height: 500px; overflow: auto; border: 1px solid black; padding: 5px; text-align: left; white-space: pre-line;">
			${dialog}
			</div>
		<form action="${pageContext.servletContext.contextPath}/tbag" method="post">
			<table>
				<tr>
					<td class="label">Command:</td>
					<td><input type="text" name="command" size="50" value="${command}" /></td>
				</tr>
			</table>
			<input name="dialog" type="hidden" value="${dialog}" />
		</form>
	</body>
</html>