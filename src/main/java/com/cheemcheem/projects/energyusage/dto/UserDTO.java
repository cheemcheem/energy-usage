package com.cheemcheem.projects.energyusage.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDTO {

  private final String userName;
  private final String fullName;
}
