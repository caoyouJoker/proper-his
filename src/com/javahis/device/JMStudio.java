package com.javahis.device;

import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Codec;
import javax.media.Control;
import javax.media.Format;
import javax.media.PlugInManager;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableException;
import javax.media.bean.playerbean.MediaPlayer;
import javax.media.control.FormatControl;
import javax.media.control.FrameGrabbingControl;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.FormatChangeEvent;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.CaptureDevice;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.rtp.RTPStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SendStream;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.util.BufferToImage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jmapps.jmstudio.AboutDialog;
import jmapps.jmstudio.CaptureControlsDialog;
import jmapps.jmstudio.CaptureDialog;
import jmapps.jmstudio.OpenRtpDialog;
import jmapps.jmstudio.OpenUrlDialog;
import jmapps.jmstudio.SaveAsDialog;
import jmapps.jmstudio.TransmitWizard;
import jmapps.rtp.SessionControlDialog;
import jmapps.rtp.TransmissionStatsDialog;
import jmapps.ui.ImageArea;
import jmapps.ui.JMDialog;
import jmapps.ui.JMFrame;
import jmapps.ui.MessageDialog;
import jmapps.ui.PlayerFrame;
import jmapps.ui.SnapFrame;
import jmapps.ui.VideoPanel;
import jmapps.ui.WizardDialog;
import jmapps.util.CDSWrapper;
import jmapps.util.JMAppsCfg;
import jmapps.util.JMFUtils;

import com.dongyang.ui.event.BaseEvent;
import com.sun.media.rtp.RTPSessionMgr;
import com.sun.media.util.JMFI18N;
public class JMStudio
    extends PlayerFrame
    implements ItemListener, ReceiveStreamListener
{
  private Menu menuRecentUrl;
  private Menu menuAE;
  private Menu menuVE;
  private MenuItem menuFileClose;
  private MenuItem menuFileExport;
  private MenuItem menuCapture;
  private CheckboxMenuItem menuAutoPlay;
  private CheckboxMenuItem menuAutoLoop;
  private CheckboxMenuItem menuKeepAspect;
  private MenuItem menuFullScreen;
  private MenuItem menuSnapShot;
  private MenuItem menuPlugins;
  private MenuItem menuCaptureControl;
  private MenuItem menuRtpSessionControl;
  private MenuItem menuTransmissionStats;
  private Dimension dimFrameSizeBeforeFullScreen;
  private Window windowFullScreen;
  private MouseListener listenerMouseFullScreen;
  private Control controlPlugins;
  private Component componentPlugins;
  private FrameGrabbingControl controlGrabber;
  private FileDialog dlgOpenFile;
  private JMFRegistry jmfRegistry;
  private Vector vectorRtpFrames;
  private SnapFrame frameSnap;
  private TransmissionStatsDialog dlgTransmissionStats;
  private SessionControlDialog dlgSessionControl;
  private String strOptionalTitle;
  private DataSource dataSourceCurrent;
  private String nameCaptureDeviceAudio;
  private String nameCaptureDeviceVideo;
  private String audioEffect;
  private String videoEffect;
  private CaptureControlsDialog dlgCaptureControls;
  private RTPSessionMgr mngrSessionRtp;
  private Vector vectorMngrSessions;
  private Vector vectorStreams;
  private Vector vectorStreamLabels;
  boolean killed;
  boolean recvRTP;
  public static final String APPNAME = "JavaHis视频工具";//JMFI18N.getResource("jmstudio.appname");
  private static final String MENU_FILE = JMFI18N.getResource(
      "jmstudio.menu.file");
  private static final String MENU_FILE_NEWWINDOW = JMFI18N.getResource(
      "jmstudio.menu.file.newwindow");
  private static final String MENU_FILE_OPENFILE = JMFI18N.getResource(
      "jmstudio.menu.file.openfile");
  private static final String MENU_FILE_OPENURL = JMFI18N.getResource(
      "jmstudio.menu.file.openurl");
  private static final String MENU_FILE_OPENRTP = JMFI18N.getResource(
      "jmstudio.menu.file.openrtp");
  private static final String MENU_FILE_CAPTURE = JMFI18N.getResource(
      "jmstudio.menu.file.capture");
  private static final String MENU_FILE_RECENTURL = JMFI18N.getResource(
      "jmstudio.menu.file.recent");
  private static final String MENU_FILE_CLOSE = JMFI18N.getResource(
      "jmstudio.menu.file.close");
  private static final String MENU_FILE_EXPORT = JMFI18N.getResource(
      "jmstudio.menu.file.export");
  private static final String MENU_FILE_TRANSMIT = JMFI18N.getResource(
      "jmstudio.menu.file.transmit");
  private static final String MENU_FILE_PREFERENCES = JMFI18N.getResource(
      "jmstudio.menu.file.preferences");
  private static final String MENU_FILE_EXIT = JMFI18N.getResource(
      "jmstudio.menu.file.exit");
  private static final String MENU_PLAYER = JMFI18N.getResource(
      "jmstudio.menu.player");
  private static final String MENU_PLAYER_AUTOPLAY = JMFI18N.getResource(
      "jmstudio.menu.player.autoplay");
  private static final String MENU_PLAYER_AUTOLOOP = JMFI18N.getResource(
      "jmstudio.menu.player.autoloop");
  private static final String MENU_PLAYER_KEEPASPECT = JMFI18N.getResource(
      "jmstudio.menu.player.keepaspect");
  private static final String MENU_PLAYER_FULLSCREEN = JMFI18N.getResource(
      "jmstudio.menu.player.fullscreen");
  private static final String MENU_PLAYER_SNAPSHOT = JMFI18N.getResource(
      "jmstudio.menu.player.snapshot");
  private static final String MENU_PLAYER_PLUGINS = JMFI18N.getResource(
      "jmstudio.menu.player.plugins");
  private static final String MENU_PLAYER_CAPTURE = JMFI18N.getResource(
      "jmstudio.menu.player.capturecontrols");
  private static final String MENU_PLAYER_RTPSESSION = JMFI18N.getResource(
      "jmstudio.menu.player.rtpsession");
  private static final String MENU_PLAYER_TRANSMISSION = JMFI18N.getResource(
      "jmstudio.menu.player.transmission");
  private static final String MENU_HELP = JMFI18N.getResource(
      "jmstudio.menu.help");
  private static final String MENU_HELP_ABOUT = JMFI18N.getResource(
      "jmstudio.menu.help.about");
  private static Vector vectorFrames = new Vector();
  private static JMAppsCfg cfgJMApps = null;
  private static double dDefaultScale = 1.0D;
  private String hostAddress;
  private String port;
  RTPTimer rtptimer;
  public static String fileName = "";
  BaseEvent listner=new BaseEvent();
  class EffectListener
      implements ItemListener
  {

    public void itemStateChanged(ItemEvent ie)
    {
      boolean state = mi.getState();
      Menu menu = (Menu) mi.getParent();
      for (int i = 0; i < menu.getItemCount(); i++)
        if (menu.getItem(i) != mi)
          ( (CheckboxMenuItem) menu.getItem(i)).setState(false);

      if (!state)
        ( (CheckboxMenuItem) menu.getItem(0)).setState(true);
      String name = mi.getName();
      if (name == null || name.length() < 1)
        name = null;
      if (menu == menuAE)
        audioEffect = name;
      else
        videoEffect = name;
    }

    CheckboxMenuItem mi;

    public EffectListener(CheckboxMenuItem mi)
    {
      this.mi = mi;
    }
  }

  class WaitOnVis
      extends Thread
  {

    public void run()
    {
      Component compVis = vp.getVisualComponent();
      if (compVis != null)
        for (; !compVis.isVisible(); System.err.println("sleeping"))
          try
          {
            Thread.sleep(10L);
          }
          catch (InterruptedException ie)
          {}

      mp.prefetch();
    }

    VideoPanel vp;
    MediaPlayer mp;

    public WaitOnVis(VideoPanel vp, MediaPlayer mp)
    {
      this.vp = vp;
      this.mp = mp;
    }
  }

  class RTPTimer
      extends Thread
  {

    public void run()
    {
      MessageDialog dlg = null;
      String answer = "";
      java.awt.Image image = null;
      try
      {
        Thread.sleep(7000L);
      }
      catch (InterruptedException ie)
      {
        return;
      }
      if (!killed && !recvRTP)
      {
        image = ImageArea.loadImage("iconInfo.gif");
        dlg = new MessageDialog(outer, "Waiting for data", "7 seconds elasped",
                                image, false, false);
        dlg.setLocationCenter();
        dlg.show();
      }
      int count;
      for (count = 7;
           !killed && !recvRTP && !Thread.currentThread().isInterrupted() &&
           count < 60; )
      {
        try
        {
          Thread.sleep(1000L);
        }
        catch (InterruptedException ie)
        {
          if (dlg != null)
          {
            dlg.dispose();
            dlg = null;
          }
          return;
        }
        count++;
        String newtime = (new Integer(count)).toString() + " seconds elasped";
        if (dlg != null)
          dlg.getTextView().setText(newtime);
      }

      if (!killed && !recvRTP && !Thread.currentThread().isInterrupted())
        answer = MessageDialog.createYesNoDialog(outer, "Waing for data",
            "You have been waiting for 60 secs. Continue to wait?");
      if (!killed && !recvRTP && !Thread.currentThread().isInterrupted() &&
          answer.equals("No"))
      {
        if (dlg != null)
        {
          dlg.dispose();
          dlg = null;
        }
        outer.killCurrentPlayer();
        return;
      }
      count = 60;
      while (!killed && !recvRTP && !Thread.currentThread().isInterrupted())
      {
        try
        {
          Thread.sleep(1000L);
        }
        catch (InterruptedException ie)
        {
          if (dlg != null)
          {
            dlg.dispose();
            dlg = null;
          }
          return;
        }
        count++;
        String newtime = (new Integer(count)).toString() + " seconds elasped";
        if (dlg != null)
          dlg.getTextView().setText(newtime);
      }
    }

    JMStudio outer;

    public RTPTimer(JMStudio outer)
    {
      this.outer = null;
      this.outer = outer;
    }
  }

  public JMStudio()
  {
    super(null, APPNAME);
    dimFrameSizeBeforeFullScreen = null;
    windowFullScreen = null;
    controlPlugins = null;
    componentPlugins = null;
    controlGrabber = null;
    dlgOpenFile = null;
    jmfRegistry = null;
    vectorRtpFrames = null;
    frameSnap = null;
    dlgTransmissionStats = null;
    dlgSessionControl = null;
    strOptionalTitle = "";
    dataSourceCurrent = null;
    nameCaptureDeviceAudio = null;
    nameCaptureDeviceVideo = null;
    audioEffect = null;
    videoEffect = null;
    dlgCaptureControls = null;
    mngrSessionRtp = null;
    vectorMngrSessions = null;
    vectorStreams = null;
    vectorStreamLabels = null;
    killed = false;
    recvRTP = false;
    rtptimer = null;
    updateMenu();
    killed = false;
    recvRTP = false;
    JPanel panel=new JPanel();
    JLabel ll = new JLabel();
    JButton jbutton=new JButton();
    jbutton.addActionListener(new java.awt.event.ActionListener(){
    public void actionPerformed(ActionEvent e)
        {
        	System.out.println("1");
          Buffer bufferFrame = controlGrabber.grabFrame();
          System.out.println("2");
          if (bufferFrame == null)
            return;
          System.out.println("3");
          VideoFormat format = (VideoFormat) bufferFrame.getFormat();
          System.out.println("4");
          if (format == null)
            return;
          System.out.println("5");
          RGBFormat vf;
          Dimension size = format.getSize();
          RGBFormat prefFormat = new RGBFormat(size, size.width * size.height,
                                               Format.intArray,
                                               format.getFrameRate(), 32, -1, -1,
                                               -1, 1, -1, 0, -1);
          int outputData[];
          System.out.println("6");
          if (format.matches(prefFormat))
          {
            outputData = (int[]) bufferFrame.getData();
            vf = (RGBFormat) bufferFrame.getFormat();
          }
          else
          {
            Codec codec = findCodec(format, prefFormat);
            Buffer outputBuffer = new Buffer();
            int retVal = codec.process(bufferFrame, outputBuffer);
            if (retVal != 0)
              return;
            outputData = (int[]) outputBuffer.getData();
            vf = (RGBFormat) outputBuffer.getFormat();
          }
          VideoData data = new VideoData(vf.getRedMask(),vf.getGreenMask(),vf.getBlueMask(),
                                         size.width,size.height);
          System.out.println("7");
          data.setData(outputData);
          Image image = createImage1(data);
          System.out.println("fileName="+fileName);
          JMCamera a = new JMCamera(image,JMStudio.this,fileName);
          a.setVisible(true);
          //videoData(vf, size, outputData);
          //killCurrentPlayer();
          //dispose();
        }
    });

    jbutton.setSize(30, 40);
    jbutton.setText("1拍照");
    ll.setText("aa");
    panel.add(jbutton);

    this.add(panel,java.awt.BorderLayout.EAST);
  }
  class AAA extends JFrame
  {
    Image image;
    public AAA(Image image)
    {
      this.image = image;
    }
    public void paint(Graphics g)
   {
     g.drawImage(image, 0, 30,this);

     BufferedImage tag = new BufferedImage(image.getWidth(this), image.getHeight(this),
                                                 BufferedImage.TYPE_INT_RGB);
           Graphics g1 = tag.getGraphics();
           g1.drawImage(image, 0, 0, null); // 绘制缩小后的图
           g1.dispose();
           try{
             ImageIO.write(tag, "JPEG", new File("c:\\aaa.jpg")); // 输出到文件流
           }catch(Exception e)
           {

           }


   }
   public void update(Graphics g) {
       paint(g);
   }

  }
  public Image createImage1(VideoData data)
  {
    DirectColorModel dcm = new DirectColorModel(32, data.getRed(), data.getGreen(),
                                                data.getBlue());
    MemoryImageSource sourceImage = new MemoryImageSource(data.getWidth(),
        data.getHeight(), dcm, data.getData(), 0, data.getWidth());
    return Toolkit.getDefaultToolkit().createImage(sourceImage);
  }
  private Codec findCodec(VideoFormat input, VideoFormat output)
  {
      Vector codecList = PlugInManager.getPlugInList(input, output, 2);
      if(codecList == null || codecList.size() == 0)
          return null;
      for(int i = 0; i < codecList.size(); i++)
      {
          String codecName = (String)codecList.elementAt(i);
          Class codecClass = null;
          Codec codec = null;
          try
          {
              codecClass = Class.forName(codecName);
              if(codecClass != null)
                  codec = (Codec)codecClass.newInstance();
          }
          catch(ClassNotFoundException cnfe) { }
          catch(IllegalAccessException iae) { }
          catch(InstantiationException ie) { }
          catch(ClassCastException cce) { }
          if(codec != null && codec.setInputFormat(input) != null)
          {
              Format outputs[] = codec.getSupportedOutputFormats(input);
              if(outputs != null && outputs.length != 0)
              {
                  for(int j = 0; j < outputs.length; j++)
                      if(outputs[j].matches(output))
                      {
                          Format out = codec.setOutputFormat(outputs[j]);
                          if(out != null && out.matches(output))
                              try
                              {
                                  codec.open();
                                  return codec;
                              }
                              catch(ResourceUnavailableException rue) { }
                      }

              }
          }
      }

      return null;
  }

  public void addNotify()
  {
    super.addNotify();
  }

  public void pack()
  {
    super.pack();
  }

  protected void initFrame()
  {
    createMenu();
    super.initFrame();
  }

  private void createMenu()
  {
    MenuBar menu = new MenuBar();
    setMenuBar(menu);
    Menu menuFile = new Menu(MENU_FILE);
    menu.add(menuFile);
    MenuShortcut shortcut = new MenuShortcut(78);
    MenuItem itemMenu = new MenuItem(MENU_FILE_NEWWINDOW, shortcut);
    itemMenu.addActionListener(this);
    //menuFile.add(itemMenu);
    shortcut = new MenuShortcut(79);
    itemMenu = new MenuItem(MENU_FILE_OPENFILE, shortcut);
    itemMenu.addActionListener(this);
    //menuFile.add(itemMenu);
    shortcut = new MenuShortcut(85);
    itemMenu = new MenuItem(MENU_FILE_OPENURL, shortcut);
    itemMenu.addActionListener(this);
    //menuFile.add(itemMenu);
    shortcut = new MenuShortcut(82);
    itemMenu = new MenuItem(MENU_FILE_OPENRTP, shortcut);
    itemMenu.addActionListener(this);
    //menuFile.add(itemMenu);
    shortcut = new MenuShortcut(80);
    menuCapture = new MenuItem(MENU_FILE_CAPTURE, shortcut);
    menuCapture.addActionListener(this);
    menuFile.add(menuCapture);
    Vector vector = CaptureDeviceManager.getDeviceList(null);
    if (vector == null || vector.size() < 1)
      menuCapture.setEnabled(false);
    else
      menuCapture.setEnabled(true);
    menuRecentUrl = new Menu(MENU_FILE_RECENTURL);
    //updateRecentUrlMenu();
    //menuFile.add(menuRecentUrl);
    shortcut = new MenuShortcut(87);
    menuFileClose = new MenuItem(MENU_FILE_CLOSE, shortcut);
    menuFileClose.addActionListener(this);
    menuFile.add(menuFileClose);
    menuFile.addSeparator();
    shortcut = new MenuShortcut(69);
    menuFileExport = new MenuItem(MENU_FILE_EXPORT, shortcut);
    menuFileExport.addActionListener(this);
    //menuFile.add(menuFileExport);
    shortcut = new MenuShortcut(84);
    itemMenu = new MenuItem(MENU_FILE_TRANSMIT, shortcut);
    itemMenu.addActionListener(this);
    //menuFile.add(itemMenu);
    menuFile.addSeparator();
    itemMenu = new MenuItem(MENU_FILE_PREFERENCES);
    itemMenu.addActionListener(this);
    //menuFile.add(itemMenu);
    shortcut = new MenuShortcut(88);
    itemMenu = new MenuItem(MENU_FILE_EXIT, shortcut);
    itemMenu.addActionListener(this);
    menuFile.add(itemMenu);
    Menu menuPlayer = new Menu(MENU_PLAYER);
    //menu.add(menuPlayer);
    addNotify();
    boolean boolValue;
    if (cfgJMApps != null)
      boolValue = cfgJMApps.getAutoPlay();
    else
      boolValue = false;
    menuAutoPlay = new CheckboxMenuItem(MENU_PLAYER_AUTOPLAY, boolValue);
    menuAutoPlay.addItemListener(this);
    menuPlayer.add(menuAutoPlay);
    if (cfgJMApps != null)
      boolValue = cfgJMApps.getAutoLoop();
    else
      boolValue = true;
    menuAutoLoop = new CheckboxMenuItem(MENU_PLAYER_AUTOLOOP, boolValue);
    menuAutoLoop.addItemListener(this);
    menuPlayer.add(menuAutoLoop);
    if (cfgJMApps != null)
      boolValue = cfgJMApps.getKeepAspectRatio();
    else
      boolValue = false;
    menuKeepAspect = new CheckboxMenuItem(MENU_PLAYER_KEEPASPECT, boolValue);
    menuKeepAspect.addItemListener(this);
    menuPlayer.add(menuKeepAspect);
    menuPlayer.addSeparator();
    shortcut = new MenuShortcut(70);
    menuFullScreen = new MenuItem(MENU_PLAYER_FULLSCREEN, shortcut);
    menuFullScreen.addActionListener(this);
    menuPlayer.add(menuFullScreen);
    shortcut = new MenuShortcut(83);
    menuSnapShot = new MenuItem(MENU_PLAYER_SNAPSHOT, shortcut);
    menuSnapShot.addActionListener(this);
    menuPlayer.add(menuSnapShot);
    menuPlayer.addSeparator();
    menuPlugins = new MenuItem(MENU_PLAYER_PLUGINS);
    menuPlugins.addActionListener(this);
    menuPlayer.add(menuPlugins);
    menuCaptureControl = new MenuItem(MENU_PLAYER_CAPTURE);
    menuCaptureControl.addActionListener(this);
    menuPlayer.add(menuCaptureControl);
    menuRtpSessionControl = new MenuItem(MENU_PLAYER_RTPSESSION);
    menuRtpSessionControl.addActionListener(this);
    menuPlayer.add(menuRtpSessionControl);
    menuTransmissionStats = new MenuItem(MENU_PLAYER_TRANSMISSION);
    menuTransmissionStats.addActionListener(this);
    menuPlayer.add(menuTransmissionStats);
    menuPlayer.addSeparator();
    Vector videoEffects = getEffectList(new VideoFormat(null));
    Vector audioEffects = getEffectList(new AudioFormat(null));
    if (videoEffects.size() > 0)
    {
      menuVE = new Menu("Insert Video Effect");
      fillEffectList(menuVE, videoEffects);
      menuPlayer.add(menuVE);
    }
    if (audioEffects.size() > 0)
    {
      menuAE = new Menu("Insert Audio Effect");
      fillEffectList(menuAE, audioEffects);
      menuPlayer.add(menuAE);
    }
    Menu menuHelp = new Menu(MENU_HELP);
    //menu.add(menuHelp);
    shortcut = new MenuShortcut(72);
    itemMenu = new MenuItem(MENU_HELP_ABOUT, shortcut);
    itemMenu.addActionListener(this);
    menuHelp.add(itemMenu);
  }

  public void actionPerformed(ActionEvent event)
  {
    String strCmd = event.getActionCommand();
    Object objSource = event.getSource();
    if (strCmd == null && (objSource instanceof MenuItem))
      strCmd = ( (MenuItem) objSource).getActionCommand();
    if (strCmd == null)
      return;
    if (strCmd.equals(MENU_FILE_NEWWINDOW))
      createNewFrame();
    else
    if (strCmd.equals(MENU_FILE_OPENFILE))
      openFile();
    else
    if (strCmd.equals(MENU_FILE_OPENURL))
      openUrl();
    else
    if (strCmd.equals(MENU_FILE_OPENRTP))
      openRtp();
    else
    if (strCmd.equals(MENU_FILE_CAPTURE))
      captureMedia();
    else
    if (strCmd.equals(MENU_FILE_RECENTURL))
    {
      if (objSource instanceof MenuItem)
      {
        String nameUrl = ( (MenuItem) objSource).getLabel();
        open(nameUrl);
      }
    }
    else
    if (strCmd.equals(MENU_FILE_CLOSE))
      killCurrentPlayer();
    else
    if (strCmd.equals(MENU_FILE_EXPORT))
      exportMedia();
    else
    if (strCmd.equals(MENU_FILE_TRANSMIT))
      transmitMedia();
    else
    if (strCmd.equals(MENU_FILE_PREFERENCES))
    {
      if (jmfRegistry == null)
        jmfRegistry = new JMFRegistry();
      jmfRegistry.setVisible(true);
      jmfRegistry.addWindowListener(new WindowAdapter()
      {

        public void windowClosing(WindowEvent event)
        {
          Vector vector = CaptureDeviceManager.getDeviceList(null);
          if (vector == null || vector.size() < 1)
            menuCapture.setEnabled(false);
          else
            menuCapture.setEnabled(true);
        }

      });
    }
    else
    if (strCmd.equals(MENU_FILE_EXIT))
      closeAll();
    else
    if (!strCmd.equals(MENU_PLAYER_AUTOPLAY) &&
        !strCmd.equals(MENU_PLAYER_AUTOLOOP) &&
        !strCmd.equals(MENU_PLAYER_KEEPASPECT))
      if (strCmd.equals(MENU_PLAYER_FULLSCREEN))
        setFullScreen(true);
      else
      if (strCmd.equals(MENU_PLAYER_SNAPSHOT))
        doSnapShot();
      else
      if (strCmd.equals(MENU_PLAYER_PLUGINS))
      {
        if (componentPlugins != null)
          componentPlugins.setVisible(true);
        else
        if (controlPlugins != null && (controlPlugins instanceof Component))
        {
          componentPlugins = (Component) controlPlugins;
          componentPlugins.setVisible(true);
          for (Component component = componentPlugins; component != null;
               component = component.getParent())
          {
            if (! (component instanceof Frame))
              continue;
            Frame frame = (Frame) component;
            JMFrame.autoPosition(frame, this);
            break;
          }

        }
      }
      else
      if (strCmd.equals(MENU_PLAYER_CAPTURE))
      {
        if (dlgCaptureControls != null)
        {
          dlgCaptureControls.setVisible(true);
          dlgCaptureControls.toFront();
        }
      }
      else
      if (strCmd.equals(MENU_PLAYER_RTPSESSION))
      {
        if (dlgSessionControl != null)
        {
          dlgSessionControl.setVisible(true);
          dlgSessionControl.toFront();
        }
      }
      else
      if (strCmd.equals(MENU_PLAYER_TRANSMISSION))
      {
        if (dlgTransmissionStats != null)
        {
          dlgTransmissionStats.setVisible(true);
          dlgTransmissionStats.toFront();
        }
      }
      else
      if (strCmd.equals(MENU_HELP_ABOUT))
        AboutDialog.createDialog(this);
      else
        super.actionPerformed(event);
  }

  public void itemStateChanged(ItemEvent event)
  {
    Object objSource = event.getSource();
    if (objSource == menuAutoPlay)
    {
      if (cfgJMApps != null)
        cfgJMApps.setAutoPlay(menuAutoPlay.getState());
    }
    else
    if (objSource == menuAutoLoop)
    {
      if (mediaPlayerCurrent != null)
        mediaPlayerCurrent.setPlaybackLoop(menuAutoLoop.getState());
      if (cfgJMApps != null)
        cfgJMApps.setAutoLoop(menuAutoLoop.getState());
    }
    else
    if (objSource == menuKeepAspect)
    {
      if (mediaPlayerCurrent != null)
      {
        mediaPlayerCurrent.setFixedAspectRatio(menuKeepAspect.getState());
        if (panelVideo != null)
          panelVideo.resizeVisualComponent();
      }
      if (cfgJMApps != null)
        cfgJMApps.setKeepAspectRatio(menuKeepAspect.getState());
    }
  }

  public void windowClosing(WindowEvent event)
  {
    closeAll();
  }

  public void windowClosed(WindowEvent event)
  {
    super.windowClosed(event);
    if (frameSnap != null)
      frameSnap.dispose();
    if (vectorFrames.contains(this))
    {
      Point pointLocation = getLocation();
      int nIndex = vectorFrames.indexOf(this);
      if (cfgJMApps != null)
        cfgJMApps.setJMStudioFrameLocation(pointLocation, nIndex);
      vectorFrames.removeElement(this);
    }
    //if (vectorFrames.size() < 1)
      //exitApllication();
  }

  public synchronized void update(ReceiveStreamEvent event)
  {
    if (event instanceof NewReceiveStreamEvent)
    {
      recvRTP = true;
      RTPSessionMgr mngrSession = (RTPSessionMgr) event.getSource();
      javax.media.rtp.ReceiveStream stream = ( (NewReceiveStreamEvent) event).
          getReceiveStream();
      DataSource dataSource = stream.getDataSource();
      strOptionalTitle = hostAddress + ":" + port;
      mngrSessionRtp = null;
      if (vectorRtpFrames != null && vectorMngrSessions != null &&
          vectorMngrSessions.size() > 0 &&
          vectorMngrSessions.firstElement() == mngrSession)
      {
        PlayerFrame frame = new PlayerFrame(this, strOptionalTitle);
        vectorRtpFrames.addElement(frame);
        frame.open(dataSource);
        frame.setVisible(true);
      }
      else
      {
        open(dataSource, false);
        vectorMngrSessions = new Vector();
        vectorMngrSessions.addElement(mngrSession);
        vectorStreams = new Vector();
        vectorStreams.addElement(stream);
        dlgSessionControl = new SessionControlDialog(this, mngrSession);
        updateMenu();
        vectorRtpFrames = new Vector();
      }
    }
  }

  protected void processRealizeComplete(RealizeCompleteEvent event)
  {
    killCurrentView();
    setCursor(cursorNormal);
    panelVideo = new VideoPanel(mediaPlayerCurrent);
    panelVideo.setZoom(dDefaultScale);
    panelVideo.addMenuZoomActionListener(this);
    compControl = mediaPlayerCurrent.getControlPanelComponent();
    Dimension dimVideo = panelVideo.getPreferredSize();
    if (compControl != null)
    {
      Dimension dimControlPanel = compControl.getPreferredSize();
      compControl.setBounds(0, dimVideo.height, dimVideo.width,
                            dimControlPanel.height);
    }
    panelContent.add(panelVideo, "Center");
    if (compControl != null)
    {
      panelContent.add(compControl, "South");
      compControl.repaint();
    }
    controlPlugins = mediaPlayerCurrent.getControl("com.sun.media.JMD");
    controlGrabber = (FrameGrabbingControl) mediaPlayerCurrent.getControl(
        "javax.media.control.FrameGrabbingControl");
    Component compVis = panelVideo.getVisualComponent();
    if (compVis != null)
      while (!compVis.isVisible())
        try
        {
          Thread.sleep(10L);
        }
        catch (InterruptedException ie)
        {}
    mediaPlayerCurrent.prefetch();
    String strMediaLocation = mediaPlayerCurrent.getMediaLocation();
    if (strMediaLocation == null || strMediaLocation.trim().length() < 1)
      strMediaLocation = strOptionalTitle;
    setTitle(strMediaLocation + " - " + APPNAME);
    updateMenu();
    //==========================================//
    //com.kernel.client.SessionManager.getManager().
    //    getSoundSession().setFrameGrabbingControl(controlGrabber);

    //==========================================//
  }

  protected void processPrefetchComplete(PrefetchCompleteEvent event)
  {
    if (menuAutoPlay.getState() && mediaPlayerCurrent != null &&
        mediaPlayerCurrent.getTargetState() != 600)
      mediaPlayerCurrent.start();
  }

  protected void processFormatChange(FormatChangeEvent event)
  {
    killCurrentView();
    panelVideo = new VideoPanel(mediaPlayerCurrent);
    panelVideo.setZoom(dDefaultScale);
    panelVideo.addMenuZoomActionListener(this);
    panelContent.add(panelVideo, "Center");
    compControl = mediaPlayerCurrent.getControlPanelComponent();
    if (compControl != null)
      panelContent.add(compControl, "South");
  }

  private void openFile()
  {
    String nameFile = null;
    if (dlgOpenFile == null)
      dlgOpenFile = new FileDialog(this, MENU_FILE_OPENFILE, 0);
    if (cfgJMApps != null)
      nameFile = cfgJMApps.getLastOpenFile();
    if (nameFile != null)
      dlgOpenFile.setFile(nameFile);
    dlgOpenFile.show();
    nameFile = dlgOpenFile.getFile();
    if (nameFile == null)
      return;
    nameFile = dlgOpenFile.getDirectory() + nameFile;
    if (cfgJMApps != null)
      cfgJMApps.setLastOpenFile(nameFile);
    String nameUrl = "file:" + nameFile;
    open(nameUrl);
  }

  private void openUrl()
  {
    String nameUrl = null;
    if (cfgJMApps != null)
      nameUrl = cfgJMApps.getLastOpenUrl();
    OpenUrlDialog dlgOpenUrl = new OpenUrlDialog(this, nameUrl);
    dlgOpenUrl.show();
    String strAction = dlgOpenUrl.getAction();
    if (!strAction.equals(JMDialog.ACTION_OPEN))
      return;
    nameUrl = dlgOpenUrl.getUrl();
    if (nameUrl == null)
      return;
    if (cfgJMApps != null)
      cfgJMApps.setLastOpenUrl(nameUrl);
    open(nameUrl);
  }

  private void openRtp()
  {
    OpenRtpDialog dlgOpenRtp = new OpenRtpDialog(this, cfgJMApps);
    dlgOpenRtp.show();
    String strAction = dlgOpenRtp.getAction();
    if (!strAction.equals(JMDialog.ACTION_OPEN))
      return;
    String strAddress = dlgOpenRtp.getAddress();
    String strPort = dlgOpenRtp.getPort();
    String strTtl = dlgOpenRtp.getTtl();
    hostAddress = strAddress;
    port = strPort;
    killCurrentPlayer();
    rtptimer = new RTPTimer(this);
    killed = false;
    recvRTP = false;
    rtptimer.start();
    mngrSessionRtp = JMFUtils.createSessionManager(strAddress, strPort, strTtl, this);
    if (mngrSessionRtp == null)
    {
      MessageDialog.createErrorDialog(this,
                                      JMFI18N.getResource("jmstudio.error.sessionmngr.create"));
      killed = true;
      if (rtptimer != null && rtptimer.isAlive())
      {
        rtptimer.interrupt();
        rtptimer = null;
      }
      return;
    }
    else
    {
      updateMenu();
      return;
    }
  }

  public void open(String nameUrl)
  {
    MediaPlayer mediaPlayer = JMFUtils.createMediaPlayer(nameUrl, this,
        audioEffect, videoEffect);
    dataSourceCurrent = null;
    boolean boolResult = open(mediaPlayer, true);
    if (boolResult)
      addToRecentUrlList(nameUrl);
  }

  public void open(DataSource dataSource)
  {
    open(dataSource, true);
  }

  public void open(DataSource dataSource, boolean killPrevious)
  {
    MediaPlayer mediaPlayer = JMFUtils.createMediaPlayer(dataSource, this);
    boolean boolResult = open(mediaPlayer, killPrevious);
    if (boolResult)
      dataSourceCurrent = dataSource;
  }

  public boolean open(MediaPlayer mediaPlayer)
  {
    return open(mediaPlayer, true);
  }

  public boolean open(MediaPlayer mediaPlayer, boolean killPrevious)
  {
    if (mediaPlayer == null)
      return false;
    if (killPrevious)
      killCurrentPlayer();
    setCursor(cursorWait);
    mediaPlayerCurrent = mediaPlayer;
    killed = false;
    mediaPlayer.setPlaybackLoop(menuAutoLoop.getState());
    mediaPlayer.setFixedAspectRatio(menuKeepAspect.getState());
    mediaPlayer.setPopupActive(false);
    mediaPlayer.setControlPanelVisible(false);
    mediaPlayer.addControllerListener(this);
    mediaPlayer.realize();
    updateMenu();
    return true;
  }

  private void exportMediaOld()
  {
    AudioFormat formatAudioDevice = null;
    VideoFormat formatVideoDevice = null;
    String nameUrl = mediaPlayerCurrent.getMediaLocation();
    if (dataSourceCurrent != null &&
        (dataSourceCurrent instanceof CaptureDevice))
    {
      FormatControl fcs[] = ( (CaptureDevice) dataSourceCurrent).
          getFormatControls();
      for (int i = 0; i < fcs.length; i++)
      {
        Format format = fcs[i].getFormat();
        if (format instanceof AudioFormat)
          formatAudioDevice = (AudioFormat) format;
        else
        if (format instanceof VideoFormat)
          formatVideoDevice = (VideoFormat) format;
      }

    }
    SaveAsDialog saveasdialog;
    if (nameUrl != null && nameUrl.length() > 1 && closeCapture())
      if (nameCaptureDeviceAudio != null || nameCaptureDeviceVideo != null)
      {
        DataSource dataSource = JMFUtils.createCaptureDataSource(
            nameCaptureDeviceAudio, formatAudioDevice, nameCaptureDeviceVideo,
            formatVideoDevice);
        if (dataSource == null)
          System.err.println("DataSource is null");
        SaveAsDialog dlgSaveAs = new SaveAsDialog(this, dataSource, cfgJMApps);
      }
      else
      {
        saveasdialog = new SaveAsDialog(this, nameUrl, null, cfgJMApps);
      }
  }

  private void exportMedia()
  {
    AudioFormat formatAudioDevice = null;
    VideoFormat formatVideoDevice = null;
    String nameUrl = mediaPlayerCurrent.getMediaLocation();
    SaveAsDialog dlgSaveAs;
    if (dataSourceCurrent != null && (dataSourceCurrent instanceof CDSWrapper))
    {
      DataSource dataSource = dataSourceCurrent;
      dataSourceCurrent = null;
      killCurrentPlayer();
      dlgSaveAs = new SaveAsDialog(this, dataSource, cfgJMApps);
    }
    else
    if (nameUrl != null && nameUrl.trim().length() > 0)
      dlgSaveAs = new SaveAsDialog(this, nameUrl, null, cfgJMApps);
    else
    if (vectorMngrSessions.size() > 0 && vectorStreams.size() > 0)
    {
      RTPSessionMgr mngrSession = (RTPSessionMgr) vectorMngrSessions.
          firstElement();
      InetAddress addrInet = null;
      try
      {
        addrInet = InetAddress.getByName(hostAddress);
      }
      catch (UnknownHostException e)
      {
        e.printStackTrace();
      }
      nameUrl = "rtp://" + hostAddress + ":" + port;
      Control arrControls[] = mediaPlayerCurrent.getControls();
      int nCount = arrControls.length;
      for (int i = 0; i < nCount; i++)
      {
        if (! (arrControls[i] instanceof TrackControl))
          continue;
        Format format = ( (TrackControl) arrControls[i]).getFormat();
        if (format instanceof AudioFormat)
        {
          nameUrl = nameUrl + "/audio";
          break;
        }
        if (! (format instanceof VideoFormat))
          continue;
        nameUrl = nameUrl + "/video";
        break;
      }

      if (!addrInet.isMulticastAddress())
      {
        String strResult = MessageDialog.createOKCancelDialog(this,
            JMFI18N.getResource("jmstudio.query.savertp.unicast"));
        if (!strResult.equals(JMDialog.ACTION_OK))
          return;
        killCurrentPlayer();
      }
      SaveAsDialog saveasdialog = new SaveAsDialog(this, nameUrl, null,
          cfgJMApps);
    }
  }

  private void captureMedia()
  {
    nameCaptureDeviceAudio = null;
    nameCaptureDeviceVideo = null;
    CaptureDialog dialogCapture = new CaptureDialog(this, cfgJMApps);
    dialogCapture.show();
    if (dialogCapture.getAction() == JMDialog.ACTION_CANCEL)
      return;
    CaptureDeviceInfo cdi = dialogCapture.getAudioDevice();
    if (cdi != null && dialogCapture.isAudioDeviceUsed())
      nameCaptureDeviceAudio = cdi.getName();
    cdi = dialogCapture.getVideoDevice();
    if (cdi != null && dialogCapture.isVideoDeviceUsed())
      nameCaptureDeviceVideo = cdi.getName();
    DataSource dataSource = JMFUtils.createCaptureDataSource(
        nameCaptureDeviceAudio, dialogCapture.getAudioFormat(),
        nameCaptureDeviceVideo, dialogCapture.getVideoFormat());
    if (dataSource != null)
    {
      if ( (dataSource instanceof CaptureDevice) &&
          (dataSource instanceof PushBufferDataSource))
      {
        DataSource cdswrapper = new CDSWrapper( (PushBufferDataSource)
                                               dataSource);
        dataSource = cdswrapper;
        try
        {
          cdswrapper.connect();
        }
        catch (IOException ioe)
        {
          dataSource = null;
          nameCaptureDeviceAudio = null;
          nameCaptureDeviceVideo = null;
          MessageDialog.createErrorDialog(this,
                                          JMFI18N.getResource("jmstudio.error.captureds"));
        }
      }
      open(dataSource);
      if (dataSource != null)
      {
        dlgCaptureControls = new CaptureControlsDialog(this, dataSource);
        if (dlgCaptureControls.isEmpty())
          dlgCaptureControls = null;
      }
    }
    else
    {
      nameCaptureDeviceAudio = null;
      nameCaptureDeviceVideo = null;
      MessageDialog.createErrorDialog(this,
                                      JMFI18N.getResource("jmstudio.error.captureds"));
    }
  }

  private void openCapture()
  {
    nameCaptureDeviceAudio = null;
    nameCaptureDeviceVideo = null;
    CaptureDialog dialogCapture = new CaptureDialog(this, cfgJMApps);
    //dialogCapture.show();
    //if (dialogCapture.getAction() == JMDialog.ACTION_CANCEL)
    //  return;
    CaptureDeviceInfo cdi = dialogCapture.getAudioDevice();
    //if (cdi != null && dialogCapture.isAudioDeviceUsed())
    //  nameCaptureDeviceAudio = cdi.getName();
    cdi = dialogCapture.getVideoDevice();
    if (cdi != null && dialogCapture.isVideoDeviceUsed())
      nameCaptureDeviceVideo = cdi.getName();
    DataSource dataSource = JMFUtils.createCaptureDataSource(
        nameCaptureDeviceAudio, dialogCapture.getAudioFormat(),
        nameCaptureDeviceVideo, dialogCapture.getVideoFormat());
    if (dataSource != null)
    {
      if ( (dataSource instanceof CaptureDevice) &&
          (dataSource instanceof PushBufferDataSource))
      {
        DataSource cdswrapper = new CDSWrapper( (PushBufferDataSource)
                                               dataSource);
        dataSource = cdswrapper;
        try
        {
          cdswrapper.connect();
        }
        catch (IOException ioe)
        {
          dataSource = null;
          nameCaptureDeviceAudio = null;
          nameCaptureDeviceVideo = null;
          MessageDialog.createErrorDialog(this,
                                          JMFI18N.getResource("jmstudio.error.captureds"));
        }
      }
      open(dataSource);
      if (dataSource != null)
      {
        dlgCaptureControls = new CaptureControlsDialog(this, dataSource);
        if (dlgCaptureControls.isEmpty())
          dlgCaptureControls = null;
      }
    }
    else
    {
      nameCaptureDeviceAudio = null;
      nameCaptureDeviceVideo = null;
      MessageDialog.createErrorDialog(this,
                                      JMFI18N.getResource("jmstudio.error.captureds"));
    }
  }

  private void transmitMedia()
  {
    String urlString = null;
    DataSource dataSource = null;
    if (dataSourceCurrent != null && (dataSourceCurrent instanceof CDSWrapper))
    {
      dataSource = dataSourceCurrent;
      dataSourceCurrent = null;
      killCurrentPlayer();
      urlString = "Capture";
    }
    else
    if (mediaPlayerCurrent != null)
      urlString = mediaPlayerCurrent.getMediaLocation();
    TransmitWizard dlgTransmit = new TransmitWizard(this, urlString, dataSource,
        cfgJMApps);
    dlgTransmit.show();
    String strAction = dlgTransmit.getAction();
    if (!strAction.equals(WizardDialog.ACTION_FINISH))
      return;
    javax.media.Processor processorTransmit = dlgTransmit.getProcessor();
    if (processorTransmit == null)
      return;
    strOptionalTitle = JMFI18N.getResource("jmstudio.playerwindow.transcoding");
    MediaPlayer mediaPlayer = JMFUtils.createMediaPlayer(processorTransmit, this);
    boolean boolResult = open(mediaPlayer);
    if (boolResult)
    {
      vectorMngrSessions = dlgTransmit.getMngrSessions();
      vectorStreams = dlgTransmit.getStreams();
      vectorStreamLabels = dlgTransmit.getStreamLabels();
      dlgTransmissionStats = new TransmissionStatsDialog(this,
          vectorMngrSessions, vectorStreamLabels);
      updateMenu();
    }
  }

  private void setFullScreen(boolean boolFullScreen)
  {
    if (panelVideo == null)
      return;
    if (boolFullScreen && panelVideo.getParent() != windowFullScreen)
    {
      dimFrameSizeBeforeFullScreen = getSize();
      Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
      if (windowFullScreen == null)
      {
        windowFullScreen = new Window(this);
        windowFullScreen.setLayout(null);
        windowFullScreen.setBackground(Color.black);
      }
      windowFullScreen.setBounds(0, 0, dimScreen.width, dimScreen.height);
      panelContent.remove(panelVideo);
      Dimension dimPrefSize = panelVideo.getPreferredSize();
      if (compControl != null)
        panelContent.remove(compControl);
      Rectangle rectVideo = new Rectangle(0, 0, dimScreen.width,
                                          dimScreen.height);
      if ( (float) dimPrefSize.width / (float) dimPrefSize.height >=
          (float) dimScreen.width / (float) dimScreen.height)
      {
        rectVideo.height = (dimPrefSize.height * dimScreen.width) /
            dimPrefSize.width;
        rectVideo.y = (dimScreen.height - rectVideo.height) / 2;
      }
      else
      {
        rectVideo.width = (dimPrefSize.width * dimScreen.height) /
            dimPrefSize.height;
        rectVideo.x = (dimScreen.width - rectVideo.width) / 2;
      }
      Toolkit.getDefaultToolkit().sync();
      windowFullScreen.add(panelVideo);
      windowFullScreen.setVisible(true);
      panelVideo.setBounds(rectVideo);
      windowFullScreen.validate();
      listenerMouseFullScreen = new MouseAdapter()
      {

        public void mouseClicked(MouseEvent event)
        {
          setFullScreen(false);
        }

      };
      panelVideo.getVisualComponent().addMouseListener(listenerMouseFullScreen);
    }
    else
    if (!boolFullScreen && panelVideo.getParent() == windowFullScreen)
    {
      panelVideo.getVisualComponent().removeMouseListener(
          listenerMouseFullScreen);
      Toolkit.getDefaultToolkit().sync();
      windowFullScreen.setVisible(false);
      windowFullScreen.remove(panelVideo);
      panelContent.add(panelVideo, "Center");
      if (compControl != null)
        panelContent.add(compControl, "South");
      if (dimFrameSizeBeforeFullScreen != null)
      {
        setSize(dimFrameSizeBeforeFullScreen);
        validate();
      }
      setVisible(true);
    }
  }
//====================================//
  private void doSnapShot()
  {
    Buffer bufferFrame = controlGrabber.grabFrame();
    BufferToImage bufferToImage = new BufferToImage( (VideoFormat) bufferFrame.
        getFormat());
    java.awt.Image image = bufferToImage.createImage(bufferFrame);
    if (image == null)
      return;
    if (frameSnap == null)
      frameSnap = new SnapFrame(image, this);
    else
      frameSnap.setImage(image);
    frameSnap.setTitle(getTitle() + " - " +
                       JMFI18N.getResource("jmstudio.snapshot"));
  }
//====================================//

  protected void killCurrentView()
  {
    if (componentPlugins != null)
    {
      componentPlugins.setVisible(false);
      componentPlugins = null;
    }
    controlGrabber = null;
    super.killCurrentView();
  }

  protected void killCurrentPlayer()
  {
    nameCaptureDeviceAudio = null;
    nameCaptureDeviceVideo = null;
    if (dlgCaptureControls != null)
      dlgCaptureControls.dispose();
    dlgCaptureControls = null;
    if (dlgTransmissionStats != null)
      dlgTransmissionStats.dispose();
    dlgTransmissionStats = null;
    if (dlgSessionControl != null)
      dlgSessionControl.dispose();
    dlgSessionControl = null;
    if (vectorRtpFrames != null)
    {
      int nCount = vectorRtpFrames.size();
      for (int i = 0; i < nCount; i++)
      {
        PlayerFrame frame = (PlayerFrame) vectorRtpFrames.elementAt(i);
        frame.dispose();
      }

      vectorRtpFrames.removeAllElements();
      vectorRtpFrames = null;
    }
    if (vectorStreams != null)
    {
      int nCount = vectorStreams.size();
      for (int i = 0; i < nCount; i++)
      {
        RTPStream streamRtp = (RTPStream) vectorStreams.elementAt(i);
        if (streamRtp instanceof SendStream)
          ( (SendStream) streamRtp).close();
      }

      vectorStreams.removeAllElements();
      vectorStreams = null;
    }
    if (vectorMngrSessions != null)
    {
      int nCount = vectorMngrSessions.size();
      for (int i = 0; i < nCount; i++)
      {
        RTPSessionMgr mngrSession = (RTPSessionMgr) vectorMngrSessions.
            elementAt(i);
        if (mngrSessionRtp == mngrSession)
          mngrSessionRtp = null;
        mngrSession.removeTargets("Transmission terminated.");
        mngrSession.dispose();
      }

      vectorMngrSessions.removeAllElements();
      vectorMngrSessions = null;
    }
    if (mngrSessionRtp != null)
    {
      mngrSessionRtp.removeTargets("Transmission terminated.");
      mngrSessionRtp.dispose();
      mngrSessionRtp = null;
    }
    super.killCurrentPlayer();
    if (dataSourceCurrent != null && (dataSourceCurrent instanceof CDSWrapper))
      ( (CDSWrapper) dataSourceCurrent).close();
    dataSourceCurrent = null;
    setTitle(APPNAME);
    killed = true;
    if (rtptimer != null && rtptimer.isAlive())
    {
      rtptimer.interrupt();
      rtptimer = null;
    }
    updateMenu();
    //==========================================//
    //com.kernel.client.SessionManager.getManager().
    //    getSoundSession().setFrameGrabbingControl(null);

//==========================================//

  }

  private boolean closeCapture()
  {
    if (mediaPlayerCurrent == null)
      return true;
    if (dataSourceCurrent != null)
    {
      String strMessage = JMFI18N.getResource(
          "jmstudio.query.erooropencapture.closepreview");
      String strAction = MessageDialog.createOKCancelDialog(this, strMessage);
      if (strAction != null && strAction.equals(JMDialog.ACTION_OK))
      {
        killCurrentPlayer();
        return true;
      }
      else
      {
        return false;
      }
    }
    else
    {
      return true;
    }
  }

  public void updateMenu()
  {
    boolean boolEnable = mediaPlayerCurrent != null;
    menuFileExport.setEnabled(boolEnable);
    menuFileClose.setEnabled(boolEnable || mngrSessionRtp != null ||
                             vectorMngrSessions != null &&
                             !vectorMngrSessions.isEmpty());
    menuKeepAspect.setEnabled(boolEnable && panelVideo != null &&
                              panelVideo.getVisualComponent() != null);
    menuFullScreen.setEnabled(boolEnable && panelVideo != null &&
                              panelVideo.getVisualComponent() != null);
    menuSnapShot.setEnabled(boolEnable && controlGrabber != null);
    menuPlugins.setEnabled(boolEnable && controlPlugins != null);
    menuCaptureControl.setEnabled(boolEnable && dlgCaptureControls != null);
    menuTransmissionStats.setEnabled(boolEnable && dlgTransmissionStats != null);
    menuRtpSessionControl.setEnabled(boolEnable && dlgSessionControl != null);
  }

  private void addToRecentUrlList(String strUrl)
  {
    if (cfgJMApps == null)
      return;
    int nPos = strUrl.lastIndexOf(".");
    String strUrlType;
    if (strUrl.substring(0, 4).equalsIgnoreCase("rtp:"))
      strUrlType = "RTP";
    else
    if (nPos < 1 || nPos == strUrl.length() - 1)
      strUrlType = "Other";
    else
      strUrlType = strUrl.substring(nPos + 1).toUpperCase();
    cfgJMApps.addRecentUrls(strUrlType, strUrl);
    updateRecentUrlMenu();
  }

  private void updateRecentUrlMenu()
  {
    if (cfgJMApps == null)
      return;
    menuRecentUrl.removeAll();
    Enumeration enumUrlTypes = cfgJMApps.getRecentUrlTypes();
    if (enumUrlTypes == null)
      return;
    while (enumUrlTypes.hasMoreElements())
    {
      Object objUrlType = enumUrlTypes.nextElement();
      Menu menuUrlType = new Menu(objUrlType.toString());
      menuRecentUrl.add(menuUrlType);
      Vector vectorUrls = cfgJMApps.getRecentUrls(objUrlType.toString());
      if (vectorUrls != null)
      {
        int nSize = vectorUrls.size();
        for (int i = 0; i < nSize; i++)
        {
          Object objUrl = vectorUrls.elementAt(i);
          MenuItem menuItem = new MenuItem(objUrl.toString());
          menuItem.setActionCommand(MENU_FILE_RECENTURL);
          menuItem.addActionListener(this);
          menuUrlType.add(menuItem);
        }

      }
    }
  }

  static void initProps()
  {
    Properties props = new Properties(System.getProperties());
    props = new Properties(props);
    String sep = File.separator;
    File theUserPropertiesFile = new File(System.getProperty("user.home") + sep +
                                          ".hotjava" + sep + "properties");
    System.out.println(System.getProperty("user.home") + sep +
                                          ".hotjava" + sep + "properties");
    try
    {
      FileInputStream in = new FileInputStream(theUserPropertiesFile);
      props.load(new BufferedInputStream(in));
      in.close();
    }
    catch (Exception e)
    {}
    System.setProperties(props);
  }
  public static  JMStudio openCamera(String filename)
  {
    if(cfgJMApps == null)
      cfgJMApps = new JMAppsCfg();
  fileName = filename;

    JMStudio jmStudio = createNewFrame();
    jmStudio.openCapture();
    return jmStudio;
  }
  public static void main(String args[])
  {
    /*MessageDialog.titleDefault = APPNAME;
    cfgJMApps = new JMAppsCfg();
    try
    {
      initProps();
    }
    catch (Throwable t)
    {
      System.out.println(
          "Unable to read Http Proxy information from the appletviewer settings");
    }
    for (int i = 0; i < args.length; i++)
      if (args[i].equals("-x"))
      {
        if (args.length > i + 1)
          try
          {
            dDefaultScale = Double.valueOf(args[i + 1]).doubleValue();
            i++;
          }
          catch (Exception exception)
          {
            dDefaultScale = 1.0D;
          }
      }
      else
      {
        String strMedia = args[i];
        if (strMedia.indexOf(":") < 2)
        {
          if (strMedia.indexOf("/") != 0)
            strMedia = "/" + strMedia;
          strMedia = "file:" + strMedia;
        }
        JMStudio jmStudio = createNewFrame();
        jmStudio.open(strMedia);
      }

    JMStudio jmstudio;
    if (vectorFrames.size() < 1)
      jmstudio = createNewFrame();*/
    openCamera("c:\\aaa.jpg");
  }

  public static JMStudio createNewFrame()
  {
    JMStudio jmStudio = new JMStudio();
    if (cfgJMApps != null)
    {
      int nIndex = vectorFrames.size();
      Point point = cfgJMApps.getJMStudioFrameLocation(nIndex);
      Dimension dim = jmStudio.getSize();
      Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
      if (point.x + dim.width > dimScreen.width)
        point.x = dimScreen.width - dim.width;
      if (point.y + dim.height > dimScreen.height)
        point.y = dimScreen.height - dim.height;
      jmStudio.setLocation(point);
      jmStudio.setVisible(true);
      jmStudio.invalidate();
      jmStudio.pack();
    }
    vectorFrames.addElement(jmStudio);
    return jmStudio;
  }

  public static void closeAll()
  {
    for (int i = vectorFrames.size(); i > 0; )
    {
      i--;
      JMStudio jmStudio = (JMStudio) vectorFrames.elementAt(i);
      jmStudio.killCurrentPlayer();
      jmStudio.dispose();
    }

  }

  public static void exitApllication()
  {
    cleanUp();
    System.exit(0);
  }

  private static void cleanUp()
  {
    if (cfgJMApps != null)
      cfgJMApps.save();
  }

  private Vector getEffectList(Format format)
  {
    Vector v = PlugInManager.getPlugInList(format, null, 3);
    return v;
  }

  private void fillEffectList(Menu menu, Vector list)
  {
    boolean first = true;
    for (int i = 0; i < list.size(); i++)
    {
      String className = (String) list.elementAt(i);
      try
      {
        Class cClass = Class.forName(className);
        Codec codec = (Codec) cClass.newInstance();
        String name = codec.getName();
        CheckboxMenuItem mi;
        if (first)
        {
          first = false;
          mi = new CheckboxMenuItem("None");
          mi.setName("");
          menu.add(mi);
          mi.setState(true);
          addEffectListener(mi);
        }
        mi = new CheckboxMenuItem(name);
        mi.setName(className);
        menu.add(mi);
        mi.setState(false);
        addEffectListener(mi);
      }
      catch (Throwable t)
      {}
    }
  }
  //取得照片
  public void camera(Image image)
  {
	  listner.callEvent("onCameraed",new Object[]{image},new String[]{"java.awt.Image"});
  }
  private void addEffectListener(CheckboxMenuItem mi)
  {
    mi.addItemListener(new EffectListener(mi));
  }
  /**
   * 加载监听事件
   * @param eventName String
   * @param object Object
   * @param methodName String
   */
  public void addListener(String eventName, Object object, String methodName) {
	  listner.add(eventName, object, methodName);
  }


  /**
   * 移除监听事件
   * @param eventName String
   * @param object Object
   * @param methodName String
   */
  public void removeListener(String eventName, Object object, String methodName) {
	  listner.remove(eventName, object, methodName);
  }


  /**
   * 得到监听
   * @return BaseListener
   */
  public BaseEvent getListener() {
    return this.listner;
  }

}
