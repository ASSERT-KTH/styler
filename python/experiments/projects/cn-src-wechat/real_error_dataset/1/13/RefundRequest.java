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
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 微信支付-申请退款-请求.
 *
 * @author zhangpeng
 */
@Getter
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class RefundRequest extends WeChatPayRequest {

    private RefundRequest() {}

    @XmlElement(name = "transaction_id")
    private String transactionId;

    @XmlElement(name = "out_trade_no")
    private String outTradeNo;

    /**
     * 商户退款单号.
     */
    @XmlElement(name = "out_refund_no")
    private String outRefundNo;

    /**
     * 订单金额.
     */
    @XmlElement(name = "total_fee")
    private int totalFee;

    /**
     * 退款金额.
     */
    @XmlElement(name = "refund_fee")
    private int refundFee;

    @XmlElement(name = "refund_fee_type")
    private String refundFeeType;

    /**
     * 退款原因.
     */
    @XmlElement(name = "refund_desc")
    private String refundDesc;

    /**
     * 退款资金来源.
     */
    @XmlElement(name = "refund_account")
    private String refundAccount;

    /**
     * create RefundRequest.
     *
     * @param outTradeNo 商户订单号
     * @param outRefundNo 商户退款单号, 同一退款单号多次请求只退一笔
     * @param totalFee 订单总金额
     * @param refundFee 退款金额
     *
     * @return RefundRequest
     */
    public static RefundRequest create(
        @NotNull final String outTradeNo,
        @NotNull final String outRefundNo,
        final int totalFee,
        final int refundFee) {
        Validate.inclusiveBetween(1, 10_0000_00, totalFee);
        Validate.inclusiveBetween(1, totalFee, refundFee);

        final RefundRequest request = new RefundRequest();

        request.outTradeNo = outTradeNo;
        request.outRefundNo = outRefundNo;
        request.totalFee = totalFee;
        request.refundFee = refundFee;

        request.configureAndSign();
        return request;
    }

    /**
     * create RefundRequest.
     *
     * @param outTradeNo 商户订单号
     * @param outRefundNo 商户退款单号, 同一退款单号多次请求只退一笔
     * @param totalFee 订单总金额
     * @param refundFee 退款金额
     * @param refundDesc 退款原因, 发给用户的退款消息中体现退款原因
     *
     * @return RefundRequest
     */
    public static RefundRequest create(
        @NotNull final String outTradeNo,
        @NotNull final String outRefundNo,
        final int totalFee,
        final int refundFee,
        @NotNull final String refundDesc) {

        Validate.inclusiveBetween(1, 100000, totalFee);
        Validate.inclusiveBetween(1, totalFee, refundFee);

        final RefundRequest request = new RefundRequest();

        request.outTradeNo = outTradeNo;
        request.outRefundNo = outRefundNo;
        request.totalFee = totalFee;
        request.refundFee = refundFee;
        request.refundDesc = refundDesc;

        request.configureAndSign();
        return request;
    }
}
