package jdo.sys;
import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;
public class ChargeDetailList extends TModifiedList{
    /**
     * 构造器
     */
    public ChargeDetailList()
    {
        setMapString("ctzCode:CTZ_CODE;chargeHospCode:CHARGE_HOSP_CODE;ownRate:OWN_RATE;description:DESCRITPION;seq:SEQ;chargeHospCode:CHARGE_HOSP_CODE_OLD:old");
    }
    /**
     * 新增折扣率明细
     * @return ChargeDetail
     */
    public ChargeDetail newChargeDetail()
    {
        ChargeDetail chargeDetail = new ChargeDetail();
        this.newData(chargeDetail);
        return chargeDetail;
    }
    /**
     * 得到折扣率明细
     * @param index int 位置
     * @return ChargeDetail
     */
    public ChargeDetail getChargeDetail(int index)
    {
        return(ChargeDetail)get(index);
    }
    /**
     * 装载数据
     * @param parm TParm
     * @return boolean
     */
    public boolean initParm(TParm parm)
    {
        if(parm == null)
            return false;
        int count = parm.getCount();
        for(int i = 0;i < count;i ++)
        {
            ChargeDetail chargeDetail = new ChargeDetail();
            add(chargeDetail);
            if(!chargeDetail.initParm(parm,i))
                return false;

        }
        return true;
    }
    public static void main(String args[])
    {
        ChargeDetailList list = new ChargeDetailList();
        TParm parm = new TParm();
        parm.addData("CTZ_CODE","99");
        parm.addData("CHARGE_HOSP_CODE","01");
        parm.addData("OWN_RATE",12.9);
        parm.addData("DESCRITPION","bz");
        parm.addData("SEQ",100);

        parm.addData("CTZ_CODE","99");
        parm.addData("CHARGE_HOSP_CODE","02");
        parm.addData("OWN_RATE",212.9);
        parm.addData("DESCRITPION","22");
        parm.addData("SEQ",200);

        parm.setData("ACTION","COUNT",2);

        list.initParm(parm);

        for(int i = 0;i <list.size();i++)
            //System.out.println(list.getChargeDetail(i).getParm());

        //list.removeData(1);

        list.getChargeDetail(0).modifySeq(1000);
        list.getChargeDetail(0).modifyChargeHospCode("88");
        //list.getChargeDetail(0).modifySeq(100);
        list.newChargeDetail().setCtzCode("1");
        list.newChargeDetail().setCtzCode("2");
        list.newChargeDetail().setCtzCode("3");

        //System.out.println("--------------------");
        //for(int i = 0;i <list.size();i++)
            //System.out.println(list.getChargeDetail(i));

        //System.out.println("list=" + list);

        //System.out.println("=================");
        //System.out.println(list.getParm());
    }
}
