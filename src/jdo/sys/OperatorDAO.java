package jdo.sys;

import jdo.opd.ODO;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOObject;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.JavaHisDebug;

/**
*
* <p>Title: Operator主结构
*
* <p>Description: Operator主结构</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2008</p>
*
* <p>Company: javahis
*
* @author ehui 20081112
* @version 1.0
*/
public class OperatorDAO  extends TJDOObject  {
	/**
	 * 部门列表
	 */
	private JDODeptList deptList;
	/**
	 * 证照列表
	 */
	private JDOLicenceList licenseList;
	/**
	 * 用户列表
	 */
	private JDOOperatorList operatorList;
	/**
	 * 工作站列表
	 */
	private JDOStationList stationList;
	public JDODeptList getDeptList(){
		return this.deptList;
	}
	public void setDeptList(JDODeptList deptList){
		this.deptList=deptList;
	}
	public JDOLicenceList getLicenseList(){
		return this.licenseList;
	}
	public void setLicenseList(JDOLicenceList licenseList){
		this.licenseList=licenseList;
	}
	public JDOOperatorList getOperatorList(){
		return this.operatorList;
	}
	public void setOperatorList(JDOOperatorList operatorList){
		this.operatorList=operatorList;
	}
	public JDOStationList getStationList(){
		return this.stationList;
	}
	public void setStationList(JDOStationList stationList){
		this.stationList=stationList;
	}
	public OperatorDAO(){
		this.setDeptList(new JDODeptList());
		this.setLicenseList(new JDOLicenceList());
		this.setOperatorList(new JDOOperatorList());
		this.setStationList(new JDOStationList());
	}
	/**
	 * 初始化部门
	 * @param parm TParm
	 * @return boolean 真：成功，假：失败
	 */
	public boolean initJDODeptList(TParm parm) {
		if (parm == null)
			return false;
		return this.getDeptList().initParm(parm);
	}
	/**
	 * 初始化证照
	 * @param parm TParm
	 * @return boolean 真：成功，假：失败
	 */
	public boolean initJDOLicenceList(TParm parm) {
		if (parm == null)
			return false;
		return this.getLicenseList().initParm(parm);
	}
	/**
	 * 初始化用户
	 * @param parm TParm
	 * @return boolean 真：成功，假：失败
	 */
	public boolean initJDOOperatorList(TParm parm) {
		if (parm == null)
			return false;

		return this.getOperatorList().initParm(parm);
	}
	/**
	 * 初始化工作站
	 * @param parm TParm
	 * @return boolean 真：成功，假：失败
	 */
	public boolean initJDOStationList(TParm parm) {
		if (parm == null)
			return false;
		return this.getStationList().initParm(parm);
	}
	/**
	 * 初始化参数
	 * @param parm TParm
	 * @return boolean 真：成功，假：失败
	 */
	public boolean initParm(TParm parm) {
		if (parm == null)
			return false;
		//初始化用户
		if (!initJDOOperatorList(parm.getParm("Operator")))
			return false;
		return true;
	}
	/**
	 * 初始化参数
	 * @param parm TParm
	 * @return boolean 真：成功，假：失败
	 */
	public boolean initParmForOperator(TParm parm){
		if (parm == null)
			return false;
		//初始化部门
		if (!initJDODeptList(parm.getParm("Dept")))
			return false;
		//初始化证照
		if (!initJDOLicenceList(parm.getParm("License")))
			return false;
		//初始化工作站
		if (!initJDOStationList(parm.getParm("Station")))
			return false;
		return true;
	}
	/**
	 * 得到全部数据
	 * @return TParm
	 */
	public TParm getParm() {
		TParm result = new TParm();
		//加载主诉客诉数据
		result.setData("Dept", this.getDeptList().getParm().getData());
		//加载医嘱数据
		result.setData("License", this.getLicenseList().getParm().getData());
		//加载过敏史数据
		result.setData("Operator",this.getOperatorList().getParm().getData());
		//加载既往史数据
		result.setData("Station", this.getStationList().getParm()
				.getData());
		return result;
	}
	/**
	 * 保存
	 * @return TParm
	 */
	public TParm onSave() {
		TParm parm = getParm();
		TParm result = TIOM_AppServer.executeAction("action.sys.SYSOperatorAction",
				"onSave", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		reset();
		return result;
	}
	/**
	 * 查询
	 * @param caseNo String
	 */
	public boolean onQuery(TParm parm) {
		reset();
		TParm result = TIOM_AppServer.executeAction("action.sys.SYSOperatorAction",
				"onQuery", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		if (!initParm(result)) {
			err("数据载入失败");
			return false;
		}
		return true;
	}
	/**
	 * 为已知的用户查询周边信息
	 * @param caseNo String
	 */
	public boolean onQueryForOperator(TParm parm){
		this.setDeptList(new JDODeptList());
		this.setLicenseList(new JDOLicenceList());
		this.setStationList(new JDOStationList());
		TParm result = TIOM_AppServer.executeAction("action.sys.SYSOperatorAction",
				"onQueryForOperator", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		if (!initParmForOperator(result)) {
			err("数据载入失败");
			return false;
		}
		return true;
	}
	public void reset()
	{
		this.getDeptList().reset();
		this.getLicenseList().reset();
		this.getOperatorList().reset();
		this.getStationList().reset();
	}
	public static void main(String args[]) {
		JavaHisDebug.initClient();
		OperatorDAO dao=new OperatorDAO();
		TParm parm=new TParm();

		dao.onQuery(parm);
		parm.setData("USER_ID","admin");
		dao.onQueryForOperator(parm);
		//System.out.println(dao.getDeptList().getParm(dao.getDeptList().PRIMARY));
		//System.out.println(dao.getStationList().getParm(dao.getStationList().PRIMARY));
		//System.out.println(dao.getLicenseList().getParm(dao.getLicenseList().PRIMARY));
		JDOOperatorList op=dao.getOperatorList();
		JDODeptList list=dao.getDeptList();
		JDOStationList slist=dao.getStationList();
		//System.out.println(slist.getParm(dao.getStationList().PRIMARY));
		for(int i=0;i<slist.size();i++){
			JDOStation opl=slist.getJDOStation(i);
//			System.out.println(opl.getDeptCode()+"i"+i+"");
		}
	}

}
