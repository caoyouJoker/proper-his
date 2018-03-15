package com.javahis.report.bil;

import com.dongyang.data.TParm;
import com.javahis.util.IReport;
/**
 * <p> Title: 补款退款明细表数据准备类 </p>
 * 
 * <p> Description: 补款退款明细表数据准备类  </p>
 * 
 * <p> Copyright: ProperSoft 20130730 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author wanglong
 * @version 1.0
 */
public class BILPayTypeDetailReportTool implements IReport {

    /**
     * 准备报表传参
     * @param parm
     * @return
     */
    public TParm getReportParm(TParm parm) {
        return parm;
    }
}
