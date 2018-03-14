package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import jdo.sys.Operator;
import jdo.sys.SYSOrderSetDetailTool;

import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TTableEvent;
import jdo.ins.INSRuleTXTool;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TNull;

/**
 * <p>Title: 医保卡三目字典:支付标准档</p>
 *
 * <p>Description:医保卡三目字典:支付标准档 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author pangben 2011-11-08
 * @version 1.0
 */
public class INSRuleTXControl extends TControl {
    private static String TABLE = "TABLE";
    private int rowOnly = -1;
    private TParm parmReturn ;//项目编码数据
    private TParm parmDetail;//集合医嘱细项
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
       callFunction("UI|" + TABLE + "|addEventListener",
                    TABLE + "->" + TTableEvent.CLICKED, this, "onTableClicked");
       //调用出生地弹出框
        callFunction("UI|SFXMBM|setPopupMenuParameter", "aaa",
                     "%ROOT%\\config\\sys\\SYSFeePopupToINS.x");
        //textfield接受回传值
        callFunction("UI|SFXMBM|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        this.setValue("REGION_CODE",Operator.getRegion());

       onClear();
    }
    /**
   *增加对Table的监听
   * @param row int
   */
  public void onTableClicked(int row) {
      if(row < 0)
            return;
        callFunction("UI|REGION_CODE|setEnabled", false);//区域
        //callFunction("UI|XMBM|setEnabled", false);//服务项目编码
        callFunction("UI|CTZ_CODE|setEnabled", false);
        callFunction("UI|delete|setEnabled",true);
        TParm parm = (TParm) callFunction("UI|" + TABLE + "|getParmValue");
        setValueForParm(
            "SFXMBM;XMBM;XMMC;XMLB;BZJG;SJJG;XMRJ;TXBZ;BZ",
            parm, row);
        this.setValue("REGION_CODE",parm.getValue("SFDLBM",row));
        //TParm parm = this.getSelectRowData(TABLE);
        rowOnly = row;
  }
  /**
  * 得到选中行数据
  * @param tableTag String
  * @return TParm
  */
 public TParm getSelectRowData(String tableTag){
     int selectRow = (Integer) callFunction("UI|" + tableTag +"|getSelectedRow");
     out("行号" + selectRow);
     TParm parm = (TParm) callFunction("UI|" + tableTag + "|getParmValue");
     out("GRID数据" + parm);
     TParm parmRow = parm.getRow(selectRow);
     parmRow.setData("OPT_USER",Operator.getID());
     parmRow.setData("OPT_TERM",Operator.getIP());
     return parmRow;
 }
  /**
   * 查询
   */
  public void onQuery() {
//      out("查询begin");
//      callFunction("UI|" + TABLE + "|onQuery");
//      int row = (Integer) callFunction("UI|" + TABLE + "|getRowCount");
//      if(row < 0)
//          this.onClear();
//      out("查询end");
      TParm parm=new TParm();
      if(this.getValue("REGION_CODE").toString().length()>0){
          parm.setData("SFDLBM",this.getValue("REGION_CODE").toString());
      }
      if (this.getValue("SFXMBM").toString().length() > 0) {
          //大类项目编码
          parm.setData("SFXMBM", this.getValue("SFXMBM").toString());
      }
      if(this.getValue("XMBM").toString().length() > 0){
          //服务项目编码
          parm.setData("XMBM", this.getValue("XMBM").toString());
      }
      TParm data=INSRuleTXTool.getInstance().selectINSRule(parm);
      if(data.getCount()<=0){
          this.messageBox("没有需要查询的数据");
          return;
      }
      ((TTable) getComponent(TABLE)).setParmValue(data);
  }
  /**
   * 保存
   */
  public void onSave() {
      TParm parm = new TParm();
      parm.setData("OPT_USER", Operator.getID());
      parm.setData("OPT_TERM", Operator.getIP());
      TParm result=null;
      if (rowOnly >=0) {
          if (!emptyTextCheck("REGION_CODE,SFXMBM,XMBM,BZJG,SJJG,XMLB"))
              return;
          result=INSRuleTXTool.getInstance().updateINSRule(getTemp(parm));
          if (result.getErrCode()<0) {
              messageBox_("保存失败");
              return;
          }
          messageBox_("保存成功");

      } else {
          if (!emptyTextCheck("REGION_CODE,SFXMBM,XMBM,BZJG,SJJG,XMLB"))
              return;
          if(parmDetail.getCount("ORDER_CODE")>0){
        	  addParmDetail(parmDetail.getCount());
          }else{
        	  parmDetail=new TParm();
        	  addParmDetail(0);
          }
          result = TIOM_AppServer.executeAction("action.ins.INSRuleTXAction",
					"insertINSRule", parmDetail);
          //result=INSRuleTXTool.getInstance().insertINSRule(getTemp(parm));
          if (result.getErrCode()<0) {
              messageBox_("新增失败");
              return;
          }
          messageBox_("新增成功");

      }
      this.onClear();
      onQuery();
      out("保存end");
  }
  /**
   * 添加一条医嘱
   * @param i
   */
  private void addParmDetail(int i){
	  parmDetail.setData("SFXMBM",i,this.getValue("SFXMBM"));// 服务名称
	  parmDetail.setData("ORDER_CODE", parmReturn.getValue("ORDER_CODE", i));// 项目编码
	  parmDetail.setData("ORDER_DESC",i,parmReturn.getValue("ORDER_DESC"));// 服务名称
	  parmDetail.setData("PY1",i,parmReturn.getValue("PY1"));// 拼音
	  parmDetail.setData("OWN_PRICE",i,parmReturn.getDouble("OWN_PRICE"));// 实收金额
	  parmDetail.setData("DOSE_DESC",i,parmReturn.getValue("DOSE_DESC"));// 剂型
	  parmDetail.setData("SPECIFICATION",i,parmReturn.getValue("SPECIFICATION"));// 规格
	  parmDetail.setData("MEDI_UNIT_DESC",i,parmReturn.getValue("MEDI_UNIT_DESC"));// 单位:开药单位
	  parmDetail.setData("ROUTE_DESC",i,parmReturn.getValue("ROUTE_DESC"));// 用法
	  parmDetail.setData("BZ", this.getValue("BZ"));
  }
  /**
   * 修改参数
   * @param parm TParm
   * @return TParm
   */
  private TParm getTemp(TParm parm){
      parm.setData("SFDLBM",Operator.getRegion());//区域
      parm.setData("SFXMBM",this.getValue("SFXMBM"));//项目编码
      parm.setData("XMBM",this.getValue("XMBM"));//服务编码
      parm.setData("XMMC",this.getValue("XMMC"));//服务名称
      parm.setData("XMRJ",this.getValue("XMRJ"));//拼音
      parm.setData("BZJG",this.getValueDouble("BZJG"));//标准金额
      parm.setData("SJJG",this.getValueDouble("SJJG"));//实收金额
      parm.setData("XMLB",this.getValue("XMLB"));//项目类别
      if(null!=parmReturn && null!=parmReturn.getValue("ORDER_CODE")){
        parm.setData("JX",updateNull(parmReturn.getValue("DOSE_DESC")));  //剂型
        parm.setData("GG",updateNull(parmReturn.getValue("SPECIFICATION")));  //规格
        parm.setData("DW",updateNull(parmReturn.getValue("MEDI_UNIT_DESC")));  //单位:开药单位
        parm.setData("YF",updateNull(parmReturn.getValue("ROUTE_DESC")));  //用法
       // result.setData("YL",updateNull(parmReturn.getValue("DOSE_CODE")));  //用量
        parm.setData("SL",1);  //数量:开药数量
       // result.setData("PZWH",updateNull(parmReturn.getValue("DOSE_CODE")));  //批准文号
      }else{
          TParm tableParm = ((TTable) getComponent(TABLE)).getParmValue().
                            getRow(rowOnly);
          parm.setData("JX", updateNull(tableParm.getValue("JX"))); //剂型
          parm.setData("GG", updateNull(tableParm.getValue("GG"))); //规格
          parm.setData("DW", updateNull(tableParm.getValue("DW"))); //单位
          parm.setData("YF", updateNull(tableParm.getValue("YF"))); //用法
          //  result.setData("YL", updateNull(parm.getValue("YL"))); //用量
          parm.setData("SL", updateNull(tableParm.getValue("SL"))); //数量
          // result.setData("PZWH", updateNull(parm.getValue("PZWH"))); //批准文号
      }
      parm.setData("BZ",this.getValue("BZ"));
      return parm;
  }
  private Object updateNull(String name){
      if(null==name){
          return "";
      }else
          return name;
  }
  /**
   * 删除
   */
  public void onDelete() {
      if (messageBox("提示信息", "是否删除?", this.YES_NO_OPTION) != 0)
          return;
      int row = (Integer) callFunction("UI|" + TABLE + "|getSelectedRow");
      if (row < 0)
          return;
      TParm parm= ((TTable) getComponent(TABLE)).getParmValue().getRow(row);
      TParm result=INSRuleTXTool.getInstance().deleteINSRule(parm);
      if (result.getErrCode()<0) {
          messageBox_("删除失败");
          return;
      }
      messageBox_("删除成功");
      this.onClear();
      onQuery();
//      int rows = (Integer) callFunction("UI|" + TABLE + "|getSelectedRow");
//      if (rows < 0) {
//          this.onClear();
//      }
//      out("删除end");
  }

  /**
   *清空
   */
  public void onClear() {
      clearValue("REGION_CODE;SFXMBM;XMBM;XMMC;XMLB;BZJG;SJJG;XMRJ;TXBZ;BZ");
      callFunction("UI|" + TABLE + "|clearSelection");
       ((TTable) getComponent(TABLE)).removeRowAll();
      callFunction("UI|save|setEnabled", true);
      callFunction("UI|delete|setEnabled", false);
      this.setValue("REGION_CODE",Operator.getRegion());
      parmReturn=null;
//      callFunction("UI|NHI_COMPANY|setEnabled",true);
//      callFunction("UI|FEE_TYPE|setEnabled",true);
//      callFunction("UI|CTZ_CODE|setEnabled",true);
      rowOnly = -1;
  }
  /**
   * 新建创建服务编码号
   */
  public void onNew() {
    if(this.getValue("XMBM").toString().length()<=0 && !((TTextField)this.getComponent("XMBM")).isEnabled()){
        this.setValue("XMBM", SystemTool.getInstance().getNo("ALL", "INS",
                "INSRULE_NO", "INSRULE_NO"));
    }
    callFunction("UI|XMBM|setEnabled", false);
  }
  /**
     *
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
         parmReturn= (TParm) obj;
//         parm.getValue("HOMEPLACE_DESC");
        //System.out.println("parm::::"+parmReturn);
        //查询集合医嘱添加集合医嘱细项
      // 查询细相数据，填充细相
 		parmDetail = SYSOrderSetDetailTool.getInstance()
 				.selectByOrderSetCode(parmReturn.getValue("ORDER_CODE"));
 		if (parmDetail.getErrCode() != 0) {
 			this.messageBox("E0050");
 			return;
 		}
 		//存在集合医嘱细项
 		double sum=parmReturn.getDouble("OWN_PRICE");
 		if (parmDetail.getCount("ORDER_CODE")>0) {
			for (int i = 0; i < parmDetail.getCount("ORDER_CODE"); i++) {
				sum+=parmDetail.getDouble("OWN_PRICE",i);
			}
		}
        this.setValue("SFXMBM", parmReturn.getValue("ORDER_CODE"));
        this.setValue("XMMC", parmReturn.getValue("ORDER_DESC"));
        this.setValue("XMRJ", parmReturn.getValue("PY1"));
        this.setValue("BZJG", sum);
        this.setValue("SJJG", sum);
        this.setValue("BZ", parmReturn.getValue("DESCRIPTION"));

//         this.setValue("DIAG_DESC",parm.getValue("ICD_CHN_DESC"));
//         this.grabFocus("DIAG_REMARK");
    }

}
