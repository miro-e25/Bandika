/*
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tag;

import de.elbe5.base.log.Log;
import de.elbe5.cms.page.*;
import de.elbe5.cms.servlet.ActionSet;
import de.elbe5.cms.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.StringWriter;

public class PageContentTag extends BaseTag {

    @Override
    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            JspWriter writer = getContext().getOut();
            PageData pageData = (PageData) request.getAttribute(ActionSet.KEY_PAGE);
            switch (pageData.getViewMode()){
                case PUBLISH: {
                    writer.write("<div id=\"pageContent\" class=\"viewArea\">");
                    PageContext context = getContext();
                    StringWriter stringWriter = new StringWriter();
                    context.pushBody(stringWriter);
                    context.include(pageData.getInclude());
                    String html = stringWriter.toString();
                    pageData.setPublishedContent(html);
                    pageData.extractSearchContent();
                    context.popBody().write(html);
                    try{
                        PageBean.getInstance().publishPage(pageData);
                    }
                    catch (Exception e){
                        Log.error("error writing published content", e);
                    }
                    pageData.setViewMode(ViewMode.VIEW);
                    writer.write("</div>");
                }break;
                case EDIT: {
                    writer.write("<div id=\"pageContent\" class=\"editArea\">");
                    getContext().include(pageData.getInclude());
                    writer.write("</div>");
                    if (pageData.getEditPagePart() != null) {
                        writer.write("<script>$('.editControl').hide();</script>");
                    } else {
                        writer.write("<script>$('.editControl').show();</script>");
                    }
                }break;
                default: {
                    writer.write("<div id=\"pageContent\" class=\"viewArea\">");
                    if (!pageData.isDynamic() && pageData.isPublished() && !SessionReader.isEditMode(request)) {
                        writer.write(pageData.getPublishedContent());
                    } else{
                        getContext().include(pageData.getInclude());
                    }
                    writer.write("</div>");
                }break;
            }

        } catch (Exception e) {
            Log.error("error writing content", e);
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

}
