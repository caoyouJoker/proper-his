package action.spc.services.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SpcOdiDspnms implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<SpcOdiDspnm> spcOdiDspnms = new ArrayList<SpcOdiDspnm>() ;
	
	private List<SpcIndStock>  spcIndStocks = new ArrayList<SpcIndStock>() ;
	
	public SpcOdiDspnms(){
		
	}

	public List<SpcOdiDspnm> getSpcOdiDspnms() {
		return spcOdiDspnms;
	}

	public void setSpcOdiDspnms(List<SpcOdiDspnm> spcOdiDspnms) {
		this.spcOdiDspnms = spcOdiDspnms;
	}

	public List<SpcIndStock> getSpcIndStocks() {
		return spcIndStocks;
	}

	public void setSpcIndStocks(List<SpcIndStock> spcIndStocks) {
		this.spcIndStocks = spcIndStocks;
	}

	 
	
	
}
