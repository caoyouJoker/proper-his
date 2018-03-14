package jdo.opd;

import jdo.sys.Pat;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;

/**
 * <p>Title: 既往史列表</p>
 *
 * <p>Description: 既往史列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author lizk 200800908
 * @version 1.0 
 */
public class MedhistoryList extends TModifiedList {
	private Pat pat;
	public MedhistoryList() {
		setMapString("admDate:ADM_DATE;icdCode:ICD_CODE;icdType:ICD_TYPE;admType:ADM_TYPE;caseNo:CASE_NO;deptCode:DEPT_CODE;drCode:DR_CODE;description:DESCRIPTION;optUser:OPT_USER;optDate:OPT_DATE;optTerm:OPT_TERM");
//		StringBuffer sb=new StringBuffer();
//		
//		
//		
//		
//		//System.out.println("bule");
//		sb.append("admDate:ADM_DATE;");
//		setMapString(sb.toString());
////		sb.append(b)
	}

	public void setPat(Pat pat) {
		this.pat = pat;
	}

	public Pat getPat() {
		return pat;
	}

	/**
	 * 新增既往史
	 * @return Medhistory
	 */
	public Medhistory newMedhistory() {
		Medhistory medhistory = new Medhistory();
		medhistory.setPat(getPat());
		this.newData(medhistory);
		return medhistory;
	}

	/**
	 * 得到既往史
	 * @param index int
	 * @return Medhistory
	 */
	public Medhistory getMedhistory(int index) {
		return (Medhistory) get(index);
	}

	/**
	 * 初始化TPARM
	 * @param parm
	 * @return 真：成功，假：失败
	 */
	public boolean initParm(TParm parm) {
		if (parm == null)
			return false;
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			Medhistory medhistory = new Medhistory();
			medhistory.setPat(getPat());
			medhistory.setMapString(getMapString());
			
			if (!medhistory.initParm(parm, i))
				return false;
			add(medhistory);
		}
		return true;
	}

	public static void main(String args[]) {
		MedhistoryList list = new MedhistoryList();

		Pat pat = new Pat();
		pat.setMrNo("1");
		list.setPat(pat);

		TParm parm = new TParm();
		parm.addData("ICD_CODE", "01");
//		parm.addData("DESCRIPTION", "UddRtnRgstPat");
		parm.addData("ICD_CODE", "02");
//		parm.addData("DESCRIPTION", "bbb");
		parm.addData("ICD_CODE", "03");
//		parm.addData("DESCRIPTION", "ccc");
		parm.setData("ACTION", "COUNT", 3);
		//System.out.println(parm);
		list.initParm(parm);
//		//System.out.println(list);
////		list.getMedhistory(1).modifyIcdCode("05");
//		//System.out.println(list.getParm());
	}
}
