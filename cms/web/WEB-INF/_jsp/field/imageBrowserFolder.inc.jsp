<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>

<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.rights.Right" %>
<%@ page import="de.elbe5.cms.file.FolderData" %>
<%@ page import="de.elbe5.cms.file.FileData" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.servlet.RequestData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata= RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    FolderData folder = (FolderData) rdata.get("folderData");
    assert folder !=null;
%>
<% if (rdata.hasContentRight(folder.getId(), Right.READ)) {%>
<li class="open">
    <a id="folder_<%=folder.getId()%>" ><%=folder.getName()%></a>
    <ul>
    <% if (!folder.getSubFolders().isEmpty() || !folder.getFiles().isEmpty()){%>
        <% for (FileData file : folder.getFiles()){
        %>
        <li>
            <i class="fa <%=file.isImage()?"fa-image" : "fa-file-o"%>">&nbsp;</i>
            <a id="<%=file.getId()%>"><%=file.getName()%></a>
            <a class="fa fa-globe" title="<%=Strings._view.html(locale)%>" href="/file/show/<%=file.getId()%>" target="_blank">
            </a>
            <a class="fa fa-check" title="<%=Strings._select.html(locale)%>" href="" onclick="return ckImgCallback('/file/show/<%=file.getId()%>');">
            </a>
        </li>
        <%}
        for (FolderData subFolder : folder.getSubFolders()){
            rdata.put("folderData",subFolder); %>
        <jsp:include  page="/WEB-INF/_jsp/field/imageBrowserFolder.inc.jsp" flush="true"/>
        <%}
        rdata.put("folderData", folder);%>
    <%}%>
    </ul>
</li>
<%}%>
