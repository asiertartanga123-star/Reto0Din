package com.mycompany.reto0din.model;

/**
 * Datos de un usuario que se muestran o procesan en la aplicacion.
 */
public final class Usuario {

    private final String dni;
    private final String nombre;
    private final String apellido;
    private final String correo;
    private final String telefono;
    private final String tipo;

    /**
     * Crea una instancia con los datos basicos de un usuario.
     *
     * @param dni identificador unico
     * @param nombre nombre
     * @param apellido apellido
     * @param correo correo electronico
     * @param telefono telefono, o {@code null} si no esta informado
     * @param tipo tipo de cuenta
     */
    public Usuario(String dni, String nombre, String apellido, String correo,
                   String telefono, String tipo) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.tipo = tipo;
    }

    /** @return DNI del usuario */
    public String getDni() {
        return dni;
    }

    /** @return nombre del usuario */
    public String getNombre() {
        return nombre;
    }

    /** @return apellido del usuario */
    public String getApellido() {
        return apellido;
    }

    /** @return correo electronico */
    public String getCorreo() {
        return correo;
    }

    /** @return telefono, o {@code null} si no esta informado */
    public String getTelefono() {
        return telefono;
    }

    /** @return tipo de cuenta */
    public String getTipo() {
        return tipo;
    }

    /** @return usuario formateado para la lista de administracion */
    @Override
    public String toString() {
        return String.format("%s | %s %s | %s | Tel: %s", dni, nombre, apellido,
            correo == null ? "" : correo, telefono == null ? "" : telefono);
    }
}
