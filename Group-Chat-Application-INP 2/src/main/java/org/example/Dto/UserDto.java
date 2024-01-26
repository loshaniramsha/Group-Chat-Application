package org.example.Dto;

import lombok.*;

import java.io.InputStream;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto {
private String username;
private String password;
private InputStream profilePic;
}
