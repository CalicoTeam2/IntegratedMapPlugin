package calico.plugins.IntegratedMapPlugin.inputhandlers;

import javax.swing.JOptionPane;

import calico.controllers.CGroupController;
import calico.inputhandlers.CGroupInputHandler;
import calico.inputhandlers.InputEventInfo;
import calico.plugins.IntegratedMapPlugin.components.IntegratedMap;

public class IntegratedMapInputHandler extends CGroupInputHandler  {

private long uuid; 

	public IntegratedMapInputHandler(long u) {
		super(u);
		this.uuid = u;
	}
	
//	@Override
	public void actionStrokeToAnotherGroup(long strokeUID, long targetGroup) {
//		 if(CGroupController.groupdb.get(targetGroup) instanceof AnalysisComponent && CGroupController.groupdb.get(this.uuid) instanceof AnalysisComponent){
//             CConnectorController.connectors.put(uuid, new ControlFlow(uuid, CCanvasController.getCurrentUUID(), CalicoDataStore.PenColor, CalicoDataStore.PenThickness, CStrokeController.strokes.get(strokeUID).getRawPolygon()));
//     		CStrokeController.delete(strokeUID);
//         }
//         else{
////        	 CConnectorController.connectors.put(uuid, new CConnector(uuid, CCanvasController.getCurrentUUID(), CalicoDataStore.PenColor, CalicoDataStore.PenThickness, CStrokeController.strokes.get(strokeUID).getRawPolygon()));
//        	 CStrokeController.show_stroke_bubblemenu(strokeUID, false);
//         }
////		CConnectorController.no_notify_create(Calico.uuid(), CCanvasController.getCurrentUUID(), 0l, CalicoDataStore.PenColor, CalicoDataStore.PenThickness, this.uuid, targetGroup, strokeUID);

		
	}
	
	public void actionDragged(InputEventInfo e)
	{
		super.actionDragged(e);	
		IntegratedMap integratedMap = (IntegratedMap) CGroupController.groupdb.get(uuid);
		double longitude = integratedMap.getLongitude(e.getX());
		double latitude = integratedMap.getLatitude(e.getY());
		System.out.println("Longitude: "+longitude +"\nLatitude: "+ latitude);
		

	}

}
