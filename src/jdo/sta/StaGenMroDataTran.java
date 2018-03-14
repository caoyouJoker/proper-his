package jdo.sta;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import jdo.sys.SystemTool;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import jdo.sta.TrustAnyHostnameVerifier;
import jdo.sta.iTrustManager;
import com.alibaba.fastjson.JSONObject;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 发送dbf文件工具类
 * </p>
 * 
 * <p>
 * Description: 发送dbf文件工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author shibl
 * @version 1.0
 * 
 * 
 */
public class StaGenMroDataTran extends TJDOTool {
	/**
	 * 实例
	 */
	public static StaGenMroDataTran instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return SYSRegionTool
	 */
	public static StaGenMroDataTran getInstance() {
		if (instanceObject == null)
			instanceObject = new StaGenMroDataTran();
		return instanceObject;
	}

	public StaGenMroDataTran() {
		TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		String url = config.getString("Servcie_Url");
		String userName = config.getString("User");
		String pass = config.getString("Pwd");
		String dataType = config.getString("DataType");
		String debug=config.getString("DEBUG");
		this.setSERVICE_URL(url);
		this.setUSER(userName);
		this.setPWD(pass);
		this.setDATA_TYPE(dataType);
		this.setDEBUG(debug);
		onInit();
	}

	/**
	 * 服务器的域名地址
	 */
	private String SERVICE_URL = "";
	/**
	 * 用户名
	 */
	private String USER = ""; // superuser

	/**
	 * 密码
	 */
	private String PWD = ""; // cltr112233

	/**
	 * 返回数据的类型
	 */
	private String DATA_TYPE = "";
	/**
	 * 调试标记
	 */
	private String DEBUG="";
	/**
	 * @return the dEBUG
	 */
	public String getDEBUG() {
		return DEBUG;
	}

	/**
	 * @param dEBUG the dEBUG to set
	 */
	public void setDEBUG(String dEBUG) {
		DEBUG = dEBUG;
	}

	/**
	 * @return
	 */
	public String getSERVICE_URL() {
		return SERVICE_URL;
	}

	/**
	 * @param
	 */
	public void setSERVICE_URL(String sERVICEURL) {
		SERVICE_URL = sERVICEURL;
	}

	/**
	 * @return the uSER
	 */
	public String getUSER() {
		return USER;
	}

	/**
	 * @param uSER
	 *            the uSER to set
	 */
	public void setUSER(String uSER) {
		USER = uSER;
	}

	/**
	 * @return the pWD
	 */
	public String getPWD() {
		return PWD;
	}

	/**
	 * @param pWD
	 *            the pWD to set
	 */
	public void setPWD(String pWD) {
		PWD = pWD;
	}

	/**
	 * @return the dATA_TYPE
	 */
	public String getDATA_TYPE() {
		return DATA_TYPE;
	}

	/**
	 * @param dATATYPE
	 *            the dATA_TYPE to set
	 */
	public void setDATA_TYPE(String dATATYPE) {
		DATA_TYPE = dATATYPE;
	}

	/**
	 * 获取token
	 * 
	 */

	public TParm getToken() {
		TParm tokenParm = new TParm();
		String sUrl = this.getSERVICE_URL()
				+ "/docking/auto/gettoken.shtml?loginid=" + this.getUSER()
				+ "&pwd=" + this.getPWD() + "&datatype=" + this.getDATA_TYPE()
				+ "&isDev=1";
		// System.out.println("send getToken request url:" + sUrl);
		String response = "{}";
		String inputLine = "";
		String token = "";
		try {
			// 创建 SSLContext 对象。
			SSLContext sc = SSLContext.getInstance("SSL");
			// 初始化
			sc.init(null, new TrustManager[] { new iTrustManager() },new java.security.SecureRandom());
			URL url = new URL(sUrl);
			HttpURLConnection myUrlcon=null;
			if(DEBUG.equals("0")){//正式环境
				HttpsURLConnection	Urlcon = (HttpsURLConnection) url.openConnection();// 建立连接
				// 设置当此实例为安全 https URL 连接创建套接字时使用的 SSLSocketFactory
				Urlcon.setSSLSocketFactory(sc.getSocketFactory());
				// 设置的默认静态主机名校验器
				Urlcon.setHostnameVerifier(new TrustAnyHostnameVerifier());
				myUrlcon=Urlcon;
			}else{
				HttpURLConnection Urlcon = (HttpURLConnection) url.openConnection();// 建立连接
				myUrlcon=Urlcon;
			}
			myUrlcon.setConnectTimeout(60000);// 连接超时,单位:毫秒
			myUrlcon.setReadTimeout(60000); // 读取超时,单位:毫秒
			BufferedReader in = new java.io.BufferedReader(new InputStreamReader(myUrlcon.getInputStream(), "UTF-8"));
			if ((inputLine = in.readLine()) == null)
				inputLine = "";
			if (in != null)
				in.close();
			// System.out.println("inputLine=" + inputLine);
			response = inputLine;
			JSONObject obj = JSONObject.parseObject(response);
			boolean succ = obj.getBoolean("succ");// 成功
			if (succ) {
				token = obj.getJSONObject("result").getString("token");
				tokenParm.setData("TOKEN", token);
			} else {// 失败
				String errMsg = obj.getString("errMsg");
				String errorCode = obj.getString("errorCode");
				tokenParm.setData("ERRORCODE", errorCode);
				tokenParm.setData("ERRMSG", errMsg);
			}
			// System.out.println("token return: " + token);
		} catch (Exception e) {
			e.printStackTrace();
			tokenParm.setData("ERRORCODE", "HIS01");
			tokenParm.setData("ERRMSG", "获得token异常");
			return tokenParm;
		}
		return tokenParm;

	}

	/**
	 * 发送数据 本方法不使用justHttp的原因：因为这是给用户的Demo，用户并没有JustHttp
	 */
	public boolean sendData(String token, InputStream is) {
		// 定义数据的年份、月份、中心编号、数据地址
		// 请在使用时更改这些值
		String sUrl = this.getSERVICE_URL() + "/autoReqTran.shtml?token="
				+ token + "&datatype=" + this.getDATA_TYPE()
				+ "&rc020=1&rc021=1";// rc020：指定ICD10诊断编码：1=全国版；2=北京版（不传默认为北京版）。
		// rc021：指定ICD10形态学编码：1=全国版；2=北京版（不传默认为北京版）。
		// System.out.println("senddata url=" + sUrl);
		try {
			SSLContext sc;
			sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new iTrustManager() },
					new java.security.SecureRandom());
			URL url = new URL(sUrl);
			HttpURLConnection connection=null;
			if(DEBUG.equals("0")){//正式环境
				HttpsURLConnection	Urlcon = (HttpsURLConnection) url.openConnection();// 建立连接
				// 设置当此实例为安全 https URL 连接创建套接字时使用的 SSLSocketFactory
				Urlcon.setSSLSocketFactory(sc.getSocketFactory());
				// 设置的默认静态主机名校验器
				Urlcon.setHostnameVerifier(new TrustAnyHostnameVerifier());
				connection=Urlcon;
			}else{
				HttpURLConnection Urlcon = (HttpURLConnection) url.openConnection();// 建立连接
				connection=Urlcon;
			}
			// 设置连接超时时长，根据数据量大小而定
			connection.setConnectTimeout(10 * 60 * 1000);

			// 设置读取超时
			connection.setReadTimeout(50000);

			// 设置允许输出
			connection.setDoOutput(true);

			// 设置允许输入
			connection.setDoInput(true);

			// 设置不缓存
			connection.setUseCaches(false);

			// 设置为POST方式，必须的
			connection.setRequestMethod("POST");

			// 读取本地ZIP文件
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			// System.out.println("data_url"+data_url);
			// File file = new File(data_url);
			// InputStream is = new FileInputStream(file);
			IOUtils.copy(is, bout);

			// ----------设置连接的参数---------------

			// 设置维持长连接
			connection.setRequestProperty("Connection", "Keep-Alive");

			// 设置字符编码集
			connection.setRequestProperty("Charset", "UTF-8");

			// 设置长度
			connection.setRequestProperty("Content-Length", String.valueOf(bout
					.toByteArray().length));

			// 设置内容为流
			connection
					.setRequestProperty(
							"Accept",
							"image/gif,   image/x-xbitmap,   image/jpeg,   image/pjpeg,   application/vnd.ms-excel,   application/vnd.ms-powerpoint,   application/msword,   application/x-shockwave-flash,   application/x-quickviewplus,   */*");
			connection
					.setRequestProperty("Content-Type",
							"multipart/form-data; boundary=Bounday---------------------------7d318fd100112");

			// 设置不缓存
			connection.setRequestProperty("Cache-Control", "no-cache");

			// 不允许服务器以压缩的格式返回结果：因为是需要本地解压的
			// connection.setRequestProperty("Accept-Encoding",
			// "gzip,   deflate");

			// 将文件写入输出流
			OutputStream os = connection.getOutputStream();

			os.write(bout.toByteArray());
			bout.close();
			os.close();
			is.close();

			InputStream in = connection.getInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			for (int b = in.read(); b >= 0; b = in.read()) {
				outputStream.write((byte) b);
			}
			String res = new String(outputStream.toByteArray(), "UTF-8").trim();
			outputStream.close();
			in.close();
			// System.out.println("sendData return: " + res);
			return true;
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (NoSuchAlgorithmException e) {
			return false;
		} catch (KeyManagementException e) {
			return false;
		}
	}

	/**
	 * 查询结果
	 */
	public String findResult(String token) {
		String response = "";
		try {
			SSLContext sc;
			sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new iTrustManager() },
					new java.security.SecureRandom());
			URL url = new URL(this.getSERVICE_URL()
					+ "/docking/auto/findResult.shtml?token=" + token
					+ "&datatype=" + this.getDATA_TYPE());
			HttpURLConnection connection=null;
			if(DEBUG.equals("0")){//正式环境
				HttpsURLConnection	Urlcon = (HttpsURLConnection) url.openConnection();// 建立连接
				// 设置当此实例为安全 https URL 连接创建套接字时使用的 SSLSocketFactory
				Urlcon.setSSLSocketFactory(sc.getSocketFactory());
				// 设置的默认静态主机名校验器
				Urlcon.setHostnameVerifier(new TrustAnyHostnameVerifier());
				connection=Urlcon;
			}else{
				HttpURLConnection Urlcon = (HttpURLConnection) url.openConnection();// 建立连接
				connection=Urlcon;
			}
			connection.setConnectTimeout(50000);
			connection.setReadTimeout(50000);
			// 设置允许输出
			connection.setDoOutput(true);
			// 设置允许输入
			connection.setDoInput(true);
			// 设置不缓存
			connection.setUseCaches(false);
			// 设置字符编码集
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("contentType", "UTF-8");
			// 开始连接并获得结果
			InputStream in = connection.getInputStream();
			// 读取数据
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			for (int b = in.read(); b >= 0; b = in.read()) {
				outputStream.write((byte) b);
			}
			response = new String(outputStream.toByteArray(), "UTF-8").trim();
			outputStream.close();
			in.close();
			return response;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
			return response;
		} catch (NoSuchAlgorithmException e) {
			return response;
		} catch (KeyManagementException e) {
			return response;
		}
	}

	/**
	 * 解析
	 * 
	 * @param response
	 * @return
	 */
	public boolean onLog(String response, TConnection conn, String log) {
		TParm result = new TParm();
		String flg = "";
		String errMsg = "";
		String errorCode = "";
		String errorStack = "";
		String id = "";
		String date = "";
		String status = "";
		String errormsg = "";
		String hasErrorMsg = "";
		String grade0 = "";
		String grade1 = "";
		String grade2 = "";
		String grade3 = "";
		String stocked = "";
		String total = "";
		String grade = "";
		if (!response.equals("")) {
			JSONObject obj = JSONObject.parseObject(response);
			boolean succ = obj.getBoolean("succ");// 成功｜失败，此参数只作为此次请求的结果
			errMsg = obj.getString("errMsg");// 错误消息
			errorCode = obj.getString("errorCode");// 错误代码
			errorStack = obj.getString("errorStack");// 错误堆栈－JAVA格式
			id = obj.getJSONObject("result").getString("id");
			String loaddate = obj.getJSONObject("result").getString("loaddate");
			date = "TO_DATE('"
					+ loaddate.replace("-", "").replace(":", "").replace(" ",
							"").substring(0, 14) + "','YYYYMMDDHH24MISS')";
			status = obj.getJSONObject("result").getString("status");
			errormsg = obj.getJSONObject("result").getString("errormsg");
			hasErrorMsg = obj.getJSONObject("result").getString("hasErrorMsg");
			grade0 = obj.getJSONObject("result").getJSONObject("checkgrade")
					.getString("grade0");
			grade1 = obj.getJSONObject("result").getJSONObject("checkgrade")
					.getString("grade1");
			grade2 = obj.getJSONObject("result").getJSONObject("checkgrade")
					.getString("grade2");
			grade3 = obj.getJSONObject("result").getJSONObject("checkgrade")
					.getString("grade3");
			stocked = obj.getJSONObject("result").getJSONObject("checkgrade")
					.getString("stocked");
			total = obj.getJSONObject("result").getJSONObject("checkgrade")
					.getString("total");
			grade = obj.getJSONObject("result").getJSONObject("checkgrade")
					.getString("grade");
			flg = succ ? "Y" : "N";
		} else {
			flg = "N";
		}
		String DateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyyMMddHHmmss");
		if(id.equals(""))
			id=DateStr;
		String sql = "INSERT INTO STA_MRO_LOG (LOG_DATE, RESULT, ERRORCODE, ID, LOADDATE, "
				+ "STATUS, ERRORMSG, HASERRORMSG, GRADE0, GRADE1, "
				+ "GRADE2, GRADE3, STOCKED, TOTAL, GRADE,LOG)"
				+ "VALUES"
				+ "('"
				+ DateStr
				+ "','"
				+ flg
				+ "', '"
				+ errorCode
				+ "', '"
				+ id
				+ "',"
				+ (date.equals("") ? "NULL" : date)
				+ ",'"
				+ status
				+ "', '"
				+ errormsg
				+ "', '"
				+ hasErrorMsg
				+ "', '"
				+ grade0
				+ "', '"
				+ grade1
				+ "', "
				+ "'"
				+ grade2
				+ "', '"
				+ grade3
				+ "', '"
				+ stocked
				+ "', '"
				+ total
				+ "', '"
				+ grade
				+ "', '"
				+ log + "')";
		result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		StaGenMroDataTran demo = new StaGenMroDataTran();
		try {
			TParm tokenParm = demo.getToken();
			String token = tokenParm.getValue("TOKEN");
			// System.out.println(token);
			// String token = "gqntanru28ob8mqvqsltuc5fn";
			if (StringUtils.isNotBlank(token)) {// false
				// demo.sendData(token);
				Thread.sleep(2000);
				// String token = "15097f92d103483fbe02d29d1ee9cd25";
				demo.findResult(token);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
