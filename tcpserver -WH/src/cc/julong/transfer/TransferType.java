package cc.julong.transfer;

public enum TransferType {
    GATHER("采集"), IMPORT("导入"), EXPORT("导出"), REPORT("上报"), DELETE("清除");

    private String name;

    TransferType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.valueOf(this.name);
    }

}
