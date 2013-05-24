package org.rebioma.server.services;

import BCrypt.BCrypt;

public class ChangePassUtility {
  public static void main(String[] args) {
    System.out.println(BCrypt.hashpw("", BCrypt.gensalt()));
  }
}
