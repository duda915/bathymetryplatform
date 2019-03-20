package com.mdud.bathymetryplatform.user.registration;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@Service
public class IPService {

    public String getExternalIp() {
        String ip;
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));

            ip = in.readLine();
        } catch (Exception e) {
            throw new RuntimeException("failed to fetch ip address");
        }

        return ip;
    }
}
