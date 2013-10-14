package assignment3;

import glWrapper.GLHalfedgeStructure;
import glWrapper.GLHashtree;
import glWrapper.GLHashtreeVertices;
import glWrapper.GLWireframeMesh;

import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import meshes.HalfEdgeStructure;
import meshes.PointCloud;
import meshes.WireframeMesh;
import meshes.exception.DanglingTriangleException;
import meshes.exception.MeshNotOrientedException;
import meshes.reader.ObjReader;
import meshes.reader.PlyReader;
import openGL.MyDisplay;
import openGL.gl.GLDisplayable;
import sparse.LinearSystem;
import sparse.SCIPY;
import assignment2.HashOctree;
import assignment2.HashOctreeVertex;

public class SSDMatricesDemo {
	
	public static void main(String[] args) throws IOException, MeshNotOrientedException, DanglingTriangleException{
		
		
		marchingCubesDemo();
		

			
	}
	
	
	public static void marchingCubesDemo() throws MeshNotOrientedException, DanglingTriangleException, IOException{
		
		PointCloud pc = ObjReader.readAsPointCloud("objs/teapot.obj", false);
		HashOctree tree = new HashOctree(pc, 8, 1, 1);
		LinearSystem system = SSDMatrices.ssdSystem(tree, pc, 1, 1, 1);
		//Test Data: create an octree
		ArrayList<Float> functionByVertex = new ArrayList<Float>();
		SCIPY.solve(system, "whatev", functionByVertex);
		
		MarchingCubes mc = new MarchingCubes(tree);
		mc.dualMC(functionByVertex);
		WireframeMesh mesh = mc.result;
		GLWireframeMesh glMesh = new GLWireframeMesh(mesh);
		//And show off...
		
		//visualization of the per vertex values (blue = negative, 
		//red = positive, green = 0);
		MyDisplay d = new MyDisplay();
		
		glMesh.configurePreferredShader("shaders/trimesh_flat.vert", "shaders/trimesh_flat.frag", "shaders/trimesh_flat.geom");
		d.addToDisplay(glMesh);
		
		//discrete approximation of the zero level set: render all
		//tree cubes that have negative values.
		GLHashtree gltree = new GLHashtree(tree);
		gltree.addFunctionValues(functionByVertex);
		gltree.configurePreferredShader("shaders/octree_zro.vert", 
				"shaders/octree_zro.frag", "shaders/octree_zro.geom");
		d.addToDisplay(gltree);
	}
}
