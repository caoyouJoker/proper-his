package jdo.spc.accountinf;

import java.util.List;

/**
 * 物联网结算对象
 * @param <IndAccount>
 */
@SuppressWarnings("hiding")
public class IndAccounts implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5535231379289699865L;

	public List<IndAccount> getIndAccounts() {
		return indAccounts;
	}

	public void setIndAccounts(List<IndAccount> indAccounts) {
		this.indAccounts = indAccounts;
	}

	private List<IndAccount> indAccounts;
	// Constructors

	/** default constructor */
	public IndAccounts() {
	}

	 

}