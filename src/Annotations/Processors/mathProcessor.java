package Annotations.Processors;

import Annotations.Math;

import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;


@SupportedAnnotationTypes("Annotations.Math")
@SupportedSourceVersion(SourceVersion.RELEASE_8)

public class mathProcessor extends AbstractProcessor
{
	public mathProcessor()
	{
		super();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
	{
		for(Element elm : roundEnv.getElementsAnnotatedWith(Math.class))
		{
			Math val = elm.getAnnotation(Math.class);

			String message = String.format("Annotation found on %s %s \nx: %s \ny: %s \nz: %s",
					elm.getKind(), elm.getSimpleName(), val.x(), val.y(), val.z());

			processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, message);
		}
		return true;
	}
}
