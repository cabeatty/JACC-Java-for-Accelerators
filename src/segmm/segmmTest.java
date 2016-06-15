package segmm;

//Imports, JCublas

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;
import segmm.Annotations.*;

//@valPull({"h_A[d_A]", "h_B[d_B]", "h_C[d_C]"})
@targetData({"mapto, h_A, d_A", "mapto, h_B, d_B", "maptofrom, h_C, d_C"})
class segmmTest
{
	// Matrix size
	private static final int N = 275;
	public static Pointer d_A = new Pointer();
	public static Pointer d_B = new Pointer();
	public static Pointer d_C = new Pointer();
	public static float alpha = 1.0f;
	public static float beta = 0.0f;
	public static int n2 = N * N;
	public static int i;

	public static float h_A[] = new float[n2];
	public static float h_B[] = new float[n2];
	public static float h_C[] = new float[n2];

	// Main
	public static void main(String args[])
	{
    // Initialize JCublas
		JCublas.cublasInit();

    // Fill the matrices with test data
		for (int i = 0; i < n2; i++)
		{
			h_A[i] = (float)Math.random();
			h_B[i] = (float)Math.random();
			h_C[i] = (float)Math.random();
		}
    ///* Allocate device memory for the matrices
		JCublas.cublasAlloc(n2, Sizeof.FLOAT, d_A);
		JCublas.cublasAlloc(n2, Sizeof.FLOAT, d_B);
		JCublas.cublasAlloc(n2, Sizeof.FLOAT, d_C);
	//*/

    ///* Initialize the device matrices with the host matrices
		JCublas.cublasSetVector(n2, Sizeof.FLOAT, Pointer.to(h_A), 1, d_A, 1);
		JCublas.cublasSetVector(n2, Sizeof.FLOAT, Pointer.to(h_B), 1, d_B, 1);
		JCublas.cublasSetVector(n2, Sizeof.FLOAT, Pointer.to(h_C), 1, d_C, 1);
	//*/

    // Performs operation using JCublas
		JCublas.cublasSgemm('n', 'n', N, N, N, alpha, d_A, N, d_B, N, beta, d_C, N);

	//Will also be result of @maptofrom
    ///* Read the result back
		JCublas.cublasGetVector(n2, Sizeof.FLOAT, d_C, 1, Pointer.to(h_C), 1);
	//*/

    ///* Memory clean up
		JCublas.cublasFree(d_A);
		JCublas.cublasFree(d_B);
		JCublas.cublasFree(d_C);
	//*/

    // Shutdown
		JCublas.cublasShutdown();

	}
}