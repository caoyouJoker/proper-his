Module.item=onSelectEMRRusecope;insertData;deleteData;updateData;insertDataEmr;onSelectEMRRule;deleteDataEMR;deleteDataEMRSCOPE

//��ѯEMR_RUSECSCOPE���ֶ�
onSelectEMRRusecope.Type=TSQL
onSelectEMRRusecope.SQL=SELECT ID,EMR_SCOPE_CODE,EMR_RULE_CODE,SECURITY_CATEGORY_CODE FROM EMR_RUSECSCOPE ORDER BY ID ASC
				
onSelectEMRRusecope.item=EMR_SCOPE_CODE;EMR_RULE_CODE;SECURITY_CATEGORY_CODE;EMR_SORTDIC_NAME
onSelectEMRRusecope.EMR_SCOPE_CODE=EMR_SCOPE_CODE=<EMR_SCOPE_CODE>
onSelectEMRRusecope.EMR_RULE_CODE=EMR_RULE_CODE=<EMR_RULE_CODE>
onSelectEMRRusecope.SECURITY_CATEGORY_CODE=SECURITY_CATEGORY_CODE=<SECURITY_CATEGORY_CODE>
onSelectEMRRusecope.EMR_SORTDIC_NAME=EMR_SORTDIC_NAME like <EMR_SORTDIC_NAME>

onSelectEMRRusecope.Debug=Y
//��������
insertData.Type=TSQL
insertData.SQL=INSERT INTO EMR_RUSECSCOPE VALUES (<EMR_SCOPE_CODE>,<SECURITY_CATEGORY_CODE>,<ID>,<EMR_RULE_CODE>)
insertData.Debug=Y

//ɾ������
deleteData.Type=TSQL
deleteData.SQL=DELETE EMR_RUSECSCOPE WHERE ID=<ID>
deleteData.Debug=Y

//���� ID �������� updateData
updateData.Type=TSQL
updateData.SQL=UPDATE EMR_RUSECSCOPE SET EMR_SCOPE_CODE=<EMR_SCOPE_CODE>,SECURITY_CATEGORY_CODE=<SECURITY_CATEGORY_CODE>, &
						EMR_RULE_CODE=<EMR_RULE_CODE> WHERE ID=<ID>
updateData.Debug=Y

//��������
insertDataEmr.Type=TSQL
insertDataEmr.SQL=INSERT INTO EMR_RULE_AUTHORITY VALUES (<ID>,<EMR_RULE_CODE>,<EMR_SCOPE_CODE>,<SECURITY_CATEGORY_CODE>,<EMR_CLASS_CODE>,&
						<IS_READ>,<MEMO>,<EMR_DIC_NAME>)
insertDataEmr.Debug=Y


onSelectEMRRule.Type=TSQL
onSelectEMRRule.SQL=SELECT ROWNUM,EMR_CLASS_CODE,EMR_DIC_NAME,ID FROM EMR_RULE_AUTHORITY 
				
onSelectEMRRule.item=EMR_SCOPE_CODE;EMR_RULE_CODE;SECURITY_CATEGORY_CODE
onSelectEMRRule.EMR_SCOPE_CODE=EMR_SCOPE_CODE=<EMR_SCOPE_CODE>
onSelectEMRRule.EMR_RULE_CODE=EMR_RULE_CODE=<EMR_RULE_CODE>
onSelectEMRRule.SECURITY_CATEGORY_CODE=SECURITY_CATEGORY_CODE=<SECURITY_CATEGORY_CODE>

onSelectEMRRusecope.Debug=Y


//ɾ������
deleteDataEMR.Type=TSQL
deleteDataEMR.SQL=DELETE EMR_RULE_AUTHORITY WHERE ID=<ID>
deleteDataEMR.Debug=Y

//ɾ������
deleteDataEMRSCOPE.Type=TSQL
deleteDataEMRSCOPE.SQL=DELETE EMR_RULE_AUTHORITY WHERE &
				       SECURITY_CATEGORY_CODE=<SECURITY_CATEGORY_CODE> AND &
				       EMR_RULE_CODE=<EMR_RULE_CODE> AND EMR_SCOPE_CODE=<EMR_SCOPE_CODE>
deleteDataEMRSCOPE.Debug=Y