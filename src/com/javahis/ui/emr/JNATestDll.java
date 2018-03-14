package com.javahis.ui.emr;
import com.sun.jna.Library;
import com.sun.jna.Native;

public interface JNATestDll extends Library {
	JNATestDll instanceDll  = (JNATestDll)Native.loadLibrary("JNATestDll",JNATestDll.class);
	short myGetKeyState();
}
