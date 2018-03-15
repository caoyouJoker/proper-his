# 
#  Title: RFID设备设定module
# 
#  Description: RFID设备设定module
# 
#  Copyright: Copyright (c) ProperSoft 2012
# 
#  author wanglong 2012.12.03
#  version 1.0
#
Module.item=selectRFIDDevice;insertRFIDDevice;updateRFIDDevice;deleteRFIDDevice;selectRFIDLogs;insertRFIDLog;deleteRFIDLog;selectRFID;updateRFIDRunStatus;selectRFIDByIP;selectDeptInfoByIP

//============================= 以下为RFID设备登记使用 ==============================================
//查询不良事件
selectRFIDDevice.Type=TSQL
selectRFIDDevice.SQL=SELECT RFID_CODE, IP_ADDRESS, RFID_MODEL, SN, RFID_STATUS, IN_ANTENNA, OUT_ANTENNA,&
                            DEPT_CODE, STATION_CODE, RFID_POSE, RFID_DESC, OPT_USER, OPT_DATE, OPT_TERM &
                       FROM DEV_RFID_BASE
selectRFIDDevice.item=RFID_CODE;IP_ADDRESS
selectRFIDDevice.RFID_CODE=RFID_CODE = <RFID_CODE>
selectRFIDDevice.IP_ADDRESS=IP_ADDRESS = <IP_ADDRESS>
selectRFIDDevice.Debug=N


// 查询RFID监测设备信息
selectRFID.Type=TSQL
selectRFID.SQL=SELECT B.RFID_CODE, A.DEPT_CHN_DESC,B.IP_ADDRESS,B.RFID_POSE,B.RFID_STATUS,B.RUN_STATUS, &
			CASE WHEN B.RUN_STATUS=0 THEN 'N' ELSE 'Y' END AS RUN_STATUS_CHECK &
			 FROM  SYS_DEPT A, DEV_RFID_BASE B &
		      WHERE B.DEPT_CODE=A.DEPT_CODE &
		      AND B.RFID_STATUS='1'
selectRFID.Debug=N

//新增不良事件
insertRFIDDevice.Type=TSQL
insertRFIDDevice.SQL=INSERT INTO DEV_RFID_BASE (RFID_CODE, IP_ADDRESS, RFID_MODEL, SN, RFID_STATUS, IN_ANTENNA, OUT_ANTENNA,&
                                                DEPT_CODE, STATION_CODE, RFID_POSE, RFID_DESC, OPT_USER, OPT_DATE, OPT_TERM) &
                          VALUES (<RFID_CODE>, <IP_ADDRESS>, <RFID_MODEL>, <SN>, <RFID_STATUS>, <IN_ANTENNA>, <OUT_ANTENNA>,&
                                  <DEPT_CODE>, <STATION_CODE>, <RFID_POSE>, <RFID_DESC>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertRFIDDevice.Debug=N

//更新不良事件信息
updateRFIDDevice.Type=TSQL
updateRFIDDevice.SQL=UPDATE DEV_RFID_BASE &
		        SET IP_ADDRESS = <IP_ADDRESS>, RFID_MODEL = <RFID_MODEL>, SN = <SN>, RFID_STATUS = <RFID_STATUS>,&
			    IN_ANTENNA = <IN_ANTENNA>, OUT_ANTENNA = <OUT_ANTENNA>, DEPT_CODE = <DEPT_CODE>,&
			    STATION_CODE = <STATION_CODE>, RFID_POSE = <RFID_POSE>, RFID_DESC = <RFID_DESC>,&
		            OPT_USER = <OPT_USER>, OPT_DATE = <OPT_DATE>, OPT_TERM = <OPT_TERM> &
		      WHERE RFID_CODE = <RFID_CODE>						
updateRFIDDevice.Debug=N

//删除不良事件
deleteRFIDDevice.Type=TSQL
deleteRFIDDevice.SQL=DELETE FROM DEV_RFID_BASE WHERE RFID_CODE = <RFID_CODE>
deleteRFIDDevice.Debug=N


// 更新RFID监测设备运行状态
updateRFIDRunStatus.Type=TSQL
updateRFIDRunStatus.SQL=UPATE DEV_RFID_BASE SET RUN_STATUS=<RFID_STATUS> &
			WHERE RFID_CODE=<RFID_CODE>
updateRFIDRunStatus.Debug=N

//============================= 以下为RFID设备日志查询使用 ==============================================
//查询RFID设备日志
selectRFIDLogs.Type=TSQL
selectRFIDLogs.SQL=SELECT '' AS FLG, RFID_LOG_CODE, RECORD_DATE, IP_ADDRESS, RFID_MESSAGE, OPT_USER, OPT_DATE, OPT_TERM &
                     FROM DEV_RFID_LOG &
		    WHERE RECORD_DATE BETWEEN <START_DATE> AND <END_DATE> &
		 ORDER BY RFID_LOG_CODE
selectRFIDLogs.item=IP_ADDRESS
selectRFIDLogs.IP_ADDRESS=IP_ADDRESS = <IP_ADDRESS>
selectRFIDLogs.Debug=N

//RFID设备插入日志
insertRFIDLog.Type=TSQL
insertRFIDLog.SQL=INSERT INTO DEV_RFID_LOG (RFID_LOG_CODE, RECORD_DATE, IP_ADDRESS, RFID_MESSAGE, OPT_USER, OPT_DATE, OPT_TERM) &
                       VALUES (<RFID_LOG_CODE>, <RECORD_DATE>, <IP_ADDRESS>, <RFID_MESSAGE>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertRFIDLog.Debug=N

//删除RFID设备日志
deleteRFIDLog.Type=TSQL
deleteRFIDLog.SQL=DELETE FROM DEV_RFID_LOG WHERE RFID_LOG_CODE = <RFID_LOG_CODE>
deleteRFIDLog.Debug=N


// 根据ip查询监测设备的出库、入库天线
selectRFIDByIP.Type=TSQL
selectRFIDByIP.SQL=SELECT A.IN_ANTENNA, A.OUT_ANTENNA FROM DEV_RFID_BASE A WHERE A.IP_ADDRESS=<IP_ADDRESS>
selectRFIDByIP.Debug=N


// 根据ip查询追踪设备的所属
selectDeptInfoByIP.Type=TSQL
selectDeptInfoByIP.SQL=SELECT A.IP_ADDRESS, A.DEPT_CODE, B.DEPT_CHN_DESC, A.STATION_CODE, C.STATION_DESC &
				FROM DEV_RFID_BASE A,SYS_DEPT B, SYS_STATION C &
				WHERE A.DEPT_CODE=B.DEPT_CODE(+) AND A.STATION_CODE=C.STATION_CODE(+) &
				AND A.IP_ADDRESS=<IP_ADDRESS>
selectDeptInfoByIP.Debug=N