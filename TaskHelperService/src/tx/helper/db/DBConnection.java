package tx.helper.db;

import java.net.URLEncoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.dbcp.BasicDataSource;

import tx.commons.util.Util;
import tx.helper.model.BrandName;
import tx.helper.model.CallInfo;
import tx.helper.model.CallStats;
import tx.helper.model.CustomerInfo;
import tx.helper.model.EmployeeInfo;
import tx.helper.model.NewCallInfo;
import tx.helper.model.UserLog;

public class DBConnection {
	
	private Util util = Util.getInstance();
	
	private static DBConnection instance;
	
	Logger logger = Logger. getLogger("asdf");
	
	private boolean bConn = false;
	
	private DBConnection() {
		this.initDS();
	}
	
	BasicDataSource ds;
	Connection conn;
	
	public static DBConnection getInstance(){
		if(instance == null) instance = new DBConnection();
		return instance;
	}
	
	public Connection createConn() throws Throwable {
		if ( bConn == false )
		{
			try{conn = DriverManager.getConnection(Config.dbURL, Config.dbUser, Config.dbPwd);}
			catch(Exception e)
			{
				util.warn(this, "Create Connection failure." + e.getMessage());
				logger.log(Level.INFO, "Create Connection failure." + e.getMessage());
				return conn;
			}
			util.warn(this, "Establish DB Connection suc.");
			logger.log(Level.INFO, "Establish DB Connection suc.");			
			setConnectionState(true);
		}
		return conn;
/*		logger.log(Level.INFO, "create conn 1.");
		if(ds != null){
			Connection conn = null;
			logger.log(Level.INFO, "create conn ds is not null.");
			try{conn = ds.getConnection();}
			catch(SQLException e)
			{
				logger.log(Level.INFO, "create conn  getConnection throw exception." + e.getMessage());
				return conn;
			}
			return conn;
		}else{
			util.warn(this, "Create DBConn failure for ds is null.");
			logger.log(Level.INFO, "Create DBConn failure for ds is null.");
		}
		return null;*/
		
	}
	
	public void executeUpdate(PreparedStatement pst) throws Throwable {
		if (pst != null){
			pst.executeUpdate();
		}
	}
	
	public ResultSet executeQuery(PreparedStatement pst) throws Throwable{
		
		if(pst != null){
			ResultSet rs = pst.executeQuery();
			return rs;
		}else{
			util.warn(this, "ExecuteQuery failure for preparedStatement is null.");
		}
		return null;
	}
	
	public boolean executeSPWXBindings(Connection conn, String openID, String identityID, String loginID, String loginPass) throws Throwable{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_bindings(?,?,?,?,?)}");
		callableStmt.setString(1, openID);
		callableStmt.setString(2, identityID);
		callableStmt.setString(3, loginID);
		callableStmt.setString(4, loginPass);
		callableStmt.registerOutParameter(5,java.sql.Types.INTEGER);
		callableStmt.execute();
		int nRet = callableStmt.getInt(5);
		System.out.println ("return of sp_wx_bindings:" + nRet);
		logger.log(Level.INFO, "return of sp_wx_bindings:" + nRet);
		if ( nRet == 0 )
			return true;
		else
			return false;
	}
	
	public String executeSPWXLoginLogoutLog(Connection conn, String openid, String loginmode, String logintime, String location) throws Throwable{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_loginlogout(?,?,?,?,?)}");
		callableStmt.setString(1, openid);
		callableStmt.setString(2, loginmode);
		callableStmt.setString(3, logintime);
		callableStmt.setString(4, location);
		callableStmt.registerOutParameter(5,java.sql.Types.NCHAR);
		callableStmt.execute();
		String strRet = callableStmt.getString(5);
		System.out.println ("return of sp_wx_loginlogout:" + strRet);
		logger.log(Level.INFO, "return of sp_wx_loginlogout:" + strRet);
		return strRet;
	}
	
	public String executeSPWXQueryLog(Connection conn, String openid, String startDate, String endDate, Collection<UserLog> userLogList) throws Throwable{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_queryloginlog(?,?,?,?)}");
		callableStmt.setString(1, openid);
		callableStmt.setString(2, startDate);
		callableStmt.setString(3, endDate);
		callableStmt.registerOutParameter(4,java.sql.Types.NCHAR);
		callableStmt.execute();
		callableStmt.getMoreResults();
		ResultSet rs = callableStmt.getResultSet();
		if ( rs != null )
		{
			while ( rs.next() )
			{
				UserLog userLog = new UserLog();
				userLog.setLogDate(rs.getString(1));
				userLog.setLoginTime(rs.getString(2));
				userLog.setLoginLocation(rs.getString(3));
				userLog.setLogoutTime(rs.getString(4));
				userLog.setLogoutLocation(rs.getString(5));			
				userLogList.add(userLog);
			}
		}
		String strRet = callableStmt.getString(4);
		System.out.println ("return of sp_wx_queryloginlog:" + strRet);
		logger.log(Level.INFO, "return of sp_wx_queryloginlog:" + strRet);		
		return strRet;
	}	
	
	public String executeSPWXQueryEmployeeInfo(Connection conn, String openid, EmployeeInfo employeeInfo) throws Throwable{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_queryemployeeinfo(?,?)}");
		callableStmt.setString(1, openid);
		callableStmt.registerOutParameter(2,java.sql.Types.NCHAR);
		callableStmt.execute();
		callableStmt.getMoreResults();
		ResultSet rs = callableStmt.getResultSet();
		employeeInfo.setEmployeeName("");
		employeeInfo.setEmployeePosition("");
		if ( rs != null ){
			while ( rs.next() )
			{
				String resMsg = URLEncoder.encode(rs.getString(1), "utf-8");
				employeeInfo.setEmployeeName(resMsg);
				resMsg = URLEncoder.encode(rs.getString(2), "utf-8");
				employeeInfo.setEmployeePosition(resMsg);
				break;
			}
		}
		String strRet = callableStmt.getString(2);
		System.out.println ("return of sp_wx_queryemployeeinfo:" + strRet);
		logger.log(Level.INFO, "return of sp_wx_queryemployeeinfo:" + strRet);		
		return strRet;
	}

	public String executeSPWXQueryCustomers(Connection conn, String openid, Collection<CustomerInfo> customerList) throws Throwable{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_querycustomers(?,?)}");
		callableStmt.setString(1, openid);
		callableStmt.registerOutParameter(2,java.sql.Types.NCHAR);
		callableStmt.execute();
		callableStmt.getMoreResults();
		ResultSet rs = callableStmt.getResultSet();
		if ( rs != null )
		{
			while ( rs.next() )
			{
				CustomerInfo customer = new CustomerInfo();
				customer.setName(URLEncoder.encode(rs.getString(1), "utf-8"));
				customerList.add(customer);
				logger.log(Level.INFO, "return of sp_wx_querycustomer  name:" + rs.getString(1));
			}
		}
		String strRet = callableStmt.getString(2);
		System.out.println ("return of sp_wx_querycustomers:" + strRet);
		logger.log(Level.INFO, "return of sp_wx_querycustomers:" + strRet);		
		return strRet;
	}
	
	public String executeSPWXQueryBrands(Connection conn, String openid, String customername, Collection<BrandName> brandNameList) throws Throwable{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_querybrands(?,?,?)}");
		callableStmt.setString(1, openid);
		callableStmt.setString(2, customername);
		callableStmt.registerOutParameter(3,java.sql.Types.NCHAR);
		callableStmt.execute();
		callableStmt.getMoreResults();
		ResultSet rs = callableStmt.getResultSet();
		if ( rs != null )
		{
			while ( rs.next() )
			{
				BrandName brandName = new BrandName();
				brandName.setName(URLEncoder.encode(rs.getString(1), "utf-8"));
				brandNameList.add(brandName);
			}
		}
		String strRet = callableStmt.getString(3);
		System.out.println ("return of sp_wx_querybrands:" + strRet);
		logger.log(Level.INFO, "return of sp_wx_querybrands:" + strRet);		
		return strRet;
	}

	public String executeSPWXQueryCalls(Connection conn, String openid, String customername, String brandname, String state, String groupflag, Collection<CallInfo> callList) throws Throwable{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_querycalls(?,?,?,?,?,?)}");
		callableStmt.setString(1, openid);
		callableStmt.setString(2, customername);
		callableStmt.setString(3, brandname);
		callableStmt.setString(4, state);
		callableStmt.setString(5, groupflag);
		callableStmt.registerOutParameter(6,java.sql.Types.NCHAR);
		callableStmt.execute();
		ResultSet rs = callableStmt.getResultSet();
		if ( rs != null )
		{
			while ( rs.next() )
			{
				CallInfo callInfo = new CallInfo();
				callInfo.setCallidentityid(rs.getString(1));
				callInfo.setCallTitle(rs.getString(2));
				callInfo.setCustomername(URLEncoder.encode(rs.getString(3), "utf-8"));
				callInfo.setBrandname(URLEncoder.encode(rs.getString(4), "utf-8"));
				callInfo.setDesc(URLEncoder.encode(rs.getString(5), "utf-8"));
				callInfo.setAddress(URLEncoder.encode(rs.getString(6), "utf-8"));
				String state_ret = rs.getString(7);
				if ( state_ret.equals("2-6") )
					state_ret = "wait";
				else if ( state_ret.equals("2-7") )
					state_ret = "wait";				
				else if ( state_ret.equals("2-12") )
					state_ret = "work";
				else if ( state_ret.equals("2-9") )
					state_ret = "offsite";
				else if ( state_ret.equals("3-") )
					state_ret = "finished";
				else
					state_ret = "unknown";
				callInfo.setState(state_ret);
				callInfo.setEngineer(URLEncoder.encode(rs.getString(8), "utf-8"));
				callInfo.setIsActive(rs.getString(9));
				callList.add(callInfo);
			}
		}
		String strRet = callableStmt.getString(6);
		System.out.println ("return of sp_wx_querycalls:" + strRet);
		logger.log(Level.INFO, "return of sp_wx_querycalls:" + strRet);		
		return strRet;
	}

	public String executeSPWXActiveCall(Connection conn, String openId, String active, String callId) throws Throwable
	{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_activecall(?,?,?,?)}");
		callableStmt.setString(1, openId);
		callableStmt.setString(2, active);
		callableStmt.setString(3, callId);
		callableStmt.registerOutParameter(4,java.sql.Types.NCHAR);
		callableStmt.execute();
		String strRet = callableStmt.getString(4);
		System.out.println ("return of sp_wx_activecall:" + strRet);
		logger.log(Level.INFO, "return of sp_wx_activecall:" + strRet);		
		return strRet;
	}
	
	public String executeSPWXQueryActiveCall(Connection conn, String openId, CallInfo callInfo) throws Throwable
	{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_queryactivecall(?,?)}");
		callableStmt.setString(1, openId);
		callableStmt.registerOutParameter(2,java.sql.Types.NCHAR);
		callableStmt.execute();
		callableStmt.getMoreResults();
		ResultSet rs = callableStmt.getResultSet();
		callInfo.setCallidentityid("0");
		if ( rs != null )
		{
			while ( rs.next() )
			{
				callInfo.setCallidentityid(rs.getString(1));
				callInfo.setCallTitle(rs.getString(2));
				callInfo.setCustomername(URLEncoder.encode(rs.getString(3), "utf-8"));
				callInfo.setBrandname(URLEncoder.encode(rs.getString(4), "utf-8"));
				String state_ret = rs.getString(5);
				if ( state_ret.equals("2-6") )
					state_ret = "wait";
				else if ( state_ret.equals("2-7") )
					state_ret = "wait";
				else if ( state_ret.equals("2-12") )
					state_ret = "work";
				else if ( state_ret.equals("2-9") )
					state_ret = "offsite";
				else if ( state_ret.equals("3-") )
					state_ret = "finished";
				else
					state_ret = "unknown";
				callInfo.setState(state_ret);
				callInfo.setDesc(URLEncoder.encode(rs.getString(6), "utf-8"));
				callInfo.setIsActive("0");
			}
		}
		String strRet = callableStmt.getString(2);
		System.out.println ("return of sp_wx_queryactivecall:" + strRet);
		logger.log(Level.INFO, "return of sp_wx_queryactivecall:" + strRet);		
		return strRet;
	}
	
	public String executeSPWXQueryStats(Connection conn, String openId, String startDate, String endDate, CallStats callStats) throws Throwable
	{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_querystats(?,?,?,?,?,?,?)}");
		callableStmt.setString(1, openId);
		callableStmt.setString(2, startDate);
		callableStmt.setString(3, endDate);
		callableStmt.registerOutParameter(4,java.sql.Types.INTEGER);
		callableStmt.registerOutParameter(5,java.sql.Types.INTEGER);
		callableStmt.registerOutParameter(6,java.sql.Types.INTEGER);
		callableStmt.registerOutParameter(7,java.sql.Types.NCHAR);
		callableStmt.execute();
		callStats.setWait(callableStmt.getInt(4));
		callStats.setProcessing(callableStmt.getInt(5));
		callStats.setFinished(callableStmt.getInt(6));
		String strRet = callableStmt.getString(7);
		System.out.println ("return of sp_wx_querystats:" + strRet + "  wait:" + callStats.getWait() + ";process:" + callStats.getProcessing() + ";finished:" + callStats.getFinished());
		logger.log(Level.INFO, "return of sp_wx_querystats:" + strRet + "  wait:" + callStats.getWait() + ";process:" + callStats.getProcessing() + ";finished:" + callStats.getFinished());
		return strRet;
	}

	public String executeSPWXOnSite(Connection conn, String openId, String callIdentityId, String location, String onsiteTime) throws Throwable
	{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_onsite(?,?,?,?,?)}");
		callableStmt.setString(1, openId);
		callableStmt.setString(2, callIdentityId);
		callableStmt.setString(3, location);
		callableStmt.setString(4, onsiteTime);
		callableStmt.registerOutParameter(5,java.sql.Types.NCHAR);
		callableStmt.execute();
		String strRet = callableStmt.getString(5);
		System.out.println ("return of sp_wx_onsite:" + strRet);
		logger.log(Level.INFO, "return of sp_wx_onsite:" + strRet);		
		return strRet;
	}

	public String executeSPWXOffSite(Connection conn, String openId, String callIdentityId, String location, String offsiteTime) throws Throwable
	{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_offsite(?,?,?,?,?)}");
		callableStmt.setString(1, openId);
		callableStmt.setString(2, callIdentityId);
		callableStmt.setString(3, location);
		callableStmt.setString(4, offsiteTime);
		callableStmt.registerOutParameter(5,java.sql.Types.NCHAR);
		callableStmt.execute();
		String strRet = callableStmt.getString(5);
		System.out.println ("return of sp_wx_offsite:" + strRet);
		logger.log(Level.INFO, "return of sp_wx_offsite:" + strRet);		
		return strRet;
	}
	
	public String executeSPWXUploadPic(Connection conn, String openId, String callIdentityId, String picuri, int size, String contenttype) throws Throwable
	{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_uploadpic(?,?,?,?,?,?)}");
		callableStmt.setString(1, openId);
		callableStmt.setString(2, callIdentityId);
		callableStmt.setString(3, picuri);
		callableStmt.setInt(4, size);
		callableStmt.setString(5, contenttype);
		callableStmt.registerOutParameter(6,java.sql.Types.NCHAR);
		callableStmt.execute();
		String strRet = callableStmt.getString(6);
		System.out.println ("return of sp_wx_uploadpic:" + strRet);
		logger.log(Level.INFO, "return of sp_wx_uploadpic:" + strRet);		
		return strRet;
	}

	public String executeSPWXUpdateCall(Connection conn, String openId, String callIdentityId, String task, int nassistnumtech, int nassistnumnontech, String location, String offsiteTime) throws Throwable
	{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_updatecall(?,?,?,?,?,?,?,?)}");
		callableStmt.setString(1, openId);
		callableStmt.setString(2, callIdentityId);
		callableStmt.setString(3, task);
		callableStmt.setInt(4, nassistnumtech);
		callableStmt.setInt(5, nassistnumnontech);
		callableStmt.setString(6, location);
		callableStmt.setString(7, offsiteTime);		
		callableStmt.registerOutParameter(8,java.sql.Types.NCHAR);
		callableStmt.execute();
		String strRet = callableStmt.getString(8);
		System.out.println ("return of sp_wx_updatecall:" + strRet);
		logger.log(Level.INFO, "return of sp_wx_updatecall:" + strRet);		
		return strRet;
	}

	public String executeSPWXQueryNewCalls(Connection conn, Collection<NewCallInfo> newCallList) throws Throwable
	{
		CallableStatement callableStmt;
		callableStmt = conn.prepareCall("{call sp_wx_querynewcalls(?)}");
		callableStmt.registerOutParameter(1,java.sql.Types.NCHAR);
		callableStmt.execute();
		ResultSet rs = callableStmt.getResultSet();
		if ( rs != null )
		{
			while ( rs.next() )
			{
				NewCallInfo newcallInfo = new NewCallInfo();
				newcallInfo.setCallidentityid(rs.getString(1));
				newcallInfo.setCallTitle(rs.getString(2));
				newcallInfo.setCustomername(URLEncoder.encode(rs.getString(3), "utf-8"));
				newcallInfo.setBrandname(URLEncoder.encode(rs.getString(4), "utf-8"));
				newcallInfo.setDesc(URLEncoder.encode(rs.getString(5), "utf-8"));
				newcallInfo.setAddress(URLEncoder.encode(rs.getString(6), "utf-8"));
				newcallInfo.setEngineer(URLEncoder.encode(rs.getString(7), "utf-8"));
				newcallInfo.setOpenId(URLEncoder.encode(rs.getString(8), "utf-8"));
				newCallList.add(newcallInfo);
			}
		}
		String strRet = callableStmt.getString(1);
		System.out.println ("return of sp_wx_querynewcalls:" + strRet);
		logger.log(Level.INFO, "return of sp_wx_querynewcalls:" + strRet);		
		return strRet;
	}

	public PreparedStatement createInsertRecord(Connection conn,String sql,String rId,String uId,String itemName,float itemPrice,int itemType) throws Throwable {
		
		if(conn != null){
			PreparedStatement pst =  conn.prepareStatement(sql);
			pst.setString(1, rId);
			pst.setString(2, uId);
			pst.setString(3, itemName);
			pst.setFloat(4, itemPrice);
			pst.setTimestamp(5, new java.sql.Timestamp(new Date().getTime()));
			pst.setInt(6, itemType);
			return pst;
		}
		return null;
	}
	
	public PreparedStatement createInsertUser(Connection conn,String sql,String uId,int flag)throws Throwable {
		if (conn != null){
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, uId);
			pst.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
			pst.setInt(3, flag);
			return pst;
		}
		return null;
	}
	
	public PreparedStatement createQuery(Connection conn,String sql,String uId,String startTime,String endTime,int itemType) throws Throwable {
		if (conn != null){
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, uId);
			pst.setString(2, startTime);
			pst.setString(3, endTime);
			pst.setInt(4, itemType);
			util.trace(this, "CreateQuery sql="+sql+";uId="+uId+";startTime="+startTime+";endTime="+endTime+";itemType"+itemType);
			return pst;
		}
		return null;
	}
	
	public PreparedStatement createQuery(Connection conn,String sql) throws Throwable {
		
		if(conn != null){
			
			PreparedStatement pst =  conn.prepareStatement(sql);
			return pst;
			
		}else{
			util.warn(this, "CreateQuery failure for dbConn is null.");
		}
		
		return null;
		
	}
	
	public PreparedStatement createQuery(Connection conn,String sql,String... params) throws Throwable{
		
		if(conn != null){
			
			PreparedStatement pst =  conn.prepareStatement(sql);
			int pSize = params.length;
			if(pSize > 0){
				for(int i = 0;i<pSize;i++){
					pst.setString(i+1, params[i]);
					util.trace(this, "ExecuteQuery update sql index["+(i+1)+"] value="+params[i]);
				}
			}
			return pst;
			
		}else{
			util.warn(this, "CreateQuery failure for dbConn is null.");
		}
		
		return null;
		
	}
	
	void initDS()  {
		
/*		try{
			
			ds = new BasicDataSource(); 
		    ds.setDriverClassName(Config.dbDriver); 
		    ds.setUrl(Config.dbURL); 
		    ds.setUsername(Config.dbUser); 
		    ds.setPassword(Config.dbPwd); 
		    ds.setMaxActive(Config.connPoolSize != null ?Integer.parseInt(Config.connPoolSize) : 10);
		    ds.setTestOnBorrow(Config.dbTestOnBorrow);
		    ds.setValidationQuery(Config.dbValidationQuery);
			
		}catch(Throwable e){
			util.error(this, e);
		}*/
		
		try{Class.forName(Config.dbDriver).newInstance();}
		catch (ClassNotFoundException e) {System.out.println("-------------ClassNotFound");return;}
		catch (InstantiationException e) {System.out.println("-------------Instantiation");return;}
		catch (IllegalAccessException e) {System.out.println("-------------IllegalAccess");return;}
		
		setConnectionState(false);
		try{conn = DriverManager.getConnection(Config.dbURL, Config.dbUser, Config.dbPwd);}
		catch(Exception e){util.error(this, e);return;}
		setConnectionState(true);
	}

	public void setConnectionState(boolean state){
		bConn = state;
	}
}
