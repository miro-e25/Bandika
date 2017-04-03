/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.doccenter;

import de.bandika.pagepart.PagePartData;

public class DocCenterPartData extends PagePartData {

    public final static int MODE_LIST = 0;
    public final static int MODE_EDIT = 1;
    public final static int MODE_DELETE = 2;
    public final static int MODE_HISTORY = 3;
    public final static int MODE_HISTORY_DELETE = 4;

    protected String title = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}