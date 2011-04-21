package info.bliki.wiki.filter;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TimeFilterTest extends FilterTestSupport {
	public TimeFilterTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public static Test suite() {
		return new TestSuite(TimeFilterTest.class);
	}

	public void testDate001() {
		// TODO implement more options for time
		assertEquals("\n" + 
				"<p><span class=\"error\">Error: invalid time</span></p>", wikiModel.render("{{#time: d F Y | 29 Feb 2004 }}"));
	}
}