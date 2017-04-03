/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.configuration;

import de.elbe5.base.data.KeyValueMap;
import de.elbe5.base.data.Locales;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Configuration extends KeyValueMap {

    private static final Configuration instance = new Configuration();

    public static Configuration getInstance() {
        return instance;
    }

    public DateFormat getDateFormat(Locale locale) {
        return new SimpleDateFormat(StringUtil.getString("_datepattern", locale));
    }

    public DateFormat getDateTimeFormat(Locale locale) {
        return new SimpleDateFormat(StringUtil.getString("_datetimepattern", locale));
    }

    public String getHtmlDateTime(Date date, Locale locale) {
        return StringUtil.toHtmlDateTime(date, getDateFormat(locale));
    }

    public void loadAppConfiguration() {
        clear();
        putAll(ConfigurationBean.getInstance().getConfiguration());
        ConfigurationBean.getInstance().setLocales(Locales.getInstance().getLocales());
        Locales.getInstance().setDefaultLocale(new Locale(getString("defaultLocale")));
        for (Locale locale : Locales.getInstance().getLocales().keySet()) {
            Log.log("found locale: " + locale.getLanguage() + '(' + locale.getDisplayName() + ')');
        }
        Log.log("default locale is " + Locales.getInstance().getDefaultLocale().getLanguage());
    }
}
