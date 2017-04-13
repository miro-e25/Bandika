<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.file.FileData" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.pagepart.CkCallbackData" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="de.bandika.site.SiteData" %>
<%@ page import="de.bandika.tree.TreeCache" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    CkCallbackData browseData = (CkCallbackData) SessionReader.getSessionObject(request, "browseData");
    TreeCache tc = TreeCache.getInstance();
    SiteData site = tc.getSite(browseData.getSiteId());
    List<PageData> pages = site.getPages();
    List<FileData> files = site.getFiles();
%>
<table class="padded listTable" id="siteTable">
    <thead>
    <tr>
        <th><%=StringUtil.getHtml("_site", locale)%>
        </th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>
            <a href="#" onclick="<%=String.format("if (CKEDITOR) CKEDITOR.tools.callFunction(%s, '%s'); closeBrowserLayer();", browseData.getCkCallbackNum(), StringUtil.toHtml(site.getUrl()))%>"><%=StringUtil.toHtml(site.getDisplayName())%>
            </a>
        </td>
    </tr>
    </tbody>
</table>
<table class="padded listTable topspace" id="pagesTable">
    <thead>
    <tr>
        <th><%=StringUtil.getHtml("_pages", locale)%>
        </th>
    </tr>
    </thead>
    <tbody>
    <%for (PageData pageData : pages) {%>
    <tr>
        <td>
            <a href="#" onclick="<%=String.format("if (CKEDITOR) CKEDITOR.tools.callFunction(%s, '%s'); closeBrowserLayer();", browseData.getCkCallbackNum(), StringUtil.toHtml(pageData.getUrl()))%>"><%=StringUtil.toHtml(pageData.getDisplayName())%>
            </a>
        </td>
    </tr>
    <%}%>
    </tbody>
</table>
<table class="padded listTable topspace" id="fileTable">
    <thead>
    <tr>
        <th><%=StringUtil.getHtml("_files", locale)%>
        </th>
    </tr>
    </thead>
    <tbody>
    <%for (FileData fileData : files) {%>
    <tr>
        <td>
            <a href="#" onclick="<%=String.format("if (CKEDITOR) CKEDITOR.tools.callFunction(%s, '%s'); closeBrowserLayer();", browseData.getCkCallbackNum(), StringUtil.toHtml(fileData.getUrl()))%>"><%=StringUtil.toHtml(fileData.getDisplayName())%>
            </a>
        </td>
    </tr>
    <%}%>
    </tbody>
</table>
