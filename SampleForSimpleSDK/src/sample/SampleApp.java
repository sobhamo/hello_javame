/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package sample;

import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.midlet.*;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import sample.SimpleMessage.RPCRequest;
import tp.skt.simple.api.DataFormat;
import tp.java.util.Log;
import tp.skt.simple.api.Simple;
import tp.skt.simple.element.ArrayElement;
import tp.skt.simple.api.ResultListener;
import tp.skt.simple.api.Configuration;
import tp.skt.simple.api.ConnectionListener;
import tp.skt.simple.common.Util;
import tp.skt.simple.element.BooleanElement;
import tp.skt.simple.element.DoubleElement;
import tp.skt.simple.element.FloatElement;
import tp.skt.simple.element.IntElement;
import tp.skt.simple.element.LongElement;
import tp.skt.simple.element.RPCResponse;
import tp.skt.simple.element.StringElement;

/**
 * SampleApp.java
 * <p>
 * Copyright (C) 2017. SK Telecom, All Rights Reserved.
 * Written 2017, by SK Telecom
 */
public class SampleApp extends MIDlet {
    private Simple simple;
    
    private final DataFormat format = DataFormat.FORMAT_JSON;
    
    private Timer timer;
    
    public void startApp() {
        logInfo("ThingPlug_Simple_SDK");
        
        String hostAddress = Config.SIMPLE_SECURE_HOST;
        int hostPort = Config.SIMPLE_SECURE_PORT;
        
        Configuration configuration = new Configuration(hostAddress, hostPort, Config.SIMPLE_KEEP_ALIVE, Config.DEVICE_TOKEN, Config.DEVICE_TOKEN, null);
        simple = new Simple( Config.SIMPLE_SERVICE_NAME, Config.SIMPLE_DEVICE_NAME,
                configuration,
                connectionListener, true);
        
        simple.tpSimpleConnect();
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
        if(simple != null) {
            simple.tpSimpleDestroy();
        }
        
        if(timer != null){
            timer.cancel();
        }
    }
    
    
    private void attributeRaw(){
        String raw = "{\"rawData\":\"true\",\"sysLocationLatitude\":37.380257,\"sysHardwareVersion\":\"1.0\",\"sysLocationLongitude\":127.115479,\"sysErrorCode\":0,\"sysSerialNumber\":\"710DJC5I10000290\",\"led\":0}";
        simple.tpSimpleRawAttribute(raw, DataFormat.FORMAT_JSON, callback);
    }
    
    
    private void attribute() {
        
        if(format == DataFormat.FORMAT_CSV){
            String data = "1.0,710DJC5I10000290,0,127.115479,37.380257,7";
            
            simple.tpSimpleRawAttribute(data, format, callback);
        } else { // if(format == DataFormat.FORMAT_JSON)
            // {"sysHardwareVersion":"1.0", "sysSerialNumber":"710DJC5I10000290", "sysErrorCode":0, "sysLocationLongitude":127.115479, "sysLocationLatitude": 37.380257, "led":7}
            String data = "{\"sysHardwareVersion\":\"1.0\", \"sysSerialNumber\":\"710DJC5I10000290\", \"sysErrorCode\":0, \"sysLocationLongitude\":127.115479, \"sysLocationLatitude\": 37.380257, \"led\":7}";
            
            simple.tpSimpleRawAttribute(data, format, callback);
        }
        
//        ArrayElement element = new ArrayElement();
//        element.addStringElement("sysHardwareVersion", "1.0");
//        element.addStringElement("sysSerialNumber", "710DJC5I10000290");
//        element.addNumberElement("sysErrorCode", 0);
//        element.addNumberElement("sysLocationLongitude", 127.115479);
//        element.addNumberElement("sysLocationLatitude", 37.380257);
//        simple.tpSimpleAttribute(element, callback);
    }
    
    private void telemetry() {
        if(format == DataFormat.FORMAT_CSV){
//            String data = "267,48,26.26";
            String data = "" + getTimeStamp() + "," + Sensor.getTemperature() + ","+ Sensor.getHumidity() + ","+ Sensor.getLight();
            
            simple.tpSimpleRawTelemetry(data, format, callback);
        } else { // if(format == DataFormat.FORMAT_JSON)
            String data = "{\"light\":"+Sensor.getLight()+",\"humidity\":"+Sensor.getHumidity()+",\"temperature\":"+Sensor.getTemperature()+",\"ts\":" + getTimeStamp() +"}";
            
            simple.tpSimpleRawTelemetry(data, format, callback);
        }       
        
//        ArrayElement element = new ArrayElement();
//        element.addNumberElement("temp1", 26.26);
//        element.addNumberElement("humi1", 48);
//        element.addNumberElement("light1", 267);
//        simple.tpSimpleTelemetry(element, false, callback);
    }
    
    private int errorCode = 0;
    ResultListener callback = new ResultListener() {
        
        public void onResponse(int response) {
            errorCode = 0;
            
            Log.print("____________________________\n\n\n");
        }
        
        
        public void onFailure(int code, String message) {
            errorCode = code;
            
            Log.print("____________________________\n\n\n");
        }
    };
    
    TimerTask timerTask = new TimerTask(){

        public void run() {
             if(simple.tpSimpleIsConnected()){
                 telemetry();
             }else{
                 timer.cancel();
             }
        }
    };

    private int getTimeStamp(){
        return (int) (System.currentTimeMillis()/1000);
    }
    
    ConnectionListener connectionListener = new ConnectionListener() {
        
        public void onConnected() {
            final int delay = 0;
            final int period = 10*1000;
            
            logInfo("onConnected");
            
            if(null == timer){
                timer = new Timer();
            }else{
                timer.cancel();
            }
            
            timer.schedule(timerTask, delay, period);
        }
        
        
        public void onDisconnected() {
            logInfo("onDisconnected");
        }
        
        
        public void onSubscribed() {
            logInfo("onSubscribed");
            
            logInfo("send Attribute");
            attribute();
            logInfo("send Telemetry");
            telemetry();
        }
        
        
        public void onSubscribeFailure() {
            logInfo("onSubscribFailure");
        }
        
        public void onDisconnectFailure() {
            logInfo("onDisconnectFailure");
        }
        
        public void onConnectFailure() {
            logInfo("onConnectFailure");
        }
        
        public void onConnectionLost() {
            logInfo("onConnectionLost");
        }
        
        
        public void onDelivered() {
            logInfo("onDelivered");
        }
        
        public void onMessageReceived(String topic, String payload) {
            logInfo("onMessageReceived topic : " + topic);
            logInfo("onMessageReceived payload : " + payload);
            
            try {
                SimpleMessage simpleMessage = SimpleMessage.parsing(payload);
                if(null != simpleMessage){
                    if(null != simpleMessage.result){
                        logInfo("onMessageReceived result : " + simpleMessage.result);
                        return;
                    }
                                        
                    if(null != simpleMessage.rpcReq){
                        RPCRequest rpcReq = simpleMessage.rpcReq;
                        if(null != rpcReq.method){
                            Log.print("\n\n\n");
                            logInfo("### " + rpcReq.method.toUpperCase() + " --------------START[[");
                        }
                        if(Define.RPC_RESET.equals(rpcReq.method)){
                            
                        }else if(Define.RPC_REBOOT.equals(rpcReq.method)){
                            
                        }else if(Define.RPC_UPLOAD.equals(rpcReq.method)){

                        }else if(Define.RPC_DOWNLOAD.equals(rpcReq.method)){

                        }else if(Define.RPC_SOFTWARE_INSTALL.equals(rpcReq.method)){

                        }else if(Define.RPC_SOFTWARE_REINSTALL.equals(rpcReq.method)){

                        }else if(Define.RPC_SOFTWARE_REUNINSTALL.equals(rpcReq.method)){

                        }else if(Define.RPC_SOFTWARE_UPDATE.equals(rpcReq.method)){

                        }else if(Define.RPC_FIRMWARE_UPGRADE.equals(rpcReq.method)){
                            
                            // DO FIRMWARE UPGRADE here...
                            
                            RPCResponse rsp = new RPCResponse();
                               rsp.setCmd(simpleMessage.cmd);
                               rsp.setCmdId(1);
                               rsp.setJsonrpc(rpcReq.jsonrpc);
                               rsp.setId(rpcReq.id);
                               rsp.setResult(true);
                               
                               ArrayElement arrayElement = new ArrayElement();
                               arrayElement.addStringElement("state", "SUCCESS");
                               rsp.setResultArray(arrayElement);
                               
                               String rawResult = convertRawResult(rsp);
                               simple.tpSimpleRawResult(rawResult, callback);
								
                            // ATCOM FINISHED

                        }else if(Define.RPC_CLOCK_SYNC.equals(rpcReq.method)){

                        }else if(Define.RPC_SIGNAL_STATUS_REPORT.equals(rpcReq.method)){

                        }else if(Define.RPC_REMOTE.equals(rpcReq.method)){

                            // DO REMOTE here...
                            
                            RPCResponse rsp = new RPCResponse();
                               rsp.setCmd(simpleMessage.cmd);
                               rsp.setCmdId(1);
                               rsp.setJsonrpc(rpcReq.jsonrpc);
                               rsp.setId(rpcReq.id);
                               rsp.setResult(true);
                               
                               ArrayElement arrayElement = new ArrayElement();
                               arrayElement.addStringElement("state", "SUCCESS");
                               rsp.setResultArray(arrayElement);
                               
                               String rawResult = convertRawResult(rsp);
                               simple.tpSimpleRawResult(rawResult, callback);
                            
                            // ATCOM FINISHED
                        }else if(Define.RPC_USER.equals(rpcReq.method)){

                            
                            if(rpcReq.params.getJSONObject(0).has("led")){
                                int control = rpcReq.params.getJSONObject(0).getInt("led");
                                logInfo("rpc : " + rpcReq.jsonrpc+", id : "+rpcReq.id+"cmd : "+  control);
                                
                                // DO CONTROL here...
                                
                                boolean result = true;
                                
                                RPCResponse rsp = new RPCResponse();
                                rsp.setCmd(simpleMessage.cmd);
                                rsp.setCmdId(1);
                                rsp.setJsonrpc(rpcReq.jsonrpc);
                                rsp.setCmdId(1);
                                rsp.setId(rpcReq.id);
                                rsp.setResult(result);
                                if(result){
                                    ArrayElement arrayElement = new ArrayElement();
                                    arrayElement.addNumberElement("led", 7);
                                    rsp.setResultArray(arrayElement);
                                }else{
                                    ArrayElement arrayElement = new ArrayElement();
                                    arrayElement.addStringElement("message", "wrong parameters");
                                    rsp.setResultArray(arrayElement);

                                }
                                String rawResult = convertRawResult(rsp);
                                simple.tpSimpleRawResult(rawResult, callback);
//                                simple.tpSimpleResult(rsp, callback);
                            }
                        }
                        
                        
                        if(null != rpcReq.method){
                            logInfo("### " + rpcReq.method.toUpperCase() + " --------------]]END\n\n\n");
                        }
                    }else{
                        if("setAttribute".equals(simpleMessage.cmd) && null != simpleMessage.attribute){
                            Log.print("\n\n\n");
                            logInfo("### " + simpleMessage.cmd.toUpperCase() + " --------------START[[");
                            
                            
                            if(format == DataFormat.FORMAT_JSON){
                                int led = simpleMessage.attribute.getInt("led");
                                logInfo("led :" +  led + ", " + simpleMessage.cmdId);
                                                                
                                ArrayElement arrayElement = new ArrayElement();
                                arrayElement.addNumberElement("led", led);
                                
                                simple.tpSimpleAttribute(arrayElement, callback);

                                
                            }else{ //  if(format == DataFormat.FORMAT_CSV)
                                int led = simpleMessage.attribute.getInt("led");
                                String data = ",,,,," + led;
                                simple.tpSimpleRawAttribute(data, format, callback);
                            }
                            
                            logInfo("### " + simpleMessage.cmd.toUpperCase() + " --------------]]END\n\n\n");
                        }
                    }
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    };
    
    
    private String convertRawResult(RPCResponse response) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject rpcRspObject = new JSONObject();
            JSONObject resultObject;
            JSONObject errorObject;
            
            addElement(jsonObject, new StringElement(tp.skt.simple.common.Define.RESULT, response.getResult()?tp.skt.simple.common.Define.SUCCESS:tp.skt.simple.common.Define.FAIL));
            addElement(jsonObject, response.getCmd());
            addElement(jsonObject, response.getCmdId());            
            
            addElement(rpcRspObject, response.getJsonrpc());
            addElement(rpcRspObject, response.getId());
            
            ArrayElement arrayElement = response.getResultArray();
            if(null != arrayElement){
                 addElement(jsonObject, new StringElement(tp.skt.simple.common.Define.RESULT, tp.skt.simple.common.Define.SUCCESS));
                resultObject = new JSONObject();
                    
                int size = arrayElement.elements.size();
                for(int index = 0; index < size ; index++){
                    Object element = arrayElement.elements.elementAt(index);
                    boolean result = addElement(resultObject, element);
                    if (result == false) {
                        throw new Exception("Bad element!");
                    }
                }
                
                if(response.getResult()){
                    rpcRspObject.put(tp.skt.simple.common.Define.RESULT, resultObject);
                }else{
                    rpcRspObject.put(tp.skt.simple.common.Define.ERROR, resultObject);
                }
            }
            
            jsonObject.put(tp.skt.simple.common.Define.RPC_RSP, rpcRspObject);
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private boolean addElement(JSONObject jsonObject, Object element) throws JSONException {
        if(element == null) {
            return false;
        }

        if(element instanceof BooleanElement){
            jsonObject.put(((BooleanElement)element).name, ((BooleanElement)element).value);
        }else  if(element instanceof DoubleElement){
            jsonObject.put(((DoubleElement)element).name, ((DoubleElement)element).value);
        }else  if(element instanceof FloatElement){
            jsonObject.put(((FloatElement)element).name, ((FloatElement)element).value);
        }else  if(element instanceof IntElement){
            jsonObject.put(((IntElement)element).name, ((IntElement)element).value);
        }else  if(element instanceof LongElement){
            jsonObject.put(((LongElement)element).name, ((LongElement)element).value);
        }else  if(element instanceof StringElement){
            jsonObject.put(((StringElement)element).name, ((StringElement)element).value);
        }else{
            return false;
        }

        return true;
        
    }
    
    private void logInfo(String message){
        Log.printInfo(message);
    }
}
