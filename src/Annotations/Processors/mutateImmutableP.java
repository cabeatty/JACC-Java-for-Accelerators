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

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("Annotations.Immutable")
@SupportedSourceVersion(SourceVersion.RELEASE_8)

public class mutateImmutableP extends AbstractProcessor
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
			Immutable val = element.getAnnotation(Immutable.class);
			String[] valIp = val.value();

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

									for(String targetD: valIp)
									{
										if (tree.name.toString().equals(targetD))
										{
											int x = tree.getPreferredPosition();
											processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Found target: " + targetD + "\nPosition: " + x);


										}
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
