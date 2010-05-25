package info.bliki.wiki.dump;

/**
 * Demo application which reads a compressed or uncompressed Wikipedia XML dump
 * file (depending on the given file extension <i>.gz</i>, <i>.bz2</i> or
 * <i>.xml</i>) and prints the title and wiki text.
 * 
 */
public class DumpExample {
	static class DemoSAXHandler implements IArticleFilter {

		public boolean process(WikiArticle page) {
			System.out.println("----------------------------------------");
			System.out.println(page.getTitle());
			System.out.println("----------------------------------------");
			System.out.println(page.getText());
			return true;
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: Parser <XML-FILE>");
			System.exit(-1);
		}
		// String bz2Filename =
		// "c:\\temp\\dewikiversity-20100401-pages-articles.xml.bz2";
		String bz2Filename = args[0];
		try {
			IArticleFilter handler = new DemoSAXHandler();
			WikiXMLParser wxp = new WikiXMLParser(bz2Filename, handler);
			wxp.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
