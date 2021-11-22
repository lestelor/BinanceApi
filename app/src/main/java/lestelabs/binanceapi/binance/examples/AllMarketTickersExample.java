package lestelabs.binanceapi.binance.examples;

import lestelabs.binanceapi.binance.api.client.BinanceApiClientFactory;
import lestelabs.binanceapi.binance.api.client.BinanceApiWebSocketClient;

/**
 * All market tickers channel examples.
 *
 * It illustrates how to create a stream to obtain all market tickers.
 */
public class AllMarketTickersExample {

  public static void main(String[] args) {
    BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
    BinanceApiWebSocketClient client = factory.newWebSocketClient();

    client.onAllMarketTickersEvent(event -> {
      System.out.println(event);
    });
  }
}
