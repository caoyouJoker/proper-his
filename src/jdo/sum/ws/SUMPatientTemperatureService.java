package jdo.sum.ws;


import javax.jws.WebService;

import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
@WebService
public interface SUMPatientTemperatureService {
    /**
     * �õ�NIS�ط��ļ�ȫ������
     * @return 
     */
    public String mainNISData(String hl7message);

}