package jdo.ins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.FileTool;
import com.javahis.util.JavaHisDebug;

import device.PassDriver;

public class INSDebugTool extends TJDOTool {

	public INSDebugTool() {
	}

	/**
	 * 实例
	 */
	public static INSDebugTool instanceObject;

	/**
	 * 得到实例
	 *
	 * @return INSTJTool
	 */
	public static INSDebugTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSDebugTool();
		return instanceObject;
	}

	/**
	 * INS数据接口
	 *
	 * @param parm
	 * @return
	 */
	public TParm INSInterface(TParm parm) {
           // System.out.println("后台入参"+parm);
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "jdo.ins.INSDebugTool.INSInterface->Err:入参为空！");
			return result;
		}
		// 目前固定
		parm.setData("HOSP_AREA", "HIS");
		// 函数名称
		String interName = parm.getValue("PIPELINE");
		// 方法名称
		String method = parm.getValue("PLOT_TYPE");
		// 参数类型
		String in_outType = "IN";
		// 参数个数
		int parcount = parm.getInt("PARM_COUNT", 0);

		if (!this.IsinsIodata(interName, method, in_outType, parcount)) {
			result.setErr(-1, "jdo.ins.INSDebugTool.INSInterface->Err:入参错误！");
			return result;
		}
		// 解析入参转换成字符串
		TParm InStrParm = this.parseIn(parm);
		if (InStrParm.getErrCode() < -1) {
			result.setErr(-1, "jdo.ins.INSDebugTool.INSInterface->Err:解析入参失败！");
			return result;
		}
		// 传送医保接口字符串
		String inStr = InStrParm.getValue("InStrParm");
		// debug标记 0:测试 1:正式
		String debug = this.getInsDebug();
		// 测试流程
		if (debug.equals("0")) {
			// 测试start
			try {
				// 测试的入参路径
				String indir = this.getInsInPath();
				File file = new File(indir);
				if (!file.isDirectory()) {

					file.mkdirs();

				}
				String infileName = parm.getValue("PIPELINE") + "("
						+ parm.getValue("PLOT_TYPE") + ").txt";
				FileTool.setString(indir + infileName, inStr, false);
			} catch (IOException e) {
				result.setErr(-1,
						"jdo.ins.INSDebugTool.INSInterface->Err:入参生成文件失败！");
				return result;
			}
			// 测试的出参路径
			String outdir = this.getInsOutPath();
			String outfileName = parm.getValue("PIPELINE") + "("
					+ parm.getValue("PLOT_TYPE") + ").txt";
                              //      System.out.println("测试的出参路径"+outdir+outfileName);
			Object object;
			try {
				object = FileTool.getString(outdir + outfileName);
			} catch (IOException e) {
				result.setErr(-1,
						"jdo.ins.INSDebugTool.INSInterface->Err:出参解析文件失败！");
				return result;
			}
			Vector vct = (Vector) object;
			// 出参字符串
			String outStr = vct.get(0).toString();
			parm.setData("OutStrParm", outStr);
			result = parseOut(parm);
                       // System.out.println("出参"+result);
			if (result.getErrCode() < 0) {
				result.setErr(-1,
						"jdo.ins.INSDebugTool.INSInterface->Err:出参解析失败！");
				return result;
			}
		} else if (debug.equals("1")) {
			// 医保接口dll
                        //      Object obj = InsManagerTool.getInstance().safe(parm);//正式接口
                     result=   InsManagerTool.getInstance().safe(parm);
		}
		return result;

	}

	/**
	 * 传入参数解析
	 *
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	private TParm parseIn(TParm parm) {
		TParm inParm = new TParm();
		// 基本数据检测
		if (parm.checkEmpty("PIPELINE,PLOT_TYPE,HOSP_AREA", parm)) {
			inParm.setErr(-1, "入参中PIPELINE,PLOT_TYPE,HOSP_AREA有为空");
			return inParm;
		}
		// 得到医保基本参数
		TParm sysParm = getTool().getSysParm(parm);
		if (sysParm.getErrCode() < 0) {
			inParm.setErr(-1, sysParm.getErrText(), sysParm.getErrName());
			return inParm;
		}
		if (sysParm.getErrCode() > 0) {
			inParm.setErr(-1, "INS_SYSPARM 表中没有找到默认的医保参数!");
			return inParm;
		}

		parm.setData("sysParm", sysParm);
		parm.setData("CITY", sysParm.getData("CITY", 0));
		// 读取医保接口函数信息
		TParm ioParm = getTool().getIOParm(parm);
		if (ioParm.getErrCode() < 0) {
			inParm.setErr(-1, ioParm.getErrText(), ioParm.getErrName());
			return inParm;
		}
		if (ioParm.getErrCode() > 0) {
			inParm.setErr(-1,
					"INS_IO_PARM 表中没有找到[CITY=" + parm.getValue("CITY")
							+ " PIPELINE=" + parm.getValue("PIPELINE")
							+ " PLOT_TYPE=" + parm.getValue("PLOT_TYPE")
							+ "]的相关数据!");
			return inParm;
		}
		parm.setData("ioParm", ioParm);

		// 得到传入接口数据类型
		parm.setData("IN_OUT", "IN");
		TParm ioData = getIOData(parm);
		if (ioData.getErrCode() < 0) {
			inParm.setErr(-1, ioData.getErrText(), ioData.getErrName());
			return inParm;
		}
		int count = ioData.getCount("ID");
		StringBuffer sb = new StringBuffer();
		// 默认的分割符
		String separator = sysParm.getValue("SEPARATOR", 0);
		// 默认的行结束符
		String newline = initNewline(sysParm.getValue("NEWLINE", 0));
		// 默认的结束符
		String finish = initNewline(sysParm.getValue("FINISH", 0));
		String cn = ioData.getValue("COLUMN_NAME", 0);
		if (parm.getData(cn) == null) {
			inParm.setErr(-1, "InsInterface.parse()parm 缺少 " + cn + " "
					+ ioData.getData("COLUMN_DESC", 0) + " 必要参数");
			return inParm;
		}
		int rowCount = parm.getCount(cn);
		for (int row = 0; row < rowCount; row++) {
			for (int i = 0; i < count; i++) {
				if (i > 0)
					sb.append(separator);
				String columnName = ioData.getValue("COLUMN_NAME", i);
				boolean need = ioData.getBoolean("NEED", i);
				String dataType = ioData.getValue("DATA_TYPE", i);
				String columnDesc = ioData.getValue("COLUMN_DESC", i);
				String format = ioData.getValue("FORMAT", i);
				String defaultValue = ioData.getValue("DEFAULT_VALUE", i);
				int precision = 0;
				int scale = 0;
				int length = 0;
				try {
					precision = ioData.getInt("PRECISION", i);
					scale = ioData.getInt("SCALE", i);
					length = ioData.getInt("LENGTH", i);
				} catch (Exception e) {
					inParm.setErr(
							-1,
							"INS_IO_表中数据非法[CITY="
									+ parm.getValue("CITY")
									+ " PIPELINE="
									+ parm.getValue("PIPELINE")
									+ " PLOT_TYPE="
									+ parm.getValue("PLOT_TYPE")
									+ " IN_OUT=IN] 在 PRECISION,SCALE,LENGTH 列中存在null!");
					return inParm;
				}
				String data = parm.getValue(columnName, row);
				// 检测必要数据是否存在
				if (need && (data.length() == 0)) {
					inParm.setErr(-1, "InsInterface.parse()parm 缺少 "
							+ columnName + " " + columnDesc + " 必要参数在第 " + row
							+ " 行!");
					return inParm;
				}
				// 扩展其他检核
				if (data.length() == 0) {
					if (defaultValue != null && defaultValue.length() > 0)
						data = defaultValue;
					else
						data = " ";
				}
				if (format != null && format.length() > 0) {
					if ("BOOLEAN".equals(format)) {
						if ("Y".equals(data))
							data = "1";
						if ("N".equals(data))
							data = "0";
						if ("".equals(data))
							data = "0";
					}
				}
				// 拼接数据
				sb.append(data);
			}
			if (rowCount > 1)
				sb.append(newline);
		}
		sb.append(finish);
		inParm.setData("InStrParm", sb.toString());
		return inParm;
		// 测试end
		// 得到传出接口数据类型
		// parm.getData("IN_OUT", "OUT");
		// ioData = getIOData(parm);
		// if (ioData.getErrCode() < 0) {
		// System.out.println(ioData.getErrText()+" "+ioData.getErrName());
		// }
		// // 计算返回数据长度
		// // 数据库设定最大缓冲行数
		// int row = ioParm.getInt("ROW_COUNT", 0);
		// int dataSize = 0;
		// // 输出列个数
		// count = ioData.getCount("ID");
		// // 累加总输出数据字节
		// for (int i = 0; i < count; i++)
		// dataSize += ioData.getInt("LENGTH", i);
		// // 累加分割符和换行符
		// dataSize += count;
		// // 乘预先设定行数
		// dataSize *= row;
		// // 加结束符
		// dataSize += 1;
		// parm.setData("ioOutData", ioData);
		// parm.setData("RETURN_DATA_COUNT", dataSize);
		// return true;
	}

	/**
	 * 传出参数解析
	 *
	 * @param parm
	 *            TParm
	 * @param resultParm
	 *            TParm
	 * @return TParm
	 */
	public static TParm parseOut(TParm parm) {
		TParm result = new TParm();
		//System.out.println("------------进入-----------"+parm);
		// 得到医保基本参数
		TParm sysParm = INSTool.getInstance().getSysParm(parm);
		parm.setData("CITY", sysParm.getData("CITY", 0));
                parm.setData("IN_OUT","OUT");
		//System.out.println("------------sysParm入-----------" + sysParm);
		// System.out.println("sysParm" + sysParm);
		TParm ioData = INSTool.getInstance().getIOData(parm);
		//System.out.println("ioData" + ioData);
		// 默认的分割符
		String separator = sysParm.getValue("SEPARATOR", 0);
		// 默认的行结束符
		String newline = initNewline(sysParm.getValue("NEWLINE", 0));
		// 默认的结束符
		String finish = initNewline(sysParm.getValue("FINISH", 0));
		// 出参字符串
		String outStr = parm.getValue("OutStrParm");

		outStr.replace("[", "");
		outStr.replace("]", "");
		if (outStr.length() == 0) {
			result.setErr(-1, "没有读到文件");
			return result;
		}
		outStr = outStr.trim();
		if (outStr.endsWith(finish)) {
			outStr = outStr.substring(0, outStr.length() - finish.length());
		}
		if (!outStr.endsWith(newline))
			outStr += newline;
		// 参数个数
		int count = ioData.getCount("ID");
//                System.out.println("参数个数AAA"+count);
//                System.out.println("出参字符串"+outStr);
		String[] sData =  parseNewLine(outStr, newline, false);
		//System.out.println("参数个数BBB"+sData.length);
		int head = -1;
		for (int i = 0; i < sData.length; i++) {
			String[] cData = parseNewLine(sData[i], separator, true);
			if (count == cData.length) {
				for (int j = 0; j < count; j++) {
					String columnName = ioData.getValue("COLUMN_NAME", j);
					String format = ioData.getValue("FORMAT", j);
					String dataType = ioData.getValue("DATA_TYPE", j);
					String value = cData[j];
					if ("DATE".equals(dataType))
						value = dateFormat(value, format);
					result.addData(columnName, value);
				}
				continue;
			} else if (cData.length != 2 && count != cData.length + 2) {
				if (cData.length == 3) {
					result.setErr(-1, "InsInterface.parseOut()返回参数与预先设定参数个数不符 ");
					return result;
				}
				result.setErr(-1, "InsInterface.parseOut()返回参数与预先设定参数个数不符 ");
				return result;
			}
			if (i == 0) {
				for (int j = 0; j < cData.length; j++) {
					String columnName = ioData.getValue("COLUMN_NAME", j);
					String format = ioData.getValue("FORMAT", j);
					String dataType = ioData.getValue("DATA_TYPE", j);
					String value = cData[j];
					if ("DATE".equals(dataType))
						value = dateFormat(value, format);
					result.addData(columnName, value);
					head = j + 1;
				}
				continue;
			}
			if (head > 0) {
				for (int j = 0; j < cData.length; j++) {
					String columnName = ioData
							.getValue("COLUMN_NAME", j + head);
					String format = ioData.getValue("FORMAT", j + head);
					String dataType = ioData.getValue("DATA_TYPE", j + head);
					String value = cData[j];
					if ("DATE".equals(dataType))
						value = dateFormat(value, format);
					result.addData(columnName, value);
				}
				continue;
			}
		}

		return result;
	}

	public static String dateFormat(String s, String format) {
		if (format == null)
			format = "";
		StringBuffer v = new StringBuffer();
		for (int i = 0; i < s.length(); i++)
			switch (s.charAt(i)) {
			case ' ':
			case '/':
			case '-':
			case ':':
				break;
			default:
				v.append(s.charAt(i));
			}
		String value = v.toString();
		if ("DATE".equals(format))
			if (value.length() > 8)
				value = value.substring(0, 8);
		return value;
	}

	/**
	 * 将数据拆分成多行
	 *
	 * @param s
	 *            String
	 * @param newline
	 *            String
	 * @param b
	 *            boolean
	 * @return String[]
	 */
	private static String[] parseNewLine(String s, String newline, boolean  flg) {
		List list = new ArrayList();
		if (s.startsWith(newline))
		s = s.substring(newline.length(), s.length());
		int index = s.indexOf(newline);
		while (index >= 0) {
			list.add(s.substring(0, index));
			s = s.substring(index + newline.length(), s.length());
			index = s.indexOf(newline);
		}
		if (flg)
			list.add(s);
		return (String[]) list.toArray(new String[] {});
	}
	/**
	 * 初始化换行符
	 *
	 * @param s
	 *            String
	 * @return String
	 */
	public static String initNewline(String s) {
		if (s.startsWith("char(") && s.endsWith(")"))
			return ""
					+ ((char) Integer.parseInt(s.substring(5, s.length() - 1)));
		return s;
	}

	/**
	 * 得到接口参数数据
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getIOData(TParm parm) {
		TParm ioData = getTool().getIOData(parm);
		if (ioData.getErrCode() < 0)
			return ioData;
		if (ioData.getErrCode() > 0) {
			ioData.setErr(-1,
					"INS_IO 表中没有找到 [CITY=" + parm.getValue("CITY")
							+ " PIPELINE=" + parm.getValue("PIPELINE")
							+ " PLOT_TYPE=" + parm.getValue("PLOT_TYPE")
							+ " IN_OUT=" + parm.getValue("IN_OUT") + "]的相关数据!");
			return ioData;
		}
		return ioData;
	}

	/**
	 * 检验方法中入参参数正确性（方法共用）
	 *
	 * @param interName
	 *            String 函数名称
	 * @param method
	 *            String 接口方法
	 * @param in_outType
	 *            String 函数类型
	 * @param count
	 *            int 函数个数
	 * @return boolean 返回值类型
	 */
	private boolean IsinsIodata(String interName, String method,
			String in_outType, int count) {
		boolean flg = true;
		StringBuffer Str = new StringBuffer();
		if (!interName.equals("")) {
			Str.append(" PIPELINE='" + interName + "'");
			Str.append(" AND ");
		}
		if (!method.equals("")) {
			Str.append(" PLOT_TYPE='" + method + "'");
			Str.append(" AND ");
		}
		// 入参类型IN
		if (!in_outType.equals("")) {
			Str.append(" IN_OUT='" + in_outType + "'");
		}
		String sql = " SELECT COLUMN_NAME,COLUMN_DESC,DATA_TYPE,LENGTH,PRECISION,SCALE,NEED,DEFAULT_VALUE,"
				+ " FORMAT,REMARK "
				+ " FROM INS_IO"
				+ " WHERE "
				+ Str.toString();
		TParm parm = new TParm(this.getDBTool().select(sql));
		int Parmcount = parm.getCount();
		if (Parmcount < 0) {
			flg = false;
		}
		if (Parmcount != count) {
			flg = false;
		}
		return flg;
	}

	/**
	 * 获得数据模型INSTool
	 *
	 * @alias 获得数据模型INSTool
	 * @return INSTool
	 */
	public INSTool getTool() {
		return INSTool.getInstance();
	}

	/**
	 * getDBTool 数据库工具实例
	 *
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 得到日志路径
	 *
	 * @return
	 */
	public String getInsLogPath() {
		String path = "";
		path = getProp().getString("InsLogPath");
		if (path == null || path.trim().length() <= 0) {
			System.out.println("配置文件日志路径错误！");
		}
		return path;
	}

	/**
	 * 得到debug标记
	 *
	 * @return
	 */
	public String getInsDebug() {
		String debug = "";
		debug = getProp().getString("", "InsDebug");
               // System.out.println("得到debug标记"+debug);
		if (debug == null || debug.trim().length() <= 0) {
			System.out.println("配置文件医保debug标记错误！");
		}
		return debug;
	}

	/**
	 * 得到医保类型
	 *
	 * @return
	 */
	public String getInsType() {
		String type = "";
		type = getProp().getString("", "InsType");
		if (type == null || type.trim().length() <= 0) {
			System.out.println("配置文件医保类型错误！");
		}
		return type;
	}

	/**
	 * 得到医保服务器Ip
	 *
	 * @return
	 */
	public String getInsHost() {
		String ip = "";
		ip = getProp().getString("", "InsHost");
		if (ip == null || ip.trim().length() <= 0) {
			System.out.println("配置文件医保服务器Ip错误！");
		}
		return ip;
	}

	/**
	 * 得到医保服务器端口
	 *
	 * @return
	 */
	public String getInsPort() {
		String port = "";
		port = getProp().getString("", "InsPort");
		if (port == null || port.trim().length() <= 0) {
			System.out.println("配置文件医保服务器端口错误！");
		}
		return port;
	}

	/**
	 * 得到调试入参路径
	 *
	 * @return
	 */
	public String getInsInPath() {
		String inPath = "";
		inPath = getProp().getString("", "InsInPath");
               // System.out.println("路径"+inPath);
		if (inPath == null || inPath.trim().length() <= 0) {
			System.out.println("配置文件调试入参路径错误！");
		}
		return inPath;
	}

	/**
	 * 得到调试出参路径
	 *
	 * @return
	 */
	public static String getInsOutPath() {
		String outPath = "";
		outPath = getProp().getString("", "InsOutPath");
		if (outPath == null || outPath.trim().length() <= 0) {
			System.out.println("配置文件得到调试出参路径错误！");
		}
		return outPath;
	}

	/**
	 * 读取 TConfig.x
	 *
	 * @return TConfig
	 */
	public static TConfig getProp() {
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		if (config == null) {
			System.out.println("TConfig.x 文件没有找到！");
		}
		return config;
	}

	public static void main(String[] args) {
		// System.out.println("------------------"+i);
		String s="-1|SUCCESS|MT05511111259865207|0003654840|6013820200805581213|01|贾兰英|2" +
				" |120106194704067027|1947-04-06 00:00:00|64|21|00028145|E0134043|天津市和平区环" +
				"境卫生管理局一队|13|1|105025.93|150000|0|0|.1|0|.1|.2| |01| |0| ";

		JavaHisDebug.initServer();
		TParm parm = new TParm();
		String outdir = "D:\\log\\INS_OUT\\";
		String outfileName = "DataDown_mts(E).txt";
		Object object = null;
		try {
			object = FileTool.getString(outdir + outfileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Vector vct = (Vector) object;
		// 出参字符串
		String outStr = vct.get(0).toString();
		//System.out.println("--------------------------" + outStr);
		parm.setData("OutStrParm", outStr);
		parm.setData("HOSP_AREA", "HIS");
		parm.setData("PIPELINE", "DataDown_mts");
		parm.setData("PLOT_TYPE", "E");
		parm.setData("IN_OUT", "OUT");
		parm = parseOut(parm);
		//System.out.println("--------------------------" + parm);
	}
}
