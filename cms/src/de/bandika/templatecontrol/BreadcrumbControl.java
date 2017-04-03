/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.templatecontrol;

import de.bandika.page.PageData;
import de.bandika.template.TemplateAttributes;
import de.bandika.tree.TreeCache;
import de.bandika.tree.TreeNode;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class BreadcrumbControl extends TemplateControl {

    public static String KEY = "breadcrumb";

    private static BreadcrumbControl instance = null;

    public static BreadcrumbControl getInstance() {
        if (instance == null)
            instance = new BreadcrumbControl();
        return instance;
    }

    public void appendHtml(StringBuilder sb, TemplateAttributes attributes, String content, PageData pageData, HttpServletRequest request) {
        TreeCache tc = TreeCache.getInstance();
        List<Integer> activeIds = new ArrayList<>();
        if (pageData != null) {
            activeIds.addAll(pageData.getParentIds());
            activeIds.add(pageData.getId());
        }
        sb.append("<div class=\"breadcrumb flexItemThree\"><ul>");
        for (int i = 1; i < activeIds.size(); i++) {
            TreeNode bcnode = tc.getNode(activeIds.get(i));
            if (bcnode == null || ((bcnode instanceof PageData) && ((PageData) bcnode).isDefaultPage())) {
                continue;
            }
            sb.append("<li><a href=\"").append(bcnode.getUrl()).append("\">").append(toHtml(bcnode.getDisplayName())).append("</a></li>");
        }
        sb.append("</ul></div>");
    }

}