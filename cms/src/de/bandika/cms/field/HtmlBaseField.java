/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.field;

import de.bandika.base.util.StringUtil;
import de.bandika.base.util.StringWriteUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.tree.TreeCache;
import de.bandika.cms.tree.TreeNode;
import de.bandika.webbase.servlet.RequestReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public abstract class HtmlBaseField extends Field {

    protected String html = "";

    public void setHtml(String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    public String getHtmlForOutput() {
        return html;
    }

    @Override
    public void getNodeUsage(Set<Integer> list) {
        registerNodesInHtml(html, " href=\"/", list);
        registerNodesInHtml(html, " src=\"/", list);
    }

    /******************* HTML part *********************************/

    @Override
    public boolean readPagePartRequestData(HttpServletRequest request) {
        setHtml(RequestReader.getString(request, getIdentifier()));
        return isComplete();
    }

    public static void registerNodesInHtml(String html, String linkPattern, Set<Integer> list) {
        int start;
        int end = 0;
        while (true) {
            start = html.indexOf(linkPattern, end);
            if (start == -1) {
                break;
            }
            // keep '/'
            start += linkPattern.length() - 1;
            end = html.indexOf('\"', start);
            if (end == -1) {
                break;
            }
            try {
                String url = html.substring(start, end);
                TreeNode node = TreeCache.getInstance().getNode(url);
                if (node != null)
                    list.add(node.getId());
            } catch (Exception ignored) {
            }
            end++;
        }
    }

    protected abstract String getCKCODE();

    @Override
    public void appendFieldHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.writer;
        HttpServletRequest request=outputContext.getRequest();
        boolean partEditMode = outputData.pageData.isEditMode() && outputData.partData == outputData.pageData.getEditPagePart();
        int siteId = outputData.pageData.getParentId();
        int pageId = outputData.pageData.getId();
        String html = getHtml().trim();
        if (partEditMode) {
            writer.write(String.format(getCKCODE(), getIdentifier(), html.isEmpty() ? outputData.content : html, getIdentifier(), StringUtil.toHtml(html), getIdentifier(), siteId, pageId, siteId, pageId));
        } else {
            try {
                if (html.isEmpty()) {
                    writer.write("");
                } else {
                    writer.write(getHtmlForOutput());
                }
            } catch (Exception ignored) {
            }
        }
    }

    /******************* XML part *********************************/

    @Override
    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = super.toXml(xmlDoc, parentNode);
        XmlUtil.addCDATA(xmlDoc, node, html);
        return node;
    }

    @Override
    public void fromXml(Element node) {
        super.fromXml(node);
        html = XmlUtil.getCData(node);
    }

}
