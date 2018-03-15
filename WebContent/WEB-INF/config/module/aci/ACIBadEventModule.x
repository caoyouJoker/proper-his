# 
#  Title:�����¼�module
# 
#  Description:�����¼�module
# 
#  Copyright: Copyright (c) ProperSoft 2012
# 
#  author wanglong 2012.11.22
#  version 1.0
#
Module.item=selectBadEventData;insertBadEventData;updateBadEventData;deleteBadEventData;examineBadEventData;unExamineBadEventData;rateBadEventData


//��ѯ�����¼�
selectBadEventData.Type=TSQL
selectBadEventData.SQL=SELECT (CASE WHEN EXAMINE_USER IS NOT NULL THEN 'Y' ELSE 'N' END) AS FLG, ACI_NO,&
                              EVENT_TYPE, EVENT_CLASS, SAC_CLASS, EVENT_DATE, REPORT_DEPT, REPORT_STATION, REPORT_USER, REPORT_DATE, REPORT_SECTION,&
                              MR_NO, PAT_NAME, SEX_CODE, AGE, USER_IN_CHARGE, DEPT_IN_CHARGE,&
                              //����ASSESS_USER��ASSESS_DATE modify by wanglong 20131101
                              EVENT_DESC, EVENT_RESULT, EXAMINE_USER, EXAMINE_DATE, ASSESS_USER, ASSESS_DATE, OPT_USER, OPT_DATE, OPT_TERM &
                              //��������ֶ� modify by wanglong 20140107
                              ,DIAG_CODE, DIAG_DESC  &
                              //�����ϱ�ʱ�� modify by wukai 20160902
                              ,ROUND(REPORT_DATE - EVENT_DATE, 2) AS REPORT_DURATION  &
                         FROM ACI_BADEVENT &
                        WHERE 1=1  &   
                     ORDER BY ACI_NO DESC
selectBadEventData.item=EVENT_TYPE;EVENT_CLASS;SAC_CLASS;REPORT_DEPT;DEPT_IN_CHARGE;EXAMINED;UNEXAMINED;REPORT_SECTION;H_START_DATE;H_END_DATE;START_DATE;END_DATE
selectBadEventData.EVENT_TYPE=EVENT_TYPE like <EVENT_TYPE>
selectBadEventData.EVENT_CLASS=EVENT_CLASS = <EVENT_CLASS>
selectBadEventData.SAC_CLASS=SAC_CLASS = <SAC_CLASS>
selectBadEventData.REPORT_DEPT=REPORT_DEPT = <REPORT_DEPT>
selectBadEventData.DEPT_IN_CHARGE=DEPT_IN_CHARGE = <DEPT_IN_CHARGE>
selectBadEventData.EXAMINED=EXAMINE_USER IS NOT NULL
selectBadEventData.UNEXAMINED=EXAMINE_USER IS NULL
selectBadEventData.REPORT_SECTION= REPORT_SECTION = <REPORT_SECTION>
selectBadEventData.H_START_DATE=REPORT_DATE >= <H_START_DATE>
selectBadEventData.H_END_DATE=REPORT_DATE <= <H_END_DATE>
selectBadEventData.START_DATE=EVENT_DATE >= <START_DATE>
selectBadEventData.END_DATE=EVENT_DATE <= <END_DATE>
selectBadEventData.Debug=N

//���������¼�
insertBadEventData.Type=TSQL
                                                  //ȥ��EVENT_CLASS modify by wanglong 20131101
insertBadEventData.SQL=INSERT INTO ACI_BADEVENT (ACI_NO, EVENT_TYPE, EVENT_DATE,&
                                                 REPORT_DEPT, REPORT_STATION, REPORT_USER, REPORT_DATE, REPORT_SECTION,&
                                                 //��������ֶ� modify by wanglong 20140107
                                                 MR_NO, PAT_NAME, SEX_CODE, AGE, DIAG_CODE, DIAG_DESC, USER_IN_CHARGE, DEPT_IN_CHARGE,&
                                                 EVENT_DESC, EVENT_RESULT, OPT_USER, OPT_DATE, OPT_TERM) &
                                         VALUES (<ACI_NO>, <EVENT_TYPE>, <EVENT_DATE>,&
					         <REPORT_DEPT>, <REPORT_STATION>, <REPORT_USER>, <REPORT_DATE>, <REPORT_SECTION>,&
						  //��������ֶ� modify by wanglong 20140107
						 <MR_NO>, <PAT_NAME>, <SEX_CODE>, <AGE>, <DIAG_CODE>, <DIAG_DESC>, <USER_IN_CHARGE>, <DEPT_IN_CHARGE>,&
						 <EVENT_DESC>, <EVENT_RESULT>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertBadEventData.Debug=N

//���²����¼���Ϣ
updateBadEventData.Type=TSQL
updateBadEventData.SQL=UPDATE ACI_BADEVENT &
                      //ȥ��EVENT_CLASS modify by wanglong 20131101
		          SET EVENT_TYPE = <EVENT_TYPE>, EVENT_DATE = <EVENT_DATE>,&
		              REPORT_DEPT = <REPORT_DEPT>, REPORT_STATION = <REPORT_STATION>, REPORT_USER = <REPORT_USER>,&
		              REPORT_DATE = <REPORT_DATE>, REPORT_SECTION = <REPORT_SECTION>,&
                      //��������ֶ� modify by wanglong 20140107
		              MR_NO = <MR_NO>, PAT_NAME = <PAT_NAME>, SEX_CODE = <SEX_CODE>, AGE = <AGE>,DIAG_CODE = <DIAG_CODE>, DIAG_DESC = <DIAG_DESC>,&
		              USER_IN_CHARGE = <USER_IN_CHARGE>, DEPT_IN_CHARGE = <DEPT_IN_CHARGE>,&
		              EVENT_DESC = <EVENT_DESC>, EVENT_RESULT = <EVENT_RESULT>,&
		              OPT_USER = <OPT_USER>, OPT_DATE = <OPT_DATE>, OPT_TERM = <OPT_TERM> &
		        WHERE ACI_NO = <ACI_NO>
updateBadEventData.Debug=N

//ɾ�������¼�
deleteBadEventData.Type=TSQL
deleteBadEventData.SQL=DELETE FROM ACI_BADEVENT WHERE ACI_NO = <ACI_NO>
deleteBadEventData.Debug=N

//��˲����¼�
examineBadEventData.Type=TSQL
examineBadEventData.SQL=UPDATE ACI_BADEVENT &
                        //����EVENT_CLASS modify by wanglong 20131101
		           SET EXAMINE_USER = <EXAMINE_USER>, EVENT_CLASS = <EVENT_CLASS>, EXAMINE_DATE = <EXAMINE_DATE> &
		         WHERE ACI_NO = <ACI_NO>
examineBadEventData.Debug=N

//�����¼�ȡ�����
unExamineBadEventData.Type=TSQL
unExamineBadEventData.SQL=UPDATE ACI_BADEVENT &
                        //����EVENT_CLASS modify by wanglong 20131101
		             SET EXAMINE_USER = null,EVENT_CLASS = null, EXAMINE_DATE = null &
		           WHERE ACI_NO = <ACI_NO>
unExamineBadEventData.Debug=N

//���������¼� add  by wanglong 20131101
rateBadEventData.Type=TSQL
rateBadEventData.SQL=UPDATE ACI_BADEVENT &
                        SET ASSESS_USER = <ASSESS_USER>, SAC_CLASS = <SAC_CLASS>, ASSESS_DATE = <ASSESS_DATE> &
                      WHERE ACI_NO = <ACI_NO>
rateBadEventData.Debug=N