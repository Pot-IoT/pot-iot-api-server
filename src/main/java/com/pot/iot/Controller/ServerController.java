package com.pot.iot.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ServerController {
    @PostMapping(value="/test")
//    public String test(@RequestBody Map<String,String> iotPayload){
    public String test(@RequestParam("payload") String payload){
//        String payload=iotPayload.get("payload");
        byte[] bytes=payload.getBytes();
        List<Integer> payloads=new ArrayList<>();
        List<Integer> param=new ArrayList<>();
        for (int i=0;i<bytes.length;++i){
            int value=bytes[i] & 0xff;
            payloads.add(value);
//            System.out.println(value);
////            if(!payloads[i].equals("")) {
////                int value = Integer.parseInt(payloads[i], 16);
////                System.out.println(value);
////            }
        }
        int payloadLength=(int)(payloads.get(0)*Math.pow(256,1)+payloads.get(1));
        String imei="";
        if (payloadLength==payloads.size()){
            for (int i=2;i<18;++i){
                String tmp=Integer.toHexString(payloads.get(i));
                for (int j=tmp.length();j<2;++j){
                    tmp="0"+tmp;
                }
                imei=imei+tmp;
            }
            int counter=(int)(payloads.get(21)*Math.pow(256,3)+payloads.get(22)*Math.pow(256,2)+payloads.get(23)*Math.pow(256,1)+payloads.get(24));
            int status=payloads.get(25);
            int communicationInterval=(int)(payloads.get(26)*Math.pow(256,1)+payloads.get(27));
            int longtitude=(int)(payloads.get(28)*Math.pow(256,3)+payloads.get(29)*Math.pow(256,2)+payloads.get(30)*Math.pow(256,1)+payloads.get(31));
            int latitude=(int)(payloads.get(32)*Math.pow(256,3)+payloads.get(33)*Math.pow(256,2)+payloads.get(34)*Math.pow(256,1)+payloads.get(35));
            int battery=payloads.get(36);
            int buttonStatus=payloads.get(37);
            int crcValue=(int)(payloads.get(38)*Math.pow(256,1)+payloads.get(39));
            byte[] crcbytes= new byte[bytes.length-2];
            System.arraycopy(bytes,0,crcbytes,0,bytes.length-2);
            int crc=calculate_crc(crcbytes);
            param.add(payloadLength);
            param.add(counter);
            param.add(status);
            param.add(communicationInterval);
            param.add(longtitude);
            param.add(latitude);
            param.add(battery);
            param.add(buttonStatus);
            param.add(crcValue);
            param.add(crc);
        }
        System.out.println(param.toString());
        return param.toString()+" "+imei;
    }

    private int calculate_crc(byte[] bytes) {
        int i;
        int crc_value = 0;
        for (int len = 0; len < bytes.length; len++) {
            for (i = 0x80; i != 0; i >>= 1) {
                if ((crc_value & 0x8000) != 0) {
                    crc_value = (crc_value << 1) ^ 0x1021;
                } else {
                    crc_value = crc_value << 1;
                }
                if ((bytes[len] & i) != 0) {
                    crc_value ^= 0x1021;
                }
            }
        }
        return crc_value;
    }
}
