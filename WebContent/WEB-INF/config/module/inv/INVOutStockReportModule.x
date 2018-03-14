 /**
 * <p>Title: 物资出库报表</p>
 *
 * <p>Description:物资出库报表</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2013.11.13
 * @version 1.0
 */

Module.item=selectdetail




//出库明细
selectdetail.Type=TSQL
selectdetail.SQL=SELECT A.DISPENSE_NO,A.INV_CODE, '1' QTY, C.STOCK_UNIT, A.VALID_DATE, &
                        A.COST_PRICE,B.DISPENSE_DATE,B.TO_ORG_CODE,C.INV_CHN_DESC ,C.DESCRIPTION,C.MAN_CODE, &
			D.CONTRACT_PRICE,A.COST_PRICE, D.CONTRACT_PRICE AS CONTRACT_AMT, &
			 A.COST_PRICE AS COST_AMT, &
			( D.CONTRACT_PRICE- A.COST_PRICE )AS DIFFERENCE_AMT, &
			E.SUP_ABS_DESC,F.SUP_ABS_DESC AS UPSUP_ABS_DESC, &
			A.DISPENSE_NO AS OUTDISPENSE_NO,G.RFID &
			FROM &
			INV_DISPENSEDD A, &
			INV_DISPENSEM B, &
			INV_BASE C, &
			INV_AGENT D, &
			SYS_SUPPLIER E, &
			SYS_SUPPLIER F, &
			INV_STOCKDD G &
                        WHERE &
			A.DISPENSE_NO=B.DISPENSE_NO AND &
			A.INV_CODE=C.INV_CODE AND &
			A.INV_CODE=D.INV_CODE AND &
			C.SUP_CODE=E.SUP_CODE AND &
			A.INV_CODE=G.INV_CODE AND &
			A.RFID=G.RFID AND &
		        A.IO_FLG='2' AND &
			C.UP_SUP_CODE=F.SUP_CODE &
			AND A.INV_CODE=<INV_CODE> &
			AND A.DISPENSE_NO=<DISPENSE_NO> 
			
                       

selectdetail.ITEM=INV_CODE;SUP_CODE;UP_SUP_CODE;TO_ORG_CODE;FROM_ORG_CODE;SEQMAN_FLG;EXPENSIVE_FLG ;CONSIGN_FLG;INV_KIND
selectdetail.INV_CODE=A.INV_CODE=<INV_CODE>
selectdetail.SUP_CODE=C.SUP_CODE=<SUP_CODE>
selectdetail.UP_SUP_CODE=C.UP_SUP_CODE=<UP_SUP_CODE>
selectdetail.FROM_ORG_CODE=B.FROM_ORG_CODE=<FROM_ORG_CODE>
selectdetail.TO_ORG_CODE=B.TO_ORG_CODE=<TO_ORG_CODE>
selectdetail.SEQMAN_FLG=C.SEQMAN_FLG=<INV_FLG>
selectdetail.EXPENSIVE_FLG=C.EXPENSIVE_FLG=<INV_FLG>
selectdetail.CONSIGN_FLG=C.CONSIGN_FLG=<CONSIGN_FLG>
selectdetail.INV_KIND=C.INV_KIND=<INV_KIND>
selectdetail.Debug=N



