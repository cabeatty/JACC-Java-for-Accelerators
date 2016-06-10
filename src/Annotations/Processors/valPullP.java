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

import static javax.lang.model.util.ElementFilter.*;


@SupportedAnnotationTypes("Annotations.valPull")
@SupportedSourceVersion(SourceVersion.RELEASE_8)

public class valPullP extends AbstractProcessor
{
	private Trees trees;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv){
		super.init(processingEnv);
		trees = Trees.instance(processingEnv);
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		for (final Element elm : roundEnv.getElementsAnnotatedWith(valPull.class))
		{
			valPull val = elm.getAnnotation(valPull.class);
			String[] cont = val.value();

			final TreePathScanner< Object, CompilationUnitTree> scanner = new TreePathScanner<Object, CompilationUnitTree>()
			{
				@Override
				public Trees visitClass(final ClassTree classTree, final CompilationUnitTree unitTree)
				{

					return trees;
				}
			};

			/*if (elm instanceof ExecutableElement)
			{
				final ExecutableElement executableElement = (ExecutableElement) elm;
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, executableElement.getSimpleName().toString());

				for(final Element encElm: executableElement.getEnclosedElements())
				{
					processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "2\n");
					if(encElm instanceof VariableElement)
					{
						final VariableElement variableElement = (VariableElement) encElm;
						String name = variableElement.getSimpleName().toString();
						String type = variableElement.asType().toString();
						String full = ("Found: " + type + " " + name + "\n");
						processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, full);
					}
				}
			}*/

			final TreePath path = trees.getPath( elm );
			scanner.scan( path, path.getCompilationUnit() );
		}
		return true;
	}
}
