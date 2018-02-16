/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.page;

import de.bandika.base.data.BaseIdData;
import de.bandika.base.data.XmlData;
import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.StringWriteUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.field.Field;
import de.bandika.cms.field.Fields;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateData;
import de.bandika.webbase.servlet.RequestReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class PagePartData extends BaseIdData implements Comparable<PagePartData>, XmlData {

    protected String name = "";
    protected String sectionName = "";
    protected int ranking = 0;
    protected String templateName;
    protected boolean editable = true;
    protected boolean dynamic = false;
    protected String cssClass = "";
    protected String content = "";
    protected Set<Integer> pageIds = null;

    protected Map<String, Field> fields = new HashMap<>();

    public PagePartData() {
    }

    public void cloneData(PagePartData data) {
        setId(PageBean.getInstance().getNextId());
        setTemplateName(data.getTemplateName());
        setEditable((data.isEditable()));
        setDynamic(data.isDynamic());
        setCssClass(data.getCssClass());
        setContent(data.getContent());
    }

    @Override
    public int compareTo(PagePartData data) {
        int val = ranking - data.ranking;
        if (val != 0) {
            return val;
        }
        return name.compareTo(data.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtmlId() {
        return getSectionName() + "-part-" + getId();
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTemplateData(TemplateData data) {
        setTemplateName(data.getName());
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public Field getField(String name) {
        return fields.get(name);
    }

    public Field ensureField(String name, String fieldType) {
        Field field = fields.get(name);
        if (field == null) {
            field = Fields.getNewField(fieldType);
            assert field != null;
            field.setName(name);
            field.setPagePartId(getId());
            fields.put(name, field);
        }
        return field;
    }

    public void getNodeUsage(Set<Integer> list) {
        for (Field field : fields.values()) {
            field.getNodeUsage(list);
        }
    }

    public void setPageIds(Set<Integer> pageIds) {
        this.pageIds = pageIds;
    }

    public boolean executePagePartMethod(String method, HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    public boolean readPagePartRequestData(HttpServletRequest request) {
        boolean complete = true;
        for (Field field : getFields().values()) {
            complete &= field.readPagePartRequestData(request);
        }
        return complete;
    }

    public void readPagePartSettingsData(HttpServletRequest request) {
        setCssClass(RequestReader.getString(request, "cssClass"));
    }

    public void prepareCopy() {
        setNew(true);
        setId(PageBean.getInstance().getNextId());
    }

    @Override
    public void prepareSave() {
        generateXmlContent();
    }

    /******************* XML part *********************************/

    public void generateXmlContent() {
        Document xmlDoc = XmlUtil.createXmlDocument();
        assert xmlDoc!=null;
        XmlUtil.createRootNode(xmlDoc, "part");
        toXml(xmlDoc, null);
        content = XmlUtil.xmlToString(xmlDoc);
    }

    public void setXmlContent(String content) {
        this.content = content == null ? "" : content;
        evaluateXmlContent();
    }

    public void evaluateXmlContent() {
        if (StringUtil.isNullOrEmpty(content)) {
            return;
        }
        Document doc = XmlUtil.getXmlDocument(content, "UTF-8");
        if (doc == null) {
            return;
        }
        Element root = XmlUtil.getRootNode(doc);
        if (root == null) {
            return;
        }
        fromXml(root);
    }

    public void addXmlAttributes(Document xmlDoc, Element node) {
    }

    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = parentNode == null ? XmlUtil.getRootNode(xmlDoc) : XmlUtil.addNode(xmlDoc, parentNode, "part");
        Element partNode = XmlUtil.addNode(xmlDoc, node, "partContent");
        addXmlAttributes(xmlDoc, partNode);
        for (Field field : fields.values()) {
            field.toXml(xmlDoc, partNode);
        }
        return node;
    }

    public void getXmlAttributes(Element node) {
    }

    public void fromXml(Element node) {
        if (node == null)
            return;
        List<Element> children = XmlUtil.getChildElements(node);
        for (Element child : children) {
            if (child.getTagName().equals("partContent")) {
                fields.clear();
                List<Element> partChildren = XmlUtil.getChildElements(child);
                for (Element partChild : partChildren) {
                    if (partChild.getTagName().equals("field")) {
                        String fieldType = XmlUtil.getStringAttribute(partChild, "fieldType");
                        Field field = Fields.getNewField(fieldType);
                        if (field != null) {
                            field.fromXml(partChild);
                            fields.put(field.getName(), field);
                        }
                    }
                }
            }
        }
    }

    public String getXmlContent() {
        return content;
    }

    /******************* HTML part *********************************/

    public void appendPartHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        if (outputData.pageData.isEditMode())
            appendEditPartHtml(outputContext, outputData);
        else
            appendLivePartHtml(outputContext, outputData);
    }

    public void appendEditPartHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.writer;
        HttpServletRequest request=outputContext.getRequest();
        String sectionType=outputData.attributes.getString("type");
        writeEditPartStart(writer, request, outputData.pageData.getEditPagePart(), getSectionName(), outputData.pageData.getId(), outputData.locale);
        TemplateData partTemplate = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_PART, getTemplateName());
        try {
            outputData.partData=this;
            partTemplate.writeTemplate(outputContext, outputData);
        } catch (Exception e) {
            Log.error("error in part template", e);
        }
        writeEditPartEnd(writer, request, outputData.pageData.getEditPagePart(), getSectionName(), sectionType, outputData.pageData.getId(), outputData.locale);
    }

    public void appendLivePartHtml(PageOutputContext outputContext, PageOutputData outputData)  {
        StringWriteUtil writer=outputContext.writer;
        TemplateData partTemplate = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_PART, getTemplateName());
        try {
            writer.write("<div class=\"pagePart\" id=\"{1}\" >",
                    getHtmlId());
            outputData.partData=this;
            partTemplate.writeTemplate(outputContext, outputData);
            writer.write("</div>");
        } catch (Exception e) {
            Log.error("error in part template", e);
        }
    }

    protected void writeEditPartStart(StringWriteUtil writer, HttpServletRequest request, PagePartData editPagePart, String sectionName, int pageId, Locale locale) throws IOException {
        if (editPagePart == null) {
            // nothing currently edited
            writer.write("<div title=\"{1}(ID={2}) - {3}\" id=\"part_{4}\" class = \"pagePart viewPagePart contextSource\">\n",
                    StringUtil.toHtml(getTemplateName()),
                    String.valueOf(getId()),
                    StringUtil.getHtml("_rightClickEditHint"),
                    String.valueOf(getId()));
        } else if (this == editPagePart) {
            // this one currently edited
            writer.write("<div id=\"part_{1}\" class = \"pagePart editPagePart\">\n" +
                            "<form action = \"/pagepart.srv\" method = \"post\" id = \"partform\" name = \"partform\" accept-charset = \"UTF-8\">\n" +
                            "<input type = \"hidden\" name = \"act\" value = \"savePagePart\"/>\n" +
                            "<input type = \"hidden\" name = \"pageId\" value = \"{2}\"/>\n" +
                            "<input type = \"hidden\" name = \"sectionName\" value = \"{3}\"/>\n" +
                            "<input type = \"hidden\" name = \"partId\" value = \"{4}\"/>\n" +
                            "<div class = \"buttonset editSection\">\n" +
                            "<button class = \"primary icn iok\" onclick = \"evaluateEditFields();return post2EditPageContent('/pagepart.srv',$('#partform').serialize());\">{5}</button>\n" +
                            "<button class=\"icn icancel\" onclick = \"return post2EditPageContent('/pagepart.srv',{act:'cancelEditPagePart',pageId:'{6}'});\">{7}</button>\n" +
                            "</div>",
                    String.valueOf(getId()),
                    String.valueOf(pageId),
                    sectionName,
                    String.valueOf(getId()),
                    getHtml("_ok", locale),
                    String.valueOf(pageId),
                    getHtml("_cancel", locale));
        } else {
            // some other currently edited
            writer.write("<div class = \"pagePart viewPagePart\">\n");
        }
    }

    public void writeEditPartEnd(StringWriteUtil writer, HttpServletRequest request, PagePartData editPagePart, String sectionName, String sectionType, int pageId, Locale locale) throws IOException {
        boolean staticSection = sectionType.equals(SectionData.TYPE_STATIC);
        // end of pagePart div of any kind
        writer.write("</div>");
        if (editPagePart == null) {
            // nothing currently edited
            writer.write("<div class = \"contextMenu\">");
            if (isEditable()) {
                writer.write("<div class=\"icn iedit\" onclick = \"return post2EditPageContent('/pagepart.ajx?',{act:'editPagePart',pageId:'{1}',sectionName:'{2}',partId:'{3}'})\">{4}</div>\n",
                        String.valueOf(pageId),
                        sectionName,
                        String.valueOf(getId()),
                        getHtml("_edit", locale));
            }
            if (!staticSection) {
                appendContextCode(writer, pageId, sectionType, locale);
            }
            writer.write("</div>\n");
        } else if (this == editPagePart) {
            // this one currently edited
            writer.write("</form>\n");
        }
    }

    protected void appendContextCode(StringWriteUtil writer, int pageId, String sectionType, Locale locale) throws IOException {
        writer.write("<div class=\"icn isetting\" onclick = \"return openLayerDialog('{1}', '/pagepart.ajx?act=openEditHtmlPartSettings&pageId={2}&sectionName={3}&partId={4}');\">{5}</div>\n",
                getHtml("_settings", locale),
                String.valueOf(pageId),
                sectionName,
                String.valueOf(getId()),
                getHtml("_settings", locale));
        writer.write("<div class=\"icn inew\" onclick = \"return openLayerDialog('{1}', '/pagepart.ajx?act=openAddPagePart&pageId={2}&sectionName={3}&sectionType={4}&partId={5}');\">{6}</div>\n",
                getHtml("_addPart", locale),
                String.valueOf(pageId),
                sectionName,
                sectionType,
                String.valueOf(getId()),
                getHtml("_newAbove", locale));
        writer.write("<div class=\"icn inew\" onclick = \"return openLayerDialog('{1}', '/pagepart.ajx?act=openAddPagePart&pageId={2}&sectionName={3}&sectionType={4}&partId={5}&below=true');\">{6}</div>\n",
                getHtml("_addPart", locale),
                String.valueOf(pageId),
                sectionName,
                sectionType,
                String.valueOf(getId()),
                getHtml("_newBelow", locale));
        writer.write("<div class=\"icn ishare\" onclick = \"return openLayerDialog('{1}', '/pagepart.ajx?act=openSharePagePart&pageId={2}&sectionName={3}&partId={4}');\">{5}</div>\n",
                getHtml("_share", locale),
                String.valueOf(pageId),
                sectionName,
                String.valueOf(getId()),
                getHtml("_share", locale));
        writer.write("<div class=\"icn iup\" onclick = \"return linkTo('/pagepart.ajx?act=movePagePart&pageId={1}&sectionName={2}&partId={3}&dir=-1');\">{4}</div>\n",
                String.valueOf(pageId),
                sectionName,
                String.valueOf(getId()),
                getHtml("_up", locale));
        writer.write("<div class=\"icn idown\" onclick = \"return linkTo('/pagepart.ajx?act=movePagePart&pageId={1}&sectionName={2}&partId={3}&dir=1');\">{4}</div>\n",
                String.valueOf(pageId),
                sectionName,
                String.valueOf(getId()),
                getHtml("_down", locale));
        writer.write("<div class=\"icn idelete\" onclick = \"return post2EditPageContent('/pagepart.ajx?',{act:'deletePagePart',pageId:'{1}',sectionName:'{2}',partId:'{3}'});\">{4}</div>\n",
                String.valueOf(pageId),
                sectionName,
                String.valueOf(getId()),
                getHtml("_delete", locale));
    }

    public String toHtml(String src) {
        return StringUtil.toHtml(src);
    }

    public String getHtml(String key, Locale locale) {
        return StringUtil.getHtml(key, locale);
    }

}
