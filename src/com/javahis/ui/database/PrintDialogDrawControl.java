package com.javahis.ui.database;

import com.dongyang.control.TDrawControl;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

public class PrintDialogDrawControl extends TDrawControl{
    private boolean type = true;
    public void setType(boolean type)
    {
        this.type = type;
    }
    public void paintForeground(Graphics g) {
        g.setFont(new Font("ËÎÌו",0,10));
        if(type)
        {
            p(18,4,g,3);
            p(75,4,g,3);
            p(11,16,g,2);
            p(68,16,g,2);
            p(4,28,g,1);
            p(61,28,g,1);
            return;
        }
        p(11,8,g,1);
        p(47,8,g,2);
        p(83,8,g,3);
        p(4,20,g,1);
        p(40,20,g,2);
        p(76,20,g,3);
    }
    public void p(int x,int y,Graphics g,int i)
    {
        g.setColor(new Color(255,255,255));
        g.fillRect(x,y,21,23);
        g.drawRect(x,y,21,23);
        g.setColor(new Color(0,0,0));
        g.drawRect(x,y,21,23);
        g.drawString("" + i,x + 16,y + 22);

    }
}
