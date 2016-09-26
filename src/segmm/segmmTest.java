package segmm;

import jcuda.Pointer;
import jcuda.jcublas.JCublas;
import segmm.Annotations.runSwitch;

@runSwitch(target = {"mapto, h_A, n2", "mapto, h_B, n2", "maptofrom, h_C, n2"}, path = "F:\\OneDrive\\CSE 494\\Annotations\\src")
class segmmTest
{
	private static final int N = 275;   // Matrix size
	static Pointer ptr_h_A = new Pointer();
	static Pointer ptr_h_B = new Pointer();
	static Pointer ptr_h_C = new Pointer();
	static float alpha = 1.0f;
	static float beta = 0.0f;
	static int n2 = N * N;
	static int i;

	static float h_A[] = new float[n2];
	static float h_B[] = new float[n2];
	static float h_C[] = new float[n2];

	public static void Fill()//Needed to fill the variables with the desired values
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
		JCublas.cublasSgemm('n', 'n', N, N, N, alpha, ptr_h_A, N, ptr_h_B, N, beta, ptr_h_C, N);
	}

}