package com.javahis.ui.ins;

import jdo.bil.BilInvoice;
import jdo.ekt.EKTTool;
import jdo.ins.INSTJReg;
import jdo.ins.INSTJTool;
import jdo.reg.ws.RegQETool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.device.card.IccCardRWUtil;

public class INSReadInsCardControl extends TControl{
	private IccCardRWUtil cardDev=new IccCardRWUtil();
	private TParm result=new TParm();
	
	 public void onInit() {
		 super.init();
	   }

	/* 医保卡读卡
	*/
	public void readCard(){
		TParm parm = new TParm();
		TParm result=new TParm();
		if (this.getValue("READ_TEXT").toString().length() <= 0) {
			this.messageBox("请刷卡");
			this.grabFocus("READ_TEXT");
			return;
		}
		String ektSql = "SELECT * FROM EKT_DEV WHERE IP='"+Operator.getIP()+"' ";
		TParm ektParm = new TParm(TJDODBTool.getInstance().select(ektSql));
		if(ektParm.getCount()>0){
			TParm re = cardDev.readINSCard();
			String cardNo = re.getValue("");
			
			parm.setData("TEXT", ";"+cardNo+"=");
			TParm regionParm = SYSRegionTool.getInstance().selectdata("H01");// 获得医保区域代码
			String admDate  = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd"); //拿到当前的时间
			parm.setData("ADVANCE_CODE",regionParm.getValue("NHI_NO", 0)+"@"+
					admDate+"@"+"1");//医院编码@费用发生时间@类别
			 TParm insParm = RegQETool.getInstance().readCardPat(parm);

			 String sql = "SELECT B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
						+ "FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C WHERE "
						+ "' B.IDNO= '"
						+ insParm.getValue("SID")
						+ "' AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y' ORDER BY A.CARD_NO DESC ";
			result = new TParm(TJDODBTool.getInstance().select(sql));
		}else{
			TParm readParm = INSTJTool.getInstance().DataDown_sp_U(this.getValueString("READ_TEXT"));// U方法，取卡号
			this.setValue("NHI_NO", readParm.getValue("CARD_NO"));
			String sql="SELECT  B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
		               +" FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C,REG_PATADM E,SYS_PATINFO D"
		               + " WHERE "
		               //+" E.NHI_NO='6217250200000958634'"
		               +" E.NHI_NO='"+readParm.getValue("CARD_NO")+"' "
		               //+" D.IDNO='"+insParm.getParm("opbReadCardParm").getValue("SID").trim()+"'"
		               +" AND E.MR_NO=A.MR_NO "
		               + " AND A.MR_NO=D.MR_NO "
		                 +" AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
			 result=new TParm(TJDODBTool.getInstance().select(sql));
		}
		
		
		
		
	}
	
	public void onOk(){	
		if(result.getCount()<=0){
			this.messageBox("请先刷卡");
			return;
		}
		this.setReturnValue(result);
		this.closeWindow();
	}

}
