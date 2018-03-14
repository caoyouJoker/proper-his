package jdo.ins;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;

public class InsSxKa002List extends TModifiedList {
    /**
     * 构造器
     */
    public InsSxKa002List() {
        StringBuffer sb = new StringBuffer();
        //药品编码
        sb.append("NhiOrderCode:NHI_ORDER_CODE;");
        // 中文名称
        sb.append("NhiOrderDesc:NHI_ORDER_DESC;");
        //英文
        sb.append("NhiOrderEngDesc:NHI_ORDER_ENG_DESC;");
        //收费类别
        sb.append("chargeCode:CHARGE_CODE;");
        //一级费用类别
        sb.append("orderCode1:ORDER_CAT1;");
        //二级费用类别
        sb.append("orderCode2:ORDER_CAT2;");
        //三级费用类别
        sb.append("orderCode3:ORDER_CAT3;");
        //处方药标志
        sb.append("preFlg:PRE_FLG;");
        //收费项目等级
        sb.append("orderType:ORDER_TYPE;");
        //注记码
        sb.append("memoCode:MEMO_CODE;");
        //单位
        sb.append("unit:UNIT;");
        //标准价格
        sb.append("price:PRICE;");
        //自付比例
        sb.append("ownRate:OWN_RATE;");
        //剂型
        sb.append("doseCode:DOSE_CODE;");
        //每次用量
        sb.append("qty:QTY;");
        //使用频次
        sb.append("freqCode:FREQ_CODE;");
        //用法
        sb.append("routeCode:ROUTE_CODE;");
        //规格
        sb.append("description:DESCRIPTION;");
        //限制类用药标志
        sb.append("LimitFlg:LIMIT_FLG;");
        //备注
        sb.append("remark:REMARK;");
        //经办人
        sb.append("aproveUserId:APROVE_USER_ID;");
        //经办日期
        sb.append("aproveDate:APPROVE_DATE;");
        //数据分区编号
        sb.append("DataAreaNo:DATAAREA_NO;");
        //当前有效标志
        sb.append("activeFlg:ACTIVE_FLG;");
        //工伤自付比例
        sb.append("bka002:BKA002;");
        //工伤收费类别
        sb.append("bka001:BKA001;");
        //医院医嘱代码
        sb.append("hospOrderCode:HOSP_ORDER_CODE;");
        //收费类别等级
        sb.append("feeType:FEE_TYPE;");
        //统计分类
        sb.append("staCode:STA_CODE");
        setMapString(sb.toString());
    }

    /**
     * 新增
     * @return InsSxKa002
     */
    public InsSxKa002 newInsSxKa002() {
        InsSxKa002 inssxka002 = new InsSxKa002();
        this.newData(inssxka002);
        return inssxka002;
    }

    /**
     * 初始化TPARM
     * @param parm
     * @return 真：成功，假：失败
     */
    public boolean initParm(TParm parm) {
        //System.out.println("002 list");
       // System.out.println("parm = "+parm);
        if (parm == null)
            return false;
        int count = parm.getCount();
        //System.out.println("count = "+count);
        for (int i = 0; i < count; i++) {
            InsSxKa002 inssxka002 = new InsSxKa002();
            add(inssxka002);
            if (!inssxka002.initParm(parm, i))
                return false;
        }
        return true;
    }
}
