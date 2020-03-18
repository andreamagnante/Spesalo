package com.andrea.spesalo.Model;

public class CalendarioItem {

    private String Id;
    private String Data;
    private String Prezzo;

    public CalendarioItem() {
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getPrezzo() {
        return Prezzo;
    }

    public void setPrezzo(String prezzo) {
        Prezzo = prezzo;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
