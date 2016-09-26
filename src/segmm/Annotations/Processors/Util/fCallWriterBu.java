package segmm.Annotations.Processors.Util;

import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;


public class fCallWriterBu
{
	List<? extends Element> encElm;
	List<String[]> valLocStr;
	String oldClassName;
	String newClassName;

	public fCallWriterBu(List<? extends Element> encElm, List<String[]> valLocStr, String oldClassName, String newClassName)
	{
		this.encElm = encElm;
		this.valLocStr = valLocStr;
		this.oldClassName = oldClassName;
		this.newClassName = newClassName;
	}

	/*-------------------------------------------------------<<CONSTRUCTOR>>------------------------------------------------------*/
	public String constructor()
	{
		String value = "";
		value += ("\tpublic " + newClassName + "()\n");
		value += ("\t{\n");
		value += ("\t}\n");
		value += ("\n");

		return value;
	}
	/*-------------------------------------------------------<<CONSTRUCTOR_END>>--------------------------------------------------*/

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

				for (String[] tempLocStr : valLocStr)
				{
					if (tempVarElm.getSimpleName().toString().equals(tempLocStr[1]))
					{
						if (tempLocStr[0].equals("mapto") | tempLocStr[0].equals("maptofrom") | tempLocStr[0].equals("alloc"))
						{
							value +=
									(
											//JCublas.cublasAlloc(segmmTest.n2, Sizeof.FLOAT, segmmTest.ptr_h_A);
											"\t\tJCublas.cublasAlloc(" + oldClassName + "." + tempLocStr[2] + ", Sizeof." +
													tempVarElm.asType().toString().replaceAll("\\[\\]", "").toUpperCase() + ", " +
													oldClassName + ".ptr_" + tempLocStr[1] + ");\n"
									);
						}
					}
				}
			}
		}
		value += "\t}\n";
		value += "\n";
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

				for (String[] tempLocStr : valLocStr)
				{
					if (tempVarElm.getSimpleName().toString().equals(tempLocStr[1]))
					{
						if (tempLocStr[0].equals("mapto") | tempLocStr[0].equals("maptofrom"))
						{
							value +=
									(
											//JCublas.cublasSetVector(segmmTest.n2, Sizeof.FLOAT, Pointer.to(segmmTest.h_A), 1, segmmTest.ptr_h_A, 1);
											"\t\tJCublas.cublasSetVector(" + oldClassName + "." + tempLocStr[2] + ", Sizeof." +
													tempVarElm.asType().toString().replaceAll("\\[\\]", "").toUpperCase() +
													", Pointer.to(" + oldClassName + "." + tempLocStr[1] + "), 1, " + oldClassName +
													".ptr_" + tempLocStr[1] + ", 1);\n"
									);
						}
					}
				}
			}
		}
		value += ("\t}\n");
		value += ("\n");
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

				for (String[] tempLocStr : valLocStr)
				{
					if (tempVarElm.getSimpleName().toString().equals(tempLocStr[1]))
					{
						if (tempLocStr[0].equals("maptofrom"))
						{
							value +=
									(
											//JCublas.cublasGetVector(segmmTest.n2, Sizeof.FLOAT, segmmTest.ptr_h_C, 1, Pointer.to(segmmTest.h_C), 1);
											"\t\tJCublas.cublasGetVector(" + oldClassName + "." + tempLocStr[2] + ", Sizeof." +
													tempVarElm.asType().toString().replaceAll("\\[\\]", "").toUpperCase() +
													", " + oldClassName + ".ptr_" + tempLocStr[1] + ", 1, Pointer.to(" + oldClassName +
													"." + tempLocStr[1] + "), 1);\n"
									);
						}
					}
				}
			}
		}
		value += ("\t}\n");
		value += ("\n");
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

				for (String[] tempLocStr : valLocStr)
				{
					if (tempVarElm.getSimpleName().toString().equals(tempLocStr[1]))
					{
						if (tempLocStr[0].equals("mapto") | tempLocStr[0].equals("maptofrom") | tempLocStr[0].equals("alloc"))
						{
							value +=
									(
											//JCublas.cublasFree(d_A);
											"\t\tJCublas.cublasFree(" + oldClassName + ".ptr_" + tempLocStr[1] + ");\n"
									);
						}
					}
				}
			}
		}
		value += "\t}\n";
		value += "\n";
		return value;
	}
	/*-------------------------------------------------------<<FREE_END>>---------------------------------------------------------*/

}
