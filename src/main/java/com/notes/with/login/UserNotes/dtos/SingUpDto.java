package com.notes.with.login.UserNotes.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingUpDto {
    private String name;
    private String email;
    private String password;
}
