package com.javahis.system.choice;

import com.dongyang.control.TDrawControl;
import java.awt.Color;
import java.awt.Graphics;

public class BackDrawControl extends TDrawControl{
    /**
     * ªÊ÷∆±≥æ∞
     * @param g Graphics
     */
    public void paintBackground(Graphics g)
    {
        /*g.setColor(new Color(76,172,219));
        int w = getWidth() - 1;
        int h = getHeight() - 1;
        for(int i = 0;i < 150;i+=2)
            g.drawLine(i,0,i,h);
        for(int i = w - 150;i < w;i+=2)
            g.drawLine(i,0,i,h);*/
    }
    /**
     * ªÊ÷∆«∞æ∞
     * @param g Graphics
     */
    public void paintForeground(Graphics g)
    {

    }
}
