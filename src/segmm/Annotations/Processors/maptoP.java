/*
* Processor for the test annotation @mapto
* At this point all it does is create a new source file and populates it with the necessary boilerplate code, and nothing else
* Still exploring how to add the boilerplate code directly into the annotated class
 */
package segmm.Annotations.Processors;

import segmm.Annotations.mapto;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("segmm.Annotations.mapto")
@SupportedSourceVersion(SourceVersion.RELEASE_8)

public class maptoP extends AbstractProcessor
{
	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)   //Initiates the processor
	{
		//defines elm as the RoundEnvironment that has the annotation mapto, and iterates through each RoundEnvironment with said annotation, executing below code each time
		for( final Element elm : roundEnv.getElementsAnnotatedWith(mapto.class))
		{
			mapto val = elm.getAnnotation(mapto.class);     //declares the annotation that is used so variables within can be accessed
			int count = val.value().length;                 //length of the array value() stored within the annotation

			String[] valIp = val.value();                   //copy's information from value() to local String[] valIp
			List<String[]> valLoc = new ArrayList<>();

			/*for(int i = 0; i < count; i++)
			{
				valIp[i] = valIp[i].replaceAll("]", "");
				String[] z = valIp[i].split("\\[");
				valLoc.add(z);
			}*/

			//Splits each string in the valIp array into individual arrays all of length 4 based on "," to separate each new string, then adds said array to the list of string arrays valLoc
			//In each new array index 0 is the name of the original vector, index 1 is the name of the target pointer, index 2 is the variable tpe, and index 3 is the size of the vector
			for(int i = 0; i < count; i++)
			{
				String[] z = valIp[i].split(",");
				valLoc.add(z);
			}

			//Only processes Annotations that are on the main method itself
			if( elm instanceof ExecutableElement)
			{
				final ExecutableElement executableElement = (ExecutableElement)elm;                     //declares said ExecutableElement as executableElement
				final TypeElement typeElement = (TypeElement)executableElement.getEnclosingElement();   //declares the enclosing element (class) of executableElement as typeElement
				final PackageElement packageElement = (PackageElement)typeElement.getEnclosingElement();//declares the enclosing element (package) of typeElement as packageElement
				try
				{
					final String className = typeElement.getSimpleName() + "Bp";    //Creates a new name for the new class, uses name of the existing class and appends "Bp" tp the end
					final JavaFileObject fileObject = processingEnv.getFiler().createSourceFile //Creates the new source file, and declares it as a JavaFileObject
							(
									packageElement.getQualifiedName() + "." + className
							);

					try (Writer writter = fileObject.openWriter())
					{
						//Simply writes all the boilerplate code into the new file based off of the information given
						writter.append("package " + packageElement.getQualifiedName() + ";\n\n");

						writter.append("import jcuda.Pointer;\n");
						writter.append("import jcuda.Sizeof;\n");
						writter.append("import jcuda.jcublas.JCublas;\n\n");

						writter.append( "public class " + className + " \n{\n" );
						writter.append("\tpublic static void main(String[] args) \n\t{\n");

						for(int i=0; i < valLoc.size(); i++)
						{
							String[] temp = valLoc.get(i);
							writter.append("\t\tCublas.cublasAlloc(" + temp[3] + ", Sizeof." + temp[2].toUpperCase() + ", " + temp[1] + ");\n");
						}

						for(int i=0; i < valLoc.size(); i++)
						{
							String[] temp = valLoc.get(i);
							writter.append("\t\tJCublas.cublasSetVector(n2, Sizeof.FLOAT, Pointer.to(" + temp[0] + "), 1, " + temp[1] + ", 1);\n");
						}

						for(int i=0; i < valLoc.size(); i++)
						{
							String[] temp = valLoc.get(i);
							writter.append("\t\tJCublas.cublasFree(" + temp[0] + ");\n");
						}

						writter.append("\t}\n");
						writter.append( "}");
					}
				}
				catch(final IOException ex)
				{
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
				}

				//Prints out where the annotation id found on the annotated class, for debug
				String message = String.format("Annotation found on %s, in %s class, in %s package",
						executableElement.getSimpleName(), typeElement.getSimpleName(), packageElement.getQualifiedName());
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);

				String message2 = "";
				for(int i=0; i < count; i++)//Prints out the contents of the valIp String[] to the processingEnvironments messenger as a note, for debugging purposes
				{
					message2 += (valIp[i] + " ");
				}
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message2);

			}
		}

		//annotations have been processed by this processor
		return true;
	}
}

