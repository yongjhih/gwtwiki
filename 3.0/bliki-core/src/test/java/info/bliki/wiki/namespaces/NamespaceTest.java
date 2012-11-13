package info.bliki.wiki.namespaces;

import java.util.Locale;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NamespaceTest extends TestCase {
	protected Namespace namespace = null;
	
	public NamespaceTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(NamespaceTest.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		namespace = new Namespace(Locale.ENGLISH);
	}

	public void testNamespace001() {
		assertEquals("Meta", namespace.getNamespace("Meta"));
		assertEquals("Meta_talk", namespace.getNamespace("Meta_talk")); // TODO: should have a space instead!
		assertEquals("Meta", namespace.getNamespace("Project"));
		assertEquals("Meta_talk", namespace.getNamespace("Project_talk")); // TODO: should have a space instead!
	}

	public void testTalkspace001() {
		// TODO: talkspaces should have spaces instead!
		assertEquals("Meta_talk", namespace.getTalkspace("Meta"));
		assertEquals("Meta_talk", namespace.getTalkspace("Meta_talk"));
		assertEquals("Meta_talk", namespace.getTalkspace("Project"));
		assertEquals("Meta_talk", namespace.getTalkspace("Project_talk"));
	}

	public void testContentspace001() {
		assertEquals("Meta", namespace.getContentspace("Meta"));
		assertEquals("Meta", namespace.getContentspace("Meta_talk"));
		assertEquals("Meta", namespace.getContentspace("Project"));
		assertEquals("Meta", namespace.getContentspace("Project_talk"));
	}
}
