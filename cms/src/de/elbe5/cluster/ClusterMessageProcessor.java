/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cluster;

import de.elbe5.base.event.IEventListener;
import de.elbe5.base.log.Log;
import de.elbe5.base.port.IPortObjectProcessor;

import java.util.HashMap;
import java.util.Map;

public class ClusterMessageProcessor implements IPortObjectProcessor {

    private static ClusterMessageProcessor instance = null;

    public static ClusterMessageProcessor getInstance() {
        if (instance == null) {
            instance = new ClusterMessageProcessor();
            instance.initialize();
        }
        return instance;
    }

    Map<String, IEventListener> listeners = null;

    public void initialize() {
        listeners = new HashMap<>();
    }

    public void putListener(String messageKey, IEventListener listener) {
        listeners.put(messageKey, listener);
    }

    public Object processObject(Object obj) {
        if (obj == null || !(obj instanceof ClusterMessage))
            return null;
        ClusterMessage msg = (ClusterMessage) obj;
        processMessage(msg);
        return msg;
    }

    protected boolean processMessage(ClusterMessage msg) {
        ClusterManager controller = ClusterManager.getInstance();
        msg.setResponder(controller.getSelf().getAddress());
        switch (msg.getMessageType()) {
            case ClusterMessage.MSGTYPE_CLUSTER: {
                return controller.processClusterMessage(msg);
            }
            case ClusterMessage.MSGTYPE_LISTENER: {
                msg.setResponder(controller.getSelf().getAddress());
                String messageKey = msg.getMessageKey();
                if (listeners.containsKey(messageKey)) {
                    Log.info(String.format("dispatching cluster message to listener: %s", listeners.get(messageKey)));
                    //listeners.get(messageKey).eventReceived(new Event(messageKey, msg.getAction(), null, msg.getId(), false);
                }
                return true;
            }
            default:
                return false;
        }
    }

}