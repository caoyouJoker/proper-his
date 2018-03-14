package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.javahis.device.SIDDriver;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TPanel;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Color;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.ImageTool;

/**
 * <p>Title: 二代身份证信息</p>
 *
 * <p>Description: 二代身份证信息</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2010.03.08
 * @version 1.0
 */
public class SYSPatInfoFromIDControl
    extends TControl {
    public void onInit() {
        super.onInit();
        setPageValue();
    }

    /**
     * 给界面控件赋值
     */
    public void setPageValue() {
    }

    /**
     * 传回动作
     */
    public void onSaveFlg() {
        if (!"OK".equals(SIDDriver.read())) {
            this.messageBox("无病患信息" + SIDDriver.read());
            return;
        }

        String idMessage = SIDDriver.getDir();
        String[] aaa = StringTool.getHead(idMessage);
//        System.out.println("长度" + aaa.length);
//        System.out.println("a1====="+aaa[0]);
//        System.out.println("a2====="+aaa[1]);
        String patName = aaa[0].trim();
        this.setValue("PAT_NAME",patName);
        String[] bbb = StringTool.getHead(aaa[1]);
//        System.out.println("b1====="+bbb[0]);
//        System.out.println("b2====="+bbb[1]);
        String idno = bbb[0].trim();
        this.setValue("INNO",idno);
        String[] ccc = StringTool.getHead(bbb[1]);
//        System.out.println("c1====="+ccc[0]);
//        System.out.println("c2====="+ccc[1]);
        String address = ccc[0].trim();
        this.setValue("ADDRESS",address);
        String dir = ccc[1].trim();
        TParm returnParm = new TParm();
        returnParm.getData("PAT_NAME", patName);
        returnParm.getData("ADDRESS", address);
        returnParm.getData("INNO", idno);
        this.viewPhoto(dir);
//        this.setReturnValue(returnParm);
//        this.closeWindow();
    }

    /**
     * 显示photo
     * @param dir String
     */
    public void viewPhoto(String dir) {

        try {
            TPanel viewPanel = (TPanel) getComponent("VIEW_PANEL");
            byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
                dir);
            if (data == null)
                return;
            double scale = 0.5;
            boolean flag = true;
            Image image = ImageTool.scale(data, scale, flag);
//             Image image = ImageTool.getImage(data);
            Pic pic = new Pic(image);
            pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
            pic.setLocation(0, 0);
            viewPanel.removeAll();
            viewPanel.add(pic);
            pic.repaint();
        }
        catch (Exception e) {}
    }

    class Pic
        extends JLabel {
        Image image;
        public Pic(Image image) {
            this.image = image;
        }

        public void paint(Graphics g) {
            g.setColor(new Color(161, 220, 230));
            g.fillRect(4, 15, 100, 100);
            if (image != null) {
                g.drawImage(image, 4, 15,110,140, null);

            }
        }
    }

}
