package com.demo.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/* Client verticle implementation*/
public class Client extends AbstractVerticle {
  ReportModule reportModule = new ReportModule();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    /*
     * Sending request and receiving response is done using
     * ReactiveX library and Observable<Object o> interface
     * The observable client is subscribed to IO scheduler
     * to provide concurrency and parallelism
     * */
    WebClient client = WebClient.create(vertx);
    Observable<WebClient> source = Observable
      .just(client)
      .subscribeOn(Schedulers.io());
    source.subscribe(
      clnt -> {
        try {
          getResponse(clnt);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    );
  }

  /* getResponse() takes a client and invoke the response to associated url
   *  For the sake of simplicity the URL is hardcoded and there is just CLI interface
   *  with no GUI and user input.
   */
  private void getResponse(WebClient client) throws InterruptedException {
    List<WebUrl> urls = new ArrayList();
    addWebURLs(urls);

    urls.forEach(
      webUrl -> client
        .get(80, webUrl.getAddress(), "/")
        .send(
          ar -> {
            if (ar.succeeded()) { // Obtain response
              HttpResponse<Buffer> response = ar.result();
              reportModule.generate(webUrl, response);
            /*
             * In case of any failure, report the cause of failure to the console
             * */
            } else System.out.println(
              "Something went wrong " + ar.cause().getMessage()
            );
          }
        )
    );
    Thread.sleep(3000);
    System.out.println(
      "Total received content: " + reportModule.getTotalSize() + " bytes"
    );
  }

  /*
   * Hardcoded URLs are added to the collection urls (I know it's awful, but let it be without
   * GUI and simple)
   */
  private void addWebURLs(List<WebUrl> urls) {
    WebUrl yahoo = new WebUrl("www.yahoo.com");
    WebUrl google = new WebUrl("www.google.com");
    WebUrl msn = new WebUrl("www.msn.com");
    WebUrl bbc = new WebUrl("www.bbc.com");
    urls.add(yahoo);
    urls.add(google);
    urls.add(msn);
    urls.add(bbc);
  }
}
