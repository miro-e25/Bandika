/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.field;

import de.elbe5.cms.rights.Right;
import de.elbe5.cms.servlet.*;

public class FieldController extends Controller {

    public static final String KEY = "field";

    private static FieldController instance=new FieldController();

    public static FieldController getInstance() {
        return instance;
    }

    @Override
    public String getKey(){
        return KEY;
    }

    public IActionResult openLinkBrowser(RequestData rdata) {
        int pageId = rdata.getInt("pageId");
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        return showLinkBrowserJsp();
    }

    public IActionResult openImageBrowser(RequestData rdata) {
        int pageId = rdata.getInt("pageId");
        if (!rdata.hasContentRight(pageId, Right.EDIT))
            return forbidden(rdata);
        return showImageBrowserJsp();
    }

    private IActionResult showImageBrowserJsp() {
        return new ForwardActionResult("/WEB-INF/_jsp/field/browseImages.jsp");
    }

    private IActionResult showLinkBrowserJsp() {
        return new ForwardActionResult("/WEB-INF/_jsp/field/browseLinks.jsp");
    }

}
