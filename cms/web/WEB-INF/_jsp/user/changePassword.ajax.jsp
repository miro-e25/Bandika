<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.user.UserActions" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._changePassword.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="/user.ajx" name="changepasswordform" act="<%=UserActions.changePassword%>" ajax="true">
            <input type="hidden" name="userId" value="<%=SessionReader.getLoginId(request)%>" />
            <div class="modal-body">
                <cms:message/>
                <cms:password name="oldPassword" label="<%=Strings._oldPassword.toString()%>"></cms:password>
                <cms:password name="newPassword1" label="<%=Strings._newPassword.toString()%>"></cms:password>
                <cms:password name="newPassword2" label="<%=Strings._retypePassword.toString()%>"></cms:password>
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

