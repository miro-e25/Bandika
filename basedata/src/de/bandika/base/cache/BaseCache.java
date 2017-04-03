/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.base.cache;

import de.bandika.base.event.Event;
import de.bandika.base.event.IEventListener;

public abstract class BaseCache implements IEventListener {

    public static final String LISTENER_TYPE = "cache";

    public static final String EVENT_DIRTY = "dirty";

    protected boolean dirty = true;
    protected final Integer lockObj = 1;

    public void setDirty() {
        dirty = true;
    }

    public void checkDirty() {
        if (dirty) {
            synchronized (lockObj) {
                if (dirty) {
                    load();
                    dirty = false;
                }
            }
        }
    }

    @Override
    public void eventReceived(Event event) {
        if (event.getType().equals(BaseCache.EVENT_DIRTY)) {
            setDirty();
        }
    }

    public abstract void load();
}