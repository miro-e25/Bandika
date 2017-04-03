/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.data;

import java.util.Map;

public interface ILoginData {

    public int getId();

    public String getLogin();

    public String getName();

    public void checkRights();

    public Map<String, IRights> getRights();

    public boolean hasRight(String type);

    public boolean hasRight(String type, int right);

    public boolean hasRight(String type, int id, int right);

}
