package jdo.spc;

import java.sql.Timestamp;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TimestampAdapter extends XmlAdapter<String, Timestamp> {
	/**
	 * <功能详细描述>
	 * 
	 * @param time
	 * @return
	 * @throws Exception
	 * @see [类、类#方法、类#成员]
	 */
	public String marshal(Timestamp time) throws Exception {
		return DateUtil.timestamp2Str(time);
	}

	/**
	 * <功能详细描述>
	 * 
	 * @param v
	 * @throws Exception
	 * @see [类、类#方法、类#成员]
	 */
	public Timestamp unmarshal(String str) throws Exception {
		return DateUtil.str2Timestamp(str);
	}
}
