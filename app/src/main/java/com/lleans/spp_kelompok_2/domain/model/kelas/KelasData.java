package com.lleans.spp_kelompok_2.domain.model.kelas;

import com.google.gson.annotations.SerializedName;

public class KelasData {

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("nama_kelas")
    private String namaKelas;

    @SerializedName("angkatan")
    private Long angkatan;

    @SerializedName("id_kelas")
    private int idKelas;

    @SerializedName("jurusan")
    private String jurusan;

    @SerializedName("updatedAt")
    private String updatedAt;

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setAngkatan(Long angkatan) {
        this.angkatan = angkatan;
    }

    public Long getAngkatan() {
        return angkatan;
    }

    public void setIdKelas(int idKelas) {
        this.idKelas = idKelas;
    }

    public int getIdKelas() {
        return idKelas;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}