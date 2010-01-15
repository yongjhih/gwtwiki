package org.jamwiki.model;

import com.googlecode.objectify.ObjectifyService;

public class OS extends ObjectifyService {
  public static void initialize() {
    factory.register(Category.class);
    factory.register(Topic.class);
    factory.register(WikiUser.class);

    factory.setDatastoreTimeoutRetryCount(2);
  }
}