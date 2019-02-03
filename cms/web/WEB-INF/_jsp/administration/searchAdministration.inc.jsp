<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.search.SearchActions" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
%>

                            <li class="open">
                                <span class="dropdown-toggle" data-toggle="dropdown"><%=Strings._search.html(locale)%></span>
                                <div class="dropdown-menu">
                                    <a class="dropdown-item" href="/search.srv?act=<%=SearchActions.indexAllContent%>"><%=Strings._indexAllContent.html(locale)%></a>
                                    <a class="dropdown-item" href="/search.srv?act=<%=SearchActions.indexAllUsers%>"><%=Strings._indexAllUsers.html(locale)%></a>
                                </div>
                            </li>
