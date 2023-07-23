package com.ianwilliams.restfulapistesting.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Token {
    String bearerToken;
}