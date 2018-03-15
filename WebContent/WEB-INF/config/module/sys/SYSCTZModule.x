Module.item=selectTreeData;selectData;insertData;updataData;deleteData;initCtzCode;selCompanyCodeByCtz;getFlgByCtz;getFlgByCtz;getNhiNoCtz;getMroCtz

//��ѯ���ݴ��룬�������ƣ����ݼ�ƣ��Է�ע�ǣ�ҽ��ע�ǣ����²���������������Ա���������ڣ�˳���ţ�ƴ�����룬ע�Ƿ�����ע
selecTtreeData.Type=TSQL
selecTtreeData.SQL=SELECT CTZ_CODE,CTZ_CODE AS CTZ_CODE_T,CTZ_DESC,CTZ_DESC AS CTZ_DESC_T,COMPANY_CODE,COMPANY_CODE AS COMPANY_CODE_T,MAIN_CTZ_FLG,MAIN_CTZ_FLG AS MAIN_CTZ_FLG_T,NHI_CTZ_FLG,NHI_CTZ_FLG AS NHI_CTZ_FLG_T,PY1,PY1 AS PY1_T,PY2,PY2 AS PY2_T,DESCRIPTION,DESCRIPTION AS DESCRIPTION,SEQ,SEQ AS SEQ_T,MRCTZ_UPD_FLG,MRCTZ_UPD_FLG AS MRCTZ_UPD_FLG_T FROM SYS_CTZ ORDER BY SEQ
selecTtreeData.Debug=N

//��ѯ���ݴ��룬�������ƣ����ݼ�ƣ��Է�ע�ǣ�ҽ��ע�ǣ����²���������������Ա���������ڣ�˳���ţ�ƴ�����룬ע�Ƿ�����ע
selectData.Type=TSQL
selectData.SQL=SELECT CTZ_CODE,CTZ_DESC,COMPANY_CODE,NHI_CTZ_FLG,PY1,PY2,DESCRIPTION,SEQ,MRCTZ_UPD_FLG,MAIN_CTZ_FLG FROM SYS_CTZ ORDER BY SEQ
selectData.item=CTZ_CODE
selectData.CTZ_CODE=CTZ_CODE=<CTZ_CODE>
selectData.Debug=N


//����������
insertData.Type=TSQL
insertData.SQL=INSERT INTO SYS_CTZ VALUES (<CTZ_CODE>,<CTZ_DESC>,<COMPANY_CODE>,<PY1>,<PY2>,<SEQ>,<DESCRIPTION>,<MAIN_CTZ_FLG>,<NHI_CTZ_FLG>,<MRCTZ_UPD_FLG>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertData.Debug=N

//��������
updataData.Type=TSQL
updataData.SQL=UPDATE SYS_CTZ SET CTZ_CODE=<CTZ_CODE>,CTZ_DESC=<CTZ_DESC>,COMPANY_CODE=<COMPANY_CODE>,MAIN_CTZ_FLG=<MAIN_CTZ_FLG>,MRCTZ_UPD_FLG=<MRCTZ_UPD_FLG>,PY1=<PY1>,PY2=<PY2>,DESCRIPTION=<DESCRIPTION>,SEQ=<SEQ>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,NHI_CTZ_FLG=<NHI_CTZ_FLG> WHERE CTZ_CODE=<CTZ_CODE>
updataData.Debug=N

//ɾ������
deleteData.Type=TSQL
deleteData.SQL=DELETE FROM SYS_CTZ WHERE CTZ_CODE=<CTZ_CODE>
deleteData.Debug=N

//�õ������ۿ�
initCtzCode.Type=TSQL
initCtzCode.SQL=SELECT CTZ_CODE AS ID,CTZ_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_CTZ ORDER BY CTZ_CODE,SEQ
initCtzCode.item=MAIN_CTZ_FLG;NHI_CTZ_FLG
initCtzCode.MAIN_CTZ_FLG=MAIN_CTZ_FLG=<MAIN_CTZ_FLG>
initCtzCode.NHI_CTZ_FLG=NHI_CTZ_FLG=<NHI_CTZ_FLG>
initCtzCode.Debug=N

//���������ݲ�ѯҽ����λ
selCompanyCodeByCtz.Type=TSQL
selCompanyCodeByCtz.SQL=SELECT COMPANY_CODE &
			  FROM SYS_CTZ &
			 WHERE CTZ_CODE = <CTZ_CODE> &
selCompanyCodeByCtz.Debug=N

//�������ݴ���õ�ҽ�����ݱ��λ
getFlgByCtz.Type=TSQL
getFlgByCtz.SQL=SELECT NHI_CTZ_FLG FROM SYS_CTZ WHERE CTZ_CODE = <CTZ_CODE>
getFlgByCtz.Debug=N

//���ҽ�����ݴ���
//====pangb 2012-2-10
getNhiNoCtz.Type=TSQL
getNhiNoCtz.SQL=SELECT NHI_NO FROM SYS_CTZ WHERE CTZ_CODE = <CTZ_CODE> AND NHI_CTZ_FLG='Y' AND MAIN_CTZ_FLG='Y'
getNhiNoCtz.Debug=N

//�������ݴ���õ�������ҳ��������
getMroCtz.Type=TSQL
getMroCtz.SQL=SELECT MRO_CTZ FROM SYS_CTZ WHERE CTZ_CODE = <CTZ_CODE>
getMroCtz.Debug=N