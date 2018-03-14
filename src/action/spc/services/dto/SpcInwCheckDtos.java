package action.spc.services.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SpcInwCheckDtos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String stationCode ;
	private String regionCode ;
	
	private List<SpcInwCheckDto> spcInwCheckDtos = new ArrayList<SpcInwCheckDto>() ;
	
	public SpcInwCheckDtos(){
		
	}

	public String getStationCode() {
		return stationCode;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public List<SpcInwCheckDto> getSpcInwCheckDtos() {
		return spcInwCheckDtos;
	}

	public void setSpcInwCheckDtos(List<SpcInwCheckDto> spcInwCheckDtos) {
		this.spcInwCheckDtos = spcInwCheckDtos;
	}
	
	
}
