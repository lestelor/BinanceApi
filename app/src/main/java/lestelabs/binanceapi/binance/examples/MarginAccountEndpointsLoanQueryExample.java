package lestelabs.binanceapi.binance.examples;

import lestelabs.binanceapi.binance.api.client.BinanceApiClientFactory;
import lestelabs.binanceapi.binance.api.client.BinanceApiMarginRestClient;
import lestelabs.binanceapi.binance.api.client.domain.account.MarginTransaction;
import lestelabs.binanceapi.binance.api.client.domain.account.MaxBorrowableQueryResult;
import lestelabs.binanceapi.binance.api.client.domain.account.RepayQueryResult;

/**
 * Examples on how to get margin account information.
 */
public class MarginAccountEndpointsLoanQueryExample {

    public static void main(String[] args) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("YOUR_API_KEY", "YOUR_SECRET");
        BinanceApiMarginRestClient client = factory.newMarginRestClient();
        MaxBorrowableQueryResult usdt = client.queryMaxBorrowable("USDT");
        System.out.println(usdt.getAmount());
        MaxBorrowableQueryResult bnb = client.queryMaxBorrowable("BNB");
        System.out.println(bnb.getAmount());
        MarginTransaction borrowed = client.borrow("USDT", "310");
        System.out.println(borrowed.getTranId());
        MarginTransaction repaid = client.repay("USDT", "310");
        System.out.println(repaid);
        RepayQueryResult repayQueryResult = client.queryRepay("BNB", System.currentTimeMillis() - 1000);
        System.out.println(repayQueryResult);
    }
}
