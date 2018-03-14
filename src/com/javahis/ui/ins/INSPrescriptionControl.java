package com.javahis.ui.ins;

import jdo.ins.INSTJTool;
import jdo.ins.INSUpLoadTool;
import jdo.ins.InsManager;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
/**
 * Title:外购处方上传和删除
 * Description:外购处方上传和删除
 * Company:Javahis
 * @author yufh 2014-04-08
 * @version 2.0
 */
public class INSPrescriptionControl  extends TControl{
	private TTable table;// table数据
    //医保医院代码
    private String nhi_hosp_code;
    //医院名称
    private String nhi_hosp_desc;
    String caseno = "";
	public void onInit() {
		super.onInit();
		//得到前台传来的数据并显示在界面上
		TParm parm = (TParm) getParameter();
	    if (null == parm) {
            return;
        }
	    caseno = parm.getValue("CASE_NO");
	    this.setValue("MR_NO",parm.getValue("MR_NO"));
        this.setValue("PAT_NAME",parm.getValue("PAT_NAME"));
		   //table1的单击侦听事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        table = (TTable) this.getComponent("TABLE");
		TParm hospParm = INSUpLoadTool.getInstance().getNhiHospCode(Operator.
                getRegion());
        this.nhi_hosp_code = hospParm.getValue("NHI_NO", 0);
        this.nhi_hosp_desc = hospParm.getValue("REGION_CHN_DESC", 0);
        //查询是否有外购处方
        onQuery();
	}
	  /**
     *增加对Table的监听
     * @param row
     */
    public void onTableClicked(int row) {
        //接收所有事件
        this.callFunction("UI|TABLE|acceptText");
//   TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
    }
	/**
	 * 查询
	 */
	public void onQuery(){
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseno);
		String sql1 =
	   		 " SELECT A.CASE_NO,A.RX_NO FROM OPD_ORDER A"+
             " WHERE A.CASE_NO = '"+ parm.getData("CASE_NO")+ "'"+
             " AND A.CAT1_TYPE = 'PHA'"+
             " AND A.RELEASE_FLG ='Y'"+
             " GROUP BY A.CASE_NO,A.RX_NO";
	   	 TParm Parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
//   	  System.out.println("Parm1========" + Parm1); 
			if(Parm1.getErrCode()<0 ){
				messageBox("E0005");//执行失败
				err(Parm1.getErrText()+":"+Parm1.getErrName());
				return;
			}
	   	 if (Parm1.getCount() <= 0) {
    		 messageBox("E0008");//查无资料
    		 return ; 
    	 }
		this.callFunction("UI|TABLE|setParmValue", Parm1);
	}
	/**
     * 上传
     */
    public void onUpload() {
    	int Row = table.getSelectedRow();//行数
		//若没有数据返回
		if (Row < 0) {
			messageBox("请选择数据");
		    return;
		}
		TParm parm = table.getParmValue().getRow(Row);//获得数据
		String sql2 =
  		" SELECT A.PAT_NAME,A.IDNO AS ID_NO,A.OWN_NO,A.INS_CROWD_TYPE,A.INS_PAT_TYPE"+
        " FROM INS_MZ_CONFIRM A"+
        " WHERE A.CASE_NO = '"+ parm.getValue("CASE_NO")+ "'";
  	    TParm Parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
//	  System.out.println("Parm2=========" + Parm2); 
  	 if (Parm2.getCount() <= 0) {
		 messageBox("E0008");
		 return ; 
	 }
   	 //用于病种编码使用
  	 String diseasecode ="";
  	 String inscrowdtype =Parm2.getValue("INS_CROWD_TYPE", 0);
  	 String inspattype =Parm2.getValue("INS_PAT_TYPE", 0); 	 
  	 if(inscrowdtype.equals("1")&&inspattype.equals("1"))
  		diseasecode ="01";
  	 else if ((inscrowdtype.equals("1")&&inspattype.equals("2"))||
  			  (inscrowdtype.equals("2")&&inspattype.equals("2")))
  		diseasecode ="4";
   	TParm upParm = new TParm();
    TParm result = new TParm();
   	String sql3 =
   		" SELECT A.RX_NO,B.DR_QUALIFY_CODE,B.USER_NAME AS ORDER_DR_NAME,"+
        " D.ICD_CHN_DESC AS DIAGE_DESC,TO_CHAR(A.ORDER_DATE,'YYYYMMDD') AS ORDER_DATE,"+
   		" F.SFXMBM,A.ORDER_DESC,G.ROUTE_CHN_DESC,A.MEDI_QTY,A.DISPENSE_QTY," +
   		" (CASE WHEN H.OTC_FLG='Y' THEN '1' ELSE '0'  END ) AS OTC_FLG"+
   		" FROM OPD_ORDER A, SYS_OPERATOR B,OPD_DIAGREC C,SYS_DIAGNOSIS D," +
   		" SYS_FEE_HISTORY  E,INS_RULE F,SYS_PHAROUTE G,PHA_BASE H "+
   		" WHERE A.CASE_NO = '"+ parm.getValue("CASE_NO")+ "'"+
   		" AND A.RX_NO = '"+ parm.getValue("RX_NO")+ "'" +
   		" AND A.RELEASE_FLG ='Y'"+
   		" AND A.DR_CODE  = B.USER_ID"+
   		" AND A.CASE_NO = C.CASE_NO"+
   		" AND C.MAIN_DIAG_FLG ='Y'"+
   		" AND C.ICD_CODE = D.ICD_CODE"+
   		" AND A.ORDER_CODE =E.ORDER_CODE"+
   		" AND E.ORDER_CODE = H.ORDER_CODE"+   		
   		" AND E.NHI_CODE_O =F.SFXMBM"+
   		" AND A.ROUTE_CODE = G.ROUTE_CODE"+
   		" AND TO_CHAR(A.ORDER_DATE,'YYYYMMDD') >= E.START_DATE"+
   		" AND TO_CHAR(A.ORDER_DATE,'YYYYMMDD')< =E.END_DATE"+
   		" AND A.ORDER_DATE BETWEEN F.KSSJ AND F.JSSJ";
   	TParm Parm3 = new TParm(TJDODBTool.getInstance().select(sql3));
//  	  System.out.println("Parm3========" + Parm3); 
	   	 if (Parm3.getCount() <= 0) {
	   		 messageBox("E0008");
	   		 return; 
	   	 }
	   	 //处方内容
	   	String rxdesc = "";
  	 int count1 = Parm3.getCount("RX_NO");	  	
  	      for (int j = 0; j < count1; j++) {
  	    	rxdesc+=Parm3.getData("SFXMBM", j)+"@"+//药品医保编码
  	    	Parm3.getData("ORDER_DESC", j)+"@"+//药品名称
  	    	Parm3.getData("ROUTE_CHN_DESC", j)+"@"+//用法
  	    	Parm3.getData("MEDI_QTY", j)+"@"+//用量
  	    	Parm3.getData("DISPENSE_QTY", j)+"@"+//数量
  	    	Parm3.getData("OTC_FLG", j)+"%";//OTC标识  	  	    	  
  	      }  
  	    upParm.addData("RX_NO", parm.getValue("RX_NO"));//处方编号
	    upParm.addData("NHI_HOSP_NO",this.nhi_hosp_code);//药店编码（或医院编码）
	    upParm.addData("DISEASE_CODE",diseasecode);//病种编码
	    upParm.addData("ORDER_HOSP_NO", this.nhi_hosp_code);//开具医院编码
	    upParm.addData("ORDER_HOSP_NAME", this.nhi_hosp_desc);//开具医院名称
	    upParm.addData("ORDER_DR_CODE", Parm3.getValue("DR_QUALIFY_CODE", 0));//开具医师编码
	    upParm.addData("ORDER_DR_NAME", Parm3.getValue("ORDER_DR_NAME", 0));//开具医师姓名
	    upParm.addData("PAT_NAME", Parm2.getValue("PAT_NAME", 0));//患者姓名
	    upParm.addData("ID_NO", Parm2.getValue("ID_NO", 0));//患者身份号码
	    upParm.addData("NHI_CARD_NO", "");//患者医保卡号
	    upParm.addData("OWN_NO", Parm2.getValue("OWN_NO", 0));//个人编码
	    upParm.addData("DIAGE_DESC", Parm3.getValue("DIAGE_DESC", 0));//诊断
	    upParm.setData("RX_DESC",0,rxdesc.length()>0? 
  	    		rxdesc.substring(0, rxdesc.length() - 1):"");//处方内容
	    upParm.addData("ORDER_DATE", Parm3.getValue("ORDER_DATE", 0));//处方开具时间
	    upParm.addData("DATA_TYPE", "1");//数据来源	
	    upParm.addData("PARM_COUNT", 15);//入参数量       	   	   
	  	upParm.setData("PIPELINE", "DataDown_zjks");
	  	upParm.setData("PLOT_TYPE", "N");	  	    
        System.out.println("upParm:"+upParm);
        result = InsManager.getInstance().safe(upParm);
//        System.out.println("result====onUpload" + result);
        if (!INSTJTool.getInstance().getErrParm(result)) {
       	 messageBox(result.getErrText());
			return;
		}
         this.messageBox("上传成功");	 
    }
    /**
    * 删除
    */
   public void onDelete() {
	   int Row = table.getSelectedRow();//行数
		//若没有数据返回
		if (Row < 0){ 
			messageBox("请选择数据");
		    return;
		}
		TParm parm = table.getParmValue().getRow(Row);//获得数据
		String sql2 =
  		" SELECT A.IDNO AS ID_NO"+
        " FROM INS_MZ_CONFIRM A"+
        " WHERE A.CASE_NO = '"+ parm.getValue("CASE_NO")+ "'";
  	    TParm Parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
  		TParm deleteParm = new TParm();
  	    TParm result = new TParm();
  	  deleteParm.addData("RX_NO", parm.getValue("RX_NO"));//处方编号
  	  deleteParm.addData("NHI_HOSP_NO",this.nhi_hosp_code);//药店编码（或医院编码）
  	  deleteParm.addData("ID_NO", Parm2.getValue("ID_NO", 0));//患者身份号码
  	  deleteParm.addData("PARM_COUNT", 3);//入参数量       	   	   
  	  deleteParm.setData("PIPELINE", "DataDown_zjks");
  	  deleteParm.setData("PLOT_TYPE", "V");	  	    
//      System.out.println("deleteParm:"+deleteParm);
      result = InsManager.getInstance().safe(deleteParm);
//      System.out.println("result====onDelete" + result);
      if (!INSTJTool.getInstance().getErrParm(result)) {
     	 messageBox(result.getErrText());
			return;
		}
        this.messageBox("删除成功");    
   }
	/**
    * 清空
    */
   public void onClear() {
	   getTable("TABLE").removeRowAll();
   }
	/**
	 * 获取table对象
	 * @param tableName
	 * @return
	 */
	public TTable getTable(String tableName) {
		return (TTable) this.getComponent(tableName);
	}

}
