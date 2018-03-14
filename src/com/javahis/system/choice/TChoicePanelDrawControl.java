package com.javahis.system.choice;

import com.dongyang.control.TDrawControl;
import java.awt.Graphics;
import java.awt.Color;

public class TChoicePanelDrawControl extends TDrawControl{
    /**
     * ªÊ÷∆±≥æ∞
     * @param g Graphics
     */
    public void paintBackground(Graphics g)
    {
        int w = getWidth() - 1;
        int h = getHeight() - 1;

        g.setColor(new Color(33,101,171));
        int i = 0;
        g.fillPolygon(new int[]{8+i,20+i,w-20-i,w-8-i,w-i,w-i,w-8-i,w-20-i,20+i,8+i,i,i},
                      new int[]{8+i,i,i,8+i,20+i,h-20-i,h-8-i,h-i,h-i,h-8-i,h-20-i,20+i},12);


        g.setColor(new Color(221,240,249));
        g.drawPolygon(new int[]{1+i,8+i,9+i,20+i,w-20-i,w-9-i,w-8-i,w-1-i,w-1-i,w-8-i,w-9-i,w-20-i,20+i,9+i,8+i,1+i},
                      new int[]{20+i,9+i,8+i,1+i,1+i,8+i,9+i,20+i,h-20-i,h-9-i,h-8-i,h-1-i,h-1-i,h-8-i,h-9-i,h-20-i},16);
        g.drawPolygon(new int[]{8+i,20+i,w-20-i,w-8-i,w-i,w-i,w-8-i,w-20-i,20+i,8+i,i,i},
                      new int[]{8+i,i,i,8+i,20+i,h-20-i,h-8-i,h-i,h-i,h-8-i,h-20-i,20+i},12);
        g.setColor(new Color(167,215,237));
        i=1;
        g.drawPolygon(new int[]{1+i,8+i,9+i,20+i,w-20-i,w-9-i,w-8-i,w-1-i,w-1-i,w-8-i,w-9-i,w-20-i,20+i,9+i,8+i,1+i},
                      new int[]{20+i,9+i,8+i,1+i,1+i,8+i,9+i,20+i,h-20-i,h-9-i,h-8-i,h-1-i,h-1-i,h-8-i,h-9-i,h-20-i},16);
        g.drawPolygon(new int[]{8+i,20+i,w-20-i,w-8-i,w-i,w-i,w-8-i,w-20-i,20+i,8+i,i,i},
                      new int[]{8+i,i,i,8+i,20+i,h-20-i,h-8-i,h-i,h-i,h-8-i,h-20-i,20+i},12);
        g.setColor(new Color(128,197,230));
        i=2;
        g.drawPolygon(new int[]{1+i,8+i,9+i,20+i,w-20-i,w-9-i,w-8-i,w-1-i,w-1-i,w-8-i,w-9-i,w-20-i,20+i,9+i,8+i,1+i},
                      new int[]{20+i,9+i,8+i,1+i,1+i,8+i,9+i,20+i,h-20-i,h-9-i,h-8-i,h-1-i,h-1-i,h-8-i,h-9-i,h-20-i},16);
        g.drawPolygon(new int[]{8+i,20+i,w-20-i,w-8-i,w-i,w-i,w-8-i,w-20-i,20+i,8+i,i,i},
                      new int[]{8+i,i,i,8+i,20+i,h-20-i,h-8-i,h-i,h-i,h-8-i,h-20-i,20+i},12);
        g.setColor(new Color(76,172,219));
        i=3;
        g.drawPolygon(new int[]{8+i,20+i,w-20-i,w-8-i,w-i,w-i,w-8-i,w-20-i,20+i,8+i,i,i},
                      new int[]{8+i,i,i,8+i,20+i,h-20-i,h-8-i,h-i,h-i,h-8-i,h-20-i,20+i},12);
    }
    /**
     * ªÊ÷∆«∞æ∞
     * @param g Graphics
     */
    public void paintForeground(Graphics g)
    {

    }
}
