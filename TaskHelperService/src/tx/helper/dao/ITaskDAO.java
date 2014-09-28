package tx.helper.dao;

import java.util.Collection;
import java.util.List;

import tx.helper.model.BrandName;
import tx.helper.model.CallInfo;
import tx.helper.model.CallStats;
import tx.helper.model.CustomerInfo;
import tx.helper.model.EmployeeInfo;
import tx.helper.model.NewCallInfo;
import tx.helper.model.UserLog;

public interface ITaskDAO {
	
	/**
	 * 绑定用户,首先检查certId是否存在，如果存在则将用户记录下来，否则失败.
	 * @param openid
	 * @param certId
	 * @param userName
	 * @param userPwd
	 * @return
	 */
	public boolean checkUser(String openid,String certId,String userName,String userPwd);
	
	/**
	 * 上下班打卡签到
	 * @param userLog
	 * @param loginmode
	 * @param logintime
	 * @param location
	 * @return
	 */
	public String userLog(String openid,String loginmode,String logintime,String location);
	
	/**
	 * 查询打卡记录
	 * @param openId
	 * @param startDate
	 * @param endDate
	 * @param userLogList OUT
	 * @return
	 */
	public String queryUserLog(String openId, String startDate,String endDate, Collection<UserLog> userLogList);
	
	/**
	 * 查询工程师信息
	 * @param openId
	 * @param employeeInfo OUT
	 * @return
	 */
	public String queryEmployeeInfo(String openId, EmployeeInfo employeeInfo);
	
	/**
	 * 查询所有客户名称
	 * @param openId
	 * @param customerList OUT
	 * @return
	 */
	public String queryCustomers(String openId, Collection<CustomerInfo> customerList);
	
	/**
	 * 查询单个客户下的所有品牌名称
	 * @param openId
	 * @param customerName
	 * @param brandNameList OUT
	 * @return
	 */
	public String queryBrands(String openId, String customerName, Collection<BrandName> brandNameList);	
	
	/**
	 * 查询单个工程师名下的特定状态的工单信息，或者与他同组其他工程师名下特定状态的工单信息
	 * @param openId
	 * @param customername
	 * @param brandname
	 * @param state
	 * @param groupflag
	 * @return
	 */
	public String queryCalls(String openId, String customername, String brandname, String state, String groupflag, Collection<CallInfo> callList);

	/**
	 * 将工单设为当前活动状态，或取消工单的当前活动状态
	 * @param openId
	 * @param active
	 * @param callId
	 * @return
	 */
	public String activeCall(String openId, String active, String callId);
	
	/**
	 * 查询当前活动状态工单，返回相关数据
	 * @param openId
	 * @return
	 */
	public String queryActiveCall(String openId, CallInfo callInfo);
	
	/**
	 * 查询工程师工单统计信息
	 * @param openId
	 * @param startDate
	 * @param endDate
	 * @param opened OUT
	 * @param processing OUT
	 * @param finished OUT
	 * @return
	 */
	public String queryStats(String openId, String startDate,String endDate, CallStats callStats);
	
	/**
	 * 到场签到
	 * @param openId
	 * @param callIdentityId
	 * @param location
	 * @param onsiteTime
	 * @return
	 */
	public String onSite(String openId, String callIdentityId, String location, String onsiteTime);

	/**
	 * 离场
	 * @param openId
	 * @param callIdentityId
	 * @param location
	 * @param offsiteTime
	 * @return
	 */
	public String offSite(String openId, String callIdentityId, String location, String offsiteTime);

	/**
	 * 上传图片
	 * @param openId
	 * @param callIdentityId
	 * @param picuri
	 * @param size
	 * @param contenttype
	 * @return
	 */
	public String uploadPic(String openId, String callIdentityId, String picuri, int size, String contenttype);
	
	/**
	 * 现场工作描述和打分
	 * @param openId
	 * @param callIdentityId
	 * @param task
	 * @param nassistnumtech
	 * @param nassistnumnontech
	 * @return
	 */
	public String updateCall(String openId, String callIdentityId, String task, int nassistnumtech, int nassistnumnontech, String location, String offsiteTime);

	/**
	 * 查询新生成的工单列表
	 * @param newCallList OUT
	 * @return
	 */
	public String queryNewCalls(Collection<NewCallInfo> newCallList);
}