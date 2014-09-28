package tx.helper.service;

import tx.helper.config.Config;
import tx.helper.util.WXUtil;

public class OffSiteOkWorkFlow extends WorkFlow {
	
	private HelperService service = HelperService.getInstance();

	public OffSiteOkWorkFlow(String openId,int expire){
		this.openId = openId;
		this.expire = expire;
		this.wfName = "OffSiteOkWorkFlow";
	}
	
	public OffSiteOkWorkFlow(String openId){
		this(openId,WorkFlow.DEFAULT_EXPIRE);
	}
	
	@Override
	public String getPrompt() {
		this.status = WFStatus.DONE;
		WXUser user = service.getWXUser(this.openId);
		String location = "";
		if(user != null)
		{
			if(user != null && user.getLocation() != null){
				location = String.valueOf(user.getLocation().getLongitude())+","+String.valueOf(user.getLocation().getLatitude());
				location = WXUtil.convertBDAxis(user.getLocation().getLatitude(), user.getLocation().getLongitude());
				user.setLocation(null);
			}			
			return "离场确认已完成，点击马上<a href=\""+Config.appUrl+"/myActiveOrder.html?openid="+openId+"&orderId=" + user.getActiveCall().getCallidentityid()+"&calltitle=" + user.getActiveCall().getCalltitle()+ "&location=" + WXUtil.geocoderBMap(location) + "\">处理工单信息</a>";
		}
		else
			return "离场确认已完成，请及时处理工单信息。</a>";
	}

	@Override
	public WorkFlow end(String res) {
		return null;
	}

}
