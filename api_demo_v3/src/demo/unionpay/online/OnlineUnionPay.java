package demo.unionpay.online;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pfpj.sm.SM2Utils;
import com.pfpj.sm.SM4Utils;
import com.pfpj.sm.Signature;
import demo.common.exceptions.ServerException;
import demo.common.pojo.common.OpenApiMessage;
import demo.common.pojo.common.OpenApiMessageHead;
import demo.common.pojo.common.OpenApiRequest;
import demo.common.pojo.common.OpenApiResponse;
import demo.common.pojo.ecny.BusiData;
import demo.common.pojo.ecny.CheckoutPayRequest;
import demo.common.pojo.ecny.OrderDetail;
import demo.common.util.HttpClientUtils;
import demo.common.util.SMUtil;
import demo.common.util.SerialNoUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;

import static demo.unionpay.online.QuickUnionPay.convertMsgHead;
import static demo.unionpay.online.QuickUnionPay.sendMsg;

public class OnlineUnionPay {

    private static final Logger logger = LoggerFactory.getLogger(OnlineUnionPay.class);

    public static final String merchantId = "tradeGroupPre001";
    public static final String appID = "1095757516090363904001";
    public static final String privateKey = "009378BDB7262E282910AAE680E0A83EE30EA2AB8D01E41FE880583D1DA512C51E";
    public static final String publicKey = "04F0F770FDD6E188E31A27A84AC9D6D820D33CF6088A78B305C948A6D98479AC3E71D0AF0356D3C93229C27C1B345B4110DEFBF86885876977573468063EFD8F4F";
    public static final String sopPublicKey = "040485CEFE14C7AF854C66D5279239E88F2E8B881C3EB1B393003D2B9F09E7064447C1A3615875B05A9164F7F637151F115B89E70DFCCD0C25CF83268E21576921";
    public static final String url = "http://220.248.252.123:8443/sop-h5/biz_pre/unionpay/${merchantId}.htm?partnerTxSriNo=${partnerTxSriNo}";

    public static void main(String[] args) throws IOException {
        System.setProperty("java.specification.version", "1.8");
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/unionpay/pay", new UnionPayPayHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("✅ Server started at http://localhost:8080");
    }

    public static void onlinePay(String orderId, String openId) {
        try {
            OpenApiMessageHead msgHead = convertMsgHead("onlinepay.orderPay");
            logger.info("原始请求报文头: {}", JSON.toJSONString(msgHead));

            CheckoutPayRequest request = new CheckoutPayRequest();
            request.setBusiMainId(msgHead.getPartnerTxSriNo());
            request.setReqTransTime(SerialNoUtil.getDateTime());

            BusiData busiData = new BusiData();
            busiData.setTxnCode("6007");
            busiData.setSourceId("69");
            busiData.setReqTraceId(msgHead.getPartnerTxSriNo());
            busiData.setReqDate(SerialNoUtil.getDateTime());
            busiData.setReqReserved("");
            busiData.setMercDtTm(SerialNoUtil.getDateTime());
            busiData.setVendorNo(msgHead.getPartnerTxSriNo());
            busiData.setMercCode("100510100077920");
            busiData.setTransAmt("1");
            busiData.setMercUrl("https://gateway.postonline.gx.cn/newspaper/bankCallback");
            busiData.setReSuccUrl("");
            busiData.setOrderUrl("/order/list");
            busiData.setRemark1("");
            busiData.setRemark2("");
            busiData.setValidTime("600");
            busiData.setMercName("gxpost_newspaper");
            busiData.setBizTp("100001");
            busiData.setOrderTitle("订单号: " + orderId);
            busiData.setOrderCount("1");
            busiData.setTrxDevcInf("||||||");
            busiData.setReserveParam("外网");

            List<OrderDetail> detailList = new ArrayList<>();
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSubMercCode("100510100077920");
            orderDetail.setSubMercName("南宁报刊收款");
            orderDetail.setTotalAmt("1");
            orderDetail.setTotalNum("1");
            orderDetail.setMerUnitDetail("商品简称^1^1");
            detailList.add(orderDetail);
            busiData.setOrderDetail(detailList);

            busiData.setPyeeAcctIssrId("C1040311005293");
            busiData.setPyeeAcctTp("00");
            busiData.setPyeeNm("用户-" + openId);
            busiData.setPyeeAcctId("6221880000000030");
            busiData.setTerminalIp("127.0.0.1");
            busiData.setMerCustId("123");
            busiData.setPayEnv("01");
            busiData.setPhonePayEnv("02");

            request.setData(busiData);
            JSONObject requestJson = JSONObject.parseObject(JSON.toJSONString(request));
            OpenApiMessage<JSONObject> reqMsg = new OpenApiMessage<>();
            reqMsg.setHead(msgHead);
            reqMsg.setBody(requestJson);

            sendMsg(msgHead, reqMsg);
        } catch (Exception e) {
            logger.error("发起交易异常: ", e);
        }
    }

    static class UnionPayPayHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            Map<String, String> queryParams = parseQuery(exchange.getRequestURI());
            String orderId = queryParams.get("orderId");
            String openId = queryParams.get("openId");

            if (StringUtils.isBlank(orderId) || StringUtils.isBlank(openId)) {
                respond(exchange, 400, "Missing required parameters: orderId and/or openId");
                return;
            }

            try {
                onlinePay(orderId, openId);
                respond(exchange, 200, "✅ UnionPay payment triggered successfully.");
            } catch (Exception e) {
                respond(exchange, 500, "发起交易异常: " + e.getMessage());
            }
        }

        private void respond(HttpExchange exchange, int status, String message) throws IOException {
            byte[] res = message.getBytes();
            exchange.sendResponseHeaders(status, res.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(res);
            }
        }

        private Map<String, String> parseQuery(URI uri) throws UnsupportedEncodingException {
            Map<String, String> queryPairs = new HashMap<>();
            String query = uri.getRawQuery();
            if (query == null) return queryPairs;

            for (String pair : query.split("&")) {
                int idx = pair.indexOf("=");
                if (idx > 0 && idx < pair.length() - 1) {
                    String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                    String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                    queryPairs.put(key, value);
                }
            }
            return queryPairs;
        }
    }

    // other methods unchanged (orderRefund, queryOrder, convertMsgHead, sendMsg...)
}
