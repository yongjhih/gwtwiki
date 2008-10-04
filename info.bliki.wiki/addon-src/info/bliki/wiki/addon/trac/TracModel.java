package info.bliki.wiki.addon.trac;

import info.bliki.wiki.filter.Encoder;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.ImageFormat;
import info.bliki.wiki.model.SemanticAttribute;
import info.bliki.wiki.model.SemanticRelation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Standard model implementation
 * 
 */
public class TracModel extends AbstractTracModel {
	protected Set<String> categories = null;

	protected Set<String> links = null;

	protected Set<String> templates = null;

	protected List<SemanticRelation> semanticRelations = null;

	protected List<SemanticAttribute> semanticAttributes = null;

	protected String fExternalImageBaseURL;

	protected String fExternalWikiBaseURL;

	public TracModel(String imageBaseURL, String linkBaseURL) {
		this(Configuration.DEFAULT_CONFIGURATION, imageBaseURL, linkBaseURL);
	}

	public TracModel(Configuration configuration, String imageBaseURL, String linkBaseURL) {
		super(configuration);
		fExternalImageBaseURL = imageBaseURL;
		fExternalWikiBaseURL = linkBaseURL;
	}

	public TracModel(Configuration configuration, Locale locale, String imageBaseURL, String linkBaseURL) {
		super(configuration, locale);
		fExternalImageBaseURL = imageBaseURL;
		fExternalWikiBaseURL = linkBaseURL;
	}

	public TracModel(Configuration configuration, ResourceBundle resourceBundle, String imageBaseURL, String linkBaseURL) {
		super(configuration, resourceBundle);
		fExternalImageBaseURL = imageBaseURL;
		fExternalWikiBaseURL = linkBaseURL;
	}

	@Override
	public void addCategory(String categoryName, String sortKey) {
		categories.add(categoryName);
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
	public void appendInternalLink(String topic, String hashSection, String topicDescription, String cssClass) {
		String encodedtopic = Encoder.encodeTitleUrl(topic);
		if (replaceColon()) {
			encodedtopic = encodedtopic.replaceAll(":", "/");
		}
		String hrefLink = fExternalWikiBaseURL.replace("${title}", encodedtopic);
		super.appendInternalLink(hrefLink, hashSection, topicDescription, cssClass);
	}

	/**
	 * Get the set of Wikipedia category names used in this text
	 * 
	 * @return the set of category strings
	 */
	public Set<String> getCategories() {
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

	public void parseInternalImageLink(String imageNamespace, String name) {
		if (fExternalImageBaseURL != null) {
			String imageHref = fExternalWikiBaseURL;
			String imageSrc = fExternalImageBaseURL;
			ImageFormat imageFormat = ImageFormat.getImageFormat(name, imageNamespace);

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
		categories = new HashSet<String>();
		links = new HashSet<String>();
		templates = new HashSet<String>();
	}

}
