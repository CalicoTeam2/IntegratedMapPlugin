package calico.plugins.iip.inputhandlers;

import java.awt.Point;
import java.awt.geom.Point2D;

import calico.inputhandlers.CalicoAbstractInputHandler;
import calico.inputhandlers.InputEventInfo;
import calico.plugins.iip.components.graph.IntentionGraph;

/**
 * Custom <code>CalicoInputManager</code>handler for events related to open space in the Intention View. The main Calico
 * event handling mechanism will determine whether input relates to the view by calling
 * <code>IntentionalInterfacesPerspective.getEventTarget()</code>. When that method returns the Intention View, the
 * associated input event will be sent here.
 * 
 * The only supported operation is to pan the Intention View by dragging it.
 * 
 * @author Byron Hawkins
 */
public class IntentionGraphInputHandler extends CalicoAbstractInputHandler
{
	private enum State
	{
		IDLE,
		PAN;
	}

	private State state = State.IDLE;
	private Point lastMouse;

	@Override
	public void actionPressed(InputEventInfo event)
	{
		state = State.PAN;
		lastMouse = event.getGlobalPoint();
	}

	@Override
	public void actionReleased(InputEventInfo event)
	{
		state = State.IDLE;
	}

	@Override
	public void actionDragged(InputEventInfo event)
	{
		double xMouseDelta = event.getGlobalPoint().x - lastMouse.x;
		double yMouseDelta = event.getGlobalPoint().y - lastMouse.y;

		double scale = IntentionGraph.getInstance().getLayer(IntentionGraph.Layer.CONTENT).getScale();

		xMouseDelta /= scale;
		yMouseDelta /= scale;

		lastMouse = event.getGlobalPoint();

		IntentionGraph.getInstance().translate(xMouseDelta, yMouseDelta);
	}
}
