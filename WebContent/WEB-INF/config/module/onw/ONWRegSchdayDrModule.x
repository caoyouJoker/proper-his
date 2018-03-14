######################################################
# <p>Title:普通诊出诊医生维护Module </p>
#
# <p>Description:普通诊出诊医生维护Module </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:javahis </p>
#
# @author zhangk
# @version 4.0
#
######################################################
Module.item=selectdate;insertdata;update;deletedata;selectall;updateAll

//查询诊间，诊室，科室，操作人员，操作日期，顺序编号，拼音编码，注记符，备注
selectall.Type=TSQL
selectall.SQL=SELECT REGION,ADM_TYPE,SESSION_CODE,CLINIC_AREA,CLINICROOM_NO,DEPT_CODE,DR_CODE,CLINICTYPE_CODE,OPT_USER,OPT_DATE,OPT_TERM &
	      FROM REG_SCHDAY_DR &
	      ORDER BY CLINIC_AREA,CLINICROOM_NO,SESSION_CODE
selectall.item=ADM_TYPE;SESSION_CODE;CLINIC_AREA;CLINICROOM_NO
selectall.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
selectall.SESSION_CODE=SESSION_CODE=<SESSION_CODE>
selectall.CLINIC_AREA=CLINIC_AREA=<CLINIC_AREA>
selectall.CLINICROOM_NO=CLINICROOM_NO=<CLINICROOM_NO>
selectall.Debug=N

//查询普通诊医生班表
selectdate.Type=TSQL
selectdate.SQL=SELECT adm_type,session_code,clinic_area,clinicroom_no FROM reg_schday_dr
selectdate.Debug=N

//修改普通诊医生班表
update.Type=TSQL
update.SQL=UPDATE REG_SCHDAY_DR SET DEPT_CODE=<DEPT_CODE>,CLINICTYPE_CODE=<CLINICTYPE_CODE>,DR_CODE =<DR_CODE> , OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE ,OPT_TERM=<OPT_TERM> WHERE ADM_TYPE=<ADM_TYPE> AND SESSION_CODE=<SESSION_CODE>  AND CLINICROOM_NO=<CLINICROOM_NO> AND REGION=<REGION>
update.Debug=N

//插入普通诊医生班表
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO reg_schday_dr (region, adm_type, session_code, clinic_area, clinicroom_no) VALUES (<REGION>,<ADM_TYPE>,<SESSION_CODE>,<CLINIC_AREA>,<CLINICROOM_NO>)
insertdata.Debug=N

//删除普通诊医生班表
deletedata.Type=TSQL
deletedata.SQL=DELETE REG_SCHDAY_DR WHERE REGION=<REGION> AND ADM_TYPE=<ADM_TYPE> AND SESSION_CODE=<SESSION_CODE> AND CLINIC_AREA=<CLINIC_AREA> AND CLINICROOM_NO=<CLINICROOM_NO>
deletedata.Debug=N

//修改普通诊医生班表
updateAll.Type=TSQL
updateAll.SQL=UPDATE REG_SCHDAY_DR SET DEPT_CODE=<DEPT_CODE>,CLINICTYPE_CODE=<CLINICTYPE_CODE>,DR_CODE =<DR_CODE> , OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE ,OPT_TERM=<OPT_TERM> WHERE ADM_TYPE=<ADM_TYPE> AND SESSION_CODE=<SESSION_CODE>  AND CLINICROOM_NO=<CLINICROOM_NO> AND REGION=<REGION> AND CLINIC_AREA=<CLINIC_AREA>
updateAll.Debug=N



