package jdo.ekt;

import com.dongyang.config.TConfig;
import com.dongyang.util.TypeTool;

public class EKTJudge {
	
	private TConfig prop;
	
	public EKTJudge() {
	}	

	/**
	 * ��ʱ����ִ��
	 */
	public boolean Facility()  {
		  prop=getProp();
			// �������ļ�ȡ�ö�ʱ���ʱ��
			boolean type = TypeTool.getBoolean(prop.getString("EKTFacilitySwitch"));
			return type;
	}
	
	/**
	 * ��ȡ TConfig.x
	 *
	 * @return TConfig
	 */
	private TConfig getProp() {
		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		return config;
	}
}
