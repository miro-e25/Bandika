/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templatecontrol;

import de.bandika.base.util.StringUtil;
import de.bandika.cms.file.FileData;
import de.bandika.cms.page.PageData;
import de.bandika.cms.site.SiteData;
import de.bandika.cms.tree.TreeCache;
import de.bandika.webbase.rights.Right;
import de.bandika.webbase.servlet.SessionReader;
import de.bandika.webbase.util.TagAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DocumentListControl extends TemplateControl {

    public static final String KEY = "documents";

    private static DocumentListControl instance = null;

    public static DocumentListControl getInstance() {
        if (instance == null)
            instance = new DocumentListControl();
        return instance;
    }

    public void appendHtml(PageContext context, JspWriter writer, HttpServletRequest request, TagAttributes attributes, String content, PageData pageData) throws IOException {
        if (pageData==null)
            return;
        int siteId = pageData.getParentId();
        SiteData site = TreeCache.getInstance().getSite(siteId);
        Locale locale = SessionReader.getSessionLocale(request);
        List<FileData> files = site.getFiles();
        for (FileData file : files) {
            if (!file.isAnonymous() && !SessionReader.hasContentRight(request, file.getId(), Right.READ))
                continue;
            writer.write("<div class=\"documentListLine icn ifile\"><a href=\"" + file.getUrl() + "\" target=\"_blank\" title=\"" + StringUtil.getHtml("_show", locale) + "\">" + StringUtil.toHtml(file.getDisplayName()) + "</a></div>");
        }
    }

}
