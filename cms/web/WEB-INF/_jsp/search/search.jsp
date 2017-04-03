<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.search.SearchData" %>
<%@ page import="de.bandika.search.SearchResultData" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    SearchResultData result = (SearchResultData) request.getAttribute("searchResultData");
%>
<section class="mainSection searchResults">
    <h1><%=StringUtil.getHtml("_searchResults")%>
    </h1>
    <% if (result != null && result.getResults() != null) {%>
    <table class="padded searchResultsTable">
        <tr>
            <th class="col1"><%=StringUtil.getHtml("_type", locale)%>
            </th>
            <th class="col2"><%=StringUtil.getHtml("_name", locale)%>
            </th>
            <th class="col3"><%=StringUtil.getHtml("_description", locale)%>
            </th>
            <th class="col4"><%=StringUtil.getHtml("_content", locale)%>
            </th>
            <th class="col5"><%=StringUtil.getHtml("_relevance", locale)%>
            </th>
        </tr>
        <%for (SearchData data : result.getResults()) {%>
        <tr>
            <td><%=data.getIconSpan()%>
            </td>
            <td><%=data.getNameSpan()%>
            </td>
            <td><%=data.getDescriptionContext()%>
            </td>
            <td><%=data.getContentContext()%>
            </td>
            <td><%=data.getScore()%>
            </td>
        </tr>
        <%}%>
    </table>
    <%}%>
</section>