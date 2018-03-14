package action.sta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdo.cdss.CDSSClientOnServerMro;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
public class STAWSAction extends TAction {

	public TParm onTest(TParm parm) {
		TParm result = new TParm();
		Map<String,List<String>> caseMap=new HashMap<String,List<String>>();
		for (int i = 0; i < parm.getCount(); i++) {
			String caseNo = parm.getValue("CASE_NO",i);
			List<String> list = CDSSClientOnServerMro.getInstance().fireMroRule(caseNo);
			caseMap.put(caseNo, list);
		}
		result.setData("DATA", caseMap);
		return result;
	}

}
