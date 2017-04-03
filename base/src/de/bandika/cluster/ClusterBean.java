/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cluster;

import de.bandika._base.Bean;
import de.bandika.application.Configuration;

import java.sql.*;
import java.util.ArrayList;

public class ClusterBean extends Bean {

  private static ClusterBean instance = null;

  public static ClusterBean getInstance() {
    if (instance == null)
      instance = new ClusterBean();
    return instance;
  }

  public Connection getConnection() throws SQLException {
    return Configuration.getConnection();
  }

  public ServerData assertSelf(String ownAddress) {
    Connection con = null;
    PreparedStatement pst = null;
    ServerData data = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select port,active,change_date from t_cluster where ipaddress=?");
      pst.setString(1, ownAddress);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data = new ServerData();
        data.setAddress(ownAddress);
        data.setPort(rs.getInt(i++));
        data.setActive(rs.getBoolean(i++));
        data.setChangeDate(rs.getTimestamp(i));
      }
      rs.close();
      if (data == null) {
        pst.close();
        data = new ServerData();
        data.setAddress(ownAddress);
        pst = con.prepareStatement("insert into t_cluster (ipaddress,port,active) values(?,?,?)");
        pst.setString(1, data.getAddress());
        pst.setInt(2, data.getPort());
        pst.setBoolean(3, data.isActive());
        pst.executeUpdate();
      }
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return data;
  }

  public ArrayList<ServerData> getOtherServers(String ownAddress) {
    ArrayList<ServerData> list = new ArrayList<ServerData>();
    Connection con = null;
    PreparedStatement pst = null;
    ServerData data;
    try {
      con = getConnection();
      pst = con.prepareStatement("select ipaddress,port,change_date from t_cluster");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        data = new ServerData();
        data.setAddress(rs.getString(i++));
        data.setPort(rs.getInt(i++));
        data.setActive(true);
        data.setChangeDate(rs.getTimestamp(i));
        if (!data.getAddress().equals(ownAddress))
          list.add(data);
      }
      rs.close();
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  public boolean updatePort(ServerData data) {
    Connection con = null;
    PreparedStatement pst = null;
    int count = 0;
    try {
      con = getConnection();
      pst = con.prepareStatement("update t_cluster set port=?, change_date=now() where ipaddress=?");
      pst.setInt(1, data.getPort());
      pst.setString(2, data.getAddress());
      count = pst.executeUpdate();
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return count != 0;
  }

  public boolean activateServer(String address, boolean flag) {
    Connection con = null;
    PreparedStatement pst = null;
    int count = 0;
    try {
      con = getConnection();
      pst = con.prepareStatement("update t_cluster set active=?, change_date=now() where ipaddress=?");
      pst.setBoolean(1, flag);
      pst.setString(2, address);
      count = pst.executeUpdate();
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return count != 0;
  }

}