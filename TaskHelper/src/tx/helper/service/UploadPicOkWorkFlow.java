package tx.helper.service;

import tx.commons.util.Util;

public class UploadPicOkWorkFlow extends WorkFlow {

	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	public UploadPicOkWorkFlow(String openId,int expire){
		this.openId = openId;
		this.expire = expire;
		this.wfName = "UploadPicOkWorkFlow";
		this.status = WFStatus.DONE;
	}
	
	public UploadPicOkWorkFlow(String openId){
		this(openId,WorkFlow.DEFAULT_EXPIRE);
	}
	
	@Override
	public String getPrompt() {
		return "上传图片完成 ，继续上传图片请直接发送照片，否则请继续其他操作。";
	}

	@Override
	public WorkFlow end(String mediaId) {
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
