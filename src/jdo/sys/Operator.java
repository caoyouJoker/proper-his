package jdo.sys;
  
import com.dongyang.data.TParm;
import com.dongyang.util.TSystem;

/**
 * 操作人员类
 * <p>
 * Title:
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author lzk 2008.08.22
 * @version 1.0
 */
public class Operator {
	public static TParm getData() {
		TParm parm = (TParm) TSystem.getObject("OperatorObject");
		//System.out.println("111===Operator parm==111"+parm);
		if (parm == null) {
			System.out.println("111===parm is null ===111");
			parm = new TParm();
			TSystem.setObject("OperatorObject", parm);
		}
		return parm;
	}

	/**
	 * 设置对象
	 *
	 * @param ID
	 *            String 用户编号
	 * @param region
	 *            String 登录区域
	 * @param IP
	 *            String 用户IP
	 * @param dept
	 *            String 登录科室
	 * @param station
	 *            String 登录病区
	 */
	public static void setData(String ID, String region, String IP,
			String dept, String station) {
		System.out.println("111===setData===111"+ID);
		getData().setData("ID", ID);
		getData().setData("REGION", region);
		//=====pangben 2013-4-18 添加物联网注记
		getData().setData("SPC_FLG", null!=SYSRegionTool.getInstance().selectdata(region)
				.getValue("SPC_FLG",0)&&SYSRegionTool.getInstance().selectdata(region).getValue("SPC_FLG",0).length()>0?SYSRegionTool.getInstance().selectdata(region)
						.getValue("SPC_FLG",0):"N");
		getData().setData("IP", IP);
		getData().setData("DEPT", dept);
		getData().setData("STATION", station);
		getData().setData("ROLE_ID",
				OperatorTool.getInstance().getOperatorRole(ID));
	}

	/**
	 * 得到用户ID
	 *
	 * @return String
	 */
	public static String getID() {
		return getData().getValue("ID");
	}

	/**
	 * 得到用户姓名
	 *
	 * @return String
	 */
	public static String getName() { 
		return OperatorTool.getInstance().getOperatorName(getID());
	}
        /**
         * 得到用户英文名
         * @return String
         */
        public static String getEngName() {
                return OperatorTool.getInstance().getOperatorEngName(getID());
        }

	/**
	 * 得到用户IP
	 *
	 * @return String
	 */
	public static String getIP() {
		return getData().getValue("IP");
	}

	/**
	 * 得到用户登录区域
	 *
	 * @return String
	 */
	public static String getRegion() {
		return getData().getValue("REGION");
	}
	/**
	 * 得到物联网注记
	 *
	 * @return String
	 */
	public static String getSpcFlg() {
		return getData().getValue("SPC_FLG");
	}
	/**
	 * 得到主科室
	 *
	 * @return String
	 */
	public static String getDept() {
		return getData().getValue("DEPT");
	}

	/**
	 * 得到角色
	 *
	 * @return String
	 */
	public static String getRole() {
		return getData().getValue("ROLE_ID");
	}

	/** 
	 * 得到用户职别
	 *
	 * @return String
	 */
	public static String getPosition() {
		 // 20170621 wuxy start
		 return OperatorTool.getInstance().getOperatorPosCode(getID());
		// 20170621 wuxy end
	}

	/**
	 * 得到病区
	 *
	 * @return String
	 */
	public static String getStation() {
		return getData().getValue("STATION");
	}
        /**
         * 得到登录语种
         * @return String
         */
        public static String getLanguage()
        {
            return (String)TSystem.getObject("Language");
        }
	/**
	 * 得到医院中文全称
	 *
	 * @return String
	 */
	public static String getHospitalCHNFullName() {
		return SYSNewRegionTool.getInstance().getHospitalName(getRegion())
				.getValue("REGION_CHN_DESC", 0);
	}

	/**
	 * 得到医院中文简称
	 *
	 * @return String
	 */
	public static String getHospitalCHNShortName() {
		return SYSNewRegionTool.getInstance().getHospitalName(getRegion())
				.getValue("REGION_CHN_ABN", 0);
	}

	/**
	 * 得到医院英文全称
	 *
	 * @return String
	 */
	public static String getHospitalENGFullName() {
		return SYSNewRegionTool.getInstance().getHospitalName(getRegion())
				.getValue("REGION_ENG_DESC", 0);
	}
        /**
         * 得到医院英文简称
         * @return String
         */
        public static String getHospitalENGShortName() {
		return SYSNewRegionTool.getInstance().getHospitalName(getRegion())
				.getValue("REGION_ENG_ABN", 0);
	}
        /**
         * 得到成本中心代码
         * @return String
         */
        public static String getCostCenter() {
                return OperatorTool.getInstance().getCostCenterCode(getID());
        }
        /**
         * 根据使用者ID得到使用者的所有科室
         * @param userID String
         * @return TParm
         */
        public static TParm getOperatorDept(String userID) {
		return OperatorTool.getInstance().getOperatorDept(userID);
	}

}
