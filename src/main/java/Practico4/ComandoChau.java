package Practico4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;

public class ComandoChau extends ComandoPizarra {
    private static final Logger logger = LogManager.getRootLogger();

    public ComandoChau(ProtocoloPizarra protocolo) {
        this.protocolo      = protocolo;
        this.nombre         = "Comando Chau";
        this.regexPrincipal = ConfigRed.expresiones.get(TipoComando.Chau);
    }

    @Override
    public boolean atenderComandoSegunProtocolo() throws IOException {
        logger.info("Se recibio el comando " + nombre + " del lado del servidor");
        protocolo.getSalida().println(ConfigRed.CMD_OK);
        logger.info(">>> OK");
        return true;
    }

    @Override
    public void ejecutarComoCliente(Lista<Object> argumentos) throws IOException {
        logger.info("Se mando el comando " + nombre + " del lado del cliente");
        protocolo.getSalida().println(ConfigRed.CMD_CHAU);
        logger.info(">>> CHAU");
        String respuesta = protocolo.getEntrada().readLine();
        logger.info("<<< " + respuesta);
    }
}
