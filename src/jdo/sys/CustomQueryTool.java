package jdo.sys;

import com.dongyang.jdo.TJDODBTool;
import java.util.Map;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * <p>Title: 自定义查询工具类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.18
 * @version 1.0
 */
public class CustomQueryTool extends TJDODBTool{
    /**
     * 唯一实例
     */
    private static CustomQueryTool instance;
    /**
     * 得到实例
     * @return TJDODBTool
     */
    public static CustomQueryTool getInstance()
    {
        if(instance == null)
            instance = new CustomQueryTool();
        return instance;
    }

    /**
     * 得到列名列表
     * @param tableID String
     * @return String[]
     */
    public String[] getViewTableColumn(String tableID)
    {
        String SQL =
            "SELECT COLUMN_ID" +
            "  FROM SYS_VIEW_COLUMN" +
            " WHERE USER_ID='"+ Operator.getID() +"' AND" +
            "       TABLE_ID='" + tableID + "'" +
            " ORDER BY SEQ";
        TParm parm = new TParm(select(SQL));
        return parm.getStringArray("COLUMN_ID");
    }
    /**
     * 保存列名列表
     * @param tableID String
     * @param columns String[]
     * @return TParm
     */
    public TParm updateViewTableColumn(String tableID,String columns[])
    {
        String userID = Operator.getID();
        String date = StringTool.getString(getDBTime(),"yyyyMMddHHmmss");
        String userIP = Operator.getIP();
        String SQL[] = new String[columns.length + 1];
        SQL[0] =
            "DELETE FROM SYS_VIEW_COLUMN"+
            " WHERE USER_ID='"+ userID +"' AND" +
            "       TABLE_ID='" + tableID + "'";
        for(int i = 0;i < columns.length;i++)
        {
            SQL[i + 1] =
                "INSERT INTO SYS_VIEW_COLUMN" +
                "       (USER_ID,TABLE_ID,COLUMN_ID,SEQ,OPT_USER,"+
                "        OPT_DATE,OPT_TERM)"+
                " VALUES('" + userID + "','" + tableID + "','" + columns[i] + "'," + i + ",'" + userID + "',"+
                "        TO_DATE('" + date + "','YYYYMMDDHH24MISS'),'" + userIP + "')";
        }
        TParm result = new TParm(update(SQL));
        if(result.getErrCode() < 0)
        {
            //System.out.println(result.getErrText());
            //System.out.println(StringTool.getString(SQL));
            return result;
        }
        return result;
    }
    /**
     * 插入Where
     * @param name String
     * @param tableID String
     * @param pubFlg boolean
     * @param defaultFlg boolean
     * @param parm TParm
     * @return TParm
     */
    public TParm insertViewWhere(String name,String tableID,boolean pubFlg,boolean defaultFlg,TParm parm)
    {
        //得到新Where编号
        int whereCode = getNewWhereCode();
        if(whereCode < 0)
        {
            TParm result = new TParm();
            result.setErr(-1,"取新编号错误!");
            return result;
        }
        String userID = Operator.getID();
        String date = StringTool.getString(getDBTime(),"yyyyMMddHHmmss");
        String userIP = Operator.getIP();
        //取顺序号
        int seq = 1;
        if(pubFlg)
            seq = getNewPubSeq(tableID);
        else
            seq = getNewPrvSeq(tableID,userID);
        if(seq < 0)
        {
            TParm result = new TParm();
            result.setErr(-1,"取新顺序号错误!");
            return result;
        }
        List list = new ArrayList();
        String whereSQL =
            "INSERT INTO SYS_VIEW_WHERE" +
            "            (WHERE_CODE,WHERE_DESC,TABLE_ID,PUB_FLG,OWNER_ID,"+
            "             SEQ,OPT_USER,OPT_DATE,OPT_TERM)" +
            "      VALUES(" + whereCode + ",'" + name + "','" + tableID + "','" + (pubFlg?'Y':'N') + "','" + userID + "'," +
            "             " + seq + ",'" + userID + "',TO_DATE('" + date + "','YYYYMMDDHH24MISS'),'" + userIP + "')";
        list.add(whereSQL);
        //保存默认
        if(defaultFlg)
        {
            String defaultSQL = getUpateDefaultSQL(tableID,whereCode,date);
            if(defaultSQL != null && defaultSQL.length() > 0)
                list.add(defaultSQL);
        }
        for(int i = 0;i < parm.getCount();i++)
        {
            String bracketStart = parm.getValue("BRACKET_START",i);
            String columnID = parm.getValue("COLUMN_ID",i);
            String mark = parm.getValue("MARK",i);
            String value = parm.getValue("VALUE",i);
            String breacketEnd = parm.getValue("BRACKET_END",i);
            String link = parm.getValue("LINK",i);
            String externalFlg = parm.getValue("EXTERNAL_FLG",i);
            String detailSQL =
                "INSERT INTO SYS_VIEW_WHERE_DETAIL" +
                "           (WHERE_CODE,SEQ,BRACKET_START,COLUMN_ID,MARK," +
                "            VALUE,BRACKET_END,LINK,EXTERNAL_FLG,OPT_USER," +
                "            OPT_DATE,OPT_TERM)"+
                "     VALUES('" + whereCode + "'," + (i + 1) + ",'" + bracketStart + "','" + columnID + "','" + mark + "',"+
                "            '" + value + "','" + breacketEnd + "','" + link + "','" + externalFlg + "','" + userID + "',"+
                "            TO_DATE('" + date + "','YYYYMMDDHH24MISS'),'" + userIP + "')";
            list.add(detailSQL);
        }
        String []SQL = (String[])list.toArray(new String[]{});
        TParm result = new TParm(update(SQL));
        if(result.getErrCode() < 0)
        {
            //System.out.println(result.getErrText());
            //System.out.println(StringTool.getString(SQL));
            return result;
        }
        result.setData("WHERE_CODE",whereCode);
        return result;
    }
    /**
     * 更新Where
     * @param whereCode int
     * @param name String
     * @param tableID String
     * @param pubFlg boolean
     * @param defaultFlg boolean
     * @param parm TParm
     * @return TParm
     */
    public TParm updateViewWhere(int whereCode,String name,String tableID,boolean pubFlg,boolean defaultFlg,TParm parm)
    {
        String userID = Operator.getID();
        String date = StringTool.getString(getDBTime(),"yyyyMMddHHmmss");
        String userIP = Operator.getIP();
        List list = new ArrayList();
        String whereSQL =
            "UPDATE SYS_VIEW_WHERE" +
            "   SET WHERE_DESC='" + name + "'," +
            "       PUB_FLG='" + (pubFlg?'Y':'N') + "'," +
            "       OWNER_ID='" + userID + "'," +
            "       OPT_USER='" + userID + "'," +
            "       OPT_DATE=TO_DATE('" + date + "','YYYYMMDDHH24MISS')," +
            "       OPT_TERM='" + userIP + "'" +
            " WHERE WHERE_CODE=" + whereCode;
        list.add(whereSQL);
        String deleteSQL =
            "DELETE FROM SYS_VIEW_WHERE_DETAIL" +
            " WHERE WHERE_CODE=" + whereCode;
        list.add(deleteSQL);
        //保存默认
        if(defaultFlg)
        {
            String defaultSQL = getUpateDefaultSQL(tableID,whereCode,date);
            if(defaultSQL != null && defaultSQL.length() > 0)
                list.add(defaultSQL);
        }
        for(int i = 0;i < parm.getCount();i++)
        {
            String bracketStart = parm.getValue("BRACKET_START",i);
            String columnID = parm.getValue("COLUMN_ID",i);
            String mark = parm.getValue("MARK",i);
            String value = parm.getValue("VALUE",i);
            String breacketEnd = parm.getValue("BRACKET_END",i);
            String link = parm.getValue("LINK",i);
            String externalFlg = parm.getValue("EXTERNAL_FLG",i);
            String detailSQL =
                "INSERT INTO SYS_VIEW_WHERE_DETAIL" +
                "           (WHERE_CODE,SEQ,BRACKET_START,COLUMN_ID,MARK," +
                "            VALUE,BRACKET_END,LINK,EXTERNAL_FLG,OPT_USER," +
                "            OPT_DATE,OPT_TERM)"+
                "     VALUES('" + whereCode + "'," + (i + 1) + ",'" + bracketStart + "','" + columnID + "','" + mark + "',"+
                "            '" + value + "','" + breacketEnd + "','" + link + "','" + externalFlg + "','" + userID + "',"+
                "            TO_DATE('" + date + "','YYYYMMDDHH24MISS'),'" + userIP + "')";
            list.add(detailSQL);
        }
        String []SQL = (String[])list.toArray(new String[]{});
        TParm result = new TParm(update(SQL));
        if(result.getErrCode() < 0)
        {
            //System.out.println(result.getErrText());
            //System.out.println(StringTool.getString(SQL));
            return result;
        }
        return result;
    }
    /**
     * 设置默认
     * @param tableID String
     * @param whereCode int
     * @return TParm
     */
    public TParm getUpateDefaultSQL(String tableID,int whereCode)
    {
        String date = StringTool.getString(getDBTime(),"yyyyMMddHHmmss");
        String SQL = getUpateDefaultSQL(tableID,whereCode,date);
        if(SQL.length() == 0)
            return new TParm();
        TParm result = new TParm(update(SQL));
        if(result.getErrCode() < 0)
        {
            //System.out.println(result.getErrText());
            //System.out.println(SQL);
            return result;
        }
        return result;
    }
    /**
     * 得到设置默认where的SQL更新语句
     * @param tableID String
     * @param whereCode int
     * @param date String
     * @return String
     */
    public String getUpateDefaultSQL(String tableID,int whereCode,String date)
    {
        String userID = Operator.getID();
        String userIP = Operator.getIP();
        int code = getDefaultWhere(tableID);
        if(code == whereCode)
            return "";
        if(code <= 0)
            return
                "INSERT INTO SYS_VIEW_TABLE"+
                "            (USER_ID,TABLE_ID,DEFAULD_WHERE_CODE,OPT_USER,OPT_DATE," +
                "             OPT_TERM)"+
                "      VALUES('" + userID + "','" + tableID + "'," + whereCode + ",'" + userID + "',TO_DATE('" + date + "','YYYYMMDDHH24MISS'),"+
                "              '" + userIP + "')";
        return
            "UPDATE SYS_VIEW_TABLE" +
            "   SET DEFAULD_WHERE_CODE='" + whereCode + "',"+
            "       OPT_USER='" + userID + "'," +
            "       OPT_DATE=TO_DATE('" + date + "','YYYYMMDDHH24MISS'),"+
            "       OPT_TERM='" + userIP + "'"+
            " WHERE USER_ID='" + userID + "' AND"+
            "       TABLE_ID='" + tableID + "'";
    }
    /**
     * 得到默认查询编号
     * @param tableID String
     * @return int
     */
    public int getDefaultWhere(String tableID)
    {
        String userID = Operator.getID();
        String SQL=
            "SELECT DEFAULD_WHERE_CODE" +
            "  FROM SYS_VIEW_TABLE" +
            " WHERE USER_ID='" + userID + "' AND" +
            "       TABLE_ID='" + tableID + "'";
        //System.out.println("");
        TParm parm = new TParm (select(SQL));
        if(parm.getErrCode() < 0)
        {
            //System.out.println(parm.getErrText());
            return -1;
        }
        return parm.getInt("DEFAULD_WHERE_CODE",0);
    }
    /**
     * 得到新的私有顺序号
     * @param tableID String
     * @param ownerID String
     * @return int
     */
    public int getNewPrvSeq(String tableID,String ownerID)
    {
        String SQL =
            "SELECT MAX(SEQ) AS CODE" +
            "  FROM SYS_VIEW_WHERE" +
            " WHERE TABLE_ID='" + tableID + "' AND" +
            "       PUB_FLG='N' AND" +
            "       OWNER_ID='" + ownerID + "'";
        TParm parm = new TParm (select(SQL));
        if(parm.getErrCode() < 0)
        {
            //System.out.println(parm.getErrText());
            return -1;
        }
        return parm.getInt("CODE",0) + 1;
    }
    /**
     * 得到新的共有顺序号
     * @param tableID String
     * @return int
     */
    public int getNewPubSeq(String tableID)
    {
        String SQL =
            "SELECT MAX(SEQ) AS CODE" +
            "  FROM SYS_VIEW_WHERE" +
            " WHERE TABLE_ID='" + tableID + "' AND" +
            "       PUB_FLG='Y'";
        TParm parm = new TParm (select(SQL));
        if(parm.getErrCode() < 0)
        {
            System.out.println(parm.getErrText());
            return -1;
        }
        return parm.getInt("CODE",0) + 1;
    }
    /**
     * 得到新Where编号
     * @return int
     */
    public int getNewWhereCode()
    {
        String SQL =
            "SELECT MAX(WHERE_CODE) AS CODE" +
            "  FROM SYS_VIEW_WHERE";
        TParm parm = new TParm (select(SQL));
        if(parm.getErrCode() < 0)
        {
            //System.out.println(parm.getErrText());
            return -1;
        }
        return parm.getInt("CODE",0) + 1;
    }
    /**
     * 得到公有查询
     * @param tableID String
     * @return TParm
     */
    public TParm getPubWhereList(String tableID)
    {
        String SQL =
            "SELECT WHERE_CODE,WHERE_DESC,OWNER_ID,USER_NAME" +
            "  FROM SYS_VIEW_WHERE,SYS_OPERATOR"+
            " WHERE TABLE_ID='" + tableID + "' AND" +
            "       PUB_FLG='Y' AND" +
            "       OWNER_ID=USER_ID" +
            " ORDER BY SYS_VIEW_WHERE.SEQ";
        //System.out.println("CustomQueryTool->getPubWhereList->sql="+SQL);
        TParm parm = new TParm(select(SQL));
        if(parm.getErrCode() < 0)
            return parm;
        for(int i = 0;i < parm.getCount();i++)
            parm.addData("DEF","N");
        int defaultWhereCode = getDefaultWhere(tableID);
        if(defaultWhereCode > 0)
        {
            Vector v = (Vector)parm.getData("WHERE_CODE");
            if(v != null)
            {
                int index = v.indexOf( (long) defaultWhereCode);
                if (index >= 0)
                    parm.setData("DEF", index, "Y");
            }
        }
        return parm;
    }
    /**
     * 得到私有查询
     * @param tableID String
     * @return TParm
     */
    public TParm getPriWhereList(String tableID)
    {
        String userID = Operator.getID();
        String SQL =
            "SELECT WHERE_CODE,WHERE_DESC,OWNER_ID,TO_CHAR(OPT_DATE,'YYYY/MM/DD HH24:MI:SS') AS CREATE_DATE" +
            "  FROM SYS_VIEW_WHERE"+
            " WHERE TABLE_ID='" + tableID + "' AND" +
            "       PUB_FLG='N' AND" +
            "       OWNER_ID='" + userID + "'"+
            " ORDER BY SEQ";
        TParm parm = new TParm(select(SQL));
        if(parm.getErrCode() < 0)
            return parm;
        for(int i = 0;i < parm.getCount();i++)
            parm.addData("DEF","N");
        int defaultWhereCode = getDefaultWhere(tableID);
        if(defaultWhereCode > 0)
        {
            Vector v = (Vector)parm.getData("WHERE_CODE");
            if(v != null)
            {
                int index = v.indexOf( (long) defaultWhereCode);
                if (index >= 0)
                    parm.setData("DEF", index, "Y");
            }
        }
        return parm;
    }
    /**
     * 删除Where
     * @param whereCode int
     * @return TParm
     */
    public TParm deleteWhere(int whereCode)
    {
        String SQL[] = new String[]{
            "DELETE FROM SYS_VIEW_WHERE" +
            " WHERE WHERE_CODE=" + whereCode,
            "DELETE FROM SYS_VIEW_WHERE_DETAIL" +
            " WHERE WHERE_CODE=" + whereCode
        };
        TParm result = new TParm(update(SQL));
        if(result.getErrCode() < 0)
        {
            //System.out.println(result.getErrText());
            //System.out.println(SQL);
            return result;
        }
        return result;
    }
    /**
     * 得到 Where 信息
     * @param whereCode int
     * @return TParm
     */
    public TParm queryWhere(int whereCode)
    {
        String SQL =
            "SELECT BRACKET_START,COLUMN_ID,MARK,VALUE,BRACKET_END,LINK,EXTERNAL_FLG" +
            "  FROM SYS_VIEW_WHERE_DETAIL" +
            " WHERE WHERE_CODE=" + whereCode;
        return new TParm(select(SQL));
    }
    /**
     * 得到
     * @param wherecode int
     * @return int
     */
    public int getSeq(int wherecode)
    {
        String SQL =
            "SELECT SEQ" +
            "  FROM SYS_VIEW_WHERE" +
            " WHERE WHERE_CODE='" + wherecode + "'";
        TParm parm = new TParm (select(SQL));
        if(parm.getErrCode() < 0)
        {
            //System.out.println(parm.getErrText());
            return -1;
        }
        return parm.getInt("SEQ",0);
    }
    /**
     * 交换SEQ
     * @param wherecode int
     * @param wherecode1 int
     * @return TParm
     */
    public TParm updateSeq(int wherecode,int wherecode1)
    {
        int seq = getSeq(wherecode);
        int seq1 = getSeq(wherecode1);
        String SQL[] = new String[]{
            "UPDATE SYS_VIEW_WHERE" +
            "   SET SEQ=" + seq1 +
            " WHERE WHERE_CODE=" + wherecode,
            "UPDATE SYS_VIEW_WHERE" +
            "   SET SEQ=" + seq +
            " WHERE WHERE_CODE=" + wherecode1
        };
        TParm result = new TParm(update(SQL));
        if(result.getErrCode() < 0)
        {
            //System.out.println(result.getErrText());
            //System.out.println(SQL);
            return result;
        }
        return result;
    }
    /**
     * 取得选中列的信息
     * @param tempCode
     * @param tableId
     * @return
     */
    public TParm getColumn(String tempCode,String tableId){
    	TParm result=new TParm();
    	if(tempCode==null||tempCode.trim().length()<0||tableId==null||tableId.trim().length()<1){
    		return result;
    	}
    	String sql=" SELECT COLUMN_ID,COLUMN_NAME FROM SYS_VIEW_COLUMN WHERE TEMP_CODE='#' AND TABLE_ID='#'";
    	sql=sql.replaceFirst("#", tempCode);
    	sql=sql.replaceFirst("#", tableId);
    	result=new TParm(select(sql));

        if(result.getErrCode() < 0)
        {
            //System.out.println(result.getErrText());
            //System.out.println(sql);
            return result;
        }
    	return result;
    }
    /**
     * 取得选中合计列的信息
     * @param tempCode
     * @param tableId
     * @return
     */
    public TParm getSumColumn(String tempCode,String tableId){
    	TParm result=new TParm();
    	if(tempCode==null||tempCode.trim().length()<0||tableId==null||tableId.trim().length()<1){
    		return result;
    	}
    	String sql=" SELECT COLUMN_ID,COLUMN_NAME FROM SYS_VIEW_SUM WHERE TEMP_CODE='#' AND TABLE_ID='#'";
    	sql=sql.replaceFirst("#", tempCode);
    	sql=sql.replaceFirst("#", tableId);
    	result=new TParm(select(sql));

        if(result.getErrCode() < 0)
        {
            //System.out.println(result.getErrText());
            //System.out.println(sql);
            return result;
        }
    	return result;
    }
}
