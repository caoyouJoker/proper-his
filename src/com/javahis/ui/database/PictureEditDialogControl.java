package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.div.MV;
import com.dongyang.tui.text.div.VPic;
import com.dongyang.tui.text.div.DIV;
import com.dongyang.util.TypeTool;
import javax.swing.ImageIcon;
import com.dongyang.util.FileTool;
import java.io.IOException;
import javax.swing.Icon;

/**
 *
 * <p>Title: 图片属性对话框控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author lzk 2010.2.3
 * @version 1.0
 */
public class PictureEditDialogControl
    extends TControl {
    /**
     * 宏
     */
    private EMacroroutine mactroroutine;
    /**
     * 初始化
     */
    public void onInit() {
        Object obj = getParameter();
        if (obj == null || ! (obj instanceof EMacroroutine)) {
            setTitle("插入图片");
            setValue("NAME", "新建图片");
            return;
        }
        mactroroutine = (EMacroroutine) obj;
        setValue("NAME", mactroroutine.getName());
        setValue("D_WIDTH", mactroroutine.getWidth());
        setValue("D_HEIGHT", mactroroutine.getHeight());
        MV mv = mactroroutine.getModel().getMVList().get("图层");
        if (mv == null) {
            return;
        }
        DIV div = mv.get("图片");
        if (! (div instanceof VPic)) {
            return;
        }
        VPic pic = (VPic) div;

        setValue("PIC_NAME", pic.getPictureName());
    }

    /**
     * 确定
     */
    public void onOK() {
        //检验文件格式;
        if (getText("PIC_NAME").indexOf("\\") != -1) {
            //this.messageBox("请输入本地上传文件的正确格式!形如：C:/XXX/XXX.gif");
            //return;
            String templatePath = getText("PIC_NAME").replaceAll("\\\\", "/");
            //this.messageBox("templatePath" + templatePath);
            setValue("PIC_NAME", templatePath);
        }
        //新建
        if (mactroroutine == null) {
                 setReturnValue(new Object[] {getText("NAME"),
                                getText("PIC_NAME"), getValue("D_WIDTH"),
                                getValue("D_HEIGHT")});

        }
        else {
            //说明为本地上传;
            if (getText("PIC_NAME").indexOf(":") == 1) {
                localUpload();
            }
            else {
                mactroroutine.setName(getText("NAME"));
                mactroroutine.getModel().setWidth(TypeTool.getInt(getValue(
                    "D_WIDTH")));
                mactroroutine.getModel().setHeight(TypeTool.getInt(getValue(
                    "D_HEIGHT")));

                MV mv = mactroroutine.getModel().getMVList().get("图层");
                if (mv == null) {
                    return;
                }
                DIV div = mv.get("图片");
                if (! (div instanceof VPic)) {
                    return;
                }
                VPic pic = (VPic) div;
                //
                pic.setPictureName(getText("PIC_NAME"));

                setReturnValue("OK");
            }
        }
        closeWindow();
    }

    /**
     *
     * @param fileName String
     * @return ImageIcon
     */
    private static ImageIcon getImage(String fileName) {

        ImageIcon icon = null;
        //
        //byte[] data = readFile(fileName);
        byte[] data = null;
        try {
            data = FileTool.getByte(fileName);
        }
        catch (IOException ex) {
        }
        if (data == null) {
            System.out.println("ERR:没有找到图片" + fileName);

            return icon;
        }
        icon = new ImageIcon(data);
        return icon;
    }

    private void localUpload(){


        mactroroutine.setName(getText("NAME"));
                mactroroutine.getModel().setWidth(TypeTool.getInt(getValue(
                    "D_WIDTH")));
                mactroroutine.getModel().setHeight(TypeTool.getInt(getValue(
                    "D_HEIGHT")));

                MV mv = mactroroutine.getModel().getMVList().get("图层");
                if (mv == null) {
                    return;
                }
                DIV div = mv.get("图片");
                if (! (div instanceof VPic)) {
                    return;
                }
                VPic pic = (VPic) div;
                //背景图
                //pic.setPictureNameBackground(getText("PIC_NAME"));
                //this.messageBox("本地上传 PictureNameBackground===="+pic.getPictureNameBackground());
                //合成图
                pic.setPictureName(getText("PIC_NAME"));
                // ((ImageIcon)pic.getIcon()).getImage();
                Icon bgIcon = (Icon) getImage(getText("PIC_NAME"));

                if (bgIcon == null) {
                    this.messageBox("没有找到图片!");
                    return;
                }

                pic.setIcon(bgIcon);
                setReturnValue("OK");

    }


    /**
     * 取消
     */
    public void onCancel() {
        closeWindow();
    }
}
