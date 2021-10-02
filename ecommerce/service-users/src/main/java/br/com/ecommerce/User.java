package br.com.ecommerce;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 4474172676041211603L;

    private final String uuid;

    public User(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getReportPath() {
        return "target/" + uuid + "-report.txt";
    }
    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                '}';
    }


}
