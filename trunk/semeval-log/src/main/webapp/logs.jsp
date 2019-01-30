<%@page import="no.uio.ifi.pgstore.gui.CountedHost"%>
<%@page import="java.io.File"%>
<%@page import="no.uio.ifi.pgstore.gui.Logging"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String host=request.getParameter("host");
	String html=null;
	if (host!=null)
		html=Logging.instance().toHtml(host);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="description" content="Search Interface" /> 
<title>Search Interface</title>
</head>
<script type="text/javascript">

function toggle(id, string) {
	var ele = document.getElementById("div-"+id);
	var text = document.getElementById("show-"+id);
	if(ele.style.display == "block") {
    	ele.style.display = "none";
		text.innerHTML = "Show "+string;
  	}
	else {
		ele.style.display = "block"; 
		text.innerHTML = "Hide "+string;
	}
}
</script>
<body>
<h2>Hosts</h2>
<% 
	for (CountedHost s:Logging.instance().hosts()) {
%>
	<a href="logs.jsp?host=<%=s.getHost() %>"><%=s.getHost()+" ( "+s.getCount()+" )" %></a><br/>
<% } %>
<% if (html!=null) { %>
<hr/>
<h2>Host <%=host %></h2>
<%=html %>
<% } %>
</body>
</html>