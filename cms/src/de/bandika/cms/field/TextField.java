/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.field;

import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.page.PageData;
import de.bandika.cms.pagepart.PagePartData;
import de.bandika.cms.template.TemplateAttributes;
import de.bandika.servlet.RequestReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;

public class TextField extends Field {

    public static String FIELDTYPE_TEXT = "text";

    @Override
    public String getFieldType() {
        return FIELDTYPE_TEXT;
    }

    protected String text = "";

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /******************* HTML part *********************************/

    @Override
    public boolean readPagePartRequestData(HttpServletRequest request) {
        setText(RequestReader.getString(request, getIdentifier()));
        return isComplete();
    }

    @Override
    public void appendFieldHtml(StringBuilder sb, TemplateAttributes attributes, String defaultContent, PagePartData partData, PageData pageData, HttpServletRequest request) {
        boolean partEditMode = pageData.isEditMode() && partData == pageData.getEditPagePart();
        int rows = attributes.getInt("rows");
        if (partEditMode) {
            String content = getText();
            if (content.isEmpty())
                content = defaultContent;
            if (rows > 1)
                sb.append("<textarea class=\"editField\" name=\"").append(getIdentifier()).append("\" rows=\"").append(rows).append("\" >").append(StringUtil.toHtmlInput(content)).append("</textarea>");
            else
                sb.append("<input type=\"text\" class=\"editField\" name=\"").append(getIdentifier()).append("\" value=\"").append(StringUtil.toHtmlInput(content)).append("\" />");
        } else {
            if (getText().length() == 0) {
                sb.append("&nbsp;");
            } else {
                sb.append(StringUtil.toHtmlText(getText()));
            }
        }
    }

    /******************* XML part *********************************/

    @Override
    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = super.toXml(xmlDoc, parentNode);
        XmlUtil.addCDATA(xmlDoc, node, text);
        return node;
    }

    @Override
    public void fromXml(Element node) {
        super.fromXml(node);
        text = XmlUtil.getCData(node);
    }

    /******************* search part *********************************/

    public void appendSearchText(StringBuilder sb) {
        sb.append(" ").append(text);
    }

}