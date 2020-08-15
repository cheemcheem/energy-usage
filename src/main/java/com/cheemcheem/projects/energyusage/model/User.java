package com.cheemcheem.projects.energyusage.model;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int userId;

  @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private Set<EnergyReading> energyReadings;

  User(int userId, Set<EnergyReading> energyReadings) {
    this.userId = userId;
    this.energyReadings = energyReadings;
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

