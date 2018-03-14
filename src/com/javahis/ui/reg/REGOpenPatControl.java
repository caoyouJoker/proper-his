package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import jdo.sys.Pat;
import jdo.reg.Reg;
import com.javahis.util.JavaHisDebug;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.DeptTool;
import com.dongyang.data.TParm;
import jdo.reg.PanelTypeTool;


/**
 *
 * <p>Title:保存病患信息提示框 </p>
 *
 * <p>Description:保存病患信息提示框 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.10.10
 * @version 1.0
 */
public class REGOpenPatControl extends TControl{
    private Reg reg;
    public void onInit() {
        super.onInit();
        reg=(Reg)this.getParameter();
        setValue("MR_NO",reg.getPat().getMrNo());
        setValue("PAT_NAME",reg.getPat().getName());
        setValue("DEPT_CODE",DeptTool.getInstance().getDescByCode(reg.getDeptCode()));
        setValue("CLINICTYPE_CODE",PanelTypeTool.getInstance().getDescByCode(reg.getClinictypeCode()));
        setValue("QUE_NO",TCM_Transform.getString(reg.getQueNo()));
    }

}
