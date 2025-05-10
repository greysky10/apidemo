package demo.decrypts;


import com.alibaba.fastjson.JSONObject;
import com.pfpj.sm.SM2Utils;
import com.pfpj.sm.SM4Utils;
import demo.common.util.SMUtil;
import demo.common.util.SerialNoUtil;

import java.io.IOException;

public class UnpassDemo {

    public static void main(String[] args) throws IOException {
        String partnerTxSriNo = SerialNoUtil.getSerialNo();
        //url
        String url = "http://220.248.252.123:8443/sop-h5/biz/unionpay/tradeGroup001.htm?partnerTxSriNo=" + partnerTxSriNo;
        //商户号
        String merchantId = "tradeGroup001";
        //应用Id
        String appID = "1095755893794127872001";
        //服开公钥
        String sopPublicKey = "04680B9F7D73BF3300C75BB0190D9799038B88511BAD90853BF4F83EC229941006FA742CDC44551A1C01572017FDE940C5D495372D3C27A70F9D9F1F9E9531C9D4";
        //商户私钥
        String privateKey = "00CC541434413C70A7043B6F914325331800646087CD0326771786114D28F66EB4";
        //商户公钥
        String publicKey = "04152126DBF4ACD0326866F100C2AEAC8353E34A5832BE86EEEF4EEDD89B01EE873D592F28B95E5D08ECBB24F6F80876DCF541C15A9B86E2E5B7332509C0BCD31E";
        //服开私钥
        String sopPrivateKey = "39329A5634572AE85913A9F6003165CF71F0097A2251C3D7C5DFC3C0DE682154";

        //这里填入请求报文
        String str = "{\"accessToken\":\"\",\"encryptKey\":\"0427825F31E9EBFD2B3D571319AA365CC1E2195813A2F557390A19F03FF8AFDD01789B57B857703880EA89E24888C6FC387437AAC64935AEEDA5F124F3710985238EBF82908F7E55EC015A6FF123D8D688DFAA2E3C088162D98BE4F76F28C37BC0251218CE731530ABFABBB95980E55F9F\",\"request\":\"0YNjTw2FenIGaEY/XfJP679XXUFhDju8flIxNEZjr84Kq5JZT1abh+9xqd6jZc95uAHJA4DWKH/O\\r\\nVG8XdQj0/MZTJoTqFzv2hZ5+dEYbk7JiAd0MfP8CtnvHqXvvT4Fleges9YtfBv+ZHC6x4rGisQ3g\\r\\nQIkSfgrx2dZGzSnCZ9hYKYA0kdkaIOS09fsNCa337C0TBItgGHTRumaL24brpKaR7P4Ol2a9GN4B\\r\\nDl7dG1fMI21LUyco749DPzHZSIbTsAlFdz+RU7M6kCSSDfr8+Usdjy89fRNlWGVbA2NHERABPYDa\\r\\n4qYThmrcwzlQF0eKwjSaFVnu5yj69fDfMnI3Dfbg4J14beyO8FwQoPSLEImiLbVHPWikFrU1tnqg\\r\\nREVi0cDBTnHkBfjJpLbqf+f2XA6x9StS69PPYXILTzW6mcCOcS6Dy+OF+X/LtQxMRAVNJYsCHMw5\\r\\nlrKtdylz9yDJaXQSFECaZvSgcbq1rdHUyVWxHUJHlaSf3utWPaPULCPZYs6JTLZtCD8kdvBzJPF4\\r\\nacEmuPTWyD2el3v1CrGmkO0iBpPMKltVlikIDZNLhuIkxMP7E99fAvt/F2ICnbohvl1h6qBzy3FI\\r\\nqYhosw6GhXz/L/+HxVST6mXIcFUrnc6D1Hd+13hkZoB+4kaDKDU/siEzalO6Khs7bRv26Wr3XExd\\r\\nmUst7EJezcXhKeRR4zbUau+OSFlQsJyrxJQOwLWEdwG74urdYHSzLAeO4pRKu/VeqOqBVCqPsXWP\\r\\ntNZm04kbdAdoOrtoh/FXZL92OS2byUhVZQT6mZAytYXwpbFCbMsObudOw7HsObW1DglRs6ONh2eF\\r\\n2mp8E5xWfMeqcGmLLdyMLdVl1KySxvuLfLSqlDz7tyYAonXzdzrpz62PWBjDt0i6HJbf+It3aUNv\\r\\nP6gg+dtB7WBG2bgpugdlymJmAu1kuCs+jbgL7ktQFN+W+8d7QQyMBDzjf8M5CWvdogP4G2kWJRaD\\r\\nxKhhTbMedxGykkX/GFDrWSlK+1Vp7dpe/YmNIDc7mFenJixrqWS3H8AtVDI7TSZkFeGEq/xsSGNC\\r\\nfbscgU05Qkm7hfegYy5fe7Cc50bYjD4t5rBJtFleR2kk/fLjVV8XAAEJgB6rOglZYND44BKtwWhy\\r\\n49Xs0jS36jD22lMxH5HTCp+EaufEIzrf5IP8FWo+5sVdDY18cMC4xfimBd6gsyLi2L1AABGOLKDl\\r\\nDn8QuGA4qnOJs7Hzn3eL3V4WcvcV4Er9vFFZqAYumFIJ3AxlDdydgOVWUhtPCmFo+OOsv5dnxtKA\\r\\nDKaV1G6OwCgdMNJ0cGlYCL9zX0fuonCz1h5TqR7XzqATbilZKIT47DAxllIXZ1twJQAfzUtPDA7b\\r\\n0bHXc/dy81896EiXuRYCzPfP82qIibQfVu5gmKyk4VNJYQKlQ4qjk52/zJJqdBZTatMvxTUgYKhS\\r\\nAEXVr/o=\",\"signature\":\"8975bb8ed80150186c7e7793c3686f0c66121744eaa5af2ded9efd30b0c6d3a4#847615eebb5879800f02b5af55a486b1a3c7f7247629cdec7726d37e3a68dfbb\"}";



        // 5 对请求进行解析
        JSONObject openApiRequest = JSONObject.parseObject(str);
        // 验签
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        sb.append(openApiRequest.getString("request"));
        sb.append(openApiRequest.getString("encryptKey"));
        sb.append("");
        SM2Utils sm2Utils = new SM2Utils();
        boolean checked = sm2Utils.verifySign(merchantId, publicKey, sb.toString(), SMUtil.fromString(openApiRequest.getString("signature")));
        if (checked) {
            // 解析密钥
            String respSm4Key = sm2Utils.decrypt(sopPrivateKey, openApiRequest.getString("encryptKey"));
            System.out.println("------------------------------");
            // 解析报文
            String respMessage = SM4Utils.decrypt(openApiRequest.getString("request"), "CBC", respSm4Key, "");
            System.out.println("请求报文明文:" + respMessage);
        } else {
            System.out.println("请求报文验签失败!");
        }
    }

}
