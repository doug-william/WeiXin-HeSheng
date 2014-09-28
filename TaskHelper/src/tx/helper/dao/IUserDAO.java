package tx.helper.dao;

import tx.helper.module.TUser;

public interface IUserDAO {
	
	public void addUser(TUser user);

	public TUser getTUserById(String uId);
	
	public TUser getTUserByCardId(String cardId);
	
	public void deleteTUser(String uId);
	
}
