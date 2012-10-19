package info.bliki.wiki.namespaces;

import info.bliki.wiki.namespaces.Namespace.NamespaceValue;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for {@link Namespace}.
 * 
 * @author Nico Kruber, kruber@zib.de
 */
public class NamespaceTest extends TestCase {

	public NamespaceTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(NamespaceTest.class);
	}

	/**
	 * Checks whether all content spaces are set.
	 */
	public void testEnsureContentSpacesNotNull() {
		Namespace namespaceObj = new Namespace();
		for (int i = -2; i <= 15; ++i) {
			NamespaceValue namespace = namespaceObj.getNamespaceByNumber(i);
			assertNotNull("contentspace of " + i + ", " + namespace, namespace.getContentspace());
		}
	}

}
