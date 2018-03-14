package com.javahis.util;

import java.util.Vector;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not WangM
 * @version 1.0
 */
public class HisObserver {
    /**
     * 状态位
     */
    private boolean changed = false;
    /**
     * 对象列表
     */
    private Vector obs;

    /**
     * 构造器
     */
    public HisObserver() {
        obs = new Vector();
    }

    /**
     * 添加观察者对象
     * @param o IodiOrder
     */
    public synchronized void addObserver(IHisObserver o) {
        if (o == null)
            throw new NullPointerException();
        if (!obs.contains(o)) {
            obs.addElement(o);
        }
    }
    /**
     * 删除一个观察者对象
     * @param o OdiObserver
     */
    public synchronized void deleteObserver(HisObserver o) {
        obs.removeElement(o);
    }
    /**
     * 通知观察者对象
     */
    public void notifyObservers() {
        notifyObservers(null);
    }
    /**
     * 通知观察者对象
     * @param arg Object
     */
    public void notifyObservers(Object arg) {
        /**
         * 对象列表
         */
        Object[] arrLocal;

        synchronized (this) {
            /**
             * 查看对象是否改变
             */
            if (!changed)
                return;
            /**
             * 拿到当前需要观察的对象列表
             */
            arrLocal = obs.toArray();
            /**
             * 清楚状态位状态
             */
            clearChanged();
        }
        /**
         * 调用观察对象的更新方法
         */
        for (int i = arrLocal.length-1; i>=0; i--)
            ((IHisObserver)arrLocal[i]).update(this, arg);
    }

    /**
     * 删除全部观察者对象
     */
    public synchronized void deleteObservers() {
        obs.removeAllElements();
    }

    /**
     * 设置状态位为改变
     */
    protected synchronized void setChanged() {
        changed = true;
    }

    /**
     * 清除状态位
     */
    protected synchronized void clearChanged() {
        changed = false;
    }

    /**
     * 得到当前状态位
     * @return boolean
     */
    public synchronized boolean hasChanged() {
        return changed;
    }
    /**
     * 得到观察者的数量
     * @return int
     */
    public synchronized int countObservers() {
        return obs.size();
    }

}
