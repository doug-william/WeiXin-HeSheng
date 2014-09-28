package tx.helper.module;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TUser {
	
	private String uId;
	
	private String cardId;
	
	private String userName;
	
	private String userPwd;
	
	private String createTime;
	
	private String uName;
	
	private String uPositon;
	
	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	public String getuName() {
		return uName;
	}

	public void setuName(String uName) {
		this.uName = uName;
	}

	public String getuPositon() {
		return uPositon;
	}

	public void setuPositon(String uPositon) {
		this.uPositon = uPositon;
	}

	public TUser(String uId,String cardId){
		this.uId = uId;
		this.cardId = cardId;
		this.createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date());
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TUser){
			TUser user = (TUser)obj;
			if(user.getuId().equals(this.getuId())) return true;
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "[uId=" +this.uId+",cardId="+this.cardId+",userName="+this.userName+",createTime="+this.createTime+"]";
	}
}
