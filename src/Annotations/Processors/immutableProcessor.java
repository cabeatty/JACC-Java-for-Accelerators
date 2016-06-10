/*
* Created as a test processor for the test annotation Immutable
* Intended as a test to find out how to parse through the annotated class, and modify any local variables found within said class that are defined as non constant variables to make
* them constant.  Basically makes all non constant variables constant.
 */
package Annotations.Processors;

import Annotations.Immutable;

import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("Annotations.Immutable")
@SupportedSourceVersion(SourceVersion.RELEASE_8)

public class immutableProcessor extends AbstractProcessor
{
	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		for( final Element element: roundEnv.getElementsAnnotatedWith(Immutable.class))
		{
			if( element instanceof TypeElement)
			{
				final TypeElement typeElement = (TypeElement)element;

				for( final Element eclosedElement: typeElement.getEnclosedElements() )
				{
					if( eclosedElement instanceof VariableElement)
					{
						final VariableElement variableElement = (VariableElement)eclosedElement;

						if(!variableElement.getModifiers().contains(Modifier.FINAL) )
						{
							processingEnv.getMessager().printMessage
							(
								Diagnostic.Kind.NOTE,
								String.format("Class %s is annotated as @Immutable, " + "but %s %s is not declared as final",
								typeElement.getSimpleName(), variableElement.asType(), variableElement.getSimpleName() )
							);
						}
					}
				}
			}
		}
		return true;
	}
}