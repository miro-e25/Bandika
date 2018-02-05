/*
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.tag;

import de.bandika.base.log.Log;
import de.bandika.cms.template.TemplateCache;
import de.bandika.cms.template.TemplateData;

public class SnippetTag extends BaseTag {
    private String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int doStartTag() {
        try {
            TemplateData snippet = TemplateCache.getInstance().getTemplate(TemplateData.TYPE_SNIPPET, name);
            getWriter().print(snippet == null ? "" : snippet.getCode());
        } catch (Exception e) {
            Log.error("could not write snippet tag", e);
        }
        return SKIP_BODY;
    }

}
