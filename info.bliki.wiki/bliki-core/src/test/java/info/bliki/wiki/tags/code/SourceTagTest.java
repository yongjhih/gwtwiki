package info.bliki.wiki.tags.code;

import info.bliki.wiki.filter.FilterTestSupport;
import junit.framework.Test;
import junit.framework.TestSuite;

public class SourceTagTest extends FilterTestSupport {
	public SourceTagTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(SourceTagTest.class);
	}

	public void testUnknownSourceTag001() {
		String result = wikiModel.render("start <source lang=unknown>Hello World</source> end");

		assertEquals("\n" + "<p>start </p><pre class=\"unknown\">Hello World</pre> end", result);
	}

	public void testUnknownSourceTag002() {
		String result = wikiModel.render("start <source lang=unknown>first line\n second line\n <html> third line\n</source> end");

		assertEquals("\n" + 
				"<p>start </p><pre class=\"unknown\">first line\n" + 
				" second line\n" + 
				" &#60;html&#62; third line\n" + 
				"</pre> end", result);
	}
}
