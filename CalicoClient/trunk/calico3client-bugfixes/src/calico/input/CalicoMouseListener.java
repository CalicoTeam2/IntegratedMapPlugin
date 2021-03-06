package calico.input;

import calico.*;
import calico.inputhandlers.CalicoInputManager;
import calico.inputhandlers.InputEventInfo;

import java.awt.Component;
import java.awt.event.*;

public class CalicoMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener
{
	public class MouseInfo
	{
		public boolean leftPressed = false;
		public boolean rightPressed = false;
		public boolean middlePressed = false;
	}
	
	private MouseInfo mouseInfo = new MouseInfo();
	
	/*
	java.awt.event.MouseEvent[
		MOUSE_PRESSED,
		(84,142),
		button=1,
		modifiers=Button1,
		extModifiers=Button1,
		clickCount=1] on calico.components.CCanvas[,0,0,800x600,alignmentX=0.0,alignmentY=0.0,border=,flags=16777224,maximumSize=,minimumSize=,preferredSize=java.awt.Dimension[width=800,height=600]]

	DEBUG] java.awt.event.MouseEvent[MOUSE_DRAGGED,(232,169),button=1,modifiers=Button1,extModifiers=Button1,clickCount=1]
		on calico.components.CCanvas[,0,0,800x600,alignmentX=0.0,alignmentY=0.0,border=,flags=16777224,maximumSize=,minimumSize=,preferredSize=java.awt.Dimension[width=800,height=600]]



	 */

	public void mouseDragged(MouseEvent e)
	{
		CalicoInputManager.handleInput(new InputEventInfo(e.getPoint(), getButtonFromMouseEvent(e), e.getModifiers(), InputEventInfo.MOUSE_DRAGGED, getButtonMaskFromMouseInfo(mouseInfo)));
    }


	public void mousePressed(MouseEvent e)
	{
		if(e.getButton()==MouseEvent.BUTTON1)
		{
			mouseInfo.leftPressed = true;
		}
		else if(e.getButton()==MouseEvent.BUTTON2)
		{
			mouseInfo.middlePressed = true;
		}
		else if(e.getButton()==MouseEvent.BUTTON3)
		{
			mouseInfo.rightPressed = true;
		}
			
		//Calico.log_debug(e.toString());
		CalicoInputManager.handleInput(new InputEventInfo(e.getPoint(), getButtonFromMouseEvent(e), e.getModifiers(), InputEventInfo.MOUSE_PRESSED, getButtonMaskFromMouseInfo(mouseInfo)));
	}

	public void mouseReleased(MouseEvent e)
	{
		if(e.getButton()==MouseEvent.BUTTON1)
		{
			mouseInfo.leftPressed = false;
		}
		else if(e.getButton()==MouseEvent.BUTTON2)
		{
			mouseInfo.middlePressed = false;
		}
		else if(e.getButton()==MouseEvent.BUTTON3)
		{
			mouseInfo.rightPressed = false;
		}
		
		CalicoInputManager.handleInput(new InputEventInfo(e.getPoint(), getButtonFromMouseEvent(e), e.getModifiers(), InputEventInfo.MOUSE_RELEASED, getButtonMaskFromMouseInfo(mouseInfo)));
	}


	// IGNORED
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseMoved(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		Calico.logger.debug(e.toString());
		CalicoInputManager.handleInput(new InputEventInfo(e.getPoint(), e.getWheelRotation(), e.getScrollType(), e.getScrollAmount()));
	}
	
	private int getButtonFromMouseEvent(MouseEvent e)
	{
		int button = 0;
		if(e.getButton()==MouseEvent.BUTTON1)
		{
			button = InputEventInfo.BUTTON_LEFT;
		}
		else if(e.getButton()==MouseEvent.BUTTON2)
		{
			button = InputEventInfo.BUTTON_MIDDLE;
		}
		else if(e.getButton()==MouseEvent.BUTTON3)
		{
			button = InputEventInfo.BUTTON_RIGHT;
		}
		
		return button;
	}
	
	private int getButtonMaskFromMouseInfo(CalicoMouseListener.MouseInfo mouseInfo)
	{
		int buttonMask = 0;
		if(mouseInfo.leftPressed)
		{
			buttonMask = buttonMask | InputEventInfo.BUTTON_LEFT;
		}
		if(mouseInfo.rightPressed)
		{
			buttonMask = buttonMask | InputEventInfo.BUTTON_RIGHT;
		}
		if(mouseInfo.middlePressed)
		{
			buttonMask = buttonMask | InputEventInfo.BUTTON_MIDDLE;
		}
		return buttonMask;
	}
	
    /*
    public void mousePressed(MouseEvent e)
    {
        requestFocus();

        boolean shouldBalanceEvent = false;

        if (e.getButton() == MouseEvent.NOBUTTON)
        {
            if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK)
            {
                e = new MouseEvent((Component) e.getSource(), MouseEvent.MOUSE_PRESSED, e.getWhen(), e
                        .getModifiers(), e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger(),
                        MouseEvent.BUTTON1);
            }
            else if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) == MouseEvent.BUTTON2_MASK)
            {
                e = new MouseEvent((Component) e.getSource(), MouseEvent.MOUSE_PRESSED, e.getWhen(), e
                        .getModifiers(), e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger(),
                        MouseEvent.BUTTON2);
            }
            else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK)
            {
                e = new MouseEvent((Component) e.getSource(), MouseEvent.MOUSE_PRESSED, e.getWhen(), e
                        .getModifiers(), e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger(),
                        MouseEvent.BUTTON3);
            }
        }

        switch (e.getButton())
        {
            case MouseEvent.BUTTON1:
                if (isButton1Pressed)
                {
                    shouldBalanceEvent = true;
                }
                isButton1Pressed = true;
                break;

            case MouseEvent.BUTTON2:
                if (isButton2Pressed)
                {
                    shouldBalanceEvent = true;
                }
                isButton2Pressed = true;
                break;

            case MouseEvent.BUTTON3:
                if (isButton3Pressed)
                {
                    shouldBalanceEvent = true;
                }
                isButton3Pressed = true;
                break;
        }

        if (shouldBalanceEvent)
        {
            MouseEvent balanceEvent = new MouseEvent((Component) e.getSource(), MouseEvent.MOUSE_RELEASED,
                    e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getClickCount(), e
                            .isPopupTrigger(), e.getButton());
            sendInputEventToInputManager(balanceEvent, MouseEvent.MOUSE_RELEASED);
        }

        sendInputEventToInputManager(e, MouseEvent.MOUSE_PRESSED);
    }

    public void mouseReleased(MouseEvent e)
    {
        boolean shouldBalanceEvent = false;

        if (e.getButton() == MouseEvent.NOBUTTON)
        {
            if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK)
            {
                e = new MouseEvent((Component) e.getSource(), MouseEvent.MOUSE_RELEASED, e.getWhen(), e
                        .getModifiers(), e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger(),
                        MouseEvent.BUTTON1);
            }
            else if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) == MouseEvent.BUTTON2_MASK)
            {
                e = new MouseEvent((Component) e.getSource(), MouseEvent.MOUSE_RELEASED, e.getWhen(), e
                        .getModifiers(), e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger(),
                        MouseEvent.BUTTON2);
            }
            else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK)
            {
                e = new MouseEvent((Component) e.getSource(), MouseEvent.MOUSE_RELEASED, e.getWhen(), e
                        .getModifiers(), e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger(),
                        MouseEvent.BUTTON3);
            }
        }

        switch (e.getButton())
        {
            case MouseEvent.BUTTON1:
                if (!isButton1Pressed)
                {
                    shouldBalanceEvent = true;
                }
                isButton1Pressed = false;
                break;

            case MouseEvent.BUTTON2:
                if (!isButton2Pressed)
                {
                    shouldBalanceEvent = true;
                }
                isButton2Pressed = false;
                break;

            case MouseEvent.BUTTON3:
                if (!isButton3Pressed)
                {
                    shouldBalanceEvent = true;
                }
                isButton3Pressed = false;
                break;
        }

        if (shouldBalanceEvent)
        {
            MouseEvent balanceEvent = new MouseEvent((Component) e.getSource(), MouseEvent.MOUSE_PRESSED, e
                    .getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getClickCount(),
                    e.isPopupTrigger(), e.getButton());
            sendInputEventToInputManager(balanceEvent, MouseEvent.MOUSE_PRESSED);
        }

        sendInputEventToInputManager(e, MouseEvent.MOUSE_RELEASED);
    }
    */

}