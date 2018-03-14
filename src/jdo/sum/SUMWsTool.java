// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SUMWsTool.java

package jdo.sum;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;
@WebService
public interface SUMWsTool
{

	public abstract String login(String s, String s1);

	public abstract String saveSUM_Data(String INHOSPITALDAYS, String OPE_DAYS, String MR_NO, String IPD_NO, String CASE_NO, String STOOL, String AUTO_STOOL, 
			String ENEMA, String DRAINAGE, String HEIGHT, String WEIGHT, String OUTPUTURINEQTY, String INTAKEFLUIDQTY, String PHYSIATRICS, 
			String RECTIME, String SPCCONDCODE, String TMPTRKINDCODE, List<String> list, String NOTPRREASONCODE, String PTMOVECATECODE, String PTMOVECATEDESC, 
			String EXAMINE_DATE);
}
