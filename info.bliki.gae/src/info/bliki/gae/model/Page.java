package info.bliki.gae.model;

import info.bliki.wiki.model.IWikiModel;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;
 
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Page implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 5518810488698138847L;

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long key;

  @Persistent
  private User author;

  @Persistent
  private String title;

  @Persistent(defaultFetchGroup = "true")
  private Text content = null;

  @Persistent
  private Date date;

  @NotPersistent
  private String htmlContent;

  public Page() {
  }
  

  public Page(String title, String content) {
    this.title = title;
    renderHtml(content);
    if (content == null) {
      this.content = new Text("");
    } else {
      this.content = new Text(content);
    }
    this.date = new Date();
  }

  public User getAuthor() {
    return author;
  }

  public String getContent() {
    return content.getValue();
  }

  public Date getDate() {
    return date;
  }

  /**
   * @return the htmlContent
   */
  public String getHtmlContent() {
    if (htmlContent == null) {
      renderHtml(content.getValue());
    }
    return htmlContent;
  }

  public Long getKey() {
    return key;
  }

  public String getTitle() {
    return title;
  }

  private void renderHtml(String content) {
    IWikiModel model = BlikiModel.get();
    this.htmlContent = model.render(content);
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public void setContent(String content) {
    renderHtml(content);
    this.content = new Text(content);
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setKey(Long key) {
    this.key = key;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return this.key.toString() + this.title;
  }
}
