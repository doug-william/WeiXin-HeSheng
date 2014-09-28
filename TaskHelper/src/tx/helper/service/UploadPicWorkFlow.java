package tx.helper.service;

import tx.commons.util.Util;
import tx.helper.config.Config;
import tx.helper.module.TCall;

public class UploadPicWorkFlow extends WorkFlow {

	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	public UploadPicWorkFlow(String openId,int expire){
		this.openId = openId;
		this.expire = expire;
		this.wfName = "UploadPicWorkFlow";
	}
	
	public UploadPicWorkFlow(String openId){
		this(openId,WorkFlow.DEFAULT_EXPIRE);
	}
	
	@Override
	public String getPrompt() {
		WXUser user = service.getWXUser(this.openId);
		if(user != null){
			TCall activeCall = service.updateTCall(this.openId);
			if(activeCall != null){
				return "【上传图片确认】\n 您正在处理工单信息：\n 工单编号："+user.getActiveCall().getCalltitle()+"\n 客户名称："+user.getActiveCall().getCname()+"\n 取消上传操作请回复\"0\",确认请直接上传相应的照片.";
			}else {
				this.status = WFStatus.DONE;
				return "您还没有选择工单，请先<a href=\"http://mvsengineertaskhelper.duapp.com/myOrders.html?openid="+openId+"\">选择工单</a>.";
			}
		}else{
			this.status = WFStatus.DONE;
			return "您尚未绑定帐号，请先<a href=\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Config.wxAppId+"&redirect_uri="+Config.appUrl+"/wxAuth2&response_type=code&scope=snsapi_base&state=login#wechat_redirect\">绑定帐号</a>";
		}
	}

	@Override
	public WorkFlow end(String mediaId) {
		// Upload picture here.
		if(mediaId != null && mediaId.length()>0){
			this.status = WFStatus.DONE;
			boolean res = service.uploadPic(mediaId, this.openId);
			if(res){
				return new UploadPicOkWorkFlow(this.openId);
			}else{
				return new SysFailedWorkFlow("uploadPic","uploadPic failure");
			}
		}else{
			util.trace(this, "UploadPic cancel.");
			this.status = WFStatus.CANCEL;
			// return new OnSiteCancelWorkFlow(this.openId);
			return null;
		}
	}

}
