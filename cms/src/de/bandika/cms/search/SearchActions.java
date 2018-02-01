/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.search;

import de.bandika.cms.application.AdminActions;
import de.bandika.cms.servlet.CmsActions;
import de.bandika.webbase.rights.Right;
import de.bandika.webbase.rights.SystemZone;
import de.bandika.webbase.servlet.ActionSetCache;
import de.bandika.webbase.servlet.RequestReader;
import de.bandika.webbase.servlet.RequestWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchActions extends CmsActions {

    public static final String openSearch="openSearch";
    public static final String search="search";
    public static final String showAdminSearchDetails="showAdminSearchDetails";
    public static final String showSiteSearchDetails="showSiteSearchDetails";
    public static final String showPageSearchDetails="showPageSearchDetails";
    public static final String showFileSearchDetails="showFileSearchDetails";
    public static final String showUserSearchDetails="showUserSearchDetails";
    public static final String indexAllContent="indexAllContent";
    public static final String indexAllUsers="indexAllUsers";

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case openSearch: {
                return showSearch(request, response);
            }
            case search: {
                ContentSearchResultData contentResult = new ContentSearchResultData();
                String pattern = RequestReader.getString(request, "searchPattern");
                contentResult.setPattern(pattern);
                SearchBean.getInstance().searchContent(contentResult);
                request.setAttribute("contentSearchResultData", contentResult);
                UserSearchResultData userResult = new UserSearchResultData();
                userResult.setPattern(pattern);
                SearchBean.getInstance().searchUsers(userResult);
                request.setAttribute("userSearchResultData", userResult);
                return showSearch(request, response);
            }
            case showAdminSearchDetails: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                return showAdminSearchDetails(request, response);
            }
            case showSiteSearchDetails: {
                return showSiteSearchDetails(request, response);
            }
            case showPageSearchDetails: {
                return showPageSearchDetails(request, response);
            }
            case showFileSearchDetails: {
                return showFileSearchDetails(request, response);
            }
            case showUserSearchDetails: {
                return showUserSearchDetails(request, response);
            }
            case indexAllContent: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                SearchQueue.getInstance().addAction(new SearchQueueAction(SearchQueueAction.ACTION_INDEX_ALL_CONTENT, 0, null));
                RequestWriter.setMessageKey(request, "_indexingContentQueued");
                return new AdminActions().openAdministration(request, response);
            }
            case indexAllUsers: {
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                SearchQueue.getInstance().addAction(new SearchQueueAction(SearchQueueAction.ACTION_INDEX_ALL_USERS, 0, null));
                RequestWriter.setMessageKey(request, "_indexingUsersQueued");
                return new AdminActions().openAdministration(request, response);
            }
            default: {
                return showSearch(request, response);
            }
        }
    }

    public static final String KEY = "search";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new SearchActions());
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean showSearch(HttpServletRequest request, HttpServletResponse response) {
        return setJspResponse(request, response, "/WEB-INF/_jsp/search/search.jsp");
    }

    protected boolean showAdminSearchDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/search/searchDetails.ajax.jsp");
    }

    protected boolean showSiteSearchDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/search/siteSearchDetails.ajax.jsp");
    }

    protected boolean showPageSearchDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/search/pageSearchDetails.ajax.jsp");
    }

    protected boolean showFileSearchDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/search/fileSearchDetails.ajax.jsp");
    }

    protected boolean showUserSearchDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/search/userSearchDetails.ajax.jsp");
    }

}
