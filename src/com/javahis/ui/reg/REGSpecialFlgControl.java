package com.javahis.ui.reg;

import org.seraph.antlr.expression.ExpressionParser.object_return;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTextField;
import com.javahis.ui.customquery.ParmInputControl;
import com.javahis.util.StringUtil;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * 门急诊挂号VVIP验证
 * 原始版本武新月于2017年10月开发
 * 发现BUG由张磊于2017年11月修改
 */
public class REGSpecialFlgControl extends TControl{
	
	String OPT_USER = Operator.getID();//当前操作人
	String OPT_DATE = SystemTool.getInstance().getDate().toString().substring(0,19).replaceAll("-", "/");
	String OPT_TERM = Operator.getIP();//当前IP
	String mrNo = "";
	TParm KG = new TParm();//开关 来判定是用户手动关的窗口还是通过验证自动关的窗口
	
	/**
	 * 初始化方法
	 */
	public void onInit(){
//		TTextField aa = (TTextField) this.getComponent("OPT_USER");
//		aa.setValue(OPT_USER);
		callFunction("UI|OPT_USER|setValue", OPT_USER);
		KG.setData("kg", 0, "N");//初始化开关
	}
	
	public void onOk(){
		//得到前台传过来的数据
		Object b = this.getParameter();
		if(b instanceof TParm){
			Object obj = this.getParameter();
			TParm parm = null;
			if (obj instanceof TParm){
				parm = (TParm) obj;
			}
			mrNo = parm.getValue("MR_NO");
//			this.messageBox(mrNo);
		}
		String AUTH_CODE = this.getValueString("AUTH_CODE");
		String authCodeDX = AUTH_CODE.toUpperCase();
		if(StringUtil.isNullString(AUTH_CODE)){
			this.messageBox("请输入授权码");
		}else{
			String sql = "SELECT STATUS FROM REG_AUTH_CODE WHERE " +
		" (STATUS = '0' OR STATUS = '1') AND AUTH_CODE = '"+authCodeDX+"'";
//			String sql = "SELECT * FROM REG_AUTH_CODE WHERE AUTH_CODE = '"+AUTH_CODE+"'";//wuxinyue源程序
			TParm authNo = new TParm(TJDODBTool.getInstance().select(sql));
			if(authNo.getValue("STATUS",0).equals("0")){
				String sql1 = "UPDATE REG_AUTH_CODE SET "+
							  " STATUS = '1', MR_NO = '" + mrNo + "'," + 
							  " OPT_USER='" + OPT_USER + "'," + 
							  " OPT_DATE=to_date('"+ OPT_DATE + "','yyyy/mm/dd hh24:mi:ss')," +
							  " OPT_TERM='" + OPT_TERM + "'" +
//							  " WHERE (STATUS = '0' OR STATUS = '1')" + 
							  " WHERE STATUS = '0'" + 
							  " AND AUTH_CODE='"+authCodeDX+"'";
				System.out.println("sql:::::::::::" + sql1);
//				String sql1 = "UPDATE REG_AUTH_CODE SET STATUS='1' WHERE AUTH_CODE='"+AUTH_CODE+"'";wuxinyue源程序
				TParm a = new TParm(TJDODBTool.getInstance().update(sql1));
//				TParm a = (TParm) TJDODBTool.getInstance().update(sql1);//wuxinyue源程序
				if(a.getErrCode()<0){
					this.messageBox("授权码更新失败");
				}else{
					KG.setData("kg", 0, "Y");
					KG.setData("AUTH_CODE", 0, authCodeDX);
					this.closeWindow();
				}
			}else if(authNo.getValue("STATUS",0).equals("1")){
				this.messageBox("授权码已使用");
			}else{
				this.messageBox("授权码不正确");
			}
//			武新月源程序
//			if(authNo.getCount()>0){
//				if(authNo.getValue("STATUS").equals("0")){
//					String sql1 = "UPDATE REG_AUTH_CODE SET STATUS='1' WHERE AUTH_CODE='"+AUTH_CODE+"'";
//					TParm a = (TParm) TJDODBTool.getInstance().update(sql1);
//					if(a.getErrCode()<0){
//						this.messageBox("授权码更新失败");
//					}
//						this.closeWindow();
//				}else{
//					this.messageBox("授权码已使用");
//				}
//			}else{
//				this.messageBox("授权码不正确");
//			}
		}
	}
	
	
		/**
	    * 窗口关闭事件
	    * @return boolean
	    */
	   public boolean onClosing(){
	       this.setReturnValue(KG);
	       return true;
	   }
	
	
	
	
	
	
	
	
	
}
