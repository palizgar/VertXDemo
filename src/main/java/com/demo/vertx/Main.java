package com.demo.vertx;

import io.vertx.core.Vertx;

/* Starter  class for deploying verticle for client code*/
public class Main {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Client());
  }
}
