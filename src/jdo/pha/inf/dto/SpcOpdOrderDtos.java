package jdo.pha.inf.dto;

import java.util.List;

public class SpcOpdOrderDtos {

	private List<SpcOpdOrderDto> spcOpdOrderDtos;
	private List<SpcRegPatadmDto> spcRegPatadmDtos;
	private List<SpcSysPatinfoDto> spcSysPatinfoDtos;
	private List<SpcOpdDrugAllergyDto> spcOpdDrugAllergyDtos;
	private List<SpcOpdDiagrecDto> spcOpdDiagrecDtos;

	/**
	 * 得到OPD_ORDER
	 * 
	 * @return List<SpcOpdOrderDto>
	 */
	public List<SpcOpdOrderDto> getSpcOpdOrderDtos() {
		return spcOpdOrderDtos;
	}

	public void setSpcOpdOrderDtos(List<SpcOpdOrderDto> spcOpdOrderDtos) {
		this.spcOpdOrderDtos = spcOpdOrderDtos;
	}

	/**
	 * 得到REG_PATADM
	 * 
	 * @return List<SpcRegPatadmDto>
	 */
	public List<SpcRegPatadmDto> getSpcRegPatadmDtos() {
		return spcRegPatadmDtos;
	}

	public void setSpcRegPatadmDtos(List<SpcRegPatadmDto> spcRegPatadmDtos) {
		this.spcRegPatadmDtos = spcRegPatadmDtos;
	}

	/**
	 * 得到SYS_PATINFO
	 * 
	 * @return List<SpcSysPatinfoDto>
	 */
	public List<SpcSysPatinfoDto> getSpcSysPatinfoDtos() {
		return spcSysPatinfoDtos;
	}

	public void setSpcSysPatinfoDtos(List<SpcSysPatinfoDto> spcSysPatinfoDtos) {
		this.spcSysPatinfoDtos = spcSysPatinfoDtos;
	}

	/**
	 * 得到OPD_DRUGALLERGY
	 * 
	 * @return List<SpcOpdDrugAllergyDto>
	 */
	public List<SpcOpdDrugAllergyDto> getSpcOpdDrugAllergyDtos() {
		return spcOpdDrugAllergyDtos;
	}

	public void setSpcOpdDrugAllergyDtos(
			List<SpcOpdDrugAllergyDto> spcOpdDrugAllergyDtos) {
		this.spcOpdDrugAllergyDtos = spcOpdDrugAllergyDtos;
	}

	/**
	 * 得到OPD_DIAGREC
	 * 
	 * @return List<SpcOpdDiagrecDto>
	 */
	public List<SpcOpdDiagrecDto> getSpcOpdDiagrecDtos() {
		return spcOpdDiagrecDtos;
	}

	public void setSpcOpdDiagrecDtos(List<SpcOpdDiagrecDto> spcOpdDiagrecDtos) {
		this.spcOpdDiagrecDtos = spcOpdDiagrecDtos;
	}

}
