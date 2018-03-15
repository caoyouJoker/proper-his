package jdo.sta;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.FileTool;

/**
 * <p>
 * Title: 导出到dbf文件工具类
 * </p>
 * 
 * <p>
 * Description: 导出到dbf文件工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author shibl
 * @version 1.0
 * 
 * 
 */
public class StaMroGenTranDataTool extends TJDOTool {
	public StaMroGenTranDataTool() {
	}

	/**
	 * 实例
	 */
	public static StaMroGenTranDataTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return StaMroGenTranDataTool
	 */
	public static StaMroGenTranDataTool getInstance() {
		if (instanceObject == null)
			instanceObject = new StaMroGenTranDataTool();
		return instanceObject;
	}

	/* STA_MRO_DAILY 手动导出到dbf文件 */
	public TParm createDbf(String strDate, String endDate, String fileNa,
			String filePath, TParm parm, TConnection conn) {
		TParm reParm = new TParm();
		fileNa="hqms_"+fileNa;
		String str = "";
		for (int i = 0; i < parm.getCount(); i++) {
			String mrNo = parm.getValue("MR_NO", i);// 病案号
			int inDays = parm.getInt("IN_COUNT", i);// 实际住院天数
			if (str.length() <= 0) {
				str += "'" + mrNo + inDays + "'";
			} else {
				str += ",'" + mrNo + inDays + "'";
			}
		}
		File file = new File(filePath);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
		try {
			String querySql = "SELECT * FROM STA_MRO_DAILY "
					+ " WHERE P25 BETWEEN TO_DATE('" + strDate
					+ "','YYYYMMDDHH24MISS') " + " AND TO_DATE('" + endDate
					+ "','YYYYMMDDHH24MISS') AND P3||P2 IN(" + str + ")";
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(querySql);
			ResultSetMetaData metaData = result.getMetaData();
			int columnCnt = metaData.getColumnCount();
			String jdbURL = "jdbc:dbf:/" + filePath;
			Class.forName("com.caigen.sql.dbf.DBFDriver");
			Properties props = new Properties();
			props.setProperty("delayedClose", "0");
//			props.setProperty("dateFormat", "yyyy-MM-dd hh:mm:ss");
			Connection connDbf = DriverManager.getConnection(jdbURL, props);
			Statement stmtDbf = connDbf
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			String insertSql1 = "";
			String createSql = "create table if not exists " + fileNa + "("
					+ " P900   VARCHAR(22),    " + " P6891  VARCHAR(80),    "
					+ " P686   VARCHAR(50),    " + " P800   VARCHAR(50),    "
					+ " P1     VARCHAR(1) ,    " + " P2     integer ,      "
					+ " P3     VARCHAR(20) ,   " + " P4     VARCHAR(40) ,   "
					+ " P5     VARCHAR(1) ,    " + " P6     DATE ,          "
					+ " P7     integer,       " + " P8      VARCHAR(1) ,    "
					+ " P9     VARCHAR(2),     " + " P101   VARCHAR(30),    "
					+ " P102   VARCHAR(30),    " + " P103   VARCHAR(30),    "
					+ " P11    VARCHAR(20),    " + " P12    VARCHAR(40),    "
					+ " P13    VARCHAR(20) ,   " + " P801   VARCHAR(200),   "
					+ " P802   VARCHAR(40),    " + " P803   VARCHAR(6),     "
					+ " P14    VARCHAR(200),   " + " P15    VARCHAR(40),    "
					+ " P16    VARCHAR(6),     " + " P17    VARCHAR(200),   "
					+ " P171   VARCHAR(6),     " + " P18    VARCHAR(20),    "
					+ " P19    VARCHAR(40),    " + " P20    VARCHAR(200),   "
					+ " P804   VARCHAR(1),     " + " P21    VARCHAR(30),    "
					+ " P22    TIMESTAMP ,           " + " P23    VARCHAR(6) ,    "
					+ " P231   VARCHAR(4),     " + " P24    VARCHAR(6),     "
					+ " P25    TIMESTAMP ,           " + " P26    VARCHAR(6) ,    "
					+ " P261   VARCHAR(4),     " + " P27    integer ,      "
					+ " P28    VARCHAR(20),    " + " P281   VARCHAR(100) ,  "
					+ " P29    VARCHAR(1),     " + " P30    VARCHAR(30),    "
					+ " P301   VARCHAR(100),   " + " P31    TIMESTAMP,            "
					+ " P321   VARCHAR(20),    " + " P322   VARCHAR(100) ,  "
					+ " P805   VARCHAR(1),     " + " P323   VARCHAR(1),     "
					+ " P324   VARCHAR(20),    " + " P325   VARCHAR(100),   "
					+ " P806   VARCHAR(1),     " + " P326   VARCHAR(1),     "
					+ " P327   VARCHAR(20),    " + " P328   VARCHAR(100),   "
					+ " P807   VARCHAR(1),     " + " P329   VARCHAR(1),     "
					+ " P3291  VARCHAR(20),    " + " P3292  VARCHAR(100),   "
					+ " P808   VARCHAR(1),     " + " P3293  VARCHAR(1),     "
					+ " P3294  VARCHAR(20),    " + " P3295  VARCHAR(100),   "
					+ " P809   VARCHAR(1),     " + " P3296  VARCHAR(1),     "
					+ " P3297  VARCHAR(20),    " + " P3298  VARCHAR(100),   "
					+ " P810   VARCHAR(1),     " + " P3299  VARCHAR(1),     "
					+ " P3281  VARCHAR(20),    " + " P3282  VARCHAR(100),   "
					+ " P811   VARCHAR(1),     " + " P3283  VARCHAR(1),     "
					+ " P3284  VARCHAR(20),    " + " P3285  VARCHAR(100),   "
					+ " P812   VARCHAR(1),     " + " P3286  VARCHAR(1),     "
					+ " P3287  VARCHAR(20),    " + " P3288  VARCHAR(100),   "
					+ " P813   VARCHAR(1),     " + " P3289  VARCHAR(1),     "
					+ " P3271  VARCHAR(20),    " + " P3272  VARCHAR(100),   "
					+ " P814   VARCHAR(1),     " + " P3273  VARCHAR(1),     "
					+ " P3274  VARCHAR(20),    " + " P3275  VARCHAR(100),   "
					+ " P815   VARCHAR(1),     " + " P3276  VARCHAR(1),     "
					+ " P689   integer,       " + " P351   VARCHAR(20),    "
					+ " P352   VARCHAR(100),   " + " P816   VARCHAR(50),    "
					+ " P353   VARCHAR(20),    " + " P354   VARCHAR(100),   "
					+ " P817   VARCHAR(50),    " + " P355   VARCHAR(20),    "
					+ " P356   VARCHAR(100),   " + " P818   VARCHAR(50),    "
					+ " P361   VARCHAR(20),    " + " P362   VARCHAR(100),   "
					+ " P363   VARCHAR(20),    " + " P364   VARCHAR(100),   "
					+ " P365   VARCHAR(20),    " + " P366   VARCHAR(100),   "
					+ " P371   VARCHAR(100),   " + " P372   VARCHAR(100),   "
					+ " P38    VARCHAR(1),     " + " P39    VARCHAR(1),     "
					+ " P40    VARCHAR(1),     " + " P411   VARCHAR(1),     "
					+ " P412   VARCHAR(1),     " + " P413   VARCHAR(1),     "
					+ " P414   VARCHAR(1),     " + " P415   VARCHAR(1),     "
					+ " P421   integer,       " + " P422   integer,       "
					+ " P687   VARCHAR(1),     " + " P688   VARCHAR(1),     "
					+ " P431   VARCHAR(40),    " + " P432   VARCHAR(40),    "
					+ " P433   VARCHAR(40),    " + " P434   VARCHAR(40),    "
					+ " P819   VARCHAR(40),    " + " P435   VARCHAR(40),    "
					+ " P436   VARCHAR(40),    " + " P437   VARCHAR(40),    "
					+ " P438   VARCHAR(40),    " + " P44    VARCHAR(1),     "
					+ " P45    VARCHAR(40),    " + " P46    VARCHAR(40),    "
					+ " P47    TIMESTAMP,            " + " P490   VARCHAR(20),    "
					+ " P491   TIMESTAMP,            " + " P820   VARCHAR(1),     "
					+ " P492   VARCHAR(100),   " + " P493   VARCHAR(4),     "
					+ " P494   integer,       " + " P495   VARCHAR(40),    "
					+ " P496   VARCHAR(40),    " + " P497   VARCHAR(40),    "
					+ " P498   VARCHAR(6),     " + " P4981  VARCHAR(1),     "
					+ " P499   VARCHAR(2),     " + " P4910  VARCHAR(40),    "
					+ " P4911  VARCHAR(20),    " + " P4912  TIMESTAMP,            "
					+ " P821   VARCHAR(1),     " + " P4913  VARCHAR(100),   "
					+ " P4914  VARCHAR(4),     " + " P4915  integer,       "
					+ " P4916  VARCHAR(40),    " + " P4917  VARCHAR(40),    "
					+ " P4918  VARCHAR(40),    " + " P4919  VARCHAR(6),     "
					+ " P4982  VARCHAR(1),     " + " P4920  VARCHAR(2),     "
					+ " P4921  VARCHAR(40),    " + " P4922  VARCHAR(40),    "
					+ " P4923  TIMESTAMP,            " + " P822   VARCHAR(1),     "
					+ " P4924  VARCHAR(100),   " + " P4925  VARCHAR(4),     "
					+ " P4526  integer,       " + " P4527  VARCHAR(40),    "
					+ " P4528  VARCHAR(40),    " + " P4529  VARCHAR(40),    "
					+ " P4530  VARCHAR(6),     " + " P4983  VARCHAR(1),     "
					+ " P4531  VARCHAR(2),     " + " P4532  VARCHAR(40),    "
					+ " P4533  VARCHAR(20),    " + " P4534  TIMESTAMP,            "
					+ " P823   VARCHAR(1),     " + " P4535  VARCHAR(100),   "
					+ " P4536  VARCHAR(4),     " + " P4537  integer,       "
					+ " P4538  VARCHAR(40),    " + " P4539  VARCHAR(40),    "
					+ " P4540  VARCHAR(40),    " + " P4541  VARCHAR(6),     "
					+ " P4984  VARCHAR(1),     " + " P4542  VARCHAR(2),     "
					+ " P4543  VARCHAR(40),    " + " P4544  VARCHAR(20),    "
					+ " P4545  TIMESTAMP,            " + " P824   VARCHAR(1),     "
					+ " P4546  VARCHAR(100),   " + " P4547  VARCHAR(4),     "
					+ " P4548  integer,       " + " P4549  VARCHAR(40),    "
					+ " P4550  VARCHAR(40),    " + " P4551  VARCHAR(40),    "
					+ " P4552  VARCHAR(6),     " + " P4985  VARCHAR(1),     "
					+ " P4553  VARCHAR(2),     " + " P4554  VARCHAR(40),    "
					+ " P45002 VARCHAR(20),    " + " P45003 TIMESTAMP,            "
					+ " P825   VARCHAR(1),     " + " P45004 VARCHAR(100),   "
					+ " P45005 VARCHAR(4),     " + " P45006 integer,       "
					+ " P45007 VARCHAR(40),    " + " P45008 VARCHAR(40),    "
					+ " P45009 VARCHAR(40),    " + " P45010 VARCHAR(6),     "
					+ " P45011 VARCHAR(1),     " + " P45012 VARCHAR(2),     "
					+ " P45013 VARCHAR(40),    " + " P45014 VARCHAR(20),    "
					+ " P45015 TIMESTAMP,            " + " P826   VARCHAR(1),     "
					+ " P45016 VARCHAR(100),   " + " P45017 VARCHAR(4),     "
					+ " P45018 integer,       " + " P45019 VARCHAR(40),    "
					+ " P45020 VARCHAR(40),    " + " P45021 VARCHAR(40),    "
					+ " P45022 VARCHAR(6),     " + " P45023 VARCHAR(1),     "
					+ " P45024 VARCHAR(2),     " + " P45025 VARCHAR(40),    "
					+ " P45026 VARCHAR(20),    " + " P45027 TIMESTAMP,            "
					+ " P827   VARCHAR(1),     " + " P45028 VARCHAR(100),   "
					+ " P45029 VARCHAR(4),     " + " P45030 integer,       "
					+ " P45031 VARCHAR(40),    " + " P45032 VARCHAR(40),    "
					+ " P45033 VARCHAR(40),    " + " P45034 VARCHAR(6),     "
					+ " P45035 VARCHAR(1),     " + " P45036 VARCHAR(2),     "
					+ " P45037 VARCHAR(40),    " + " P45038 VARCHAR(20),    "
					+ " P45039 TIMESTAMP,            " + " P828   VARCHAR(1),     "
					+ " P45040 VARCHAR(100),   " + " P45041 VARCHAR(4),     "
					+ " P45042 integer,       " + " P45043 VARCHAR(40),    "
					+ " P45044 VARCHAR(40),    " + " P45045 VARCHAR(40),    "
					+ " P45046 VARCHAR(6),     " + " P45047 VARCHAR(1),     "
					+ " P45048 VARCHAR(2),     " + " P45049 VARCHAR(40),    "
					+ " P45050 VARCHAR(20),    " + " P45051 TIMESTAMP,            "
					+ " P829   VARCHAR(1),     " + " P45052 VARCHAR(100),   "
					+ " P45053 VARCHAR(4),     " + " P45054 integer,       "
					+ " P45055 VARCHAR(40),    " + " P45056 VARCHAR(40),    "
					+ " P45057 VARCHAR(40),    " + " P45058 VARCHAR(6),     "
					+ " P45059 VARCHAR(1),     " + " P45060 VARCHAR(2),     "
					+ " P45061 VARCHAR(40),    " + " P561   integer,       "
					+ " P562   integer,       " + " P563   integer,       "
					+ " P564   integer,       " + " P6911  VARCHAR(4),     "
					+ " P6912  TIMESTAMP,            " + " P6913  TIMESTAMP,            "
					+ " P6914  VARCHAR(4),     " + " P6915  TIMESTAMP,            "
					+ " P6916  TIMESTAMP,            " + " P6917  VARCHAR(4),     "
					+ " P6918  TIMESTAMP,            " + " P6919  TIMESTAMP,            "
					+ " P6920  VARCHAR(4),     " + " P6921  TIMESTAMP,            "
					+ " P6922  TIMESTAMP,            " + " P6923  VARCHAR(4),     "
					+ " P6924  TIMESTAMP,            " + " P6925  TIMESTAMP,            "
					+ " P57    VARCHAR(1),     " + " P58    VARCHAR(1),     "
					+ " P581   VARCHAR(1),     " + " P60    VARCHAR(1),     "
					+ " P611   integer,       " + " P612   integer,       "
					+ " P613   integer,       " + " P59    VARCHAR(1),     "
					+ " P62    VARCHAR(1) ,    " + " P63    VARCHAR(1) ,    "
					+ " P64    VARCHAR(1),     " + " P651   integer,       "
					+ " P652   integer,       " + " P653   integer,       "
					+ " P654   integer,       " + " P655   integer,       "
					+ " P656   integer,      " + " P66    numeric(4,2),   "
					+ " P681   integer,       " + " P682   integer,       "
					+ " P683   integer,       " + " P684   integer,       "
					+ " P685   integer,       " + " P67    integer,       "
					+ " P731   integer,       " + " P732   integer,       "
					+ " P733   integer,       " + " P734   integer,       "
					+ " P72    integer,       " + " P830   VARCHAR(1),     "
					+ " P831   VARCHAR(100),   " + " P741   VARCHAR(1),     "
					+ " P742   VARCHAR(100),   " + " P743   VARCHAR(100),   "
					+ " P782   numeric(10,2) ,   "
					+ " P751   numeric(10,2),    "
					+ " P752   numeric(10,2),    "
					+ " P754   numeric(10,2),    "
					+ " P755   numeric(10,2),    "
					+ " P756   numeric(10,2),    "
					+ " P757   numeric(10,2),    "
					+ " P758   numeric(10,2),    "
					+ " P759   numeric(10,2),    "
					+ " P760   numeric(10,2),    "
					+ " P761   numeric(10,2),    "
					+ " P762   numeric(10,2),    "
					+ " P763   numeric(10,2),    "
					+ " P764   numeric(10,2),    "
					+ " P765   numeric(10,2),    "
					+ " P767   numeric(10,2),    "
					+ " P768   numeric(10,2),    "
					+ " P769   numeric(10,2),    "
					+ " P770   numeric(10,2),    "
					+ " P771   numeric(10,2),    "
					+ " P772   numeric(10,2),    "
					+ " P773   numeric(10,2),    "
					+ " P774   numeric(10,2),    "
					+ " P775   numeric(10,2),    "
					+ " P776   numeric(10,2),    "
					+ " P777   numeric(10,2),    "
					+ " P778   numeric(10,2),    "
					+ " P779   numeric(10,2),    "
					+ " P780   numeric(10,2),    "
					+ " P781   numeric(10,2)     " + ");";
//			System.out.println(createSql);
			stmtDbf.execute(createSql);
			String delSql = "delete from " + fileNa;
//			System.out.println(delSql);
			stmtDbf.execute(delSql);
			while (result.next()) {
				insertSql1 = "insert into " + fileNa + " values(";
				for (int i = 1; i <= columnCnt; i++) {
					if (result.getString(i) == null)
						insertSql1 += "null";
					else
						insertSql1 += "'" + result.getString(i) + "'";

					if (i < columnCnt)
						insertSql1 += ",";
				}
				insertSql1 += ");";
//				System.out.println(insertSql1);
				stmtDbf.execute(insertSql1);
			}

			stmtDbf.close();
			connDbf.close();
			stmt.close();
			FileInputStream stream = new FileInputStream(filePath + fileNa
					+ ".dbf");
			byte[] data = FileTool.getByte(stream);
			reParm.setData("BYTES", data);
//			System.out.println("dbf文件成功输出到" + fileNa + ".dbf" + "大小"
//					+ data.length);
			stream.close();
			file = new File(filePath+ fileNa+".dbf");
			file.delete();
			return reParm;
		} catch (SQLException sqle) {
			reParm.setErrCode(-1);
			reParm.setErrText("生成文件失败");
			do {
				System.out.println(sqle.getMessage());
				System.out.println("Error Code:" + sqle.getErrorCode());
				System.out.println("SQL State:" + sqle.getSQLState());
				sqle.printStackTrace();
			} while ((sqle = sqle.getNextException()) != null);
			return reParm;
		} catch (Exception e) {
			reParm.setErrCode(-1);
			reParm.setErrText("生成文件失败");
			e.printStackTrace();
			return reParm;
		}
	}
}
