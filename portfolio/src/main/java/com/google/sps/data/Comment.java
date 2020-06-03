package com.google.sps.data;

/** An item containing comment text and name */
public final class Comment {

  private final String name;
  private final String commentText;

  public Comment(String name, String commentText) {
    this.name = name;
    this.commentText = commentText;
  }
}