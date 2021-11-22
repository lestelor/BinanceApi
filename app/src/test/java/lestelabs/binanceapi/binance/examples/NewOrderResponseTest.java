package lestelabs.binanceapi.binance.examples;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import lestelabs.binanceapi.binance.api.client.domain.account.NewOrderResponse;
import lestelabs.binanceapi.binance.api.client.domain.account.Trade;

/**
 * @see NewOrderResponse
 */
public class NewOrderResponseTest {

  private NewOrderResponse newOrderResponse;
  private Trade trade;

  @Before
  public void setUp() {
    newOrderResponse = new NewOrderResponse();
    trade = new Trade();
    trade.setId(123L);
  }

  @Test
  public void shouldHandleToStringWithNullFills() {
    assertThat(newOrderResponse.toString(), containsString(",fills="));
  }

  @Test
  public void shouldHandleToStringWithNoFills() {
    newOrderResponse.setFills(Collections.emptyList());
    assertThat(newOrderResponse.toString(), containsString(",fills="));
  }

  @Test
  public void shouldHandleToStringWithFills() {
    newOrderResponse.setFills(trades(trade));
    assertThat(newOrderResponse.toString(), containsString(",fills=Trade[id=123,"));
  }

  private static List<Trade> trades(final Trade... trades) {
    return Arrays.asList(trades);
  }
} 