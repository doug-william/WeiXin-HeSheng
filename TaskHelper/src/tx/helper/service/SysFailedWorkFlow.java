package tx.helper.service;

import tx.commons.util.Util;

public class SysFailedWorkFlow extends WorkFlow {
	
	private String operation;
	private String failedMsg;
	
	private Util util = Util.getInstance();
	
	public SysFailedWorkFlow(String operation, String failMsg){
		this.operation = operation;
		this.failedMsg = failMsg;
		this.status = WFStatus.DONE;
	}
	
	public String getOperationChineseName(String operationName){
		if (operationName.equalsIgnoreCase("login"))
			return "上班打卡";
		else if (operationName.equalsIgnoreCase("logout"))
			return "下班打卡";
		else if (operationName.equalsIgnoreCase("onsite"))
			return "到场签到";
		else if (operationName.equalsIgnoreCase("offsite"))
			return "离场";
		else if (operationName.equalsIgnoreCase("mystats"))
			return "报修统计";
		else if(operationName.equalsIgnoreCase("uploadPic"))
			return "上传图片";
		else
			return "无法识别的操作";
	}
	@Override
	public String getPrompt() {
		if ("missing some parameter".equals(failedMsg))
			return getOperationChineseName(operation) + "：请求缺少必要的参数。";
		else if ("not binding".equals(failedMsg))
			return getOperationChineseName(operation) + "：您尚未绑定帐号，请先绑定帐号。";
		else if ("dbfatal".equals(failedMsg))
			return getOperationChineseName(operation) + "：数据库异常，如反复出现请联系管理员。";
		else if ("not found userid".equals(failedMsg))
			return getOperationChineseName(operation) + "：无法检索到工程师信息，请确认绑定关系。";
		else if ("invalid callid".equals(failedMsg))
			return getOperationChineseName(operation) + "：无效的呼叫标识。";
		else if ("invalid state".equals(failedMsg))
			return getOperationChineseName(operation) + "：工单状态不符。";
		else if ("uploadPic failure".equals(failedMsg)){
			return getOperationChineseName(operation)+ ": 上传图片失败，请稍候重试。稍候重试时请再次点击菜单中的【上传图片】以确保是在处理正确的工单。";
		}
		String msg;
		if("login".equals(operation)){
			msg = "上班打卡：";
			if("1".equals(failedMsg)){
				msg = msg + "当天已经下班";
			}else if("2".equals(failedMsg)){
				msg = msg + "上班时间早于最后一条记录的下班时间";
			}else if("3".equals(failedMsg)){
				msg = msg + "当天已经上班";
			}else if("4".equals(failedMsg)){
				msg = msg + "上班时间早于最后一条记录的上班时间";
			}else if("5".equals(failedMsg)){
				msg = msg + "下班时间早于最后一条记录的上班时间";
			}else if("6".equals(failedMsg)){
				msg = msg + "当天已经下班";
			}else if("7".equals(failedMsg)){
				msg = msg + "下班时间早于最后一条记录的下班时间";
			}else{
				msg = msg + this.failedMsg;
			}			
		}
		else if("logout".equals(operation)){
			msg = "下班打卡：";
			if("1".equals(failedMsg)){
				msg = msg + "当天已经下班";
			}else if("2".equals(failedMsg)){
				msg = msg + "上班时间早于最后一条记录的下班时间";
			}else if("3".equals(failedMsg)){
				msg = msg + "当天已经上班";
			}else if("4".equals(failedMsg)){
				msg = msg + "上班时间早于最后一条记录的上班时间";
			}else if("5".equals(failedMsg)){
				msg = msg + "下班时间早于最后一条记录的上班时间";
			}else if("6".equals(failedMsg)){
				msg = msg + "当天已经下班";
			}else if("7".equals(failedMsg)){
				msg = msg + "下班时间早于最后一条记录的下班时间";
			}else{
				msg = msg + this.failedMsg;
			}			
		}
		else
			msg = getOperationChineseName(operation) + "：" + this.failedMsg;
		
		util.trace(this, "SysFailed -> "+msg);
		
		return msg;
	}

	@Override
	public WorkFlow end(String res) {
		return null;
	}

}
