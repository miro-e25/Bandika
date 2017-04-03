/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

import de.net25.base.BaseBean;
import de.net25.resources.statics.Statics;
import sun.misc.BASE64Encoder;

/**
 * Class UserBean is the persistence class for users and groups. <br>
 * Usage:
 */
public class UserBean extends BaseBean {

  /**
   * Method getAllUsers returns the allUsers of this UserBean object.
   *
   * @return the allUsers (type ArrayList<UserData>) of this UserBean object.
   */
  public ArrayList<UserData> getAllUsers() {
    ArrayList<UserData> list = new ArrayList<UserData>();
    Connection con = null;
    PreparedStatement pst = null;
    UserData data;
    try {
      con = getConnection();
      pst = con.prepareStatement("select id,version,name,admin,editor from t_user where deleted=0");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        data = new UserData();
        data.setId(rs.getInt(i++));
        data.setVersion(rs.getInt(i++));
        data.setName(rs.getString(i++));
        data.setAdmin(rs.getInt(i++) == 1);
        data.setEditor(rs.getInt(i++) == 1);
        data.setDeleted(false);
        list.add(data);
      }
      rs.close();
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  /**
   * Method getUser
   *
   * @param id of type int
   * @return UserData
   */
  public UserData getUser(int id) {
    Connection con = null;
    PreparedStatement pst = null;
    UserData data = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select version,login,name,email,admin,editor,deleted from t_user where id=?");
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data = new UserData();
        data.setId(id);
        data.setVersion(rs.getInt(i++));
        data.setLogin(rs.getString(i++));
        data.setPassword(null);
        data.setName(rs.getString(i++));
        data.setEmail(rs.getString(i++));
        data.setAdmin(rs.getInt(i++) == 1);
        data.setEditor(rs.getInt(i++) == 1);
        data.setDeleted(rs.getInt(i++) == 1);
        readUserGroups(con, data);
        readUserRights(data);
      }
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return data;
  }

  /**
   * Method getUser
   *
   * @param login of type String
   * @param pwd   of type String
   * @return UserData
   */
  public UserData getUser(String login, String pwd) {
    Connection con = null;
    PreparedStatement pst = null;
    UserData data = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select id,version,name,email,admin,editor from t_user where login=? and pwd=? and deleted=0");
      pst.setString(1, login);
      pst.setString(2, encryptPassword(pwd));
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data = new UserData();
        data.setId(rs.getInt(i++));
        data.setVersion(rs.getInt(i++));
        data.setLogin(login);
        data.setPassword(null);
        data.setName(rs.getString(i++));
        data.setEmail(rs.getString(i++));
        data.setAdmin(rs.getInt(i++) != 0);
        data.setEditor(rs.getInt(i++) != 0);
        data.setDeleted(false);
        readUserGroups(con, data);
        readUserRights(data);
      }
      rs.close();
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return data;
  }

  /**
   * Method readUserGroups
   *
   * @param con  of type Connection
   * @param data of type UserData
   * @throws SQLException when data processing is not successful
   */
  protected void readUserGroups(Connection con, UserData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("select group_id from t_user2group where user_id=?");
      pst.setInt(1, data.getId());
      ResultSet rs = pst.executeQuery();
      data.getGroupIds().clear();
      while (rs.next()) {
        data.getGroupIds().add(rs.getInt(1));
      }
      rs.close();
    }
    finally {
      closeStatement(pst);
    }
  }

  /**
   * Method readUserRights
   *
   * @param data of type UserData
   */
  protected void readUserRights(UserData data) {
    RightBean bean = (RightBean) Statics.getBean(Statics.KEY_RIGHT);
    data.setUserRights(bean.getUserRights(data.getId()));
  }

  /**
   * Method saveUser
   *
   * @param data of type UserData
   * @return boolean
   */
  public boolean saveUser(UserData data) {
    Connection con = startTransaction();
    try {
      if (!isOfCurrentVersion(con, data, "t_user")) {
        rollbackTransaction(con);
        return false;
      }
      data.increaseVersion();
      writeUser(con, data);
      saveUserRights(con, data);
      return commitTransaction(con);
    }
    catch (Exception se) {
      return rollbackTransaction(con, se);
    }
  }

  /**
   * Method writeUser
   *
   * @param con  of type Connection
   * @param data of type UserData
   * @throws SQLException when data processing is not successful
   */
  protected void writeUser(Connection con, UserData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement(data.isBeingCreated() ?
          "insert into t_user (version,login,pwd,name,email,admin,editor,deleted,id) values(?,?,?,?,?,?,?,?,?)" :
          data.getPassword().length() == 0 ?
              "update t_user set version=?,login=?,name=?,email=?,admin=?,editor=?,deleted=? where id=?"
              :
              "update t_user set version=?,login=?,pwd=?,name=?,email=?,admin=?,editor=?,deleted=? where id=?");
      int i = 1;
      pst.setInt(i++, data.getVersion());
      pst.setString(i++, data.getLogin());
      if (data.getPassword().length() > 0)
        pst.setString(i++, encryptPassword(data.getPassword()));
      pst.setString(i++, data.getName());
      pst.setString(i++, data.getEmail());
      pst.setInt(i++, data.isAdmin() ? 1 : 0);
      pst.setInt(i++, data.isEditor() ? 1 : 0);
      pst.setInt(i++, data.isDeleted() ? 1 : 0);
      pst.setInt(i++, data.getId());
      pst.executeUpdate();
      pst.close();
      pst = con.prepareStatement("delete from t_user2group where user_id=?");
      pst.setInt(1, data.getId());
      pst.execute();
      if (data.getGroupIds() != null) {
        pst.close();
        pst = con.prepareStatement("insert into t_user2group (user_id,group_id) values(?,?)");
        pst.setInt(1, data.getId());
        for (int gid : data.getGroupIds()) {
          pst.setInt(2, gid);
          pst.executeUpdate();
        }
      }
    }
    finally {
      closeStatement(pst);
    }
  }

  /**
   * Method saveUserRights
   *
   * @param con  of type Connection
   * @param data of type UserData
   * @throws SQLException when data processing is not successful
   */
  protected void saveUserRights(Connection con, UserData data) throws SQLException {
    RightBean bean = (RightBean) Statics.getBean(Statics.KEY_RIGHT);
    bean.setUserRights(con, data.getId(), data.getUserRights());
  }

  /**
   * Method deleteUser
   *
   * @param id of type int
   */
  public void deleteUser(int id) {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("update t_user set deleted=1 where id=?");
      pst.setInt(1, id);
      pst.executeUpdate();
      pst.close();
      pst = con.prepareStatement("delete from t_user2group where user_id=?");
      pst.setInt(1, id);
      pst.executeUpdate();
      deleteUserRights(con, id);
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  /**
   * Method deleteUserRights ...
   *
   * @param con of type Connection
   * @param id  of type int
   * @throws SQLException when
   */
  protected void deleteUserRights(Connection con, int id) throws SQLException {
    RightBean bean = (RightBean) Statics.getBean(Statics.KEY_RIGHT);
    bean.deleteUserRights(con, id);
  }

  /**
   * Method encryptPassword
   *
   * @param pwd of type String
   * @return String
   */
  public String encryptPassword(String pwd) {
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("SHA");
    }
    catch (NoSuchAlgorithmException e) {
      return null;
    }
    try {
      md.update(pwd.getBytes("UTF-8"));
    }
    catch (UnsupportedEncodingException e) {
      return null;
    }
    byte raw[] = md.digest();
    return (new BASE64Encoder()).encode(raw);
  }

  ////////////////// groups /////////////////////////

  /**
   * Method getAllGroups returns the allGroups of this UserBean object.
   *
   * @return the allGroups (type ArrayList<GroupData>) of this UserBean object.
   */
  public ArrayList<GroupData> getAllGroups() {
    ArrayList<GroupData> list = new ArrayList<GroupData>();
    Connection con = null;
    PreparedStatement pst = null;
    GroupData data;
    try {
      con = getConnection();
      pst = con.prepareStatement("select id,version,name from t_group");
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        data = new GroupData();
        data.setId(rs.getInt(i++));
        data.setVersion(rs.getInt(i++));
        data.setName(rs.getString(i++));
        list.add(data);
      }
      rs.close();
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  /**
   * Method getGroup
   *
   * @param id of type int
   * @return GroupData
   */
  public GroupData getGroup(int id) {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    GroupData data = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select version,name from t_group where id=? order by name");
      pst.setInt(1, id);
      rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data = new GroupData();
        data.setId(id);
        data.setVersion(rs.getInt(i++));
        data.setName(rs.getString(i++));
        rs.close();
        pst.close();
        pst = con.prepareStatement("select user_id from t_user2group where group_id=?");
        pst.setInt(1, id);
        rs = pst.executeQuery();
        HashSet<Integer> set = new HashSet<Integer>();
        while (rs.next()) {
          set.add(rs.getInt(1));
        }
        data.setUserIds(set);
        readGroupRights(con, data);
      }
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeAll(rs, pst, con);
    }
    return data;
  }

  /**
   * Method readGroupRights
   *
   * @param con  of type Connection
   * @param data of type GroupData
   * @throws SQLException when data processing is not successful
   */
  protected void readGroupRights(Connection con, GroupData data) throws SQLException {
    RightBean bean = (RightBean) Statics.getBean(Statics.KEY_RIGHT);
    data.setGroupRights(bean.getGroupRights(con, data.getId()));
  }

  /**
   * Method saveGroup
   *
   * @param data of type GroupData
   * @return boolean
   */
  public boolean saveGroup(GroupData data) {
    Connection con = startTransaction();
    try {
      if (!isOfCurrentVersion(con, data, "t_group")) {
        rollbackTransaction(con);
        return false;
      }
      data.increaseVersion();
      writeGroup(con, data);
      saveGroupRights(con, data);
      return commitTransaction(con);
    }
    catch (Exception se) {
      return rollbackTransaction(con, se);
    }
  }

  /**
   * Method writeGroup
   *
   * @param con  of type Connection
   * @param data of type GroupData
   * @throws SQLException when data processing is not successful
   */
  protected void writeGroup(Connection con, GroupData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement(data.isBeingCreated() ?
          "insert into t_group (version,name,id) values(?,?,?)" :
          "update t_group set version=?,name=? where id=?");
      int i = 1;
      pst.setInt(i++, data.getVersion());
      pst.setString(i++, data.getName());
      pst.setInt(i++, data.getId());
      pst.executeUpdate();
      pst.close();
      pst = con.prepareStatement("delete from t_user2group where group_id=?");
      pst.setInt(1, data.getId());
      pst.execute();
      if (data.getUserIds() != null) {
        pst.close();
        pst = con.prepareStatement("insert into t_user2group (group_id,user_id) values(?,?)");
        pst.setInt(1, data.getId());
        for (int uid : data.getUserIds()) {
          pst.setInt(2, uid);
          pst.executeUpdate();
        }
      }
    }
    finally {
      closeStatement(pst);
    }
  }

  /**
   * Method saveGroupRights
   *
   * @param con  of type Connection
   * @param data of type GroupData
   * @throws SQLException when data processing is not successful
   */
  protected void saveGroupRights(Connection con, GroupData data) throws SQLException {
    RightBean bean = (RightBean) Statics.getBean(Statics.KEY_RIGHT);
    bean.setGroupRights(con, data.getId(), data.getGroupRights());
  }

  /**
   * Method deleteGroup
   *
   * @param id of type int
   */
  public void deleteGroup(int id) {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("delete from t_group where id=?");
      pst.setInt(1, id);
      pst.executeUpdate();
    }
    catch (SQLException se) {
      se.printStackTrace();
    }
    finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }


}
