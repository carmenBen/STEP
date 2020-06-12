package com.google.sps.data;

/** An item containing comment text and name. */
public final class Comment {
  private final long id;
  private final String username;
  private final String email;
  private final String commentText;
  private final long timestamp;

  /** Comment entity constructor.*/
  public Comment(long id, String username, String email, String commentText, long timestamp) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.commentText = commentText;
    this.timestamp = timestamp;
  }
}
