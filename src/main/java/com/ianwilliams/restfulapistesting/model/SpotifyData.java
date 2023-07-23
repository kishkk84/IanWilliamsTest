package com.ianwilliams.restfulapistesting.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpotifyData {
    private Token token;
    private String searchQuery;
    private String type;
    private String market;
    private int limit;
}