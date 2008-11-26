package info.bliki.wiki.model;

import info.bliki.wiki.filter.Encoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Standard model implementation
 * 
 */
public class WikiModel extends AbstractWikiModel {
	/**
	 * A map for categories and their associated sort keys
	 */
	protected Map<String, String> categories = null;

	protected Set<String> links = null;

	protected Set<String> templates = null;

	protected List<SemanticRelation> semanticRelations = null;

	protected List<SemanticAttribute> semanticAttributes = null;

	protected String fExternalImageBaseURL;

	protected String fExternalWikiBaseURL;

	public WikiModel(String imageBaseURL, String linkBaseURL) {
		this(Configuration.DEFAULT_CONFIGURATION, imageBaseURL, linkBaseURL);
	}

	public WikiModel(Configuration configuration, String imageBaseURL, String linkBaseURL) {
		super(configuration);
		fExternalImageBaseURL = imageBaseURL;
		fExternalWikiBaseURL = linkBaseURL;
	}

	public WikiModel(Configuration configuration, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(configuration, locale);
		fExternalImageBaseURL = imageBaseURL;
		fExternalWikiBaseURL = linkBaseURL;
	}

	public WikiModel(Configuration configuration, ResourceBundle resourceBundle, String imageBaseURL, String linkBaseURL) {
		super(configuration, resourceBundle);
		fExternalImageBaseURL = imageBaseURL;
		fExternalWikiBaseURL = linkBaseURL;
	}

	@Override
	public void addCategory(String categoryName, String sortKey) {
		categories.put(categoryName, sortKey);
	}

	@Override
	public void addLink(String topicName) {
		links.add(topicName);
	}

	@Override
	public boolean addSemanticAttribute(String attribute, String attributeValue) {
		if (semanticAttributes == null) {
			semanticAttributes = new ArrayList<SemanticAttribute>();
		}
		semanticAttributes.add(new SemanticAttribute(attribute, attributeValue));
		return true;
	}

	@Override
	public boolean addSemanticRelation(String relation, String relationValue) {
		if (semanticRelations == null) {
			semanticRelations = new ArrayList<SemanticRelation>();
		}
		semanticRelations.add(new SemanticRelation(relation, relationValue));
		return true;
	}

	@Override
	public void addTemplate(String template) {
		templates.add(template);
	}

	@Override
	public void appendInternalLink(String topic, String hashSection, String topicDescription, String cssClass, boolean parseRecursive) {
		String hrefLink;
		if (topic.length() > 0) {
			String encodedtopic = Encoder.encodeTitleUrl(topic);
			if (replaceColon()) {
				encodedtopic = encodedtopic.replaceAll(":", "/");
			}
			hrefLink = fExternalWikiBaseURL.replace("${title}", encodedtopic);
		} else {
			if (hashSection != null) {
				hrefLink = "";
			} else {
				hrefLink = fExternalWikiBaseURL.replace("${title}", "");
			}
		}
		super.appendInternalLink(hrefLink, hashSection, topicDescription, cssClass, parseRecursive);
	}

	/**
	 * Get the set of Wikipedia category names used in this text
	 * 
	 * @return the set of category strings
	 */
	public Map<String, String> getCategories() {
		return categories;
	}

	/**
	 * Get the set of Wikipedia links used in this text
	 * 
	 * @return the set of category strings
	 */
	public Set<String> getLinks() {
		return links;
	}

	@Override
	public List<SemanticAttribute> getSemanticAttributes() {
		return semanticAttributes;
	}

	@Override
	public List<SemanticRelation> getSemanticRelations() {
		return semanticRelations;
	}

	public Set<String> getTemplates() {
		return templates;
	}

	/**
	 * Append the internal wiki image link to this model.
	 * 
	 * <br/><br/><b>Note</b>: the pipe symbol (i.e. &quot;|&quot;) splits the
	 * <code>rawImageLink</code> into different segments. The first segment is
	 * used as the <code>&lt;image-name&gt;</code> and typically ends with
	 * extensions like <code>.png</code>, <code>.gif</code>,
	 * <code>.jpg</code> or <code>.jpeg</code>.
	 * 
	 * <br/><br/><b>Note</b>: if the image link contains a "width" attribute,
	 * the filename is constructed as
	 * <code>&lt;size&gt;px-&lt;image-name&gt;</code>, otherwise it's only the
	 * <code>&lt;image-name&gt;</code>.
	 * 
	 * @param imageNamespace
	 *          the image namespace
	 * @param rawImageLink
	 *          the raw image link text without the surrounding
	 *          <code>[[...]]</code>
	 */
	public void parseInternalImageLink(String imageNamespace, String rawImageLink) {
		if (fExternalImageBaseURL != null) {
			String imageHref = fExternalWikiBaseURL;
			String imageSrc = fExternalImageBaseURL;
			ImageFormat imageFormat = ImageFormat.getImageFormat(rawImageLink, imageNamespace);

			String imageName = imageFormat.getFilename();
			String sizeStr = imageFormat.getSizeStr();
			if (sizeStr != null) {
				imageName = sizeStr + '-' + imageName;
			}
			if (imageName.endsWith(".svg")) {
				imageName += ".png";
			}
			imageName = Encoder.encodeUrl(imageName);
			if (replaceColon()) {
				imageName = imageName.replaceAll(":", "/");
			}
			if (replaceColon()) {
				imageHref = imageHref.replace("${title}", imageNamespace + '/' + imageName);
				imageSrc = imageSrc.replace("${image}", imageName);
			} else {
				imageHref = imageHref.replace("${title}", imageNamespace + ':' + imageName);
				imageSrc = imageSrc.replace("${image}", imageName);
			}
			appendInternalImageLink(imageHref, imageSrc, imageFormat);
		}
	}

	@Override
	public boolean replaceColon() {
		return false;
	}

	@Override
	public void setUp() {
		super.setUp();
		categories = new HashMap<String, String>();
		links = new HashSet<String>();
		templates = new HashSet<String>();
	}

}
