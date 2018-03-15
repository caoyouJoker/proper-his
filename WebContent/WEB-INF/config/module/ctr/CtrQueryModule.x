# 
#  Title:ҽ��ָ�ز�ѯ
# 
#  Description:ҽ��ָ�ز�ѯ
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author shibl   2011.6.24
#  version 1.0
#
Module.item=query;insert;update;delete

//��ѯ����
query.Type=TSQL
query.SQL=SELECT RESTRITEM_CODE,RESTRITEM_DESC,DRUG_FLG,TREAT_FLG,MATERIAL_FLG,&
    OPD_FLG,EMG_FLG,INP_FLG,UD_FLG,INCLUDE_FLG,&
    EXCLUDE_FLG,PROMPT_MSG1,RESTRPARA_TYPE1,PARADATATYPE_1,PROMPT_MSG2, &
    RESTRPARA_TYPE2,PARADATATYPE_2,PROMPT_MSG3,RESTRPARA_TYPE3,PARADATATYPE_3 FROM CTR_PANEL ORDER BY RESTRITEM_CODE
query.item=RESTRITEM_CODE
query.RESTRITEM_CODE=RESTRITEM_CODE=<RESTRITEM_CODE>
query.Debug=N

//�������
insert.Type=TSQL
insert.SQL=INSERT INTO CTR_PANEL &
            		  (RESTRITEM_CODE, RESTRITEM_DESC, DRUG_FLG, TREAT_FLG, MATERIAL_FLG, &
    OPD_FLG, EMG_FLG, INP_FLG, UD_FLG, INCLUDE_FLG, &
    EXCLUDE_FLG, PROMPT_MSG1, RESTRPARA_TYPE1, PARADATATYPE_1, PROMPT_MSG2, &
    RESTRPARA_TYPE2, PARADATATYPE_2, PROMPT_MSG3, RESTRPARA_TYPE3, PARADATATYPE_3,OPT_USER,OPT_DATE,OPT_TERM) &
     		   VALUES (<RESTRITEM_CODE>,<RESTRITEM_DESC>,<DRUG_FLG>,<TREAT_FLG>,<MATERIAL_FLG>,<OPD_FLG>,<EMG_FLG>,&
             		   <INP_FLG>, <UD_FLG>,<INCLUDE_FLG>,<EXCLUDE_FLG>, <PROMPT_MSG1>, <RESTRPARA_TYPE1>,<PARADATATYPE_1>,<PROMPT_MSG2>, &
    <RESTRPARA_TYPE2>, <PARADATATYPE_2>, <PROMPT_MSG3>, <RESTRPARA_TYPE3>, <PARADATATYPE_3>,<OPT_USER>, SYSDATE, <OPT_TERM>)
insert.Debug=N

//���±���
update.Type=TSQL
update.SQL=UPDATE CTR_PANEL SET &
         RESTRITEM_DESC=<RESTRITEM_DESC>,DRUG_FLG=<DRUG_FLG>,TREAT_FLG=<TREAT_FLG>,MATERIAL_FLG=<MATERIAL_FLG>,OPD_FLG=<OPD_FLG>,EMG_FLG=<EMG_FLG>,&
         INP_FLG=<INP_FLG>,UD_FLG=<UD_FLG>,INCLUDE_FLG=<INCLUDE_FLG>,EXCLUDE_FLG=<EXCLUDE_FLG>,PROMPT_MSG1=<PROMPT_MSG1>,RESTRPARA_TYPE1=<RESTRPARA_TYPE1>,PARADATATYPE_1=<PARADATATYPE_1>,&
         PROMPT_MSG2=<PROMPT_MSG2>,RESTRPARA_TYPE2=<RESTRPARA_TYPE2>,PARADATATYPE_2=<PARADATATYPE_2>,PROMPT_MSG3=<PROMPT_MSG3>,RESTRPARA_TYPE3=<RESTRPARA_TYPE3>,& 
         PARADATATYPE_3=<PARADATATYPE_3>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE RESTRITEM_CODE=<RESTRITEM_CODE> 
update.Debug=N

//ɾ������
delete.Type=TSQL
delete.SQL=DELETE FROM CTR_PANEL WHERE RESTRITEM_CODE=<RESTRITEM_CODE> 
delete.Debug=N