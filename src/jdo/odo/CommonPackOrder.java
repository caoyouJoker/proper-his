package jdo.odo;

import java.sql.Timestamp;
import java.util.Vector;

import jdo.pha.PhaBaseTool;
import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>Title: 门诊医生工作站常用组套处置</p>
 * 
 * <p>Description:门诊医生工作站常用组套处置</p>
 * 
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 * 
 * <p>Company:Javahis</p>
 * 
 * @author ehui 20090429
 * @version 1.0
 */
public class CommonPackOrder extends StoreBase {
	
	/**
	 * sys_fee的数据
	 */
	TDataStore sysFee = TIOM_Database.getLocalTable("SYS_FEE");

	/**
	 * 科室、医师分类
	 */
	private String deptOrDr;
	/**
	 * 科室或医师代码
	 */
	private String deptOrDrCode;
	/**
	 * 模板代码
	 */
	private String packCode;
	
	/**
	 * 得到科室、医师分类
	 * @return deptOrDr String
	 */
	public String getDeptOrDr() {
		return deptOrDr;
	}
	/**
	 * 设置科室、医师分类
	 * @parm deptOrDr String
	 */
	public void setDeptOrDr(String deptOrDr) {
		this.deptOrDr=deptOrDr;
	}
	/**
	 * 得到科室或医师代码
	 * @return deptOrDrCode String
	 */
	public String getDeptOrDrCode() {
		return deptOrDrCode;
	}
	/**
	 * 设置科室或医师代码
	 * @parm deptOrDrCode String
	 */
	public void setDeptOrDrCode(String deptOrDrCode) {
		this.deptOrDrCode=deptOrDrCode;
	}
	/**
	 * 得到模板代码
	 * @return packCode String
	 */
	public String getPackCode() {
		return packCode;
	}
	/**
	 * 设置模板代码
	 * @parm packCode String
	 */
	public void setPackCode(String packCode) {
		this.packCode=packCode;
	}
	
	/**
	 * Constructor
	 * @param deptOrDr
	 * @param deptOrDrCode
	 * @param packCode
	 */
	public CommonPackOrder(String deptOrDr,String deptOrDrCode,String packCode){
		this.setDeptOrDr(deptOrDr);
		this.setDeptOrDrCode(deptOrDrCode);
		this.setPackCode(packCode);
	
	}
    /**
     * 得到SQL
     * @return String
     */
    protected String getQuerySQL(){
    	TParm parm=new TParm();
    	parm.setData("DEPT_OR_DR",this.getDeptOrDr());
    	parm.setData("DEPTORDR_CODE",this.getDeptOrDrCode());
    	parm.setData("PACK_CODE",this.getPackCode());
    	//System.out.println("order init sql="+OpdComPackQuoteTool.INIT_ORDER_SQL);
        return OpdComPackQuoteTool.getInstance().initOrderSQL(OpdComPackQuoteTool.INIT_ORDER_SQL,parm);
    }
    /**
     * 插入数据
     * @param row int
     * @return int
     */
    public int insertRow(int row){
    	//System.out.println("insert row===============");
        int newRow = super.insertRow(row);
        if(newRow==-1)
            return -1;
        //设置门急住别
        setItem(newRow,"DEPT_OR_DR",getDeptOrDr());
        setItem(newRow,"DEPTORDR_CODE",getDeptOrDrCode());
        setItem(newRow,"PACK_CODE",getPackCode());
        setItem(newRow,"SEQ_NO",this.getMaxSeq()+1);
        setItem(newRow,"PRESRT_NO","0");
//        this.showDebug();
        setActive(newRow, false);
//        setItem(newRow,"ORDER_DATE",super.getDBTime());
        return newRow;
    }
    /**
     * 初始化西成药、处置、检验检查
     * @param row
     * @param sysFee
     * @return
     */
    public boolean initOrder(int row,TParm sysFee){
    	 //取得pha_base数据
    	 String orderCode=sysFee.getValue("ORDER_CODE");
    	 TParm phaBaseParm=PhaBaseTool.getInstance().selectByOrder(orderCode).getRow(0);
//    	 this.setItem(row, "PRESRT_NO", "");
    	 String orderCat1Code=sysFee.getValue("ORDER_CAT1_CODE");
    	 String cat1Type=sysFee.getValue("CAT1_TYPE");
    	 if("PHA_W".equalsIgnoreCase(orderCat1Code)||"PHA_C".equalsIgnoreCase(orderCat1Code)){
    		 if(StringUtil.isNullString(phaBaseParm.getValue("CTRLDRUGCLASS_CODE"))){
    			 this.setItem(row, "RX_TYPE", "1");	 
    		 }else{
    			 this.setItem(row, "RX_TYPE", "2");
    		 }
    		 
    	 }else if("TRT".equalsIgnoreCase(cat1Type)||"PLN".equalsIgnoreCase(cat1Type)||"OTH".equalsIgnoreCase(cat1Type)){
    		 this.setItem(row, "RX_TYPE", "4");
    	 }
    	 if(this.getItemInt(row, "LINK_NO")<=0){
    		 this.setItem(row, "LINK_NO", "");
    	 }
    	 this.setItem(row, "ORDER_CODE", sysFee.getValue("ORDER_CODE"));
    	 this.setItem(row, "TRADE_ENG_DESC", sysFee.getValue("TRADE_ENG_DESC"));
    	 this.setItem(row, "MEDI_QTY", phaBaseParm.getDouble("MEDI_QTY"));
    	 this.setItem(row, "MEDI_UNIT", phaBaseParm.getValue("MEDI_UNIT"));
    	 this.setItem(row, "FREQ_CODE", phaBaseParm.getValue("FREQ_CODE"));
    	 this.setItem(row, "ROUTE_CODE", phaBaseParm.getValue("ROUTE_CODE"));
    	 this.setItem(row, "TAKE_DAYS", phaBaseParm.getInt("TAKE_DAYS"));
    	 this.setItem(row, "GIVEBOX_FLG", phaBaseParm.getValue("GIVEBOX_FLG"));
    	 this.setItem(row, "DESCRIPTION", phaBaseParm.getValue("DESCRIPTION"));
    	 this.setItem(row, "EXEC_DEPT_CODE", phaBaseParm.getValue("EXEC_DEPT_CODE"));
    	 this.setItem(row, "SETMAIN_FLG", sysFee.getValue("SETMAIN_FLG"));
    	 this.setItem(row, "ORDER_DESC", sysFee.getValue("ORDER_DESC"));
    	 this.setItem(row, "SPECIFICATION", sysFee.getValue("SPECIFICATION"));
    	return true;
    }
    /**
     * 返回最大的SEQ_NO
     * @return result int 
     */
    public int getMaxSeq(){
    	int result=0;
    	int temp=0;
    	int count=this.rowCount();
    	for(int i=0;i<count;i++){
    		temp=this.getItemInt(i, "SEQ_NO");
    		if(temp>=result){
    			result=temp;
    			continue;
    		}
    		
    	}
    	return result;
    }
    /**
     * 设置操作用户
     * @param optUser String 操作用户
     * @param optDate Timestamp 操作时间
     * @param optTerm String 操作IP
     * @return boolean true 成功 false 失败
     */
    public boolean setOperator(String optUser,Timestamp optDate,String optTerm)
    {
        String storeName = isFilter()?FILTER:PRIMARY;
        int rows[] = getNewRows(storeName);
        return super.setOperator(optUser,optDate,optTerm);
    }
    /**
	 * 判断给入的ORDER_CODE是否已经开过
	 * @param orderCode String 给如的ORDER_CODE
	 * @return boolean true:开过,false:没开过
	 */
	public boolean isSameOrder(String orderCode){
		TParm parm=this.getBuffer(this.isFilter()?this.FILTER:this.PRIMARY);
		Vector code=(Vector)parm.getData("ORDER_CODE");
		return code.indexOf(orderCode)>0;
	}

	/**
	 * 得到其他列数据
	 * @param parm TParm
	 * @param row int
	 * @param column String
	 * @return Object
	 */
	public Object getOtherColumnValue(TParm parm, int row, String column) {
		if("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(column)){
			TParm sysFeeParm=sysFee.getBuffer(sysFee.isFilter()?sysFee.FILTER:sysFee.PRIMARY);
			Vector orderCode=(Vector)sysFeeParm.getData("ORDER_CODE");
			String code=this.getItemString(row, "ORDER_CODE");
			int rowNow=orderCode.indexOf(code);
			if(rowNow<0)
				return "";
			Operator d;
			if("en".equalsIgnoreCase(Operator.getLanguage())){
				return sysFeeParm.getValue("TRADE_ENG_DESC",rowNow)+"  "+sysFeeParm.getValue("SPECIFICATION",rowNow);	
			}else{
				return sysFeeParm.getValue("ORDER_DESC",rowNow)+"  "+sysFeeParm.getValue("SPECIFICATION",rowNow);
			}
			
		}
		return "";
	}
	/**
	 * 取最大处方签内的连接号
	 * @param order
	 * @return
	 */
	public int getMaxLinkNo(){
		int linkNo=0;
		for(int i=0;i<this.rowCount();i++){
			if(StringUtil.isNullString(this.getItemString(i, "ORDER_CODE")))
				continue;
			int temp=this.getItemInt(i, "LINK_NO");
			if(temp>linkNo)
				linkNo=temp;
		}
		return linkNo;
	}
	/**
	 * 列的值改变
	 */
	public boolean setItem(int row, String column, Object value) {
		super.setItem(row, column, value);
		//LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;EXEC_DEPT_CODE;DESCRIPTION
		if (!(column.equalsIgnoreCase("MEDI_QTY")
				|| column.equalsIgnoreCase("TAKE_DAYS")
				|| column.equalsIgnoreCase("FREQ_CODE")
				|| "ROUTE_CODE".equalsIgnoreCase(column)
				|| "LINKMAIN_FLG".equalsIgnoreCase(column)
				|| "LINK_NO".equalsIgnoreCase(column)
				|| "EXEC_DEPT_CODE".equalsIgnoreCase(column) || "DESCRIPTION"
				.equalsIgnoreCase(column))) {
			return true;
		}
		
		if(TypeTool.getBoolean(this.getItemData(row, "LINKMAIN_FLG"))){
			int linkNo=this.getItemInt(row, "LINK_NO");
			boolean isTakeDay="TAKE_DAYS".equalsIgnoreCase(column);
			for(int i=0;i<this.rowCount();i++){
				int temp=TypeTool.getInt(this.getItemString(i, "LINK_NO"));
				boolean linkM=TypeTool.getBoolean(this.getItemData(i, "LINKMAIN_FLG"));
				if(linkNo!=temp||linkM){
					continue;
				}
				if(isTakeDay){
					this.setItem(i, "TAKE_DAYS", this.getItemData(row, "TAKE_DAYS"));
				}else{
					this.setItem(i, "FREQ_CODE", this.getItemData(row, "FREQ_CODE"));
				}
				if("EXEC_DEPT_CODE".equalsIgnoreCase(column)){
					this.setItem(i, "EXEC_DEPT_CODE", value);
				}
				this.addOtherShowRowList(i);
		}
			
		}
		return true;
	}
    /**
     * 用门诊病历初始化
     * @param odo ODO
     * @return boolean true:初始化成功,false:初始化失败
     */
    public boolean initOdo(ODO odo){
    	if(!this.onQuery())
    		return false;
    	if(odo==null||odo.getDiagrec()==null||odo.getDiagrec().rowCount()<1)
    		return false;
    	OpdOrder order=odo.getOpdOrder();
    	order.setFilter("RX_TYPE !='3'");
    	order.filter();
    	TParm orderParm=order.getBuffer(order.PRIMARY);
    	int count=orderParm.getCount();
    	for(int i=0;i<count;i++){
    		int row=this.insertRow();
    		if(row<0)
    			return false;
    		boolean isMainSet=TypeTool.getBoolean(orderParm.getBoolean("SETMAIN_FLG",i));
    		String groupNo=TypeTool.getString(orderParm.getValue("ORDERSET_CODE",i));
    		if (!isMainSet && !StringUtil.isNullString(groupNo))
				continue;
    		this.setItem(row, "SEQ_NO", this.getMaxSeq()+1);
    		this.setItem(row, "RX_TYPE", orderParm.getValue("RX_TYPE",i));
    		this.setItem(row, "LINKMAIN_FLG", orderParm.getValue("LINKMAIN_FLG",i));
    		this.setItem(row, "LINK_NO", orderParm.getValue("LINK_NO",i));
    		this.setItem(row, "ORDER_CODE", orderParm.getValue("ORDER_CODE",i));
    		this.setItem(row, "TRADE_ENG_DESC", orderParm.getValue("TRADE_ENG_DESC",i));
    		this.setItem(row, "MEDI_QTY", orderParm.getValue("MEDI_QTY",i));
    		this.setItem(row, "MEDI_UNIT", orderParm.getValue("MEDI_UNIT",i));
    		this.setItem(row, "FREQ_CODE", orderParm.getValue("FREQ_CODE",i));
    		this.setItem(row, "ROUTE_CODE", orderParm.getValue("ROUTE_CODE",i));
    		this.setItem(row, "TAKE_DAYS", orderParm.getValue("TAKE_DAYS",i));
    		this.setItem(row, "GIVEBOX_FLG", orderParm.getValue("GIVEBOX_FLG",i));
    		this.setItem(row, "DESCRIPTION", orderParm.getValue("DR_NOTE",i));
    		this.setItem(row, "EXEC_DEPT_CODE", orderParm.getValue("EXEC_DEPT_CODE",i));
    		this.setItem(row, "SETMAIN_FLG", orderParm.getValue("SETMAIN_FLG",i));
    		this.setItem(row, "DCTAGENT_CODE", orderParm.getValue("DCTAGENT_CODE",i));
    		this.setItem(row, "DCTEXCEP_CODE", orderParm.getValue("DCTEXCEP_CODE",i));
    		this.setItem(row, "DCT_TAKE_QTY", orderParm.getValue("DCT_TAKE_QTY",i));
    		this.setItem(row, "PACKAGE_TOT", orderParm.getValue("PACKAGE_TOT",i));
    		this.setItem(row, "ORDER_DESC", orderParm.getValue("ORDER_DESC",i));
    		this.setItem(row, "SPECIFICATION", orderParm.getValue("SPECIFICATION",i));
    	}
    	order.insertRow();
    	return true;
    }
    /**
     * 根据模板号删除模板
     * @param packCode
     * @return
     */
    public boolean deletePack(String packCode){
    	if(StringUtil.isNullString(packCode)){
    		return false;
    	}
    	this.setFilter("PACK_CODE='" +packCode+
    			"'");
    	this.filter();
    	for(int i=this.rowCount()-1;i>-1;i--){
    		this.deleteRow(i);
    	}
    	return true;
    }
}
