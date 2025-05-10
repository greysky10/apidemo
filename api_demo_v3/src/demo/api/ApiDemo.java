package demo.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pfpj.sm.SM2Utils;
import com.pfpj.sm.SM4Utils;
import com.pfpj.sm.Signature;
import demo.common.pojo.common.OpenApiMessage;
import demo.common.pojo.common.OpenApiMessageHead;
import demo.common.pojo.common.OpenApiRequest;
import demo.common.pojo.common.OpenApiResponse;
import demo.common.pojo.ecny.HardwareWalletOpenRequest;
import demo.common.util.HttpClientUtils;
import demo.common.util.SMUtil;
import demo.common.util.SerialNoUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Properties;

public class ApiDemo {

    private static final Logger logger = LoggerFactory.getLogger(ApiDemo.class);

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/hardware-wallet/open", new HardwareWalletOpenHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("✅ Server started at http://localhost:8080");
    }

    static class HardwareWalletOpenHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            try {
                Properties properties = new Properties();
                FileInputStream fis = new FileInputStream("resource/sit_config.properties");
                properties.load(fis);

                String merchantId = properties.getProperty("merchantId");
                String appID = properties.getProperty("appID");
                String moduleName = properties.getProperty("moduleName");
                String url = properties.getProperty("url");
                String privateKey = properties.getProperty("privateKey");
                String publicKey = properties.getProperty("publicKey");
                String sopPublicKey = properties.getProperty("sopPublicKey");

                OpenApiMessageHead msgHead = new OpenApiMessageHead();
                msgHead.setPartnerTxSriNo(SerialNoUtil.getSerialNo());
                msgHead.setMethod("ecny.openHardwareWallet");
                msgHead.setVersion("1");
                msgHead.setMerchantId(merchantId);
                msgHead.setAccessType("API");
                msgHead.setAppID(appID);
                msgHead.setReqTime(SerialNoUtil.getDateTime());

                logger.debug("原始请求报文头: {}", JSON.toJSONString(msgHead));

                HardwareWalletOpenRequest request = new HardwareWalletOpenRequest();
                request.setPhone("13544441235");
                request.setAPDURespData("000000230080000000020759273137bdbc52f06dafa969784a579ace6fb3830e929e5a58f5381008e64641203e241944ac1703d94d866a4de86eb7d1ed535b964c1400414721db5223f0a5084736323f");
                request.setDeviceName("vivo");
                request.setBusiMainId(msgHead.getPartnerTxSriNo());
                request.setReqTransTime(SerialNoUtil.getDateTime());

                String reqstr = JSONObject.toJSONString(request);
                JSONObject requestJson = JSONObject.parseObject(reqstr);

                OpenApiMessage<JSONObject> reqMsg = new OpenApiMessage<>();
                reqMsg.setHead(msgHead);
                reqMsg.setBody(requestJson);

                OpenApiRequest openApiRequest = new OpenApiRequest();
                String orignReqJsonStr = JSON.toJSONString(reqMsg);
                logger.debug("原始请求报文: {}", orignReqJsonStr);
                String sm4Key = SMUtil.getSM4Key();
                String encryptRequest = SM4Utils.encrypt(orignReqJsonStr, "CBC", sm4Key, "");
                openApiRequest.setRequest(encryptRequest);

                SM2Utils sm2Utils = new SM2Utils();
                String encryptKey = sm2Utils.encrypt(sopPublicKey, sm4Key);
                openApiRequest.setEncryptKey(encryptKey);
                openApiRequest.setAccessToken("");

                StringBuilder sb = new StringBuilder();
                sb.append(StringUtils.defaultString(openApiRequest.getRequest(), ""));
                sb.append(StringUtils.defaultString(openApiRequest.getEncryptKey(), ""));
                sb.append(StringUtils.defaultString(openApiRequest.getAccessToken(), ""));
                Signature sign = sm2Utils.sign(merchantId, privateKey, sb.toString(), publicKey);
                String signature = SMUtil.toSignStr(sign);
                openApiRequest.setSignature(signature);
                logger.info(signature);

                logger.debug("请求报文: {}", JSON.toJSONString(openApiRequest));
                url = url.replace("${moduleName}", moduleName)
                              .replace("${merchantId}", merchantId)
                              .replace("${partnerTxSriNo}", msgHead.getPartnerTxSriNo());

                String respJson = HttpClientUtils.post(url, JSON.toJSONString(openApiRequest));
                OpenApiResponse openApiResponse = JSON.parseObject(respJson, OpenApiResponse.class);

                sb.setLength(0);
                sb.append(StringUtils.defaultString(openApiResponse.getResponse(), ""));
                sb.append(StringUtils.defaultString(openApiResponse.getEncryptKey(), ""));
                sb.append(StringUtils.defaultString(openApiResponse.getAccessToken(), ""));

                boolean checked = sm2Utils.verifySign(merchantId, sopPublicKey, sb.toString(), SMUtil.fromString(openApiResponse.getSignature()));
                String result;

                if (checked) {
                    String respSm4Key = sm2Utils.decrypt(privateKey, openApiResponse.getEncryptKey());
                    logger.info("------------------------------");
                    logger.debug("响应报文sm4密钥:{}", respSm4Key);
                    String respMessage = SM4Utils.decrypt(openApiResponse.getResponse(), "CBC", respSm4Key, "");
                    logger.debug("响应报文:{}", respMessage);
                    result = respMessage;
                } else {
                    logger.error("响应报文验签失败!");
                    result = "验签失败";
                }

                byte[] responseBytes = result.getBytes();
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }

            } catch (Exception e) {
                logger.error("发起交易异常: ", e);
                String error = "发起交易异常: " + e.getMessage();
                byte[] responseBytes = error.getBytes();
                exchange.sendResponseHeaders(500, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            }
        }
    }
}
