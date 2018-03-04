package com.example.ilyada.tododenemem;

import java.io.Serializable;

/**
 * Created by ilyada on 11.12.2017.
 */

public class Notlar implements  Serializable
{

    private static final long serialVersionUID = 1L;


    String icerik;
    String baslik;
    String priority;
    String kategori;
    String deadline_tarihi;
    String yazim_tarihi;


    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getDeadline_tarihi() {
        return deadline_tarihi;
    }

    public void setDeadline_tarihi(String deadline_tarihi) {
        this.deadline_tarihi = deadline_tarihi;
    }

    public String getYazim_tarihi() {
        return yazim_tarihi;
    }

    public void setYazim_tarihi(String yazim_tarihi) {
        this.yazim_tarihi = yazim_tarihi;
    }



}
