package com.andrea.spesalo.Model;

public class Product {
    private String Nome;
    private String Descrizione;
    private String Prezzo;
    private String Immagine;

    public Product() {
    }

    public String getNome() {

        return Nome;
    }

    public void setNome(String nome) {

        Nome = nome;
    }

    public String getDescrizione() {

        return Descrizione;
    }

    public void setDescrizione(String descrizione) {

        Descrizione = descrizione;
    }

    public String getPrezzo() {

        return Prezzo;
    }

    public void setPrezzo(String prezzo) {

        Prezzo = prezzo;
    }

    public String getImmagine() {

        return Immagine;
    }

    public void setImmagine(String immagine) {

        Immagine = immagine;
    }
}
