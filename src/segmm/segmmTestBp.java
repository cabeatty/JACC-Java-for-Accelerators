/*
This is an example of the file that would be generated by the targetDataP processor with the
targetData annotation on the segmmTest class
*/

package segmm;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;
import segmm.segmmTest;

public class segmmTestBp
{
	public static segmmTest segmmTestObj = new segmmTest();

	public static void mapto()
	{
		JCublas.cublasAlloc(segmmTestObj.h_A.length, Sizeof.FLOAT, segmmTestObj.d_A);
		JCublas.cublasAlloc(segmmTestObj.h_B.length, Sizeof.FLOAT, segmmTestObj.d_B);
		JCublas.cublasAlloc(segmmTestObj.h_C.length, Sizeof.FLOAT, segmmTestObj.d_C);

		JCublas.cublasSetVector(segmmTestObj.h_A.length, Sizeof.FLOAT, Pointer.to(segmmTestObj.h_A), 1, segmmTestObj.d_A, 1);
		JCublas.cublasSetVector(segmmTestObj.h_B.length, Sizeof.FLOAT, Pointer.to(segmmTestObj.h_B), 1, segmmTestObj.d_B, 1);
		JCublas.cublasSetVector(segmmTestObj.h_C.length, Sizeof.FLOAT, Pointer.to(segmmTestObj.h_C), 1, segmmTestObj.d_C, 1);
	}

	public static void maptofrom()
	{
		JCublas.cublasGetVector(segmmTestObj.h_C.length, Sizeof.FLOAT, segmmTestObj.d_C, 1, Pointer.to(segmmTestObj.h_C), 1);
	}

	public static void free()
	{
		JCublas.cublasFree(segmmTestObj.d_A);
		JCublas.cublasFree(segmmTestObj.d_B);
		JCublas.cublasFree(segmmTestObj.d_C);
	}

	public static void main(String args[])
	{
		JCublas.cublasInit();
		segmmTestObj.Fill();
		mapto();
		segmmTestObj.Run();
		maptofrom();
		free();
		JCublas.cublasShutdown();
	}
}