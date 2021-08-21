package com.deportes.sp1.models;

public class EntrenamientoModelo {
    private String cancha, ciudad, contacto, creado_en, deporte, descripcion, dia_semana, entrenador, estado, hora_fin, hora_inicio, usuario, pais;

    public EntrenamientoModelo() {
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EntrenamientoModelo(String cancha, String ciudad, String contacto, String creado_en, String deporte,
                               String descripcion, String dia_semana, String entrenador, String estado, String hora_fin, String hora_inicio,
                               String usuario, String pais) {
        this.cancha = cancha;
        this.ciudad = ciudad;
        this.contacto = contacto;
        this.creado_en = creado_en;
        this.deporte = deporte;
        this.descripcion = descripcion;
        this.dia_semana = dia_semana;
        this.entrenador = entrenador;
        this.estado = estado;
        this.hora_fin = hora_fin;
        this.hora_inicio = hora_inicio;
        this.usuario = usuario;
        this.pais = pais;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCreado_en() {
        return creado_en;
    }

    public void setCreado_en(String creado_en) {
        this.creado_en = creado_en;
    }

    public String getCancha() {
        return cancha;
    }

    public void setCancha(String cancha) {
        this.cancha = cancha;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getDeporte() {
        return deporte;
    }

    public void setDeporte(String deporte) {
        this.deporte = deporte;
    }

    public String getDia_semana() {
        return dia_semana;
    }

    public void setDia_semana(String dia_semana) {
        this.dia_semana = dia_semana;
    }

    public String getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(String entrenador) {
        this.entrenador = entrenador;
    }

    public String getHora_fin() {
        return hora_fin;
    }

    public void setHora_fin(String hora_fin) {
        this.hora_fin = hora_fin;
    }

    public String getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
