package com.javahis.ui.emr;


import com.dongyang.control.TControl;
import com.dongyang.tui.text.EFixed;
import com.dongyang.ui.TWord;
import com.dongyang.wcomponent.expression.ExpressionUtil;
import com.dongyang.wcomponent.util.TiString;

/**
 *
 * @author whaosoft
 *
 */
public class EMRExpressionDialogControl extends TControl{

	/**  */
	private EFixed ef = null;

	/**  */
	private TWord word = null;


    /**
     * 初始化
     */
    public void onInit() {

    	word = (TWord) ( (Object[]) getParameter() )[0];
		ef = (EFixed) ( (Object[]) getParameter() )[1];

		this.setValue("NAME", ef.getName() );
		this.setValue("VALUE", ef.getExpressionDesc() );
		this.setValue("CHK_SAVEDB", ef.isSaveDB() ? "Y" : "N");

	}

    /**
	 * 确定
	 */
    public void onSave() {

    	if( !this.doCheck() )
    	   return;

		//
		this.closeWindow();
	}

    /**
     *
     * @return
     */
    private boolean doCheck(){

    	String eName = (String) this.getValue("NAME");
    	String eDesc = (String)this.getValue("VALUE");
    	
        //
    	if( TiString.isEmpty( eName ) ){

    		this.messageBox("表达式必须定义一个名称!");
    		return false;
    	}

        //
    	if( TiString.isEmpty( eDesc ) ){

    		this.messageBox("表达式内容不可以为空!");
    		return false;
    	}

    	//
    	if( ExpressionUtil.isExistExpression(word, ef,eName) ){

    		this.messageBox("此表达式名称已被占用!");
    		return false;
    	}
    	//
    	ef.setName(eName);
    	ef.setExpressionDesc(eDesc);
    	//
    	if (getValue("CHK_SAVEDB").equals("Y")) {
    		//
    		ef.setSaveDB(true);
        }
        else {
        	ef.setSaveDB(false);
        }
    	//
    	return true;
    }

    /**
     *
     */
    public void onHelp(){

    	StringBuilder sb = new StringBuilder();

    	sb.append("1.表达式内变量名必须加方括号'[ ]',例如: [变量1]+[变量2] \n");
    	sb.append("2.表达式内运算符号必须为英文符号 \n");
    	sb.append("3.表达式语法错误返回值'NaN' \n");
    	sb.append("4.支持普通四则运算,例如: ((15+69)-90)*3/2 \n");
    	sb.append("5.支持平均值函数,例如: avg(1,2,3,4,5,6) \n");
    	sb.append("6.支持汇总函数,例如: sum(1,2,3,4,5,6) \n");
    	sb.append("7.支持最大最小值开方开根等函数 ");


    	this.messageBox( "学习语法", sb.toString());
    }

}
