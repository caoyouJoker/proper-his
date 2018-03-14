package com.javahis.device;

/**
 *
 * <p>Title: EMRPad30 GetCurCursorPos方法返回对象</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.12.30
 * @version 1.0
 */
public class EMRPadCurCursor {
    /**
     * 基行号
     * 保存返回的基行号。第一个基行的序号是0,如果返回-1,则说明此函数返回的所有值都无效
     */
    public long nBaseLineIndex;


    /**
     * 单元序号
     * 保存返回的单元需要,如果当前光标不在表格内,则返回-1,表格第一个单元格的序号是0
     */
    public long nCellIndex;


    /**
     * 行号
     * 保存返回的当前行的行号从0开始
     */
    public long nLineIndex;


    /**
     * 元素序号
     * 保存返回的当前元素序号从0开始
     */
    public long nFieldElemIndex;


    /**
     * 字符位置
     * 保存返回的当前字符位置从0开始
     */
    public long nCharPos;
    public String toString() {
        return "nBaseLineIndex=" + nBaseLineIndex + ",nCellIndex=" + nCellIndex +
            ",nLineIndex=" + nLineIndex + ",nFieldElemIndex=" + nFieldElemIndex +
            ",nCharPos=" + nCharPos;
    }
}
