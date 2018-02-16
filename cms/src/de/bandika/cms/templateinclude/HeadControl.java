/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templateinclude;

import de.bandika.base.util.StringUtil;
import de.bandika.base.util.StringWriteUtil;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HeadControl extends TemplateInclude {

    public static final String KEY = "head";

    private static HeadControl instance = null;

    public static HeadControl getInstance() {
        if (instance == null)
            instance = new HeadControl();
        return instance;
    }

    public boolean isDynamic(){
        return false;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.writer;
        HttpServletRequest request=outputContext.getRequest();
        if (outputData.pageData==null)
            return;
        writer.write("<title>{1}</title>\n" +
                        "<meta name=\"keywords\" content=\"{2}\">\n",
                StringUtil.getHtml("appTitle", outputData.locale),
                StringUtil.toHtml(outputData.pageData.getKeywords()));
    }

}
