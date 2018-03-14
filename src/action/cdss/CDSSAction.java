package action.cdss;

import java.util.List;

import jdo.cdss.AdvicePojo;
import jdo.cdss.CDSSClientOnServer;
import jdo.cdss.CDSSClientOnServer1;
import jdo.cdss.CDSSClientOnServer2;
import jdo.cdss.CDSSClientOnServer3;
import jdo.cdss.CDSSClientOnServer4;
import jdo.cdss.CDSSClientOnServer5;
import jdo.cdss.CDSSClientOnServer6;
import jdo.cdss.CDSSClientOnServer7;
import jdo.cdss.CDSSClientOnServer8;
import jdo.cdss.ClpPojo;
import jdo.cdss.HisPojo;
import jdo.cdss.SysUtil;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;

public class CDSSAction extends TAction {
	
	public TParm fireRule1(TParm parm) throws Exception{
		SysUtil sysUtil = new SysUtil();
		HisPojo hisPojo = sysUtil.parseTParmToHisPojo(parm);
		hisPojo = CDSSClientOnServer1.getInstance().fireRules1(hisPojo);
		TParm p = sysUtil.parseHisPojoToTParm(hisPojo);
		return p;
	}
	
	public TParm fireRule2(TParm parm) throws Exception{
		SysUtil sysUtil = new SysUtil();
		HisPojo hisPojo = sysUtil.parseTParmToHisPojo(parm);
		hisPojo = CDSSClientOnServer2.getInstance().fireRules2(hisPojo);
		TParm p = sysUtil.parseHisPojoToTParm(hisPojo);
		return p;
	}
	
	public TParm fireRule3(TParm parm) throws Exception{
		SysUtil sysUtil = new SysUtil();
		HisPojo hisPojo = sysUtil.parseTParmToHisPojo(parm);
		hisPojo = CDSSClientOnServer3.getInstance().fireRules3(hisPojo);
		List<AdvicePojo> advicePojos = hisPojo.getAdvicePojos();
		for (AdvicePojo advicePojo : advicePojos) {
			System.out.println("ID: " + advicePojo.getKnowladgeId() + "  LEVEL: " + advicePojo.getLevel() + "  ADVICE: " + advicePojo.getAdviceText()
					+ " MEDQTY: " + advicePojo.getMedQty() + " UNIT: " + advicePojo.getUnit() + " FREQCODE: " + advicePojo.getFreqCode() + " ORDERCODE: " + advicePojo.getOrderCode()
					+ " REMARKS: " + advicePojo.getRemarks());
		}
		TParm p = sysUtil.parseHisPojoToTParm(hisPojo);
		return p;
	}
	
	public TParm fireRule4(TParm parm) throws Exception{
		SysUtil sysUtil = new SysUtil();
		ClpPojo clpPojo = sysUtil.parseTParmToClpPojo(parm);
		clpPojo = CDSSClientOnServer4.getInstance().fireRules4(clpPojo);
		TParm p = sysUtil.parseClpPojoToTParm(clpPojo);
		System.out.println("rule4==="+p);
		return p;
		
	}
	
	public TParm fireRule5(TParm parm) throws Exception{
		SysUtil sysUtil = new SysUtil();
		HisPojo hisPojo = sysUtil.parseTParmToHisPojo(parm);
		hisPojo = CDSSClientOnServer5.getInstance().fireRules5(hisPojo);
		TParm p = sysUtil.parseHisPojoToTParm(hisPojo);
		return p;
	}
	
	public TParm fireRule6(TParm parm) throws Exception{
		SysUtil sysUtil = new SysUtil();
		HisPojo hisPojo = sysUtil.parseTParmToHisPojo(parm);
		hisPojo = CDSSClientOnServer6.getInstance().fireRules6(hisPojo);
		List<AdvicePojo> advicePojos = hisPojo.getAdvicePojos();
		for (AdvicePojo advicePojo : advicePojos) {
			System.out.println( " MEDQTY: " + advicePojo.getMedQty() +  " ORDERCODE: " + advicePojo.getOrderCode()
					+ " REMARKS: " + advicePojo.getRemarks());
		}
		TParm p = sysUtil.parseHisPojoToTParm(hisPojo);
		return p;
	}
	//用于护士单次执行界面
	public TParm fireRule7(TParm parm) throws Exception{
		SysUtil sysUtil = new SysUtil();
		HisPojo hisPojo = sysUtil.parseTParmToHisPojo(parm);
		hisPojo = CDSSClientOnServer7.getInstance().fireRules7(hisPojo);
		TParm p = sysUtil.parseHisPojoToTParm(hisPojo);
		return p;
	}	
	//用于单病种转入界面
	public TParm fireRule8(TParm parm) throws Exception{
		SysUtil sysUtil = new SysUtil();
		HisPojo hisPojo = sysUtil.parseTParmToHisPojo(parm);
		hisPojo = CDSSClientOnServer8.getInstance().fireRules8(hisPojo);
		TParm p = sysUtil.parseHisPojoToTParm(hisPojo);
		return p;
	}	
}
