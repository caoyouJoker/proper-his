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
public class InsSxKa004List extends TModifiedList {
    /**
    * 构造器
    */
   public InsSxKa004List() {
       StringBuffer sb = new StringBuffer();
       //医疗服务设施编码
       sb.append("aka100:AKA100;");
       // 最高限价
       sb.append("limitPrice:LIMIT_PRICE;");
       //医院等级
       sb.append("aka101:AKA101;");
       //服务设施名称
       sb.append("aka102:AKA102;");
       //收费类别
       sb.append("aka063:AKA063;");
       //收费项目等级
       sb.append("aka065:AKA065;");
       //一级费用类别
       sb.append("bka246:BKA246;");
       //二级费用类别
       sb.append("bka247:BKA247;");
       //三级费用类别
       sb.append("bka260:BKA260;");
       //三大目录使用范围
       sb.append("bka001:BKA001;");
       //病床等级
       sb.append("bka103:AKA103;");
       //注记码
       sb.append("aka066:AKA066;");
       //标准价格
       sb.append("aka068:AKA068;");
       //自付比例
       sb.append("aka069:AKA069;");
       //基础支付标准
       sb.append("aka104:AKA104;");
       //经办人
       sb.append("aae011:AAE011;");
       //经办日期
       sb.append("aae036:AAE036;");
       //数据分区编号
       sb.append("baa001:BAA001;");
       //当前有效标志
       sb.append("aae100:AAE100;");
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
   public InsSxKa004 newInsSxKa004() {
       InsSxKa004 inssxka004 = new InsSxKa004();
       this.newData(inssxka004);
       return inssxka004;
   }

   /**
    * 初始化TPARM
    * @param parm
    * @return 真：成功，假：失败
    */
   public boolean initParm(TParm parm) {
      // System.out.println(" 004 list");
      // System.out.println("list parm = "+parm);
       if (parm == null)
           return false;
       int count = parm.getCount();
       for (int i = 0; i < count; i++) {
           InsSxKa004 inssxka004 = new InsSxKa004();
           add(inssxka004);
           if (!inssxka004.initParm(parm, i))
               return false;
       }
       return true;
   }

}
