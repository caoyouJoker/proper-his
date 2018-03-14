package action.ccb;

import java.util.Map;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * <p>
 * Title: socket通信Client端
 * </p>
 * 
 * <p>
 * Description: socket通信Client端
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author fuwj 2012-09-05
 * @version 1.0
 */
public class ClientHandler extends IoHandlerAdapter {

	private Map m;

	public ClientHandler(Map m) {
		this.m = m;
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		//session.write(m);
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		Map m = (Map) message;
		CBCClientAction.map = m;
		session.write(m);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		session.close(false);
		session.getService().dispose();
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		session.close(true);

	}

	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		session.close(true);// timeout now, close it
	}
}
