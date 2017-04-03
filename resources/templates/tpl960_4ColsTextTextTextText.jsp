<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.content.ParagraphData" %>
<%@ page import="de.net25.http.SessionData" %>
<%
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  ParagraphData pdata = (ParagraphData) rdata.getParam("pdata");
  boolean editMode = rdata.getParamBoolean("editMode");
  String[][] fieldDescriptions = {{"text1", "textarea"}, {"text2", "textarea"}, {"text3", "textarea"}, {"text4", "textarea"}};
  pdata.ensureFields(fieldDescriptions);
%>
<tr>
  <td class="c_1column"><%=pdata.getFieldHtml("text1", sdata.getLocale(), editMode)%>
  </td>
  <td>&nbsp;</td>
  <td class="c_1column"><%=pdata.getFieldHtml("text2", sdata.getLocale(), editMode)%>
  </td>
  <td>&nbsp;</td>
  <td class="c_1column"><%=pdata.getFieldHtml("text3", sdata.getLocale(), editMode)%>
  </td>
  <td>&nbsp;</td>
  <td class="c_1column"><%=pdata.getFieldHtml("text4", sdata.getLocale(), editMode)%>
  </td>
</tr>
