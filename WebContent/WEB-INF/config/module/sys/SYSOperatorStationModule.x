Module.item=select;delete;update;insert;selectStationClinicCode;getComboList

select.TYPE=TSQL
select.SQL=SELECT USER_ID,STATION_ID,AREA_TYPE,MAIN_FLG,OPT_USER,OPT_DATE,OPT_TERM FROM SYS_OPERATOR_STATION WHERE USER_ID=<USER_ID> ORDER BY STATION_ID
select.Debug=N

delete.TYPE=TSQL
delete.SQL=DELETE SYS_OPERATOR_STATION WHERE USER_ID=<USER_ID>

update.TYPE=TSQL
update.SQL=UPDATE SYS_OPERATOR_STATION SET USER_ID=<USER_ID>, STATION_ID=<STATION_ID>,AREA_TYPE=<AREA_TYPE>,MAIN_FLG=<MAIN_FLG>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>
update.ITEM=USER_ID;STATION_ID;AREA_TYPE
update.USER_ID=USER_ID=<KUSER_ID>
update.STATION_ID=STATION_ID=<KSTATION_ID>
update.AREA_TYPE=AREA_TYPE=<KAREA_TYPE>

insert.TYPE=TSQL
insert.SQL=INSERT SYS_OPERATOR_STATION VALUES(<USER_ID>,<STATION_ID>,<AREA_TYPE>,<MAIN_FLG>,<OPT_USER>,SYSDATE,<OPT_TERM>)

selectStationClinicCode.TYPE=TSQL 
selectStationClinicCode.SQL=SELECT STATION_CLINIC_CODE FROM SYS_OPERATOR_STATION WHERE USER_ID=<USER_ID> AND TYPE=<TYPE> AND MAIN_FLG='Y'
selectStationClinicCode.Debug=N

getComboList.TYPE=TSQL
getComboList.SQL=(SELECT CLINICAREA_CODE AS ID,CLINIC_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2,'1' AS TYPE &
      FROM REG_CLINICAREA WHERE CLINICAREA_CODE in( &
      SELECT STATION_CLINIC_CODE &
          FROM SYS_OPERATOR_STATION &
         WHERE USER_ID=<USER_ID> AND TYPE='1') &
UNION ALL &
SELECT STATION_CODE AS ID,STATION_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2,'2' AS TYPE &
          FROM SYS_STATION WHERE STATION_CODE in ( &
        SELECT STATION_CLINIC_CODE  &
          FROM SYS_OPERATOR_STATION &
         WHERE USER_ID=<USER_ID> AND TYPE='2'))ORDER BY TYPE,ID
getComboList.Debug=N	
