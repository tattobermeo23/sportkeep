package com.deportes.sp1.models;

public class PublicacionModelo {
    private String deporte, descripcion, creado_en, usuario, cancha, pais, estado, ciudad;

    public PublicacionModelo() {
    }


    public PublicacionModelo(String deporte, String descripcion, String creado_en, String usuario,
                              String cancha, String pais, String estado, String ciudad) {
        this.deporte = deporte;
        this.descripcion = descripcion;
        this.creado_en = creado_en;
        this.usuario = usuario;
        this.cancha = cancha;
        this.pais = pais;
        this.estado = estado;
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCreado_en() {
        return creado_en;
    }

    public void setCreado_en(String creado_en) {
        this.creado_en = creado_en;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDeporte() {
        return deporte;
    }

    public void setDeporte(String deporte) {
        this.deporte = deporte;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCancha() {
        return cancha;
    }

    public void setCancha(String cancha) {
        this.cancha = cancha;
    }
}
