/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.team.file;

import de.bandika._base.Bean;
import de.bandika._base.FileData;
import de.bandika.application.Configuration;

import java.sql.*;
import java.util.ArrayList;

public class TeamFileBean extends Bean {

  private static TeamFileBean instance = null;

  public static TeamFileBean getInstance() {
    if (instance == null)
      instance = new TeamFileBean();
    return instance;
  }

  public Connection getConnection() throws SQLException {
    return Configuration.getConnection();
  }

  protected boolean unchanged(Connection con, TeamFileData data) {
    if (data.isBeingCreated())
      return true;
    PreparedStatement pst = null;
    ResultSet rs;
    boolean result = false;
    try {
      pst = con.prepareStatement("select change_date from t_teamfile where id=?");
      pst.setInt(1, data.getId());
      rs = pst.executeQuery();
      if (rs.next()) {
        Timestamp date = rs.getTimestamp(1);
        rs.close();
        result = date.getTime() == data.getChangeDate().getTime();
      }
    } catch (Exception ignored) {
    } finally {
      closeStatement(pst);
    }
    return result;
  }

  public boolean saveFileData(TeamFileData data) {
    Connection con = startTransaction();
    try {
      if (!unchanged(con, data)) {
        rollbackTransaction(con);
        return false;
      }
      data.setChangeDate();
      writeFileData(con, data);
      writeFileVersionData(con, data);
      if (data.isBeingCreated())
        writeInitialFileCurrent(con, data);
      return commitTransaction(con);
    } catch (Exception e) {
      return rollbackTransaction(con, e);
    }
  }

  public boolean checkoutFileData(TeamFileData data) {
    Connection con = startTransaction();
    try {
      if (!unchanged(con, data)) {
        rollbackTransaction(con);
        return false;
      }
      data.setChangeDate();
      writeFileData(con, data);
      writeFileVersionData(con, data);
      updateCheckoutVersion(con, data);
      return commitTransaction(con);
    } catch (Exception e) {
      return rollbackTransaction(con, e);
    }
  }

  public boolean undoCheckoutFileData(TeamFileData data) {
    Connection con = startTransaction();
    try {
      if (!unchanged(con, data)) {
        rollbackTransaction(con);
        return false;
      }
      data.setChangeDate();
      writeFileData(con, data);
      int oldVersion = data.getVersion();
      data.setVersion(0);
      updateCheckoutVersion(con, data);
      deleteFileVersion(con, data.getId(), oldVersion);
      return commitTransaction(con);
    } catch (Exception e) {
      return rollbackTransaction(con, e);
    }
  }

  public boolean checkinFileData(TeamFileData data) {
    Connection con = startTransaction();
    try {
      if (!unchanged(con, data)) {
        rollbackTransaction(con);
        return false;
      }
      data.setChangeDate();
      writeFileData(con, data);
      updateCurrentVersion(con, data);
      return commitTransaction(con);
    } catch (Exception e) {
      return rollbackTransaction(con, e);
    }
  }

  public boolean restoreFileData(TeamFileData data) {
    Connection con = startTransaction();
    try {
      if (!unchanged(con, data)) {
        rollbackTransaction(con);
        return false;
      }
      int newVersion = getMaxVersion(con, data.getId()) + 1;
      data.setVersion(newVersion);
      data.setChangeDate();
      writeFileData(con, data);
      writeFileVersionData(con, data);
      updateCheckoutVersion(con, data);
      return commitTransaction(con);
    } catch (SQLException e) {
      return rollbackTransaction(con, e);
    }
  }

  protected void writeFileData(Connection con, TeamFileData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      String sql = data.isBeingCreated() ? "insert into t_teamfile (change_date,teampart_id,owner_id,owner_name,checkout_id,checkout_name,search_content,id) values (?,?,?,?,?,?,?,?)"
        : "update t_teamfile set change_date=?,teampart_id=?,owner_id=?,owner_name=?,checkout_id=?,checkout_name=?,search_content=? where id=?";
      pst = con.prepareStatement(sql);
      int i = 1;
      pst.setTimestamp(i++, data.getSqlChangeDate());
      pst.setInt(i++, data.getTeamPartId());
      pst.setInt(i++, data.getOwnerId());
      pst.setString(i++, data.getOwnerName());
      if (data.getCheckoutId() == 0)
        pst.setNull(i++, Types.INTEGER);
      else
        pst.setInt(i++, data.getCheckoutId());
      pst.setString(i++, data.getCheckoutName());
      pst.setString(i++, data.getSearchContent());
      pst.setInt(i, data.getId());
      pst.executeUpdate();
    } finally {
      closeStatement(pst);
    }
  }

  protected void writeFileVersionData(Connection con, TeamFileData data) throws SQLException {
    PreparedStatement pst = null;
    boolean insert = true;
    try {
      pst = con.prepareStatement("select 'x' from t_teamfile_version where id=? and version=?");
      pst.setInt(1, data.getId());
      pst.setInt(2, data.getVersion());
      ResultSet rs = pst.executeQuery();
      if (rs.next())
        insert = false;
      rs.close();
      pst.close();
      String sql = insert ? "insert into t_teamfile_version (change_date,file_name,name,description,content_type,size,author_id,author_name,bytes,id,version) values (?,?,?,?,?,?,?,?,?,?,?)" :
        "update t_teamfile_version version set change_date=?,file_name=?,name=?,description=?,content_type=?,size=?,author_id=?,author_name=?,bytes=? where id=? and version=?";
      pst = con.prepareStatement(sql);
      int i = 1;
      pst.setTimestamp(i++, data.getSqlVersionChangeDate());
      pst.setString(i++, data.getFileName());
      pst.setString(i++, data.getName());
      pst.setString(i++, data.getDescription());
      pst.setString(i++, data.getContentType());
      pst.setInt(i++, data.getSize());
      pst.setInt(i++, data.getAuthorId());
      pst.setString(i++, data.getAuthorName());
      pst.setBytes(i++, data.getBytes());
      pst.setInt(i++, data.getId());
      pst.setInt(i, data.getVersion());
      pst.executeUpdate();
    } finally {
      closeStatement(pst);
    }
  }

  protected void writeInitialFileCurrent(Connection con, TeamFileData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      String sql = "insert into t_teamfile_current (change_date,current_version,checkout_version,id) values (?,null,?,?)";
      pst = con.prepareStatement(sql);
      int i = 1;
      pst.setTimestamp(i++, data.getSqlChangeDate());
      pst.setInt(i++, data.getVersion());
      pst.setInt(i, data.getId());
      pst.executeUpdate();
    } finally {
      closeStatement(pst);
    }
  }

  protected void updateCheckoutVersion(Connection con, TeamFileData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      String sql = "update t_teamfile_current set change_date=?,checkout_version=? where id=?";
      pst = con.prepareStatement(sql);
      int i = 1;
      pst.setTimestamp(i++, data.getSqlChangeDate());
      if (data.getVersion() == 0)
        pst.setNull(i++, Types.INTEGER);
      else
        pst.setInt(i++, data.getVersion());
      pst.setInt(i, data.getId());
      pst.executeUpdate();
    } finally {
      closeStatement(pst);
    }
  }

  protected void updateCurrentVersion(Connection con, TeamFileData data) throws SQLException {
    PreparedStatement pst = null;
    try {
      String sql = "update t_teamfile_current set change_date=?,current_version=?,checkout_version=? where id=?";
      pst = con.prepareStatement(sql);
      int i = 1;
      pst.setTimestamp(i++, data.getSqlChangeDate());
      pst.setInt(i++, data.getVersion());
      pst.setNull(i++, Types.INTEGER);
      pst.setInt(i, data.getId());
      pst.executeUpdate();
    } finally {
      closeStatement(pst);
    }
  }

  public TeamFileData getFileDataForUser(int id, int userId) {
    Connection con = null;
    TeamFileData data = new TeamFileData();
    data.setId(id);
    try {
      con = getConnection();
      readFileData(con, data);
      data.setVersion(getFileVersionForUser(con, id, userId, data.getCheckoutId()));
      readFileVersionData(con, data);
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeConnection(con);
    }
    return data;
  }

  public TeamFileData getFileData(int id, int version) {
    Connection con = null;
    TeamFileData data = new TeamFileData();
    data.setId(id);
    data.setVersion(version);
    try {
      con = getConnection();
      readFileData(con, data);
      readFileVersionData(con, data);
    } catch (SQLException se) {
      se.printStackTrace();
    } finally {
      closeConnection(con);
    }
    return data;
  }

  public void readFileData(Connection con, TeamFileData data) throws SQLException {
    PreparedStatement pst = null;
    String sql = "select change_date,teampart_id,owner_id,owner_name,checkout_id,checkout_name from t_teamfile where id=?";
    try {
      pst = con.prepareStatement(sql);
      pst.setInt(1, data.getId());
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data.setChangeDate(rs.getTimestamp(i++));
        data.setTeamPartId(rs.getInt(i++));
        data.setOwnerId(rs.getInt(i++));
        data.setOwnerName(rs.getString(i++));
        data.setCheckoutId(rs.getInt(i++));
        data.setCheckoutName(rs.getString(i));
      }
      rs.close();
    } finally {
      closeStatement(pst);
    }
  }

  public void readFileVersionData(Connection con, TeamFileData data) throws SQLException {
    PreparedStatement pst = null;
    String sql = "select version,change_date,file_name,name,description,content_type,size,author_id,author_name,bytes from t_teamfile_version where id=? and version=?";
    try {
      pst = con.prepareStatement(sql);
      pst.setInt(1, data.getId());
      pst.setInt(2, data.getVersion());
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        int i = 1;
        data.setVersion(rs.getInt(i++));
        data.setVersionChangeDate(rs.getTimestamp(i++));
        data.setFileName(rs.getString(i++));
        data.setName(rs.getString(i++));
        data.setDescription(rs.getString(i++));
        data.setContentType(rs.getString(i++));
        data.setSize(rs.getInt(i++));
        data.setAuthorId(rs.getInt(i++));
        data.setAuthorName(rs.getString(i++));
        data.setBytes(rs.getBytes(i));
      }
      rs.close();
    } finally {
      closeStatement(pst);
    }
  }

  public int getFileVersionForUser(Connection con, int id, int userId, int checkoutId) throws SQLException {
    PreparedStatement pst = null;
    int version = 1;
    try {
      String sql;
      if (userId != 0 && userId == checkoutId)
        sql = "select checkout_version from t_teamfile_current where id=?";
      else
        sql = "select current_version from t_teamfile_current where id=?";
      pst = con.prepareStatement(sql);
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        version = rs.getInt(1);
      }
      rs.close();
    } finally {
      closeStatement(pst);
    }
    return version;
  }

  public int getFileVersionForUser(Connection con, int id, int userId) throws SQLException {
    PreparedStatement pst = null;
    int checkoutId = 0;
    int version = 1;
    try {
      pst = con.prepareStatement("select checkout_version from t_teamfile_current where id=?");
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next())
        checkoutId = rs.getInt(1);
      rs.close();
      pst.close();
      String sql;
      if (userId != 0 && userId == checkoutId)
        sql = "select checkout_version from t_teamfile_current where id=?";
      else
        sql = "select current_version from t_teamfile_current where id=?";
      pst = con.prepareStatement(sql);
      pst.setInt(1, id);
      rs = pst.executeQuery();
      if (rs.next()) {
        version = rs.getInt(1);
      }
      rs.close();
    } finally {
      closeStatement(pst);
    }
    return version;
  }

  public int getMaxVersion(Connection con, int id) throws SQLException {
    PreparedStatement pst = null;
    int version = 0;
    try {
      String sql = "select max(version) from t_teamfile_version where id=?";
      pst = con.prepareStatement(sql);
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        version = rs.getInt(1);
      }
      rs.close();
    } finally {
      closeStatement(pst);
    }
    return version;
  }

  public FileData getFileForUser(int id, int userId) throws SQLException {
    Connection con = null;
    PreparedStatement pst = null;
    FileData data = null;
    String sql = "select file_name,content_type,bytes from t_teamfile_version where id=? and version=?";
    try {
      con = getConnection();
      int version = getFileVersionForUser(con, id, userId);
      pst = con.prepareStatement(sql);
      pst.setInt(1, id);
      pst.setInt(2, version);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        data = new TeamFileData();
        int i = 1;
        data.setFileName(rs.getString(i++));
        data.setContentType(rs.getString(i++));
        data.setBytes(rs.getBytes(i));
      }
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return data;
  }

  public FileData getFile(int id, int version) throws SQLException {
    Connection con = null;
    PreparedStatement pst = null;
    FileData data = null;
    String sql = "select file_name,content_type,bytes from t_teamfile_version where id=? and version=?";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      pst.setInt(1, id);
      pst.setInt(2, version);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        data = new TeamFileData();
        int i = 1;
        data.setFileName(rs.getString(i++));
        data.setContentType(rs.getString(i++));
        data.setBytes(rs.getBytes(i));
      }
      rs.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return data;
  }

  public ArrayList<TeamFileData> getFileList(int teampartId, int userId) {
    ArrayList<TeamFileData> list = new ArrayList<TeamFileData>();
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select t1.id,t1.change_date,t1.teampart_id,t1.owner_id,t1.owner_name,t1.checkout_id,t1.checkout_name," +
        "t2.version,t2.file_name,t2.name,t2.description,t2.content_type,t2.size,t2.author_id,t2.author_name " +
        "from t_teamfile t1,t_teamfile_version t2,t_teamfile_current t3 " +
        "where t1.teampart_id=? and t1.id=t2.id and t1.id=t3.id " +
        "and (((t1.checkout_id is null or t1.checkout_id<>?) and t2.version=t3.current_version)or(t1.checkout_id=? and t2.version=t3.checkout_version)) " +
        "order by t2.name;");
      pst.setInt(1, teampartId);
      pst.setInt(2, userId);
      pst.setInt(3, userId);
      readFileList(con, pst, list);
    } catch (SQLException ignore) {
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  public ArrayList<TeamFileData> getFileHistory(int fileId) {
    ArrayList<TeamFileData> list = new ArrayList<TeamFileData>();
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("select t1.id,t1.change_date,t1.teampart_id,t1.owner_id,t1.owner_name,t1.checkout_id,t1.checkout_name," +
        "t2.version,t2.file_name,t2.name,t2.description,t2.content_type,t2.size,t2.author_id,t2.author_name " +
        "from t_teamfile t1,t_teamfile_version t2 " +
        "where t1.id=? and t2.id=? and not exists(select 'x' from t_teamfile_current t3 where t3.id=? and (t2.version=t3.current_version or t2.version=t3.checkout_version)) " +
        "order by t2.version desc;");
      pst.setInt(1, fileId);
      pst.setInt(2, fileId);
      pst.setInt(3, fileId);
      readFileList(con, pst, list);
    } catch (SQLException ignore) {
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  public void readFileList(Connection con, PreparedStatement pst, ArrayList<TeamFileData> list) throws SQLException {
    int i;
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      i = 1;
      TeamFileData data = new TeamFileData();
      data.setId(rs.getInt(i++));
      data.setChangeDate(rs.getTimestamp(i++));
      data.setTeamPartId(rs.getInt(i++));
      data.setOwnerId(rs.getInt(i++));
      data.setOwnerName(rs.getString(i++));
      data.setCheckoutId(rs.getInt(i++));
      data.setCheckoutName(rs.getString(i++));
      data.setVersion(rs.getInt(i++));
      data.setFileName(rs.getString(i++));
      data.setName(rs.getString(i++));
      data.setDescription(rs.getString(i++));
      data.setContentType(rs.getString(i++));
      data.setSize(rs.getInt(i++));
      data.setAuthorId(rs.getInt(i++));
      data.setAuthorName(rs.getString(i));
      list.add(data);
    }
    rs.close();
  }

  public void deleteHistoryFile(int id, int version) throws SQLException {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("delete from t_teamfile_version where id=? and version=?");
      pst.setInt(1, id);
      pst.setInt(2, version);
      pst.executeUpdate();
      pst.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  public void deleteFile(int id) throws SQLException {
    Connection con = null;
    PreparedStatement pst = null;
    try {
      con = getConnection();
      pst = con.prepareStatement("delete from t_teamfile where id=?");
      pst.setInt(1, id);
      pst.executeUpdate();
      pst.close();
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

  public void deleteFileVersion(Connection con, int id, int version) throws SQLException {
    PreparedStatement pst = null;
    try {
      pst = con.prepareStatement("delete from t_teamfile_version where id=? and version=?");
      pst.setInt(1, id);
      pst.setInt(2, version);
      pst.executeUpdate();
      pst.close();
    } finally {
      closeStatement(pst);
    }
  }

}