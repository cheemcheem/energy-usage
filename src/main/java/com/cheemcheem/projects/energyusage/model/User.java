package com.cheemcheem.projects.energyusage.model;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int userId;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<EnergyReading> energyReading;

  User(int userId, Set<EnergyReading> energyReading) {
    this.userId = userId;
    this.energyReading = energyReading;
  }

  public static UserBuilder builder() {
    return new UserBuilder();
  }

  public static class UserBuilder {

    private int userId;
    private Set<EnergyReading> energyReading;

    UserBuilder() {
    }

    public UserBuilder userId(int userId) {
      this.userId = userId;
      return this;
    }

    public UserBuilder energyReading(Set<EnergyReading> energyReading) {
      this.energyReading = energyReading;
      return this;
    }

    public User build() {
      return new User(userId, energyReading);
    }

    public String toString() {
      return "User.UserBuilder(userId=" + this.userId + ", energyReading=" + this.energyReading
          + ")";
    }
  }
}

