package com.andrea.spesalo.Model;

public class CartItem {

    private String Nome;
    private String Prezzo;
    private String Quantita;
    private String PrezzoFinale;

    public CartItem() {
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getPrezzo() {
        return Prezzo;
    }

    public void setPrezzo(String prezzo) {
        Prezzo = prezzo;
    }

    public String getQuantita() {
        return Quantita;
    }

    public void setQuantita(String quantita) {
        Quantita = quantita;
    }

    public String getPrezzoFinale() {
        return PrezzoFinale;
    }

    public void setPrezzoFinale(String prezzofinale) {
        PrezzoFinale = prezzofinale;
    }
}
