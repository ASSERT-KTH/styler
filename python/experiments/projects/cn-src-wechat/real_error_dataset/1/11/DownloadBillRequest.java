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
import lombok.NonNull;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 微信支付-下载对账单-请求.
 *
 * @author zhangpeng
 */
@Getter
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class DownloadBillRequest extends WeChatPayRequest {

    private DownloadBillRequest() {}

    /**
     * 对账单日期.
     *
     * <p>下载对账单的日期，格式：20140603</p>
     */
    @XmlElement(name = "bill_date")
    private String billDate;

    /**
     * 账单类型.
     */
    @NonNull
    @XmlElement(name = "bill_type")
    private String billType;

    /**
     * 压缩账单.
     *
     * <p>非必传参数，固定值：GZIP，返回格式为.gzip的压缩包账单。不传则默认为数据流形式。</p>
     */
    @XmlElement(name = "tar_type")
    private String tarType;


    /**
     * Create new DownloadBillRequest.
     *
     * @param queryDate the query date
     * @param billType the bill type
     *
     * @return the DownloadBillRequest
     */
    public static DownloadBillRequest create(final LocalDate queryDate, final BillType billType) {
        final DownloadBillRequest request = new DownloadBillRequest();
        request.billDate = queryDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        request.billType = billType.toString();
        request.configureAndSign();
        return request;
    }

    /**
     * Create new DownloadBillRequest.
     *
     * @param queryDate the query date
     * @param billType the bill type
     *
     * @return the DownloadBillRequest
     */
    public static DownloadBillRequest createWithGzip(final LocalDate queryDate, final BillType billType) {
        final DownloadBillRequest request = new DownloadBillRequest();
        request.billDate = queryDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        request.billType = billType.toString();
        request.tarType = "GZIP";
        request.configureAndSign();
        return request;
    }

    /**
     * 账单类型.
     */
    public enum BillType {

        /**
         * 返回当日所有订单信息，默认值.
         */
        ALL,

        /**
         * 返回当日成功支付的订单.
         */
        SUCCESS,

        /**
         * 返回当日退款订单.
         */
        REFUND,

        /**
         * 返回当日充值退款订单（相比其他对账单多一栏“返还手续费”）.
         */
        RECHARGE_REFUND
    }
}
