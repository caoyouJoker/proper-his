package jdo.ind.inf;

import javax.jws.WebService;

/**
 * 物联网结算接口
 * @author liyanhui
 *
 */
@WebService
public interface IndAccountService {

	/**
	 * 保存物联网结算的数据
	 * @param xml
	 * @return
	 */
	public String onSaveIndAccount(IndAccounts  indAccounts);
	
}
