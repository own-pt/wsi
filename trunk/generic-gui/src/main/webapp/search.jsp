<%@page import="java.lang.reflect.Method"%>
<%@page import="java.util.Map"%>
<%@page import="no.uio.ifi.wsi.gui.SearchInterface"%>
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
	String calculate = request.getParameter("calculate");
	if (calculate!=null){
		String html = intf.calculate(calculate);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(html);
		response.getWriter().close();
		return;
	} 

	String format = request.getParameter("format");
	if (format != null) {
		String html = intf.returnDiv(request.getParameter("id"), format);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(html);
		response.getWriter().close();
		return;
	}
	
	
	Map<String,String> examples=intf.examples(); 
	
	String result = null;
	
	String query=intf.query(request);
	
	String remoteHost = intf.httpHost(request);
	List<String> annotation = intf.annotations(request);

	int resultsPerPage = intf.requestsPerPage(request);

	String id = request.getParameter("id");
	

	if (request.getParameter("query") != null) {
		if (query.startsWith("::")){
			result=intf.searchId(query.substring(2),annotation.get(0));
		}else{
			PageResult pr = intf.searchWithPaginator(query,resultsPerPage,annotation.get(0), remoteHost);
			if (pr.getErrorMessage() != null) {
				StringBuilder b = new StringBuilder();
				if (pr.getSparql() != null) {
					b.append("<h3>Error executing sparql query:</h3>");
					b.append(StringEscapeUtils.escapeHtml4(query).replace("\n", "\n<br/>"));
				} else {
					b.append("<h3>Error parsing query:</h3>");
				}
				b.append(StringEscapeUtils.escapeHtml4(pr.getErrorMessage()));
				result = b.toString();
			} else {
				result = pr.getPaginator().nextPage();
				id = pr.getPaginator().getId();
			}
		}
	} else {
		if (id != null) {
			HTMLPaginator pager = intf.getPaginator(id);
			if (pager == null) {
				result = "<p>Your session expired!</p>";
			}
			if (request.getParameter("next") != null) {
				result = pager.nextPage();
			} else {
				result = pager.previousPage();
			}
		}
	}
	
	StringBuilder b=new StringBuilder();
	for (String fds:intf.formats())
		b.append(fds+" ");
	String annotations=b.toString().trim();
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="description" content="Search Interface" />
<title><%=intf.demoName()%> Search Interface</title>

<link rel='stylesheet' type='text/css' href="css/logon.css"/>
<link rel='stylesheet' type='text/css' href='css/styles.css'/>
<link rel='stylesheet' type='text/css' href='css/simpletabs.css'/>

<script type="text/javascript" src="js/simpletabs_1.3.js"></script>
<script type="text/javascript" src="js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="js/sdp.js"></script>
<script type="text/javascript" src="js/alttxt.js"></script>
<script type="text/javascript" src="js/logon.js"></script>

<script type="text/javascript">
window.annotations="<%=annotations%>".split(" "); 
function handleClick(myRadio) {
    var ele = document.getElementById("query-text");
    <%for(String fmr:intf.formats()){%>
	if (myRadio.value==="<%=fmr%>"){
  		ele.value='<%=examples.get(fmr).replace("\n", "\\n")%>';
	}
    <%}%>
}
</script>
</head>
<body onload="messenger()">
	<div class="drop">
		<ul class="drop_menu">
			<li><a href='#'><%=intf.demoName()%> Search Interface</a>
			<li>
			<li><a href='sparql.jsp'>SPARQL</a>
			<li>
			<li><a target="_blank"
				href="http://alt.qcri.org/semeval2015/task18/index.php?id=search">Help</a>
			</li>
		</ul>
	</div>
	<h3>
		Semantic Search Interface -
		<%=intf.demoName()%></h3>
	<div class="centered">
		<form method="post" action="search.jsp">
			<%
				if (id != null) {
			%>
				<input type="hidden" name="id" value="<%=id%>" />
			<%
				}
			%>

			<strong>Query</strong><br />
			<textarea rows="5" cols="100" name="search" id="query-text"><%=query%></textarea>
			<br /> <strong>Format</strong>
			<%
				List<String> formats=intf.formats();
					for (int i=0; i<formats.size(); i++){
						String fs=formats.get(i);
			%>
			&nbsp;<input type="radio" name="annotation" value="<%=fs%>"
				onclick="handleClick(this);"
				<%=annotation.contains(fs) ? "checked" : ""%> /><%=fs.toUpperCase()%>&nbsp;
			<%
				}
			%>
			&nbsp;&nbsp;<strong>Results per Page</strong> <input type="number"
				name=resultsPerPage min="10" step="10" max="100"
				value="<%=resultsPerPage%>" /> <input name="query" type="submit"
				value="search" /><br />
			<%
				if (result != null) {
			%>
			<br />
			<%=result%>
			<%
				}
			%>
		</form>
	</div>
	<div id="navtxt" class="navtext"
		style="position: absolute; top: -100px; left: 0px; visibility: hidden"></div>
</body>
</html>