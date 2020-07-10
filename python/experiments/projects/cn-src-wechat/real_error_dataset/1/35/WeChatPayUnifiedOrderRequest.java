/*
 * Copyright (c) 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.javaer.wechat.sdk.pay.model;

import cn.javaer.wechat.sdk.pay.WeChatPayConfigurator;
import cn.javaer.wechat.sdk.util.WeChatUtils;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 微信支付-统一下单-请求.
 *
 * @author zhangpeng
 */
@Getter
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WeChatPayUnifiedOrderRequest extends WeChatPayRequest {

    public static final String TRADE_TYPE_JSAPI = "JSAPI";
    public static final String TRADE_TYPE_NATIVE = "NATIVE";
    public static final String TRADE_TYPE_APP = "APP";

    private WeChatPayUnifiedOrderRequest() {}

    /**
     * 设备号.
     */
    @XmlElement(name = "device_info")
    private String deviceInfo;

    /**
     * 商品描述.
     */
    @XmlElement(name = "body")
    private String body;

    /**
     * 商品详情.
     */
    @XmlElement(name = "detail")
    private String detail;

    /**
     * 附加数据.
     */
    @XmlElement(name = "attach")
    private String attach;

    /**
     * 商户订单号.
     */
    @XmlElement(name = "out_trade_no")
    private String outTradeNo;

    /**
     * 货币类型.
     */
    @XmlElement(name = "fee_type")
    private String feeType;

    /**
     * 总金额.
     */
    @XmlElement(name = "total_fee")
    private Integer totalFee;

    /**
     * 终端IP.
     */
    @XmlElement(name = "spbill_create_ip")
    private String spbillCreateIp;

    /**
     * 交易起始时间.
     */
    @XmlElement(name = "time_start")
    private String timeStart;

    /**
     * 交易结束时间.
     */
    @XmlElement(name = "time_expire")
    private String timeExpire;

    /**
     * 订单优惠标记.
     */
    @XmlElement(name = "goods_tag")
    private String goodsTag;

    /**
     * 通知地址.
     */
    @XmlElement(name = "notify_url")
    private String notifyUrl;

    /**
     * 交易类型.
     */
    @XmlElement(name = "trade_type")
    private String tradeType;

    /**
     * 商品ID.
     */
    @XmlElement(name = "product_id")
    private String productId;

    /**
     * 指定支付方式.
     */
    @XmlElement(name = "limit_pay")
    private String limitPay;

    /**
     * 用户标识.
     */
    @XmlElement(name = "openid")
    private String openid;

    /**
     * 场景信息.
     */
    @XmlElement(name = "scene_info")
    private String sceneInfo;

    /**
     * 微信支付-统一下单-NATIVE 类型.
     *
     * @param body 商品简述
     * @param outTradeNo 商户订单号
     * @param totalFee 待支付的金额
     *
     * @return WeChatPayUnifiedOrderRequest
     */
    public static WeChatPayUnifiedOrderRequest createWithNative(
        @NotNull final String body,
        @NotNull final String outTradeNo,
        final int totalFee) {
        Validate.inclusiveBetween(1, 10_0000_00, totalFee);

        final WeChatPayConfigurator configurator = WeChatPayConfigurator.INSTANCE;
        final WeChatPayUnifiedOrderRequest request = new WeChatPayUnifiedOrderRequest();

        request.productId = WeChatUtils.uuid32();
        request.tradeType = TRADE_TYPE_NATIVE;

        request.notifyUrl = configurator.getNotifyUrl();
        request.spbillCreateIp = configurator.getSpbillCreateIp();

        request.body = body;
        request.outTradeNo = outTradeNo;
        request.totalFee = totalFee;

        request.configureAndSign();
        return request;
    }

    /**
     * 微信支付-统一下单-JSAPI 类型.
     *
     * @param openid openid
     * @param body 商品简述
     * @param outTradeNo 商户订单号
     * @param totalFee 待支付的金额
     *
     * @return WeChatPayUnifiedOrderRequest
     */
    public static WeChatPayUnifiedOrderRequest createWithJsApi(
        @NotNull final String openid,
        @NotNull final String body,
        @NotNull final String outTradeNo,
        final int totalFee) {
        Validate.inclusiveBetween(1, 10_0000_00, totalFee);

        final WeChatPayConfigurator configurator = WeChatPayConfigurator.INSTANCE;
        final WeChatPayUnifiedOrderRequest request = new WeChatPayUnifiedOrderRequest();

        request.tradeType = TRADE_TYPE_JSAPI;

        request.notifyUrl = configurator.getNotifyUrl();
        request.spbillCreateIp = configurator.getSpbillCreateIp();

        request.openid = openid;
        request.body = body;
        request.outTradeNo = outTradeNo;
        request.totalFee = totalFee;

        request.configureAndSign();
        return request;
    }
}
