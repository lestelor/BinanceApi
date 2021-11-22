package lestelabs.binanceapi.binance.examples;

import lestelabs.binanceapi.binance.api.client.BinanceApiClientFactory;
import lestelabs.binanceapi.binance.api.client.BinanceApiRestClient;
import lestelabs.binanceapi.binance.api.client.domain.TimeInForce;
import lestelabs.binanceapi.binance.api.client.domain.account.NewOrderResponse;
import lestelabs.binanceapi.binance.api.client.domain.account.NewOrderResponseType;
import lestelabs.binanceapi.binance.api.client.domain.account.Order;
import lestelabs.binanceapi.binance.api.client.domain.account.request.AllOrdersRequest;
import lestelabs.binanceapi.binance.api.client.domain.account.request.CancelOrderRequest;
import lestelabs.binanceapi.binance.api.client.domain.account.request.CancelOrderResponse;
import lestelabs.binanceapi.binance.api.client.domain.account.request.OrderRequest;
import lestelabs.binanceapi.binance.api.client.domain.account.request.OrderStatusRequest;
import lestelabs.binanceapi.binance.api.client.exception.BinanceApiException;

import java.util.List;

import static lestelabs.binanceapi.binance.api.client.domain.account.NewOrder.limitBuy;
import static lestelabs.binanceapi.binance.api.client.domain.account.NewOrder.marketBuy;

/**
 * Examples on how to place orders, cancel them, and query account information.
 */
public class OrdersExample {

  public static void main(String[] args) {
    BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("YOUR_API_KEY", "YOUR_SECRET");
    BinanceApiRestClient client = factory.newRestClient();

    // Getting list of open orders
    List<Order> openOrders = client.getOpenOrders(new OrderRequest("LINKETH"));
    System.out.println(openOrders);

    // Getting list of all orders with a limit of 10
    List<Order> allOrders = client.getAllOrders(new AllOrdersRequest("LINKETH").limit(10));
    System.out.println(allOrders);

    // Get status of a particular order
    Order order = client.getOrderStatus(new OrderStatusRequest("LINKETH", 751698L));
    System.out.println(order);

    // Canceling an order
    try {
      CancelOrderResponse cancelOrderResponse = client.cancelOrder(new CancelOrderRequest("LINKETH", 756762l));
      System.out.println(cancelOrderResponse);
    } catch (BinanceApiException e) {
      System.out.println(e.getError().getMsg());
    }

    // Placing a test LIMIT order
    client.newOrderTest(limitBuy("LINKETH", TimeInForce.GTC, "1000", "0.0001"));

    // Placing a test MARKET order
    client.newOrderTest(marketBuy("LINKETH", "1000"));

    // Placing a real LIMIT order
    NewOrderResponse newOrderResponse = client.newOrder(limitBuy("LINKETH", TimeInForce.GTC, "1000", "0.0001").newOrderRespType(NewOrderResponseType.FULL));
    System.out.println(newOrderResponse);
  }

}
