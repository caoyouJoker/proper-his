Module.item=onSelectEMRScope;insertData;deleteData;updateData

//��ѯEMR_SCOPE���ֶ�
onSelectEMRScope.Type=TSQL
onSelectEMRScope.SQL=SELECT EMR_SCOPE_CODE,EMR_SCOPE_NAME,IN_HOP_DEPT_DOC,MEMO FROM EMR_SCOPE 
				
onSelectEMRScope.item=EMR_SCOPE_CODE;EMR_SCOPE_NAME;IN_HOP_DEPT_DOC
onSelectEMRScope.EMR_SCOPE_CODE=EMR_SCOPE_CODE=<EMR_SCOPE_CODE>
onSelectEMRScope.EMR_SCOPE_NAME=EMR_SCOPE_NAME=<EMR_SCOPE_NAME>
onSelectEMRScope.IN_HOP_DEPT_DOC=IN_HOP_DEPT_DOC=<IN_HOP_DEPT_DOC>

onSelectEMRScope.Debug=Y
//��������
insertData.Type=TSQL
insertData.SQL=INSERT INTO EMR_SCOPE VALUES (<EMR_SCOPE_NAME>,<EMR_SCOPE_CODE>,<IN_HOP_DEPT_DOC>,<MEMO>)
insertData.Debug=Y

//ɾ������
deleteData.Type=TSQL
deleteData.SQL=DELETE EMR_SCOPE WHERE EMR_SCOPE_CODE=<EMR_SCOPE_CODE>
deleteData.Debug=Y

//���� MR_NO �������� updateData
updateData.Type=TSQL
updateData.SQL=UPDATE EMR_SCOPE SET EMR_SCOPE_NAME=<EMR_SCOPE_NAME>,IN_HOP_DEPT_DOC=<IN_HOP_DEPT_DOC>,MEMO=<MEMO> WHERE EMR_SCOPE_CODE=<EMR_SCOPE_CODE>
updateData.Debug=Y



