package jdo.sys;

import com.dongyang.data.TModifiedData;
import com.dongyang.data.StringValue;
import com.dongyang.data.DoubleValue;
import com.dongyang.data.IntValue;

public class ChargeDetail extends TModifiedData{
    /**
     * 身份代码
     */
    private String ctzCode;
    /**
     * 院内费用代码
     */
    private StringValue chargeHospCode = new StringValue(this);
    /**
     * 自费率
     */
    private DoubleValue ownRate = new DoubleValue(this);
    /**
     * 备注
     */
    private StringValue description = new StringValue(this);
    /**
     * 顺序
     */
    private IntValue seq = new IntValue(this);
    /**
     * 设置身份代码
     * @param ctzCode String
     */
    public void setCtzCode(String ctzCode)
    {
        this.ctzCode = ctzCode;
    }
    /**
     * 得到身份代码
     * @return String
     */
    public String getCtzCode()
    {
        return ctzCode;
    }
    /**
     * 设置院内费用代码
     * @param chargeHospCode String
     */
    public void setChargeHospCode(String chargeHospCode)
    {
        this.chargeHospCode.setValue(chargeHospCode);
    }
    /**
     * 得到院内费用代码
     * @return String
     */
    public String getChargeHospCode()
    {
        return chargeHospCode.getValue();
    }
    /**
     * 修改院内费用代码
     * @param chargeHospCode String
     */
    public void modifyChargeHospCode(String chargeHospCode)
    {
        this.chargeHospCode.modifyValue(chargeHospCode);
    }
    /**
     * 设置自费率
     * @param ownRate double
     */
    public void setOwnRate(double ownRate)
    {
        this.ownRate.setValue(ownRate);
    }
    /**
     * 得到自费率
     * @return double
     */
    public double getOwnRate()
    {
        return ownRate.getValue();
    }
    /**
     * 修改自费率
     * @param ownRate double
     */
    public void modifyOwnRate(double ownRate)
    {
        this.ownRate.modifyValue(ownRate);
    }
    /**
     * 设置备注
     * @param description String
     */
    public void setDescription(String description)
    {
        this.description.setValue(description);
    }
    /**
     * 得到备注
     * @return String
     */
    public String getDescription()
    {
        return description.getValue();
    }
    /**
     * 修改备注
     * @param description String
     */
    public void modifyDescription(String description)
    {
        this.description.modifyValue(description);
    }
    /**
     * 设置顺序
     * @param seq int
     */
    public void setSeq(int seq)
    {
        this.seq.setValue(seq);
    }
    /**
     * 得到顺序
     * @return int
     */
    public int getSeq()
    {
        return seq.getValue();
    }
    /**
     * 修改顺序
     * @param seq int
     */
    public void modifySeq(int seq)
    {
        this.seq.modifyValue(seq);
    }
}
