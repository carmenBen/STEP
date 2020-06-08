package com.google.sps.data;

/** An item containing comment text and name. */
public final class Comment {
  private final long id;
  private final String name;
  private final String commentText;
  private final long timestamp;

  public Comment(long id, String name, String commentText, long timestamp) {
    this.id = id;
    this.name = name;
    this.commentText = commentText;
    this.timestamp = timestamp;
  }
}
