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
		assertEquals("", wikiModel.render("{{#time: d F Y | 29 Feb 2004 }}"));
	}
}