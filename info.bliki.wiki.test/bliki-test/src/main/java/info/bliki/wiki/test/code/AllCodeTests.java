package info.bliki.wiki.test.code;

import info.bliki.wiki.tags.code.ABAPTest;
import info.bliki.wiki.tags.code.CSharpTest;
import info.bliki.wiki.tags.code.GroovyTest;
import info.bliki.wiki.tags.code.JavaScriptTest;
import info.bliki.wiki.tags.code.JavaTest;
import info.bliki.wiki.tags.code.PHPTest;
import info.bliki.wiki.tags.code.XMLTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllCodeTests extends TestCase {
  public AllCodeTests(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite s = new TestSuite();
    
    s.addTestSuite(ABAPTest.class);
    s.addTestSuite(CSharpTest.class);
    s.addTestSuite(JavaTest.class);
    s.addTestSuite(JavaScriptTest.class);
    s.addTestSuite(PHPTest.class);
    s.addTestSuite(XMLTest.class);
    
    s.addTestSuite(GroovyTest.class);
    return s;
  }

}
