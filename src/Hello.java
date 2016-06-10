import Annotations.Math;
import Annotations.valPull;
import Annotations.someMethods;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;


public class Hello
{
	public static void main(String[] args)
	{
		final int a = 1;
		final int b = 1;
		int c;

		/*someMethods s = new someMethods(1,3);
		//@Math
		c = s.add();
		System.out.println(c);
		s.x = 10;
		c = s.add();
		System.out.println(c);

		Annotation ann = Hello.class.getAnnotation(Doc.class);
		Doc doc = (Doc) ann;
		System.out.printf("Name: " + doc.author() + "\n");
		System.out.printf("Email: " + doc.email() + "\n");*/

		String[] x = new String[2];
		x[0] = "h_A[d_A]";
		x[1] = "h_B[d_B]";
		List<String[]> y = new ArrayList<>();

		for(String s : x)
		{
			System.out.println(s);
		}
		System.out.println("------------------------");

		for(int i = 0; i < x.length; i++)
		{
			x[i] = x[i].replaceAll("]", "");
			String[] z = x[i].split("\\[");
			y.add(z);
		}

		for(int i=0; i < y.size(); i++)
		{
			String[] temp = y.get(i);
			System.out.println(temp[0] + " " + temp[1]);
		}
	}
}