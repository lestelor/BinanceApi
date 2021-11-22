package lestelabs.binanceapi.binance.examples;

import lestelabs.binanceapi.binance.api.client.BinanceApiClientFactory;
import lestelabs.binanceapi.binance.api.client.BinanceApiMarginRestClient;
import lestelabs.binanceapi.binance.api.client.domain.TimeInForce;
import lestelabs.binanceapi.binance.api.client.domain.account.MarginNewOrderResponse;
import lestelabs.binanceapi.binance.api.client.domain.account.NewOrderResponseType;
import lestelabs.binanceapi.binance.api.client.domain.account.Order;
import lestelabs.binanceapi.binance.api.client.domain.account.request.CancelOrderRequest;
import lestelabs.binanceapi.binance.api.client.domain.account.request.CancelOrderResponse;
import lestelabs.binanceapi.binance.api.client.domain.account.request.OrderRequest;
import lestelabs.binanceapi.binance.api.client.domain.account.request.OrderStatusRequest;
import lestelabs.binanceapi.binance.api.client.exception.BinanceApiException;

import java.util.List;

import static lestelabs.binanceapi.binance.api.client.domain.account.MarginNewOrder.limitBuy;

/**
 * Examples on how to place orders, cancel them, and query account information.
 */
public class MarginOrdersExample {

    public static void main(String[] args) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("YOUR_API_KEY", "YOUR_SECRET");
        BinanceApiMarginRestClient client = factory.newMarginRestClient();

        // Getting list of open orders
        List<Order> openOrders = client.getOpenOrders(new OrderRequest("LINKETH"));
        System.out.println(openOrders);

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

        // Placing a real LIMIT order
        MarginNewOrderResponse newOrderResponse = client.newOrder(limitBuy("LINKETH", TimeInForce.GTC, "1000", "0.0001").newOrderRespType(NewOrderResponseType.FULL));
        System.out.println(newOrderResponse);
    }

}
