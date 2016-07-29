package segmm;

import jcuda.Pointer;
import jcuda.jcublas.JCublas;
import segmm.Annotations.*;
//import segmm.Annotations.runSwitch;

@targetData({"mapto, h_A, n", "mapto, h_B, n", "maptofrom, h_C, n"})
class segmmTest
{
	private static final int N = 275;   // Matrix size
	static Pointer d_A = new Pointer();
	static Pointer d_B = new Pointer();
	static Pointer d_C = new Pointer();
	static float alpha = 1.0f;
	static float beta = 0.0f;
	static int n2 = N * N;
	static int i;

	static float h_A[] = new float[n2];
	static float h_B[] = new float[n2];
	static float h_C[] = new float[n2];

	public static void segmmTest()
	{
	}

	public static void Fill()  //Needed to fill the variables with the desired values
	{
		for (int i = 0; i < n2; i++)
		{
			h_A[i] = (float)Math.random();
			h_B[i] = (float)Math.random();
			h_C[i] = (float)Math.random();
		}
	}


	public static void Run() //Used to run the desired code in the generated class
	{
		JCublas.cublasSgemm('n', 'n', N, N, N, alpha, d_A, N, d_B, N, beta, d_C, N);
	}
}