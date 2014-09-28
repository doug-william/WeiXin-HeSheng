package tx.helper.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import tx.commons.util.Util;
import tx.helper.db.DBConnection;
import tx.helper.model.BrandName;
import tx.helper.model.CallInfo;
import tx.helper.model.CallStats;
import tx.helper.model.CustomerInfo;
import tx.helper.model.EmployeeInfo;
import tx.helper.model.NewCallInfo;
import tx.helper.model.UserLog;

public class TaskDAO implements ITaskDAO {

	private Util util = Util.getInstance();
	
	private DBConnection dbConn = DBConnection.getInstance();
	
	private  static TaskDAO dao = null;
	
	Logger logger = Logger. getLogger("asdf");
	
	private TaskDAO() {}
	
	public static TaskDAO getInstance() {
		if (dao == null) dao = new TaskDAO();
		return dao;
	}
	
	@Override
	public boolean checkUser(String openid, String certId, String userName,String userPwd) {		
		boolean bRet;
		try
		{
			Connection conn = dbConn.createConn();
			bRet = dbConn.executeSPWXBindings(dbConn.createConn(), openid, certId, userName, userPwd);
			util.warn(this, "sp_wx_bindings return code:" + bRet);
			logger.log(Level.INFO, "sp_wx_bindings return code:" + bRet);
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in CheckUser\r\n" + se.getMessage());
        	return false;
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in CheckUser");
			return false;
		}		
		return bRet;
	}

	@Override
	public String userLog(String openid,String loginmode,String logintime,String location) {
		try
		{
			String strRet = dbConn.executeSPWXLoginLogoutLog(dbConn.createConn(), openid, loginmode, logintime, location);
			util.warn(this, "sp_wx_loginlogout return code:" + strRet);
			logger.log(Level.INFO, "sp_wx_loginlogout return code:" + strRet);
			return strRet;
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in UserLog\r\n" + se.getMessage());
        	return "false";
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in UserLog");
			return "false";
		}
	}

	@Override
	public String queryUserLog(String openId, String startDate,String endDate, Collection<UserLog> userLogList) {
		try
		{
			String strRet = dbConn.executeSPWXQueryLog(dbConn.createConn(), openId, startDate, endDate, userLogList);
			util.warn(this, "sp_wx_queryloginlog return code:" + strRet);
			logger.log(Level.INFO, "sp_wx_queryloginlog return code:" + strRet);
			return strRet;
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in QueryLog\r\n" + se.getMessage());
        	return "dbfatal";
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in QueryLog");
			return "dbfatal";
		}		
	}

	@Override
	public String queryEmployeeInfo(String openId, EmployeeInfo employeeInfo)
	{
		try
		{
			String strRet = dbConn.executeSPWXQueryEmployeeInfo(dbConn.createConn(), openId, employeeInfo);
			util.warn(this, "sp_wx_queryemployeeinfo return code:" + strRet);
			logger.log(Level.INFO, "sp_wx_queryemployeeinfo return code:" + strRet);			
			return strRet;
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in QueryEmployeeInfo\r\n" + se.getMessage());
			return "dbfatal";
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in QueryEmployeeInfo");
			return "dbfatal";
		}
	}

	public String queryCustomers(String openId, Collection<CustomerInfo> customerList)
	{
		try
		{
			String strRet = dbConn.executeSPWXQueryCustomers(dbConn.createConn(), openId, customerList);
			util.warn(this, "sp_wx_querycustomers return code:" + strRet);
			logger.log(Level.INFO, "sp_wx_querycustomers return code:" + strRet);			
			return strRet;
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in QueryCustomers\r\n" + se.getMessage());
			return "dbfatal";
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in QueryCustomers");
			return "dbfatal";
		}
	}
	
	public String queryBrands(String openId, String customerName, Collection<BrandName> brandNameList)
	{
		try
		{
			String strRet = dbConn.executeSPWXQueryBrands(dbConn.createConn(), openId, customerName, brandNameList);
			util.warn(this, "sp_wx_querybrands return code:" + strRet);
			logger.log(Level.INFO, "sp_wx_querybrands return code:" + strRet);			
			return strRet;
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in QueryBrands\r\n" + se.getMessage());
			return "dbfatal";
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in QueryBrands");
			return "dbfatal";
		}		
	}

	public String queryCalls(String openId, String customername, String brandname, String state, String groupflag, Collection<CallInfo> callList)
	{
		try
		{
			String strRet = dbConn.executeSPWXQueryCalls(dbConn.createConn(), openId, customername, brandname, state, groupflag, callList);
			util.warn(this, "sp_wx_querycalls return code:" + strRet);
			logger.log(Level.INFO, "sp_wx_querycalls return code:" + strRet);			
			return strRet;			
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in QueryCalls\r\n" + se.getMessage());
			return "dbfatal";
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in QueryCalls");
			return "dbfatal";
		}		
	}

	public String activeCall(String openId, String active, String callId)
	{
		try
		{
			String strRet = dbConn.executeSPWXActiveCall(dbConn.createConn(), openId, active, callId);
			util.warn(this, "sp_wx_activecall return code:" + strRet);
			logger.log(Level.INFO, "sp_wx_activecall return code:" + strRet);			
			return strRet;
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in ActiveCall\r\n" + se.getMessage());
			return "dbfatal";
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in ActiveCall");
			return "dbfatal";
		}		
	}

	public String queryActiveCall(String openId, CallInfo callInfo)
	{
		try
		{
			String strRet = dbConn.executeSPWXQueryActiveCall(dbConn.createConn(), openId, callInfo);
			util.warn(this, "sp_wx_queryactivecall return code:" + strRet);
			logger.log(Level.INFO, "sp_wx_queryactivecall return code:" + strRet);			
			return strRet;
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in QueryActiveCall\r\n" + se.getMessage());
			return "dbfatal";
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in QueryActiveCall");
			return "dbfatal";
		}
	}

	public String queryStats(String openId, String startDate,String endDate, CallStats callStats)
	{
		try
		{
			String strRet = dbConn.executeSPWXQueryStats(dbConn.createConn(), openId, startDate, endDate, callStats);
			util.warn(this, "sp_wx_querystats return code:" + strRet);
			logger.log(Level.INFO, "sp_wx_querystats return code:" + strRet);
			return strRet;
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in QueryStats\r\n" + se.getMessage());
			return "dbfatal";
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in QueryStats");
			return "dbfatal";
		}
	}

	public String onSite(String openId, String callIdentityId, String location, String onsiteTime)
	{
		try
		{
			String strRet = dbConn.executeSPWXOnSite(dbConn.createConn(), openId, callIdentityId, location, onsiteTime);
			util.warn(this, "sp_wx_onsite return code:" + strRet);
			logger.log(Level.INFO, "sp_wx_onsite return code:" + strRet);			
			return strRet;
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in OnSite\r\n" + se.getMessage());
			return "dbfatal";
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in OnSite");
			return "dbfatal";
		}
	}

	public String offSite(String openId, String callIdentityId, String location, String offsiteTime)
	{
		try
		{
			String strRet = dbConn.executeSPWXOffSite(dbConn.createConn(), openId, callIdentityId, location, offsiteTime);
			util.warn(this, "sp_wx_offsite return code:" + strRet);
			logger.log(Level.INFO, "sp_wx_offsite return code:" + strRet);			
			return strRet;
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in OffSite\r\n" + se.getMessage());
			return "dbfatal";
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in OffSite");
			return "dbfatal";
		}
	}

	public String uploadPic(String openId, String callIdentityId, String picuri, int size, String contenttype)
	{
		try
		{
			String strRet = dbConn.executeSPWXUploadPic(dbConn.createConn(), openId, callIdentityId, picuri, size, contenttype);
			util.warn(this, "sp_wx_uploadpic return code:" + strRet);
			logger.log(Level.INFO, "sp_wx_uploadpic return code:" + strRet);			
			return strRet;			
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in UploadPic\r\n" + se.getMessage());
			return "dbfatal";
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in UploadPic");
			return "dbfatal";
		}
	}
	
	public String updateCall(String openId, String callIdentityId, String task, int nassistnumtech, int nassistnumnontech, String location, String offsiteTime)
	{
		try
		{
			String strRet = dbConn.executeSPWXUpdateCall(dbConn.createConn(), openId, callIdentityId, task, nassistnumtech, nassistnumnontech, location, offsiteTime);
			util.warn(this, "sp_wx_updatecall return code:" + strRet);
			logger.log(Level.INFO, "sp_wx_updatecall return code:" + strRet);			
			return strRet;			
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in UpdateCall\r\n" + se.getMessage());
			return "dbfatal";
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in UpdateCall");
			return "dbfatal";
		}
	}

	public String queryNewCalls(Collection<NewCallInfo> newCallList)
	{
		try
		{
			String strRet = dbConn.executeSPWXQueryNewCalls(dbConn.createConn(), newCallList);
			util.warn(this, "sp_wx_querynewcalls return code:" + strRet);
			logger.log(Level.INFO, "sp_wx_querynewcalls return code:" + strRet);			
			return strRet;			
		}
		catch (java.sql.SQLException se) {
			dbConn.setConnectionState(false);
			logger.log(Level.WARNING, "catch SQLException in QueryNewCalls\r\n" + se.getMessage());
			return "dbfatal";
		} catch (Throwable e) {
			logger.log(Level.WARNING, "catch Throwable in QueryNewCalls");
			return "dbfatal";
		}
	}
}