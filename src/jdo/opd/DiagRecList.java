package jdo.opd;

import jdo.sys.Pat;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;
/**
 * 
 * <p>
 * Title: 诊断jdo
 * </p>
 * 
 * <p>
 * Description:诊断jdo
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 200800909
 * @version 1.0
 */
public class DiagRecList extends TModifiedList{
	private Pat pat;

	
	public DiagRecList() {
		setMapString("caseNo:CASE_NO;icdCode:ICD_CODE;icdType:ICD_TYPE;mainDiagFlg:MAIN_DIAG_FLG;admType:ADM_TYPE;diagNote:DIAG_NOTE;drCode:DR_CODE;orderDate:ORDER_DATE;fileNO:FILE_NO;optUser:OPT_USER;optDate:OPT_DATE;optTerm:OPT_TERM");
	}

	public void setPat(Pat pat) {
		this.pat = pat;
	}

	public Pat getPat() {
		return pat;
	}

	/**
	 * 新增诊断
	 * @return DiagRecList
	 */
	public DiagRec newDiagRec() {
		DiagRec diagRec = new DiagRec();
		diagRec.setPat(getPat());
		this.newData(diagRec);
		return diagRec;
	}

	/**
	 * 得到诊断
	 * @param index int
	 * @return DiagRec
	 */
	public DiagRec getDiagRec(int index) {
		return (DiagRec) get(index);
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
			DiagRec diagrec = new DiagRec();
			diagrec.setPat(getPat());
			diagrec.setMapString(getMapString());
			if (!diagrec.initParm(parm, i))
				return false;
			add(diagrec);
		}
		return true;
	}
}
