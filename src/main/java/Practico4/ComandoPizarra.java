package Practico4;

import java.io.IOException;

public abstract class ComandoPizarra {

    protected String           regexPrincipal;
    protected String           nombre;
    protected ProtocoloPizarra protocolo;


    public abstract boolean atenderComandoSegunProtocolo() throws IOException;

    public abstract void ejecutarComoCliente(Lista<Object> argumentos) throws IOException;

}
