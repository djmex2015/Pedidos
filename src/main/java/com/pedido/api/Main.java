package com.pedido.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main class for the App.
 */
@EnableAsync
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Main {

  /**
   * Main method for run application.
   *
   * @param args command line arguments for the application.
   */
  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }

}
