package jdo.sys;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title:构建SQL数据类</p>
 *
 * <p>Description: 构建SQL数据类</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author ehui 2009.08.14
 * @version 1.0
 */
public class BuildSqlTool
    extends TJDODBTool {
    /**
     * 唯一实例
     */
    private static BuildSqlTool instance;
    /**
     * 得到实例
     * @return BuildSqlTool
     */
    public static BuildSqlTool getInstance() {
        if (instance == null)
            instance = new BuildSqlTool();
        return instance;
    }

    /**
     * 根据给入表名得到COLUMN TABLE的数据
     * @param tableId
     * @return
     */
    public TParm getColumn(String tableId) {
        TParm result = new TParm();
        if (tableId == null || tableId.trim().length() < 0) {
            return result;
        }
        TParm parm = new TParm(TJDODBTool.getInstance().getColumnsInf(tableId));
        //System.out.println("tableParm============" + parm);
        if (parm.getErrCode() < 0) {
            return result;
        }
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            result.addData("COLUMN_ID", parm.getValue("COLUMN_NAME", i));
            result.addData("COLUMN_NAME", parm.getValue("COMMENTS", i));
            result.addData("DATA_TYPE", parm.getValue("DATA_TYPE", i));
        }

        result.setCount(count);
        //System.out.println("result============" + result);
        return result;
    }

    /**
     * 根据给入的值拼装查询SQL
     * @param select TParm
     * @param groupby TParm
     * @param orderby TParm
     * @param where TParm
     * @param tableName String
     * @param isTop boolean
     * @param topNo String
     * @return sql String
     */
    public String buildSql(TParm select, TParm groupby, TParm orderby,
                           TParm where, String tableName, boolean isTop,
                           String topNo) {
        StringBuffer sql = new StringBuffer("SELECT ");
        if (select == null || select.getCount() < 1 || tableName == null ||
            tableName.trim().length() < 1) {
            return sql.toString();
        }
        int count = select.getCount();
        String columnName = "COLUMN_ID";
        String direction = "DIRECTION";
        boolean isSum = false;
        for (int i = 0; i < count; i++) {
            if (select.getBoolean("SUM", i)) {
                isSum = true;
                sql.append("SUM(" + select.getValue(columnName, i).toUpperCase() +
                           ") " + select.getValue(columnName, i).toUpperCase() +
                           ",");
            }
            else {
                sql.append(select.getValue(columnName, i).toUpperCase() + ",");
            }

        }
        sql = sql.replace(sql.lastIndexOf(","), sql.lastIndexOf(",") + 1,
                          " \r\n");
        sql.append(" FROM " + tableName.toUpperCase() + " ");
        if (where != null && where.getCount(columnName) > 0) {
            sql.append(" \r\n WHERE ");
            for (int i = where.getCount(columnName) - 1; i >= 0; i--) {
                if (where.getValue("VALUE", i) == null ||
                    "".equals(where.getValue("VALUE", i))) {
                    if ("Y".equals(where.getValue("NULL_FLG", i))) {
                        where.removeRow(i);
                    }
                }
            }
            int whereCount = where.getCount("COLUMN_ID");
            //System.out.println("whereCount="+whereCount);
            //System.out.println("where======"+where);
            for (int i = 0; i < whereCount; i++) {
                String dataType = where.getValue("DATA_TYPE", i);
                String Operator = where.getValue("MARK", i);
            // System.out.println("operator=" + Operator +
            //(Operator.contains("IS")));
            //fuxin modify start 2012.06.01  
                if (i < whereCount - 1)
                {
                    if ("DATE".equalsIgnoreCase(dataType) ||
                        "TIMESTAMP".equalsIgnoreCase(dataType)) {
                        sql.append("	" + where.getValue("BRACKET_START", i))
                        .append(where.getValue("COLUMN_NAME", i))
                            .append(" " +
                                    where.getValue("MARK", i)).append(
                            " TO_DATE('").
                            append(where.getValue("VALUE", i)).append(
                                "','YYYYMMDDHH24MISS') ")
                            .append(where.getValue("BRACKET_END", i) + " ").
                            append(where.getValue("LINK", i) + " \r\n");
                    }
                    else {
                        if (Operator.indexOf("IS") > -1) {
                            sql.append("	" + where.getValue("BRACKET_START", i)).
                                append(where.getValue("COLUMN_NAME", i))
                                .append(" " +
                                        where.getValue("MARK", i)).append(" ").
                                append(
                                    where.getValue("VALUE", i)).append(" ")
                                .append(where.getValue("BRACKET_END", i) + " ").
                                append(where.getValue("LINK", i) + " \r\n");
                        }
                        else {
                            sql.append("	" + where.getValue("BRACKET_START", i)).
                                append(where.getValue("COLUMN_NAME", i))
                                .append(" " +
                                        where.getValue("MARK", i)).append(" '").
                                append(
                                    where.getValue("VALUE", i)).append("' ")
                                .append(where.getValue("BRACKET_END", i) + " ").
                                append(where.getValue("LINK", i) + " \r\n");
                        }
                    }
               //fuxin modify start 2012.06.01                   
                }
                else 
                { 
                
                    if ("DATE".equalsIgnoreCase(dataType) ||
                        "TIMESTAMP".equalsIgnoreCase(dataType)) {
                        sql.append("	" + where.getValue("BRACKET_START", i)).
                            append(where.getValue("COLUMN_NAME", i))
                            .append(" " +
                                    where.getValue("MARK", i)).append(
                            " TO_DATE('").
                            append(where.getValue("VALUE", i)).append(
                                "','YYYYMMDDHH24MISS') ")
                            .append(where.getValue("BRACKET_END", i) + " ").
                            append("#").append(where.getValue("LINK", i) +
                                               " #\r\n");
                    }
                    else {
                        if (Operator.indexOf("IS") > -1) {
                            sql.append("	" + where.getValue("BRACKET_START", i)).
                                append(where.getValue("COLUMN_NAME", i))
                                .append(" " +
                                        where.getValue("MARK", i)).append(" ").
                                append(
                                    where.getValue("VALUE", i)).append(" ")
                                .append(where.getValue("BRACKET_END", i) + " ").
                                append("#").append(where.getValue("LINK", i) +
                                " #\r\n"); ;
                        }
                        else {
                            sql.append("	" + where.getValue("BRACKET_START", i)).
                                append(where.getValue("COLUMN_NAME", i))
                                .append(" " +
                                        where.getValue("MARK", i)).append(" '").
                                append(
                                    where.getValue("VALUE", i)).append("' ")
                                .append(where.getValue("BRACKET_END", i) + " ").
                                append("#").append(where.getValue("LINK", i) +
                                " #\r\n"); ;
                        }
                    }

                }
            }
            //fuxin modify end 2012.06.01  
            //System.out.println("输出----"+sql);
            sql = sql.replace(sql.indexOf("#"), sql.lastIndexOf("#") + 1, "");
        }
        if (isSum) {
            if (groupby == null || groupby.getCount() < 0) {
                return "";
            }
            sql.append(" \r\n GROUP BY ");
            int groupbyCount = groupby.getCount();
            for (int i = 0; i < groupbyCount; i++) {
                sql.append(groupby.getValue(columnName, i) + ",");
            }
            sql = sql.replace(sql.lastIndexOf(","), sql.lastIndexOf(",") + 1,
                              "");
        }
        else {
            if (groupby != null && groupby.getCount() > 0) {
                sql.append(" \r\n GROUP BY ");
                int groupbyCount = groupby.getCount();
                for (int i = 0; i < groupbyCount; i++) {
                    sql.append(groupby.getValue(columnName, i) + ",");
                }
                sql = sql.replace(sql.lastIndexOf(","),
                                  sql.lastIndexOf(",") + 1, "");
            }
        }

        if (orderby != null && orderby.getCount() > 0) {
            sql.append(" \r\n ORDER BY ");
            int orderbyCount = orderby.getCount();
            for (int i = 0; i < orderbyCount; i++) {
                sql.append(orderby.getValue(columnName, i)).append(" ").append(
                    orderby.getValue(direction, i) + ",");
            }
            sql = sql.replace(sql.lastIndexOf(","), sql.lastIndexOf(",") + 1,
                              "");
        }
        //System.out.println("sql.toString90===============" + sql.toString());
        if (isTop) {
            String subSql = sql.toString();
            subSql = "SELECT * FROM (" + subSql + ") TABLE1 WHERE ROWNUM<=" +
                topNo;
            return subSql;
        }
        return sql.toString();
    }

    /**
     * 保存SYS_VIEW_TEMPLATE，先按逐渐删除，再插入
     * @param template String
     * @param tableId String
     * @param tempCode TParm
     * @param conn TConnection
     * @return result TParm
     */
    public TParm onSaveTemplate(TParm template, String tableId, String tempCode,
                                TConnection conn) {
        TParm result = new TParm();
        if (tempCode == null || tableId == null || tempCode.trim().length() < 1 ||
            tableId.trim().length() < 1) {
            result.setErrCode( -1);
            //System.out.println("onSaveTemplate param is null");
            return result;
        }
        String delete = "DELETE SYS_VIEW_TEMPLATE WHERE TEMP_CODE='" + tempCode +
            "' AND TABLE_ID='" + tableId + "' AND USER_ID='" +
            template.getValue("USER_ID") +
            "'";
        result = new TParm(TJDODBTool.getInstance().update(delete));
        if (result == null || result.getErrCode() < 0) {
            //System.out.println("onSaveTemplate " + result.getErrText());
            return result;
        }
        /**
           TEMP_CODE    VARCHAR2(20 BYTE),
           USER_ID      VARCHAR2(20 BYTE),
           TABLE_ID     VARCHAR2(20 BYTE),
           TEMP_DESC    VARCHAR2(200 BYTE),
           SQL          NUMBER(3),
           DESCRIPTION  VARCHAR2(200 BYTE),
           OPT_USER     VARCHAR2(20 BYTE)                NOT NULL,
           OPT_DATE     TIMESTAMP(6)                     NOT NULL,
           OPT_TERM     VARCHAR2(20 BYTE)                NOT NULL
         */
        String insert =  
            "INSERT INTO SYS_VIEW_TEMPLATE(TEMP_CODE,USER_ID,TABLE_ID,TEMP_DESC,OPT_USER,OPT_DATE,OPT_TERM,IS_TOP,TOP_NO)" +
            "		VALUES('" + tempCode + "','" + template.getValue("USER_ID") +
            "','" + template.getValue("TABLE_ID") + "','" +
            template.getValue("TEMP_DESC") +
            "','" + template.getValue("OPT_USER") +
            "',TO_DATE('" + template.getValue("OPT_DATE") +
            "','YYYYMMDDHH24MISS'),'" + template.getValue("OPT_TERM") +
            "','" + template.getValue("IS_TOP") +
            "','" + template.getValue("TOP_NO") +
            "')";
        result = new TParm(TJDODBTool.getInstance().update(insert, conn));
        if (result == null || result.getErrCode() < 0) {
            //System.out.println("saveTemplate.sql======" + insert);
            //System.out.println("onSaveTemplate " + result.getErrText());
            return result;
        }

        return result;
    }

    /**
     * 保存SYS_VIEW_COLUMN，先按逐渐删除，再插入
     * @param template String
     * @param tableId String
     * @param tempCode TParm
     * @param conn TConnection
     * @return result TParm
     */
    public TParm onSaveSelect(TParm select, String tableId, String tempCode,
                              TConnection conn) {
        TParm result = new TParm();
        if (tempCode == null || tableId == null || tempCode.trim().length() < 1 ||
            tableId.trim().length() < 1) {
            result.setErrCode( -1);
            //System.out.println("onSaveSelect is null " + result.getErrText());
            return result;
        }
        if (select == null || select.getCount() < 1) {
            result.setErrCode( -1);
//            System.out.println("onSaveSelect select is null " +
//                               result.getErrText());
            return result;
        }

        String delete = "DELETE SYS_VIEW_COLUMN WHERE TEMP_CODE='" + tempCode +
            "' AND TABLE_ID='" + tableId + "'";
        //System.out.println("save Select.delete=" + delete);
        result = new TParm(TJDODBTool.getInstance().update(delete));
        if (result == null || result.getErrCode() < 0) {
            //System.out.println("onSaveSelect   " + result.getErrText());
            return result;
        }
        String now = StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                          "yyyyMMddHHmmss");
        int count = select.getCount();
        String insert = "INSERT INTO SYS_VIEW_COLUMN (TEMP_CODE,TABLE_ID,COLUMN_ID,COLUMN_NAME,SEQ,SUM,OPT_USER,OPT_DATE,OPT_TERM,DATA_TYPE)" +
            " VALUES('#','#','#','#',#,'#','#',TO_DATE('#','YYYYMMDDHH24MISS'),'#','#')";
        for (int i = 0; i < count; i++) {
            String sql = insert;
            sql = sql.replaceFirst("#", select.getValue("TEMP_CODE", i));
            sql = sql.replaceFirst("#", select.getValue("TABLE_ID", i));
            sql = sql.replaceFirst("#", select.getValue("COLUMN_ID", i));
            sql = sql.replaceFirst("#", select.getValue("COLUMN_NAME", i));
            sql = sql.replaceFirst("#", select.getValue("SEQ", i));
            sql = sql.replaceFirst("#", select.getValue("SUM", i));
            sql = sql.replaceFirst("#", select.getValue("OPT_USER", i));
            //System.out.println("now==========" + now);
//            System.out.println("opt_date============" +
//                               select.getValue("OPT_DATE", i));
            sql = sql.replaceFirst("#", now);
            sql = sql.replaceFirst("#", select.getValue("OPT_TERM", i));
            sql = sql.replaceFirst("#", select.getValue("DATA_TYPE", i));
            result = new TParm(TJDODBTool.getInstance().update(sql, conn));
            if (result == null || result.getErrCode() < 0) {
                //System.out.println("select sql=============" + sql);
                //System.out.println("onSaveSelect " + result.getErrText());
                return result;
            }
        }

        return result;
    }

    /**
     * 保存SYS_VIEW_ORDERBY，先按逐渐删除，再插入
     * @param template String
     * @param tableId String
     * @param tempCode TParm
     * @param conn TConnection
     * @return result TParm
     */
    public TParm onSaveOrderBy(TParm orderBy, String tableId, String tempCode,
                               TConnection conn) {
        TParm result = new TParm();
        if (tempCode == null || tableId == null || tempCode.trim().length() < 1 ||
            tableId.trim().length() < 1) {
            result.setErrCode( -1);
            //System.out.println("onSaveOrderBy is null " + result.getErrText());
            return result;
        }
        if (orderBy == null || orderBy.getCount() < 1) {
            result.setErrCode( -1);
//            System.out.println("onSaveOrderBy orderBy null " +
//                               result.getErrText());
            return result;
        }
        String delete = "DELETE SYS_VIEW_ORDERBY WHERE TEMP_CODE='" + tempCode +
            "' AND TABLE_ID='" + tableId + "'";
        result = new TParm(TJDODBTool.getInstance().update(delete));
        if (result == null || result.getErrCode() < 0) {
            //System.out.println("onSaveOrderBy " + result.getErrText());
            return result;
        }
        String now = StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                          "yyyyMMddHHmmss");
        int count = orderBy.getCount();
        String insert = "INSERT INTO SYS_VIEW_ORDERBY (TEMP_CODE,TABLE_ID,COLUMN_ID,DIRECTION,SEQ,OPT_USER,OPT_DATE,OPT_TERM)" +
            " VALUES('#','#','#','#',#,'#',TO_DATE('#','YYYYMMDDHH24MISS'),'#')";
        for (int i = 0; i < count; i++) {
            String sql = insert;
            sql = sql.replaceFirst("#", orderBy.getValue("TEMP_CODE", i));
            sql = sql.replaceFirst("#", orderBy.getValue("TABLE_ID", i));
            sql = sql.replaceFirst("#", orderBy.getValue("COLUMN_ID", i));
            sql = sql.replaceFirst("#", orderBy.getValue("DIRECTION", i));
            sql = sql.replaceFirst("#", orderBy.getValue("SEQ", i));
            sql = sql.replaceFirst("#", orderBy.getValue("OPT_USER", i));
            sql = sql.replaceFirst("#", now);
            sql = sql.replaceFirst("#", orderBy.getValue("OPT_TERM", i));
            result = new TParm(TJDODBTool.getInstance().update(sql, conn));
            if (result == null || result.getErrCode() < 0) {
                //System.out.println("onSaveOrderBy " + result.getErrText());
                //System.out.println("onSaveOrderBy" + sql);
                return result;
            }
        }

        return result;
    }

    /**
     * 保存SYS_VIEW_ORDERBY，先按逐渐删除，再插入
     * @param template String
     * @param tableId String
     * @param tempCode TParm
     * @param conn TConnection
     * @return result TParm
     */
    public TParm onSaveGroupBy(TParm groupBy, String tableId, String tempCode,
                               TConnection conn) {
        TParm result = new TParm();
        if (tempCode == null || tableId == null || tempCode.trim().length() < 1 ||
            tableId.trim().length() < 1) {
            result.setErrCode( -1);
            //System.out.println("onSaveGroupBy is null " + result.getErrText());
            return result;
        }
        if (groupBy == null || groupBy.getCount() < 1) {
            result.setErrCode( -1);
//            System.out.println("onSaveGroupBy groupby null " +
//                               result.getErrText());
            return result;
        }
        String delete = "DELETE SYS_VIEW_GROUPBY WHERE TEMP_CODE='" + tempCode +
            "' AND TABLE_ID='" + tableId + "'";
        result = new TParm(TJDODBTool.getInstance().update(delete));
        if (result == null || result.getErrCode() < 0) {
            //System.out.println("onSaveGroupBy " + result.getErrText());
            return result;
        }
        String now = StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                          "yyyyMMddHHmmss");
        int count = groupBy.getCount();
        String insert = "INSERT INTO SYS_VIEW_GROUPBY (TEMP_CODE,TABLE_ID,COLUMN_ID,SEQ,OPT_USER,OPT_DATE,OPT_TERM)" +
            " VALUES('#','#','#',#,'#',TO_DATE('#','YYYYMMDDHH24MISS'),'#')";
        for (int i = 0; i < count; i++) {
            String sql = insert;
            sql = sql.replaceFirst("#", groupBy.getValue("TEMP_CODE", i));
            sql = sql.replaceFirst("#", groupBy.getValue("TABLE_ID", i));
            sql = sql.replaceFirst("#", groupBy.getValue("COLUMN_ID", i));
            sql = sql.replaceFirst("#", groupBy.getValue("SEQ", i));
            sql = sql.replaceFirst("#", groupBy.getValue("OPT_USER", i));
            sql = sql.replaceFirst("#", now);
            sql = sql.replaceFirst("#", groupBy.getValue("OPT_TERM", i));
            result = new TParm(TJDODBTool.getInstance().update(sql, conn));
            if (result == null || result.getErrCode() < 0) {
                //System.out.println("sql==============" + sql);
                //System.out.println("onSaveGroupBy " + result.getErrText());
                return result;
            }
        }

        return result;
    }

    /**
     * 保存SYS_VIEW_WHERE_DETAIL，先按逐渐删除，再插入
     * @param template String
     * @param tableId String
     * @param tempCode TParm
     * @param conn TConnection
     * @return result TParm
     */
    public TParm onSaveWhere(TParm where, String tableId, String tempCode,
                             TConnection conn) {
        TParm result = new TParm();
        if (tempCode == null || tableId == null || tempCode.trim().length() < 1 ||
            tableId.trim().length() < 1) {
            result.setErrCode( -1);
            //System.out.println("onSaveWhere is null " + result.getErrText());
            return result;
        }
        if (where == null || where.getCount() < 1) {
            result.setErrCode( -1);
//            System.out.println("onSaveWhere  where is null " +
//                               result.getErrText());
            return result;
        }
        String delete = "DELETE SYS_VIEW_WHERE_DETAIL WHERE TEMP_CODE='" +
            tempCode +
            "' AND TABLE_ID='" + tableId + "'";
        result = new TParm(TJDODBTool.getInstance().update(delete));
        if (result == null || result.getErrCode() < 0) {
            //System.out.println("onSaveWhere " + result.getErrText());
            return result;
        }
        String now = StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                          "yyyyMMddHHmmss");
        int count = where.getCount();
        String insert = "INSERT INTO SYS_VIEW_WHERE_DETAIL (TEMP_CODE,TABLE_ID,COLUMN_ID,SEQ,BRACKET_START," +
            "MARK,VALUE,BRACKET_END,LINK,EXTERNAL_FLG,OPT_USER,OPT_DATE,OPT_TERM,COLUMN_NAME,DATA_TYPE,NULL_FLG)" +
            " VALUES('#','#','#',#,'#','#','#','#','#','#','#',TO_DATE('#','YYYYMMDDHH24MISS'),'#','#','#','#')";
        for (int i = 0; i < count; i++) {
            String sql = insert;
            sql = sql.replaceFirst("#", where.getValue("TEMP_CODE", i));
            sql = sql.replaceFirst("#", where.getValue("TABLE_ID", i));
            sql = sql.replaceFirst("#", where.getValue("COLUMN_ID", i));
            sql = sql.replaceFirst("#", where.getValue("SEQ", i));
            sql = sql.replaceFirst("#", where.getValue("BRACKET_START", i));
            sql = sql.replaceFirst("#", where.getValue("MARK", i));
            sql = sql.replaceFirst("#", where.getValue("VALUE", i));
            sql = sql.replaceFirst("#", where.getValue("BRACKET_END", i));
            sql = sql.replaceFirst("#", where.getValue("LINK", i));
            sql = sql.replaceFirst("#", where.getValue("EXTERNAL_FLG", i));
            sql = sql.replaceFirst("#", where.getValue("OPT_USER", i));
            sql = sql.replaceFirst("#", now);
            sql = sql.replaceFirst("#", where.getValue("OPT_TERM", i));
            sql = sql.replaceFirst("#", where.getValue("COLUMN_NAME", i));
            sql = sql.replaceFirst("#", where.getValue("DATA_TYPE", i));
            sql = sql.replaceFirst("#", where.getValue("NULL_FLG", i));
            result = new TParm(TJDODBTool.getInstance().update(sql, conn));
            if (result == null || result.getErrCode() < 0) {
                //System.out.println("onSaveWhere " + result.getErrText());
                //System.out.println("on saveWhere.sql=" + sql);
                return result;
            }
        }

        return result;
    }

    /**
     * 根据用户名取得模板代码和模板名
     * @param ownerId
     * @return
     */
    public TParm getTempCodeComboData(String ownerId) {
        TParm result = new TParm();
        if (ownerId == null || ownerId.trim().length() < 1) {
            result.setErrCode( -1);
            return result;
        }
        String sql =
            "SELECT TEMP_CODE ID,TEMP_DESC  NAME FROM SYS_VIEW_TEMPLATE WHERE USER_ID='" +
            ownerId + "'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 根据给入的用户名查询模板数据
     * @param userId
     * @return
     */
    public TParm getTempCodeTableData(String userId) {
        TParm result = new TParm();
        if (userId == null || userId.trim().length() < 1) {
            result.setErrCode( -1);
            return result;
        }
        String sql =
            "SELECT   TEMP_CODE,TEMP_DESC,B.USER_NAME OWNER,TABLE_ID,C.USER_NAME," +
            "         TO_CHAR (A.OPT_DATE, 'YYYY/MM/DD HH24:MI') BUILDDATE," +
            "         A.OPT_TERM" +
            "  FROM   SYS_VIEW_TEMPLATE A, SYS_OPERATOR B,SYS_OPERATOR C" +
            " WHERE   A.USER_ID='" + userId + "'" +
            "         AND A.USER_ID = B.USER_ID" +
            "         AND A.OPT_USER = C.USER_ID" +
            " ORDER BY A.OPT_DATE";
        //System.out.println("sql============================" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 根据给入的用户名查询模板数据
     * @param userId
     * @return
     */
    public TParm getTempCodeTableDataByTempCode(String userId, String tempCode) {
        TParm result = new TParm();
        if (userId == null || userId.trim().length() < 1 || tempCode == null ||
            tempCode.trim().length() < 1) {
            result.setErrCode( -1);
            return result;
        }
        String sql =
            "SELECT   TEMP_CODE,TEMP_DESC,B.USER_NAME OWNER,TABLE_ID,C.USER_NAME," +
            "         TO_CHAR (A.OPT_DATE, 'YYYY/MM/DD HH24:MI') BUILDDATE," +
            "         A.OPT_TERM" +
            "  FROM   SYS_VIEW_TEMPLATE A, SYS_OPERATOR B,SYS_OPERATOR C" +
            " WHERE    A.USER_ID='" + userId + "'" +
            "		  AND A.TEMP_CODE='" + tempCode + "'" +
            "         AND A.USER_ID = B.USER_ID" +
            "         AND A.OPT_USER = C.USER_ID";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 根据给入的主键取得查询条件
     * @param tempCode
     * @param tableId
     * @return
     */
    public TParm getWhereTable(String tempCode, String tableId) {
        TParm result = new TParm();
        if (tempCode == null || tableId == null || tempCode.trim().length() < 1 ||
            tableId.trim().length() < 1) {
            result.setErrCode( -1);
            return result;
        }
        //modify   
        String sql =
            "SELECT COLUMN_ID,COLUMN_NAME,VALUE ,SEQ" +
            "	FROM SYS_VIEW_WHERE_DETAIL " +
            "	WHERE TEMP_CODE='" + tempCode + "' " +
            "		  AND TABLE_ID='" + tableId + "'" +
            "		  AND EXTERNAL_FLG='Y'" +
            "   ORDER BY SEQ ";
        //System.out.println("getWhereTable.sql=" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 为查询数据TABLE初始化列信息
     * @param tempCode
     * @return
     */
    public TParm getColumnInfo(String tempCode) {
        TParm result = new TParm();
        if (tempCode == null || tempCode.trim().length() < 1) {
            result.setErrCode( -1);
            return result;
        }
  

        String sql = "SELECT * FROM SYS_VIEW_COLUMN WHERE TEMP_CODE='" +
            tempCode + "'" +
            " ORDER BY SEQ";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 根据给入的column的TParm,得到header
     * @param column
     * @return
     */
    public String getColumnHeader(TParm column) {
        String header = "";
        if (column == null || column.getCount() < 1) {
            return header;
        }
        int count = column.getCount();
        for (int i = 0; i < count; i++) {
            header = header + column.getValue("COLUMN_NAME", i) + ",100;";
        }
        header = header.substring(0, header.lastIndexOf(";"));
        return header;
    }

    /**
     * 根据给入的column的TParm，得到ParmMap
     * @param column
     * @return
     */
    public String getColumnParmMap(TParm column) {
        String parmMap = "";
        if (column == null || column.getCount() < 1) {
            return parmMap;
        }
        int count = column.getCount();
        for (int i = 0; i < count; i++) {
            parmMap = parmMap + column.getValue("COLUMN_ID", i) + ";";
        }
        parmMap = parmMap.substring(0, parmMap.lastIndexOf(";"));
//		parmMap=parmMap.r
        return parmMap;
    }

    /**
     * 根据给入的SQL查询
     * @param sql String
     * @return result TParm
     */
    public TParm queryBySql(String sql) {
        TParm result = new TParm();
        if (sql == null || sql.trim().length() < 1) {
            result.setErrCode( -1);
            return result;
        }
        result = new TParm(TJDODBTool.getInstance().select(sql));

        return result;
    }

    /**
     * 根据给入的TEMP_CODE和外部WHERE的值拼装SQL，根据TEMP_CODE查询SYS_VIEW_COLUMN,SYS_VIEW_ORDERBY,SYS_VIEW_GROUPBY,SYS_VIEW_WHERE_DETAIL,再根据COLUMN_ID将外部传入值拼到SQL里
     * @param tempCode
     * @param whereValue
     * @return
     */
    public String buildSql(String tempCode, TParm whereValue, String tableName) {
        String sql = ""; 
        if (tempCode == null || tempCode.trim().length() < 1) {
            return sql;
        }

        TParm select = getSelect(tempCode);
        if (select == null || select.getErrCode() < 0) {
            //System.out.println("select.err=" + select.getErrText());
            return sql;
        }
        TParm orderBy = getOrderBy(tempCode);
        if (orderBy == null || orderBy.getErrCode() < 0) {
            //System.out.println("orderBy.err=" + orderBy.getErrText());
            return sql;
        }
        TParm groupby = getGroupBy(tempCode);
        if (groupby == null || groupby.getErrCode() < 0) {
            //System.out.println("groupby.err=" + groupby.getErrText());
            return sql;
        }
        TParm where = getWhere(tempCode);
        if (where == null || where.getErrCode() < 0) {
            //System.out.println("where.err=" + where.getErrText());
            return sql;
        }

        TParm template = getTemplate(tempCode);
        if (template == null || template.getErrCode() < 0) {
            //System.out.println("template.err=" + template.getErrText());
            return sql;
        }
        boolean isTop = template.getBoolean("IS_TOP", 0);
        String topNo = template.getValue("TOP_NO", 0);
        //循环WHERE，找到外部FLAG为Y的，再根据COLUMN_ID，将外部传入值设到WHERE中的VALUE里
        int count = where.getCount("COLUMN_ID");
        Vector columnVct = whereValue.getVectorValue("COLUMN_NAME");
        //System.out.println("columNVct=========" + columnVct);
//        System.out.println("columnVct.0 is " +
//                           columnVct.get(0).getClass().getName());
        if (count < 1) {
            sql = this.buildSql(select, groupby, orderBy, where, tableName,
                                isTop, topNo);
            return sql;
        }
        //System.out.println("whereValue="+whereValue);
        //System.out.println("where======"+where);
        for (int i = 0; i < count; i++) {
            if (where.getBoolean("EXTERNAL_FLG", i)) {
            //modify 	COLUMN_NAME->COLUMN_ID
            	String columnName=where.getValue("COLUMN_NAME",i);
            	int row=columnVct.indexOf(columnName);
                String value = whereValue.getValue("VALUE", row);
                //System.out.println("value of " + i + " is:" + value);
                where.setData("VALUE", i, value);
            }

        }
        //System.out.println("where=-=============" + where);
        sql = this.buildSql(select, groupby, orderBy, where, tableName, isTop,
                            topNo);
        return sql;
    }

    /**
     * 根据给入的TEMP_CODE查询SYS_VIEW_COLUMN
     * @param tempCode
     * @return
     */
    public TParm getSelect(String tempCode) {
        TParm result = new TParm();
        if (tempCode == null || tempCode.trim().length() < 1) {
            result.setErrCode( -1);
            return result;
        }
        String sql =
            "SELECT * " +
            "	FROM SYS_VIEW_COLUMN " +
            "	WHERE TEMP_CODE='" + tempCode + "'" +
            "	ORDER BY SEQ";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 根据给入的TEMP_CODE查询SYS_VIEW_ORDERBY
     * @param tempCode
     * @return
     */
    public TParm getOrderBy(String tempCode) {
        TParm result = new TParm();
        if (tempCode == null || tempCode.trim().length() < 1) {
            result.setErrCode( -1);
            return result;
        }
        String sql =
            "SELECT * " +
            "	FROM SYS_VIEW_ORDERBY " +
            "	WHERE TEMP_CODE='" + tempCode + "'" +
            "	ORDER BY SEQ";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 根据给入的TEMP_CODE查询SYS_VIEW_GROUPBY

     * @param tempCode
     * @return
     */
    public TParm getGroupBy(String tempCode) {
        TParm result = new TParm();
        if (tempCode == null || tempCode.trim().length() < 1) {
            result.setErrCode( -1);
            return result;
        }
        String sql =
            "SELECT * " +
            "	FROM SYS_VIEW_GROUPBY " +
            "	WHERE TEMP_CODE='" + tempCode + "'" +
            "	ORDER BY SEQ";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 根据给入的TEMP_CODE查询SYS_VIEW_GROUPBY

     * @param tempCode
     * @return
     */
    public TParm getWhere(String tempCode) {
        TParm result = new TParm();
        if (tempCode == null || tempCode.trim().length() < 1) {
            result.setErrCode( -1);
            return result;
        }
        String sql =
            "SELECT * " +
            "	FROM SYS_VIEW_WHERE_DETAIL " +
            "	WHERE TEMP_CODE='" + tempCode + "'" +
            "	ORDER BY SEQ";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 根据给入的TEMP_CODE查询SYS_VIEW_TEMPLATE
     * @param tempCode
     * @param tableId
     * @return
     */
    public TParm getTemplate(String tempCode) {
        TParm result = new TParm();
        if (tempCode == null || tempCode.trim().length() < 1) {
            result.setErrCode( -1);
            return result;
        }
        String sql = "SELECT * FROM SYS_VIEW_TEMPLATE WHERE TEMP_CODE='" +
            tempCode + "'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 根据给入的TEMP_CODE删除所有的表
     * @param tempCode String
     * @return result TParm
     */
    public TParm onDelete(String tempCode, TConnection conn) {
        TParm result = new TParm();
        if (tempCode == null || tempCode.trim().length() < 1) {
            result.setErrCode( -1);
            //System.out.println("onDelete.tempCode is null");
            return result;
        }
        result = deleteTable(tempCode, "SYS_VIEW_TEMPLATE", conn);
        if (result == null || result.getErrCode() < 1) {
            return result;
        }
        result = deleteTable(tempCode, "SYS_VIEW_COLUMN", conn);
        if (result == null || result.getErrCode() < 1) {
            return result;
        }
        result = deleteTable(tempCode, "SYS_VIEW_ORDERBY", conn);
        if (result == null || result.getErrCode() < 1) {
            return result;
        }
        result = deleteTable(tempCode, "SYS_VIEW_GROUPBY", conn);
        if (result == null || result.getErrCode() < 1) {
            return result;
        }
        result = deleteTable(tempCode, "SYS_VIEW_WHERE_DETAIL", conn);
        if (result == null || result.getErrCode() < 1) {
            return result;
        }
        return result;
    }

    /**
     * 根据给入的TEMP_CODE删除SYS_VIEW_COLUMN
     * @param tempCode String
     * @return result TParm
     */
    public TParm deleteTable(String tempCode, String tableName,
                             TConnection conn) {
        TParm result = new TParm();
        if (tempCode == null || tempCode.trim().length() < 1 || tableName == null ||
            tableName.trim().length() < 1) {
            result.setErrCode( -1);
            //System.out.println("deleteTable.tempCode is null");
            return result;
        }
        String delete = "DELETE " + tableName + " WHERE TEMP_CODE='" + tempCode +
            "'";
        result = new TParm(TJDODBTool.getInstance().update(delete, conn));
        if (result == null || result.getErrCode() < 0) {
            //System.out.println("deleteTable.delete=" + delete);
        }
        return result;
    }

    /**
     * 各科抗菌素用量统计取得各科室抗菌素总金额
     * @param deptDesc
     * @param startDate
     * @param endDate
     * @return
     */
    public TParm getAntiByDept(String deptDesc, String startDate,
                               String endDate) {
        TParm result = new TParm();
        if (deptDesc == null || deptDesc.trim().length() < 1 || startDate == null ||
            startDate.trim().length() < 1 || endDate == null ||
            endDate.trim().length() < 1) {
            result.setErrCode( -1);
            return result;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date sDate = sdf.parse(startDate);
            if (!StringTool.getString(sDate,
                                      "yyyyMMddHHmmss").equalsIgnoreCase(
                startDate)) {
                result.setErrCode( -1);
                return result;
            }
            Date eDate = sdf.parse(endDate);
            if (!StringTool.getString(eDate,
                                      "yyyyMMddHHmmss").equalsIgnoreCase(
                endDate)) {
                result.setErrCode( -1);
                return result;
            }
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result.setErrCode( -1);
            return result;
        }
        String sql =
            "SELECT SUM(TOT_AMT) ANTISUM FROM UDD_SHEET WHERE DEPTDESC='" +
            deptDesc + "' AND ORDERDATE>=TO_DATE('" + startDate +
            "','YYYYMMDDHH24MISS') AND ORDERDATE<=TO_DATE('" + endDate +
            "','YYYYMMDDHH24MISS') AND ANTIBIOTIC_CODE IS NOT NULL";
        result = new TParm(TJDODBTool.getInstance().select(sql));

        return result;
    }

    /**
     * 各类药品销售额占总销售额的比例
     * @param startDate
     * @param endDate
     * @return
     */
    public TParm getTotAmtByDate(String startDate, String endDate) {
        TParm result = new TParm();
        if (startDate == null || startDate.trim().length() < 1 || endDate == null ||
            endDate.trim().length() < 1) {
            result.setErrCode( -1);
            return result;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date sDate = sdf.parse(startDate);
            if (!StringTool.getString(sDate,
                                      "yyyyMMddHHmmss").equalsIgnoreCase(
                startDate)) {
                result.setErrCode( -1);
                return result;
            }
            Date eDate = sdf.parse(endDate);
            if (!StringTool.getString(eDate,
                                      "yyyyMMddHHmmss").equalsIgnoreCase(
                endDate)) {
                result.setErrCode( -1);
                return result;
            }
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result.setErrCode( -1);
            return result;
        }
        String sql =
            "SELECT   SUM (TOTAMT) ALLAMT" +
            "  FROM   UDD_TOTAMT" +
            "  WHERE   BILLDATE >= TO_DATE ('" + startDate +
            "', 'YYYYMMDDHH24MISS')" +
            "         AND BILLDATE <= TO_DATE ('" + endDate +
            "', 'YYYYMMDDHH24MISS')";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 每种药品销售额/同类销售额
     * @param startDate
     * @param endDate
     * @return
     */
    public TParm getPhaClassifyTotAmt(String startDate, String endDate) {
        TParm result = new TParm();
        if (startDate == null || startDate.trim().length() < 1 || endDate == null ||
            endDate.trim().length() < 1) {
            result.setErrCode( -1);
            return result;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date sDate = sdf.parse(startDate);
            if (!StringTool.getString(sDate,
                                      "yyyyMMddHHmmss").equalsIgnoreCase(
                startDate)) {
                result.setErrCode( -1);
                return result;
            }
            Date eDate = sdf.parse(endDate);
            if (!StringTool.getString(eDate,
                                      "yyyyMMddHHmmss").equalsIgnoreCase(
                endDate)) {
                result.setErrCode( -1);
                return result;
            }
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result.setErrCode( -1);
            return result;
        }
        String sql = "SELECT CATCODE,SUM(TOTAMT) TOTAMT FROM UDD_SHEET_ORDERCATEGORY WHERE ORDERDATE>=TO_DATE('" +
            startDate +
            "','YYYYMMDDHH24MISS') AND ORDERDATE <=TO_DATE('" + endDate +
            "','YYYYMMDDHH24MISS') GROUP BY CATCODE ORDER BY TOTAMT";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
}
