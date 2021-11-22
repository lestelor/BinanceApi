package lestelabs.binanceapi.binance.examples;

import lestelabs.binanceapi.binance.api.client.domain.account.Withdraw;
import lestelabs.binanceapi.binance.api.client.domain.account.WithdrawHistory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import junit.framework.Assert;

/**
 * Test deserialization of a withdraw/deposit history.
 */
public class WithdrawHistoryDeserializerTest {

  @Test
  public void testWithdrawHistoryDeserialziation() {
    String withdrawHistoryJson = "{\"withdrawList\":\n" +
        "[{\"amount\":0.1,\"address\":\"0x456\",\"successTime\":\"2017-10-13 21:20:09\",\n" +
        "\"txId\":\"0x123\",\"id\":\"1\",\"asset\":\"ETH\",\"applyTime\":\"2017-10-13 20:59:38\",\"userId\":\"1\",\"status\":6}],\n" +
        "\"success\":true}";
    ObjectMapper mapper = new ObjectMapper();
    try {
      WithdrawHistory withdrawHistory = mapper.readValue(withdrawHistoryJson, WithdrawHistory.class);
      Assert.assertTrue(withdrawHistory.isSuccess());
      List<Withdraw> withdrawList = withdrawHistory.getWithdrawList();
      Assert.assertEquals(withdrawHistory.getWithdrawList().size(), 1);
      Withdraw withdraw = withdrawList.get(0);
      Assert.assertEquals(withdraw.getAmount(), "0.1");
      Assert.assertEquals(withdraw.getAddress(), "0x456");
      Assert.assertEquals(withdraw.getAsset(), "ETH");
      Assert.assertEquals(withdraw.getApplyTime(), "2017-10-13 20:59:38");
      Assert.assertEquals(withdraw.getSuccessTime(), "2017-10-13 21:20:09");
      Assert.assertEquals(withdraw.getTxId(), "0x123");
      Assert.assertEquals(withdraw.getId(), "1");
    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
  }
}
