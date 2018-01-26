package tp.skt.simple.element;



import tp.java.util.TextUtils;

import tp.skt.simple.common.Define;

/**
 * RPCResponse.java
 * <p>
 * Copyright (C) 2017. SK Telecom, All Rights Reserved.
 * Written 2017, by SK Telecom
 */
public class RPCResponse {
    /** command **/
    private StringElement cmd;
    /** command ID **/
    private IntElement cmdId;
    /** JSON RPC version **/
    private StringElement jsonrpc;
    /** request ID from server **/
    private LongElement id;
    /** control result **/
    private boolean result;
    /** result body(ArrayElement) **/
    private ArrayElement resultArray;   


    /**
     *
     *
     */
    public RPCResponse() {
    }

    /**
     *
     *
     * @param cmd
     * @param cmdId
     * @param jsonrpc
     * @param id
     * @param method
     * @param result
     * @param errorCode
     * @param errorMessage
     */
    public RPCResponse(String cmd, int cmdId, String jsonrpc, long id, boolean result, ArrayElement resultArray) {
        this.setCmd(cmd);
        this.setCmdId(cmdId);
        this.setJsonrpc(jsonrpc);
        this.setId(id);
        this.setResult(result);
        this.setResultArray(resultArray);
    }

    /**
     *
     *
     * @return
     */
    public StringElement getCmd() {
        return cmd;
    }

    /**
     *
     *
     * @param cmd
     */
    public void setCmd(String cmd) {
        if(TextUtils.isEmpty(cmd) == false) {
            this.cmd = new StringElement(Define.CMD, cmd);
        }
    }

    /**
     *
     *
     * @return
     */
    public IntElement getCmdId() {
        return cmdId;
    }

    /**
     *
     *
     * @param cmdId
     */
    public void setCmdId(int cmdId) {
        this.cmdId = new IntElement(Define.CMD_ID, cmdId);
    }

    /**
     *
     *
     * @return
     */
    public StringElement getJsonrpc() {
        return jsonrpc;
    }

    /**
     *
     *
     * @param jsonrpc
     */
    public void setJsonrpc(String jsonrpc) {
        if(TextUtils.isEmpty(jsonrpc) == false) {
            this.jsonrpc = new StringElement(Define.JSONRPC, jsonrpc);
        }
    }

    /**
     *
     *
     * @return
     */
    public LongElement getId() {
        return id;
    }

    /**
     *
     *
     * @param id
     */
    public void setId(long id) {
        this.id = new LongElement(Define.ID, id);
    }


    /**
     *
     *
     * @return
     */
    public boolean getResult() {
        return result;
    }

    /**
     *
     *
     * @param result
     */
    public void setResult(boolean result) {
        this.result = result;
    }
    
    /**
     * setResultArray
     *
     */
    public void setResultArray(ArrayElement resultArray) {
        this.resultArray = resultArray;
    }
    
    
    /**
     * getResultArray
     *
     */
    public ArrayElement getResultArray(){
        return this.resultArray;
    }
}


