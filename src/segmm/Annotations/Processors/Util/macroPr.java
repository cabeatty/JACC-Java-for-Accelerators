package segmm.Annotations.Processors.Util;

import javax.lang.model.element.Element;
import java.util.List;
import segmm.Annotations.Processors.Util.fCallWriter;

public class macroPr
{
	private List<? extends Element> encElm;
	private List<String[]> targeVal;
	private List<String> ipData;
	private String className;

	public macroPr(List<? extends Element> encElm, List<String[]> targetVal, List<String> ipData, String className)
	{
		this.encElm = encElm;
		this.targeVal = targetVal;
		this.ipData = ipData;
		this.className = className;
	}

	public List<String> run()
	{
		fCallWriter fcw = new fCallWriter(encElm, targeVal);
		String runSt = functionWrapper("Run()");

		int importPos = findPos("import");
		ipData.add(importPos, "import jcuda.Pointer;");
		ipData.add(importPos, "import jcuda.Sizeof;");

		int annotationPos = findPos("@runSwitch");
		ipData.remove(annotationPos);

		int classDeclPos = findPos("class " + className);
		ipData.set(classDeclPos, "class " + className + "_gen");

		int runPos = findPos("Run()");
		ipData.add(runPos, fcw.alloc());
		ipData.add(runPos, fcw.mapto());
		ipData.add(runPos, fcw.mapfrom());
		ipData.add(runPos, fcw.free());

		functionRemover("Run()");
		ipData.add(runPos, runSt);

		int endPos = findPos("}");
		ipData.add(endPos, fcw.main());

		return ipData;
	}

	private int findPos(String line)
	{
		int positionOf = 0;

		for(String tempStr: ipData)
		{
			if (tempStr.toLowerCase().contains(line.toLowerCase()))
			{
				positionOf = ipData.indexOf(tempStr);
			}
		}

		return positionOf;
	}

	private int findEndPos(String line)
	{
		int startPos = findPos(line);
		int endPos = 0;
		int open = 0;
		int close = 0;

		for(int i = startPos; i < ipData.size(); i++)
		{
			if(open != 0 && open == close)
			{
				endPos = i;
				break;
			}

			if(ipData.get(i).contains("{"))
			{
				open++;
			}
			if(ipData.get(i).contains("}"))
			{
				close++;
			}
		}

		return endPos;
	}

	private String functionWrapper(String fName)
	{
		String wrapFn = "";
		int start = findPos(fName);
		int end = findEndPos(fName);

		for(int i = start; i < end; i++)
		{
			wrapFn += ipData.get(i) + "\n";
		}
		return wrapFn;
	}

	private void functionRemover(String fName)
	{
		int start = findPos(fName);
		int end = findEndPos(fName);

		for(int i = end; i >= start; i--)
		{
			ipData.remove(i);
		}

	}

}
