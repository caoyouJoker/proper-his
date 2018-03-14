package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.manager.TIOM_PatchServer;

/**
 * <p>
 * Title:批次服务器参数
 * </p>
 * 
 * <p>
 * Description:批次服务器参数
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author zhangy 2009.7.20
 * @version 1.0
 */
public class SYSPatchServerControl extends TControl {
	public SYSPatchServerControl() {
		super();
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		initPage();
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		if (!checkPatchServerData()) {
			return;
		}
		TIOM_PatchServer.setSleeptime(this.getValueInt("SleepTime"));
		TIOM_PatchServer.setExpiredtime(this.getValueInt("ExpiredTime"));
		this.messageBox("P0001");
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		this.setValue("SleepTime", 0);
		this.setValue("ExpiredTime", 0);
	}

	/**
	 * 初始画面数据
	 */
	private void initPage() {
		this.setValue("SleepTime", TIOM_PatchServer.getSleeptime());
		this.setValue("ExpiredTime", TIOM_PatchServer.getExpiredtime());
	}

	/**
	 * 数据检验
	 * 
	 * @return
	 */
	private boolean checkPatchServerData() {
		if (this.getValueInt("SleepTime") == 0) {
			this.messageBox("休眠时间不能为0");
			return false;
		}
		if (this.getValueInt("ExpiredTime") == 0) {
			this.messageBox("过期扫描时间不能为0");
			return false;
		}
		return true;
	}
}
