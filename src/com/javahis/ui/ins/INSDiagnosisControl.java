package com.javahis.ui.ins;

import jdo.ins.INSTJTool;
import jdo.ins.INSUpLoadTool;
import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;


/**
 * 
 * <p>
 * Title: 诊疗项目外检备案信息下载
 * </p>
 */
public class INSDiagnosisControl extends TControl {
	private TTable table;// table数据
    //医保医院代码
    private String nhi_hosp_code;
    //医院名称
    private String nhi_hosp_desc;

	public void onInit() {
		super.onInit();
		((TTable) getComponent("Table")).addEventListener("Table->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		 table = (TTable) this.getComponent("Table");
		TParm hospParm = INSUpLoadTool.getInstance().getNhiHospCode(Operator.
                getRegion());
        this.nhi_hosp_code = hospParm.getValue("NHI_NO", 0);
        this.nhi_hosp_desc = hospParm.getValue("REGION_CHN_DESC", 0);
		onClear();

	}

	/**
	 * 增加对Table的监听
	 * 
	 * @param row
	 */
	public void onTableClicked() {
		int row = (Integer) callFunction("UI|Table|getClickedRow");
		if (row < 0)
			return;
		TTable table3 = (TTable) callFunction("UI|Table|getThis");
	}
	/**
	 * 查询
	 */
	public void onQuery() {
		TParm queryTParm = new TParm();
		TParm result = new TParm();
		 if ("".equals(this.getValue("MR_NO"))) {
	            messageBox("病案号不能为空");
	            return;
	        }
		 setValue("MR_NO", PatTool.getInstance().checkMrno(
					TypeTool.getString(getValue("MR_NO"))));
		queryTParm.setData("MR_NO", this.getValue("MR_NO"));		
		String sql =
		  " SELECT A.MR_NO,A.PAT_NAME,A.CASE_NO,A.ADM_SEQ,"+
          " A.IN_DATE,A.HIS_CTZ_CODE"+
          " FROM INS_ADM_CONFIRM A"+
          " WHERE A.MR_NO  = '"+ queryTParm.getData("MR_NO")+ "'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("result=====:"+result);
		// 判断错误值
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//执行失败
			return;
		}
		if (result.getCount()<= 0) {
			messageBox("E0008");//查无资料
			return;
		}
		((TTable) getComponent("Table")).setParmValue(result);
	}
	/**
	 * 上传
	 */
	public void onUpload() {
		 TParm UpParm = new TParm();
		 TParm result = new TParm();
		 UpParm.addData("NHI_HOSP_NO", "000551");//医院编码
		 UpParm.addData("NHI_TYPE", "2");//险种
		 UpParm.addData("NHI_CODE", "002242");//收费项目编码
		 UpParm.addData("PERSONAL_NO", "B915109123");//个人编码
		 UpParm.addData("ADM_SEQ",  "L0141405080013");//顺序号
		 UpParm.addData("OUTEXM_HOSP_NO", "000168");//项目所属医院编码
		 UpParm.addData("OUTEXM_REASON", "外检");//外检原因、目的
		 UpParm.setData("PIPELINE", "DataDown_zjks");	
		 UpParm.setData("PLOT_TYPE", "U");
		 UpParm.addData("PARM_COUNT", 7);//入参数量 
         System.out.println("UpParm:====="+UpParm);
	     result = InsManager.getInstance().safe(UpParm);
	        System.out.println("result" + result);
	        if (result.getErrCode() < 0) {
	            this.messageBox(result.getErrText());
	            return;
	        }
	        this.messageBox("上传成功");
	     //诊疗项目外检备案信息上传
        //得到诊疗项目外检备案信息的内容
//		 TParm parm = new TParm();
//        TParm Diagnosis1 = INSUpLoadTool.getInstance().getDiagnosisData1(parm);
//        System.out.println("得到诊疗项目外检备案信息Diagnosis1====" + Diagnosis1);
//        System.out.println("Diagnosis1====ORDER_CODE" + Diagnosis1.getData("ORDER_CODE"));
//        if (Diagnosis1.getErrCode() < 0) {
//            this.messageBox(Diagnosis1.getErrText());
//            return;
//        }
//        if(Diagnosis1.getData("ORDER_CODE")!=null){
//        	 //个人编码
//            String personalNo = parm.getValue("PERSONAL_NO");
//            if (this.DataUpload_U(Diagnosis1,personalNo).getErrCode() < 0)
//                return;   	 
//      }	
	}
    /**
     * 诊疗项目外检备案信息上传
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_U(TParm Diagnosis1, String personalNo) {
	    TParm Parm = new TParm();
	    TParm result = new TParm();
	    TParm UpParm = new TParm();
	    String hisctzCode = Diagnosis1.getValue("HIS_CTZ_CODE",0);
	    System.out.println("hisctzCode=====:"+hisctzCode);
	    String nhiType = "";
	    //险种类别
	    if(hisctzCode.equals("11")||
	       hisctzCode.equals("12")||
	       hisctzCode.equals("13")){
	          	nhiType = "1";//城职
	    }else 
	    if(hisctzCode.equals("21")||
	       hisctzCode.equals("22")||
	       hisctzCode.equals("23")){
	          	nhiType = "2";//城乡
	     }
	    int count = Diagnosis1.getCount("CASE_NO");
    	 for (int i = 0; i < count; i++) {
    	 Parm.setData("ORDER_CODE", Diagnosis1.getData("ORDER_CODE",i));
    	 Parm.setData("ADM_SEQ", Diagnosis1.getData("ADM_SEQ",i));
    	 TParm Diagnosis2 = INSUpLoadTool.getInstance().getDiagnosisData2(Parm);
    	 System.out.println("得到诊疗项目外检备案信息Diagnosis2====" + Diagnosis2);
     	 if(Diagnosis2.getData("ORDER_CODE")==null)
    		 return result;
    	 int cnt = Diagnosis2.getCount("ORDER_CODE");
    	 for (int j = 0; j < cnt; j++) {
    		 UpParm.addData("NHI_HOSP_NO", this.nhi_hosp_code);//医院编码
    		 UpParm.addData("NHI_TYPE", nhiType);//险种
    		 UpParm.addData("NHI_CODE", Diagnosis2.getData("NHI_ORDER_CODE",j));//收费项目编码
    		 UpParm.addData("PERSONAL_NO", personalNo);//个人编码
    		 UpParm.addData("ADM_SEQ",  Diagnosis1.getData("ADM_SEQ",i));//顺序号
    		 UpParm.addData("OUTEXM_HOSP_NO", Diagnosis1.getData("TRANS_HOSP_CODE",i));//项目所属医院编码
    		 UpParm.addData("OUTEXM_REASON", Diagnosis1.getData("DR_NOTE",i));//外检原因、目的
    		 UpParm.setData("PIPELINE", "DataDown_zjks");	
    		 UpParm.setData("PLOT_TYPE", "U");
    		 UpParm.addData("PARM_COUNT", 7);//入参数量 
             System.out.println("UpParm:====="+UpParm);
    	     result = InsManager.getInstance().safe(UpParm);
    	        System.out.println("result" + result);
    	        if (result.getErrCode() < 0) {
    	            this.messageBox(result.getErrText());
    	            return result;
    	        }   	        
    	  }   	 
     } 
    	 this.messageBox("上传成功");
    	 return result;
  }
	
	/**
	 * 下载
	 */
	public void onDownload() {
    	int Row = table.getSelectedRow();//行数
    	System.out.println("Row=====:"+Row);
		//若没有数据返回
		if (Row < 0){
			messageBox("请选择数据");
			  return;
		}		    
		TParm parm = table.getParmValue().getRow(Row);//获得数据
		System.out.println("parm=====:"+parm);
        String admSeq = parm.getValue("ADM_SEQ");
        String hisctzCode = parm.getValue("HIS_CTZ_CODE");
        System.out.println("hisctzCode=====:"+hisctzCode);
        String nhiType = "";
    	//险种类别
        if(hisctzCode.equals("11")||
           hisctzCode.equals("12")||
           hisctzCode.equals("13")){
        	nhiType = "1";//城职
        }else 
        if(hisctzCode.equals("21")||
   	       hisctzCode.equals("22")||
   	       hisctzCode.equals("23")){
        	nhiType = "2";//城乡
        }
        System.out.println("nhiType=====:"+nhiType);
         TParm downParm = new TParm();
    	 TParm result = new TParm();
    	 downParm.addData("NHI_HOSP_NO",this.nhi_hosp_code);//医院编码
    	 downParm.addData("NHI_TYPE", nhiType);//险种
    	 downParm.addData("ADM_SEQ", admSeq);//顺序号
    	 downParm.addData("PARM_COUNT", 3);//入参数量       	   	   
    	 downParm.setData("PIPELINE", "DataDown_zjkd");
    	 downParm.setData("PLOT_TYPE", "K");	  	    
         System.out.println("downParm:"+downParm);
         result = InsManager.getInstance().safe(downParm,"");
         System.out.println("result===========" + result);
         if (!INSTJTool.getInstance().getErrParm(result)) {
        	 messageBox(result.getErrText());
 			return;
 		}
	     this.messageBox("下载成功");
	     ((TTable) getComponent("TableDown")).setParmValue(result);  
	}
	/**
	 * 清空
	 */
	public void onClear() {
		((TTable) getComponent("Table")).removeRowAll();
		((TTable) getComponent("TableDown")).removeRowAll();
		this.setValue("MR_NO", "");

	}	    	 
}
