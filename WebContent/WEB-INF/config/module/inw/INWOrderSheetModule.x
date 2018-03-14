Module.item=getPatForOrderPrt

//获得病人信息--02(在院) 03(出院)
getPatForOrderPrt.Type=TSQL

getPatForOrderPrt.SQL=SELECT   A.BED_NO,A.MR_NO,B.PAT_NAME, &

         	TO_CHAR (A.IN_DATE, 'YYYY/MM/DD') AS IN_DATE, &
         	
         	TO_CHAR (A.DS_DATE, 'YYYY/MM/DD') AS DS_DATE &
         	
  		FROM   ADM_INP A, SYS_PATINFO B &
  		
 		WHERE   A.MR_NO = B.MR_NO
 		        	
getPatForOrderPrt.item=MR_NO;STATION_CODE;IN_DATE;02;03
getPatForOrderPrt.MR_NO=A.MR_NO=<MR_NO>
getPatForOrderPrt.STATION_CODE=A.STATION_CODE=<STATION_CODE>
getPatForOrderPrt.IN_DATE=A.IN_DATE BETWEEN <START_DATE> AND <END_DATE>
getPatForOrderPrt.02=A.DS_DATE IS NULL
getPatForOrderPrt.03=A.DS_DATE IS NOT NULL
        	
getPatForOrderPrt.Debug=N

