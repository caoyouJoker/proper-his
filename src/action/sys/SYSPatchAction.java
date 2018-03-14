package action.sys;


import com.dongyang.Service.Server;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.patch.Patch;

/**
 * <p>Title: 执行批次</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SYSPatchAction
    extends TAction {
    public SYSPatchAction() {
    }

    /**
     * 批次立即执行
     * @param parm TParm
     * @return TParm
     */
    public TParm onServerAction(TParm parm) {
        String src = parm.getValue("PATCH_SRC");
        Object actionObject = Server.getConfigParm().loadObject(src);
        TParm result = new TParm();
        if (actionObject == null){
            result.setData("MESSAGE", src + "不存在");
            result.setErr(-1,src + "不存在");
            return result;
        }
        Patch patch = (Patch) actionObject;
        TParm parmPatch = new TParm(TJDODBTool.getInstance().select(
            "SELECT * FROM SYS_PATCH_PARM WHERE PATCH_CODE='" +
            parm.getValue("PATCH_CODE") + "'"));
        //System.out.println("parmPatch"+parmPatch);
        int count = parmPatch.getCount();
        TParm patchParm = new TParm();
        for (int i = 0; i < count; i++) {
            String name = parmPatch.getValue("PATCH_PARM_NAME", i);
            String value = parmPatch.getValue("PATCH_PARM_VALUE", i);
            patchParm.setData(name, value);
        }
        //System.out.println("patchParm"+patchParm);
        patch.setParm(patchParm);
        //System.out.println("------------");
        boolean status = patch.run();
        //System.out.println("status"+status+ "--"+ patch.getMessage());
        result.setData("MESSAGE", patch.getMessage());
        if (!status) {
            result.setErr( -1, patch.getMessage());
            return result;
        }
        //System.out.println(result);
        return result;
    }
    
    /**
     * 部门立即执行批次
     * @param parm TParm
     * @return TParm
     */
    public TParm onImmeServerAction(TParm parm) {
    	System.out.println("部门立即执行批次");   
    	System.out.println("部门立即执行批次Parm"+parm);
        String src = parm.getValue("PATCH_SRC");
        Object actionObject = Server.getConfigParm().loadObject(src);
        TParm result = new TParm();
        if (actionObject == null){
            result.setData("MESSAGE", src + "不存在"); 
            result.setErr(-1,src + "不存在");
            return result;   
        }    
        Patch patch = (Patch) actionObject;
        TParm parmPatch = new TParm(TJDODBTool.getInstance().select(
            "SELECT * FROM SYS_PATCH_PARM WHERE PATCH_CODE='" +
            parm.getValue("PATCH_CODE") + "'"));  
        System.out.println("parmPatch"+parmPatch);
        int count = parmPatch.getCount(); 
        TParm patchParm = new TParm();
        for (int i = 0; i < count; i++) {
            String name = parmPatch.getValue("PATCH_PARM_NAME", i);
            String value = parmPatch.getValue("PATCH_PARM_VALUE", i);
            patchParm.setData(name, value);
        }
        patchParm.setData("DEPT_CODE", parm.getValue("DEPT_CODE")); 
        //System.out.println("patchParm"+patchParm);
        patch.setParm(patchParm);
//        ((INVImmediatelyBatchPatchAction)patch).setDeptCode(parm.getValue("DEPT_CODE"));
//        patch.setDeptCode(parm.getValue("DEPT_CODE"));
        //System.out.println("------------");
        boolean status = patch.run();
        //System.out.println("status"+status+ "--"+ patch.getMessage());
        result.setData("MESSAGE", patch.getMessage());
        if (!status) {
            result.setErr( -1, patch.getMessage());
            return result;
        }
        //System.out.println(result);
        return result;
    }
    
}
