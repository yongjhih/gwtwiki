package info.bliki.wiki.dump;

import info.bliki.api.Connector;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A Wikipedia XML dump file parser
 * 
 * Original version with permission from Marco Schmidt. See: <a
 * href="http://schmidt.devlib.org/software/lucene-wikipedia.html"
 * >http://schmidt.devlib.org/software/lucene-wikipedia.html</a>
 * 
 * @author Marco Schmidt
 * 
 */
public class WikiXMLParser extends DefaultHandler {
	private static final String WIKIPEDIA_TITLE = "title";

	private static final String WIKIPEDIA_TEXT = "text";

	private static final String WIKIPEDIA_PAGE = "page";

	private static final String WIKIPEDIA_NAMESPACE = "namespace";

	private static final String WIKIPEDIA_TIMESTAMP = "timestamp";

	private static final String WIKIPEDIA_ID = "id";

	private WikiArticle fArticle;

	private StringBuilder fData;

	private XMLReader fXMLReader;

	private Reader fReader;

	private IArticleFilter fArticleFilter;

	public WikiXMLParser(String filename, IArticleFilter filter) throws UnsupportedEncodingException, IOException, SAXException,
			FileNotFoundException {
		this(getBufferedReader(filename), filter);
	}

	public WikiXMLParser(InputStream inputStream, IArticleFilter filter) throws SAXException {
		super();
		try {
			fArticleFilter = filter;
			fXMLReader = XMLReaderFactory.createXMLReader();
			fXMLReader.setContentHandler(this);
			fXMLReader.setErrorHandler(this);
			fReader = new BufferedReader(new InputStreamReader(inputStream, Connector.UTF8_CHARSET));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public WikiXMLParser(Reader reader, IArticleFilter filter) throws SAXException {
		super();
		fArticleFilter = filter;
		fXMLReader = XMLReaderFactory.createXMLReader();
		fXMLReader.setContentHandler(this);
		fXMLReader.setErrorHandler(this);
		fReader = reader;
	}

	/**
	 * 
	 * @return a BufferedReader created from wikiDumpFilename
	 * @throws UnsupportedEncodingException
	 * 
	 */
	public static BufferedReader getBufferedReader(String wikiDumpFilename) throws UnsupportedEncodingException,
			FileNotFoundException, IOException {
		BufferedReader br = null;

		if (wikiDumpFilename.endsWith(".gz")) {

			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(wikiDumpFilename)), "UTF-8"));

		} else if (wikiDumpFilename.endsWith(".bz2")) {
			FileInputStream fis = new FileInputStream(wikiDumpFilename);
			br = new BufferedReader(new InputStreamReader(new BZip2CompressorInputStream(fis), "UTF-8"));
		} else {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(wikiDumpFilename), "UTF-8"));
		}

		return br;
	}

	private String getString() {
		if (fData == null) {
			return null;
		} else {
			String s = fData.toString();
			fData = null;
			return s;
		}
	}

	@Override
	public void startDocument() {
		// System.out.println("START");
	}

	@Override
	public void endDocument() {
		// System.out.println("END");
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
		// fAttributes = atts;

		if (WIKIPEDIA_PAGE.equals(qName)) {
			fArticle = new WikiArticle();
		}
		fData = null;
	}

	@Override
	public void endElement(String uri, String name, String qName) {
		try {
			if (WIKIPEDIA_PAGE.equals(qName)) {
				if (fArticle != null) {
				}
			} else if (WIKIPEDIA_TEXT.equals(qName)) {
				fArticle.setText(getString());
				fArticleFilter.process(fArticle);
				// emit(wikiText);
			} else if (WIKIPEDIA_TITLE.equals(qName)) {
				fArticle.setTitle(getString());
			} else if (WIKIPEDIA_TIMESTAMP.equals(qName)) {
				fArticle.setTimeStamp(getString());
			} else if (WIKIPEDIA_ID.equals(qName)) {
				fArticle.setId(getString());
			}

			fData = null;
			// fAttributes = null;

		} catch (RuntimeException re) {
			re.printStackTrace();
		}
	}

	/**
	 * parse an unlimited amount of characters between 2 enclosing XML-Tags
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (fData == null) {
			fData = new StringBuilder(length);
		}
		fData.append(ch, start, length);
	}

	public void parse() throws IOException, SAXException {
		fXMLReader.parse(new InputSource(fReader));
	}

}
