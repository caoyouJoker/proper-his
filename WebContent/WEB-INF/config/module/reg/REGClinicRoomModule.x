# 
#  Title:诊室module
# 
#  Description:诊室module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=queryTree;selectdata;deletedata;insertdata;updatedata;existsClinicRoom;initclinicroomno;initclinicroomreg;getClinicRoomForAdmType;&
		getAreaByRoom;getOrgCodeByRoomNo;getOrgByODO;selActiveFlg;getRoomNo

//根据院区代码，就诊日期，就诊时段，门急诊，真正医师查询诊断对应的药房
getOrgByODO.Type=TSQL
getOrgByODO.SQL=SELECT A.ORG_CODE &
		  FROM REG_CLINICROOM A,REG_SCHDAY B &
		 WHERE B.REGION_CODE=<REGION_CODE> &
		   AND B.ADM_DATE=<ADM_DATE> &
		   AND B.SESSION_CODE=<SESSION_CODE> &
		   AND B.ADM_TYPE=<ADM_TYPE> &
		   AND B.REALDR_CODE=<REALDR_CODE> &
		   AND B.CLINICROOM_NO=A.CLINICROOM_NO
getOrgByODO.Debug=N

//查询诊间
queryTree.Type=TSQL
queryTree.SQL=SELECT CLINICROOM_NO,CLINICROOM_DESC,PY1,PY2,SEQ,&
		     DESCRIPTION,ADM_TYPE,REGION_CODE,CLINICAREA_CODE,LOCATION,&
		     ORG_CODE,ACTIVE_FLG,OPT_USER,OPT_DATE,OPT_TERM &
		FROM REG_CLINICROOM &
	       WHERE CLINICROOM_NO=<CLINICROOM_NO> &
	    ORDER BY CLINICROOM_NO
queryTree.Debug=N

//查询诊间号,诊间号说明,拼音码,注记码,顺序编号,备注,门急别,子院区代码,诊区号,诊间位置,预设药房,启用标记,操作人员,操作日期,操作终端
selectdata.Type=TSQL
selectdata.SQL=SELECT CLINICROOM_NO,CLINICROOM_DESC,PY1,PY2,SEQ,&
		      DESCRIPTION,ADM_TYPE,REGION_CODE,CLINICAREA_CODE,LOCATION,&
		      ORG_CODE,ACTIVE_FLG,PHA_REGION_CODE,OPT_USER,OPT_DATE,OPT_TERM &
		 FROM REG_CLINICROOM &
	     ORDER BY CLINICROOM_NO
selectdata.item=CLINICROOM_NO;CLINICAREA_CODE
selectdata.CLINICROOM_NO=CLINICROOM_NO=<CLINICROOM_NO>
selectdata.CLINICAREA_CODE=CLINICAREA_CODE=<CLINICAREA_CODE>
selectdata.Debug=N

//删除诊间号,诊间号说明,拼音码,注记码,顺序编号,备注,门急别,子院区代码,诊区号,诊间位置,预设药房,启用标记,操作人员,操作日期,操作终端
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_CLINICROOM &
		     WHERE CLINICROOM_NO=<CLINICROOM_NO>
deletedata.Debug=N

//新增诊间号,诊间号说明,拼音码,注记码,顺序编号,备注,门急别,子院区代码,诊区号,诊间位置,预设药房,启用标记,操作人员,操作日期,操作终端
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_CLINICROOM &
			   (CLINICROOM_NO,CLINICROOM_DESC,PY1,PY2,SEQ,&
			   DESCRIPTION,ADM_TYPE,REGION_CODE,CLINICAREA_CODE,LOCATION,&
			   ORG_CODE,ACTIVE_FLG,OPT_USER,OPT_DATE,OPT_TERM) &
		    VALUES (<CLINICROOM_NO>,<CLINICROOM_DESC>,<PY1>,<PY2>,<SEQ>,&
			   <DESCRIPTION>,<ADM_TYPE>,<REGION_CODE>,<CLINICAREA_CODE>,<LOCATION>,&
			   <ORG_CODE>,<ACTIVE_FLG>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N            

//更新诊间号,诊间号说明,拼音码,注记码,顺序编号,备注,门急别,子院区代码,诊区号,诊间位置,预设药房,启用标记,操作人员,操作日期,操作终端
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_CLINICROOM &
		  SET CLINICROOM_NO=<CLINICROOM_NO>,CLINICROOM_DESC=<CLINICROOM_DESC>,PY1=<PY1>,&
		      PY2=<PY2>,SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,&
		      ADM_TYPE=<ADM_TYPE>,REGION_CODE=<REGION_CODE>,CLINICAREA_CODE=<CLINICAREA_CODE>,&
		      LOCATION=<LOCATION>,ORG_CODE=<ORG_CODE>,ACTIVE_FLG=<ACTIVE_FLG>,&
		      OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		WHERE CLINICROOM_NO=<CLINICROOM_NO>						
updatedata.Debug=N

//是否存在诊间
existsClinicRoom.type=TSQL
existsClinicRoom.SQL=SELECT COUNT(*) AS COUNT &
		       FROM REG_CLINICROOM &
		      WHERE CLINICROOM_NO=<CLINICROOM_NO>

//取得诊室combo信息
initclinicroomno.Type=TSQL
initclinicroomno.SQL=SELECT CLINICROOM_NO AS ID,CLINICROOM_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 &
		       FROM REG_CLINICROOM &
		      WHERE ACTIVE_FLG = 'Y' &
		   ORDER BY CLINICROOM_NO,SEQ
initclinicroomno.item=CLINICAREA_CODE;REGION_CODE_ALL
initclinicroomno.CLINICAREA_CODE=CLINICAREA_CODE=<CLINICAREA_CODE>
initclinicroomno.REGION_CODE_ALL=REGION_CODE=<REGION_CODE_ALL>
initclinicroomno.Debug=N

//得到诊室(主界面)
initclinicroomreg.Type=TSQL
initclinicroomreg.SQL=SELECT DISTINCT A.CLINICROOM_NO AS ID,B.CLINICROOM_DESC AS NAME,B.ENG_DESC AS ENNAME,B.PY1 AS PY1, B.PY2 AS PY2 &
			FROM REG_SCHDAY A,REG_CLINICROOM B &
		       WHERE B.ACTIVE_FLG = 'Y' &
		         AND A.CLINICROOM_NO = B.CLINICROOM_NO &
		    ORDER BY A.CLINICROOM_NO
initclinicroomreg.item=REGION_CODE;ADM_TYPE;SESSION_CODE;ADM_DATE;REGION_CODE_ALL
initclinicroomreg.REGION_CODE=A.REGION_CODE=<REGION_CODE>
initclinicroomreg.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
initclinicroomreg.SESSION_CODE=A.SESSION_CODE=<SESSION_CODE>
initclinicroomreg.ADM_DATE=A.ADM_DATE=<ADM_DATE>
initclinicroomreg.REGION_CODE_ALL=B.REGION_CODE=<REGION_CODE_ALL>
initclinicroomreg.Debug=N

//得到诊区，诊室（For ONW）
getClinicRoomForAdmType.Type=TSQL
getClinicRoomForAdmType.SQL=SELECT CLINICAREA_CODE,CLINICROOM_NO,REGION_CODE &
			      FROM REG_CLINICROOM &
			     WHERE ACTIVE_FLG = 'Y' &
			       AND ADM_TYPE=<ADM_TYPE>
getClinicRoomForAdmType.item=CLINICAREA_CODE;REGION_CODE
getClinicRoomForAdmType.CLINICAREA_CODE=CLINICAREA_CODE=<CLINICAREA_CODE>
getClinicRoomForAdmType.REGION_CODE=REGION_CODE=<REGION_CODE>
getClinicRoomForAdmType.Debug=N

//根据诊室得到诊区（For REG）
getAreaByRoom.Type=TSQL
getAreaByRoom.SQL=SELECT CLINICAREA_CODE &
		    FROM REG_CLINICROOM &
		   WHERE CLINICROOM_NO=<CLINICROOM_NO>
getAreaByRoom.Debug=N

//根据诊室得到药房（For OPD）
getOrgCodeByRoomNo.Type=TSQL
getOrgCodeByRoomNo.SQL=SELECT ORG_CODE &
			 FROM REG_CLINICROOM &
			WHERE CLINICROOM_NO=<CLINICROOM_NO>
getOrgCodeByRoomNo.Debug=N

//诊室是否启用
selActiveFlg.Type=TSQL
selActiveFlg.SQL=SELECT ACTIVE_FLG &
		   FROM REG_CLINICROOM &
		  WHERE CLINICROOM_NO = <CLINICROOM_NO>
selActiveFlg.Debug=N
//得到诊室号码
getRoomNo.Type=TSQL
getRoomNo.SQL=SELECT CLINICROOM_NO FROM REG_SCHDAY &
					WHERE REGION_CODE=<REGION_CODE> &
				 	  AND ADM_TYPE=<ADM_TYPE> &
				 	  AND ADM_DATE=<ADM_DATE> &
				 	  AND SESSION_CODE=<SESSION_CODE> &
				 	  AND DEPT_CODE=<DEPT_CODE> &
				 	  AND DR_CODE=<DR_CODE> 
getRoomNo.Debug=N
