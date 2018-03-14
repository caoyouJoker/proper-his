package com.javahis.system.choice;

import com.dongyang.control.TDrawControl;
import java.awt.Color;
import java.awt.Graphics;

public class TitleDrawControl extends TDrawControl{
    /**
     * ªÊ÷∆±≥æ∞
     * @param g Graphics
     */
    public void paintBackground(Graphics g)
    {
        g.setColor(new Color(8,53,133));

        int w = getWidth() - 1;
        int h = getHeight() - 1;
        g.fillPolygon(new int[]{20,w - 20,w,0},new int[]{h/2,h/2,h,h},4);
    }
}
