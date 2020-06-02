package com.google.sps.data;

/** Class containing comments information. */
public final class Comment {

  private final String name;
  private final String commentText;

  public Comment(String name, String commentText) {
    this.name = name;
    this.commentText = commentText;
  }

  public String getName() {
    return name;
  }

  public String getCommentText() {
    return commentText;
  }
}