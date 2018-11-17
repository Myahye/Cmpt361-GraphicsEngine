package client.interpreter;

import java.util.Stack;

import client.interpreter.LineBasedReader;
import geometry.Point3DH;
import geometry.Rectangle;
import geometry.Vertex3D;
import line.LineRenderer;
//import notProvided.client.Clipper;
import notProvided.client.DepthCueingDrawable;
import notProvided.client.DepthEffectDrawable;
import notProvided.client.RendererTrio;
import notProvided.geometry.Transformation;
import notProvided.geometry.LightObj;
import polygon.Polygon;
import polygon.PolygonRenderer;
import polygon.Shader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import polygon.Chain;

public class SimpInterpreter {
	private static final int NUM_TOKENS_FOR_POINT = 3;
	private static final int NUM_TOKENS_FOR_COMMAND = 1;
	private static final int NUM_TOKENS_FOR_COLORED_VERTEX = 6;
	private static final int NUM_TOKENS_FOR_UNCOLORED_VERTEX = 3;
	private static final char COMMENT_CHAR = '#';
	private static RenderStyle renderStyle;
	
	private static Transformation CTM;
	private static Transformation worldToScreen;
	private static Transformation cameraSpace;
	private static Transformation perspectiveMatrix;
	public static Transformation inverseScreen;
	public static Transformation inversePerspective;


	
	private static int WORLD_LOW_X = -100;
	private static int WORLD_HIGH_X = 100;
	private static int WORLD_LOW_Y = -100;
	private static int WORLD_HIGH_Y = 100;

	//FOR CLIPPING
	private static double NEAR_PLANEZ;
	private static double FAR_PLANEZ;
	private static boolean CLIP_FLAG = false;
	//-clipping
	private static double MAX_Y_DIMENSION;
	private static double MIN_Y_DIMENSION;
	private static boolean RECTANGLE_FLAG;
	private static boolean SYMETRIC_FLAG;


	private LineBasedReader reader;
	private Stack<LineBasedReader> readerStack;
	
	private static Color defaultColor = Color.WHITE;
	private static Color ambientLight = Color.BLACK;
	
	private Drawable fullPanel;
	private static Drawable drawable;
	private Drawable depthCueingDrawable;
	
	//I added the following
	private static int globalCounter; //FOR TESTING PURPOSES
	private Stack<Transformation> transformationStack;
	//assignment4
	//private static List<Light> lightsArr;


	private static double specularReflectionCoefficient;  // ks //(refer to assignment doc)
 	private static double specularExponent; 				  // p  // these are set by surface command
 	
 	private static double cameraPoint[] = {0, 0, 0, 1};

 	private static ShadeStyle shadeStyle;

	// private static double viewVector[] = new double[3];
	// private static double normalVector[] = new double[3];

	private static Color lightColor;

	private static List<LightObj> lightSources;

	private static double ac_A;
	private static double ac_B;
	///////////////////////


	private LineRenderer lineRenderer;
	private static PolygonRenderer filledRenderer;
	private static PolygonRenderer wireframeRenderer;
	private static PolygonRenderer phongShader;
	//private Clipper clipper;
	public static enum ShadeStyle {
		PHONG,
		GOURAUD,
		FLAT;
	}
	public static enum RenderStyle {
		FILLED,
		WIREFRAME;
	}
	public SimpInterpreter(String filename, 
			Drawable drawable,
			RendererTrio renderers) {

		System.out.println("constructor called!");
		this.fullPanel = drawable;
		this.drawable = new DepthCueingDrawable(fullPanel, 0, -200, Color.WHITE);
		this.depthCueingDrawable = this.drawable;
		this.lineRenderer = renderers.getLineRenderer();
		this.filledRenderer = renderers.getFilledRenderer();
		this.wireframeRenderer = renderers.getWireframeRenderer();
		this.phongShader = renderers.getPhongShader();
		this.defaultColor = Color.WHITE;
		this.MAX_Y_DIMENSION = 650;
		this.MIN_Y_DIMENSION = 0;
		this.RECTANGLE_FLAG =false;
		this.SYMETRIC_FLAG = false;
		this.ambientLight = Color.BLACK;

		worldToScreen = Transformation.identity();
		inverseScreen = Transformation.identity();
		inversePerspective = Transformation.identity();
		makeWorldToScreenTransform(drawable.getDimensions());
		
		reader = new LineBasedReader(filename);
		readerStack = new Stack<>();
		renderStyle = RenderStyle.FILLED;

		shadeStyle = ShadeStyle.PHONG;
		
		CTM = Transformation.identity();
		//I've added
		transformationStack = new Stack<>();
		cameraSpace = Transformation.identity();
		perspectiveMatrix = Transformation.identity();
		// this.lightSourcePoint = {0,0,0,1};
		// this.cameraPoint = {0,0,0,1};
		this.specularExponent = 8;
		this.specularReflectionCoefficient = 0.3; 
		this.lightColor = Color.WHITE;
		this.globalCounter = 0;
		this.lightSources = new ArrayList<LightObj>();

		//colorUsed = drawable.getColor();//drawable will be depth-cueing, change by next assigment
	}

	private void makeWorldToScreenTransform(Dimensions dimensions) {
		// TODO: fill this in
		worldToScreen = Transformation.identity();

		double translateVector[] = new double[3];
		
		if(!RECTANGLE_FLAG){
			translateVector[0] = (double) 650/2;
			translateVector[1] = (double) 650/2;
			translateVector[2] = 0;
		} else if(SYMETRIC_FLAG){
			translateVector[0] = (double) 650/2;
			translateVector[1] = (double) 650 - 170;
			translateVector[2] = 0;
		} else {
			translateVector[0] = (double) 650;
			translateVector[1] = (double) 650;
			translateVector[2] = 0;
		}

		double translateM[][] = new double[4][4];
		translateM = Transformation.translate(translateVector);
		worldToScreen = worldToScreen.multiply4by4_Trans(translateM);


		double scaleVector[] = new double[3];

		if(!RECTANGLE_FLAG){
			scaleVector[0] = (double) 650/(WORLD_HIGH_X - WORLD_LOW_X); //dimensions.getWidth();
			scaleVector[1] = (double) 650/(WORLD_HIGH_Y - WORLD_LOW_Y); //dimensions.getHeight();
			scaleVector[2] = 1;
		} else if(SYMETRIC_FLAG){
			scaleVector[0] = (double) 325; //dimensions.getWidth();
			scaleVector[1] = (double) 325/(WORLD_HIGH_Y - WORLD_LOW_Y); //dimensions.getHeight();
			scaleVector[2] = 1;
		}
		else {
			scaleVector[0] = (double) 650; //dimensions.getWidth();
			scaleVector[1] = (double) 650/(WORLD_HIGH_Y - WORLD_LOW_Y); //dimensions.getHeight();
			scaleVector[2] = 1;
		}
	
		double scaleM[][] = new double[4][4];
		scaleM = Transformation.scale(scaleVector);
		worldToScreen = worldToScreen.multiply4by4_Trans(scaleM);

		inverseScreen = inverseScreen.inverseMatrix(worldToScreen);		


	}
	
	public void interpret() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretLine(line);
			while(!reader.hasNext()) {
				if(readerStack.isEmpty()) {
					return;
				}
				else {
					reader = readerStack.pop();
				}
			}
		}
	}
	public void interpretLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretCommand(tokens);
			}
		}
	}
	private void interpretCommand(String[] tokens) {
		switch(tokens[0]) {
		case "{" :      push();   break;
		case "}" :      pop();    break;
		case "wire" :   wire();   break;
		case "filled" : filled(); break;
		case "phong" :  phong();  break;
		case "flat" :   flat();   break;
		case "gouraud" :   gouraud();   break;
		
		case "file" :		interpretFile(tokens);		break;
		case "scale" :		interpretScale(tokens);		break;
		case "translate" :	interpretTranslate(tokens);	break;
		case "rotate" :		interpretRotate(tokens);	break;
		case "line" :		interpretLine(tokens);		break;
		case "polygon" :	interpretPolygon(tokens);	break;
		case "camera" :		interpretCamera(tokens);	break;
		case "surface" :	interpretSurface(tokens);	break;
		case "ambient" :	interpretAmbient(tokens);	break;
		case "depth" :		interpretDepth(tokens);		break;
		case "obj" :		interpretObj(tokens);		break;
		case "light" :		interpretLight(tokens);		break;
		
		default :
			System.err.println("bad input line: " + tokens);
			System.out.print(tokens);
			break;
		}
	}

	private void push() {
		// TODO: finish this method
		transformationStack.push(CTM);
	}
	private void pop() {
		// TODO: finish this method
		CTM = transformationStack.pop();
	}
	private void wire() {
		//use wireframeRenderer
		renderStyle = RenderStyle.WIREFRAME;
	}
	private void filled() {
		//use filledPolygonRenderer
		renderStyle = RenderStyle.FILLED;
	}
	private void phong() {
		//use phong shading
		shadeStyle = ShadeStyle.PHONG;
	}
	private void flat() {
		//use flat shading
		shadeStyle = ShadeStyle.FLAT;
	}
	private void gouraud() {
		//use gouraud shading
		shadeStyle = ShadeStyle.GOURAUD;
	}
	
	
	// this one is complete.
	private void interpretFile(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"'; 
		String filename = quotedFilename.substring(1, length-1);
		file(filename + ".simp");
	}
	private void file(String filename) {
		readerStack.push(reader);
		reader = new LineBasedReader(filename);
	}	

	private void interpretScale(String[] tokens) {
		
		double sx = cleanNumber(tokens[1]);
		double sy = cleanNumber(tokens[2]);
		double sz = cleanNumber(tokens[3]);
		// TODO: finish this method
		double scaleVector[] = new double[3];
		scaleVector[0] = sx;
		scaleVector[1] = sy;
		scaleVector[2] = sz;
	
		double scaleM[][] = new double[4][4];
		scaleM = Transformation.scale(scaleVector);
		

		CTM = CTM.multiply4by4_Trans(scaleM); //4by4

		//CTM.printTransformation();
		
	}
	private void interpretTranslate(String[] tokens) {
		//System.out.print("Translate\n");		

		double tx = cleanNumber(tokens[1]);
		double ty = cleanNumber(tokens[2]);
		double tz = cleanNumber(tokens[3]);
		
		// TODO: finish this method
		double translateVector[] = new double[3];
		translateVector[0] = tx;
		translateVector[1] = ty;
		translateVector[2] = tz;

		double translateM[][] = new double[4][4];
		translateM = Transformation.translate(translateVector);
		
		CTM = CTM.multiply4by4_Trans(translateM); //4by4
	}
	private void interpretRotate(String[] tokens) {
		String axisString = tokens[1];
		double angleInDegrees = cleanNumber(tokens[2]);

		// TODO: finish this method
		double rotateM[][] = new double[4][4];
		rotateM = Transformation.rotate(axisString, angleInDegrees);

	

		CTM = CTM.multiply4by4_Trans(rotateM); //4by4
	}
	
	private enum VertexColors {
		COLORED(NUM_TOKENS_FOR_COLORED_VERTEX),
		UNCOLORED(NUM_TOKENS_FOR_UNCOLORED_VERTEX);
		
		private int numTokensPerVertex;
		
		private VertexColors(int numTokensPerVertex) {
			this.numTokensPerVertex = numTokensPerVertex;
		}
		public int numTokensPerVertex() {
			return numTokensPerVertex;
		}
	}
	private void interpretLine(String[] tokens) {			
		Vertex3D[] vertices = interpretVertices(tokens, 2, 1);
		
		double transformedLineP1[] = new double[4];
		double transformedLineP2[] = new double[4];

		double lineVertice[] = new double[4];

		//P1
		lineVertice[0] = vertices[0].getX();
		lineVertice[1] = vertices[0].getY();
		lineVertice[2] = vertices[0].getZ();
		lineVertice[3] = 1;//vertices[0].getW()

		transformedLineP1 = CTM.multiply1by1Matrix(lineVertice); //4by4

		//P2
		lineVertice[0] = vertices[1].getX();
		lineVertice[1] = vertices[1].getY();
		lineVertice[2] = vertices[1].getZ();
		lineVertice[3] = 1;//vertices[0].getW()

		transformedLineP2 = CTM.multiply1by1Matrix(lineVertice);
		
		// Color color_ = depthCueingDrawable.getColor();

		Vertex3D newP1 = new Vertex3D(transformedLineP1[0], transformedLineP1[1], transformedLineP1[2], Color.GREEN);
		Vertex3D newP2 = new Vertex3D(transformedLineP2[0], transformedLineP2[1], transformedLineP2[2], Color.GREEN);
		
		lineRenderer.drawLine(newP1, newP2, drawable); //both drawable variables are depth-cueing, refer to client nextPage()
	}	
	private void interpretPolygon(String[] tokens) {
		//getting vertices
		Vertex3D[] vertices = interpretVertices(tokens, 3, 1);
		

		polygon(vertices[0], vertices[1], vertices[2]);
	
		globalCounter++;
		
	}
	
	
	
	public Vertex3D[] interpretVertices(String[] tokens, int numVertices, int startingIndex) {
		VertexColors vertexColors = verticesAreColored(tokens, numVertices);	
		Vertex3D vertices[] = new Vertex3D[numVertices];
		
		for(int index = 0; index < numVertices; index++) {
			vertices[index] = interpretVertex(tokens, startingIndex + index * vertexColors.numTokensPerVertex(), vertexColors);
		}
		return vertices;
	}
	public VertexColors verticesAreColored(String[] tokens, int numVertices) {
		return hasColoredVertices(tokens, numVertices) ? VertexColors.COLORED :
														 VertexColors.UNCOLORED;
	}
	public boolean hasColoredVertices(String[] tokens, int numVertices) {
		return tokens.length == numTokensForCommandWithNVertices(numVertices);
	}
	public int numTokensForCommandWithNVertices(int numVertices) {
		return NUM_TOKENS_FOR_COMMAND + numVertices*(NUM_TOKENS_FOR_COLORED_VERTEX);
	}

	
	private Vertex3D interpretVertex(String[] tokens, int startingIndex, VertexColors colored) { //completed
		Point3DH point = interpretPoint(tokens, startingIndex);
		
		Color color = defaultColor;
		if(colored == VertexColors.COLORED) {
			color = interpretColor(tokens, startingIndex + NUM_TOKENS_FOR_POINT);
		}

		// TODO: finish this method
		Vertex3D vertex_ = new Vertex3D(point, color);
		return vertex_;
	}
	public static Point3DH interpretPoint(String[] tokens, int startingIndex) { //completed
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);

		// TODO: finish this method
		Point3DH point_ = new Point3DH(x,y,z);
		return point_;
	}
	public static Color interpretColor(String[] tokens, int startingIndex) { //completed
		double r = cleanNumber(tokens[startingIndex]);
		double g = cleanNumber(tokens[startingIndex + 1]);
		double b = cleanNumber(tokens[startingIndex + 2]);

		// TODO: finish this method
		Color color_ = new Color(r,g,b);
		return color_;
	}

	/*Assignment 3*/
	private void line(Vertex3D p1, Vertex3D p2) {
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);
		// TODO: finish this method
		//for next assignment
	}


	


	public static Color getlightingColor(double[] centerPoint, double[] faceNormal){
		
		Color lightCalculationColorp1;
		double summationR = 0; 
		double summationG = 0;   
		double summationB = 0;

		for (int i=0; i< lightSources.size(); i++){
			
			LightObj lightobj = lightSources.get(i);
			//Get view vector  ----------------------------------- Looks right
				double viewVectorP1[] = new double[3];
				viewVectorP1[0] = centerPoint[0] - cameraPoint[0];
				viewVectorP1[1] = centerPoint[1] - cameraPoint[1];
				viewVectorP1[2] = centerPoint[2] - cameraPoint[2];
				//System.out.println("viewVector is" + Arrays.toString(viewVectorP1));
				convertToUnitVector(viewVectorP1);
			//--------------------------------------------------------------

			//get left light Vector --------------------------------- looks right
				double leftLightVectorP1[] = new double[3];
				leftLightVectorP1[0] = (lightobj.getLightSourcePoint())[0] - centerPoint[0];
				leftLightVectorP1[1] = (lightobj.getLightSourcePoint())[1] - centerPoint[1];
				leftLightVectorP1[2] = (lightobj.getLightSourcePoint())[2] - centerPoint[2]; 
				convertToUnitVector(leftLightVectorP1);
				//System.out.println("lightviewVector is" + Arrays.toString(leftLightVectorP1));
			//---------------------------------------------------------------------

			//get right light Vector ---------------------------

				double rightLightVectorP1[] = new double[3];
				double dotProductN_leftLight1 = leftLightVectorP1[0] * faceNormal[0] + leftLightVectorP1[1] * faceNormal[1] + leftLightVectorP1[2] * faceNormal[2];
				rightLightVectorP1 = Transformation.multiplyBySingleDouble3x3(faceNormal, 2*dotProductN_leftLight1);
				rightLightVectorP1 = Transformation.subtract(rightLightVectorP1, 3, leftLightVectorP1);
				convertToUnitVector(rightLightVectorP1);
				double dotProductV_rightLight1 = rightLightVectorP1[0] * viewVectorP1[0] + rightLightVectorP1[1] * viewVectorP1[1] + rightLightVectorP1[2] * viewVectorP1[2];
				//System.out.println("dotProductN_leftLight1 is" + dotProductN_leftLight1);

			//--------------------------------------------------


			//get fatt,i --------------------------------------------------------------------------------- Looks right
				double fatt_p1;
				double d_p1;
				double dotProductN_LightSource = ((lightobj.getLightSourcePoint())[0]) * faceNormal[0] + ((lightobj.getLightSourcePoint())[1]) * faceNormal[1] + ((lightobj.getLightSourcePoint())[2]) * faceNormal[2];

				// d_p1 = Math.sqrt( 		 Math.pow( (centerPoint[0] - (lightobj.getLightSourcePoint())[0]), 2) +  
				// 						 Math.pow( (centerPoint[1] - (lightobj.getLightSourcePoint())[1]), 2) +
				// 						 Math.pow( (centerPoint[2] - (lightobj.getLightSourcePoint())[2]), 2) 
				// 				);

				d_p1 = dotProductN_LightSource/ ( Math.sqrt( 	Math.pow( faceNormal[0], 2) +  
										 						Math.pow( faceNormal[1], 2) +
										 						Math.pow( faceNormal[2], 2) 
												));
				fatt_p1 = 1 / ( lightobj.getAc_A() + lightobj.getAc_B() *d_p1);
			//---------------------------------------------------------------------------------------------		

			//get light Calculation Color------------------------------------------------

				//kdh*Iah
				double firstPartR = defaultColor.getR() * ambientLight.getR();
				double firstPartG = defaultColor.getG() * ambientLight.getG();
				double firstPartB = defaultColor.getB() * ambientLight.getB();
				//kdh * ( N o Ll)
				double nextPartR_P1 = defaultColor.getR() * dotProductN_leftLight1;
				double nextPartG_P1 = defaultColor.getG() * dotProductN_leftLight1;
				double nextPartB_P1 = defaultColor.getB() * dotProductN_leftLight1;

				//System.out.println("kd*(n dot l ) is" + nextPartR_P1);

				double ks_VR = specularReflectionCoefficient * ( Math.pow(dotProductV_rightLight1, specularExponent));

				//System.out.println("ks_VR is" + ks_VR);

				double innerBracketR = nextPartR_P1 + ks_VR;
				double innerBracketG = nextPartG_P1 + ks_VR;
				double innerBracketB = nextPartB_P1 + ks_VR;

				//System.out.println("innerBracketR is" + innerBracketR);
				//System.out.println("fatti is" + fatt_p1);

				summationR +=  firstPartR + (lightobj.getLightColor().getR() * (fatt_p1 * innerBracketR)); //lgiht is 1,1,1
				summationG +=  firstPartG + (lightobj.getLightColor().getG() * (fatt_p1 * innerBracketG));
				summationB +=  firstPartB + (lightobj.getLightColor().getB() * (fatt_p1 * innerBracketB));
			}

			lightCalculationColorp1 = new Color(summationR, summationG, summationB);
			return lightCalculationColorp1;														
	}	


	public static void polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3) {
		// Vertex3D screenP1 = transformToCamera(p1);
		// Vertex3D screenP2 = transformToCamera(p2);
		// Vertex3D screenP3 = transformToCamera(p3);
	
		//getting colors
		Color v1_Color = p1.getColor();
		Color v2_Color = p2.getColor();
		Color v3_Color = p3.getColor();

		//initialization
		double transformedLineP1[] = new double[4];
		double transformedLineP2[] = new double[4];
		double transformedLineP3[] = new double[4];

		double lineVertice[] = new double[4];

		
		//multiplying CTM 
		lineVertice[0] = p1.getX();
		lineVertice[1] = p1.getY();
		lineVertice[2] = p1.getZ();
		lineVertice[3] = 1;//vertices[0].getW()
		transformedLineP1 = CTM.multiply1by1Matrix(lineVertice); //4by4

		lineVertice[0] = p2.getX();
		lineVertice[1] = p2.getY();
		lineVertice[2] = p2.getZ();
		lineVertice[3] = 1;//vertices[0].getW()
		transformedLineP2 = CTM.multiply1by1Matrix(lineVertice);

		lineVertice[0] = p3.getX();
		lineVertice[1] = p3.getY();
		lineVertice[2] = p3.getZ();
		lineVertice[3] = 1;//vertices[0].getW()
		transformedLineP3 = CTM.multiply1by1Matrix(lineVertice);


		//multiplying camera space
		transformedLineP1 = cameraSpace.multiply1by1Matrix(transformedLineP1);
		transformedLineP2 = cameraSpace.multiply1by1Matrix(transformedLineP2);
		transformedLineP3 = cameraSpace.multiply1by1Matrix(transformedLineP3);

		double[] cameraSpacePointsP1 = {transformedLineP1[0], transformedLineP1[1], transformedLineP1[2], 1};
		double[] cameraSpacePointsP2 = {transformedLineP2[0], transformedLineP2[1], transformedLineP2[2], 1};
		double[] cameraSpacePointsP3 = {transformedLineP3[0], transformedLineP3[1], transformedLineP3[2], 1};

		//GETTING LIGHTING CALCULATION VARIABLE
		//------------------------------------------------------------------------
			double centerPoint[] = new double[3];
			centerPoint[0] = (transformedLineP1[0] + transformedLineP2[0] + transformedLineP3[0] )/ 3;
			centerPoint[1] = (transformedLineP1[1] + transformedLineP2[1] + transformedLineP3[1] )/ 3;
			centerPoint[2] = (transformedLineP1[2] + transformedLineP2[2] + transformedLineP3[2] )/ 3;

			//System.out.println("centerPoint is" + Arrays.toString(centerPoint));

			//get Normal Vector---------------------------------------------------------- LOOKS RIGHT

				double p2minusP1[] = new double[3];
				double p3minusP1[] = new double[3];
				double faceNormal[] = new double[4];


				//p2 - p1
				p2minusP1[0] = transformedLineP2[0] - transformedLineP1[0];
				p2minusP1[1] = transformedLineP2[1] - transformedLineP1[1];
				p2minusP1[2] = transformedLineP2[2] - transformedLineP1[2];

				//p3 - p1
				p3minusP1[0] = transformedLineP3[0] - transformedLineP1[0];
				p3minusP1[1] = transformedLineP3[1] - transformedLineP1[1];
				p3minusP1[2] = transformedLineP3[2] - transformedLineP1[2];

				//take cross product of (p2 - p1) X (p3 - p1)
				faceNormal[0] = p2minusP1[1]*p3minusP1[2] - p2minusP1[2]*p3minusP1[1]; 
				faceNormal[1] = - ( p2minusP1[0]*p3minusP1[2] - p2minusP1[2]*p3minusP1[0]);
				faceNormal[2] = p2minusP1[0]*p3minusP1[1] - p2minusP1[1]*p3minusP1[0];
				faceNormal[3] = 1;
				//System.out.println("cameraSourcePoint is" + Arrays.toString(cameraPoint));
				//System.out.println("lightSourcePoint is" + Arrays.toString(lightSourcePoint));
				//System.out.println("faceNormal is" + Arrays.toString(faceNormal));

				convertToUnitVector(faceNormal); //Now a unit vector
			
				//System.out.println("faceNormal to unitVector is" + Arrays.toString(faceNormal) );

			//---------------------------------------------------------------------
			Color lightingCalculationColorP1 = Color.WHITE;
			Color lightingCalculationColorP2 = Color.WHITE;
			Color lightingCalculationColorP3 = Color.WHITE;
			if(shadeStyle == ShadeStyle.FLAT){
				lightingCalculationColorP1 = getlightingColor(centerPoint, faceNormal);
				lightingCalculationColorP2 = lightingCalculationColorP1;
				lightingCalculationColorP3 = lightingCalculationColorP1;

			} else if ( shadeStyle == ShadeStyle.GOURAUD || shadeStyle == ShadeStyle.PHONG){
				lightingCalculationColorP1 = getlightingColor(transformedLineP1, faceNormal);
				lightingCalculationColorP2 = getlightingColor(transformedLineP2, faceNormal);
				lightingCalculationColorP3 = getlightingColor(transformedLineP3, faceNormal);

			}

		

											

			//---------------------------------------------------------------------------


		//------------------------------------------------------------------------


		/*-----------------*/
		//we need to clip
		int draw_flag = 0;
		if(CLIP_FLAG){
			//System.out.println("clip flag true!, camera command called");
			//draw_flag = polygonClipper(NEAR_PLANEZ, FAR_PLANEZ, transformedLineP1, transformedLineP2, transformedLineP3);
		}

		//multiplying perspective
		transformedLineP1 = perspectiveMatrix.multiply1by1Matrix(transformedLineP1);
		transformedLineP2 = perspectiveMatrix.multiply1by1Matrix(transformedLineP2);
		transformedLineP3 = perspectiveMatrix.multiply1by1Matrix(transformedLineP3);

		//multiply by 1/w
		transformedLineP1 = Transformation.multiplyBySingleDouble1x1(transformedLineP1, 1/(transformedLineP1[3]), 4, transformedLineP1[2]);
		transformedLineP2 = Transformation.multiplyBySingleDouble1x1(transformedLineP2, 1/(transformedLineP2[3]), 4, transformedLineP2[2]);
		transformedLineP3 = Transformation.multiplyBySingleDouble1x1(transformedLineP3, 1/(transformedLineP3[3]), 4, transformedLineP3[2]);

		//multiplying WorldToScreen
		transformedLineP1 = worldToScreen.multiply1by1Matrix(transformedLineP1);		
		transformedLineP2 = worldToScreen.multiply1by1Matrix(transformedLineP2);
		transformedLineP3 = worldToScreen.multiply1by1Matrix(transformedLineP3);

		//getting new points
		Vertex3D newP1 = new Vertex3D(transformedLineP1[0], transformedLineP1[1], transformedLineP1[2], lightingCalculationColorP1);
		Vertex3D newP2 = new Vertex3D(transformedLineP2[0], transformedLineP2[1], transformedLineP2[2], lightingCalculationColorP2);
		Vertex3D newP3 = new Vertex3D(transformedLineP3[0], transformedLineP3[1], transformedLineP3[2], lightingCalculationColorP3);
		
		//System.out.println("lightingColor is" + lightCalculationColorp1.toString());

		//System.out.println("defaultColor is" + defaultColor.toString());
		//System.out.println("ambientColor is" + ambientLight.toString());


		
		if(	renderStyle == RenderStyle.FILLED){
			
			if(shadeStyle == ShadeStyle.FLAT || shadeStyle == ShadeStyle.GOURAUD || shadeStyle == ShadeStyle.PHONG){
				Polygon poly_p = Polygon.make(newP1, newP2, newP3);

				//System.out.println("shadeStyle flat");
				filledRenderer.drawPolygon(poly_p, drawable);
			} /*else {
				Polygon poly_p = Polygon.make(cameraSpacePointsP1,cameraSpacePointsP2, cameraSpacePointsP3, faceNormal, newP1, newP2, newP3);

				phongShader.drawPolygon(poly_p, drawable);//both drawable variables are depth-cueing, refer to client nextPage()
			}*/

		} else {
			
			if(draw_flag == 0){
				Polygon poly_p = Polygon.make(newP1, newP2, newP3);

				wireframeRenderer.drawPolygon(poly_p, drawable); 
			}

		}
	}



	public static void polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3, Point3DH p1Norm, Point3DH p2Norm, Point3DH p3Norm) {
		// Vertex3D screenP1 = transformToCamera(p1);
		// Vertex3D screenP2 = transformToCamera(p2);
		// Vertex3D screenP3 = transformToCamera(p3);
	
		//getting colors
		Color v1_Color = p1.getColor();
		Color v2_Color = p2.getColor();
		Color v3_Color = p3.getColor();

		//initialization
		double transformedLineP1[] = new double[4];
		double transformedLineP2[] = new double[4];
		double transformedLineP3[] = new double[4];

		double lineVertice[] = new double[4];

		
		//multiplying CTM 
		lineVertice[0] = p1.getX();
		lineVertice[1] = p1.getY();
		lineVertice[2] = p1.getZ();
		lineVertice[3] = 1;//vertices[0].getW()
		transformedLineP1 = CTM.multiply1by1Matrix(lineVertice); //4by4

		lineVertice[0] = p2.getX();
		lineVertice[1] = p2.getY();
		lineVertice[2] = p2.getZ();
		lineVertice[3] = 1;//vertices[0].getW()
		transformedLineP2 = CTM.multiply1by1Matrix(lineVertice);

		lineVertice[0] = p3.getX();
		lineVertice[1] = p3.getY();
		lineVertice[2] = p3.getZ();
		lineVertice[3] = 1;//vertices[0].getW()
		transformedLineP3 = CTM.multiply1by1Matrix(lineVertice);


		//multiplying camera space
		transformedLineP1 = cameraSpace.multiply1by1Matrix(transformedLineP1);
		transformedLineP2 = cameraSpace.multiply1by1Matrix(transformedLineP2);
		transformedLineP3 = cameraSpace.multiply1by1Matrix(transformedLineP3);

		//GETTING LIGHTING CALCULATION VARIABLE
		//------------------------------------------------------------------------
			double centerPoint[] = new double[3];
			centerPoint[0] = (transformedLineP1[0] + transformedLineP2[0] + transformedLineP3[0] )/ 3;
			centerPoint[1] = (transformedLineP1[1] + transformedLineP2[1] + transformedLineP3[1] )/ 3;
			centerPoint[2] = (transformedLineP1[2] + transformedLineP2[2] + transformedLineP3[2] )/ 3;

			//System.out.println("centerPoint is" + Arrays.toString(centerPoint));

			//get Normal Vector---------------------------------------------------------- LOOKS RIGHT

				double faceNormal[] = new double[4];
				double p1NormArr[] = {p1Norm.getX(), p1Norm.getY(), p1Norm.getZ(), 0};
				double p2NormArr[] = {p2Norm.getX(), p2Norm.getY(), p2Norm.getZ(), 0};
				double p3NormArr[] = {p2Norm.getX(), p2Norm.getY(), p2Norm.getZ(), 0};

				Transformation inverseCTM = Transformation.identity();
				inverseCTM = inverseCTM.inverseMatrix(CTM);
				inverseCTM = inverseCTM.transposeTransformation(inverseCTM);

				p1NormArr = CTM.multiply1by1Matrix(p1NormArr);
				p1NormArr = cameraSpace.multiply1by1Matrix(p1NormArr);

				p2NormArr = CTM.multiply1by1Matrix(p2NormArr);
				p2NormArr = cameraSpace.multiply1by1Matrix(p2NormArr);

				p3NormArr = CTM.multiply1by1Matrix(p3NormArr);
				p3NormArr = cameraSpace.multiply1by1Matrix(p3NormArr);


				//take cross product of (p2 - p1) X (p3 - p1)
				faceNormal[0] = ( p1NormArr[0] + p2NormArr[0] + p3NormArr[0] )/ 3;
				faceNormal[1] = ( p1NormArr[1] + p2NormArr[1] + p3NormArr[1] )/ 3;
				faceNormal[2] = ( p1NormArr[2] + p2NormArr[2] + p3NormArr[2] )/ 3;
				faceNormal[3] = 1;

				//faceNormal = CTM.multiply1by1Matrix(faceNormal);
				//faceNormal = cameraSpace.multiply1by1Matrix(faceNormal);

				convertToUnitVector(faceNormal); //Now a unit vector
			
			//---------------------------------------------------------------------
			Color lightingCalculationColorP1 = Color.WHITE;
			Color lightingCalculationColorP2 = Color.WHITE;
			Color lightingCalculationColorP3 = Color.WHITE;
			if(shadeStyle == ShadeStyle.FLAT){
				lightingCalculationColorP1 = getlightingColor(centerPoint, faceNormal);
				lightingCalculationColorP2 = lightingCalculationColorP1;
				lightingCalculationColorP3 = lightingCalculationColorP1;

			} else if ( shadeStyle == ShadeStyle.GOURAUD || shadeStyle == ShadeStyle.PHONG){
				lightingCalculationColorP1 = getlightingColor(transformedLineP1, faceNormal);
				lightingCalculationColorP2 = getlightingColor(transformedLineP2, faceNormal);
				lightingCalculationColorP3 = getlightingColor(transformedLineP3, faceNormal);

			}

		

											

			//---------------------------------------------------------------------------


		//------------------------------------------------------------------------


		/*-----------------*/
		//we need to clip
		int draw_flag = 0;
		if(CLIP_FLAG){
			//System.out.println("clip flag true!, camera command called");
			//draw_flag = polygonClipper(NEAR_PLANEZ, FAR_PLANEZ, transformedLineP1, transformedLineP2, transformedLineP3);
		}

		//multiplying perspective
		transformedLineP1 = perspectiveMatrix.multiply1by1Matrix(transformedLineP1);
		transformedLineP2 = perspectiveMatrix.multiply1by1Matrix(transformedLineP2);
		transformedLineP3 = perspectiveMatrix.multiply1by1Matrix(transformedLineP3);

		//multiply by 1/w
		transformedLineP1 = Transformation.multiplyBySingleDouble1x1(transformedLineP1, 1/(transformedLineP1[3]), 4, transformedLineP1[2]);
		transformedLineP2 = Transformation.multiplyBySingleDouble1x1(transformedLineP2, 1/(transformedLineP2[3]), 4, transformedLineP2[2]);
		transformedLineP3 = Transformation.multiplyBySingleDouble1x1(transformedLineP3, 1/(transformedLineP3[3]), 4, transformedLineP3[2]);

		//multiplying WorldToScreen
		transformedLineP1 = worldToScreen.multiply1by1Matrix(transformedLineP1);		
		transformedLineP2 = worldToScreen.multiply1by1Matrix(transformedLineP2);
		transformedLineP3 = worldToScreen.multiply1by1Matrix(transformedLineP3);

		//getting new points
		Vertex3D newP1 = new Vertex3D(transformedLineP1[0], transformedLineP1[1], transformedLineP1[2], lightingCalculationColorP1);
		Vertex3D newP2 = new Vertex3D(transformedLineP2[0], transformedLineP2[1], transformedLineP2[2], lightingCalculationColorP2);
		Vertex3D newP3 = new Vertex3D(transformedLineP3[0], transformedLineP3[1], transformedLineP3[2], lightingCalculationColorP3);
		
		//System.out.println("lightingColor is" + lightCalculationColorp1.toString());

		//System.out.println("defaultColor is" + defaultColor.toString());
		//System.out.println("ambientColor is" + ambientLight.toString());



		
		if(	renderStyle == RenderStyle.FILLED){
			
			if(shadeStyle == ShadeStyle.FLAT || shadeStyle == ShadeStyle.GOURAUD || shadeStyle == shadeStyle.PHONG){
				Polygon poly_p = Polygon.make(newP1, newP2, newP3);

				//System.out.println("shadeStyle flat");
				filledRenderer.drawPolygon(poly_p, drawable);
			} /*else {
				Polygon poly_p = Polygon.make(faceNormal, newP1, newP2, newP3);

				phongShader.drawPolygon(poly_p, drawable);//both drawable variables are depth-cueing, refer to client nextPage()
			}*/	

		} else {
			
			if(draw_flag == 0){
				Polygon poly_p = Polygon.make(newP1, newP2, newP3);

				wireframeRenderer.drawPolygon(poly_p, drawable); 
			}

		}
	}
	
	private static void convertToUnitVector(double[] arr){
		double denom = Math.sqrt( ( Math.pow(arr[0],2) + Math.pow(arr[1],2) + Math.pow(arr[2],2) ) );
		arr[0] = arr[0]/denom;
		arr[1] = arr[1]/denom;
		arr[2] = arr[2]/denom;
	}

	private Vertex3D transformToCamera(Vertex3D vertex) {
		// TODO: finish this method
		Vertex3D retV = new Vertex3D(1,1,1,Color.GREEN);
		return retV;
	}

	private void interpretObj(String[] tokens){
		String tokens1 = tokens[1].replace("\"", "");
		String filename = tokens1 + ".obj";

		ObjReader objReader = new ObjReader(filename, defaultColor);
		objReader.read();
		objReader.render();
	}


	private void interpretAmbient(String[] tokens){
		//command will be ambient (r,g,b)
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);

		Color ambientColor = new Color(r,g,b);

		this.ambientLight = ambientColor;	
	}

	private void interpretSurface(String[] tokens){
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);

		double ks = cleanNumber(tokens[4]);
		double p = cleanNumber(tokens[5]);

		Color surfaceColor = new Color(r,g,b);

		this.defaultColor = surfaceColor;
		this.specularReflectionCoefficient = ks;
		this.specularExponent = p;	
	}

	private void interpretLight(String[] tokens){
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);

		//Handle all variable initialization here for now
		lightColor = new Color(r,g,b);
		ac_A = cleanNumber(tokens[4]); //attentuation constant = ac
		ac_B = cleanNumber(tokens[5]);
		
		//Get new light source position
		double lightSourcePoint[] = {0, 0, 0, 1};

		lightSourcePoint = CTM.multiply1by1Matrix(lightSourcePoint);
		System.out.println("lightSourcePoint" + Arrays.toString(lightSourcePoint));
		lightSourcePoint = cameraSpace.multiply1by1Matrix(lightSourcePoint);

		LightObj light = new LightObj(lightColor, ac_A, ac_B, lightSourcePoint);
		lightSources.add(light);
	}

	private void interpretDepth(String[] tokens){
		double near = cleanNumber(tokens[1]);
		double far = cleanNumber(tokens[2]);

		Color depthColor = interpretColor(tokens, 3);

		this.drawable = new DepthEffectDrawable(fullPanel, near, far, NEAR_PLANEZ, depthColor, this.ambientLight, MAX_Y_DIMENSION, MIN_Y_DIMENSION);
	}

	private void interpretCamera(String[] tokens) {

		//camera is inverse of CTM till camera command called
		cameraSpace = cameraSpace.inverseMatrix(CTM);		

		double xlow = cleanNumber(tokens[1]);
		double ylow = cleanNumber(tokens[2]);
		double xhigh = cleanNumber(tokens[3]);
		double yhigh = cleanNumber(tokens[4]);
		double near = cleanNumber(tokens[5]);
		double far = cleanNumber(tokens[6]);

		double width = xhigh - xlow;
		double height = yhigh - ylow;
		

		cameraPoint = CTM.multiply1by1Matrix(cameraPoint);
		cameraPoint = cameraSpace.multiply1by1Matrix(cameraPoint);

		//set perspective matrix
		perspectiveMatrix.setPerspectiveMatrix(width, height, near, far);
		
		inversePerspective = inversePerspective.inverseMatrix(perspectiveMatrix);
		if(width != 2 || height != 2){
			//change dimensions
			WORLD_HIGH_Y = (int) yhigh;
			RECTANGLE_FLAG = true;

			if( width != height){//PAGEH
				SYMETRIC_FLAG = true;
				MAX_Y_DIMENSION = 487.5;
				MIN_Y_DIMENSION = 160;
				System.out.println("miny,maxy dimension changed!");
			}

		} else {
			WORLD_HIGH_Y = (int) yhigh;
		}

		WORLD_LOW_X = (int) xlow;
		WORLD_HIGH_X = (int) xhigh;
		WORLD_LOW_Y = (int) ylow;

		makeWorldToScreenTransform(drawable.getDimensions());

		NEAR_PLANEZ = near;
		FAR_PLANEZ = far;
		CLIP_FLAG = true;

	}

	public static Point3DH interpretPointWithW(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
		double w = cleanNumber(tokens[startingIndex + 3]);
		Point3DH point = new Point3DH(x, y, z, w);
		return point;
	}

	private static double cleanNumber(String string) {
		return Double.parseDouble(string);
	}

	public static int polygonClipper(double zPLaneNear, double zPLaneFar, double[] p1, double[] p2, double[] p3 ){
		//BASIC CLIPPING
		if( p1[2] > zPLaneNear || p2[2] > zPLaneNear || p1[2] > zPLaneNear){
			return 1; //DON'T DRAW POLYGON!
		} else if ( p1[2] < zPLaneFar || p2[2] < zPLaneFar || p1[2] < zPLaneFar) {
			return 1;
		} else {
			return 0;
		}
	}
}
