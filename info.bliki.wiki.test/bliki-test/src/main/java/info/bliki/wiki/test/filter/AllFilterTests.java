package info.bliki.wiki.test.filter;

import info.bliki.wiki.filter.BBCodeFilterTest;
import info.bliki.wiki.filter.BasicFilterTest;
import info.bliki.wiki.filter.BoldFilterTest;
import info.bliki.wiki.filter.CiteFilterTest;
import info.bliki.wiki.filter.DefinitionListFilterTest;
import info.bliki.wiki.filter.DivFilterTest;
import info.bliki.wiki.filter.EntityFilterTest;
import info.bliki.wiki.filter.FontFilterTest;
import info.bliki.wiki.filter.HRBRTest;
import info.bliki.wiki.filter.HTMLTableFilterTest;
import info.bliki.wiki.filter.HTTPUrlFilterTest;
import info.bliki.wiki.filter.HeaderFilterTest;
import info.bliki.wiki.filter.ItalicFilterTest;
import info.bliki.wiki.filter.MathFilterTest;
import info.bliki.wiki.filter.PreFilterTest;
import info.bliki.wiki.filter.PreFormattedFilterTest;
import info.bliki.wiki.filter.RefFilterTest;
import info.bliki.wiki.filter.TOCFilterTest;
import info.bliki.wiki.filter.TagFilterTest;
import info.bliki.wiki.filter.TemplateFilterTest;
import info.bliki.wiki.filter.TemplateParserTest;
import info.bliki.wiki.filter.WPImageFilterTest;
import info.bliki.wiki.filter.WPLinkFilterTest;
import info.bliki.wiki.filter.WPListFilterTest;
import info.bliki.wiki.filter.WPSemanticLinkTest;
import info.bliki.wiki.filter.WPTableFilterTest;
import info.bliki.wiki.filter.WrongTagFilterTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllFilterTests extends TestCase {
	public AllFilterTests(String name) {
		super(name);
	}

	public static Test suite() {
		TestSuite s = new TestSuite();

		s.addTestSuite(BasicFilterTest.class);
		s.addTestSuite(BBCodeFilterTest.class);
		s.addTestSuite(BoldFilterTest.class);
		s.addTestSuite(DefinitionListFilterTest.class);
		s.addTestSuite(CiteFilterTest.class);
		s.addTestSuite(DivFilterTest.class);
//		s.addTestSuite(EmbedFilterTest.class);
		s.addTestSuite(EntityFilterTest.class);
		s.addTestSuite(FontFilterTest.class);
		s.addTestSuite(HeaderFilterTest.class);
		s.addTestSuite(HRBRTest.class);
		s.addTestSuite(HTMLTableFilterTest.class);
		s.addTestSuite(HTTPUrlFilterTest.class);
		s.addTestSuite(ItalicFilterTest.class);
		s.addTestSuite(MathFilterTest.class);
		s.addTestSuite(PreFilterTest.class);
		s.addTestSuite(PreFormattedFilterTest.class);
		s.addTestSuite(RefFilterTest.class);
		s.addTestSuite(TagFilterTest.class);
		s.addTestSuite(TemplateFilterTest.class);
		s.addTestSuite(TOCFilterTest.class);
		s.addTestSuite(WPImageFilterTest.class);
		s.addTestSuite(WPLinkFilterTest.class);
		s.addTestSuite(WPListFilterTest.class);
		s.addTestSuite(WPTableFilterTest.class);
		s.addTestSuite(WrongTagFilterTest.class);
		s.addTestSuite(WPSemanticLinkTest.class);
		s.addTestSuite(TemplateParserTest.class);
		return s;
	}

}
