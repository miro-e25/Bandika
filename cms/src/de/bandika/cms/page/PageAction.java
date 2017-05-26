/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.page;

import de.bandika.cms.tree.ITreeAction;
import de.bandika.cms.tree.TreeCache;
import de.bandika.rights.Right;
import de.bandika.servlet.ActionDispatcher;
import de.bandika.servlet.RequestReader;
import de.bandika.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public enum PageAction implements ITreeAction {
    /**
     * redirects to show
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return PageAction.show.execute(request, response);
        }
    }, /**
     * shows a page
     */
    show {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                PageData data;
                int pageId = RequestReader.getInt(request, "pageId");
                TreeCache tc = TreeCache.getInstance();
                if (pageId == 0) {
                    String url = request.getRequestURI();
                    data = tc.getPage(url);
                } else {
                    data = tc.getPage(pageId);
                }
                checkObject(data);
                if (!data.isAnonymous() && !SessionReader.hasContentRight(request, pageId, Right.READ)) {
                    return forbidden();
                }
                request.setAttribute("pageId", Integer.toString(data.getId()));
                int pageVersion = data.getVersionForUser(request);
                if (pageVersion == data.getPublishedVersion()) {
                    assert (data.isPublishedLoaded());
                } else {
                    data = PageBean.getInstance().getPage(pageId, pageVersion);
                }
                return setPageResponse(request, response, data);
            }
        }, /**
     * executes a method within the page part
     */
    executePagePartMethod {
            @Override
            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                int pageId = RequestReader.getInt(request, "pageId");
                int partId = RequestReader.getInt(request, "partId");
                String sectionName = RequestReader.getString(request, "sectionName");
                String partMethod = RequestReader.getString(request, "partMethod");
                PageData data = TreeCache.getInstance().getPage(pageId);
                if (!data.isAnonymous() && !SessionReader.hasContentRight(request, pageId, Right.READ)) {
                    return forbidden();
                }
                if (!data.isPublishedLoaded())
                    return false;
                PagePartData pdata = data.getPagePart(sectionName, partId);
                return pdata != null && pdata.executePagePartMethod(partMethod, request, response);
            }
        };

    public static final String KEY = "page";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, PageAction.class);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean setPageResponse(HttpServletRequest request, HttpServletResponse response, PageData data) {
        request.setAttribute("pageData", data);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page.jsp");
    }

}
