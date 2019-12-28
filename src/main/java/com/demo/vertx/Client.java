package com.demo.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Client extends AbstractVerticle {
  private final static Logger LOGGER = Logger.getLogger(Client.class.getName());
  
  //  report module object holds each response result details
  ReportModule reportModule = new ReportModule();
  
  
  @Override
  public void start() throws InterruptedException {
    
    //  Vert.X web-client API
    WebClient client = WebClient.create(vertx);
    
    //  Three sample URL to assign requests
    WebUrl google = new WebUrl("http://www.google.com");
    WebUrl yahoo  = new WebUrl("http://www.yahoo.com");
    WebUrl msn    = new WebUrl("http://www.msn.com");
    
    //  future holds the result of calculations and returns once completed
    Future<HttpResponse<Buffer>> googleFuture = Future.future();
    Future<HttpResponse<Buffer>> yahooFuture  = Future.future();
    Future<HttpResponse<Buffer>> msnFuture    = Future.future();
    
    //  First request
    urlFetcher(client, google, googleFuture);
    
    //  Second request
    urlFetcher(client, yahoo, yahooFuture);
    
    //  Third request
    urlFetcher(client, msn, msnFuture);
    
    //  create a list of futures for composition
    List<Future> allFutures = new ArrayList<>();
    allFutures.add(googleFuture);
    allFutures.add(yahooFuture);
    allFutures.add(msnFuture);
    
    //  Here CompositeFuture is used for coordination of multiple futures
    //  The handlers set for the coordinated futures are overridden by the handler of the composite future
    //  record the start time of url fetches in milliseconds
    long startTime = System.currentTimeMillis();
    CompositeFuture.all(allFutures).setHandler(ar -> {
      if (ar.succeeded()) {
        System.out.println(
            "All URLs fetched successfully in " + (System.currentTimeMillis() - startTime) + " ms");
      } else {
        LOGGER.severe("Error: Unable to fetch one or more URLs");
        LOGGER.info(ar.cause().getMessage());
      }
    });
  }
  
  //  method responsible for sending parallel requests to each url
  private void urlFetcher(WebClient client, WebUrl url, Future<HttpResponse<Buffer>> future)
      throws InterruptedException {
    long                startTime = System.currentTimeMillis();
    client.getAbs(url.getAddress()).send(ar -> {
      if (ar.succeeded()) {
        System.out.println(
            "Fetch duration for URL: " + url.getAddress() + " = " + (System.currentTimeMillis()
                - startTime)+ reportModule.generate(url, ar.result()));
        
        future.complete();
      }
    });
  }
}
