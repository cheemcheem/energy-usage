package com.cheemcheem.springprojects.energyusage.exception;

public class EmptyRepositoryException extends RuntimeException {

  public EmptyRepositoryException(String message) {
    super(message);
  }
}
