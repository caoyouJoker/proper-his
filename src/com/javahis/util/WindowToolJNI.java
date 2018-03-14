package com.javahis.util;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class WindowToolJNI {
    public native void helloworld();
    static {
        System.loadLibrary("WindowToolJNI");
    }
    public static void main(String[] args) {
        new WindowToolJNI().helloworld();
    }
}
