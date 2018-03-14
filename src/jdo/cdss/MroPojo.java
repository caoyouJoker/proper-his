package jdo.cdss;

import java.util.ArrayList;
import java.util.List;

public class MroPojo {
	
	private String caseNo;
	
	private String mrNo;
	
	private List<OpPojo> opPojos;
	
	private String isMedDept;
	
	private List<String> diseases;
	
	private String mainDiag;
	
	private List<FeePojo> feePojos;
	
	private Number weight = 0;
	
	private List<OpPojo> opBookPojos;

	/**
	 * @return the caseNo
	 */
	public String getCaseNo() {
		return caseNo;
	}

	/**
	 * @param caseNo the caseNo to set
	 */
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	/**
	 * @return the mrNo
	 */
	public String getMrNo() {
		return mrNo;
	}

	/**
	 * @param mrNo the mrNo to set
	 */
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	/**
	 * @return the opPojos
	 */
	public List<OpPojo> getOpPojos() {
		if(opPojos == null){
			opPojos = new ArrayList<OpPojo>();
		}
		return opPojos;
	}

	/**
	 * @param opPojos the opPojos to set
	 */
	public void setOpPojos(List<OpPojo> opPojos) {
		this.opPojos = opPojos;
	}

	/**
	 * @return the isMedDept
	 */
	public String getIsMedDept() {
		return isMedDept;
	}

	/**
	 * @param isMedDept the isMedDept to set
	 */
	public void setIsMedDept(String isMedDept) {
		this.isMedDept = isMedDept;
	}

	/**
	 * @return the diseases
	 */
	public List<String> getDiseases() {
		if(diseases == null){
			diseases = new ArrayList<String>();
		}
		return diseases;
	}

	/**
	 * @param diseases the diseases to set
	 */
	public void setDiseases(List<String> diseases) {
		this.diseases = diseases;
	}

	/**
	 * @return the mainDiag
	 */
	public String getMainDiag() {
		return mainDiag;
	}

	/**
	 * @param mainDiag the mainDiag to set
	 */
	public void setMainDiag(String mainDiag) {
		this.mainDiag = mainDiag;
	}
	
	/**
	 * @return the feePojos
	 */
	public List<FeePojo> getFeePojos() {
		if(feePojos == null){
			feePojos = new ArrayList<FeePojo>();
		}
		return feePojos;
	}

	/**
	 * @param feePojos the feePojos to set
	 */
	public void setFeePojos(List<FeePojo> feePojos) {
		this.feePojos = feePojos;
	}

	/**
	 * @return the weight
	 */
	public Number getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(Number weight) {
		this.weight = weight;
	}
	
	public List<OpPojo> getOpBookPojos() {
		if(opBookPojos == null){
			opBookPojos = new ArrayList<OpPojo>();
		}
		return opBookPojos;
	}

	public void setOpBookPojos(List<OpPojo> opBookPojos) {
		this.opBookPojos = opBookPojos;
	}

	@Override
	public String toString() {
		return "MroPojo [caseNo=" + caseNo + ", mrNo=" + mrNo + ", opPojos=" + opPojos + ", isMedDept=" + isMedDept
				+ ", diseases=" + diseases + ", mainDiag=" + mainDiag + ", feePojos=" + feePojos + ", weight=" + weight
				+ ", opBookPojos=" + opBookPojos + "]";
	}

	public void fireRulesMedDiseases(){
		
	}
	
	public void fireRulesOpDiseases1(){
		
	}
	
	public void fireRulesOpDiseases2(){
		
	}
	
	public void fireRulesOpDiseases3(){
		
	}
	
	public void fireRulesOpDiseases4(){
		
	}
	
	public void fireRulesOpDiseases5(){
		
	}
	
	public void fireRulesOpDiseases61(){
		
	}
	
	public void fireRulesOpDiseases62(){
		
	}
	
	public void fireRulesOpDiseases71(){
		
	}
	
	public void fireRulesOpDiseases72(){
		
	}

}

