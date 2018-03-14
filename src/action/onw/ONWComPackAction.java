package action.onw;

import java.util.Map;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.onw.ONWComPackTool;

/**
 * <p>��������֮��ͷҽ���ײ�</p>
 * 
 * @author wangqing 20170901
 *
 */
public class ONWComPackAction extends TAction {

	/**
	 * ����
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm){
		if(parm == null){
			System.out.println("//parm is null//");
			return null;
		}
		TParm parmPackValue = null;
		Map parmPackValueMap = (Map)parm.getData("#PACK");
		if(parmPackValueMap != null){
			parmPackValue = new TParm(parmPackValueMap);
		}
		TParm parmOrderValue = null;
		Map parmOrderValueMap = (Map)parm.getData("#ORDER");
		if(parmOrderValueMap != null){
			parmOrderValue = new TParm(parmOrderValueMap);
		}
		TParm deletePackParm = null;
		Map deletePackParmMap = (Map)parm.getData("#PACK_DELETE");
		if(deletePackParmMap != null){
			deletePackParm = new TParm(deletePackParmMap);
		}
		TParm deleteOrderParm = null;
		Map deleteOrderParmMap = (Map)parm.getData("#ORDER_DELETE");
		if(deleteOrderParmMap != null){
			deleteOrderParm = new TParm(deleteOrderParmMap);
		}

		TConnection connection = getConnection();
		TParm result = new TParm();
		if(parmPackValue != null){
			for(int i=0; i<parmPackValue.getCount(); i++){
				// �����ײ�
				if(parmPackValue.getInt("#STATUS", i)==0){
					result = ONWComPackTool.getInstance().insertOnwPackMain(parmPackValue.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
				}
				// �޸��ײ�
				if(parmPackValue.getInt("#STATUS", i)==2){
					result = ONWComPackTool.getInstance().updateOnwPackMain(parmPackValue.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
				}
			}
		}
		if(deletePackParm != null){// ɾ���ײͣ�ͬʱɾ���ײ��ڵ�ҽ��
			for(int i=0; i<deletePackParm.getCount(); i++){
				// ɾ���ײ�
				result = ONWComPackTool.getInstance().deleteOnwPackMain(deletePackParm.getRow(i), connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					connection.close();
					return result;
				}
				// ɾ��ҽ��
				TParm temp = new TParm();
				temp.setData("PACK_CODE", deletePackParm.getRow(i).getValue("PACK_CODE"));
				result = ONWComPackTool.getInstance().deleteOnwPackOrder(temp, connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					connection.close();
					return result;
				}
			}		
		}
		if(parmOrderValue != null){
			for(int i=0; i<parmOrderValue.getCount(); i++){
				// ����ҽ��
				if(parmOrderValue.getInt("#STATUS", i)==0){
					result = ONWComPackTool.getInstance().insertOnwPackOrder(parmOrderValue.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
				}
				// �޸�ҽ��
				if(parmOrderValue.getInt("#STATUS", i)==2){
					result = ONWComPackTool.getInstance().updateOnwPackOrder(parmOrderValue.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
				}
			}
		}
		if(deleteOrderParm != null){// ɾ��ҽ��
			for(int i=0; i<deleteOrderParm.getCount(); i++){
				result = ONWComPackTool.getInstance().deleteOnwPackOrder(deleteOrderParm.getRow(i), connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					connection.close();
					return result;
				}
			}		
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ��ѯ��ͷҽ���ײ�
	 * @param parm
	 * @return
	 */
	public TParm selectOnwPackMain(TParm parm){
		TConnection connection = getConnection();
		TParm result = ONWComPackTool.getInstance().selectOnwPackMain(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ��ѯ�ײ�ҽ��ϸ��
	 * @param parm
	 * @return
	 */
	public TParm selectOnwPackOrder(TParm parm){
		TConnection connection = getConnection();
		TParm result = ONWComPackTool.getInstance().selectOnwPackOrder(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ONW_ORDER����ҽ��
	 * @param parm
	 * @return
	 */
	public TParm insertOnwOrder(TParm parm){
		TConnection connection = getConnection();
		TParm result = ONWComPackTool.getInstance().insertOnwOrder(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ����ҽ������ʿǩ��
	 * @param parm
	 * @return
	 */
	public TParm onSaveOrder1(TParm parm){
		if(parm == null){
			System.out.println("//parm is null//");
			return null;
		}
		TParm orderP = null;
		Map orderPMap = (Map)parm.getData("#ORDER");
		if(orderPMap != null){
			orderP = new TParm(orderPMap);
		}
		TParm deleteOrderP = null;
		Map deleteOrderPMap = (Map)parm.getData("#ORDER_DELETE");
		if(deleteOrderPMap != null){
			deleteOrderP = new TParm(deleteOrderPMap);
		}
		TConnection connection = getConnection();
		TParm result = new TParm();
		// ɾ��ҽ��
		if(deleteOrderP != null){
			for(int i=0; i<deleteOrderP.getCount(); i++){
				result = ONWComPackTool.getInstance().deleteOnwOrder(deleteOrderP.getRow(i), connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					connection.close();
					return result;
				}
			}
		}
		if(orderP != null){
			for(int i=0; i<orderP.getCount(); i++){
				// ������ͷҽ��
				if(orderP.getInt("#STATUS", i)==0){
					result = ONWComPackTool.getInstance().insertOnwOrder(orderP.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
				}
				// �޸Ŀ�ͷҽ��
				if(orderP.getInt("#STATUS", i)==2){
					result = ONWComPackTool.getInstance().updateOnwOrder1(orderP.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
				}
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ����ҽ����ȡ����ʿǩ��
	 * @param parm
	 * @return
	 */
	public TParm onSaveOrder2(TParm parm){
		if(parm == null){
			System.out.println("//parm is null//");
			return null;
		}
		TParm orderP = null;
		Map orderPMap = (Map)parm.getData("#ORDER");
		if(orderPMap != null){
			orderP = new TParm(orderPMap);
		}
		TParm deleteOrderP = null;
		Map deleteOrderPMap = (Map)parm.getData("#ORDER_DELETE");
		if(deleteOrderPMap != null){
			deleteOrderP = new TParm(deleteOrderPMap);
		}
		TConnection connection = getConnection();
		TParm result = new TParm();
		// ɾ��ҽ��
		if(deleteOrderP != null){
			for(int i=0; i<deleteOrderP.getCount(); i++){
				result = ONWComPackTool.getInstance().deleteOnwOrder(deleteOrderP.getRow(i), connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					connection.close();
					return result;
				}
			}
		}
		if(orderP != null){
			for(int i=0; i<orderP.getCount(); i++){
				// ������ͷҽ��
				if(orderP.getInt("#STATUS", i)==0){
					result = ONWComPackTool.getInstance().insertOnwOrder(orderP.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
				}
				// �޸Ŀ�ͷҽ����ȡ����ʿǩ��
				if(orderP.getInt("#STATUS", i)==2){
					// �޸Ŀ�ͷҽ��
					result = ONWComPackTool.getInstance().updateOnwOrder1(orderP.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
					// ȡ����ʿǩ��
					result = ONWComPackTool.getInstance().updateOnwOrder2(orderP.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
				}
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ҽ��ǩ��
	 * @param parm
	 * @return
	 */
	public TParm onSaveOrder3(TParm parm){
		if(parm == null){
			System.out.println("//parm is null//");
			return null;
		}
		TParm orderP = null;
		Map orderPMap = (Map)parm.getData("#ORDER");
		if(orderPMap != null){
			orderP = new TParm(orderPMap);
		}
		TParm deleteOrderP = null;
		Map deleteOrderPMap = (Map)parm.getData("#ORDER_DELETE");
		if(deleteOrderPMap != null){
			deleteOrderP = new TParm(deleteOrderPMap);
		}
		TConnection connection = getConnection();
		TParm result = new TParm();
		// ɾ��ҽ��
		if(deleteOrderP != null){
			for(int i=0; i<deleteOrderP.getCount(); i++){
				result = ONWComPackTool.getInstance().deleteOnwOrder(deleteOrderP.getRow(i), connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					connection.close();
					return result;
				}
			}
		}
		if(orderP != null){
			for(int i=0; i<orderP.getCount(); i++){
				// ������ͷҽ��
				if(orderP.getInt("#STATUS", i)==0){
					result = ONWComPackTool.getInstance().insertOnwOrder(orderP.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
				}
				// �޸Ŀ�ͷҽ����ȡ����ʿǩ��
				if(orderP.getInt("#STATUS", i)==2){
					// �޸Ŀ�ͷҽ��
					result = ONWComPackTool.getInstance().updateOnwOrder1(orderP.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
					// ҽ��ǩ��
					result = ONWComPackTool.getInstance().updateOnwOrder3(orderP.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
				}
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ȡ��ҽ��ǩ��
	 * @param parm
	 * @return
	 */
	public TParm onSaveOrder4(TParm parm){
		if(parm == null){
			System.out.println("//parm is null//");
			return null;
		}
		TParm orderP = null;
		Map orderPMap = (Map)parm.getData("#ORDER");
		if(orderPMap != null){
			orderP = new TParm(orderPMap);
		}
		TParm deleteOrderP = null;
		Map deleteOrderPMap = (Map)parm.getData("#ORDER_DELETE");
		if(deleteOrderPMap != null){
			deleteOrderP = new TParm(deleteOrderPMap);
		}
		TConnection connection = getConnection();
		TParm result = new TParm();
		// ɾ��ҽ��
		if(deleteOrderP != null){
			for(int i=0; i<deleteOrderP.getCount(); i++){
				result = ONWComPackTool.getInstance().deleteOnwOrder(deleteOrderP.getRow(i), connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					connection.close();
					return result;
				}
			}
		}
		if(orderP != null){
			for(int i=0; i<orderP.getCount(); i++){
				// ������ͷҽ��
				if(orderP.getInt("#STATUS", i)==0){
					result = ONWComPackTool.getInstance().insertOnwOrder(orderP.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
				}
				// �޸Ŀ�ͷҽ����ȡ����ʿǩ��
				if(orderP.getInt("#STATUS", i)==2){
					// �޸Ŀ�ͷҽ��
					result = ONWComPackTool.getInstance().updateOnwOrder1(orderP.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
					// ȡ��ҽ��ǩ��
					result = ONWComPackTool.getInstance().updateOnwOrder4(orderP.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.close();
						return result;
					}
				}
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ONW_ORDER��ѯҽ��
	 * @param parm
	 * @return
	 */
	public TParm selectOnwOrder(TParm parm){
		TConnection connection = getConnection();
		TParm result = ONWComPackTool.getInstance().selectOnwOrder(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ������������
	 * @param parm
	 * @return
	 */
	public TParm onSaveVSData(TParm parm){
		if(parm == null){
			System.out.println("parm is null");
			return null;
		}
		TConnection connection = getConnection();
		TParm result = new TParm();
		for(int i=0; i<parm.getCount(); i++){
			// ���²���
			if(parm.getInt("#STATUS", i)==2){
				result = ONWComPackTool.getInstance().updateAmiErdVtsRecord(parm.getRow(i), connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					connection.close();
					return result;
				}
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
	
	/**
	 * ��ʿǩ��
	 * @param parm
	 * @return
	 */
	public TParm updateAmiErdVtsRecord1(TParm parm){
		if(parm == null){
			System.out.println("parm is null");
			return null;
		}
		TConnection connection = getConnection();
		TParm result = new TParm();
		for(int i=0; i<parm.getCount(); i++){
			// ���²���
			if(parm.getInt("#STATUS", i)==2){
				result = ONWComPackTool.getInstance().updateAmiErdVtsRecord1(parm.getRow(i), connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					connection.close();
					return result;
				}
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
	
	/**
	 * ȡ����ʿǩ��
	 * @param parm
	 * @return
	 */
	public TParm updateAmiErdVtsRecord2(TParm parm){
		if(parm == null){
			System.out.println("parm is null");
			return null;
		}
		TConnection connection = getConnection();
		TParm result = new TParm();
		for(int i=0; i<parm.getCount(); i++){
			// ���²���
			if(parm.getInt("#STATUS", i)==2){
				result = ONWComPackTool.getInstance().updateAmiErdVtsRecord2(parm.getRow(i), connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					connection.close();
					return result;
				}
			}
		}
		connection.commit();
		connection.close();
		return result;
	}



}
