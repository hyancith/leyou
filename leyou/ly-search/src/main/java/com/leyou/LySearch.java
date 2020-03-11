package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.swing.*;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class LySearch {
    public static void main(String[] args) {
        SpringApplication.run(LySearch.class,args);
    }
}
