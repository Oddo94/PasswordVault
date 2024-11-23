package com.razvan.utils.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountRecord {
    private String accountName;
    private String username;
    private String password;
    private String lastChangeDate;
}
