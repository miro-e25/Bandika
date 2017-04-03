/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika.data.IRights;
import de.bandika.data.IRightsProvider;

import java.util.Set;

public class GeneralRightsProvider implements IRightsProvider {

    public static final String RIGHTS_TYPE_GENERAL = "general";


    @Override
    public String getKey() {
        return RIGHTS_TYPE_GENERAL;
    }

    @Override
    public IRights getRights(Set<Integer> groupIds){
        return GeneralRightsBean.getInstance().getGeneralRightsData(groupIds);
    }

}