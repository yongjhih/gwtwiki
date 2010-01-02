package info.bliki.gae.model;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class BlikiUtil {
  private static UserService userService = UserServiceFactory.getUserService();

  /**
   * Replace all '_' (underscore) characters with a ' ' (space) character
   * 
   * @param title
   *          the title of the wiki page
   * @return
   */
  public static String decodeTitle(String title) {
    return title.replaceAll("_", " ");
  }

  /**
   * Replace all ' ' (space) characters with a '_' (underscore) character
   * 
   * @param title
   *          the title of the wiki page
   * @return
   */
  public static String encodeTitle(String title) {
    return title.replaceAll(" ", "_");
  }

  public static boolean isUserEditor() {
    return userService.isUserAdmin();
  }
  public static String securityCheck(String destinationUrl) {
    // logger.debug("destination url: " + destinationUrl);
    if (!userService.isUserLoggedIn()) {
      return "redirect:" + userService.createLoginURL("/" + destinationUrl);
    } else if (!isUserEditor()) {
      return "common/403";
    }
    return destinationUrl;
  }
}
