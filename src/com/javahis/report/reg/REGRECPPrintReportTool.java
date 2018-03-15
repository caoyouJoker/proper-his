package com.javahis.report.reg;

import com.dongyang.data.TParm;
import com.javahis.util.IReport;
/**
 * <p> Title: 挂号收据数据准备类 </p>
 * 
 * <p> Description: 挂号收据数据准备类  </p>
 * 
 * <p> Copyright: ProperSoft 20130730 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author wanglong
 * @version 1.0
 */
public class REGRECPPrintReportTool implements IReport {

    /**
     * 为报表传参做准备
     * @param parm
     * @return
     */
    public TParm getReportParm(TParm parm) {
        return parm;
    }
}
