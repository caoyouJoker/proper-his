package com.javahis.ui.ekt.testEkt;

import java.math.BigDecimal;

import jdo.odo.ODO;

import com.javahis.ui.ekt.testEkt.impl.EktTradeStrategyOpbImpl;
import com.javahis.ui.ekt.testEkt.impl.EktTradeStrategyOpdImpl;
import com.javahis.ui.ekt.testEkt.impl.EktTradeStrategyRegImpl;
import com.javahis.ui.opd.OdoMainControl;



public class EktTradeContext {
	
private IEktTradeStrategy ektTradeStrategy;
	
	private EktParam ektParam;
	private String type;

	
	public EktTradeContext(EktParam ektParam){
		
		this.ektParam = ektParam;
		
		if(ektParam.getType().equals("ODO")){
			ektTradeStrategy = new EktTradeStrategyOpdImpl(ektParam);
			type="ODO";
			
		}
		
		if(ektParam.getType().equals("REG")){
			ektTradeStrategy = new EktTradeStrategyRegImpl(ektParam);
			type="REG";
			
		}
		
		if(ektParam.getType().equals("OPB")){
			ektTradeStrategy = new EktTradeStrategyOpbImpl(ektParam);
			type="OPB";
			
		}
		
		
		
	}
	
	
	private EktParam openClient(){
		return ektTradeStrategy.openClient(ektParam);
	}
	
	private <T> EktParam creatParam(T t){
		return ektTradeStrategy.creatParam(t);
	}
	
	public <T> void openClient(T t) throws Exception{
	
		ektParam = creatParam(t);
		
		if(!"ODO".equals(type) && ektParam == null ){
			 throw new Exception("Ò½ÁÆ¿¨±£´æ´íÎó");
		}
		
		if(ektParam.getOpType().length() == 0){
			ektParam = openClient();
			
			System.out.println("½áÊø ---------");
			
			if(!"ODO".equals(type) && ektParam == null ){
				 throw new Exception("Ò½ÁÆ¿¨±£´æ´íÎó");
			}

		}
		
	}
	
	public <T> void openClientR(T t) throws Exception{
		ektParam = (EktParam) t;
		
		ektParam = ektTradeStrategy.openClientR(ektParam);
		if(ektParam == null ){
			 throw new Exception("Ò½ÁÆ¿¨±£´æ´íÎó");
		}
	}
	

	

}
