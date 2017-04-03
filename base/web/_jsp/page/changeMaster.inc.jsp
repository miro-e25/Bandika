<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.template.TemplateData" %>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.template.TemplateCache" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  PageData data = (PageData) rdata.getParam("pageData");
  ArrayList<TemplateData> masterTemplates = TemplateCache.getInstance().getTemplates("master");
  int id = rdata.getCurrentPageId();
%>
<div class="layerContent">
  <table class="table">
    <colgroup>
      <col width="30%">
      <col width="70%">
    </colgroup>
    <thead>
    <tr>
      <th><%=StringCache.getHtml("name")%>
      </th>
      <th><%=StringCache.getHtml("description")%>
      </th>
    </tr>
    </thead>
    <tbody>
    <% for (TemplateData tdata : masterTemplates) {%>
    <tr <%=tdata.getName().equals(data.getMasterTemplate()) ? "class=\"info\"" : ""%>>
      <td>
        <a href="/_page?method=changeMaster&id=<%=id%>&master=<%=FormatHelper.encode(tdata.getName())%>"><%=FormatHelper.toHtml(tdata.getName())%>
        </a></td>
      <td><%=FormatHelper.toHtml(tdata.getDescription())%>
      </td>
    </tr>
    <%}%>
    </tbody>
  </table>
</div>


