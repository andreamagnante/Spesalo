package com.andrea.spesalo.Model;

public class ListaItem {

    private String Nome;
    private Boolean checkboxLista;
    private Boolean Offerta;

    public ListaItem() {
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public Boolean getCheckboxLista() {
        return checkboxLista;
    }

    public void setCheckboxLista(Boolean checkboxLista) {
        this.checkboxLista = checkboxLista;
    }

    public Boolean getOfferta() {
        return Offerta;
    }

    public void setOfferta(Boolean offerta) {
        Offerta = offerta;
    }
}