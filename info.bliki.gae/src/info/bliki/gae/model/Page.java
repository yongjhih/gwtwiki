package info.bliki.gae.model;

import info.bliki.wiki.model.IWikiModel;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;

@Entity
public class Page implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 5518810488698138847L;

  @Id
  private String title;

  private User author;

  private Text content = null;

  private Date date;

  @Transient
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

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return this.title;
  }
}
