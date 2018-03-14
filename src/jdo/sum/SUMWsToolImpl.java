// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SUMWsToolImpl.java

package jdo.sum;

import com.dongyang.Service.Server;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

// Referenced classes of package jdo.sum:
//			SUMWsTool
@WebService
public class SUMWsToolImpl
	implements SUMWsTool
{

	public SUMWsToolImpl()
	{
	}
	public String login(String userID, String password)
	{
		String enPass = encrypt(password);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String dstr = format.format(new Date());
		String sql = (new StringBuilder("SELECT count(1) AS COUNT FROM SYS_OPERATOR WHERE USER_ID = '")).append(userID).append("' ").append("AND USER_PASSWORD='").append(enPass).append("' ").append("AND END_DATE>TO_DATE('").append(dstr).append("','yy-mm-dd hh24:mi:ss')").toString();
		Server.autoInit(this);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		int count = Integer.parseInt(result.getValue("COUNT", 0));
		if (count == 0)
			return "1";
		else
			return "0";
	}
	public String saveSUM_Data(String INHOSPITALDAYS, String OPE_DAYS, String MR_NO, String IPD_NO, String CASE_NO, String STOOL, String AUTO_STOOL, 
			String ENEMA, String DRAINAGE, String HEIGHT, String WEIGHT, String OUTPUTURINEQTY, String INTAKEFLUIDQTY, String PHYSIATRICS, 
			String RECTIME, String SPCCONDCODE, String TMPTRKINDCODE, List<String> list, String NOTPRREASONCODE, String PTMOVECATECODE, String PTMOVECATEDESC, 
			String EXAMINE_DATE)
	{	
		
		String allString="0";
		
		StringBuffer sbBuffer=new StringBuffer();
		sbBuffer.append("SELECT COUNT(DISPOSAL_FLG) AS COUNT,DISPOSAL_FLG  FROM SUM_VITALSIGN WHERE  ADM_TYPE='I' AND CASE_NO ='"+CASE_NO+"'");
		sbBuffer.append("AND EXAMINE_DATE =").append(castString(EXAMINE_DATE));
		sbBuffer.append(" GROUP BY DISPOSAL_FLG");
		
		TParm my = new TParm(TJDODBTool.getInstance().select(sbBuffer.toString()));
		if (my.getCount()>0) {
				StringBuffer sb=new StringBuffer();
				sb.append("UPDATE SUM_VITALSIGN  SET ");
				sb.append("HEIGHT=").append(HEIGHT).append(",");
				sb.append("WEIGHT=").append(castString(WEIGHT)).append(",");
				sb.append("OUTPUTURINEQTY=").append(castString(OUTPUTURINEQTY)).append(",");
				sb.append("STOOL=").append(castString(STOOL)).append(",");
				sb.append("INTAKEFLUIDQTY=").append(castString(INTAKEFLUIDQTY)).append(",");
				sb.append("AUTO_STOOL=").append(castString(AUTO_STOOL)).append(",");
				sb.append("ENEMA=").append(castString(ENEMA)).append(",");
				sb.append("OPE_DAYS=").append(castString(OPE_DAYS)).append(",");
				sb.append("DRAINAGE=").append(castString(DRAINAGE));
				sb.append(" WHERE ADM_TYPE=").append("'I' ");
				sb.append(" and CASE_NO=").append(castString(CASE_NO));
				sb.append(" and EXAMINE_DATE=").append(castString(EXAMINE_DATE));
				TParm result = new TParm(TJDODBTool.getInstance().update(sb.toString()));
				
				if (result.getErrCode() < 0)
					 allString="3";
				
				for (int i = 0; i < list.size(); i++) {
					String c=list.get(i);
					String[] strings= c.split("::");
					
					StringBuffer sb2=new StringBuffer();
					sb2.append("UPDATE SUM_VTSNTPRDTL SET ");
					sb2.append("PHYSIATRICS=").append(castString(PHYSIATRICS)).append(",");
					sb2.append("RECTIME=").append(castString(RECTIME)).append(",");
					sb2.append("SPCCONDCODE=").append(castString(SPCCONDCODE)).append(",");
					sb2.append("TMPTRKINDCODE=").append(castString(TMPTRKINDCODE)).append(",");
					sb2.append("TEMPERATURE=").append(castString(strings[1])).append(",");
					sb2.append("PLUSE=").append(castString(strings[2])).append(",");
					sb2.append("RESPIRE=").append(castString(strings[3])).append(",");
					sb2.append("SYSTOLICPRESSURE=").append(castString(strings[4])).append(",");
					sb2.append("DIASTOLICPRESSURE=").append(castString(strings[5])).append(",");
					sb2.append("HEART_RATE=").append(castString(strings[6])).append(",");
					sb2.append("NOTPRREASONCODE=").append(castString(NOTPRREASONCODE)).append(",");
					sb2.append("PTMOVECATECODE=").append(castString(PTMOVECATECODE)).append(",");
					sb2.append("PTMOVECATEDESC=").append(castString(PTMOVECATEDESC));
					
					sb2.append(" WHERE ADM_TYPE=").append("'I' ");
					sb2.append(" and CASE_NO=").append(castString(CASE_NO));
					sb2.append(" and EXAMINE_DATE=").append(castString(EXAMINE_DATE));
					sb2.append(" and EXAMINESESSION=").append(castString(strings[0]));
					
					TParm re = new TParm(TJDODBTool.getInstance().update(sb2.toString()));
					
					if (re.getErrCode() < 0)
						 allString="4";
					
				}
			
			
		}else {
				SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				String nowString = df.format(new Date());
				StringBuffer strBuf = new StringBuffer();
				strBuf.append("INSERT INTO SUM_VITALSIGN (ADM_TYPE,CASE_NO,EXAMINE_DATE,IPD_NO, MR_NO,INHOSPITALDAYS,OPE_DAYS,HEIGHT,WEIGHT,");
				strBuf.append("OUTPUTURINEQTY,STOOL,INTAKEFLUIDQTY,USER_ID, ");
				strBuf.append("OPT_USER,OPT_DATE,OPT_TERM,");
				strBuf.append("AUTO_STOOL,ENEMA,DRAINAGE)");
				strBuf.append((new StringBuilder("VALUES('I',")).append(castString(CASE_NO)).append(",").append(castString(EXAMINE_DATE)).append(",").append(castString(IPD_NO)).append(",").append(castString(MR_NO)).toString());
				strBuf.append((new StringBuilder(",")).append(castString(INHOSPITALDAYS)).append(",").append(castString(OPE_DAYS)).append(",").append(castString(HEIGHT)).append(",").append(castString(WEIGHT)).toString());
				strBuf.append((new StringBuilder(",")).append(castString(OUTPUTURINEQTY)).append(",").append(castString(STOOL)).append(",").append(castString(INTAKEFLUIDQTY)).append(",").append(castString("webservice")).toString());
				strBuf.append((new StringBuilder(",")).append(castString("webservice")).append(",TO_DATE('").append(nowString).append("', 'YYYY/MM/DD HH24:MI:SS'),").append(castString("1.1.1.1")).append(",").append(castString(AUTO_STOOL)).toString());
				strBuf.append((new StringBuilder(",")).append(castString(ENEMA)).append(",").append(castString(DRAINAGE)).append(")").toString());
				Server.autoInit(this);
				TParm result = new TParm(TJDODBTool.getInstance().update(strBuf.toString()));
				
				
				for (int i = 0; i < list.size(); i++) {
					String c=list.get(i);
					String[] strings= c.split("::");
					StringBuffer buffer=new StringBuffer();
					
					buffer.append("INSERT INTO SUM_VTSNTPRDTL ( ADM_TYPE,CASE_NO,EXAMINE_DATE,EXAMINESESSION,PHYSIATRICS, RECTIME,SPCCONDCODE,TMPTRKINDCODE,TEMPERATURE,PLUSE, RESPIRE") ;
					
					buffer.append(",SYSTOLICPRESSURE,DIASTOLICPRESSURE,NOTPRREASONCODE,PTMOVECATECODE, PTMOVECATEDESC,USER_ID,OPT_USER,OPT_DATE,OPT_TERM,HEART_RATE )");
					
					buffer.append("VALUES ('I',").append(castString(CASE_NO)).append(",").append(castString(EXAMINE_DATE)).append(",");
					
					buffer.append(castString(strings[0])).append(",").append(castString(PHYSIATRICS)).append(",").append(castString(RECTIME)).append(",");
					
					buffer.append(castString(SPCCONDCODE)).append(",").append(castString(TMPTRKINDCODE)).append(",").append(castString(strings[1])).append(",");
					
					buffer.append(castString(strings[2])).append(",").append(castString(strings[3])).append(",").append(castString(strings[4])).append(",");
					
					buffer.append(castString(strings[5])).append(",").append(castString(NOTPRREASONCODE)).append(",").append(castString(PTMOVECATECODE)).append(",");
					
					buffer.append(castString(PTMOVECATEDESC)).append(",");
					
					buffer.append(castString("webservice")).append(",").append(castString("webservice")).append(",").append("TO_DATE('").append(nowString).append("', 'YYYY/MM/DD HH24:MI:SS'),").append(castString("1.1.1.1")).append(",");
					
					buffer.append(castString(strings[6])+")");
					
					TParm cc = new TParm(TJDODBTool.getInstance().update(buffer.toString()));
					
					if (cc.getErrCode() < 0)
						 allString="2";
					
				}
				
				if (result.getErrCode() < 0)
					allString="1";
		}
		return returnSaveMsg(Integer.parseInt(allString));
	}

	private String returnSaveMsg(int status)
	{
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("<SUM_SAVE_RESULT>");
		strBuf.append("<STATUS>");
		strBuf.append(String.valueOf(status));
		strBuf.append("</STATUS>");
		strBuf.append("</SUM_SAVE_RESULT>");
		return strBuf.toString();
	}

	private String castString(String s)
	{
		return (new StringBuilder("'")).append(s).append("'").toString();
	}

	private String encrypt(String text)
	{
		String av_str = "";
		try
		{
			byte aa[] = text.getBytes("UTF-16BE");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < aa.length; i++)
			{
				aa[i] = (byte)(~aa[i]);
				sb.append(Integer.toHexString(aa[i]).substring(6));
			}

			av_str = sb.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return av_str;
	}
}
