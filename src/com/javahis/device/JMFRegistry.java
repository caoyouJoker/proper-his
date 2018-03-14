package com.javahis.device;

import com.sun.media.ui.TabControl;
import com.sun.media.util.JMFI18N;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import jmapps.registry.*;
import jmapps.ui.JMFrame;

public class JMFRegistry
    extends JMFrame
{

  private TabControl tabs;
  private Panel panelPM;
  private Panel panelPIM;
  private Panel panelCDM;
  private Panel panelMime;
  private Panel panelOther;
  public JMFRegistry()
  {
    super(JMFI18N.getResource("jmfregistry.title"));
    setLayout(new BorderLayout());
    tabs = new TabControl();
    add(tabs, "Center");
    panelOther = new SettingsPanel();
    tabs.addPage(panelOther, JMFI18N.getResource("jmfregistry.settings"));
    panelCDM = new CDMPanel();
    tabs.addPage(panelCDM, JMFI18N.getResource("jmfregistry.capture"));
    panelPIM = new PIMPanel();
    tabs.addPage(panelPIM, JMFI18N.getResource("jmfregistry.plugins"));
    panelMime = new MimePanel();
    tabs.addPage(panelMime, JMFI18N.getResource("jmfregistry.mimetypes"));
    panelPM = new PMPanel();
    tabs.addPage(panelPM, JMFI18N.getResource("jmfregistry.package"));
    setSize(700, 400);
  }

  public void windowClosing(WindowEvent event)
  {
    setVisible(false);
  }

  public static void main(String args[])
  {
    JMFRegistry jmfr = new JMFRegistry();
    jmfr.addWindowListener(new WindowAdapter()
    {

      public void windowClosing(WindowEvent event)
      {
        event.getWindow().dispose();
        System.exit(0);
      }

    });
    jmfr.setVisible(true);
  }

}
