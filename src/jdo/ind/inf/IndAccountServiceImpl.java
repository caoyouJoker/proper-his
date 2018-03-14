package jdo.ind.inf;

import javax.jws.WebService;

/**
 * 物联网结算接口-实现类
 * @author liyanhui
 *
 */
@WebService
public class IndAccountServiceImpl implements IndAccountService{

	@Override
	public String onSaveIndAccount(IndAccounts indAccounts) {
		// TODO Auto-generated method stub
		return IndAccountDaoImpl.getInstance().onSave(indAccounts);
	}

	
	

	
}
