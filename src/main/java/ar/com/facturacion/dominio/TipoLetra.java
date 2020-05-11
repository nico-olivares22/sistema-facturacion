package ar.com.facturacion.dominio;

public enum TipoLetra {
    A("A"), B("B"), C("C");
    private String tipo;

    TipoLetra(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return tipo;
    }
}
