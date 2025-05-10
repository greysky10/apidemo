package demo.common.pojo.ecny;


import lombok.Data;

@Data
public class ReceiveOrderRequest {

    /**交易码*/
    private String txnCode;

    /**渠道ID*/
    private String sourceId;

    /**请求方系统代码*/
    private String reqSysId;

    /**请求方交易流水号*/
    private String reqTraceId;

    /**请求方交易时间*/
    private String reqDate;

    /**请求方自定义字段*/
    private String reqReserved;
}
