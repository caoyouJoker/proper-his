Module.item=updateDspnd;updateDspnm

updateDspnd.Type=TSQL

updateDspnd.SQL=UPDATE ODI_DSPND &
                SET NS_EXEC_DATE_REAL = <NS_EXEC_DATE_REAL>, &
                    NS_EXEC_CODE_REAL = <NS_EXEC_CODE_REAL>, &
                    INV_CODE = <INV_CODE>, &
                    CANCELRSN_CODE = <CANCELRSN_CODE> &
                WHERE CASE_NO = <CASE_NO> &
                AND   ORDER_NO = <ORDER_NO> &
                AND   ORDER_SEQ = <ORDER_SEQ> &
                AND   ORDER_DATE = <ORDER_DATE> &
                AND   ORDER_DATETIME = <ORDER_DATETIME>
updateDspnd.Debug=N


updateDspnm.Type=TSQL

updateDspnm.SQL=UPDATE ODI_DSPNM &
                SET CANCELRSN_CODE = <CANCELRSN_CODE>, &
			OPT_DATE=<OPT_DATE>,OPT_USER=<OPT_USER>,OPT_TERM=<OPT_TERM>,&
			NS_EXEC_CODE=<NS_EXEC_CODE>,NS_EXEC_DATE=<NS_EXEC_DATE>,&	
	NS_EXEC_DC_CODE=<NS_EXEC_DC_CODE>,NS_EXEC_DC_DATE=<NS_EXEC_DC_DATE> &
                WHERE CASE_NO = <CASE_NO> &
                AND   ORDER_NO = <ORDER_NO> &
                AND   ORDER_SEQ = <ORDER_SEQ> &
                AND   <ORDER_DATE>||<ORDER_DATETIME> BETWEEN START_DTTM AND END_DTTM
updateDspnm.Debug=N