package notProvided.client.testpages;

import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;
import polygon.PolygonRenderer;
import polygon.Polygon;
import java.util.List;
import java.util.ArrayList;
import java.util.Random; //to generate random number
import java.util.Collections; //to set list


public class MeshPolygonTest {
	private static final int NUM_POLY = 20;
	private static final long SEED = 12341754L;
	private static Random randGen = new Random(SEED);	
	private final PolygonRenderer renderer;
	private final Drawable panel;

	public static int NO_PERTURBATION = 1;
	public static int USE_PERTURBATION = 0;


	private int perturbationFlag; //assumes no_perturbation
	private Vertex3D[][] arr;

	public MeshPolygonTest(Drawable panel, PolygonRenderer renderer,int flag ) {
		this.panel = panel;
		this.renderer = renderer;
		this.randGen = new Random(SEED);
		this.perturbationFlag = flag; // 0
		render();
	}

	
	private void render() {		

		
		
		int arrlength = 10;
		int arrHeight = 10;

		int height = panel.getHeight();
		int width = panel.getWidth();

		int margin = 15;

		int marginHeight = height - margin*2;
		int marginWidth = width - margin*2;


		int lenWidth = marginWidth/10;
		int lenHeight = marginHeight/10;


		int perturbationVal = 0;
		List<Integer> listRand;	

		if(perturbationFlag == 0){
			listRand = new ArrayList<Integer>();
			Random random =new Random();

			
			for(int i = 0; i < arrlength*arrHeight; i++){
	   				int randomNumber = (random.nextInt(12+12)-12);
	       			listRand.add(randomNumber);

	   		}
		} else {
			listRand = new ArrayList<Integer>(Collections.nCopies(arrlength*arrHeight,0)); //fills list with zeros
		}
		



		arr = new Vertex3D[10][10];

		int count = 0;

		Color randColor = Color.random(randGen);
		for(int i = 0; i < arrlength; i++){
   			arr[i][0] = new Vertex3D( margin*2 + listRand.get(count), ( (marginHeight -(i *lenHeight)) + listRand.get(count) ), 0.0, randColor); //first point of next row
   			count++;	
   			for(int j = 1; j < arrHeight; j++){
       			randColor = Color.random(randGen);

       			arr[i][j] = new Vertex3D( ((margin*2 + (j*lenWidth))+listRand.get(count) )  , ((marginHeight -(i *lenHeight)) + listRand.get(count)) , 0.0, randColor);
   				count++;
   			}

   			randColor = Color.random(randGen);			
		}

		
		for(int i = 0; i < arrlength-1; i++){

   			
   			for(int j = 0; j < arrHeight-1; j++){

   				Polygon poly_p = Polygon.make(arr[i][j], arr[i][j+1], arr[i+1][j]); //botleft
   				renderer.drawPolygon(poly_p, panel);

   				Polygon poly_p2 = Polygon.make(arr[i+1][j], arr[i+1][j+1], arr[i][j+1]); //botleft
   				renderer.drawPolygon(poly_p2, panel);

   			}
   		
		}
		


	}

}