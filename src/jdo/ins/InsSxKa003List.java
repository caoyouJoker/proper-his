package jdo.ins;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;

/**
 * <p>Title: 医保程序</p>
 *
 * <p>Description: 内嵌式医保程序</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author JiaoY
 * @version JavaHis 1.0
 */
public class InsSxKa003List extends TModifiedList {
    /**
     * 构造器
     */
    public InsSxKa003List() {
        StringBuffer sb = new StringBuffer();
        //药品编码
        sb.append("nhiOrderCode:NHI_ORDER_CODE;");
        // 最高限价
        sb.append("limitPrice:LIMIT_PRICE;");
        //医院等级
        sb.append("hospClass:HOSP_CLASS;");
        //项目名称
        sb.append("nhiOrderDesc:NHI_ORDER_DESC;");
        //收费类别
        sb.append("changeCode:CHARGE_CODE;");
        //收费项目等级
        sb.append("aka065:AKA065;");
        //一级费用类别
        sb.append("orderCat1:ORDER_CAT1;");
        //二级费用类别
        sb.append("orderCat2:ORDER_CAT2;");
        //三级费用类别
        sb.append("orderCat3:ORDER_CAT3;");
        //三大目录使用范围
        sb.append("description:DESCRIPTION;");
        //注记码
        sb.append("memo:MEMO;");
        //标准价格
        sb.append("price:PRICE;");
        //自付比例
        sb.append("ownRate:OWN_RATE;");
        //基础支付标准
        sb.append("nhiBasePrice:NHI_BASE_PRICE;");
        //经办人
        sb.append("approveUserId:APPROVE_USER_ID;");
        //经办日期
        sb.append("approveDate:APPROVE_DATE;");
        //数据分区编号
        sb.append("dataAreaNo:DATAAREA_NO;");
        //当前有效标志
        sb.append("activeFlg:ACTIVE_FLG;");
        //医院医嘱代码
        sb.append("hospOrderCode:HOSP_ORDER_CODE;");
        //收费类别等级
        sb.append("feeType:FEE_TYPE;");
        setMapString(sb.toString());
    }

    /**
     * 新增
     * @return InsSxKa002
     */
    public InsSxKa003 newInsSxKa003() {
        InsSxKa003 inssxka003 = new InsSxKa003();
        this.newData(inssxka003);
        return inssxka003;
    }

    /**
     * 初始化TPARM
     * @param parm
     * @return 真：成功，假：失败
     */
    public boolean initParm(TParm parm) {
        //System.out.println(" 003 list");
        //System.out.println("list parm = "+parm);
        if (parm == null)
            return false;
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            InsSxKa003 inssxka003 = new InsSxKa003();
            add(inssxka003);
            if (!inssxka003.initParm(parm, i))
                return false;
        }
        return true;
    }

}
