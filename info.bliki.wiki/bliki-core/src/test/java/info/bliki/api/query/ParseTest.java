package info.bliki.api.query;

import junit.framework.TestCase;
import info.bliki.api.User;
import info.bliki.api.Connector;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Tests Parse query.
 */
public class ParseTest extends TestCase {

    public void testParseQuery() {

        User user = new User("", "", "http://meta.wikimedia.org/w/api.php");       
        Connector connector = new Connector();
        user = connector.login(user);
        System.out.println(user.getToken());
        info.bliki.api.Parse parse = connector.parse(user, "Main Page");
        assertNotNull(parse);
        assertNotNull(parse.getText());
        String html = StringEscapeUtils.unescapeHtml(parse.getText());
        System.out.println("Retrieved html text:\n" + html);
    }
}
