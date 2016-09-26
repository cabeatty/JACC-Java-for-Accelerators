package Annotations.Processors;

import Annotations.PointerInj;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.lang.reflect.Field;
import java.util.Set;

@SupportedAnnotationTypes("Annotations.PointerInj")
@SupportedSourceVersion(SourceVersion.RELEASE_8)

public class PointerInjP extends AbstractProcessor
{
	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		for (final Element elm : roundEnv.getElementsAnnotatedWith(PointerInj.class))
		{
			try
			{
				PointerInj val = elm.getAnnotation(PointerInj.class);
				String[] valIp = val.value();
				String clStr = val.Class();

				Class<?> cl = Class.forName(clStr);
				String packageName = cl.getPackage().getName();

				for (Field field: cl.getDeclaredFields())
				{
					for(String targetName : valIp)
					{
						if (field.getName().equals(targetName)) ;
						{

						}
					}
				}
			}
			catch(java.lang.ClassNotFoundException e)
			{
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
			}

		}
		return true;
	}
}
