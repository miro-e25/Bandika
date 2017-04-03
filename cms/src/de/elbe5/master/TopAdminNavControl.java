/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.master;

import de.elbe5.base.util.StringUtil;
import de.elbe5.page.PageData;
import de.elbe5.pagepart.PagePartData;
import de.elbe5.rights.Right;
import de.elbe5.tree.TreeNode;
import de.elbe5.template.TemplateAttributes;
import de.elbe5.template.TemplateControl;
import de.elbe5.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class TopAdminNavControl extends TemplateControl {

    private static TopAdminNavControl instance = null;

    public static TopAdminNavControl getInstance() {
        if (instance == null)
            instance = new TopAdminNavControl();
        return instance;
    }

    public void appendHtml(StringBuilder sb, TemplateAttributes attributes, String content, PageData pageData, HttpServletRequest request) {
        Locale locale = SessionReader.getSessionLocale(request);
        int siteId = pageData==null ? 0 : pageData.getParentId();
        int pageId = pageData==null ? 0 : pageData.getId();
        boolean editMode = pageData != null && pageData.isEditMode();
        boolean hasAnyEditRight = SessionReader.hasAnyContentRight(request);
        boolean hasEditRight = SessionReader.hasContentRight(request, pageId, Right.EDIT);
        boolean hasAdminRight = SessionReader.hasAnySystemRight(request) || SessionReader.hasContentRight(request, TreeNode.ID_ALL, Right.EDIT);
        boolean hasApproveRight = hasEditRight && SessionReader.hasContentRight(request, pageId, Right.EDIT);
        sb.append("<ul>");
        if (editMode & hasEditRight) {
            sb.append("<li class=\"edit\"><a href=\"/page.srv?act=savePageContent&pageId=").append(pageId).append("\">").append(getHtml("_save", locale)).append("</a></li>");

            if (hasApproveRight) {
                sb.append("<li class=\"edit\"><a href=\"/page.srv?act=savePageContentAndPublish&pageId=").append(pageId).append("\">").append(getHtml("_publish", locale)).append("</a></li>");
            }
            sb.append("<li class=\"edit\"><a href=\"/page.srv?act=stopEditing&pageId=").append(pageId).append("\">").append(getHtml("_cancel", locale)).append("</a></li>");
        } else {
            if (pageId!=0 && hasEditRight) {
                sb.append("<li class=\"admin\"><a href=\"/page.srv?act=openEditPageContent&pageId=").append(pageId).append("\" title=\"").append(getHtml("_editPage", locale)).append("\"><span class=\"icn iedit\"></span></a></li>");
            }
            if (pageId!=0 && hasApproveRight) {
                if (pageData.getDraftVersion() != 0) {
                    sb.append("<li class=\"admin\"><a href=\"/page.srv?act=publishPage&pageId=").append(pageId).append("\" title=\"").append(getHtml("_publish", locale)).append("\"><span class=\"icn ipublish\"></span></a></li>");
                }
            }
            if (hasAnyEditRight) {
                sb.append("<li class=\"admin\"><a href=\"#\" onclick=\"return openTreeLayer('").append(StringUtil.getHtml("_tree")).append("', '").append("/tree.ajx?act=openTree&siteId=").append(siteId)
                        .append("&pageId=").append(pageId).append("');\" title=\"").append(getHtml("_tree", locale)).append("\"><span class=\"icn isite\"></span></a></li>");
            }
            if (hasAdminRight) {
                sb.append("<li class=\"admin\"><a href=\"/admin.srv?act=openAdministration&siteId=").append(siteId).append("&pageId=").append(pageId).append("\" title=\"").append(getHtml("_administration", locale)).append("\"><span class=\"icn isetting\"></span></a></li>");
            }
        }
        sb.append("</ul>");
    }

}
