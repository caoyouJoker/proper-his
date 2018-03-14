package com.javahis.ui.inv;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JLabel;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TPanel;
import com.dongyang.util.ImageTool;

public class DisplayPictureControl extends TControl {
	
	public void onInit(){
		Object obj = this.getParameter();
       if(obj!=null){
           TParm parm = (TParm)obj;
           this.viewPhoto(parm.getValue("PACK_CODE"));
       }
	}
	
	/**
	 * œ‘ æ’’∆¨
	 * 
	 * @param packCode
	 *            String ∞¸∫≈
	 */
	public void viewPhoto(String packCode) {

		String photoName = packCode + ".jpg";
		String fileName = photoName;
		try {
			TPanel viewPanel = (TPanel) getComponent("VIEW_PANEL");
			String root = TIOM_FileServer.getRoot();
			String dir = TIOM_FileServer.getPath("OpeInfPIC.ServerPath");
			dir = root + dir ;
			byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					dir + fileName);
			if (data == null) {
				viewPanel.removeAll();
				return;
			}
			double scale = 0.5;
			boolean flag = true;
			Image image = ImageTool.scale(data, scale, flag);
//			 ImageIcon image = TIOM_AppServer.getImage(dir+fileName);
			Pic pic = new Pic(image);
			pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
			pic.setLocation(0, 0);
			viewPanel.removeAll();
			viewPanel.add(pic);
			pic.repaint();
		} catch (Exception e) {
		}
	}
	
	class Pic extends JLabel {
		Image image;

		public Pic(Image image) {
			this.image = image;
		}

		public void paint(Graphics g) {
			g.setColor(new Color(161, 220, 230));
			g.fillRect(4, 15, 100, 100);
			if (image != null) {
				g.drawImage(image, 4, 15, null);

			}
		}
	}
	
	public TLabel getLabel(String tag){
		return (TLabel) this.getComponent(tag);
	}

}
