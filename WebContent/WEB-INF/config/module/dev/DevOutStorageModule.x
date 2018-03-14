# 
#  Title:�豸��������module
# 
#  Description:�豸��������module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author  fux 2013.07.15 
#  version 1.0
#
Module.item=selectDevOutStorageInf;getExStorgeInf1;getExStorgeInf2;getDeptExStorgeQty;deleteExStorgeQty;modifyStorgeQty;insertExStorgeD;insertStock;updateStockM;updateStockD;insertExStorgeM;updateExStorgeD;queryZTExStorgeD;queryWCExStorgeD;queryExReceiptData;deductStockMQty;deductStockDQty;deductStockDDQty;insertExStorgeDD;queryStockDD;UpdateRequsetMFinal;UpdateRequsetDFate;updateStockDD;selectDevStockD;queryExStorgeDD     
//�����豸������Ϣ      
selectDevOutStorageInf.Type=TSQL
selectDevOutStorageInf.SQL=SELECT EXWAREHOUSE_NO,EXWAREHOUSE_DATE,EXWAREHOUSE_USER,EXWAREHOUSE_DEPT,INWAREHOUSE_DEPT &
	                   FROM DEV_EXWAREHOUSEM &
	                   ORDER BY EXWAREHOUSE_NO
selectDevOutStorageInf.item=EXWAREHOUSE_NO;EXWAREHOUSE_DATE_BEGIN;EXWAREHOUSE_DEPT;INWAREHOUSE_DEPT;EXWAREHOUSE_USER;DISCHECK_FLG
//���ⵥ�� 
selectDevOutStorageInf.EXWAREHOUSE_NO=EXWAREHOUSE_NO LIKE <EXWAREHOUSE_NO>||'%' 
//��������
selectDevOutStorageInf.EXWAREHOUSE_DATE_BEGIN=EXWAREHOUSE_DATE BETWEEN <EXWAREHOUSE_DATE_BEGIN> AND <EXWAREHOUSE_DATE_END>
//�������
selectDevOutStorageInf.EXWAREHOUSE_DEPT=EXWAREHOUSE_DEPT = <EXWAREHOUSE_DEPT>
//������
selectDevOutStorageInf.INWAREHOUSE_DEPT=INWAREHOUSE_DEPT = <INWAREHOUSE_DEPT>
//������Ա
selectDevOutStorageInf.EXWAREHOUSE_USER=EXWAREHOUSE_USER = <EXWAREHOUSE_USER>
//��;��� 
selectDevOutStorageInf.DISCHECK_FLG= DISCHECK_FLG = <DISCHECK_FLG> 
selectDevOutStorageInf.Debug=N



//��ѯ��������Ϣ1
getExStorgeInf1.Type=TSQL
getExStorgeInf1.SQL=SELECT 'N' DEL_FLG,B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, 0 DEVSEQ_NO,&
		           B.DEV_CHN_DESC, B.DESCRIPTION, '' SETDEV_CODE , A.QTY STORGE_QTY, &
		           B.UNIT_CODE, A.UNIT_PRICE, A.CARE_USER, A.USE_USER, A.LOC_CODE,&
		           A.SCRAP_VALUE, A.DEPT_CODE,A.MAN_DATE,&
		           A.QTY,'' INWAREHOUSE_DEPT, A.QTY * A.UNIT_PRICE TOT_VALUE,'' REMARK1,'' REMARK2 &
		     FROM  DEV_STOCKD A,DEV_BASE B &  
		     WHERE B.SEQMAN_FLG='N' &
		     AND A.DEPT_CODE=<DEPT_CODE> &
		     AND A.QTY <> 0 &  
		     AND A.DEV_CODE=B.DEV_CODE &
		     ORDER BY B.DEV_CODE,A.DEPT_CODE
getExStorgeInf1.item=DEV_CODE;DEVPRO_CODE
//�豸���
getExStorgeInf1.DEV_CODE=B.DEV_CODE = <DEV_CODE>
//�豸����
getExStorgeInf1.DEVPRO_CODE=B.DEVPRO_CODE = <DEVPRO_CODE>  
getExStorgeInf1.Debug=N



//��ѯ��������Ϣ2
getExStorgeInf2.Type=TSQL
getExStorgeInf2.SQL=SELECT 'N' DEL_FLG,B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, & 
		           B.DEV_CHN_DESC, B.DESCRIPTION, A.SETDEV_CODE , A.QTY STORGE_QTY, &
		           B.UNIT_CODE, A.UNIT_PRICE, A.CARE_USER, A.USE_USER, A.LOC_CODE, &
		           A.SCRAP_VALUE,A.DEPT_CODE,A.MAN_DATE, &
		           A.QTY,'' INWAREHOUSE_DEPT, A.QTY * A.UNIT_PRICE TOT_VALUE,'' REMARK1,'' REMARK2 &
		    FROM   DEV_STOCKD A,DEV_BASE B &
		    WHERE  B.SEQMAN_FLG='Y' &
		    AND A.DEPT_CODE=<DEPT_CODE> &
		    AND A.DEV_CODE=B.DEV_CODE &  
		    ORDER BY B.DEV_CODE,A.DEPT_CODE
getExStorgeInf2.item=DEV_CODE;DEVSEQ_NO;DEVPRO_CODE
//�豸���
getExStorgeInf2.DEV_CODE=B.DEV_CODE = <DEV_CODE>
//�豸���
getExStorgeInf2.DEVSEQ_NO=A.DEVSEQ_NO = <DEVSEQ_NO>
//�豸����
getExStorgeInf2.DEVPRO_CODE=B.DEVPRO_CODE = <DEVPRO_CODE>
getExStorgeInf2.Debug=N


//ȡ�ó�������豸���
getDeptExStorgeQty.Type=TSQL
getDeptExStorgeQty.SQL=SELECT QTY &
	               FROM DEV_STOCKM &
	               WHERE  DEV_CODE=<DEV_CODE> 
getDeptExStorgeQty.Debug=N

 

//ɾ�����Ϊ�����Ϣ
deleteExStorgeQty.Type=TSQL 
deleteExStorgeQty.SQL=DELETE * FROM DEV_STOCKM &
                      WHERE DEV_CODE=<DEV_CODE> 
deleteExStorgeQty.Debug=N
 

//�۳����
modifyStorgeQty.Type=TSQL
modifyStorgeQty.SQL=UPDATE DEV_STOCKM SET QTY=QTY+<QTY> &
	            WHERE  DEV_CODE=<DEV_CODE> 
modifyStorgeQty.Debug=N 

//���ɿ������
insertExStorgeM.Type=TSQL
insertExStorgeM.SQL=INSERT INTO DEV_EXWAREHOUSEM (EXWAREHOUSE_NO,EXWAREHOUSE_DATE,EXWAREHOUSE_USER,EXWAREHOUSE_DEPT,INWAREHOUSE_DEPT, &
                                                  OPT_USER,OPT_DATE,OPT_TERM,DISCHECK_FLG)  &
		                          VALUES (<EXWAREHOUSE_NO>,<EXWAREHOUSE_DATE>,<EXWAREHOUSE_USER>,<EXWAREHOUSE_DEPT>,<INWAREHOUSE_DEPT>, &
		                                  <OPT_USER>,<OPT_DATE>,<OPT_TERM>,<DISCHECK_FLG>)

insertExStorgeM.Debug=N


 
//���ɳ�����ϸ
insertExStorgeD.Type=TSQL     
insertExStorgeD.SQL=INSERT INTO DEV_EXWAREHOUSED (EXWAREHOUSE_NO,SEQ_NO,DEV_CODE,& 
                                                  QTY,REMARK1,REMARK2,CARE_USER,&
                                                  OPT_USER,OPT_DATE,OPT_TERM,DISCHECK_FLG,UNIT_PRICE,&
						  MAN_DATE,SCRAP_VALUE,GUAREP_DATE,DEP_DATE,BRAND,&
						  SPECIFICATION,MODEL) &    
                                          VALUES (<EXWAREHOUSE_NO>,<SEQ_NO>,<DEV_CODE>,&
                                                  <QTY>,<REMARK1>,<REMARK2>,<CARE_USER>,&     
                                                  <OPT_USER>,<OPT_DATE>,<OPT_TERM>,<DISCHECK_FLG>,<UNIT_PRICE>,&
						  <MAN_DATE>,<SCRAP_VALUE>,<GUAREP_DATE>,<DEP_DATE>,<BRAND>,&
						  <SPECIFICATION>,<MODEL>)    
insertExStorgeD.Debug=N
    
//USE_USER=<USE_USER>
//LOC_CODE=<LOC_CODE> 
//���ɳ�����ϸ  
insertExStorgeDD.Type=TSQL      
insertExStorgeDD.SQL=INSERT INTO DEV_EXWAREHOUSEDD (EXWAREHOUSE_NO,SEQ_NO,DEVSEQ_NO,DEV_CODE,DEV_CODE_DETAIL,&
                                                  SETDEV_CODE,MAN_DATE,MANSEQ_NO,SCRAP_VALUE,&
                                                  GUAREP_DATE,DEP_DATE,UNIT_PRICE,OPT_USER,OPT_DATE,&
                                                  OPT_TERM ,RFID,BARCODE,MODEL,BRAND, &
						  SPECIFICATION,SERIAL_NUM,MAN_CODE,WIRELESS_IP, &
						  IP,TERM, LOC_CODE,USE_USER ) & 
                                          VALUES (<EXWAREHOUSE_NO>,<SEQ_NO>,<DEVSEQ_NO>,<DEV_CODE>,<DEV_CODE_DETAIL>,&
                                                  <SETDEV_CODE>,<MAN_DATE>,<MANSEQ_NO>,<SCRAP_VALUE>,&
                                                  <GUAREP_DATE>,<DEP_DATE>,<UNIT_PRICE>,<OPT_USER>,<OPT_DATE>,&
                                                  <OPT_TERM>,<RFID>,<BARCODE>,<MODEL>,<BRAND>, &
						  <SPECIFICATION>,<SERIAL_NUM>,<MAN_CODE>,<WIRELESS_IP>, &
						  <IP>,<TERM>,<LOC_CODE>,<USE_USER>)   
insertExStorgeDD.Debug=N      

//д������Ϣ
insertStock.Type=TSQL 
insertStock.SQL=INSERT INTO DEV_STOCKM (DEV_CODE,STOCK_FLG,QTY, & 
                                        OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE) &
                                VALUES (<DEV_CODE>,<STOCK_FLG>,<QTY>, &
                                        <OPT_USER>,<OPT_DATE>,<OPT_TERM>,<REGION_CODE>)
insertStock.Debug=N  

//���¿��������Ϣ (OnUpDate) 
updateStockM.Type=TSQL 
updateStockM.SQL=UPDATE DEV_STOCKM SET OPT_USER=<OPT_USER>, & 
                                       OPT_DATE=<OPT_DATE>, &  
                                       OPT_TERM=<OPT_TERM> &  
                                 WHERE  DEV_CODE=<DEV_CODE> 
updateStockM.Debug=N

 
//���¿����ϸ��Ϣ(ONUPDATE)
updateStockD.Type=TSQL
updateStockD.SQL=UPDATE DEV_STOCKD SET DEPT_CODE=<INWAREHOUSE_DEPT>, & 
                                       CARE_USER=<CARE_USER>, & 
                                       USE_USER=<USE_USER>, & 
                                       LOC_CODE=<LOC_CODE>, & 
                                       OPT_USER=<OPT_USER>, & 
                                       OPT_DATE=<OPT_DATE>, & 
                                       OPT_TERM=<OPT_TERM> & 
                                 WHERE DEPT_CODE=<EXWAREHOUSE_DEPT> & 
                                 AND   DEV_CODE=<DEV_CODE> 
updateStockD.Debug=N
  

//������Ź�����ϸ��Ϣ(�����۾����)  
updateStockDD.Type=TSQL 
updateStockDD.SQL=UPDATE DEV_STOCKDD SET DEPT_CODE=<DEPT_CODE>, & 
                                       WAIT_ORG_CODE = <WAIT_ORG_CODE>, &
                                       OPT_USER=<OPT_USER>, &   
                                       OPT_DATE=<OPT_DATE>, & 
                                       OPT_TERM=<OPT_TERM> & 
                                 WHERE DEV_CODE=<DEV_CODE> &  
                                 AND   DEVSEQ_NO=<DEVSEQ_NO> 
updateStockDD.Debug=N


//�޸ĳ�����ϸ��Ϣ
updateExStorgeD.Type=TSQL
updateExStorgeD.SQL=UPDATE DEV_EXWAREHOUSED SET CARE_USER=<CARE_USER>, &
                                                OPT_USER=<OPT_USER>, &
                                                OPT_DATE=<OPT_DATE>,&
                                                OPT_TERM=<OPT_TERM>,&  
                                                REMARK1=<REMARK1>,&
                                                REMARK2=<REMARK2> &
                                          WHERE EXWAREHOUSE_NO=<EXWAREHOUSE_NO> &
                                          AND   SEQ_NO=<SEQ_NO>
updateExStorgeD.Debug=N


//�õ���;������Ϣ  
queryZTExStorgeD.Type=TSQL 
queryZTExStorgeD.SQL=SELECT 'N' DEL_FLG,C.SEQMAN_FLG,C.DEVPRO_CODE,C.DEV_CODE,C.DEV_CHN_DESC, &
                          C.DESCRIPTION,B.QTY,D.QTY STORGE_QTY,A.INWAREHOUSE_DEPT,B.CARE_USER, & 
                          C.SETDEV_CODE,C.UNIT_CODE,D.UNIT_PRICE, & 
                          B.QTY * D.UNIT_PRICE TOT_VALUE,D.SCRAP_VALUE,B.GUAREP_DATE,B.DEP_DATE,B.REMARK1, & 
                          B.REMARK2,B.SEQ_NO,D.MAN_DATE,D.BRAND,D.SPECIFICATION,D.MODEL & 
                   FROM DEV_EXWAREHOUSEM A,DEV_EXWAREHOUSED B,DEV_BASE C,DEV_STOCKD D & 
                   WHERE  A.EXWAREHOUSE_NO=B.EXWAREHOUSE_NO &    
                   AND   B.DEV_CODE = C.DEV_CODE & 
                   AND   B.DEV_CODE = D.DEV_CODE &  
		   //�������=D�����Ϊ��;
                   AND   A.EXWAREHOUSE_DEPT = D.DEPT_CODE &
                   AND   A.DISCHECK_FLG = 'Y'     
                        
queryZTExStorgeD.Debug=N     

//�õ��Ѿ���ɳ�����Ϣ
queryWCExStorgeD.Type=TSQL 
queryWCExStorgeD.SQL=SELECT 'N' DEL_FLG,C.SEQMAN_FLG,C.DEVPRO_CODE,C.DEV_CODE,C.DEV_CHN_DESC, & 
                          C.DESCRIPTION,B.QTY,D.QTY STORGE_QTY,A.INWAREHOUSE_DEPT,B.CARE_USER, & 
                          C.SETDEV_CODE,C.UNIT_CODE,D.UNIT_PRICE, & 
                          B.QTY * C.UNIT_CODE TOT_VALUE,D.SCRAP_VALUE,B.GUAREP_DATE,B.DEP_DATE,B.REMARK1, & 
                          B.REMARK2,B.SEQ_NO,D.MAN_DATE,D.BRAND,D.SPECIFICATION,D.MODEL & 
                   FROM DEV_EXWAREHOUSEM A,DEV_EXWAREHOUSED B,DEV_BASE C,DEV_STOCKD D & 
                   WHERE A.EXWAREHOUSE_NO=<EXWAREHOUSE_NO> AND A.EXWAREHOUSE_NO=B.EXWAREHOUSE_NO &  
                   AND   B.DEV_CODE = C.DEV_CODE &    
                   AND   B.DEV_CODE = D.DEV_CODE &                              
		   //������=D����Ҳ������
                   AND   A.EXWAREHOUSE_DEPT = D.DEPT_CODE     
                   //AND   A.DISCHECK_FLG = 'N'
queryWCExStorgeD.Debug=N       
  
   
 

//���ⵥ��ӡʱ�õ��Ѿ�������Ϣ(��û������һ���ң�)       
queryExReceiptData.Type=TSQL      
queryExReceiptData.SQL=SELECT A.EXWAREHOUSE_NO,A.EXWAREHOUSE_DATE,A.EXWAREHOUSE_DEPT,A.EXWAREHOUSE_USER,A.INWAREHOUSE_DEPT, & 
                              C.DEVPRO_CODE,B.SEQ_NO,C.DEV_CHN_DESC,E.SPECIFICATION, & 
                              B.QTY,C.SETDEV_CODE,B.CARE_USER, &  
                              B.REMARK1,B.REMARK2 & 
                         FROM DEV_EXWAREHOUSEM A,DEV_EXWAREHOUSED B,DEV_BASE C,DEV_STOCKM D,DEV_STOCKD E &  
                         WHERE A.EXWAREHOUSE_NO = B.EXWAREHOUSE_NO &  
                         AND   A.EXWAREHOUSE_DEPT = E.DEPT_CODE &
                         AND   B.DEV_CODE = C.DEV_CODE &  
                         AND   B.DEV_CODE = D.DEV_CODE &                  
                         AND   D.DEV_CODE = E.DEV_CODE(+)     
queryExReceiptData.item=EXWAREHOUSE_NO               
queryExReceiptData.EXWAREHOUSE_NO=A.EXWAREHOUSE_NO = <EXWAREHOUSE_NO>    
queryExReceiptData.Debug=N
 


//����(����)����������  
deductStockMQty.Type=TSQL
deductStockMQty.SQL=UPDATE DEV_STOCKM  SET QTY = QTY-<QTY>, &
                                       OPT_USER=<OPT_USER>, & 
                                       OPT_DATE=<OPT_DATE>, &  
                                       OPT_TERM=<OPT_TERM> & 
                                 WHERE DEV_CODE=<DEV_CODE>  
deductStockMQty.Debug=N
 

//����(����)ϸ�������   
deductStockDQty.Type=TSQL  
deductStockDQty.SQL=UPDATE DEV_STOCKD  SET QTY = QTY-<QTY>, &
                                       OPT_USER=<OPT_USER>, & 
                                       OPT_DATE=<OPT_DATE>, &  
                                       OPT_TERM=<OPT_TERM> &  
                                 WHERE DEPT_CODE=<DEPT_CODE> & 
                                 AND   DEV_CODE=<DEV_CODE> 
deductStockDQty.Debug=N
   

//������Ź�����ϸ��Ϣ(ONNEW) ����WAIT_ORG_CODE�������۾����
deductStockDDQty.Type=TSQL
deductStockDDQty.SQL=UPDATE DEV_STOCKDD SET  DEPT_CODE = <DEPT_CODE>, &  
                                        WAIT_ORG_CODE=<INWAREHOUSE_DEPT>, & 
                                        OPT_USER=<OPT_USER>, & 
                                        OPT_DATE=<OPT_DATE>, & 
                                        OPT_TERM=<OPT_TERM> & 
                                 WHERE  DEV_CODE=<DEV_CODE> &   
                                 AND    DEVSEQ_NO=<DEVSEQ_NO> 
deductStockDDQty.Debug=N   

  
            	

//��ѯSTOCK_DD ��ϸ��Ϣ    
queryStockDD.Type=TSQL  
queryStockDD.SQL= SELECT A.DEV_CODE_DETAIL,A.DEVSEQ_NO,A.DEV_CODE,C.DEV_CHN_DESC,C.SETDEV_CODE, & 
                   B.MAN_DATE,B.MANSEQ_NO, B.SCRAP_VALUE,A.GUAREP_DATE,A.DEP_DATE, &       
                   A.UNIT_PRICE,A.MDEP_PRICE,A.DEP_PRICE,A.CURR_PRICE,C.UNIT_CODE,A.RFID,A.BARCODE,A.MODEL, &
		   A.BRAND,A.SPECIFICATION,A.SERIAL_NUM,A.MAN_CODE,A.IP,A.TERM,A.LOC_CODE,A.USE_USER,A.WIRELESS_IP & 
                   FROM DEV_STOCKDD A, DEV_STOCKD B, DEV_BASE C &   
                   WHERE A.DEV_CODE = B.DEV_CODE &    
                   AND A.DEV_CODE = C.DEV_CODE &        
                   AND A.DEPT_CODE = B.DEPT_CODE &    
                   ORDER BY A.DEVSEQ_NO,A.DEV_CODE     
queryStockDD.item=DEV_CODE;DEPT_CODE                  
queryStockDD.DEV_CODE=A.DEV_CODE = <DEV_CODE>  
queryStockDD.DEPT_CODE=A.DEPT_CODE = <DEPT_CODE>       
queryStockDD.Debug=N       


  

//������������
UpdateRequsetMFinal.Type=TSQL
UpdateRequsetMFinal.SQL=UPDATE DEV_REQUESTM SET FINAL_FLG = <FINAL_FLG>, &
                                       OPT_USER=<OPT_USER>, & 
                                       OPT_DATE=<OPT_DATE>, &  
                                       OPT_TERM=<OPT_TERM> & 
                                 WHERE REQUEST_NO=<REQUEST_NO> & 
                                 AND   DEV_CODE=<DEV_CODE> 
UpdateRequsetMFinal.Debug=N 
 

//��������ϸ��    
UpdateRequsetDFate.Type=TSQL
UpdateRequsetDFate.SQL=UPDATE DEV_REQUESTD FINA_TYPE = <FINA_TYPE>, &
                                       OPT_USER=<OPT_USER>, & 
                                       OPT_DATE=<OPT_DATE>, &  
                                       OPT_TERM=<OPT_TERM> & 
                                 WHERE REQUEST_NO=<REQUEST_NO> & 
                                 AND   DEV_CODE=<DEV_CODE> 
UpdateRequsetDFate.Debug=N 





//�õ�������С����   
//getMinBatchSeq.Type=TSQL
//getMinBatchSeq.SQL=SELECT MIN(BATCH_SEQ) AS BATCH_SEQ  FROM DEV_STOCKD &
//                   WHERE  DEV_CODE=<DEV_CODE>   
//getMinBatchSeq.Debug=N


//���ݵõ������ϵĿ��Һ��豸����õ�STOCKD����Ϣ(ע��ȥ��QTY����0��ֵ���в�ѯ) 
selectDevStockD.Type=TSQL
selectDevStockD.SQL=SELECT DEPT_CODE,DEV_CODE,QTY  FROM DEV_STOCKD &
                   WHERE  DEV_CODE=<DEV_CODE> & 
                   AND  DEPT_CODE=<DEPT_CODE> &
                   AND  QTY > 0
selectDevStockD.Debug=N
 

//�õ���;��ϸ����Ϣ  
queryExStorgeDD.Type=TSQL 
queryExStorgeDD.SQL=SELECT 'Y' AS SELECT_FLG,A.DEVSEQ_NO,A.DEV_CODE,B.DEV_CHN_DESC,A.BARCODE, &
                            A.SPECIFICATION,B.UNIT_CODE,A.UNIT_PRICE,A.DEV_CODE_DETAIL,A.BRAND, &
			    A.MODEL,A.MAN_CODE,A.WIRELESS_IP,A.IP,A.TERM, &
			    A.LOC_CODE,A.USE_USER &  
                   FROM DEV_EXWAREHOUSEDD A,DEV_BASE B &     
                   WHERE A.EXWAREHOUSE_NO=<EXWAREHOUSE_NO> &
                   AND   A.DEV_CODE = B.DEV_CODE   
queryExStorgeDD.Debug=N     
     