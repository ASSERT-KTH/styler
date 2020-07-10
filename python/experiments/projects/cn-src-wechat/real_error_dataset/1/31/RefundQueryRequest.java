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

import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 微信支付-查询退款-请求.
 *
 * @author zhangpeng
 */
@Getter
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class RefundQueryRequest extends BasePayRequest {

    private RefundQueryRequest() {}

    @XmlElement(name = "transaction_id")
    private String transactionId;

    @XmlElement(name = "out_trade_no")
    private String outTradeNo;

    /**
     * 商户退款单号.
     */
    @XmlElement(name = "out_refund_no")
    private String outRefundNo;

    @XmlElement(name = "refund_id")
    private String refundId;

    @XmlElement(name = "offset")
    private Integer offset;

    /**
     * create RefundQueryRequest.
     *
     * @param outTradeNo 商户订单号
     *
     * @return RefundQueryRequest
     */
    public static RefundQueryRequest createWithOutTradeNo(@NotNull final String outTradeNo) {
        final RefundQueryRequest request = new RefundQueryRequest();
        request.outTradeNo = outTradeNo;
        request.configureAndSign();
        return request;
    }

    /**
     * create RefundQueryRequest.
     *
     * @param outTradeNo 商户订单号
     * @param offset 分页查询的偏移量,
     *     举例：当商户想查询第25笔时，可传入订单号及offset=24，微信支付平台会返回第25笔到第35笔的退款单信息.
     *
     * @return RefundQueryRequest
     */
    public static RefundQueryRequest createWithOutTradeNo(
        @NotNull final String outTradeNo, @NotNull final Integer offset) {
        final RefundQueryRequest request = new RefundQueryRequest();
        request.outTradeNo = outTradeNo;
        request.offset = offset;
        request.configureAndSign();
        return request;
    }

    /**
     * create RefundQueryRequest.
     *
     * @param outRefundNo 商户退款单号
     *
     * @return RefundQueryRequest
     */
    public static RefundQueryRequest createWithOutRefundNo(final String outRefundNo) {
        final RefundQueryRequest request = new RefundQueryRequest();
        request.outRefundNo = outRefundNo;
        request.configureAndSign();
        return request;
    }
}
