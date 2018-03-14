package jdo.opd;

import jdo.sys.Pat;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;

/**
*
* <p>
* Title: 药，诊疗项目，检验检查历史list
* </p>
*
* <p>
* Description:药，诊疗项目，检验检查历史list
* </p>
*
* <p>
* Copyright:  Copyright (c) Liu dongyang 2008
* </p>
*
* <p>
* Company:Javahis
* </p>
*
* @author ehui 200800910
* @version 1.0
*/
public class OrderHistoryList extends TModifiedList {
	private Pat pat;
    public OrderHistoryList()
    {
        setMapString("caseNo:CASE_NO;rxNo:RX_NO;seqNo:SEQ_NO;dcOrderDate:DC_ORDER_DATE;optUser:OPT_USER;optTerm:OPT_TERM;" +
                "presrtNo:PRESRT_NO;regionCode:REGION_CODE;admType:ADM_TYPE;rxType:RX_TYPE;" +
                "releaseFlg:RELEASE_FLG;linkmainFlg:LINKMAIN_FLG;linkNo:LINK_NO;orderCode:ORDER_CODE;orderDesc:ORDER_DESC;" +
                "goodsDesc:GOODS_DESC;orderCat1:ORDER_CAT1;takeQty:TAKE_QTY;mediUnit:MEDI_UNIT;freqCode:FREQ_CODE;" +
                "routeCode:ROUTE_CODE;takeDays:TAKE_DAYS;totQty:TOT_QTY;dgtTot:DGT_TOT;dispenseUnit:DISPENSE_UNIT;" +
                "opdgiveboxFlg:OPD_GIVEBOX_FLG;ownPrice:OWN_PRICE;nhiPrice:NHI_PRICE;discnrate:DISCN_RATE;ownAmt:OWN_AMT;" +
                "totAmt:TOT_AMT;description:DESCRIPTION;nsNote:NS_NOTE;drCode:DR_CODE;orderDate:ORDER_DATE;deptCode:DEPT_CODE;" +
                "dcDrCode:DC_DR_CODE;dcOrderDate:DC_ORDER_DATE;dcDeptCode:DC_DEPT_CODE;rborderDeptCode:RBORDER_DEPT_CODE;" +
                "setmainFlg:SETMAIN_FLG;ordsetGroupNo:ORDSET_GROUP_NO;ordersetCode:ORDERSET_CODE;hideFlg:HIDE_FLG;rpttypeCode:RPTTYPE_CODE;" +
                "optitemCode:OPTITEM_CODE;devCode:DEV_CODE;mrCode:MR_CODE;fileNo:FILE_NO;degreeCode:DEGREE_CODE;urgentFlg:URGENT_FLG;" +
                "inspayType:INSPAY_TYPE;phaType:PHA_TYPE;doseType:DOSE_TYPE;printtypeflgInfant:PRINTTYPEFLG_INFANT;" +
                "expensiveFlg:EXPENSIVE_FLG;ctrldrugclassCode:CTRLDRUGCLASS_CODE;dctagentCode:DCTAGENT_CODE;dctexcepCode:DCTEXCEP_CODE;" +
                "dctTakeQty:DCT_TAKE_QTY;packageTot:PACKAGE_TOT");
    }
    public void setPat(Pat pat)
    {
        this.pat = pat;
    }
    public Pat getPat()
    {
        return pat;
    }
    /**
     * 新增医嘱历史
     * @return Order
     */
    public OrderHistory newOrderHistory()
    {
    	OrderHistory orderhistory = new OrderHistory();
    	orderhistory.setPat(getPat());
        this.newData(orderhistory);
        return orderhistory;
    }
    /**
     * 得到医嘱历史
     * @param index int
     * @return Order
     */
    public OrderHistory getOrderHistory(int index)
    {
        return (OrderHistory)get(index);
    }

    public boolean initParm(TParm parm)
    {
        if(parm == null)
            return false;
        int count = parm.getCount();
        for(int i = 0;i < count;i ++)
        {
        	OrderHistory orderhistory = new OrderHistory();
        	orderhistory.setPat(getPat());
        	orderhistory.setMapString(getMapString());
            if(!orderhistory.initParm(parm,i))
                return false;
            add(orderhistory);
        }
        return true;
    }
}
