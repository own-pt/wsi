<%@page import="no.uio.ifi.wsi.gui.SearchInterface"%>
<%@page import="com.hp.hpl.jena.xmloutput.impl.Abbreviated"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="no.uio.ifi.wsi.gui.html.PageResult"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.net.InetAddress"%>
<%@page import="no.uio.ifi.wsi.gui.html.HTMLPaginator"%>
<%@page import="java.util.ArrayList"%> 
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
	SearchInterface intf=SearchInterface.instance();
	int resultsPerPage = intf.requestsPerPage(request);
	
	StringBuilder b=new StringBuilder();
	for (String fds:intf.formats())
		b.append(fds+" ");
	String annotations=b.toString().trim();

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="description" content="Search Interface" />
<title><%=intf.demoName() %> Search Interface</title>

<link rel='stylesheet' type='text/css' href="css/logon.css"/>
<link rel='stylesheet' type='text/css' href='css/styles.css'/>
<link rel='stylesheet' type='text/css' href='css/simpletabs.css'/>

<script type="text/javascript" src="js/simpletabs_1.3.js"></script>
<script type="text/javascript" src="js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="js/sdp.js"></script>
<script type="text/javascript" src="js/alttxt.js"></script>
<script type="text/javascript" src="js/logon.js"></script>

<script type="text/javascript">
window.annotations=<%=annotations%>.value.split(" "); 
</script>

</head>
<body onload="messenger()">
	<div class="drop">
		<ul class="drop_menu">
			<li> <a href='search.jsp'><%=intf.demoName() %> Search Interface</a><li>
			<li><a 
				href='#'>SPARQL</a>
			</li>
			<li><a target="_blank"
				href="http://alt.qcri.org/semeval2014/task8/index.php?id=search">Help</a>
			</li>
		</ul>
	</div>
	<h3>Semantic Search Interface - <%=intf.demoName() %> (SPARQL)</h3>
	<div class="centered">
		<form method="post" action="search.jsp">
			<strong>Query</strong><br/>
			<textarea rows="20" cols="100" name="search"></textarea><br/>
			&nbsp;&nbsp;<strong>Results per Page</strong>
			<input type="number" name=resultsPerPage min="10" step="10" max="100" value="<%=resultsPerPage%>" />
			<input name="query" type="submit" value="search" /><br />
		</form>
	</div>
	<div id="navtxt" class="navtext" style="position: absolute; top: -100px; left: 0px; visibility: hidden"></div>
</body>
</html>