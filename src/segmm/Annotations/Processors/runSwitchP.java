package segmm.Annotations.Processors;

import com.sun.tools.internal.xjc.model.CClassInfoParent;
import segmm.Annotations.runSwitch;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.SourceVersion;

import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import segmm.Annotations.Processors.Util.macroPr;

@SupportedAnnotationTypes("segmm.Annotations.runSwitch")
@SupportedSourceVersion(SourceVersion.RELEASE_8)

public class runSwitchP extends AbstractProcessor
{
	@Override
	public boolean process(final Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
	{
		for (final Element elm : roundEnv.getElementsAnnotatedWith(runSwitch.class))
		{
			runSwitch val = elm.getAnnotation(runSwitch.class);
			String[] targetIp = val.target();
			String path = val.path();
			List<String[]> valLoc = new ArrayList<>();

			for(int i = 0; i < targetIp.length; i++)
			{
				targetIp[i] = targetIp[i].replaceAll(" ", "");
				String[] z = targetIp[i].split(",");
				valLoc.add(z);
			}

			if (elm instanceof TypeElement)
			{
				TypeElement typeElement = (TypeElement) elm;
				PackageElement packageElement = (PackageElement) typeElement.getEnclosingElement();

				final String oldClassName = typeElement.getSimpleName().toString() + ".java";
				final String newClassName = typeElement.getSimpleName().toString() + "_gen.java";
				final String classPackage = packageElement.getQualifiedName().toString().replace(".", "\\");

				final Path inFile = Paths.get(path + "\\" + classPackage + "\\" + oldClassName);
				final Path outFile = Paths.get(path + "\\" + classPackage + "\\" + newClassName);
				List<String> content = new ArrayList<>();

				try
				{
					content = Files.readAllLines(inFile);
					OutputStream out = Files.newOutputStream(outFile);
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

					int i = 1;
					for(String s : content)
					{
						processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, i + " " + s);
						i++;
					}

					macroPr mpr = new macroPr(typeElement.getEnclosedElements(), valLoc, content, elm.getSimpleName().toString());
					content = mpr.run();

					for(String line: content)
					{
						writer.append(line + "\n");
					}

					writer.close();

				}
				catch (IOException x)
				{
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, x.toString());
				}
			}


		}

		return true;
	}
}
