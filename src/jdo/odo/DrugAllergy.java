package jdo.odo;

import java.sql.Timestamp;
import java.util.Vector;

import jdo.sys.DictionaryTool;
import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: ����ʷ�洢����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.2.11
 * @version 1.0
 */
public class DrugAllergy extends StoreBase{
	private TDataStore sysFee= TIOM_Database.getLocalTable("SYS_FEE");
    /**
     * ������
     */
    private String mrNo;
    /**
     * �ż�ס��
     */
    private String admType;
    /**
     * ҽʦ
     */
    private String drCode;
    /**
     * ����
     */
    private String deptCode;
    /**
     * ���ò�����
     * @return String
     */
    public String getMrNo()
    {
        return this.mrNo;
    }
    /**
     * �õ�������
     * @param mrNo String
     */
    public void setMrNo(String mrNo)
    {
        this.mrNo = mrNo;
    }
    /**
     * �����ż�ס��
     * @param admType String
     */
    public void setAdmType(String admType)
    {
        this.admType = admType;
    }
    /**
     * �õ��ż�ס��
     * @return String
     */
    public String getAdmType()
    {
        return admType;
    }
    /**
     * ����ҽʦ
     * @param drCode String
     */
    public void setDrCode(String drCode)
    {
        this.drCode = drCode;
    }
    /**
     * �õ�ҽʦ
     * @return String
     */
    public String getDrCode()
    {
        return drCode;
    }
    /**
     * ���ò���
     * @param deptCode String
     */
    public void setDeptCode(String deptCode)
    {
        this.deptCode = deptCode;
    }
    /**
     * �õ�����
     * @return String
     */
    public String getDeptCode()
    {
        return deptCode;
    }
    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT * FROM OPD_DRUGALLERGY WHERE MR_NO = '" + getMrNo() + "'";
    }
    /**
     * ��������
     * @param row int
     * @return int
     */
    public int insertRow(int row){
        int newRow = super.insertRow(row);
        if(newRow==-1)
            return -1;
        setItem(newRow,"MR_NO",getMrNo());
        //�����ż�ס��
        setItem(newRow,"ADM_TYPE",getAdmType());
        setItem(newRow,"DR_CODE",getDrCode());
        setItem(newRow,"DEPT_CODE",getDeptCode());
        setItem(newRow,"ADM_DATE",StringTool.getString(super.getDBTime(),"yyyyMMddHHmmss"));
//        System.out.println("admDate~~~~~~~~~~~~~~~~~:"+this.getItemString(newRow, "ADM_DATE"));
        return newRow;
    }
    /**
     * ���ò����û�
     * @param optUser String �����û�
     * @param optDate Timestamp ����ʱ��
     * @param optTerm String ����IP
     * @return boolean true �ɹ� false ʧ��
     */
    public boolean setOperator(String optUser,Timestamp optDate,String optTerm)
    {
//        String storeName = isFilter()?FILTER:PRIMARY;
//        int rows[] = getNewRows(storeName);
//        for(int i = 0;i < rows.length;i++)
//            setItem(rows[i],"ADM_DATE",StringTool.getString(optDate,"yyyyMMddHHmmss"),storeName);
        return super.setOperator(optUser,optDate,optTerm);
    }
    /**
	 * �õ�����������
	 * @param parm TParm
	 * @param row int
	 * @param column String
	 * @return Object
	 */
	public Object getOtherColumnValue(TParm parm, int row, String column) {
		if ("ADM_DATE_FORMAT".equalsIgnoreCase(column)) {
			Timestamp time = StringTool.getTimestamp(parm.getValue("ADM_DATE",row),
					"yyyyMMddHHmmss");
//			//System.out.println("time="+time);
//			//System.out.println("strtime="+StringTool.getString(time, "yyyy/MM/dd HH:mm:ss"));
			return StringTool.getString(time, "yyyy/MM/dd HH:mm:ss");
		}
		boolean isEng="en".equalsIgnoreCase(Operator.getLanguage());
		if ("DRUGORINGRD_DESC".equalsIgnoreCase(column)) {
			if("B".equalsIgnoreCase(parm.getValue("DRUG_TYPE",row))){
				TParm sysFeeParm=sysFee.getBuffer(sysFee.isFilter()?sysFee.FILTER:sysFee.PRIMARY);
				Vector code=(Vector)sysFeeParm.getData("ORDER_CODE");
				int rowNow=code.indexOf(parm.getValue("DRUGORINGRD_CODE",row));
				if(rowNow==-1)
					return "";
				String desc="";
				if(isEng){
					desc=sysFeeParm.getValue("TRADE_ENG_DESC",rowNow);
				}else{
					desc=sysFeeParm.getValue("ORDER_DESC",rowNow);
				}
				return desc;
			//add by huangtt 20150505 start	
			}else if("D".equalsIgnoreCase(parm.getValue("DRUG_TYPE",row))){
				String desc="";
				String sql = "SELECT B.CATEGORY_CODE , B.CATEGORY_CHN_DESC,B.CATEGORY_ENG_DESC" +
						" FROM SYS_RULE A, SYS_CATEGORY B" +
						" WHERE A.RULE_TYPE = 'PHA_RULE'" +
						" AND A.CLASSIFY1 > 0" +
						" AND B.RULE_TYPE = A.RULE_TYPE" +
						" AND LENGTH (B.CATEGORY_CODE) = A.CLASSIFY1" +
						" AND B.CATEGORY_CODE='"+parm.getValue("DRUGORINGRD_CODE",row)+"'";
				TParm phaClass = new TParm(TJDODBTool.getInstance().select(sql));
				if(isEng){
					desc=phaClass.getValue("CATEGORY_ENG_DESC", 0);
				}else{
					desc=phaClass.getValue("CATEGORY_CHN_DESC", 0);
				}
				return desc;
				//add by huangtt 20151111 start
			}else if("N".equalsIgnoreCase(parm.getValue("DRUG_TYPE",row))){
				String desc="";
				String sql ="SELECT CHN_DESC,ENG_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='NONE_ALLERGYTYPE' " +
						"AND ID='"+parm.getValue("DRUGORINGRD_CODE",row)+"'";
				TParm noParm = new TParm(TJDODBTool.getInstance().select(sql));
				if(isEng){
					desc=noParm.getValue("ENG_DESC", 0);
				}else{
					desc=noParm.getValue("CHN_DESC", 0);
				}
				return desc;
				//add by huangtt 20151111 end 
			} else if("E".equalsIgnoreCase(parm.getValue("DRUG_TYPE",row))){ 
				String desc="";
				String sql = "SELECT B.CATEGORY_CODE, B.CATEGORY_CHN_DESC,B.CATEGORY_ENG_DESC" +
						" FROM SYS_RULE A, SYS_CATEGORY B" +
						" WHERE A.RULE_TYPE = 'PHA_RULE'" +
						" AND A.CLASSIFY2 > 0" +
						" AND B.RULE_TYPE = A.RULE_TYPE" +
						" AND LENGTH (B.CATEGORY_CODE) = A.CLASSIFY1 + A.CLASSIFY2" +
						" AND B.CATEGORY_CODE='"+parm.getValue("DRUGORINGRD_CODE",row)+"'";
				TParm phaClass = new TParm(TJDODBTool.getInstance().select(sql));
				if(isEng){
					desc=phaClass.getValue("CATEGORY_ENG_DESC", 0);
				}else{
					desc=phaClass.getValue("CATEGORY_CHN_DESC", 0);
				}
				return desc;
				//add by huangtt 20150505 end	
			}else if("A".equalsIgnoreCase(parm.getValue("DRUG_TYPE",row))){
				String desc="";
				if(isEng){
					desc=DictionaryTool.getInstance().getEnName("PHA_INGREDIENT", parm.getValue("DRUGORINGRD_CODE",row));
				}else{
					desc=DictionaryTool.getInstance().getName("PHA_INGREDIENT", parm.getValue("DRUGORINGRD_CODE",row));
				}
				return desc;
			}else {
				String desc="";
				if(isEng){
					desc=DictionaryTool.getInstance().getEnName("SYS_ALLERGYTYPE", parm.getValue("DRUGORINGRD_CODE",row));
				}else{
					desc=DictionaryTool.getInstance().getName("SYS_ALLERGYTYPE", parm.getValue("DRUGORINGRD_CODE",row));
				}
				return desc;
			}
		}
		return "";
	}
}
