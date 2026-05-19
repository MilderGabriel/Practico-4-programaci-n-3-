package Practico4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;

public class ComandoHola extends ComandoPizarra {
    private static final Logger logger = LogManager.getRootLogger();

    public ComandoHola(ProtocoloPizarra protocolo) {
        this.protocolo      = protocolo;
        this.nombre         = "Comando Hola";
        this.regexPrincipal = ConfigRed.expresiones.get(TipoComando.Hola);
    }

    @Override
    public boolean atenderComandoSegunProtocolo() throws IOException {
        logger.info("Se recibio el comando " + nombre + " del lado del servidor");
        protocolo.getSalida().println(ConfigRed.CMD_OK);
        logger.info(">>> OK");
        return false;
    }

    @Override
    public void ejecutarComoCliente(Lista<Object> argumentos) throws IOException {
        logger.info("Se mando el comando " + nombre + " del lado del cliente");
        protocolo.getSalida().println(ConfigRed.CMD_HOLA);
        logger.info(">>> HOLA");
        String respuesta = protocolo.getEntrada().readLine();
        logger.info("<<< " + respuesta);
    }
}