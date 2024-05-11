package my.snole.laba11.server;

import java.util.List;

public class Message {
    private Integer sender;
    private String method;
    private Integer port;
    private String clientListString;
    private Integer transferObjectCount;
    private List<TransferObject> ants;
    private Long millis;

    public Message(
            Integer sender,
            String method,
            Integer port,
            String clientListString,
            Integer transferObjectCount,
            List<TransferObject> ants
    ) {
        this.sender = sender;
        this.method = method;
        this.port = port;
        this.clientListString = clientListString;
        this.transferObjectCount = transferObjectCount;
        this.ants = ants;
        this.millis = System.currentTimeMillis();
    }

    public Message() {
    }

    public String getClientListString() {
        return clientListString;
    }

    public void setClientListString(String clientListString) {
        this.clientListString = clientListString;
    }

    public Long getMillis() {
        return millis;
    }

    public void setMillis(Long millis) {
        this.millis = millis;
    }

    public List<TransferObject> getAnts() {
        return ants;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setAnts(List<TransferObject> ants) {
        this.ants = ants;
    }

    public Integer getSender() {
        return sender;
    }

    public void setSender(Integer sender) {
        this.sender = sender;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getTransferObjectCount() {
        return transferObjectCount;
    }

    public void setTransferObjectCount(Integer transferObjectCount) {
        this.transferObjectCount = transferObjectCount;
    }
}

