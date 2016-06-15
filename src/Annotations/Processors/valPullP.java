/*
* Created as a test processor for the test annotation valPull
* Intended as a test to find out how to parse through the annotated class, and use comparison to strings passed into the annotation to locate desired
* variables for processing within the annotated class.  Also test on how to modify said annotated class.
 */

package Annotations.Processors;

import Annotations.valPull;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

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



@SupportedAnnotationTypes("Annotations.valPull")
@SupportedSourceVersion(SourceVersion.RELEASE_8)

public class valPullP extends AbstractProcessor
{
	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		for (final Element elm : roundEnv.getElementsAnnotatedWith(valPull.class))
		{
			valPull val = elm.getAnnotation(valPull.class);
			int count = val.value().length;                 //length of the array value() stored within the annotation
			String[] valIp = val.value();                   //copy's information from value() to local String[] valIp
			List<String[]> valLoc = new ArrayList<>();

			for(int i = 0; i < count; i++)
			{
				valIp[i] = valIp[i].replaceAll("]", "");
				String[] z = valIp[i].split("\\[");
				valLoc.add(z);
			}

			if (elm instanceof TypeElement)
			{
				final TypeElement typeElement = (TypeElement) elm;
				final PackageElement packageElement = (PackageElement)typeElement.getEnclosingElement();

				try
				{
					final String oldClassName = typeElement.getSimpleName().toString();
					final String oldClassPath = packageElement.getQualifiedName() + "." + oldClassName;
					final String newClassName = typeElement.getSimpleName().toString() + "Bp";
					final String newClassPath = packageElement.getQualifiedName() + "." + newClassName;

					final JavaFileObject fileObject = processingEnv.getFiler().createSourceFile
							(
									newClassPath
							);
					try(Writer wr = fileObject.openWriter())
					{
						wr.append("package " + packageElement.getQualifiedName() + ";\n\n");
						wr.append("import jcuda.Pointer;\n");
						wr.append("import jcuda.Sizeof;\n");
						wr.append("import jcuda.jcublas.JCublas;\n");
						wr.append("import " + oldClassPath + ";\n\n");
						wr.append( "public class " + newClassName + " \n{\n" );

						wr.append("\tpublic static void mapto()\n");
						wr.append("\t{\n");

						for(final Element encElm: typeElement.getEnclosedElements())
						{
							if(encElm instanceof VariableElement)
							{
								VariableElement tempVar = (VariableElement) encElm;
								for(String[] valLocStr : valLoc)
								{
									if(tempVar.getSimpleName().toString().equals(valLocStr[0]))
									{
										wr.append(
												"\t\tJCublas.cublasAlloc(" + oldClassName + ".n2, Sizeof." +
												tempVar.asType().toString().replaceAll("\\[\\]", "").toUpperCase() +
												", " + oldClassName + "." + valLocStr[1] +  ");\n"
												);
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
									if(tempVar.getSimpleName().toString().equals(valLocStr[0]))
									{
										wr.append(
												"\t\tJCublas.cublasSetVector(" + oldClassName  + ".n2, Sizeof." +
												tempVar.asType().toString().replaceAll("\\[\\]", "").toUpperCase() +
												", Pointer.to(" + oldClassName + "." + valLocStr[0] + "), 1, " +
												oldClassName + "." + valLocStr[1] + ", 1);\n"
										);
									}
								}
							}
						}
						wr.append("\t}\n");

						wr.append("\n");

						wr.append("\tpublic static void free()\n");
						wr.append("\t{\n");

						for(final Element encElm: typeElement.getEnclosedElements())
						{
							if (encElm instanceof VariableElement)
							{
								VariableElement tempVar = (VariableElement) encElm;
								for(String[] valLocStr : valLoc)
								{
									if(tempVar.getSimpleName().toString().equals(valLocStr[0]))
									{
										wr.append(
												"\t\tJCublas.cublasFree(" + oldClassName + "." + valLocStr[1] + ");\n"
										);
									}
								}
							}
						}

						wr.append("\t}\n");
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
