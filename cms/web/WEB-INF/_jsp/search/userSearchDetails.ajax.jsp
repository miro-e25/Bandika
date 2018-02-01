<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.user.UserBean" %>
<%@ page import="de.bandika.cms.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    int userId = RequestReader.getInt(request, "userId");
    UserData data = UserBean.getInstance().getUser(userId);
%>
<div class="details">
    <h3><%=StringUtil.getString("_user", locale)%> - <%=StringUtil.getHtml("_details", locale)%>
    </h3>
    <table class="details">
        <tr>
            <td><label><%=StringUtil.getHtml("_id", locale)%>
            </label></td>
            <td><%=data.getId()%>
            </td>
        </tr>
        <tr>
            <td><label><%=StringUtil.getHtml("_firstName", locale)%>
            </label></td>
            <td><%=StringUtil.toHtml(data.getFirstName())%>
            </td>
        </tr>
        <tr>
            <td><label><%=StringUtil.getHtml("_lastName", locale)%>
            </label></td>
            <td><%=StringUtil.toHtml(data.getLastName())%>
            </td>
        </tr>
        <tr>
            <td><label><%=StringUtil.getHtml("_street", locale)%>
            </label></td>
            <td><%=StringUtil.toHtml(data.getStreet())%>
            </td>
        </tr>
        <tr>
            <td><label><%=StringUtil.getHtml("_zipCode", locale)%>
            </label></td>
            <td><%=StringUtil.toHtml(data.getZipCode())%>
            </td>
        </tr>
        <tr>
            <td><label><%=StringUtil.getHtml("_city", locale)%>
            </label></td>
            <td><%=StringUtil.toHtml(data.getCity())%>
            </td>
        </tr>
        <tr>
            <td><label><%=StringUtil.getHtml("_country", locale)%>
            </label></td>
            <td><%=StringUtil.toHtml(data.getCountry())%>
            </td>
        </tr>
        <tr>
            <td><label><%=StringUtil.getHtml("_locale", locale)%>
            </label></td>
            <td><%=StringUtil.toHtml(data.getLocale().getDisplayName(locale))%>
            </td>
        </tr>
        <tr>
            <td><label><%=StringUtil.getHtml("_email", locale)%>
            </label></td>
            <td><%=StringUtil.toHtml(data.getEmail())%>
            </td>
        </tr>
        <tr>
            <td><label><%=StringUtil.getHtml("_phone", locale)%>
            </label></td>
            <td><%=StringUtil.toHtml(data.getPhone())%>
            </td>
        </tr>
        <tr>
            <td><label><%=StringUtil.getHtml("_mobile", locale)%>
            </label></td>
            <td><%=StringUtil.toHtml(data.getMobile())%>
            </td>
        </tr>
    </table>
</div>


