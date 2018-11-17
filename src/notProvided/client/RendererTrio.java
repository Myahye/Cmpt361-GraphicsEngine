package notProvided.client;

import line.LineRenderer;
import polygon.PolygonRenderer;


public class RendererTrio {

	private PolygonRenderer filledRenderer;
	private PolygonRenderer wireframeRenderer;
	private LineRenderer lineRenderer;
	private PolygonRenderer phongShader;


	public RendererTrio( PolygonRenderer filledRenderer_, PolygonRenderer wireRenderer_, LineRenderer line_, PolygonRenderer phongShader){
		this.filledRenderer = filledRenderer_;
		this.wireframeRenderer = wireRenderer_;
		this.lineRenderer = line_;
		this.phongShader = phongShader;
	}

	public LineRenderer getLineRenderer(){
		return lineRenderer;
	}

	public PolygonRenderer getFilledRenderer(){
		return filledRenderer;
	}

	public PolygonRenderer getWireframeRenderer(){
		return wireframeRenderer;
	}

	public PolygonRenderer getPhongShader(){
		return phongShader;
	}

}
