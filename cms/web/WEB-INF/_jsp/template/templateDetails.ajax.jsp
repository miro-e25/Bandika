<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.servlet.RequestReader" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="de.bandika.template.TemplateCache" %>
<%@ page import="de.bandika.template.TemplateData" %>
<%@ page import="de.bandika.template.TemplateType" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    TemplateType templateType = TemplateType.valueOf(RequestReader.getString(request, "templateType"));
    String templateName = RequestReader.getString(request, "templateName");
    TemplateData data = TemplateCache.getInstance().getTemplate(templateType, templateName);
    if (data != null) {
%>
<h3><%=StringUtil.getString("_template", locale)%> - <%=StringUtil.getHtml("_details", locale)%>
</h3>
<table class="padded details">
    <tr>
        <td><label><%=StringUtil.getHtml("_name", locale)%>
        </label></td>
        <td><%=StringUtil.toHtml(data.getName())%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_displayName", locale)%>
        </label></td>
        <td><%=StringUtil.toHtml(data.getDisplayName())%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_description", locale)%>
        </label></td>
        <td><%=StringUtil.toHtml(data.getDescription())%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_usage", locale)%>
        </label></td>
        <td><%=StringUtil.toHtml(data.getUsage())%>
        </td>
    </tr>
</table>
<%}%>

