package tx.helper.model;

public class UserLog {
	private String logDate;
	private String loginTime;
	private String loginLocation;
	private String logoutTime;
	private String logoutLocation;

	public void setLogDate(String LogDate) {
		logDate = LogDate;
	}
	
	public String getLogDate() {
		return logDate;
	}
	
	public void setLoginTime(String LoginTime) {
		loginTime = LoginTime;
	}
	
	public String getLoginTime() {
		return loginTime;
	}
	
	public void setLoginLocation(String LoginLocation) {
		loginLocation = LoginLocation;
	}
	
	public String getLoginLocation() {
		return loginLocation;
	}
	
	public void setLogoutTime(String LogoutTime) {
		logoutTime = LogoutTime;
	}
	
	public String getLogoutTime() {
		return logoutTime;
	}
	
	public void setLogoutLocation(String LogoutLocation) {
		logoutLocation = LogoutLocation;
	}
	
	public String getLogoutLocation() {
		return logoutLocation;
	}	
}
