# 
#  Title:ȡ���걨module
# 
#  Description:ȡ���걨module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=selectdata;updateINSStatus;updateUploadFlg

//��ѯ
selectdata.Type=TSQL
selectdata.SQL=SELECT C.IN_DATE, C.CASE_NO, C.CONFIRM_NO, C.PAT_NAME, D.CHN_DESC,&
		      C.SEX_CODE, B.CTZ_DESC, C.IDNO, CASE IN_STATUS WHEN '0' THEN '�ʸ�ȷ����¼��' WHEN '1' THEN '�����ѽ���' &
		      WHEN '2' THEN '�������ϴ�' WHEN '3' THEN '���������' WHEN '4' THEN '������֧��' WHEN '5' THEN '����ȷ����' &
		      WHEN '6' THEN '�����ʸ�ȷ����ʧ��' WHEN '7' THEN '�ʸ�ȷ���������' ELSE '' END IN_STATUS, &
		      C.MR_NO, C.ADM_SEQ, C.NHIHOSP_NO, E.ACCOUNT_PAY_AMT,E.YEAR_MON,C.MR_NO &
		 FROM SYS_CTZ B, INS_ADM_CONFIRM C, SYS_DICTIONARY D, INS_IBS E &
		WHERE B.NHI_CTZ_FLG = 'Y'  &
		  AND B.CTZ_CODE = C.HIS_CTZ_CODE &
		  AND C.IN_DATE BETWEEN TO_DATE (<STARTDATE>, 'YYYY/MM/DD') &
				    AND TO_DATE (<ENDDATE>, 'YYYY/MM/DD') &
		  AND C.IN_STATUS='2' &
		  AND B.NHI_CTZ_FLG = 'Y' &
		  AND B.INS_CROWD_TYPE=<CTZCODE> &
		  AND C.SEX_CODE = D.ID(+) &
		  AND D.GROUP_ID = 'SYS_SEX' &
		  AND E.ADM_SEQ = C.ADM_SEQ &
	        ORDER BY C.ADM_SEQ
selectdata.item=SDISEASECODENULL;SDISEASECODE;MR_NO
selectdata.SDISEASECODENULL=(C.SDISEASE_CODE IS NULL OR C.SDISEASE_CODE='')
selectdata.SDISEASECODE=(C.SDISEASE_CODE IS NOT NULL )
selectdata.MR_NO=C.MR_NO=<MR_NO>
selectdata.Debug=N

updateINSStatus.Type=TSQL
updateINSStatus.SQL=UPDATE INS_ADM_CONFIRM SET IN_STATUS='1',OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE CONFIRM_NO=<CONFIRM_NO> 
updateINSStatus.Debug=N

updateUploadFlg.Type=TSQL
updateUploadFlg.SQL=UPDATE INS_IBS SET  UPLOAD_FLG='N',OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,STATUS='N'  WHERE CONFIRM_NO=<CONFIRM_NO> 
updateUploadFlg.Debug=N