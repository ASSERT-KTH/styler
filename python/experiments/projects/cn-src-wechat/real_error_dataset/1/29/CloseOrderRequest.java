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
 * 微信支付-关闭订单-请求.
 *
 * @author zhangpeng
 */
@Getter
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class CloseOrderRequest extends BasePayRequest {

    private CloseOrderRequest() {}

    @XmlElement(name = "out_trade_no")
    private String outTradeNo;

    /**
     * create CloseOrderRequest.
     *
     * @param outTradeNo 商户订单号
     *
     * @return CloseOrderRequest
     */
    public static CloseOrderRequest create(@NotNull final String outTradeNo) {
        final CloseOrderRequest request = new CloseOrderRequest();
        request.outTradeNo = outTradeNo;
        request.configureAndSign();
        return request;
    }
}
