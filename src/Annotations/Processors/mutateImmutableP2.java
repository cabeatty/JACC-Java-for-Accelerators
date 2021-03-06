/*
* Created as a test processor for the test annotation Immutable
* Intended as a test to find out how to parse through the annotated class, and modify any local variables found within said class that are defined as non constant variables to make
* them constant.  Basically makes all non constant variables constant in a class annotated with @Immutable.
 */
package Annotations.Processors;

import Annotations.Immutable;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.source.util.TreePathScanner;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeTranslator;

import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.SourceVersion;

import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("Annotations.Immutable")
@SupportedSourceVersion(SourceVersion.RELEASE_8)

public class mutateImmutableP2 extends AbstractProcessor
{
	private Trees trees;

	@Override
	public void init(ProcessingEnvironment processingEnv)
	{
		super.init(processingEnv);
		trees = Trees.instance(processingEnv);
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		for (final Element element : roundEnv.getElementsAnnotatedWith(Immutable.class))
		{
			final TreePathScanner<Object, CompilationUnitTree> scanner = new TreePathScanner<Object, CompilationUnitTree>()
			{
				@Override
				public Trees visitClass(final ClassTree classTree, final CompilationUnitTree unitTree)
				{
					if (unitTree instanceof JCTree.JCCompilationUnit)
					{
						final JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit) unitTree;

						// Only process on files which have been compiled from source
						if (compilationUnit.sourcefile.getKind() == JavaFileObject.Kind.SOURCE)
						{
							compilationUnit.accept(new TreeTranslator()
							{
								public void visitVarDef(final JCVariableDecl tree)
								{
									super.visitVarDef(tree);

									if ((tree.mods.flags & Flags.FINAL) == 0)
									{
										tree.mods.flags |= Flags.FINAL;
									}
								}
							});
						}
					}
					return trees;
				}
			};
			final TreePath path = trees.getPath(element);
			scanner.scan(path, path.getCompilationUnit());
		}
		return true;
	}
}