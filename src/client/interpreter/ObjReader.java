package client.interpreter;

import java.util.ArrayList;
import java.util.List;

import geometry.Point3DH;
import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

class ObjReader {
	private static final char COMMENT_CHAR = '#';
	private static final int NOT_SPECIFIED = -1;

	private class ObjVertex {
		// TODO: fill this class in.  Store indices for a vertex, a texture, and a normal.  Have getters for them.
		int vertexIndex;
		int textureIndex;
		int normalIndex;

		ObjVertex(int vertexIndex_, int textureIndex_, int normalIndex_){
			vertexIndex = vertexIndex_;
			textureIndex = textureIndex_;
			normalIndex = normalIndex_;
		}

		public int getVertexIndex(){
			return vertexIndex;
		}
		public int getTextureIndex(){
			return textureIndex;
		}
		public int getNormalIndex(){
			return normalIndex;
		}
	}
	private class ObjFace extends ArrayList<ObjVertex> {
		private static final long serialVersionUID = -4130668677651098160L;

		 public void addElement(ObjVertex objVertex){
        	super.add(objVertex);
    	}
	}	
	private LineBasedReader reader;
	
	private List<Vertex3D> objVertices;
	private List<Point3DH> objNormals;
	private List<ObjFace> objFaces;

	private Color defaultColor;
	
	ObjReader(String filename, Color defaultColor) {
		// TODO: Initialize an instance of this class.
		this.objVertices = new ArrayList<Vertex3D>();
		this.objNormals = new ArrayList<Point3DH>();
		this.objFaces = new ArrayList<ObjFace>();

		this.reader = new LineBasedReader(filename);
		this.defaultColor = defaultColor;
	}

	public void render() {
		// TODO: Implement.  All of the vertices, normals, and faces have been defined.
		// First, transform all of the vertices.		
		// Then, go through each face, break into triangles if necessary, and send each triangle to the renderer.
		// You may need to add arguments to this function, and/or change the visibility of functions in SimpInterpreter.
		for (int i = 0; i < objFaces.size() ; i++) {
			
			polygonForFace(objFaces.get(i));
		}
	}
	
	private void polygonForFace(ObjFace face) {
		// this will split faces into triangles, then render each triangle

		//for face of 1,2,3,4,5
		// f   1/2/3   8/9/10  4/5/6
		//make polygons combining each vertice index
		//ex: 1,2,3 / 1,2,4/ 1,2,5/ 1,3,4 / 1,4,5 / 2,3,4 / 2,3,5 / 3,4,5
		
		//objFace contains arrayList of objVertex ( which contains texture, normal, vertex index)

		Vertex3D v0 = objVertices.get( ((face.get(0)).getVertexIndex()) - 1);

		int v0NormIndex = ((face.get(0)).getNormalIndex());

		for (int i = 1; i <  face.size() - 1; i++) {
			
			for (int j = i+1; j < face.size() ; j++) {
				
				if(v0NormIndex != 0 ){
					SimpInterpreter.polygon(v0, objVertices.get( ( (face.get(i)).getVertexIndex() ) - 1), objVertices.get( ( (face.get(j)).getVertexIndex()) - 1), objNormals.get( v0NormIndex - 1), objNormals.get( ((face.get(i)).getNormalIndex()) - 1), objNormals.get( ((face.get(j)).getNormalIndex()) - 1) );
				} else {
					SimpInterpreter.polygon(v0, objVertices.get( ( (face.get(i)).getVertexIndex() ) - 1), objVertices.get( ( (face.get(j)).getVertexIndex()) - 1) );
				}
			}
		}
	}

	public void read() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretObjLine(line);
		}
	}
	private void interpretObjLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretObjCommand(tokens);
			}
		}
	}

	private void interpretObjCommand(String[] tokens) { //completed
		switch(tokens[0]) {
		case "v" :
		case "V" :
			interpretObjVertex(tokens);
			break;
		case "vn":
		case "VN":
			interpretObjNormal(tokens);
			break;
		case "f":
		case "F":
			interpretObjFace(tokens);
			break;
		default:	// do nothing
			break;
		}
	}
	private void interpretObjFace(String[] tokens) {
		ObjFace face = new ObjFace();
		
		//ArrayList<ObjVertex> objVertexArr;

		for(int i = 1; i<tokens.length; i++) {
			String token = tokens[i];
			String[] subtokens = token.split("/");
			
			int vertexIndex  = objIndex(subtokens, 0, objVertices.size());
			int textureIndex = objIndex(subtokens, 1, 0);
			int normalIndex  = objIndex(subtokens, 2, objNormals.size());

			// TODO: fill in action to take here
			ObjVertex objVertex = new ObjVertex(vertexIndex, textureIndex, normalIndex);
			face.add(objVertex);
		}
		// TODO: fill in action to take here.
		objFaces.add(face);
	}

	private int objIndex(String[] subtokens, int tokenIndex, int baseForNegativeIndices) {
		// TODO: write this.  subtokens[tokenIndex], if it exists, holds a string for an index.
		// use Integer.parseInt() to get the integer value of the index.
		// Be sure to handle both positive and negative indices.  
		


		//handle negative index
		//can ignore texture indices as its not used, but must be able to handle them cause could be in files
		//pseudo code
		// if tokenIndex is 0 //subtokens only has one string
		// 	its a vertex index,
		// else if its 1 //subtokens has more stings 11/1 (texture index is 11), split into [11,1]
		// 	its a textureIndex, 
		// else its 2 //subtokens has more stings 3//2 (normal index is 3) , split into [3, , 2]

		int index;

		if( tokenIndex < subtokens.length){ //it exists
			//parse the int
			index = Integer.parseInt(subtokens[tokenIndex]);

			if(index < 0){ //negative
				index = index + baseForNegativeIndices + 1; // -4 + 4 = 0, so add 1 to make it index 1
 			}
			return index;
		} 

		return 0;
	}

	private void interpretObjNormal(String[] tokens) {
		int numArgs = tokens.length - 1;
		if(numArgs != 3) {
			throw new BadObjFileException("vertex normal with wrong number of arguments : " + numArgs + ": " + tokens);				
		}//numArgs must be 3
		Point3DH normal = SimpInterpreter.interpretPoint(tokens, 1);
		// TODO: fill in action to take here.
		objNormals.add(normal);
	}
	private void interpretObjVertex(String[] tokens) {
		int numArgs = tokens.length - 1;
		Point3DH point = objVertexPoint(tokens, numArgs);
		Color color = objVertexColor(tokens, numArgs);
		
		// TODO: fill in action to take here.
		Vertex3D vertex = new Vertex3D(point, color);
		objVertices.add(vertex);
	}

	private Color objVertexColor(String[] tokens, int numArgs) {
		if(numArgs == 6) {
			return SimpInterpreter.interpretColor(tokens, 4);
		}
		if(numArgs == 7) {
			return SimpInterpreter.interpretColor(tokens, 5);
		}
		return defaultColor;
	}

	private Point3DH objVertexPoint(String[] tokens, int numArgs) {
		if(numArgs == 3 || numArgs == 6) {
			return SimpInterpreter.interpretPoint(tokens, 1);
		}
		else if(numArgs == 4 || numArgs == 7) {
			return SimpInterpreter.interpretPointWithW(tokens, 1);
		}
		throw new BadObjFileException("vertex with wrong number of arguments : " + numArgs + ": " + tokens);
	}
}