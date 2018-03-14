package com.javahis.system.choice;

import com.dongyang.control.TDrawControl;
import java.awt.Color;
import java.awt.Graphics;

public class TextDrawControl extends TDrawControl{
    /**
     * ªÊ÷∆±≥æ∞
     * @param g Graphics
     */
    public void paintBackground(Graphics g)
    {
        g.setColor(new Color(8,53,133));
        g.fillRoundRect(0,0,getWidth() - 1,getHeight() - 1,30,30);
    }
    /**
     * ªÊ÷∆«∞æ∞
     * @param g Graphics
     */
    public void paintForeground(Graphics g)
    {

    }
}
