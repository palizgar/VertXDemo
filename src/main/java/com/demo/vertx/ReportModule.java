package com.demo.vertx;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;

public class ReportModule {
  private int totalSize = 0;

  public String generate(WebUrl url, HttpResponse<Buffer> response) {
    /*
     * Generates the response details
     */
    return
        " /status code: "
            + response.statusCode()
            + " /body Size: "
            + getResponseSize(response, url);
  }

  private int getResponseSize(HttpResponse<Buffer> response, WebUrl url) {
    url.setSize(response.bodyAsString().getBytes().length);
    return url.getSize();
  }

  public int getTotalSize() {
    return totalSize;
  }
}
