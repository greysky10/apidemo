package demo.decrypts;

import com.alibaba.fastjson.JSON;
import com.pfpj.sm.SM2Utils;
import com.pfpj.sm.SM4Utils;
import demo.common.exceptions.ServerException;
import demo.common.pojo.common.OpenApiRequest;
import demo.common.pojo.common.OpenApiResponse;
import demo.common.util.SMUtil;
import demo.common.util.SerialNoUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class Demo {

    public static void main(String[] args) throws ServerException, IOException {
//        test1();
        test2();
    }

    /**
     * 解析服开发出的请求密文
     * @throws IOException
     */
    public static void test1() throws IOException {
        String partnerTxSriNo = SerialNoUtil.getSerialNo();
        //商户号
        String merchantId = "tradeGroupPre001";
        //服开公钥
        String sopPublicKey = "040485CEFE14C7AF854C66D5279239E88F2E8B881C3EB1B393003D2B9F09E7064447C1A3615875B05A9164F7F637151F115B89E70DFCCD0C25CF83268E21576921";
        //商户私钥
        String privateKey = "009378BDB7262E282910AAE680E0A83EE30EA2AB8D01E41FE880583D1DA512C51E";
        //商户公钥
        String publicKey = "04F0F770FDD6E188E31A27A84AC9D6D820D33CF6088A78B305C948A6D98479AC3E71D0AF0356D3C93229C27C1B345B4110DEFBF86885876977573468063EFD8F4F";
        //服开私钥
//        String sopPrivateKey = "00F32A9E3D6772EBAE2122FF1ADFB4F6B33B5F8A9D169E2387C0A982C00198212C";  //定版
        String sopPrivateKey = "2FF4C4A8E0682D481C6F495D75C0D5EB9D7454E49F4489ADF771ABE9732E1051";      //预演


        // 请求报文示例
//        String str = "{\"encryptKey\":\"048CD5D6DD7113DC015DA8231D7AE9148EA3D04C5DF501E5B18CF639863D9FCC09C05EC06538D703E4E4A40B7E4AE9150CF7EABD22659E976486F87C092F1778708A8C9BEBCCE3F49E2A7FEFF1D3D4C4542C7DB6C5EBAA3567E8139224F9AEB2D0D3179873C434F04134A5BACFD8ED6BED\",\"request\":\"E/y3AmOvS//fZHxTN/qecJOTFrJ3pp+5G2hZZlt7Yxxm8V2mqi2exFF1Oe65I1Vq5a4lpF8O6pkH\\r\\nbWSVOVKcOUtDNuvTcYKFpTx6sUAt+hjXI3IZPxI3+bkSCE68N8LBJeWdTPoRGwKfd2zeYaCp6vU/\\r\\nsME7mJcOxZBxWgNRGXlq7LpRKKmva35fEG2GgTOzFCMnGIP38825C+lDzZC18Z9aZy1gtu4YiaTS\\r\\nmvus9GPVwBZLv4v1nVuA5JzVcUlBYaQx2OjsbL5xZgHbzLmM6YFLxkk98Dc2sZ5rzjthoE3jP4Nq\\r\\nuGMnUu3M1vkdIK+oLXrRQKuT6OjKUAMJ6H5YrF8eG5YnriONXjXBhYDBmckgDVgBOEThmfE9Ir2n\\r\\n3ZRW+hOjohQ0JTC3oTF/MhLBdKBWbfJo05KjxjFGCW7v8IlmFv4c5ErMbAH7Hd2f6zexMm4vuz/O\\r\\nL/BmJuM7BeeEnLyHQFyqqP6VvirNCgbnJtf4AbnO/b1j0CSA6sIIICBqLODc8IpPugAgaAWO2rEG\\r\\nScaUogJKBlRHvWV9BgikFP9KUI38FAdGLDQ3Z87vOd//HcVwGkktMBjr1sRK+hjRRSs8G2J/Vcef\\r\\nE5j1QZ9IGaI/y1jXZUKTRdeAVDYlSEmiHzm4FfNUZklAFiIoMgGYAjD+Uaz25cfq5vULtCaiTYFf\\r\\nUjUsdjH8QqOsu7jGFweC9l4GF9kA98OIp8ko4FQmEwlSfzUBU5H4v3A1c1OUAyo5Ntg3asm9WHss\\r\\nCe0rsUaBrUHtzgb1seFG965xA560b+yjj1HpBEqFdlz3haF9k6vmloJjpnF4RlFzhA7egvfm7UJZ\\r\\nJ3xXB5mnWHXr7y8ZzAWOwgACd8/WsmFuBQxhrNiADzoNbxnUeiai2PJAbMtq+mAZT3VCUdBGLC0m\\r\\nfLtAyxY7xmDM1Qc0j/C9VbIWCMbuJceoiPM2hxErjSvfJ3ap8ECgl10l+cq1++YsAUzofst2RzV5\\r\\n9MInHthfrzp0ZNRmRRAx0h+IjWEBHYcRDwG68pKlukVAwHOyWL8d23foREH+jiNcU8Nl0xUi7wL/\\r\\nTqx2pNv6m8kgx7XHfcqn618/\",\"acceeToken\":\"\",\"signature\":\"f47ba33891577473a53ffaaad44258b2ac8658013603c33c2e46e2918228c5d9#299c2b180ed412e8329c9c7b61a12927f3c260137b2f74e648af266feed89d48\"}";
        String str = "{\"signature\":\"12399E55003DE02C7EFF2FDA94CF3196F05E54E0658E454B982B89943E57B8B0#26DF4361531F1B75A8C71DA3E7143E7762F4D77A0034895AC089D4DBF143603B\",\"encryptKey\":\"045FFE148EC31A50F38B8856808F26CB84449F48609684B7DB44CF2B42AD2549BC2BCBCD4459C53A63409AA1ECCC66D17CE2DDE4EAB7119C6108BD8F1140D1F890204195C08FBA36D310412F6BD09FA127F420B3D6EFF5EF73E657A8864CE065AA8569F8FF0D7E687FD23D31DD9C7CCBDB\",\"request\":\"dmSYKu9WtHwhN+SnLjk9IdNdvz4WBE1Ta7tKHjFbdS/sePwjibvFsyg5iTsLHK6mgRBIAfDZUVaJ\\r\\nmCy3/HaqcBRsmCskFBDUdE3/wFl1/GBJizjH1jcl7sYCdpAIgkJ/9c5yCd3Zn3cBjr4pGDzfcoYZ\\r\\nirAVWGkEsXXDkguoUnYd0lBMePDX8d8x9SW8sbOYRvqna0t8s0GB8YXEY8BvWypAPHWrK81ssCrR\\r\\nGEfZlii+SIP9/48vROnH6NQG7y4iR0KEGpjZlNPZzMMaYfiTyvlDvCBfUllwCPS1UwkDZDJGRj5S\\r\\nnsYoxDcLhDp+7ihYFpCOugPEBzvDwueO3d1OcXY9M1ct3YTVCynh+STYc0cUQ67OprqlXpAHaQ4V\\r\\n/gscRGm6BSt6Bmd4rlJ6ZdK2jQoBLe4qgivUOlKPA1o1fqxVbdA06Irsq7ddcY9byRqqqKpAIWQE\\r\\nhY1gcwF17p8Vr3thbgfwn6+D7IftId2eLgwJkRpAurh8V+0N0V493WZZIrweC7UsN+KI383xlwvh\\r\\nYUiSp1De56lgRTTYpinyVy6V1GE/rutgL6sHlsa9SNoQiS4gXavx7Js1tsQyHK5mHGq/JwNeHtZa\\r\\nsWCVqCV3Fb0lVqzTd1Z+NfcQsX+oOGeV8uRC2M/afgZ9eZsfivf0XjykLko6BypNLdFRA8/Uq/2j\\r\\nUB3Ffap6b4gI+oQamDELmiC3fNFLAbcWhJibdFVNjzTTdRIqvoWt+UNZXVo3oOIFLYSUGjMVApBn\\r\\nwAJWzqBn+YSEDJUhFZ1nNn01iU40oO5iTqKeHpGnnPB5N0ef1mHRXYZxZLCb50zdiAAPD5LjSsKb\\r\\n5vGt6Kvv0Xy/2DKA5jO1xaEgyC37EtmZAairXkNQ2aHiLqVaoWm9F/Oj76aXCvR36Vmq6+TrlU1I\\r\\nhv0WWpANhFo4I64bmnz5l/W3O0R9pwhaSerHMUkful7RYK26AvQEuFpI45qQPodHvnFK+lOLi7cq\\r\\nGmfVRoK26Nq+NL1DP5z6jy5KKtUVDenWLV1if54v4kVI6KhHvBflvGDz481052XrQ7Z6ybAsmr84\\r\\nNtctwqUXX5ochfIPXfZjwn/cv5uQZrI2hEoKokvGV95PEw==\",\"accessToken\":\"\"}";
        // 5 对请求进行解析
        OpenApiRequest openApiRequest = JSON.parseObject(str, OpenApiRequest.class);
        // 验签
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        sb.append(StringUtils.defaultString(openApiRequest.getRequest(), ""));
        sb.append(StringUtils.defaultString(openApiRequest.getEncryptKey(), ""));
        sb.append(StringUtils.defaultString(openApiRequest.getAccessToken(), ""));
        SM2Utils sm2Utils = new SM2Utils();
        boolean checked = sm2Utils.verifySign(merchantId, publicKey, sb.toString(), SMUtil.fromString(openApiRequest.getSignature()));
        if (checked) {
            // 解析密钥
            String respSm4Key = sm2Utils.decrypt(sopPrivateKey, openApiRequest.getEncryptKey());
            System.out.println("------------------------------");
            // 解析报文
            String respMessage = SM4Utils.decrypt(openApiRequest.getRequest(), "CBC", respSm4Key, "");
            System.out.println("请求报文明文:" + respMessage);
        } else {
            System.out.println("请求报文验签失败!");
        }
    }

    /**
     * 解析服开收到的密文
     * @throws IOException
     */
    public static void test2() throws IOException {
        String partnerTxSriNo = SerialNoUtil.getSerialNo();
        //预演
        String merchantId = "tradeGroupPre001";
        String sopPublicKey = "040485CEFE14C7AF854C66D5279239E88F2E8B881C3EB1B393003D2B9F09E7064447C1A3615875B05A9164F7F637151F115B89E70DFCCD0C25CF83268E21576921";
        String privateKey = "009378BDB7262E282910AAE680E0A83EE30EA2AB8D01E41FE880583D1DA512C51E";
        String publicKey = "04F0F770FDD6E188E31A27A84AC9D6D820D33CF6088A78B305C948A6D98479AC3E71D0AF0356D3C93229C27C1B345B4110DEFBF86885876977573468063EFD8F4F";
        String sopPrivateKey = "2FF4C4A8E0682D481C6F495D75C0D5EB9D7454E49F4489ADF771ABE9732E1051";

        //定版
//        String merchantId = "testMerchant001";
//        String sopPublicKey = "04CABE03249C94BDC8A6A4440DA1B2ADFACF73F4340E5F1B9A76463694B44C2E5600A9BEAA035739383C292CF9F1C4695FAAC7963CD5033D5D647A6B1EBE78EC6A";
//        String privateKey = "1F0E2F085955461A9B87820AFBD513712CAEA89687BE657DE4EC91613BE62D32";
//        String publicKey = "0493FC9669F3AAC5450284F9E2E54D65AADEF2F8AD77F8DE2F4C167BA2B1244205F2DF671590E841C01AF63AA6F5F2377367D4277CBDB7F1FF5039F55A55EC4BDF";
//        String sopPrivateKey = "00F32A9E3D6772EBAE2122FF1ADFB4F6B33B5F8A9D169E2387C0A982C00198212C";


        // 请求报文示例
        String str = "{\"accessToken\":\"\",\"encryptKey\":\"042D5AC4B6ADCCEBE6CCC545918556616C1467EBFE33BF672C52C4E3384485CFF06B4359AD7C7EED43A0F979C19B12CE40FDDCCD3E996CE6C3D08A45DA3EB6D8F741DB7F8403CE91D00E3DDB6D1E7EE07A309A50CA773D114FC232BA476DF92526AB1D0589BBD322BB9626E8BA7D4E2784\",\"response\":\"JG2Z6yVy3VN3blJUpzOw+1mNDR+rgKeaZAGQ8bMawVWPjdOCoLoe+KAVTb8B8wwo9Dbu58T/CrbU\\r\\nbdd6K6b5nLidEamB4GByvQQ5LGUX86hl6vNuvV202LbWf15nTOYoExED5u6B2UoCG+5SJSfNN9Pq\\r\\nA7mK9D7AjVvXA7Hl++ZBRx88Cp9kNGBIDNc3obsJjdHzNyEz/KTnVMd3AHi1Jaj3iL269vYcvFd7\\r\\nm2g1SUHRpO0cU4oxXYO/6zxavHDJ6JHrLSnddDGbmL5h0Gh02qir1vFHMpOJtGgT+yzKnovz4iVA\\r\\n6hiykmPmQ5qgA64j\",\"signature\":\"5f0a555101d599d42aa8c5d55c9541e4ad317adfe2b248b7275a7ceaf7bbc40#5b85ef65d3964818e76e0c0a9305a0e20e99a07edaf1d61edf146b203fb6f5c4\"}";//请求报文密文
        // 5 对请求进行解析
        OpenApiResponse openApiResponse = JSON.parseObject(str, OpenApiResponse.class);
        // 验签
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        sb.append(StringUtils.defaultString(openApiResponse.getResponse(), ""));
        sb.append(StringUtils.defaultString(openApiResponse.getEncryptKey(), ""));
        sb.append(StringUtils.defaultString(openApiResponse.getAccessToken(), ""));
        SM2Utils sm2Utils = new SM2Utils();
        boolean checked = sm2Utils.verifySign(merchantId, publicKey, sb.toString(), SMUtil.fromString(openApiResponse.getSignature()));
        if (checked) {
            // 解析密钥
            String respSm4Key = sm2Utils.decrypt(sopPrivateKey, openApiResponse.getEncryptKey());
            System.out.println("------------------------------");
            // 解析报文
            String respMessage = SM4Utils.decrypt(openApiResponse.getResponse(), "CBC", respSm4Key, "");
            System.out.println("响应报文明文:" + respMessage);
        } else {
            System.out.println("响应报文验签失败!");
        }
    }




}
