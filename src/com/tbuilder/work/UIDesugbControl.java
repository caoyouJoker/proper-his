package com.tbuilder.work;

import com.dongyang.control.TControl;
import com.dongyang.config.TConfigParse;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TSelectBlock;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JEditorPane;

public class UIDesugbControl extends TControl{
    TSelectBlock selectBlock = new TSelectBlock();
    TPanel workPanel = new TPanel();
    JEditorPane keyListener = new JEditorPane();
    TConfigParse.TObject tobject;
    /**
     * 更新UI
     * @param object TObject
     */
    public void updateUI(TConfigParse.TObject object)
    {
        this.tobject = object;
        workPanel.callFunction("setConfigParm",getConfigParm());
        workPanel.setPreferredSize(new Dimension(1500,1500));
        workPanel.setBKColor("灰50");
        workPanel.setLayout("null");
        workPanel.setRefetchTopMenu(true);
        workPanel.setRefetchToolBar(true);
        callFunction("UI|addItem",workPanel);
        TComponent component = loadObject(object);
        if(component == null)
            return;
        component.callFunction("loadTObject",object);
        component.callFunction("setX",10);
        component.callFunction("setY",10);
        workPanel.addItem(component);
        initKeyListener();
        //启动UI适配器
        component.callFunction("startUIAdapter",true);
        /*
                 if("TMenuBar".equalsIgnoreCase(type))
                 {
                     TPanel panel = new TPanel();
                     panel.setWidth(300);
                     panel.setHeight(100);
                     panel.setLayout(new java.awt.BorderLayout());

                     return panel;
                 }
        */
    }
    /**
     * 加载对象
     * @param object TObject
     * @return TComponent
     */
    public TComponent loadObject(TConfigParse.TObject object)
    {
        String type = object.getType();
        if(type == null)
            return null;
        if("TFrame".equalsIgnoreCase(type))
            type = "TPanel";
        Object obj = getConfigParm().loadObject(type);
        if(obj == null)
            return null;
        if(!(obj instanceof TComponent))
            return null;
        TComponent component = (TComponent)obj;
        component.setTag(object.getTag());
        component.callFunction("setConfigParm",getConfigParm());
        return component;
    }
    /**
     * 初始化键盘监听对象
     */
    public void initKeyListener()
    {
        keyListener.setLocation(10,1400);
        keyListener.setSize(200,25);
        workPanel.addItem(keyListener);
        workPanel.addMouseListener(new MouseAdapter()
            {
                public void mouseEntered(MouseEvent e) {
                    keyListener.grabFocus();
                }
            });
        keyListener.grabFocus();
        keyListener.addKeyListener(tobject.getView().getKeyAdapter());
    }
    /**
     * 键盘监听组件得到焦点
     */
    public void onKeyGrabFocus()
    {
        keyListener.grabFocus();
    }
    /**
     * 选择语种
     */
    public void onChangeLanguage(String language)
    {
        workPanel.changeLanguage(language);
    }
}
