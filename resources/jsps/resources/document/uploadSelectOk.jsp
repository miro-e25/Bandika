<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.net25.http.SessionData" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.resources.document.DocumentData" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  DocumentData doc = (DocumentData) sdata.getParam("document");
%>
<script src="<%=Statics.JS_PATH%>document.js" type="text/javascript"></script>
<div class="hline">&nbsp;</div>
<div class="admin">
  <div class="adminLine">
    <%=doc.getName()%>
  </div>
  <div class="adminLine">
    <%=Strings.getHtml("documentUploaded", sdata.getLocale())%>
  </div>
</div>
<div class="hline">&nbsp;</div>
<ul class="adminButtonList">
  <li class="adminButton"><a href="#"
                             onclick="opener.DocumentUploadCallback('<%=doc.getId()%>');window.close();"><%=Strings.getHtml("close", sdata.getLocale())%>
  </a></li>
</ul>
