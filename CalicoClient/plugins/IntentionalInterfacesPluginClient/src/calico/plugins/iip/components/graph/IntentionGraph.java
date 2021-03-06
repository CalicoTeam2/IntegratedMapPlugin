package calico.plugins.iip.components.graph;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import calico.Calico;
import calico.CalicoDataStore;
import calico.components.bubblemenu.BubbleMenu;
import calico.components.menus.CanvasMenuBar;
import calico.input.CalicoMouseListener;
import calico.inputhandlers.CalicoInputManager;
import calico.inputhandlers.InputEventInfo;
import calico.plugins.iip.components.CIntentionCell;
import calico.plugins.iip.components.menus.IntentionGraphMenuBar;
import calico.plugins.iip.controllers.CIntentionCellController;
import calico.plugins.iip.inputhandlers.IntentionGraphInputHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Visual container for the Intention View. The <code>contentCanvas</code> contains canvas thumbnails and arrows, and
 * the <code>canvas</code> contains tools and handlers and such things. The <code>menuBar</code> sits along the bottom
 * of the screen, and the <code>topology</code> renders the visible elements of the layout topology. Methods should be
 * self explanatory by name.
 * 
 * @author Byron Hawkins
 */
public class IntentionGraph
{
	/**
	 * Represents the 3 layers of the Intention View. The order and indexing is deliberate--changing it would probably
	 * break the entire view.
	 * 
	 * @author Byron Hawkins
	 */
	public enum Layer
	{
		TOPOLOGY(0),
		CONTENT(1),
		TOOLS(2);

		public final int id;

		private Layer(int id)
		{
			this.id = id;
		}
	}

	public static IntentionGraph getInstance()
	{
		if (INSTANCE == null)
		{
			new IntentionGraph();
		}
		return INSTANCE;
	}

	private static IntentionGraph INSTANCE;

	/**
	 * Piccolo layer containing the painted topology elements.
	 */
	private final PLayer topologyLayer = new PLayer();
	/**
	 * Piccolo layer containing the bubble and pie menus.
	 */
	private final PLayer toolLayer = new PLayer();

	/**
	 * Piccolo canvas containing the topology, tool and content layers. The content layer is constructed within
	 * <code>contentCanvas</code>, and then added to this <code>canvas</code>, to prevent zoom from getting tangled up
	 * with menus and other statically sized stuff.
	 */
	private final ContainedCanvas canvas = new ContainedCanvas();
	/**
	 * Piccolo canvas containing the <code>CIntentionCell</code>s. This canvas is never added to the Intention View
	 * directly; instead, its only layer is extracted and added to the <code>canvas</code> above.
	 */
	private final ContainedCanvas contentCanvas = new ContainedCanvas();
	/**
	 * Simple menu bar sitting at the bottom of the Intention View.
	 */
	private IntentionGraphMenuBar menuBar;

	/**
	 * Contains the Piccolo components which render the topology of the cluster layout.
	 */
	private CIntentionTopology topology;

	/**
	 * obsolete
	 */
	private boolean iconifyMode = false;

	private final long uuid;

	private IntentionGraph()
	{
		INSTANCE = this;

		uuid = Calico.uuid();

		// IntentionGraph.exitButtonBounds = new Rectangle(CalicoDataStore.ScreenWidth-32,5,24,24);

		canvas.setPreferredSize(new Dimension(CalicoDataStore.ScreenWidth, CalicoDataStore.ScreenHeight));
		setBounds(0, 0, CalicoDataStore.ScreenWidth, CalicoDataStore.ScreenHeight);
		translate((CalicoDataStore.ScreenWidth / 2) - (CIntentionCell.THUMBNAIL_SIZE.width / 2), (CalicoDataStore.ScreenHeight / 2)
				- (CIntentionCell.THUMBNAIL_SIZE.height / 2));

		CalicoInputManager.addCustomInputHandler(uuid, new IntentionGraphInputHandler());

		canvas.addMouseListener(new CalicoMouseListener());
		canvas.addMouseMotionListener(new CalicoMouseListener());

		canvas.removeInputEventListener(canvas.getPanEventHandler());
		canvas.removeInputEventListener(canvas.getZoomEventHandler());

		repaint();

		PLayer contentLayer = contentCanvas.getLayer();
		toolLayer.setParent(contentLayer.getParent());
		topologyLayer.setParent(contentLayer.getParent());
		canvas.getCamera().addLayer(Layer.TOPOLOGY.id, topologyLayer);
		canvas.getCamera().addLayer(Layer.CONTENT.id, contentLayer);
		canvas.getCamera().addLayer(Layer.TOOLS.id, toolLayer);

		drawMenuBar();
	}

	public long getId()
	{
		return uuid;
	}

	public PLayer getLayer(Layer layer)
	{
		switch (layer)
		{
			case TOPOLOGY:
				return topologyLayer;
			case CONTENT:
				return contentCanvas.getLayer();
			case TOOLS:
				return toolLayer;
			default:
				throw new IllegalArgumentException("Unknown layer " + layer);
		}
	}

	public JComponent getComponent()
	{
		return canvas;
	}

	public Point getTranslation()
	{
		double x = getLayer(Layer.CONTENT).getTransform().getTranslateX();
		double y = getLayer(Layer.CONTENT).getTransform().getTranslateY();
		return new Point((int) x, (int) y);
	}

	public void translate(double x, double y)
	{
		getLayer(Layer.CONTENT).translate(x, y);
		getLayer(Layer.TOPOLOGY).translate(x, y);

		if (BubbleMenu.isBubbleMenuActive())
		{
			BubbleMenu.clearMenu();
		}
	}

	public void translateGlobal(double x, double y)
	{
		Point2D.Double translation = new Point2D.Double(x, y);
		getLayer(Layer.CONTENT).setGlobalTranslation(translation);
		getLayer(Layer.TOPOLOGY).setGlobalTranslation(translation);

		if (BubbleMenu.isBubbleMenuActive())
		{
			BubbleMenu.clearMenu();
		}
	}

	public void setTopology(CIntentionTopology topology)
	{
		if (this.topology != null)
		{
			topologyLayer.removeAllChildren();
		}

		this.topology = topology;

		for (CIntentionTopology.Cluster cluster : this.topology.getClusters())
		{
			topologyLayer.addChild(cluster);
		}

		repaint();
	}

	public void setScale(double scale)
	{
		if (getLayer(IntentionGraph.Layer.CONTENT).getScale() == Double.NaN)
		{
			getLayer(IntentionGraph.Layer.CONTENT).setGlobalScale(scale);
		}
		else
		{
			getLayer(IntentionGraph.Layer.CONTENT).setScale(scale);
		}

		if (getLayer(IntentionGraph.Layer.TOPOLOGY).getScale() == Double.NaN)
		{
			getLayer(IntentionGraph.Layer.TOPOLOGY).setGlobalScale(scale);
		}
		else
		{
			getLayer(IntentionGraph.Layer.TOPOLOGY).setScale(scale);
		}

		if (BubbleMenu.isBubbleMenuActive())
		{
			BubbleMenu.clearMenu();
		}
	}

	public void activateIconifyMode(boolean b)
	{
		iconifyMode = b;
	}

	public boolean getIconifyMode()
	{
		return iconifyMode;
	}

	public void fitContents()
	{
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;

		int visibleCount = 0;
		for (PNode node : (Iterable<PNode>) contentCanvas.getLayer().getChildrenReference())
		{
			if (node.getVisible())
			{
				visibleCount++;

				PBounds bounds = node.getBounds();
				if (bounds.x < minX)
				{
					minX = bounds.x;
				}
				if (bounds.y < minY)
				{
					minY = bounds.y;
				}
				if ((bounds.x + bounds.width) > maxX)
				{
					maxX = bounds.x + bounds.width;
				}
				if ((bounds.y + bounds.height) > maxY)
				{
					maxY = bounds.y + bounds.height;
				}
			}
		}

		if (visibleCount < 2)
		{
			translate(minX, minY);
			repaint();
		}
		else
		{
			zoomToRegion(new PBounds(minX, minY, (maxX - minX), (maxY - minY)));
		}
	}

	public void zoomToCell(long cellId)
	{
		setScale(1.0);

		CIntentionCell cell = CIntentionCellController.getInstance().getCellById(cellId);
		Point2D center = cell.getCenter();
		Dimension canvasSize = contentCanvas.getBounds().getSize();
		translateGlobal((canvasSize.width / 2.0) - center.getX(), (canvasSize.height / 2.0) - center.getY());
	}

	public void zoomToCluster(long memberCanvasId)
	{
		long clusterRootCanvasId = CIntentionCellController.getInstance().getClusterRootCanvasId(memberCanvasId);
		CIntentionTopology.Cluster cluster = topology.getCluster(clusterRootCanvasId);
		PBounds maxRingBounds = cluster.getMaxRingBounds();
		if (maxRingBounds == null)
		{
			return; // no zooming on atomic clusters
		}

		double margin = maxRingBounds.width * 0.03;
		maxRingBounds.x -= margin;
		maxRingBounds.y -= margin;
		maxRingBounds.width += (2 * margin);
		maxRingBounds.height += (2 * margin);
		zoomToRegion(maxRingBounds);
	}

	private void zoomToRegion(PBounds bounds)
	{
		Dimension canvasSize = contentCanvas.getBounds().getSize();
		double xRatio = canvasSize.width / bounds.width;
		double yRatio = canvasSize.height / bounds.height;

		double scale = Math.min(xRatio, yRatio) * 0.9;
		setScale(scale);
		double xMargin = (bounds.width * (xRatio - scale)) / 2;
		double yMargin = (bounds.height * (yRatio - scale)) / 2;

		// be very careful, it scales the translation!!!
		translateGlobal(xMargin - (bounds.x * scale), yMargin - (bounds.y * scale));
	}

	public void initialize()
	{
		menuBar.initialize();
	}

	public void repaint()
	{
		canvas.repaint();
		contentCanvas.repaint();
	}

	public Rectangle getBounds()
	{
		return canvas.getBounds();
	}

	public Rectangle getLocalBounds(Layer layer)
	{
		Rectangle globalBounds = canvas.getBounds();
		Point2D localPoint = getLayer(layer).globalToLocal(globalBounds.getLocation());
		Dimension2D localSize = getLayer(layer).globalToLocal(globalBounds.getSize());
		return new Rectangle((int) localPoint.getX(), (int) localPoint.getY(), (int) localSize.getWidth(), (int) localSize.getHeight());
	}

	public void setBounds(int x, int y, int w, int h)
	{
		canvas.setBounds(x, y, w, h);
	}

	private void drawMenuBar()
	{
		if (menuBar != null)
		{
			canvas.getCamera().removeChild(menuBar);
		}

		menuBar = new IntentionGraphMenuBar(CanvasMenuBar.POSITION_BOTTOM);
		canvas.getCamera().addChild(menuBar);

		contentCanvas.setBounds(0, 0, CalicoDataStore.ScreenWidth, (int) (CalicoDataStore.ScreenHeight - menuBar.getBounds().height));
	}

	public boolean processToolEvent(InputEventInfo event)
	{
		if (menuBar.isPointInside(event.getGlobalPoint()))
		{
			menuBar.processEvent(event);
			return true;
		}
		return false;
	}

	public void addMouseListener(MouseListener listener)
	{
		canvas.addMouseListener(listener);
	}

	public void addMouseMotionListener(MouseMotionListener listener)
	{
		canvas.addMouseMotionListener(listener);
	}

	public void removeMouseListener(MouseListener listener)
	{
		canvas.removeMouseListener(listener);
	}

	public void removeMouseMotionListener(MouseMotionListener listener)
	{
		canvas.removeMouseMotionListener(listener);
	}

	/**
	 * Wrap the Piccolo canvas to gain access to one protected method.
	 * 
	 * @author Byron Hawkins
	 */
	private class ContainedCanvas extends PCanvas
	{
		public ContainedCanvas()
		{
			super.removeInputSources();
		}
	}
}
