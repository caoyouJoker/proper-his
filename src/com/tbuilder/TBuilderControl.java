package com.tbuilder;

import com.dongyang.config.TConfigParse;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.*;

public class TBuilderControl extends TControl
{

    public TBuilderControl()
    {
    }

    public void onInit()
    {
        super.onInit();
        addEventListener("ProjectTree->tree.doubleClicked", "onProjectTreeDoubleClicked");
        addEventListener("WorkRight->tabbedPane.stateChanged", "onTabStateChanged");
        addEventListener("StructureTree->tree.clicked", "onStructureTreeClicked");
        onInitProjectTree();
    }

    public void setCurrEditTag(String currEditTag)
    {
        this.currEditTag = currEditTag;
    }

    public String getCurrEditTag()
    {
        return currEditTag;
    }

    public void onInitProjectTree()
    {
        out("begin");
        TTreeNode root = (TTreeNode)callMessage("UI|ProjectTree|getRoot");
        root.setText("JavaHis");
        root.setType("Root");
        root.removeAllChildren();
        TTreeNode clientConfig = new TTreeNode("Client Config", "Path");
        clientConfig.setValue("%ROOT%\\config");
        root.add(clientConfig);
        downloadProjectTree(clientConfig, "%ROOT%\\config", "UI");
        TTreeNode serverConfig = new TTreeNode("Server Config", "Path");
        serverConfig.setValue("%ROOT%\\WEB-INF\\config");
        root.add(serverConfig);
        downloadServerProjectTree(serverConfig, "%ROOT%\\WEB-INF\\config");
        callMessage("UI|ProjectTree|update");
        out("begin");
    }

    public TTree getProjectTree()
    {
        return (TTree)callFunction("UI|ProjectTree|getThis", new Object[0]);
    }

    public void downloadProjectTree(TTreeNode root, String dir, String type)
    {
        if(root == null)
            return;
        String dirs[] = TIOM_AppServer.listDir(dir);
        for(int i = 0; i < dirs.length; i++)
        {
            TTreeNode node = new TTreeNode(dirs[i], "Path");
            node.setValue((new StringBuilder()).append(dir).append("\\").append(dirs[i]).toString());
            downloadProjectTree(node, (new StringBuilder()).append(dir).append("\\").append(dirs[i]).toString(), type);
            root.add(node);
        }

        String files[] = TIOM_AppServer.listFile(dir);
        for(int i = 0; i < files.length; i++)
        {
            TTreeNode node = new TTreeNode(files[i], type);
            node.setID((new StringBuilder()).append(dir).append("\\").append(files[i]).toString());
            root.add(node);
        }

    }

    public void downloadServerProjectTree(TTreeNode root, String dir)
    {
        if(root == null)
            return;
        String dirs[] = TIOM_AppServer.listDir(dir);
        for(int i = 0; i < dirs.length; i++)
        {
            if("system".equals(dirs[i]))
            {
                TTreeNode node = new TTreeNode(dirs[i], "System");
                node.setValue((new StringBuilder()).append(dir).append("\\").append(dirs[i]).toString());
                downloadSystemTree(node, (new StringBuilder()).append(dir).append("\\").append(dirs[i]).toString());
                root.add(node);
            }
            if("module".equals(dirs[i]))
            {
                TTreeNode node = new TTreeNode(dirs[i], "Path");
                node.setValue((new StringBuilder()).append(dir).append("\\").append(dirs[i]).toString());
                downloadProjectTree(node, (new StringBuilder()).append(dir).append("\\").append(dirs[i]).toString(), "Module");
                root.add(node);
            }
        }

        String files[] = TIOM_AppServer.listFile(dir);
        for(int i = 0; i < files.length; i++)
        {
            String type = "Module";
            String name = files[i];
            TTreeNode node = new TTreeNode(name, type);
            node.setID((new StringBuilder()).append(dir).append("\\").append(files[i]).toString());
            root.add(node);
        }

    }

    public void downloadSystemTree(TTreeNode root, String dir)
    {
        TTreeNode dbnode = new TTreeNode("DataBase", "DataBase");
        String name = (new StringBuilder()).append(dir).append("\\TDBInfo.x").toString();
        dbnode.setID(name);
        downloadDBTree(dbnode, name);
        root.add(dbnode);
    }

    public void downloadDBTree(TTreeNode root, String filename)
    {
        String data = new String(TIOM_AppServer.readFile(filename));
        TConfigParse tConfig = new TConfigParse(data, "Pools");
        String mainLink = tConfig.getRoot().getValue("MainPool");
        int count = tConfig.getRoot().getItemCount();
        for(int i = 0; i < count; i++)
        {
            String tag = tConfig.getRoot().getItem(i).getTag();
            String type = mainLink.equals(tag) ? "Main_DBLink" : "DBLink";
            TTreeNode dbnode = new TTreeNode(tag, type);
            dbnode.setID(filename);
            root.add(dbnode);
        }

    }

    public void onProjectTreeDoubleClicked(Object parm)
    {
        TTreeNode node = (TTreeNode)parm;
        if(node == null)
            return;
        if("Path".equals(node.getType()))
            return;
        String fileName = node.getID();
        String name = node.getText();
        TParm inParm = new TParm();
        inParm.setData("FILE_NAME", fileName);
        inParm.setData("NAME", name);
        if("UI".equals(node.getType()))
        {
            if(fileName.toUpperCase().endsWith(".XML"))
                callFunction("UI|WorkRight|openPanel", new Object[] {
                    fileName, getConfigString("DataWindowPanelConfig"), inParm
                });
            else
            if(fileName.toUpperCase().endsWith(".o"))
                callFunction("UI|WorkRight|openPanel", new Object[] {
                    fileName, getConfigString("UIObjectConfig"), inParm
                });
            else
                callFunction("UI|WorkRight|openPanel", new Object[] {
                    fileName, getConfigString("UIPanelConfig"), inParm
                });
        } else
        if("Module".equals(node.getType()))
            callFunction("UI|WorkRight|openPanel", new Object[] {
                fileName, getConfigString("ModulePanelConfig"), inParm
            });
    }

    public void onStructureTreeClicked()
    {
        if(getCurrEditTag() == null)
        {
            return;
        } else
        {
            callFunction((new StringBuilder()).append("UI|").append(getCurrEditTag()).append("|onStructureTreeClicked").toString(), new Object[0]);
            return;
        }
    }

    public void onTabStateChanged(Object parm)
    {
        TComponent component = (TComponent)parm;
        if(component == null)
        {
            return;
        } else
        {
            String title = getConfigString("Title");
            setCurrEditTag(component.getTag());
            callFunction("UI|setTitle", new Object[] {
                (new StringBuilder()).append(title).append(" - ").append(getCurrEditTag()).toString()
            });
            callFunction((new StringBuilder()).append("UI|").append(getCurrEditTag()).append("|onInitStructureTree").toString(), new Object[0]);
            return;
        }
    }

    public void onNewProject()
    {
        out("begin");
        Object value = openDialog(getConfigString("NewPojectDialogConfig"));
        if(value == null)
        {
            return;
        } else
        {
            messageBox(value.toString());
            out("end");
            return;
        }
    }

    public void onClosePanel()
    {
        if(getCurrEditTag() == null)
            return;
        callFunction("UI|WorkRight|closePanel", new Object[] {
            getCurrEditTag()
        });
        TComponent component = (TComponent)callFunction("UI|WorkRight|getSelectedComponent", new Object[0]);
        if(component == null)
        {
            return;
        } else
        {
            setCurrEditTag(component.getTag());
            return;
        }
    }

    public void onResetAction()
    {
        TIOM_AppServer.resetAction();
    }

    public void onProjectTreeRefurbish()
    {
        onInitProjectTree();
    }

    public void onCreateDir()
    {
        TTreeNode node = getProjectTree().getSelectionNode();
        if(node == null)
            return;
        String value = (String)openDialog(getConfigString("NewDirDialogConfig"), node.getValue());
        if(value == null || value.length() == 0)
        {
            return;
        } else
        {
            String dir = (new StringBuilder()).append(node.getValue()).append("\\").append(value).toString();
            TIOM_AppServer.mkdir(dir);
            TTreeNode newnode = new TTreeNode(value, "Path");
            newnode.setValue(dir);
            node.add(newnode);
            getProjectTree().update();
            return;
        }
    }

    public void onCreateUI()
    {
        TTreeNode node = getProjectTree().getSelectionNode();
        if(node == null)
            return;
        String value = (String)openDialog(getConfigString("NewUIDialogConfig"), node.getValue());
        if(value == null || value.length() == 0)
        {
            return;
        } else
        {
            TTreeNode newnode = new TTreeNode(value, "UI");
            newnode.setID((new StringBuilder()).append(node.getValue()).append("\\").append(value).toString());
            node.add(newnode);
            getProjectTree().update();
            return;
        }
    }

    public void onCreateDBLink()
    {
        TTreeNode node = getProjectTree().getSelectionNode();
        if(node == null)
        {
            return;
        } else
        {
            String id = node.getID();
            return;
        }
    }

    public void onProjectTreeExpand()
    {
        TTreeNode node = getProjectTree().getSelectionNode();
        if(node == null)
        {
            return;
        } else
        {
            getProjectTree().expandRow(node);
            return;
        }
    }

    public void onProjectTreeCollapse()
    {
        TTreeNode node = getProjectTree().getSelectionNode();
        if(node == null)
        {
            return;
        } else
        {
            getProjectTree().collapseRow(node);
            return;
        }
    }

    public static final String PROJECT_TREE = "ProjectTree";
    public static final String NEW_POJECT_DIALOG_CONFIG = "NewPojectDialogConfig";
    public static final String NEW_DIR_DIALOG_CONFIG = "NewDirDialogConfig";
    public static final String NEW_UI_DIALOG_CONFIG = "NewUIDialogConfig";
    public static final String WORK_TAB = "WorkRight";
    public static final String STRUCTURE_TREE = "StructureTree";
    private String currEditTag;

}
