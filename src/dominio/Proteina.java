package dominio;

public class Proteina {

    private final String id;
    private final String nombre;
    private final String funcion;

    public Proteina(String id, String nombre, String funcion) {
        this.id = id;
        this.nombre = nombre != null ? nombre : "";
        this.funcion = funcion != null ? funcion : "";
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFuncion() {
        return funcion;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", nombre, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Proteina p = (Proteina) obj;
        return id != null && id.equals(p.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
