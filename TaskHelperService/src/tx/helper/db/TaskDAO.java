package tx.helper.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tx.commons.util.Util;
import tx.helper.util.HelperUtil;

public class TaskDAO {
	
	private Util util = Util.getInstance();
	
	private DBConnection dbConn = DBConnection.getInstance();
	
	private  static TaskDAO dao = null;
	
	private TaskDAO() {}
	
	public static TaskDAO getInstance() {
		if (dao == null) dao = new TaskDAO();
		return dao;
	}
	
	public List<String> getUserList() {
		
		List<String> userList = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try{
			conn = dbConn.createConn();
			String sql = "SELECT * FROM UserHelper";
			pst = dbConn.createQuery(conn, sql);
			rs = dbConn.executeQuery(pst);
			if(rs != null){
				userList = new ArrayList<String>();
				while(rs.next()){
					userList.add(rs.getString(1));
				}
			}
		}catch(Throwable e){
			util.error(this, e);
		}
		finally {
			if (rs != null){
				try{rs.close();}catch(SQLException e){}
			}
			if (pst != null){
				try{pst.close();}catch(SQLException e){}
			}
			if(conn !=null){
				try {conn.close();} catch(SQLException e){}
			}
		}
		
		return userList;
		
	}
	
	public void addUserItem(String uId,int notifyFlag){
		Connection conn = null;
		PreparedStatement pst = null;
		
		try{
			conn = dbConn.createConn();
			String sql = "insert into UserHelper values (?,?,?)";
			pst = dbConn.createInsertUser(conn, sql, uId, notifyFlag);
			pst.executeUpdate();
			util.trace(this, "Insert userHelper (uId="+uId+";flag="+notifyFlag+") over.");
		}catch(Throwable e){
			util.error(this, "Insert userHelper (uId="+uId+";flag="+notifyFlag+") failure for "+e.getMessage());
			util.error(this, e);
		}
		finally{
			if (pst != null){
				try {pst.close();} catch (SQLException e) {}
			}
			if(conn !=null){
				try {conn.close();} catch(SQLException e){}
			}
		}
	}
	
	public List<String> queryRecordStat(String uId,String startTime,String endTime){
		
		List<String> recordList = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try{
			conn = dbConn.createConn();
			String sql = "SELECT ITEMNAME,SUM(ITEMPRICE) FROM RecordItem WHERE UID = ? AND RDATETIME BETWEEN ? AND ? AND ITEMTYPE = ? GROUP BY ITEMNAME ORDER BY SUM(ITEMPRICE) DESC";
			pst = dbConn.createQuery(conn, sql, uId, startTime, endTime, 0);
			rs = dbConn.executeQuery(pst);
			if(rs != null){
				recordList = new ArrayList<String>();
				while(rs.next()){
					recordList.add(rs.getString(1)+"@@"+rs.getFloat(2));
				}
			}
		}catch(Throwable e){
			util.error(this, e);
		}
		finally {
			if (rs != null){
				try{rs.close();}catch(SQLException e){}
			}
			if (pst != null){
				try{pst.close();}catch(SQLException e){}
			}
			if(conn !=null){
				try {conn.close();} catch(SQLException e){}
			}
		}
		
		return recordList;
		
	}
	
	public boolean addRecordItem(String uId,String itemName,float itemPrice,int itemType){
		
		Connection conn = null;
		PreparedStatement pst = null;
		boolean res = false;
		try{
			
			conn = dbConn.createConn();
			String sql = "insert into RecordItem values (?,?,?,?,?,?)";
			pst = dbConn.createInsertRecord(conn, sql, HelperUtil.geneUUID(), uId, itemName, itemPrice, itemType);
			pst.executeUpdate();
			util.trace(this, "Insert recordItme (uId="+uId+";itemName="+itemName+";itemPrice="+itemPrice+";itemType"+itemType+") over.");
			res = true;
		}catch(Throwable e){
			util.error(this,"Insert recordItem (uId="+uId+";itemName="+itemName+";itemPrice"+itemPrice+") for "+e.getMessage());
			util.error(this, e);
			res = false;
		}
		finally{
			if (pst != null){
				try {pst.close();} catch (SQLException e) {}
			}
			if(conn !=null){
				try {conn.close();} catch(SQLException e){}
			}
		}
		return res;
	}

}
