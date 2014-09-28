package tx.helper.service;

import tx.helper.module.TUser;

public class UserService {

	private static UserService service;
	
	private UserService(){}
	
	public static UserService getInstance() {
		if(service == null) service = new UserService();
		return service;
	}
	
	// private IUserDAO userDao = new UserDAO4DB();
	
	public void addUser(String uId,String cardId,String userName,String userPwd){
		TUser user = new TUser(uId,cardId);
		user.setUserName(userName);
		user.setUserPwd(userPwd);
		// userDao.addUser(user);
	}
	
	public void deleteUser(String uId){
		// userDao.deleteTUser(uId);
	}
	
	public TUser getTUserByUId(String uId){
		// return userDao.getTUserById(uId);
		return null;
	}
	
}
