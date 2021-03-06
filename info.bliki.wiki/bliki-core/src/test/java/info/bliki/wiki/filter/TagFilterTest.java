package info.bliki.wiki.filter;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TagFilterTest extends FilterTestSupport {
  public TagFilterTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(TagFilterTest.class);
  }
  
  public void testWrongTag1() {
    assertEquals("\n" + 
    		"<pre>madfkfj </pre>hg", wikiModel.render("<pre>madfkfj </pre>hg", false));
  }
  
  public void testWrongTag2() {
    assertEquals("\n" + 
    		"<pre>madfkfj </pre>hg", wikiModel.render("<pre>madfkfj </pRE>hg", false));
  }
  
  public void testWrongTag3() {
    assertEquals("\n" + 
    		"<pre>madfk&lt;/prefj </pre>hg", wikiModel.render("<pre>madfk</prefj </pRE>hg", false));
  }
}