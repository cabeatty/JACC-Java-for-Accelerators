package segmm.Annotations.Processors;

import segmm.Annotations.targetData;
import segmm.Annotations.Processors.Util.fCallWriterBu;

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

				try //Auto compile/run must be done here (Only place in which fileObject exists)
				{
					final String oldClassName = typeElement.getSimpleName().toString();                 //Name of the annotated class
					final String oldClassPath = packageElement.getQualifiedName() + "." + oldClassName; //File path to the annotated class
					final String newClassName = typeElement.getSimpleName().toString() + "Bp";          //Name of the generated class
					final String newClassPath = packageElement.getQualifiedName() + "." + newClassName; //File path to the generated class
					//final String oldClassObj = oldClassName + "Obj";

					//Creates new class with same name as annotated class appended with 'Bp'
					final JavaFileObject fileObject = processingEnv.getFiler().createSourceFile
							(
									newClassPath
							);
					try(Writer wr = fileObject.openWriter())
					{
						fCallWriterBu gtr = new fCallWriterBu(typeElement.getEnclosedElements(), valLoc, oldClassName, newClassName);

						//Package, import, and class declaration
						wr.append("package " + packageElement.getQualifiedName() + ";\n\n");
						wr.append("import jcuda.Pointer;\n");
						wr.append("import jcuda.Sizeof;\n");
						wr.append("import jcuda.jcublas.JCublas;\n");
						wr.append("import " + oldClassPath + ";\n\n");
						wr.append("public class " + newClassName + " \n{\n" );

						wr.append(gtr.constructor());
						wr.append(gtr.alloc());
						wr.append(gtr.mapto());
						wr.append(gtr.mapfrom());
						wr.append(gtr.free());

						/*-------------------------------------------------------<<MAIN>>------------------------------------------------------------*/
						/*Creates a main method using the methods created
						earlier and the methods Run() and Fill() in the
						annotated class*/

						wr.append("\tpublic static void main(String args[])\n");
						wr.append("\t{\n");
						wr.append("\t\tJCublas.cublasInit();\n");
						wr.append("\t\t" + oldClassName + ".Fill();\n");
						wr.append("\t\tmapto();\n");
						wr.append("\t\t" + oldClassName + ".Run();\n");
						wr.append("\t\tmapfrom();\n");
						wr.append("\t\tfree();\n");
						wr.append("\t\tJCublas.cublasShutdown();\n");

						wr.append("\t}\n");
						/*-------------------------------------------------------<<MAIN_END>>--------------------------------------------------------*/

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
