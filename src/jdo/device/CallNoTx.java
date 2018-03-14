package jdo.device;

/**
 * 叫号程序
 * @author lixiang
 *
 */
public class CallNoTx {
	
	/**
	   * 类构造器
	   * @alias 类构造器
	   */
	  static{
	    System.loadLibrary("CallNo");
	    init();
	  }

	  /**
	   * 初始化连接
	   * @alias  初始化连接
	   * @return int
	   */
	  public native static int init();
	  /**
	   *
	   * @param MsgType int
	   * @param Msg String
	   */
	  public native static void SendMsg(int MsgType, String Msg);
	  /**
	   *
	   * @param MsgType int
	   * @param Msg String
	   * @return int
	   */
	  public native static int SyncSendMsg(int MsgType, String Msg);


	  /**
	   * 构造器
	 * @return 
	   * @alias 构造器
	   */
	  public  CallNoTx() {
	  }


	  /**
	   * 主函数
	   */
	  public static void main(String[] args) {
		  
	  }

}
