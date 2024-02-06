package org.example.Dto;

import lombok.*;

import java.io.InputStream;
import java.util.InputMismatchException;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class ChatDto {
    private String userName;
    private String id;
    private String massege;
    private InputStream image;
    private String time;
}
