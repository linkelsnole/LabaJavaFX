package my.snole.laba11.server;

public class TransferObject {
    private AntType antType;
    private long birthTime;
    private long lifetime;

    public TransferObject(
            AntType antType,
            long birthTime,
            long lifetime
    ) {
        this.antType = antType;
        this.birthTime = birthTime;
        this.lifetime = lifetime;
    }

    public TransferObject() {
    }

    public AntType getAntType() {
        return antType;
    }

    public void setAntType(AntType antType) {
        this.antType = antType;
    }

    public long getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(long birthTime) {
        this.birthTime = birthTime;
    }

    public long getLifetime() {
        return lifetime;
    }

    public void setLifetime(long lifetime) {
        this.lifetime = lifetime;
    }
}

