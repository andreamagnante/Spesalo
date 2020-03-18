package com.andrea.spesaloadministration.Model;

public class OffertaItem {

    private String Nome;
    private String codice;
    private String originale;
    private String offerta;

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getOriginale() {
        return originale;
    }

    public void setOriginale(String originale) {
        this.originale = originale;
    }

    public String getOfferta() {
        return offerta;
    }

    public void setOfferta(String offerta) {
        this.offerta = offerta;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }
}
