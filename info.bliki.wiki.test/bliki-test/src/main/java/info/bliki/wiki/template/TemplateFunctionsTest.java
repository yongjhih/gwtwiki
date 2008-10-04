package info.bliki.wiki.template;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TemplateFunctionsTest extends TestCase {

	protected ParserFunctionModel wikiModel = null;

	public TemplateFunctionsTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(TemplateFunctionsTest.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		wikiModel = new ParserFunctionModel("http://www.bliki.info/wiki/${image}", "http://www.bliki.info/wiki/${title}");
		wikiModel.setUp();
	}

	public void testIf01() {
		// {{ #if: {{{x| }}} | not blank | blank }} = blank
		assertEquals("blank", wikiModel.parseTemplates("{{ #if: {{{x| }}} | not blank | blank }}", false));
	}

	public void testIfEq01() {
		// {{ #ifeq: +07 | 007 | 1 | 0 }} gives 1
		assertEquals("1", wikiModel.parseTemplates("{{ #ifeq: +07 | 007 | 1 | 0 }}", false));
	}

	public void testIfEq02() {
		// {{ #ifeq: "+07" | "007" | 1 | 0 }} gives 0
		assertEquals("0", wikiModel.parseTemplates("{{ #ifeq: \"+07\" | \"007\" | 1 | 0 }}", false));
	}

	public void testIfEq03() {
		// {{ #ifeq: A | a | 1 | 0 }} gives 0
		assertEquals("0", wikiModel.parseTemplates("{{ #ifeq: A | a | 1 | 0 }}", false));
	}

	public void testIfEq05() {
		// {{ #ifeq: {{{x| }}} | | blank | not blank }} = blank,
		assertEquals("blank", wikiModel.parseTemplates("{{ #ifeq: {{{x| }}} | | blank | not blank }}", false));
	}

	public void testIfEq06() {
		// {{ #ifeq: {{{x| }}} | {{{x|u}}} | defined | undefined }} = undefined.
		assertEquals("undefined", wikiModel.parseTemplates("{{ #ifeq: {{{x| }}} | {{{x|u}}} | defined | undefined }}", false));
	}

	public void testIfEq07() {
		// {{ #ifeq: {{{x}}} | {{concat| {|{|{x}|}|} }} | 1 | 0 }} = 1
		assertEquals("{{{x}}}", wikiModel.parseTemplates("{{concat| {|{|{x}|}|} }}", false));
		
		assertEquals("1", wikiModel.parseTemplates("{{ #ifeq: {{{x}}} | {{concat| {|{|{x}|}|} }} | 1 | 0 }}", false));
	}
}