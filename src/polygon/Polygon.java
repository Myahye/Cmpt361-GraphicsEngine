package polygon;

import java.util.ArrayList;
import java.util.List;

import geometry.Vertex;
import geometry.Vertex3D;

public class Polygon extends Chain {
	private static final int INDEX_STEP_FOR_CLOCKWISE = -1;
	private static final int INDEX_STEP_FOR_COUNTERCLOCKWISE = 1;
	private static boolean isClockwiseFlag;

	private static double[] faceNormal = new double[4];
	private static double[] cameraSpacePointsP1 = new double[4];
	private static double[] cameraSpacePointsP2 = new double[4];
	private static double[] cameraSpacePointsP3 = new double[4];



	private Polygon(Vertex3D... initialVertices) {
		super(initialVertices);
		if(length() < 3) {
			throw new IllegalArgumentException("Not enough vertices to construct a polygon");
		}
	}
	
	private Polygon(double[] cameraSpacePointsP1_, double[] cameraSpacePointsP2_ , double[] cameraSpacePointsP3_ , double[] faceNormal_, Vertex3D... initialVertices) {
		super(initialVertices);
		faceNormal = faceNormal_;
		cameraSpacePointsP1 = cameraSpacePointsP1_;
		cameraSpacePointsP2 = cameraSpacePointsP2_;
		cameraSpacePointsP3 = cameraSpacePointsP3_;
		//System.out.println("faceNormal" + faceNormal[0]);
		if(length() < 3) {
			throw new IllegalArgumentException("Not enough vertices to construct a polygon");
		}
	}
	// the EmptyMarker is to distinguish this constructor from the one above (when there are no initial vertices).
	private enum EmptyMarker { MARKER; };
	private Polygon(EmptyMarker ignored) {
		super();
	}
	
	public static Polygon makeEmpty() {
		return new Polygon(EmptyMarker.MARKER);
	}

	public static Polygon make(Vertex3D... initialVertices) {
		return new Polygon(initialVertices);
	}
	public static Polygon make(double[] cameraSpacePointsP1, double[] cameraSpacePointsP2 , double[] cameraSpacePointsP3, double[] faceNormal,Vertex3D... initialVertices) {
		return new Polygon(cameraSpacePointsP1, cameraSpacePointsP2, cameraSpacePointsP3, faceNormal, initialVertices);
	}
	public static Polygon makeEnsuringClockwise(Vertex3D... initialVertices) {
		if(isClockwise(initialVertices[0], initialVertices[1], initialVertices[2])) {
			return new Polygon(reverseArray(initialVertices));
		}
		return new Polygon(initialVertices);
	}


	public static <V extends Vertex> boolean isClockwise(Vertex3D a, Vertex3D b, Vertex3D c) {
		Vertex3D vector1 = b.subtract(a);
		Vertex3D vector2 = c.subtract(a);
		
		double term1 = vector1.getX() * vector2.getY();
		double term2 = vector2.getX() * vector1.getY();
		double cross = term1 - term2;
		
		return cross < 0;
	}
	
	private static <V extends Vertex> V[] reverseArray(V[] initialVertices) {
		int length = initialVertices.length;
		List<V> newVertices = new ArrayList<V>();
		
		for(int index = 0; index < length; index++) {
			newVertices.add(initialVertices[index]);
		}
		for(int index = 0; index < length; index++) {
			initialVertices[index] = newVertices.get(length - 1 - index);
		}
		return initialVertices;
	}
	
	public static double[] getFaceNormal(){
		System.out.println("faceNormal" + faceNormal[0]);
		return faceNormal;
	}

	public static double[] getCSP1(){
		return cameraSpacePointsP1;
	}

	public static double[] getCSP2(){
		return cameraSpacePointsP2;
	}

	public static double[] getCSP3(){
		return cameraSpacePointsP3;
	}
	/** 
	 * The Polygon is a circular Chain and
	 *  the index given will be taken modulo the number
	 *  of vertices in the Chain. 
	 *  
	 * @param index
	 * @return
	 */
	public Vertex3D get(int index) {
		int realIndex = wrapIndex(index);
		return vertices.get(realIndex);
	}
	/**
	 *  Wrap the indices for the list vertices.
	 *  
	 *  @param index any integer
	 *  @return the number n such that n is equivalent 
	 *  to the given index modulo the number of vertices.
	 */
	private int wrapIndex(int index) {
		return ((index % numVertices) + numVertices) % numVertices;
	}

	
	/////////////////////////////////////////////////////////////////////////////////
	//
	// methods for dividing a y-monotone polygon into a left chain and a right chain.

	/**
	 * returns the left-hand chain of the polygon, ordered from top to bottom.
	 */
	public Chain leftChain() {
		return sideChain(INDEX_STEP_FOR_COUNTERCLOCKWISE);
	}
	/**
	 * returns the right-hand chain of the polygon, ordered from top to bottom.
	 */
	public Chain rightChain() {
		return sideChain(INDEX_STEP_FOR_CLOCKWISE);
	}
	private Chain sideChain(int indexStep) {
		int topIndex = topIndex();
		int bottomIndex = bottomIndex();
		
		Chain chain = new Chain();
		for(int index = topIndex; wrapIndex(index) != bottomIndex; index += indexStep) {
			chain.add(get(index));
		}
		chain.add(get(bottomIndex));
		
		return chain;
	}
	
	private int topIndex() {
		int maxIndex = 0;
		double maxY = get(0).getY();
		
		for(int index = 1; index < vertices.size(); index++) {
			if(get(index).getY() > maxY) {
				maxY = get(index).getY();
				maxIndex = index;
			}
		}
		return maxIndex;
	}
	private int bottomIndex() {
		int minIndex = 0;
		double minY = get(0).getY();
		
		for(int index = 1; index < vertices.size(); index++) {
			if(get(index).getY() < minY) {
				minY = get(index).getY();
				minIndex = index;
			}
		}
		return minIndex;
	}
	
	public String toString() {
		return "Polygon[" + super.toString() + "]";
	}

	public boolean isPolygonClockwise(){
		isClockwiseFlag = isClockwise(this.get(0), this.get(1), this.get(2));
		return isClockwiseFlag;
	}
}
