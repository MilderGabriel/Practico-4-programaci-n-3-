package Practico4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;

public class ComandoFigura extends ComandoPizarra {
    private static final Logger logger = LogManager.getRootLogger();
    private final PizarraModelo modelo;

    public ComandoFigura(ProtocoloPizarra protocolo, PizarraModelo modelo) {
        this.protocolo      = protocolo;
        this.modelo         = modelo;
        this.nombre         = "Comando Figura";
        this.regexPrincipal = ConfigRed.expresiones.get(TipoComando.Figura);
    }

    @Override
    public boolean atenderComandoSegunProtocolo() throws IOException {
        logger.info("Se recibio el comando " + nombre + " del lado del servidor");
        protocolo.getSalida().println(ConfigRed.CMD_OK);
        logger.info(">>> OK");

        String lineaFigura = protocolo.getEntrada().readLine();
        logger.info("<<< " + lineaFigura);

        Figura figura = ComandoLista.parsearFigura(lineaFigura);
        if (figura != null) modelo.agregarFiguraRemota(figura);

        protocolo.getSalida().println(ConfigRed.CMD_OK);
        logger.info(">>> OK");
        return false;
    }

    @Override
    public void ejecutarComoCliente(Lista<Object> argumentos) throws IOException {
        logger.info("Se mando el comando " + nombre + " del lado del cliente");
        Figura figura = (Figura) argumentos.getPrimero().getContenido();

        protocolo.getSalida().println(ConfigRed.CMD_FIGURA);
        logger.info(">>> FIGURA");

        String ok1 = protocolo.getEntrada().readLine();
        logger.info("<<< " + ok1);

        String lineaFigura = ComandoLista.construirLinea(figura);
        protocolo.getSalida().println(lineaFigura);
        logger.info(">>> " + lineaFigura);

        String ok2 = protocolo.getEntrada().readLine();
        logger.info("<<< " + ok2);
    }
}
