Module.item=query

// ²éÑ¯È«×Ö¶Î
query.Type=TSQL
query.SQL=select count(rx_no),dept_code,dr_code,adm_type,sum(ar_amt) from  (select distinct rx_no,dept_code,dr_code,adm_type,ar_amt from opd_order where order_date >=to_date('20090101000000','yyyymmddhh24miss') and order_date<=to_date('20091212000000','yyyymmddhh24miss') ) a  group by dept_code,dr_code,adm_type
query.item=RX_TYPE;ADM_TYPE;REGION_CODE;DEPT_CODE;DR_CODE
query.RX_TYPE=RX_TYPE=<RX_TYPE>
query.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
query.REGION_CODE=REGION_CODE=<REGION_CODE>
query.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
query.DR_CODE=DR_CODE=<DR_CODE>
query.INFAINT=PRINTTYPEFLG_INFANT=<PRINTTYPEFLG_INFANT>
query.Debug=N