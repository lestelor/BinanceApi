package lestelabs.binanceapi.data.streams.datasource;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lestelabs.binanceapi.binance.api.client.constant.BinanceApiConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

//https://api.binance.com/api/v3/klines?symbol=ADAEUR&interval=15m

/**
 * Kline/Candlestick bars for a symbol. Klines are uniquely identified by their open time.
 */


@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"openTime","open","high","low","close","volume","closeTime",
        "quoteAssetVolume","numberOfTrades","takerBuyBaseAssetVolume",
        "takerBuyQuoteAssetVolume","status", "stick", "sma", "rsi", "ownQuantity", "ownValueEUR", "ownValueBTC"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Candlestick {

  private Long openTime;
  private String open;
  private String high;
  private String low;
  private String close;
  private String volume;
  private Long closeTime;
  private String quoteAssetVolume;
  private Long numberOfTrades;
  private String takerBuyBaseAssetVolume;
  private String takerBuyQuoteAssetVolume;
  private String status;
  private String stick;
  private Double sma;
  private Double rsi;
  private Double ownFree;
  private Double ownLocked;
  private Double ownValueEUR;
  private Double maxValue80;


  public Long getOpenTime() {
    return openTime;
  }

  public void setOpenTime(Long openTime) {
    this.openTime = openTime;
  }

  public String getOpen() {
    return open;
  }

  public void setOpen(String open) {
    this.open = open;
  }

  public String getHigh() {
    return high;
  }

  public void setHigh(String high) {
    this.high = high;
  }

  public String getLow() {
    return low;
  }

  public void setLow(String low) {
    this.low = low;
  }

  public String getClose() {
    return close;
  }

  public void setClose(String close) {
    this.close = close;
  }

  public String getVolume() {
    return volume;
  }

  public void setVolume(String volume) {
    this.volume = volume;
  }

  public Long getCloseTime() {
    return closeTime;
  }

  public void setCloseTime(Long closeTime) {
    this.closeTime = closeTime;
  }

  public String getQuoteAssetVolume() {
    return quoteAssetVolume;
  }

  public void setQuoteAssetVolume(String quoteAssetVolume) {
    this.quoteAssetVolume = quoteAssetVolume;
  }

  public Long getNumberOfTrades() {
    return numberOfTrades;
  }

  public void setNumberOfTrades(Long numberOfTrades) {
    this.numberOfTrades = numberOfTrades;
  }

  public String getTakerBuyBaseAssetVolume() {
    return takerBuyBaseAssetVolume;
  }

  public void setTakerBuyBaseAssetVolume(String takerBuyBaseAssetVolume) {
    this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
  }

  public String getTakerBuyQuoteAssetVolume() {
    return takerBuyQuoteAssetVolume;
  }

  public void setTakerBuyQuoteAssetVolume(String takerBuyQuoteAssetVolume) {
    this.takerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
  }

  public String getStatus() {return status;}

  public void setLStatus(String status) {
    this.status = status;
  }

  public String getStick() {return stick;}

  public void setStick(String stick) {
    this.stick = stick;
  }

  public Double getSma() {return sma;}

  public void setSma(Double sma) {
    this.sma = sma;
  }

  public Double getRsi() {return rsi;}

  public void setRsi(Double rsi) {
    this.rsi = rsi;
  }


  public Double getOwnFree() {return ownFree;}

  public void setOwnFree(Double ownFree) {
    this.ownFree = ownFree;
  }

  public Double getOwnLocked() {return ownLocked;}

  public void setOwnLocked(Double ownLocked) {
    this.ownLocked = ownLocked;
  }

  public Double getOwnValueEUR() {return ownValueEUR;}

  public void setOwnValueEUR(Double ownValueEUR) {
    this.ownValueEUR = ownValueEUR;
  }
  public Double getMaxValue80() {return maxValue80;}

  public void setMaxValue80(Double maxValue80) {
    this.maxValue80 = maxValue80;
  }

  @NonNull
  @Override
  public String toString() {
    return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
            .append("openTime", openTime)
            .append("open", open)
            .append("high", high)
            .append("low", low)
            .append("close", close)
            .append("volume", volume)
            .append("closeTime", closeTime)
            .append("quoteAssetVolume", quoteAssetVolume)
            .append("numberOfTrades", numberOfTrades)
            .append("takerBuyBaseAssetVolume", takerBuyBaseAssetVolume)
            .append("takerBuyQuoteAssetVolume", takerBuyQuoteAssetVolume)
            .append("status",status)
            .toString();
  }
}
