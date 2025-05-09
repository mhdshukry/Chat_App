package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String username;
    private String password;
    private String nickname;
    private String profilePicture;
    private String role;

    // ✅ Correct Constructor Matching ChatServiceImpl Usage
    public User(String email, String username, String password, String nickname, String profilePicture, String role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.profilePicture = profilePicture;
        this.role = role;
    }

    // ✅ Default Constructor (Required for Hibernate)
    public User() {}
}
