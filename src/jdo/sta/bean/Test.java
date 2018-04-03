package jdo.sta.bean;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class Test {

	public static void main(String[] args) throws JAXBException {
		try {  
			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);

			Marshaller m = context.createMarshaller();
			
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			ObjectFactory object = new ObjectFactory();
			CASES cases=object.createCASES();
			m.marshal(object, System.out);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}
