/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.servlet;

import java.util.HashMap;
import java.util.Map;

public class ActionSetCache {

    private static final Map<String, ActionSet> actionSets = new HashMap<>();

    public static void addActionSet(String key, ActionSet action) {
        actionSets.put(key, action);
    }

    public static ActionSet getActionSet(String key) {
        if (!actionSets.containsKey(key)) {
            return null;
        }
        return actionSets.get(key);
    }

}
