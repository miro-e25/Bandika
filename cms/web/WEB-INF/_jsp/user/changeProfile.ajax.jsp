<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.user.UserBean" %>
<%@ page import="de.elbe5.cms.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.user.UserActions" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    UserData user = UserBean.getInstance().getUser(SessionReader.getSessionLoginData(request).getId());
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._changeProfile.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="/user.ajx" name="changeprofileform" act="<%=UserActions.changeProfile%>" ajax="true" multi="true">
            <input type="hidden" name="userId" value="<%=SessionReader.getLoginId(request)%>" />
            <div class="modal-body">
                <cms:message/>
                <cms:line label="<%=Strings._id.toString()%>"><%=Integer.toString(user.getId())%></cms:line>
                <cms:line label="<%=Strings._login.toString()%>" required="true"><%=StringUtil.toHtml(user.getLogin())%></cms:line>
                <cms:text name="title" label="<%=Strings._title.toString()%>"><%=StringUtil.toHtml(user.getTitle())%></cms:text>
                <cms:text name="firstName" label="<%=Strings._firstName.toString()%>"><%=StringUtil.toHtml(user.getFirstName())%></cms:text>
                <cms:text name="lastName" label="<%=Strings._lastName.toString()%>" required="true"><%=StringUtil.toHtml(user.getLastName())%></cms:text>
                <cms:text name="locale" label="<%=Strings._locale.toString()%>"><%=StringUtil.toHtml(user.getLocale().getLanguage())%></cms:text>
                <cms:textarea name="notes" label="<%=Strings._notes.toString()%>" height="5rem"><%=StringUtil.toHtml(user.getNotes())%></cms:textarea>
                <cms:file name="portrait" label="<%=Strings._portrait.toString()%>"><% if (!user.getPortraitName().isEmpty()) {%><img
                        src="/user.srv?act=<%=UserActions.showPortrait%>&userId=<%=user.getId()%>"
                        alt="<%=StringUtil.toHtml(user.getName())%>"/> <%}%></cms:file>
                <h3><%=Strings._address.html(locale)%></h3>
                <cms:text name="street" label="<%=Strings._street.toString()%>"><%=StringUtil.toHtml(user.getStreet())%></cms:text>
                <cms:text name="zipCode" label="<%=Strings._zipCode.toString()%>"><%=StringUtil.toHtml(user.getZipCode())%></cms:text>
                <cms:text name="city" label="<%=Strings._city.toString()%>"><%=StringUtil.toHtml(user.getCity())%></cms:text>
                <cms:text name="country" label="<%=Strings._country.toString()%>"><%=StringUtil.toHtml(user.getCountry())%></cms:text>
                <h3><%=Strings._contact.html(locale)%></h3>
                <cms:text name="email" label="<%=Strings._email.toString()%>" required="true"><%=StringUtil.toHtml(user.getEmail())%></cms:text>
                <cms:text name="phone" label="<%=Strings._phone.toString()%>"><%=StringUtil.toHtml(user.getPhone())%></cms:text>
                <cms:text name="fax" label="<%=Strings._fax.toString()%>"><%=StringUtil.toHtml(user.getFax())%></cms:text>
                <cms:text name="mobile" label="<%=Strings._mobile.toString()%>"><%=StringUtil.toHtml(user.getMobile())%></cms:text>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary"
                        data-dismiss="modal"><%=Strings._close.html(locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=Strings._save.html(locale)%>
                </button>
            </div>
        </cms:form>
    </div>
</div>

        