package notProvided.geometry;

import windowing.graphics.Color;
import java.util.Arrays;

public final class Transformation {
	private double matrix_CTM[][];
	private static int height = 4;
	private static int width = 4;

	private Transformation() { //fills matrix with 1's
		matrix_CTM = new double[height][width];
		clearCTM(); //creates identity matrix
	}

	public static Transformation identity() {
		return new Transformation();
	}

	public static double[][] scale(double scaleVector[]){
		double scaleM[][] = new double[height][width]; //filled with zeros
		for (int i = 0; i < height - 1 ; i++) { //only 3 points
			scaleM[i][i] = scaleVector[i];
		}
		scaleM[height-1][width-1] = 1;
		return scaleM;
	}

	public static double[][] translate(double translateVector[]){
		double translateM[][] = new double[height][width]; //filled with zeros
		
		for (int i = 0; i < height ; i++) {
			translateM[i][i] = 1; //filling diagonals with 1
		}

		for (int i = 0; i < height - 1 ; i++) { //only 3 points
			translateM[i][height-1] = translateVector[i]; //height = 4
		}
		return translateM;
	}

	public static double[][] rotate(String axis, double angleInDegrees){
		double cos_theta = Math.cos(Math.toRadians(angleInDegrees));
		double sin_theta = Math.sin(Math.toRadians(angleInDegrees)); 
		double negative_sin_theta = -1*(Math.sin(Math.toRadians(angleInDegrees))); 
 		
 		double rotateM[][] = new double[height][width]; //filled with zeros

 		for (int i = 0; i < height ; i++) {
			rotateM[i][i] = 1; //filling diagonals with 1
		}

		if( axis.equals("X")){
			rotateM[1][1] = cos_theta;
			rotateM[1][2] = negative_sin_theta;
			rotateM[2][1] = sin_theta;
			rotateM[2][2] = cos_theta;
		} else if ( axis.equals("Y")){
			rotateM[0][0] = cos_theta;
			rotateM[2][0] = negative_sin_theta;
			rotateM[0][2] = sin_theta;
			rotateM[2][2] = cos_theta;
		} else { //can we assume axis will always be x,y,z
			rotateM[0][0] = cos_theta;
			rotateM[0][1] = negative_sin_theta;
			rotateM[1][0] = sin_theta;
			rotateM[1][1] = cos_theta;
		}
		
		return rotateM;

	}

	public void clearCTM(){
		//sets diagonal values to 1
		for (int i = 0; i < height ; i++ ) { //height = 4
				matrix_CTM[i][i] = 1;
		}
	}

	public void setCTMtoZero(){
		for (double[] row: matrix_CTM)
    		Arrays.fill(row, 0.0);
	}

	public Transformation multiply4by4_Trans(double CTM_1[][]){

		Transformation newTransformation  = new Transformation();

		newTransformation.setCTMtoZero();

		for(int i = 0; i < height; i++){

			for (int j = 0; j < width; j++) {
				
				for (int m = 0; m < width ; m++ ) {
					newTransformation.matrix_CTM[i][j] += this.matrix_CTM[i][m] * CTM_1[m][j];
				}
			}
		}

		return newTransformation;
	}

	public double[] multiply1by1Matrix(double CTM_1[]){

		double transformedLine[] = new double[4];

		for(int i = 0; i < height; i++){

			for (int j = 0; j < 1; j++) {
				
				for (int m = 0; m < width ; m++ ) {
					transformedLine[i] += matrix_CTM[i][m] * CTM_1[m];
				}
			}
		}

		return transformedLine;
	}

	public void printTransformation(){
		for(int i = 0; i < height; i++){
			System.out.print("row:\n");
			System.out.println(Arrays.toString(matrix_CTM[i]));
		}
	}
	
	public static double[] subtract(double[] arr, int len, double[] arr2){
		double[] newMatrix = new double[4];

		for (int i = 0; i < len ; i++ ) {
			newMatrix[i] = arr[i] - arr2[i];
		}

		return newMatrix;
	}
	//Assignment 3
	public Transformation inverseMatrix(Transformation currentCTM){ 
		//create a matrix of minors (cofactor expansion)
		Transformation newTransformation  = new Transformation();

		double[][] ret = new double[4][4];

		double val = 1/ (det4x4(currentCTM.matrix_CTM));
		//System.out.print(val);
		ret = multiplyBySingleDouble4x4(transposeMatrix(matrixOfMinors(currentCTM.matrix_CTM)), val, 4, 4);

		//System.out.println(Arrays.deepToString(ret));

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				newTransformation.matrix_CTM[i][j] = ret[i][j];			
			}
		}

		return newTransformation;
	}

	public static double[][] transposeMatrix(double[][] matrix){
		double [][] transposedM = new double[4][4];

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				transposedM[i][j] = matrix[j][i];			
			}
		}

		return transposedM;
	}

	public static Transformation transposeTransformation(Transformation matrix){
		Transformation newTransformation  = new Transformation();

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				newTransformation.matrix_CTM[i][j] = matrix.matrix_CTM[j][i];			
			}
		}

		return newTransformation;
	}


	//multiply 4x4 array by singleDouble
	public static double[][] multiplyBySingleDouble4x4(double[][] matrix, double val, int row, int col){
		double [][] newMatrix = new double[4][4];

		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				newMatrix[i][j] = matrix[i][j]*val;			
			}
		}		

		return newMatrix;
	}

	public static double[] multiplyBySingleDouble1x1(double[] arr, double val, int len, double z){
		double [] newMatrix = new double[len];

		for (int i = 0; i < len; i++) {
			if(i != 2){	
				newMatrix[i] = arr[i]*val;			
			}
		}		
		newMatrix[2] = z;
		return newMatrix;
	}

	public static double[] multiplyBySingleDouble3x3(double[] arr, double val){
		double [] newMatrix = new double[3];

		for (int i = 0; i < 3; i++) {
				newMatrix[i] = arr[i]*val;			
		}
		return newMatrix;		
	}

	//matrix of minors ( det of cofactor expansion matrix)
	public static double[][] matrixOfMinors(double[][] matrix){
		double [][] minors = new double[4][4];
		for (int i = 0; i < 4; i++) {
			
			for (int j = 0; j < 4; j++) {
					
					if( i % 2 == 0){ //if i even

						if( j % 2 == 0){
							minors[i][j] = det3x3(removeRowandCol(i,j, matrix));
						} else {
							minors[i][j] = -1 * det3x3(removeRowandCol(i,j, matrix));
							
							//can get -0.0, what to do in this case?
						}

					} else {

						if( j % 2 == 0){
							minors[i][j] = -1 * det3x3(removeRowandCol(i,j, matrix));
						} else {
							minors[i][j] = det3x3(removeRowandCol(i,j, matrix));
						}

					}					
			}
		}
		return minors;
	}


	public static double det4x4( double[][] matrix){ //solves determinant of 4by4 matrix
		//cofactor expansion along first row
		double det1 = matrix[0][0] * det3x3( removeRowandCol(0,0,matrix));
		double det2 = -1 * matrix[0][1] * det3x3( removeRowandCol(0,1,matrix));
		double det3 = matrix[0][2] * det3x3( removeRowandCol(0,2,matrix));
		double det4 = -1 * matrix[0][3] * det3x3( removeRowandCol(0,3,matrix));
		double det = det1 + det2 + det3 + det4;
		return det;
	}


	public static double det3x3( double[][] matrix){ //solves determinant of 3by3 matrix
		//cofactor expansion along first row
		double det1 = matrix[0][0] * ( (matrix[1][1] * matrix[2][2]) - (matrix[1][2] * matrix[2][1]) );
		double det2 = -1 * matrix[0][1] * ( (matrix[1][0] * matrix[2][2]) - (matrix[1][2] * matrix[2][0]));
		double det3 = matrix[0][2] * ( (matrix[1][0] * matrix[2][1]) - (matrix[1][1] * matrix[2][0]) );
		double det = det1 + det2 + det3;
		return det;
	}

	public static double[][] removeRowandCol(int row, int col, double[][] matrix){ //given 4by4 matrix, returns cofactor at row/col
		double retMatrix[][] = new double[3][3];

		int n = 0; //rows
		
		//rows
		for (int i = 0; i < 4 ; i++ ) {
			
			if( i == row)
				continue; //skip code, move to next loop
			//cols

			int m = 0; //cols
			for (int j = 0; j < 4; j++) {

				if(j == col)
					continue;

				retMatrix[n][m] = matrix[i][j];
				m++;
				
			}
			
			n++;
			
		}

		return retMatrix;
	}

	public void setPerspectiveMatrix(double width, double height, double near, double far){ //given 4by4 matrix, returns cofactor at row/col
		/*
		this.matrix_CTM = { 
							{2*near/width, 0, 0, 0}, 
							{0, 2*near/height, 0, 0}, 
							{0, 0, far/(far-near), -1*(near*far)/(far-near)}, 
							{0, 0, 1, 0},
						  };
		*/
		float fov = 90;
		double S = 1 / ( Math.tan( fov*0.5*(Math.PI/180)));
		// this.matrix_CTM[0][0] = (2*near)/width;
		// this.matrix_CTM[1][1] = (2*near)/height;

		// this.matrix_CTM[0][0] = S;
		// this.matrix_CTM[1][1] = S;
		// this.matrix_CTM[2][2] = -far/(far-near);
	
		// this.matrix_CTM[3][2] = -1;	

		// this.matrix_CTM[2][3] = -1*(near*far)/(far-near);
		// this.matrix_CTM[3][3] = 0;


		// this.matrix_CTM[0][0] = -1/width;
		// this.matrix_CTM[1][1] = -1/height;
		// this.matrix_CTM[2][2] = -(far+near)/(far-near);
	
		// this.matrix_CTM[3][2] = -1;	

		// this.matrix_CTM[2][3] = -1*(2*near*far)/(far-near);
		this.matrix_CTM[3][3] = 0;
		this.matrix_CTM[3][2] = -1;

	}
}	

//TO TEST ON ONLINE IDE

// import java.util.Arrays;


// public class MyClass {
//     public static double[][] removeRowandCol(int row, int col, double[][] matrix){ //given 4by4 matrix, returns cofactor at row/col
// 		double retMatrix[][] = new double[3][3];

// 		int n = 0; //rows
		
// 		//rows
// 		for (int i = 0; i < 4 ; i++ ) {
			
// 			if( i == row)
// 				continue; //skip code, move to next loop
// 			//cols

// 			int m = 0; //cols
// 			for (int j = 0; j < 4; j++) {

// 				if(j == col)
// 					continue;

// 				retMatrix[n][m] = matrix[i][j];
// 				m++;
				
// 			}
			
// 			n++;
			
// 		}

// 		return retMatrix;
// 	}
    
//     public static void main(String args[]) {
//         int x=10;
//         int y=25;
//         int z=x+y;
//         double ret[][]={{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,16}};
        
//         double[][] newRet = new double[4][4];
//         newRet = removeRowandCol(1,1,ret);

//         System.out.println(Arrays.deepToString(newRet));
//     }
// }