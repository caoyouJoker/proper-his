package jdo.ekt;

import com.dongyang.config.TConfig;
import com.dongyang.util.TypeTool;

public class EKTJudge {
	
	private TConfig prop;
	
	public EKTJudge() {
	}	

	/**
	 * 定时任务执行
	 */
	public boolean Facility()  {
		  prop=getProp();
			// 从配置文件取得定时间隔时间
			boolean type = TypeTool.getBoolean(prop.getString("EKTFacilitySwitch"));
			return type;
	}
	
	/**
	 * 读取 TConfig.x
	 *
	 * @return TConfig
	 */
	private TConfig getProp() {
		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		return config;
	}
}
