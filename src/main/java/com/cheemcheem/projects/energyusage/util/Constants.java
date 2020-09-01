package com.cheemcheem.projects.energyusage.util;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Constants {

  public static final String USER_ID_ATTRIBUTE_KEY = "USER_ID_ATTRIBUTE_KEY";

  /**
   * A list of endpoints that are required to be attached to request attributes.
   */
  public static final Predicate<String> DO_NOT_INTERCEPT = Pattern.compile(
      "(/api/spending(/.*)*)"
          + "|(/api/average(/.*)*)"
          + "|(/api/reading(/.*)*)"
  ).asMatchPredicate().negate();
}
