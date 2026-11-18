/** Representa los datos de un usuario. */


package com.agritech.models;

public class Usuario {

    private int id;
    private String nombreUsuario;
    private String passwordHash;

    public Usuario(int id, String nombreUsuario, String passwordHash) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}

