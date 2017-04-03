<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.timer.TimerCache" %>
<%@ page import="de.bandika.timer.TimerTaskData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    List<TimerTaskData> tasks = null;
    try {
        TimerCache timerCache = TimerCache.getInstance();
        tasks = timerCache.getTasks();
    } catch (Exception ignore) {
    }
%>
<form class="form-horizontal" action="/timer.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value="openEditTimerTask"/>

    <div class="well">
        <legend><%=StringCache.getHtml("portal_timers",locale)%>
        </legend>
        <bandika:table id="taskTable" checkId="tname" formName="form" headerKeys="portal_name,portal_active">
            <%
                if (tasks != null) {
                    for (TimerTaskData task : tasks) { %>
            <tr>
                <td><input type="checkbox" name="tname" value="<%=task.getName()%>"/></td>
                <td>
                    <a href="/timer.srv?act=openEditTimerTask&tname=<%=StringFormat.encode(task.getName())%>"><%=StringFormat.toHtml(task.getName())%>
                    </a></td>
                <td><%=task.isActive() ? "X" : ""%>
                </td>
            </tr>
            <%
                    }
                }
            %>
        </bandika:table>
    </div>
    <div class="btn-toolbar">
        <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webapp_change",locale)%>
        </button>
    </div>
</form>
