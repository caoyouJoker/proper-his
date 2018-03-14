package com.javahis.ui.ekt;

import jdo.sys.Operator;
import jdo.opb.OPB;
import jdo.reg.PatAdmTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import java.sql.Timestamp;

/**
 * <p>Title: 医疗卡就诊号选择</p>
 *
 * <p>Description: 医疗卡就诊号选择</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20111008
 * @version 1.0
 */
public class EKTChooseVisitControl  extends TControl {
   int selectrow = -1;
   OPB opb=new OPB();
   /**
    * 初始化界面
    */
   public void onInit() {
       super.onInit();
       //得到前台传来的数据并显示在界面上
       TParm recptype = (TParm) getParameter();
       if (!recptype.getData("count").equals("0")) {
           setValueForParm("MR_NO;PAT_NAME;SEX_CODE;AGE", recptype.getParm("PARM"),
                           -1);
           callFunction("UI|TABLE|setParmValue", recptype.getParm("RESULT"));
       }
       if (recptype.getData("count").equals("0")) {
           setValueForParm("MR_NO;PAT_NAME;SEX_CODE;AGE", recptype, -1);
       }
       //预设就诊时间段
       this.callFunction("UI|STARTTIME|setValue",
                         SystemTool.getInstance().getDate());
       this.callFunction("UI|ENDTIME|setValue",
                         SystemTool.getInstance().getDate());
       //table1的单击侦听事件
       callFunction("UI|TABLE|addEventListener",
                    "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
       //table1的单击侦听事件
       callFunction("UI|TABLE|addEventListener",
                    "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                    "onTableDoubleClicked");
       //默认Table上显示当天挂号记录
//    onQuery();
   }

   /**
    *增加对Table的监听
    * @param row int
    */
   public void onTableClicked(int row) {
       //接收所有事件
       this.callFunction("UI|TABLE|acceptText");
       selectrow = row;
   }

   public void onTableDoubleClicked(int row) {
       TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
       this.setReturnValue( (String) data.getData("CASE_NO", row));
       this.callFunction("UI|onClose");
   }

   /**
    * 查询
    */
   public void onQuery() {
       //==========pangben modify 20110421 start 添加参数因为selDateByMrNo有重载所以添加数组参数
       String regionCode = Operator.getRegion();
       TParm parm = PatAdmTool.getInstance().selDateByMrNo(getValueString("MR_NO"),
           (Timestamp) getValue("STARTTIME"), (Timestamp) getValue("ENDTIME"),regionCode);
       //==========pangben modify 20110421 start
       if (parm.getCount() < 0)
           return;
       if (parm.getCount() == 0)
           this.messageBox("无挂号信息!");
       this.callFunction("UI|TABLE|setParmValue", parm);
   }

   /**
    *
    */
   public void onOK() {
       TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
       this.setReturnValue( (String) data.getData("CASE_NO", selectrow));
       this.callFunction("UI|onClose");
   }

}
