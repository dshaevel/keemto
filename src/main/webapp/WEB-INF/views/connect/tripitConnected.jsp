<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>

<h3>Connected to TripIt</h3>

<form id="disconnect" method="post">
	<div class="formInfo">
		<p>
			The Spring Social Showcase sample application is connected to your TripIt account.
			Click the button if you wish to disconnect.
		</p>			
	</div>
	<button type="submit">Disconnect</button>	
	<input type="hidden" name="_method" value="delete" />
</form>

<p><a href="<c:url value="/tripit" />">View your TripIt data</a></p>
