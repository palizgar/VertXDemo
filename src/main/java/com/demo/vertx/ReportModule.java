package com.demo.vertx;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;

public class ReportModule {
  private int totalSize = 0;

  public void generate(WebUrl url, HttpResponse<Buffer> response) {
    /*
     * Writing the response details to the console
     */
    System.out.println(
        "Received response from url: "
            + url.getAddress()
            + " /status code: "
            + response.statusCode()
            + " /body Size: "
            + getResponseSize(response, url)
            + " /"
            + Thread.currentThread().getName().toUpperCase());
    totalSize += url.getSize();
  }

  private int getResponseSize(HttpResponse<Buffer> response, WebUrl url) {
    url.setSize(response.bodyAsString().getBytes().length);
    return url.getSize();
  }

  public int getTotalSize() {
    return totalSize;
  }
}
