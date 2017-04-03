<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.http.RequestData" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.user.UserController" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ page import="de.bandika.base.Bean" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.user.UserData" %>
<%@ page import="de.bandika.base.DataConverter" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  RequestData rdata=HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
<%
  ArrayList<Integer> ids = rdata.getParamIntegerList("uid");
  UserBean bean=(UserBean) Bean.getBean(UserController.KEY_USER);
%>
  <div class="adminTopHeader"><%=AdminStrings.user%></div>
	<div class="hline">&nbsp;</div>
  <bnd:adminTable>
    <tr class="adminHeader">
      <td class="adminMostCol"><%=AdminStrings.reallydeleteuser%></td>
    </tr>
    <% for (int i=0;i<ids.size();i++){
      UserData user=bean.getUser(ids.get(i));%>
    <tr class="<%=i%2==0 ? "adminWhiteLine" : "adminGreyLine"%>">
      <td><%=user.getName()%></td>
    </tr>
    <%}%>
  </bnd:adminTable>
	<div class="hline">&nbsp;</div>
  <div class="adminTableButtonArea">
    <button	onclick="return linkTo('/_jsp/userEditAll.jsp?ctrl=<%=UserController.KEY_USER%>&method=openEditUsers');"><%=AdminStrings.back%></button>
    <button	onclick="return linkTo('/_jsp/userDelete.jsp?ctrl=<%=UserController.KEY_USER%>&method=deleteUser&uid=<%=DataConverter.getIntString(ids)%>');"><%=AdminStrings.delete%></button>
  </div>
</bnd:setMaster>
