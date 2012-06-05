package info.bliki.api.creator;

import info.bliki.api.User;
import info.bliki.wiki.filter.Encoder;
import info.bliki.wiki.filter.HTMLConverter;
import info.bliki.wiki.impl.APIWikiModel;

import java.io.IOException;
import java.util.Locale;

/**
 * Test to load a page, images and templates from en.wikipedia.org and render it
 * into an HTML file. The CSS is always included in the generated HTML text
 * which blows up the size of the HTML file.
 */
public class HTMLCreatorExample {
	public HTMLCreatorExample() {
		super();
	}

	public static String testWikipediaENAPI(String title) {
		return testWikipediaENAPI(title, "http://en.wikipedia.org/w/api.php", Locale.ENGLISH);
	}

	public static void testWikipediaText(String rawWikiText, String title, Locale locale) {
		String[] listOfTitleStrings = { title };
		String titleURL = Encoder.encodeTitleLocalUrl(title);
		User user = new User("", "", null);
		String mainDirectory = "c:/temp/";
		// the following subdirectory should not exist if you would like to create a
		// new database
		String databaseSubdirectory = "WikiDB";
		// the following directory must exist for image downloads
		String imageDirectory = "c:/temp/WikiImages";
		// the generated HTML will be stored in this file name:
		String generatedHTMLFilename = mainDirectory + titleURL + ".html";

		WikiDB db = null;

		try {
			db = new WikiDB(mainDirectory, databaseSubdirectory);
			APIWikiModel wikiModel = new APIWikiModel(user, db, locale, "${image}", "${title}", imageDirectory);
			DocumentCreator creator = new DocumentCreator(wikiModel, user, listOfTitleStrings);
			// create header and CSS information
			creator.setHeader(HTMLConstants.HTML_HEADER1 + HTMLConstants.CSS_MAIN_STYLE + HTMLConstants.CSS_SCREEN_STYLE
					+ HTMLConstants.HTML_HEADER2);
			creator.setFooter(HTMLConstants.HTML_FOOTER);
			wikiModel.setUp();
			creator.renderToFile(rawWikiText, title, new HTMLConverter(), generatedHTMLFilename);
			System.out.println("Created file: " + generatedHTMLFilename);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (db != null) {
				try {
					db.tearDown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Get the wiki text throuh the Wikipedia API (i.e. <a
	 * href="http://en.wikipedia.org/w/api.php"
	 * >http://en.wikipedia.org/w/api.php</a> for the english Wikipedia) and write
	 * the generated HTML file to the<code>c:/temp/</code> Windows directory.
	 * 
	 * @param title
	 *          the wiki article's title
	 * @param apiLink
	 *          the link to the Wikipedia API
	 * @param locale
	 *          the locale (i.e. for english use
	 *          <code>java.util.Locale.ENGLISH</code>)
	 * @return the redirected link title if a <code>#REDIRECT [[...]]</code> link
	 *         is set in the wiki text; <code>null</code> otherwise
	 */
	public static String testWikipediaENAPI(String title, String apiLink, Locale locale) {
		String[] listOfTitleStrings = { title };
		String titleURL = Encoder.encodeTitleLocalUrl(title);
		User user = new User("", "", apiLink);
		user.login();
		String mainDirectory = "c:/temp/";
		// the following subdirectory should not exist if you would like to create a
		// new database
		String databaseSubdirectory = "WikiDB";
		// the following directory must exist for image downloads
		String imageDirectory = "c:/temp/WikiImages";
		// the generated HTML will be stored in this file name:
		String generatedHTMLFilename = mainDirectory + titleURL + ".html";

		WikiDB db = null;

		try {
			db = new WikiDB(mainDirectory, databaseSubdirectory);
			APIWikiModel wikiModel = new APIWikiModel(user, db, locale, "${image}", "${title}", imageDirectory);
			DocumentCreator creator = new DocumentCreator(wikiModel, user, listOfTitleStrings);
			// create header and CSS information
			creator.setHeader(HTMLConstants.HTML_HEADER1 + HTMLConstants.CSS_MAIN_STYLE + HTMLConstants.CSS_SCREEN_STYLE
					+ HTMLConstants.HTML_HEADER2);
			creator.setFooter(HTMLConstants.HTML_FOOTER);
			wikiModel.setUp();
			creator.renderToFile(generatedHTMLFilename);
			System.out.println("Created file: " + generatedHTMLFilename);
			return wikiModel.getRedirectLink();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (db != null) {
				try {
					db.tearDown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void testCreator001() {
		testWikipediaENAPI("Tom Hanks");
	}

	public static void testCreator002() {
		testWikipediaENAPI("Political party strength in California");
	}

	public static void testCreator003() {
		testWikipediaENAPI("Chris Capuano");
	}

	public static void testCreator004() {
		testWikipediaENAPI("Protein");
	}

	public static void testCreator005() {
		testWikipediaENAPI("Depeche Mode");
	}

	public static void testCreator006() {
		testWikipediaENAPI("Anarchism");
	}

	public static void testCreator007() {
		testWikipediaENAPI("JavaScript", "http://de.wikipedia.org/w/api.php", Locale.GERMAN);
	}

	public static void testCreator008() {
		testWikipediaENAPI("libero", "http://en.wiktionary.org/w/api.php", Locale.ENGLISH);
	}

	public static void testCreator009() {
		testWikipediaENAPI("Metallica");
	}

	public static void testCreator010() {
		testWikipediaENAPI("HTTP-Statuscode", "http://de.wikipedia.org/w/api.php", Locale.GERMAN);
	}

	public static void testCreator011() {
		testWikipediaENAPI("Main Page", "http://simple.wikipedia.org/w/api.php", Locale.ENGLISH);
	}

	public static void testCreator012() {
		testWikipediaENAPI("Grafenwöhr", "http://bar.wikipedia.org/w/api.php", Locale.GERMAN);
	}

	public static void testCreator013() {
		testWikipediaENAPI("Wikipedia:Hauptseite/Artikel_des_Tages/Montag", "http://de.wikipedia.org/w/api.php", Locale.GERMAN);
	}

	public static void testCreateText014() {
		testWikipediaText("This is a '''hello world''' example.", "Hello World", Locale.ENGLISH);
	}

	public static void testCreateText015() {
		String redirectedLink = testWikipediaENAPI("Manchester United Football Club");
		if (redirectedLink != null) {
			// see http://code.google.com/p/gwtwiki/issues/detail?id=38
			testWikipediaENAPI(redirectedLink);
		}
	}

	public static void main(String[] args) {
		testCreateText015();
	}
}
