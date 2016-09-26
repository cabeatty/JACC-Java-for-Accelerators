package segmm.Annotations.Processors.Util;

import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

public class fCallWriter
{
	List<? extends Element> encElm;
	List<String[]> targetVal;


	public fCallWriter(List<? extends Element> encElm, List<String[]> targetVal)
	{
		this.encElm = encElm;
		this.targetVal = targetVal;
	}

	/*-------------------------------------------------------<<FREE>>------------------------------------------------------------*/
	public String pointers()
	{
		String value = "";

		for (final Element tempElm : encElm)
		{
			if (tempElm instanceof VariableElement)
			{
				VariableElement tempVarElm = (VariableElement) tempElm;

				for (String[] tempLocStr : targetVal)
				{
					if (tempVarElm.getSimpleName().toString().equals(tempLocStr[1]))
					{
						if (tempLocStr[0].equals("mapto") | tempLocStr[0].equals("maptofrom") | tempLocStr[0].equals("alloc"))
						{
							value +=
									(
											//static Pointer ptr_h_A = new Pointer();
											"\tstatic Pointer ptr_" + tempLocStr[1] + " = new Pointer);\n"
									);
						}
					}
				}
			}
		}
		return value;
	}
	/*-------------------------------------------------------<<FREE_END>>---------------------------------------------------------*/

	/*-------------------------------------------------------<<ALLOC>>-----------------------------------------------------------*/
	public String alloc()
	{
		String value = "";

		value += ("\tpublic static void alloc()\n");
		value += ("\t{\n");

		for (final Element tempElm : encElm)
		{
			if (tempElm instanceof VariableElement)
			{
				VariableElement tempVarElm = (VariableElement) tempElm;

				for (String[] tempLocStr : targetVal)
				{
					if (tempVarElm.getSimpleName().toString().equals(tempLocStr[1]))
					{
						if (tempLocStr[0].equals("mapto") | tempLocStr[0].equals("maptofrom") | tempLocStr[0].equals("alloc"))
						{
							value +=
							(
								//JCublas.cublasAlloc(n2, Sizeof.FLOAT, ptr_h_A);
								"\t\tJCublas.cublasAlloc(" + tempLocStr[2] + ", Sizeof." +
								tempVarElm.asType().toString().replaceAll("\\[\\]", "").toUpperCase() + ", " +
								"ptr_" + tempLocStr[1] + ");\n"
							);
						}
					}
				}
			}
		}
		value += "\t}\n";
		return value;
	}
	/*-------------------------------------------------------<<ALLOC_END>>-------------------------------------------------------*/

	/*-------------------------------------------------------<<MAPTO>>-----------------------------------------------------------*/
	public String mapto()
	{
		String value = "";

		value += ("\tpublic static void mapto()\n");
		value += ("\t{\n");

		for (final Element tempElm : encElm)
		{
			if (tempElm instanceof VariableElement)
			{
				VariableElement tempVarElm = (VariableElement) tempElm;

				for (String[] tempLocStr : targetVal)
				{
					if (tempVarElm.getSimpleName().toString().equals(tempLocStr[1]))
					{
						if (tempLocStr[0].equals("mapto") | tempLocStr[0].equals("maptofrom"))
						{
							value +=
							(
								//JCublas.cublasSetVector(n2, Sizeof.FLOAT, Pointer.to(h_A), 1, ptr_h_A, 1);
								"\t\tJCublas.cublasSetVector(" + tempLocStr[2] + ", Sizeof." +
								tempVarElm.asType().toString().replaceAll("\\[\\]", "").toUpperCase() +
								", Pointer.to(" + tempLocStr[1] + "), 1, ptr_" + tempLocStr[1] + ", 1);\n"
							);
						}
					}
				}
			}
		}
		value += ("\t}\n");
		return value;
	}
	/*-------------------------------------------------------<<MAPTO_END>>-------------------------------------------------------*/

	/*-------------------------------------------------------<<MAPFROM>>---------------------------------------------------------*/
	public String mapfrom()
	{
		String value = "";

		value += ("\tpublic static void mapfrom()\n");
		value += ("\t{\n");

		for (final Element tempElm : encElm)
		{
			if (tempElm instanceof VariableElement)
			{
				VariableElement tempVarElm = (VariableElement) tempElm;

				for (String[] tempLocStr : targetVal)
				{
					if (tempVarElm.getSimpleName().toString().equals(tempLocStr[1]))
					{
						if (tempLocStr[0].equals("maptofrom"))
						{
							value +=
							(
								//JCublas.cublasGetVector(n2, Sizeof.FLOAT, ptr_h_C, 1, Pointer.to(h_C), 1);
								"\t\tJCublas.cublasGetVector(" + tempLocStr[2] + ", Sizeof." +
								tempVarElm.asType().toString().replaceAll("\\[\\]", "").toUpperCase() +
								", ptr_" + tempLocStr[1] + ", 1, Pointer.to(" + tempLocStr[1] + "), 1);\n"
							);
						}
					}
				}
			}
		}
		value += ("\t}\n");
		return value;

	}
	/*-------------------------------------------------------<<MAPFROM_END>>------------------------------------------------------*/

	/*-------------------------------------------------------<<FREE>>------------------------------------------------------------*/
	public String free()
	{
		String value = "";

		value += ("\tpublic static void free()\n");
		value += ("\t{\n");

		for (final Element tempElm : encElm)
		{
			if (tempElm instanceof VariableElement)
			{
				VariableElement tempVarElm = (VariableElement) tempElm;

				for (String[] tempLocStr : targetVal)
				{
					if (tempVarElm.getSimpleName().toString().equals(tempLocStr[1]))
					{
						if (tempLocStr[0].equals("mapto") | tempLocStr[0].equals("maptofrom") | tempLocStr[0].equals("alloc"))
						{
							value +=
							(
								//JCublas.cublasFree(d_A);
								"\t\tJCublas.cublasFree(ptr_" + tempLocStr[1] + ");\n"
							);
						}
					}
				}
			}
		}
		value += "\t}\n";
		return value;
	}
	/*-------------------------------------------------------<<FREE_END>>---------------------------------------------------------*/

	/*-------------------------------------------------------<<MAIN>>-------------------------------------------------------------*/
	public String main()
	{
		String value = "";

		value += ("\tpublic static void main(String args[])\n");
		value += ("\t{\n");
		value += ("\t\tJCublas.cublasInit();\n");
		value += ("\t\tFill();\n");
		value += ("\t\talloc();\n");
		value += ("\t\tmapto();\n");
		value += ("\t\tRun();\n");
		value += ("\t\tmapfrom();\n");
		value += ("\t\tfree();\n");
		value += ("\t\tJCublas.cublasShutdown();\n");
		value += ("\t}\n");

		return value;
	}
	/*-------------------------------------------------------<<MAIN_END>>---------------------------------------------------------*/

}
