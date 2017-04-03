/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.timer;

import de.bandika.application.AppConfiguration;
import de.bandika.data.Log;

import java.util.Date;

public class ControlTask implements TimerTask {

    public boolean execute(Date executionTime, Date checkTime) {
        Log.info(String.format("control task executing task of %s at %s",
                AppConfiguration.getInstance().getDateTimeFormat(AppConfiguration.getInstance().getStdLocale()).format(executionTime),
                AppConfiguration.getInstance().getDateTimeFormat(AppConfiguration.getInstance().getStdLocale()).format(TimerBean.getInstance().getServerTime())));
        return true;
    }

}