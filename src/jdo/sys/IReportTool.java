package jdo.sys;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.javahis.util.IReport;
/**
 * <p>
 * Title: �������÷���
 * </p>
 * 
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author chenxi 2013-04-19
 * @version 4.0.1
 */
public class IReportTool {
	
	/**
	 * ʵ��
	 */
	private  static IReportTool instance ;
	/**
	 * �õ�ʵ��
	 * 
	 * @return EKTIO
	 */
	public static IReportTool getInstance() {
		if (instance == null)
			instance = new IReportTool();
		return instance;
	}

	/**
	 * ��ȡ ReportConfig.x
	 * 
	 * @return reportConfig
	 */
	public static TConfig getReportConfig() {
		TConfig reportConfig = TConfig
				.getConfig("WEB-INF\\config\\system\\ReportConfig.x");
		return reportConfig;
	}
	
	
	/**
	 * �õ�����·��
	 * id  �����ļ����ñ���·����id
	 * 
	 * @return   com
	 */
	public String getReportPath(String id) {
		String com = "";
		com = getReportConfig().getString(id) ;
		if (com == null || com.trim().length() <= 0) {
			System.out.println("���ñ���·������");
		}
		return com;
	}	
	/**
	 * �õ���̬��������
	 * id �����ļ��ı�ʾ��ͨ����ȡclassname
	 * parm ����
	 * @return  result
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public TParm getReportParm(String id,TParm parm) {
		String className = this.getReportPath(id) ;
		TParm result = null ;
		Class c  ;
		try {
			  c = Class.forName(className);
			  IReport instance=(IReport)c.newInstance();
			  result = instance.getReportParm(parm);
				} catch (ClassNotFoundException e) {
				System.out.println("������Ϣ1======"+e.toString());
				} catch (InstantiationException e) {
				System.out.println("������Ϣ2======"+e.toString());
				} catch (IllegalAccessException e) {
				System.out.println("������Ϣ3======"+e.toString());
				}
			
		return result; 
	}
	public static void main(String[] args) {
		IReportTool  tool  =  new IReportTool() ;
//		tool.getReportParm("") ;
	}
}