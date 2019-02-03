/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.servlet;

import de.elbe5.cms.application.Statics;

import javax.servlet.http.HttpServletRequest;

public abstract class Message {

    public static void setMessage(HttpServletRequest request, Message error) {
        request.setAttribute(Statics.KEY_MESSAGE, error);
    }

    public static Message getMessage(HttpServletRequest request) {
        return (Message) request.getAttribute(Statics.KEY_MESSAGE);
    }

    // not html encoded
    protected String message="";

    public Message(String message) {
        this.message = message;
    }

    public abstract String getType();

    public abstract String getTypeKey();

    public String getMessage() {
        return message;
    }
}
