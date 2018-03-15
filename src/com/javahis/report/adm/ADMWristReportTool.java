package com.javahis.report.adm;

import com.dongyang.data.TParm;
import com.javahis.util.IReport;
/**
 * <p> Title: 腕带打印数据准备类 </p>
 * 
 * <p> Description: 腕带打印数据准备类  </p>
 * 
 * <p> Copyright: ProperSoft 20130730 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author wanglong
 * @version 1.0
 */
public class ADMWristReportTool implements IReport {

    /**
     * 准备报表传参
     * @param parm
     * @return
     */
    public TParm getReportParm(TParm parm) {
        return parm;
    }
}
