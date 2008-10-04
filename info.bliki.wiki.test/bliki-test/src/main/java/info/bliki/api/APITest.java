package info.bliki.api;

import info.bliki.wiki.model.WikiModel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class APITest {
	public APITest() {
		super();
	}

	public static void testQueryContent001() {
		String[] listOfTitleStrings = { "Main Page", "API" };
		User user = new User("", "", "http://meta.wikimedia.org/w/api.php");
		user.login();
		List<Page> listOfPages = user.queryContent(listOfTitleStrings);
		for (Page page : listOfPages) {
			// print page information
			System.out.println(page.getCurrentContent());
		}
	}

	public static void testQueryContent002() {
		WikiModel wikiModel = new WikiModel("http://en.wikipedia.org/wiki/${image}", "http://en.wikipedia.org/wiki/${title}");
		wikiModel.setUp();
		String[] listOfTitleStrings = { "Tom Hanks" };
		User user = new User("", "", "http://en.wikipedia.org/w/api.php");
		user.login();
		List<Page> listOfPages = user.queryContent(listOfTitleStrings);
		for (Page page : listOfPages) {
			String renderedHTML = wikiModel.render(page.getCurrentContent());
			System.out.println(renderedHTML);
			break;
		}
	}

	public static void testQueryLinks001() {
		String[] listOfTitleStrings = { "Main Page", "API" };
		User user = new User("", "", "http://meta.wikimedia.org/w/api.php");
		user.login();
		List<Page> listOfPages = user.queryLinks(listOfTitleStrings);
		for (Page page : listOfPages) {
			// print page information
			System.out.println(page.toString());
			for (int j = 0; j < page.sizeOfLinksList(); j++) {
				Link link = page.getLink(j);
				// print every link in this page
				System.out.println(link.toString());
			}
		}
	}

	public static void testQueryCategories001() {
		String[] listOfTitleStrings = { "Main Page", "API" };
		User user = new User("", "", "http://meta.wikimedia.org/w/api.php");
		user.login();
		List<Page> listOfPages = user.queryCategories(listOfTitleStrings);
		for (Page page : listOfPages) {
			// print page information
			System.out.println(page.toString());
			for (int j = 0; j < page.sizeOfCategoryList(); j++) {
				Category cat = page.getCategory(j);
				// print every category in this page
				System.out.println(cat.toString());
			}
		}
	}

	public static void testQueryImageinfo001() {
		String[] listOfImageStrings = { "Image:Question book-new.svg", "Image:Test.jpg", "Image:Brandenburger Tor Blaue Stunde.jpg" };
		User user = new User("", "", "http://en.wikipedia.org/w/api.php");
		user.login();
		List<Page> listOfPages = user.queryImageinfo(listOfImageStrings);
		for (Page page : listOfPages) {
			// print page information
			System.out.println(page.toString());
			// download the image to c:\temp directory
			FileOutputStream os = null;
			try {
				os = new FileOutputStream("c:\\temp\\" + page.getTitle().substring(6).replaceAll(" ", "_"));
				page.downloadImageUrl(os);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			for (int j = 0; j < page.sizeOfLinksList(); j++) {
				Link link = page.getLink(j);
				// print every link in this page
				System.out.println(link.toString());
			}
		}
	}

	public static void main(String[] args) {
		 testQueryContent002();
		 testQueryContent001();
		 testQueryLinks001();
		 testQueryCategories001();
		testQueryImageinfo001();
	}
}
