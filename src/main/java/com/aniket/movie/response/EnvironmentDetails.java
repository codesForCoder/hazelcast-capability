package com.aniket.movie.response;

import lombok.Data;

@Data
public class EnvironmentDetails {
    private String postNumber;
    private String localHostName;
    private String localHostAddr;
    private String remoteHostName;
    private String remoteHostAddr;
}
