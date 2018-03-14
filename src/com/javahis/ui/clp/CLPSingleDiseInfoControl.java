package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.*;
import jdo.clp.*;

/**
 * <p>Title: </p>
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
public class CLPSingleDiseInfoControl extends TControl {

    /**
     * 界面上的cUI对象
     */
    private TTextFormat START_DATE;
    private TTextFormat END_DATE;

    //医嘱类别
    private TRadioButton All;
    private TRadioButton DEPT;

    //科室
    private TTextFormat DEPT_CODE;
    private TComboBox CLASSIFY;
    /**
     * 初始化函数
     */
    public void onInit() {
        super.onInit();
        //本界面的初始化
        myInitControler();
    }


    /**
     * 首先得到所有UI的控件对象/注册相应的事件
     */

    public void myInitControler() {
        //得到时间控件
        END_DATE = (TTextFormat)this.getComponent("END_DATE");
        START_DATE = (TTextFormat)this.getComponent("START_DATE");
        DEPT_CODE = (TTextFormat)this.getComponent("DEPT_CODE");
        CLASSIFY = (TComboBox)this.getComponent("CLASSIFY");
        //得到查询条件UI的对象
        All = (TRadioButton)this.getComponent("All");
        DEPT = (TRadioButton)this.getComponent("DEPT");

        //初始化时间
        initDateTime();

    }

    /**
     * 初始化时间控件
     */
    public void initDateTime() {
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                "SELECT current date as DATE FROM sysibm.sysdummy1"));
//        this.messageBox_(parm);
        START_DATE.setValue(parm.getData("DATE", 0));
        END_DATE.setValue(parm.getData("DATE", 0));

    }

    public TParm getParm() {

        TParm result = new TParm();
        result.setData("START_DATE", START_DATE.getValue());
        result.setData("END_DATE", END_DATE.getValue());

        if (All.isSelected()) {
            //
        } else {
            result.setData("DEPT_CODE", DEPT_CODE.getValue());
        }

        result.setData("CLASSIFY", CLASSIFY.getValue());
        return result;
    }


    public CLPSingleDiseInfoControl() {

    }

    public void onSave() {
        String lab = CLASSIFY.getValue();
        TParm parm = new TParm();
        //导出EXCLE
        if ("ORAL.CLP_AMI".equals(lab)) {
//            this.messageBox("急性心肌梗死");
            ClpAmiTool ClpAmiTool = new ClpAmiTool();
            parm.addData("A", "AMI-1到达医院后即刻使用阿司匹林（有禁忌者应给予氯吡格雷）");
            parm.addData("A", "AMI-2实施左心室功能评价");
            parm.addData("A", "AMI-3再灌注治疗（仅适用于STEMI）");
            parm.addData("A", "实施溶栓治疗");
            parm.addData("A", "实施PCI治疗");
            parm.addData("A", "需要急诊PCI转院");
            parm.addData("A", "到达医院后即刻使用β阻滞剂，无禁忌症者");
            parm.addData("A", "AMI-5住院期间使用阿司匹林、β阻滞剂、ACEI/ARB、他汀类药物有明示（无禁忌症者）");
            parm.addData("A", "出院时继续使用阿司匹林、β阻滞剂、ACEI/ARB、他汀类药物有明示（无禁忌症者）");
            parm.addData("A", "AMI-7为患者提供健康教育");
            parm.addData("A", "AMI-8低密度脂蛋白胆固醇的评估");

            parm.addData("B", ClpAmiTool.getAmi1ERate());
            parm.addData("B", ClpAmiTool.getAmi2Rate());
            parm.addData("B", ClpAmiTool.getAmi3Rate());
            parm.addData("B", ClpAmiTool.getAmi3_1Rate());
            parm.addData("B", ClpAmiTool.getAmi3_2Rate());
            parm.addData("B", ClpAmiTool.getAmi3_3Rate());
            parm.addData("B", ClpAmiTool.getAmi4Rate());
            parm.addData("B", ClpAmiTool.getAmi5Rate());
            parm.addData("B", ClpAmiTool.getAmi6Rate());
            parm.addData("B", ClpAmiTool.getAmi7Rate());
            parm.addData("B", ClpAmiTool.getAmi8Rate());

            parm.setCount(11);

            ClpExportExcleTool.getInstance().exeSaveExcel("AMI,500;数据,100",
                    parm, "文件");
        } else if ("ORAL.CLP_CABG".equals(lab)) {
//            this.messageBox("冠状动脉旁路移植术");
            ClpCABGTool ClpCABGTool = new ClpCABGTool();
            parm.addData("A", "1.到达医院后即刻内科治疗");
            parm.addData("A", "2.手术适应征：择期");
            parm.addData("A", "3.术中选择LIMA搭桥");
            parm.addData("A", "4.1 手术前一小时开始使用");
            parm.addData("A", "4.2 手术时间超过3小时须追加一次");
            parm.addData("A", "4.3 术后120h之内结束使用");
            parm.addData("A", "5.术后无活动性出血或血肿再手术");
            parm.addData("A", "6.手术后无并发症");
            parm.addData("A", "7.切口愈合：Ⅰ甲");
            parm.addData("A", "8.住院21天内出院");

            parm.addData("B", ClpCABGTool.getCABG1Rate());
            parm.addData("B", ClpCABGTool.getCABG2_2Rate());
            parm.addData("B", ClpCABGTool.getCABG3_1Rate());
            parm.addData("B", ClpCABGTool.getCABG4_1Rate());
            parm.addData("B", ClpCABGTool.getCABG4_2Rate());
            parm.addData("B", ClpCABGTool.getCABG4_3Rate());
            parm.addData("B", ClpCABGTool.getCABG5Rate());
            parm.addData("B", ClpCABGTool.getCABG6Rate());
            parm.addData("B", ClpCABGTool.getCABG7Rate());
            parm.addData("B", ClpCABGTool.getCABG9Rate());

            parm.setCount(10);

            ClpExportExcleTool.getInstance().exeSaveExcel("CABG,500;数据,100",
                    parm, "文件");

        } else if ("ORAL.CLP_HF".equals(lab)) {
//            this.messageBox("心力衰竭");
            ClpHFTool ClpHFTool = new ClpHFTool();
            parm.addData("A", "1.实施左心室功能评价：胸片");
            parm.addData("A", "1.实施左心室功能评价：CDFI");
            parm.addData("A", "2.利尿剂+钾剂");
            parm.addData("A", "3.血管紧张素转换酶（ACE）抑制剂或血管紧张素Ⅱ受体拮抗剂（ARB）");
            parm.addData("A", "4.β-受体阻滞剂");
            parm.addData("A", "5.醛固酮拮抗剂（重度心衰）");
            parm.addData("A",
                         "6.住院期间维持使用利尿剂、钾剂、ACEI/ARBs、β-B和醛固酮拮抗剂（有适应症，若无副作用）");
            parm.addData("A",
                         "7.出院时继续使用利尿剂、ACEI/ARBs、β-B和醛固酮拮抗剂（有适应症，若无副作用）有明示");
            parm.addData("A", "8.非药物治疗：心脏同步化治疗（有适应症）");
            parm.addData("A", "9.为患者提供健康教育");

            parm.addData("B", ClpHFTool.getHf1Rate());
            parm.addData("B", ClpHFTool.getHf1_1Rate());
            parm.addData("B", ClpHFTool.getHf2Rate());
            parm.addData("B", ClpHFTool.getHf3Rate());
            parm.addData("B", ClpHFTool.getHf4Rate());
            parm.addData("B", ClpHFTool.getHf5Rate());
            parm.addData("B", ClpHFTool.getHf6Rate());
            parm.addData("B", ClpHFTool.getHf7Rate());
            parm.addData("B", ClpHFTool.getHf8Rate());
            parm.addData("B", ClpHFTool.getHf9Rate());

            parm.setCount(10);

            ClpExportExcleTool.getInstance().exeSaveExcel("HF,500;数据,100", parm,
                    "文件");

        } else {
            this.messageBox("请选择一种病种导出");
            return;
        }


    }


    public void onQuery() {
//        this.messageBox_(getParm());
        String sql = "";
        sql = getSql(getParm());
//        System.out.println("===>"+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//        this.messageBox_(parm);


    }

    public String getSql(TParm parm) {
        String str = "";
        str = " SELECT * FROM " + parm.getData("CLASSIFY") + " WHERE " +
              " IN_DATE BETWEEN DATE((char('" + parm.getData("START_DATE") +
              "'))) AND DATE((char('" + parm.getData("END_DATE") + "'))) " +
              (parm.getData("DEPT_CODE") == null ? "" :
               " AND DEPT_CODE='" + parm.getData("DEPT_CODE") + "'");

        return str;
    }

    public void onClear() {
        initDateTime();
        All.setSelected(true);
        DEPT_CODE.setText("");
        CLASSIFY.setText("");

    }

    public void onDept() {
        DEPT_CODE.setEnabled(true);
    }


}
