/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tag;

import de.elbe5.base.cache.StringCache;
import de.elbe5.base.log.Log;
import de.elbe5.cms.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import java.util.Locale;

public class ResourceTag extends BaseTag {

    private String key="";

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int doStartTag() {
        try {
            HttpServletRequest request = (HttpServletRequest) getContext().getRequest();
            JspWriter writer = getContext().getOut();
            Locale locale= SessionReader.getSessionLocale(request);

            writer.write(StringCache.getHtml(key, locale));

        } catch (Exception e) {
            Log.error("could not write tag", e);
        }
        return SKIP_BODY;
    }

}

