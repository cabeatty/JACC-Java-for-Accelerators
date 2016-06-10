/*
* Test source file with Main method(ExecutableElement) for processing with annotation @valPull
 */

package Tests;

import Annotations.valPull;
import Annotations.Immutable;

@Immutable
public class valPullT
{
	@valPull({"a", "b"})
	public static void main(String[] args)
	{
		int a = 1;
		int b = 2;
		int c;
		System.out.println("HelloWorld");
	}
}
