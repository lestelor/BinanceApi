package lestelabs.binanceapi.binance.api.client.domain.market;

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
@JsonPropertyOrder()
@JsonIgnoreProperties(ignoreUnknown = true)
public class Candlestick {

  private Long AopenTime;
  private String Bopen;
  private String Chigh;
  private String Dlow;
  private String Eclose;
  private String Fvolume;
  private Long GcloseTime;
  private String HquoteAssetVolume;
  private Long InumberOfTrades;
  private String JtakerBuyBaseAssetVolume;
  private String KtakerBuyQuoteAssetVolume;
  private String Lstatus;

  public Long getAOpenTime() {
    return AopenTime;
  }

  public void setAOpenTime(Long openTime) {
    this.AopenTime = openTime;
  }

  public String getBOpen() {
    return Bopen;
  }

  public void setBOpen(String open) {
    this.Bopen = open;
  }

  public String getCHigh() {
    return Chigh;
  }

  public void setCHigh(String high) {
    this.Chigh = high;
  }

  public String getDLow() {
    return Dlow;
  }

  public void setDLow(String low) {
    this.Dlow = low;
  }

  public String getEClose() {
    return Eclose;
  }

  public void setEClose(String close) {
    this.Eclose = close;
  }

  public String getFVolume() {
    return Fvolume;
  }

  public void setFVolume(String volume) {
    this.Fvolume = volume;
  }

  public Long getGCloseTime() {
    return GcloseTime;
  }

  public void setGCloseTime(Long closeTime) {
    this.GcloseTime = closeTime;
  }

  public String getHQuoteAssetVolume() {
    return HquoteAssetVolume;
  }

  public void setHQuoteAssetVolume(String quoteAssetVolume) {
    this.HquoteAssetVolume = quoteAssetVolume;
  }

  public Long getINumberOfTrades() {
    return InumberOfTrades;
  }

  public void setINumberOfTrades(Long numberOfTrades) {
    this.InumberOfTrades = numberOfTrades;
  }

  public String getJTakerBuyBaseAssetVolume() {
    return JtakerBuyBaseAssetVolume;
  }

  public void setJTakerBuyBaseAssetVolume(String takerBuyBaseAssetVolume) {
    this.JtakerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
  }

  public String getKTakerBuyQuoteAssetVolume() {
    return KtakerBuyQuoteAssetVolume;
  }

  public void setKTakerBuyQuoteAssetVolume(String takerBuyQuoteAssetVolume) {
    this.KtakerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
  }

  public String getLStatus(String status) {return Lstatus;}

  public void setLStatus(String status) {
    this.Lstatus = status;
  }

  @NonNull
  @Override
  public String toString() {
    return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)



            .append("openTime", AopenTime)
            .append("open", Bopen)
            .append("high", Chigh)
            .append("low", Dlow)
            .append("close", Eclose)
            .append("volume", Fvolume)
            .append("closeTime", GcloseTime)
            .append("quoteAssetVolume", HquoteAssetVolume)
            .append("numberOfTrades", InumberOfTrades)
            .append("takerBuyBaseAssetVolume", JtakerBuyBaseAssetVolume)
            .append("takerBuyQuoteAssetVolume", KtakerBuyQuoteAssetVolume)
            .append("status",Lstatus)
            .toString();
  }
}
