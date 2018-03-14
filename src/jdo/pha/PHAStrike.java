package jdo.pha;

import com.dongyang.jdo.TStrike;
import com.dongyang.data.TParm;
import java.util.Map;
import jdo.opd.TotQtyTool;

public class PHAStrike extends TStrike{
    /**
     * 唯一实例
     */
    private static PHAStrike instance;
    /**
     * 得到实例
     * @return PHAStrike
     */
    public static PHAStrike getInstance()
    {
        if(instance == null)
            instance = new PHAStrike();
        return instance;
    }
    /**
     * 总量计算
     * @param data Map
     * @return Map
     */
    public Map getTotQty(Map data) {
        if(isClientlink())
            return (Map)callServerMethod(data);
        TotQtyTool qty = new TotQtyTool();
        return qty.getTotQty(new TParm(data)).getData();
    }
}
