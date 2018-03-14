package com.javahis.system;

import com.dongyang.control.TChildControl;
import java.awt.Font;
import java.util.Timer;
import java.awt.Dimension;
import java.awt.Color;
import java.util.StringTokenizer;
import java.util.TimerTask;
import java.awt.Graphics;
import java.awt.FontMetrics;

public class TMainShowControl
    extends TChildControl {
  private Timer timer;
  private int delay = 400;
  private String labelString =
      "JavaHis,JavaHis,JavaHis,正在运行,正在运行,正在运行,不要关闭,不要关闭,不要关闭";
  Font font = new java.awt.Font("Serif", Font.PLAIN, 24);
  /**
   * 初始化
   */
  public void onInit() {
    super.onInit();
    out("begin");
    callMessage("UI|addEventListener|paint|paint", this);
    callMessage("UI|addEventListener|destroy|destroy", this);
    callMessage("UI|addEventListener|start|start", this);
    callMessage("UI|addEventListener|stop|stop", this);
    out("end");
  }

  public void paint(Graphics g) {
    int fontSize = g.getFont().getSize();
    int x = 0, y = fontSize, space;
    int red = (int) (50 * Math.random());
    int green = (int) (50 * Math.random());
    int blue = (int) (256 * Math.random());
    Dimension d = (Dimension) callMessage("UI|getSize");
    g.setColor(Color.black);
    FontMetrics fm = g.getFontMetrics();
    space = fm.stringWidth(" ");
    for (StringTokenizer t = new StringTokenizer(labelString, ",");
         t.hasMoreTokens(); ) {
      String word = t.nextToken();
      int w = fm.stringWidth(word) + space;
      if (x + w > d.width) {
        x = 0;
        y += fontSize;
      }
      if (Math.random() < 0.5)
        g.setColor(new java.awt.Color( (red + y * 30) % 256,
                                      (green + x / 3) % 256, blue));
      else
        g.setColor( (Color) callMessage("UI|getBackground"));
      g.drawString(word, x, y);
      x += w;
    }
  }

  public void start() {
    out("begin");
    timer = new Timer();
    timer.schedule(new TimerTask() {
      public void run() {
        callMessage("UI|repaint");
      }
    }
    , delay, delay);
    out("end");
  }

  public void stop() {
    out("begin");
    timer.cancel();
    out("end");
  }

  public void destroy() {
    out("begin");
    out("end");
  }
}
