package calico.plugins.IntegratedMapPlugin.components.buttons;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import calico.Calico;
import calico.CalicoDraw;
import calico.components.menus.CanvasMenuButton;
import calico.controllers.CCanvasController;
import calico.controllers.CImageController;
import calico.inputhandlers.InputEventInfo;
import calico.networking.Networking;
import calico.plugins.IntegratedMapPlugin.IntegratedMapPlugin;
import calico.plugins.IntegratedMapPlugin.IntegratedMapPluginNetworkCommands;
import calico.plugins.IntegratedMapPlugin.components.IntegratedMap;
import calico.plugins.IntegratedMapPlugin.iconsets.CalicoIconManager;

public class CreateIntegratedMapButton extends CanvasMenuButton
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CreateIntegratedMapButton(long c)
	{
		super();
		cuid = c;
		
		iconString = "plugins.exampleplugin.pancakebunny.jpg";
		try
		{
//			Image img = CanvasGenericMenuBar.getTextImage("  UserList  ", 
//				new Font("Verdana", Font.BOLD, 12));
			setImage(CalicoIconManager.getIconImage(iconString));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void actionMouseClicked(InputEventInfo event)
	{	
		if (event.getAction() == InputEventInfo.ACTION_PRESSED)
		{
			super.onMouseDown();
		}
		else if (event.getAction() == InputEventInfo.ACTION_RELEASED && isPressed)
		{
			final JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new ImageFileFilter());
	        int returnVal = fc.showOpenDialog(CCanvasController.canvasdb.get(CCanvasController.getCurrentUUID()).getComponent());
	
	        if (returnVal == JFileChooser.APPROVE_OPTION) 
	        {
	            File file = fc.getSelectedFile();
	            //save image
	            long map_uuid = Calico.uuid();
	            String ext = CImageController.getFileExtension(file.getName());
	            byte[] bytes = CImageController.getBytesFromDisk(file);
	            try {
					CImageController.save_to_disk(map_uuid, map_uuid + "." + ext, bytes);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
//	    		p.rewind();
//	    		p.getInt();
//	    		long new_uuid=p.getLong();
//	    		long cuuid=p.getLong();
//	    		int x=p.getInt();
//	    		int y=p.getInt();
	            IntegratedMapPlugin.UI_send_command(
	            		IntegratedMapPluginNetworkCommands.CUSTOM_SCRAP_CREATE, map_uuid,CCanvasController.getCurrentUUID(),100,100);
//	            Networking.send(CImageController.getImageTransferPacket(Calico.uuid(), CCanvasController.getCurrentUUID(), 
//	            		50, 50, file));
	        }
		}
	}
	
	/**
	 * Part of scaffolding
	 */
	public void highlight_on()
	{
		if (!isPressed)
		{
			isPressed = true;
			setSelected(true);
			final CanvasMenuButton tempButton = this;
			SwingUtilities.invokeLater(
					new Runnable() { public void run() { 
						double tempX = tempButton.getX();
						double tempY = tempButton.getY();
								
						setImage(CalicoIconManager.getIconImage(iconString));
						tempButton.setX(tempX);
						tempButton.setY(tempY);
					}});
			//CalicoDraw.setNodeX(this, tempX);
			//CalicoDraw.setNodeY(this, tempY);
			//this.repaintFrom(this.getBounds(), this);
			CalicoDraw.repaintNode(this);
		}
	}
	
	/**
	 * Part of scaffolding
	 */
	public void highlight_off()
	{
		if (isPressed)
		{
			isPressed = false;
			setSelected(false);
			final CanvasMenuButton tempButton = this;
			SwingUtilities.invokeLater(
					new Runnable() { public void run() { 
						double tempX = tempButton.getX();
						double tempY = tempButton.getY();
								
						setImage(CalicoIconManager.getIconImage(iconString));
						tempButton.setX(tempX);
						tempButton.setY(tempY);
					}});
			//CalicoDraw.setNodeX(this, tempX);
			//CalicoDraw.setNodeY(this, tempY);
			//this.repaintFrom(this.getBounds(), this);
			CalicoDraw.repaintNode(this);
		}
	}		
	
	class ImageFileFilter extends javax.swing.filechooser.FileFilter 
	{
	    public boolean accept(File file) 
	    {
	    	if (file.isFile())
	    	{
    	        String filename = file.getName().toLowerCase();
    	        return filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".gif");
	    	}
	    	else
	    	{
	    		return true;
	    	}
	    }
	    
	    public String getDescription() 
	    {
	        return "*.png, *.jpg, *.gif";
	    }
	}
}
