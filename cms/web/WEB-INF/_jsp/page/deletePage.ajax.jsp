<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.servlet.RequestReader" %>
<%@ page import="de.elbe5.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%Locale locale = SessionReader.getSessionLocale(request);
    int pageId = RequestReader.getInt(request, "pageId");%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<div class="info">
    <div class="formText"><%=StringUtil.getHtml("_reallyDeletePage", locale)%>
    </div>
</div>
<div class="buttonset topspace">
    <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
    </button>
    <button class="primary" onclick="post2ModalDialog('/page.ajx', {act: 'delete', pageId: '<%=pageId%>'});"><%=StringUtil.getHtml("_delete", locale)%>
    </button>
</div>
