package segmm.Annotations.Processors;

import segmm.Annotations.runSwitch;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.SourceVersion;

import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

@SupportedAnnotationTypes("segmm.Annotations.runSwitch")
@SupportedSourceVersion(SourceVersion.RELEASE_8)

public class runSwitchP extends AbstractProcessor
{
	@Override
	public boolean process(final Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
	{
		try
		{

			for (final Element elm : roundEnv.getElementsAnnotatedWith(runSwitch.class))
			{
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "1");
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, StandardLocation.CLASS_PATH.toString());
				FileObject inFile = processingEnv.getFiler().getResource
						(
								StandardLocation.SOURCE_PATH, "",
								elm.asType().toString().replace(".", "/") + ".java"
						);
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "2");
				FileObject out_file = processingEnv.getFiler().getResource
				(
						StandardLocation.SOURCE_OUTPUT, "",
						elm.asType().toString().replace(".", "/") + "_gen.java"
				);

				CharSequence data = inFile.getCharContent(false);

				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, data.toString());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
		}

		return true;
	}
}
