<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%
    String redirectUrl = RequestReader.getString(request, "redirectUrl");
    if (redirectUrl.length() > 0) {
%>
<html>
<head></head>
<body style="background: #333 url(/_statics/css/img/carbon.png);">
&nbsp;
</body>
</html>
<script type="text/javascript">
    try {
        window.location.href = '<%=redirectUrl%>';
    } catch (e) {
    }
</script>
<%}%>

