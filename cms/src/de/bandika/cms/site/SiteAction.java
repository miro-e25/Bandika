/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.site;

import de.bandika.base.data.BaseIdData;
import de.bandika.base.data.BinaryFileData;
import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.page.PageData;
import de.bandika.cms.file.FileBean;
import de.bandika.cms.file.FileData;
import de.bandika.cms.page.PageAction;
import de.bandika.cms.page.PageBean;
import de.bandika.rights.Right;
import de.bandika.rights.RightsCache;
import de.bandika.servlet.*;
import de.bandika.cms.tree.ITreeAction;
import de.bandika.cms.tree.TreeBean;
import de.bandika.cms.tree.TreeCache;
import de.bandika.cms.tree.TreeNodeSortData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public enum SiteAction implements ITreeAction {
    /**
     * no action
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return SiteAction.show.execute(request, response);
        }
    }, /**
     * shows site (default page)
     */
    show {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    //if (!hasContentRight(request,siteId,Right.READ))
                    //    return false;
                    SiteData data;
                    TreeCache tc = TreeCache.getInstance();
                    if (siteId == 0) {
                        String url = request.getRequestURI();
                        if (StringUtil.isNullOrEmpty(url)) {
                            url = "/";
                        }
                        data = tc.getSite(url);
                    } else {
                        data = tc.getSite(siteId);
                    }
                    checkObject(data);
                    request.setAttribute("siteId", Integer.toString(data.getId()));
                    if (data.hasDefaultPage()) {
                        int defaultPageId = data.getDefaultPageId();
                        request.setAttribute("pageId", Integer.toString(defaultPageId));
                        return PageAction.show.execute(request, response);
                    }
                    return showBlankSite(request, response);
                }
            }, /**
     * open dialog for creating a new site
     */
    openCreateSite {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    return showCreateSite(request, response);
                }
            }, /**
     * creates a new site in database
     */
    createSite {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    SiteData data = new SiteData();
                    int parentId = RequestReader.getInt(request, "siteId");
                    SiteBean ts = SiteBean.getInstance();
                    TreeCache tc = TreeCache.getInstance();
                    SiteData parentNode = tc.getSite(parentId);
                    data.readSiteCreateRequestData(request);
                    if (!isDataComplete(data, request)) {
                        return showCreateSite(request, response);
                    }
                    data.setCreateValues(parentNode);
                    data.setRanking(parentNode.getSites().size());
                    data.setOwnerId(SessionReader.getLoginId(request));
                    data.setAuthorName(SessionReader.getLoginName(request));
                    data.setInheritsMaster(true);
                    data.setTemplateName(parentNode.getTemplateName());
                    data.prepareSave();
                    ts.saveSiteSettings(data);
                    if (!data.inheritsRights()) {
                        ts.saveRights(data);
                    }
                    data.setNew(false);
                    String defaultTemplateName = RequestReader.getString(request, "templateName");
                    if (!defaultTemplateName.isEmpty()) {
                        PageBean pageBean = PageBean.getInstance();
                        PageData page = new PageData();
                        page.setCreateValues(data);
                        page.setName("default");
                        page.setDisplayName(data.getDisplayName());
                        page.setRanking(0);
                        page.setAuthorName(data.getAuthorName());
                        page.setTemplateName(defaultTemplateName);
                        page.prepareSave();
                        page.setPublished(false);
                        pageBean.createPage(page, false);
                        page.stopEditing();
                    }
                    TreeCache.getInstance().setDirty();
                    RightsCache.getInstance().setDirty();
                    return closeLayerToTree(request, response, "/tree.srv?act=openTree&siteId=" + data.getId(), "_siteCreated");
                }
            }, /**
     * show site properties
     */
    showSiteDetails {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    return showSiteDetails(request, response);
                }
            }, /**
     * opens dialog for editing site settings
     */
    openEditSiteSettings {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    SiteBean ts = SiteBean.getInstance();
                    SiteData data = ts.getSite(siteId);
                    if (data == null) {
                        RequestError.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request))));
                        return sendForwardResponse(request, response, "/WEB-INF/_jsp/error.inc.jsp");
                    }
                    data.prepareEditing();
                    SessionWriter.setSessionObject(request, "siteData", data);
                    return showEditSiteSettings(request, response);
                }
            }, /**
     * opens dialog for editing site rights
     */
    openEditSiteRights {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    SiteBean ts = SiteBean.getInstance();
                    SiteData data = ts.getSite(siteId);
                    if (data == null) {
                        RequestError.setError(request, new RequestError(StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request))));
                        return sendForwardResponse(request, response, "/WEB-INF/_jsp/error.inc.jsp");
                    }
                    data.prepareEditing();
                    SessionWriter.setSessionObject(request, "siteData", data);
                    return showEditSiteRights(request, response);
                }
            }, /**
     * closes dialog and stops editing site
     */
    stopEditing {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    SessionWriter.removeSessionObject(request, "siteData");
                    return SiteAction.show.execute(request, response);
                }
            }, /**
     * saves site settings to database
     */
    saveSiteSettings {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    SiteData data = (SiteData) getSessionObject(request, "siteData");
                    checkObject(data, siteId);
                    data.readSiteRequestData(request);
                    if (!data.isComplete()) {
                        addError(request, StringUtil.getString("_notComplete", SessionReader.getSessionLocale(request)));
                        return showEditSiteSettings(request, response);
                    }
                    data.prepareSave();
                    SiteBean.getInstance().saveSiteSettings(data);
                    SessionWriter.removeSessionObject(request, "siteData");
                    data.stopEditing();
                    TreeCache.getInstance().setDirty();
                    RightsCache.getInstance().setDirty();
                    return closeLayerToTree(request, response, "/tree.ajx?act=openTree&siteId=" + siteId, "_siteSettingsChanged");
                }
            }, /**
     * saves site rights to database
     */
    saveSiteRights {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    SiteData data = (SiteData) getSessionObject(request, "siteData");
                    checkObject(data, siteId);
                    data.readTreeNodeRightsData(request);
                    SiteBean.getInstance().saveRights(data);
                    SessionWriter.removeSessionObject(request, "siteData");
                    data.stopEditing();
                    TreeCache.getInstance().setDirty();
                    RightsCache.getInstance().setDirty();
                    return closeLayerToTree(request, response, "/tree.ajx?act=openTree", "_siteRightsChanged");
                }
            }, /**
     * publish all subitems
     */
    publishAll {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.APPROVE))
                        return false;
                    SiteData data = TreeCache.getInstance().getSite(siteId);
                    checkObject(data, siteId);
                    List<SiteData> sites = new ArrayList<>();
                    data.getAllSites(sites);
                    for (SiteData site : sites) {
                        for (PageData page : site.getPages()) {
                            if (page.getDraftVersion() > page.getPublishedVersion()) {
                                PageData draft = new PageData(page);
                                draft.copy(page);
                                PageBean.getInstance().loadPageContent(draft, page.getDraftVersion());
                                draft.setAuthorName(SessionReader.getLoginName(request));
                                draft.prepareSave();
                                draft.setPublished(true);
                                PageBean.getInstance().publishPage(draft);
                            }
                        }
                        for (FileData file : site.getFiles()) {
                            if (file.getDraftVersion() > file.getPublishedVersion()) {
                                FileData draft = new FileData();
                                draft.copy(file);
                                FileBean.getInstance().loadFileContent(draft, file.getDraftVersion());
                                draft.setAuthorName(SessionReader.getLoginName(request));
                                draft.prepareSave();
                                draft.setPublished(true);
                                FileBean.getInstance().publishFile(draft);
                            }
                        }
                    }
                    TreeCache.getInstance().setDirty();
                    RightsCache.getInstance().setDirty();
                    return closeLayerToTree(request, response, "/tree.ajx?act=openTree", "_allPublished");
                }
            }, /**
     * inherit all subitems
     */
    inheritAll {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.APPROVE))
                        return false;
                    SiteData data = TreeCache.getInstance().getSite(siteId);
                    boolean anonymous = data.isAnonymous();
                    checkObject(data, siteId);
                    List<SiteData> sites = new ArrayList<>();
                    data.getAllSites(sites);
                    for (SiteData site : sites) {
                        site.setAuthorName(SessionReader.getLoginName(request));
                        site.setAnonymous(anonymous);
                        site.setInheritsRights(true);
                        site.prepareSave();
                        SiteBean.getInstance().saveSiteSettings(site);
                        for (PageData page : site.getPages()) {
                            page.setAuthorName(SessionReader.getLoginName(request));
                            page.setAnonymous(anonymous);
                            page.setInheritsRights(true);
                            page.prepareSave();
                            PageBean.getInstance().savePageSettings(page);
                        }
                        for (FileData file : site.getFiles()) {
                            file.setAuthorName(SessionReader.getLoginName(request));
                            file.setAnonymous(anonymous);
                            file.setInheritsRights(true);
                            file.prepareSave();
                            FileBean.getInstance().saveFileSettings(file);
                        }
                    }
                    TreeCache.getInstance().setDirty();
                    RightsCache.getInstance().setDirty();
                    return closeLayerToTree(request, response, "/tree.ajx?act=openTree", "_allInherited");
                }
            }, /**
     * cuts the site for pasting it somewhere else in the tree
     */
    cutSite {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    SessionWriter.setSessionObject(request, "cutSiteId", siteId);
                    RequestWriter.setMessageKey(request, "_siteCut");
                    return showTree(request, response);
                }
            }, /**
     * pastes a cut site in this site
     */
    pasteSite {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    Integer cutSiteId = (Integer) getSessionObject(request, "cutSiteId");
                    TreeCache tc = TreeCache.getInstance();
                    SiteData cutData = tc.getSite(cutSiteId);
                    SiteData site = tc.getSite(siteId);
                    if (site != null && !site.getParentIds().contains(cutData.getId())) {
                        TreeBean.getInstance().moveTreeNode(cutSiteId, siteId);
                        SessionWriter.removeSessionObject(request, "cutSiteId");
                        TreeCache.getInstance().setDirty();
                        RightsCache.getInstance().setDirty();
                        RequestWriter.setMessageKey(request, "_sitePasted");
                    } else {
                        RequestError.setError(request, new RequestError("_badParent"));
                    }
                    return showTree(request, response);
                }
            }, /**
     * pastes a cut page in this site
     */
    pastePage {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    Integer cutPageId = (Integer) getSessionObject(request, "cutPageId");
                    TreeCache tc = TreeCache.getInstance();
                    PageData cutData = tc.getPage(cutPageId);
                    SiteData site = tc.getSite(siteId);
                    if (site != null && !site.getParentIds().contains(cutData.getId())) {
                        TreeBean.getInstance().moveTreeNode(cutPageId, siteId);
                        SessionWriter.removeSessionObject(request, "cutPageId");
                        TreeCache.getInstance().setDirty();
                        RightsCache.getInstance().setDirty();
                        RequestWriter.setMessageKey(request, "_pagePasted");
                    } else {
                        RequestError.setError(request, new RequestError("_badParent"));
                    }
                    return showTree(request, response);
                }
            }, /**
     * pastes a cut file in this site
     */
    pasteFile {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    Integer cutFileId = (Integer) getSessionObject(request, "cutFileId");
                    TreeCache tc = TreeCache.getInstance();
                    FileData cutData = tc.getFile(cutFileId);
                    SiteData site = tc.getSite(siteId);
                    if (site != null && !site.getParentIds().contains(cutData.getId())) {
                        TreeBean.getInstance().moveTreeNode(cutFileId, siteId);
                        SessionWriter.removeSessionObject(request, "cutFileId");
                        TreeCache.getInstance().setDirty();
                        RightsCache.getInstance().setDirty();
                        RequestWriter.setMessageKey(request, "_filePasted");
                    } else {
                        RequestError.setError(request, new RequestError("_badParent"));
                    }
                    return showTree(request, response);
                }
            }, /**
     * move this site to somewhere else in the tree
     */
    moveSite {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    int parentId = RequestReader.getInt(request, "parentId");
                    TreeCache tc = TreeCache.getInstance();
                    SiteData parent = tc.getSite(parentId);
                    if (parent != null && !parent.getParentIds().contains(siteId)) {
                        TreeBean.getInstance().moveTreeNode(siteId, parentId);
                        TreeCache.getInstance().setDirty();
                        RightsCache.getInstance().setDirty();
                        RequestWriter.setMessageKey(request, "_siteMoved");
                    } else {
                        return false;
                    }
                    return true;
                }
            }, /**
     * open dialog for sorting children manually
     */
    openSortChildren {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    PageBean ts = PageBean.getInstance();
                    TreeNodeSortData sortData = ts.getSortData(siteId);
                    if (sortData.getChildren().size() <= 1) {
                        addError(request, StringUtil.getString("_nothingToSort", SessionReader.getSessionLocale(request)));
                        return SiteAction.show.execute(request, response);
                    }
                    SessionWriter.setSessionObject(request, "sortData", sortData);
                    return showSortChildren(request, response);
                }
            }, /**
     * changes the ranking / sort order of site children
     */
    changeRanking {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    TreeNodeSortData sortData = (TreeNodeSortData) getSessionObject(request, "sortData");
                    if (siteId == 0) {
                        addError(request, StringUtil.getString("_noData", SessionReader.getSessionLocale(request)));
                        return showTree(request, response);
                    }
                    sortData.readSortRequestData(request);
                    return showSortChildren(request, response);
                }
            }, /**
     * saves ranking of newly sorted children to database
     */
    saveSortChildren {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    TreeNodeSortData sortData = (TreeNodeSortData) getSessionObject(request, "sortData");
                    if (siteId == 0) {
                        addError(request, StringUtil.getString("_noData", SessionReader.getSessionLocale(request)));
                        return SiteAction.show.execute(request, response);
                    }
                    PageBean ts = PageBean.getInstance();
                    ts.saveSortData(sortData);
                    TreeCache.getInstance().setDirty();
                    RequestWriter.setMessageKey(request, "_pageOrderSaved");
                    return showTree(request, response);
                }
            }, /**
     * opens dialog for deleting the site
     */
    openDeleteSite {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    if (siteId == 0) {
                        addError(request, StringUtil.getString("_noSelection", SessionReader.getSessionLocale(request)));
                        return SiteAction.show.execute(request, response);
                    }
                    return showDeleteSite(request, response);
                }
            }, /**
     * delete the site including children mailFrom database
     */
    delete {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    if (siteId < BaseIdData.ID_MIN) {
                        addError(request, StringUtil.getString("_notDeletable", SessionReader.getSessionLocale(request)));
                        return showDeleteSite(request, response);
                    }
                    TreeCache tc = TreeCache.getInstance();
                    int parent = tc.getParentNodeId(siteId);
                    SiteBean.getInstance().deleteTreeNode(siteId);
                    TreeCache.getInstance().setDirty();
                    RightsCache.getInstance().setDirty();
                    request.setAttribute("siteId", Integer.toString(parent));
                    return closeLayerToTree(request, response, "/tree.ajx?act=openTree", "_siteDeleted");
                }
            }, /**
     * exports the site's structure and content as xml
     */
    exportToXml {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    Document xmlDoc = XmlUtil.createXmlDocument();
                    Element root = XmlUtil.createRootNode(xmlDoc, "root");
                    SiteData siteData = TreeCache.getInstance().getSite(siteId);
                    siteData.toXml(xmlDoc, root);
                    String xml = XmlUtil.xmlToString(xmlDoc);
                    assert xml != null;
                    return sendBinaryResponse(request, response, "site_" + siteId + ".xml", "text/xml", xml.getBytes(RequestStatics.ENCODING), true);
                }
            }, /**
     * open dialog for creating sub structure mailFrom xml
     */
    openImportFromXml {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    return showImportFromXml(request, response);
                }
            }, /**
     * creates substructure mailFrom xml
     */
    importFromXml {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    int siteId = RequestReader.getInt(request, "siteId");
                    if (!hasContentRight(request, siteId, Right.EDIT))
                        return false;
                    SiteData site = TreeCache.getInstance().getSite(siteId);
                    BinaryFileData file = RequestReader.getFile(request, "file");
                    String xml = null;
                    if (file != null && file.getBytes() != null) {
                        xml = new String(file.getBytes());
                    }
                    if (xml == null)
                        return showImportFromXml(request, response);
                    Document xmlDoc = XmlUtil.getXmlDocument(xml, "UTF-8");
                    Element root = XmlUtil.getRootNode(xmlDoc);
                    try {
                        site.childrenFromXml(root);
                    } catch (ParseException e) {
                        Log.error(e.getMessage());
                        return false;
                    }
                    return closeLayerToTree(request, response, "/tree.ajx?act=openTree&siteId=" + siteId, "_importSucceeded");
                }
            };

    public static final String KEY = "site";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, SiteAction.class);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean showEditSiteSettings(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/editSiteSettings.ajax.jsp");
    }

    protected boolean showBlankSite(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/blankSite.jsp");
    }

    protected boolean showCreateSite(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/createSite.ajax.jsp");
    }

    protected boolean showEditSiteRights(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/editSiteRights.ajax.jsp");
    }

    protected boolean showSortChildren(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/sortSiteChildren.ajax.jsp");
    }

    protected boolean showDeleteSite(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/deleteSite.ajax.jsp");
    }

    public boolean showImportFromXml(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/importFromXml.ajax.jsp");
    }

    protected boolean showSiteDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/site/siteDetails.ajax.jsp");
    }

}