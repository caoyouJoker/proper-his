package com.javahis.ui.ekt.testEkt;



public interface IEktTradeStrategy {
	
	public EktParam openClient(EktParam ektParam);
	
	public EktParam openClientR(EktParam ektParam);
	
	public <T> EktParam creatParam(T t);

}
