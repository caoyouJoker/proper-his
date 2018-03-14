Module.item=updateDspnd;updateDspnm

updateDspnd.Type=TSQL

updateDspnd.SQL=UPDATE ODI_DSPND &
                SET NS_EXEC_DATE_REAL = <NS_EXEC_DATE_REAL>, &
                    NS_EXEC_CODE_REAL = <NS_EXEC_CODE_REAL>, &
                    INV_CODE = <INV_CODE>, &
                    CANCELRSN_CODE = <CANCELRSN_CODE>, &
		            //add by wanglong 20130604
		            BARCODE_1 = <BARCODE_1>, &
		            BARCODE_2 = <BARCODE_2>, &
		            BARCODE_3 = <BARCODE_3>, &
                    //add end
					
					//add by yangjj 20151020
					LATE_REASON = <LATE_REASON> &
					
                WHERE CASE_NO = <CASE_NO> &
                AND   ORDER_NO = <ORDER_NO> &
                AND   ORDER_SEQ = <ORDER_SEQ> &
                AND   ORDER_DATE = <ORDER_DATE> &
                AND   ORDER_DATETIME = <ORDER_DATETIME>
updateDspnd.Debug=N


updateDspnm.Type=TSQL

updateDspnm.SQL=UPDATE ODI_DSPNM &
                SET CANCELRSN_CODE = <CANCELRSN_CODE> &
                WHERE CASE_NO = <CASE_NO> &
                AND   ORDER_NO = <ORDER_NO> &
                AND   ORDER_SEQ = <ORDER_SEQ> &
                AND   <ORDER_DATE>||<ORDER_DATETIME> BETWEEN START_DTTM AND END_DTTM
updateDspnm.Debug=N