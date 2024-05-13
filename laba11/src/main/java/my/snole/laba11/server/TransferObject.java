package my.snole.laba11.server;

public class TransferObject {
    private AntType antType;
    private long birthTime;
    private long lifetime;
    private double birthX;
    private double birthY;

    public TransferObject(
            AntType antType,
            long birthTime,
            long lifetime,
            double birthX,
            double birthY

    ) {
        this.antType = antType;
        this.birthTime = birthTime;
        this.lifetime = lifetime;

        this.birthX = birthX;
        this.birthY = birthY;
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

    public double getBirthX() {
        return birthX;
    }

    public void setBirthX(double birthX) {
        this.birthX = birthX;
    }

    public double getBirthY() {
        return birthY;
    }

    public void setBirthY(double birthY) {
        this.birthY = birthY;
    }
}

