package segmm.Annotations.Processors;


import segmm.Annotations.targetData;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.SourceVersion;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;



@SupportedAnnotationTypes("segmm.Annotations.targetData")
@SupportedSourceVersion(SourceVersion.RELEASE_8)

public class targetDataP extends AbstractProcessor
{
	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		for (final Element elm : roundEnv.getElementsAnnotatedWith(targetData.class))
		{
			targetData val = elm.getAnnotation(targetData.class);
			int count = val.value().length;                 //length of the array value() stored within the annotation
			String[] valIp = val.value();                   //copy's information from value() to local String[] valIp
			List<String[]> valLoc = new ArrayList<>();      //Array list for contents of the Value String

			//separates contents of each string in valIp by , and adds each new array into valLoc
			//index [0] should be the desired function (mapto/maptofrom)
			//index [1] should be the name of the var on the host
			//index [2] should be the name of the pointer to be sent to the GPU
			for(int i = 0; i < count; i++)
			{
				valIp[i] = valIp[i].replaceAll(" ", "");
				String[] z = valIp[i].split(",");
				valLoc.add(z);
			}

			for(String[] z : valLoc)    //For debug purposes, sends diagnostic message upon compilation of the annotated class containing contents of valLoc String array
			{
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"String Found: " + z[0] + " " + z[1] + " " + z[2] + "\n");
			}

			if (elm instanceof TypeElement) //Checks to see if annotated element is of type TypeElement(Class)
			{
				final TypeElement typeElement = (TypeElement) elm;
				final PackageElement packageElement = (PackageElement)typeElement.getEnclosingElement();  //Package

				try
				{
					final String oldClassName = typeElement.getSimpleName().toString();                 //Name of the annotated class
					final String oldClassPath = packageElement.getQualifiedName() + "." + oldClassName; //File path to the annotated class
					final String newClassName = typeElement.getSimpleName().toString() + "Bp";          //Name of the generated class
					final String newClassPath = packageElement.getQualifiedName() + "." + newClassName; //File path to the generated class
					final String oldClassObj = oldClassName + "Obj";

					//Creates new class with same name as annotated class appended with 'Bp'
					final JavaFileObject fileObject = processingEnv.getFiler().createSourceFile
							(
									newClassPath
							);
					try(Writer wr = fileObject.openWriter())
					{
						//Package, import, and class declaration
						wr.append("package " + packageElement.getQualifiedName() + ";\n\n");
						wr.append("import jcuda.Pointer;\n");
						wr.append("import jcuda.Sizeof;\n");
						wr.append("import jcuda.jcublas.JCublas;\n");
						wr.append("import " + oldClassPath + ";\n\n");
						wr.append("public class " + newClassName + " \n{\n" );
						wr.append("\tpublic static " + oldClassName + " " + oldClassObj + " = new " + oldClassName + "();\n\n");

						/*---------------------------------------------<<MAPTO>>-------------------------------------------------*/
						/*creates new method 'mapto' that handles
						sending the correct variables to the GPU*/

						wr.append("\tpublic static void mapto()\n");
						wr.append("\t{\n");
						for(final Element encElm: typeElement.getEnclosedElements())    //loops through enclosed elements in annotated class
						{
							if(encElm instanceof VariableElement)   //Only instances of Variable elements will be processed
							{
								VariableElement tempVar = (VariableElement) encElm;
								for(String[] valLocStr : valLoc)    //loops through String arrays stored in list valLoc, desired targets
								{
									if(tempVar.getSimpleName().toString().equals(valLocStr[1])) //Only Variables with same name as desired target will be processed, loops until one is found
									{
										if (valLocStr[0].equals("mapto") | valLocStr[0].equals("maptofrom"))    //Checks syntax of array and makes sure user wants to send said variable to the GPU
										{
											wr.append(
													"\t\tJCublas.cublasAlloc(" + oldClassObj + "." + valLocStr[1] + ".length, Sizeof." +
													tempVar.asType().toString().replaceAll("\\[\\]", "").toUpperCase() +
													", " + oldClassObj + "." + valLocStr[2] + ");\n"
											);  //This writes the JCublas.cublasAlloc(...) function for each of the desired variables into the new file
										}
									}
								}
							}
						}
						wr.append("\n");

						for(final Element encElm: typeElement.getEnclosedElements())
						{
							if (encElm instanceof VariableElement)
							{
								VariableElement tempVar = (VariableElement) encElm;
								for(String[] valLocStr : valLoc)
								{
									if(tempVar.getSimpleName().toString().equals(valLocStr[1]))
									{
										if (valLocStr[0].equals("mapto") | valLocStr[0].equals("maptofrom"))
										{
											wr.append(
													"\t\tJCublas.cublasSetVector(" + oldClassObj + "." + valLocStr[1] + ".length, Sizeof." +
													tempVar.asType().toString().replaceAll("\\[\\]", "").toUpperCase() +
													", Pointer.to(" + oldClassObj + "." + valLocStr[1] + "), 1, " +
													oldClassObj + "." + valLocStr[2] + ", 1);\n"
											);  //This writes the JCublas.cublasSetVector(...) function for each of the desired variables into the new file
										}
									}
								}
							}
						}
						wr.append("\t}\n");
						/*---------------------------------------------<<MAPTO_END>>---------------------------------------------*/

						wr.append("\n");

						/*---------------------------------------------<<MAPTOFROM>>---------------------------------------------*/
						/*creates new method 'maptofrom' that handles
						pulling the correct variables back from the
						GPU after computation is done*/

						wr.append("\tpublic static void maptofrom()\n");
						wr.append("\t{\n");
						for(final Element encElm: typeElement.getEnclosedElements())
						{
							if (encElm instanceof VariableElement)
							{
								VariableElement tempVar = (VariableElement) encElm;
								for(String[] valLocStr : valLoc)
								{
									if(tempVar.getSimpleName().toString().equals(valLocStr[1]))
									{
										if (valLocStr[0].equals("maptofrom"))   //Only processes variables flagged with maptofrom
										{
											wr.append(
													"\t\tJCublas.cublasGetVector(" + oldClassObj + "." + valLocStr[1] + ".length, Sizeof." +
													tempVar.asType().toString().replaceAll("\\[\\]", "").toUpperCase() +
													", " + oldClassObj + "." + valLocStr[2] + ", 1, Pointer.to(" + oldClassObj +
													"." + valLocStr[1] + "), 1);\n"
											);  //This writes the JCublas.cublasGetVector(...) function for each of the desired variables into the new file
										}
									}
								}
							}
						}
						wr.append("\t}\n");
						/*---------------------------------------------<<MAPTOFROM_END>>-----------------------------------------*/

						wr.append("\n");

						/*---------------------------------------------<<FREE>>--------------------------------------------------*/
						/*creates new method 'free' that handles clearing
						the memory from the GPU once the computation is
						finished*/

						wr.append("\tpublic static void free()\n");
						wr.append("\t{\n");
						for(final Element encElm: typeElement.getEnclosedElements())
						{
							if (encElm instanceof VariableElement)
							{
								VariableElement tempVar = (VariableElement) encElm;
								for(String[] valLocStr : valLoc)
								{
									if(tempVar.getSimpleName().toString().equals(valLocStr[1]))
									{
										if (valLocStr[0].equals("mapto") | valLocStr[0].equals("maptofrom"))
										{
											wr.append(
													"\t\tJCublas.cublasFree(" + oldClassObj + "." + valLocStr[2] + ");\n"
											);  //This writes the JCublas.cublasFree(...) function for each of the desired variables into the new file
										}
									}
								}
							}
						}
						wr.append("\t}\n");
						/*---------------------------------------------<<FREE_END>>----------------------------------------------*/

						wr.append("\n");

						/*---------------------------------------------<<MAIN>>--------------------------------------------------*/
						/*Creates a main method using the methods created
						earlier and the methods Run() and Fill() in the
						annotated class*/

						wr.append("\tpublic static void main(String args[])\n");
						wr.append("\t{\n");
						wr.append("\t\tJCublas.cublasInit();\n");
						wr.append("\t\t" + oldClassObj + ".Fill();\n");
						wr.append("\t\tmapto();\n");
						wr.append("\t\t" + oldClassObj + ".Run();\n");
						wr.append("\t\tmaptofrom();\n");
						wr.append("\t\tfree();\n");
						wr.append("\t\tJCublas.cublasShutdown();\n");

						wr.append("\t}\n");
						/*---------------------------------------------<<MAIN_END>>----------------------------------------------*/

						wr.append( "}");
					}
				}
				catch(final IOException ex)
				{
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
				}
			}
			else
			{
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@valLoc needs to be on TypeElement");
			}
		}
		return false;
	}
}
