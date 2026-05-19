package Practico4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComandoLista extends ComandoPizarra {
    private static final Logger logger = LogManager.getRootLogger();
    private final PizarraModelo modelo;

    public ComandoLista(ProtocoloPizarra protocolo, PizarraModelo modelo) {
        this.protocolo      = protocolo;
        this.modelo         = modelo;
        this.nombre         = "Comando Lista";
        this.regexPrincipal = ConfigRed.expresiones.get(TipoComando.Lista);
    }

    @Override
    public boolean atenderComandoSegunProtocolo() throws IOException {
        logger.info("Se recibio el comando " + nombre + " del lado del servidor");
        enviarLista();

        String ok = protocolo.getEntrada().readLine();
        logger.info("<<< " + ok);

        logger.info(">>> " + ConfigRed.CMD_LISTA);
        protocolo.getSalida().println(ConfigRed.CMD_LISTA);

        recibirLista();

        protocolo.getSalida().println(ConfigRed.CMD_OK);
        logger.info(">>> " + ConfigRed.CMD_OK);

        modelo.setListaCambiada(true);

        return false;
    }

    @Override
    public void ejecutarComoCliente(Lista<Object> argumentos) throws IOException {
        logger.info("Se mando el comando " + nombre + " del lado del cliente");
        logger.info(">>> " + ConfigRed.CMD_LISTA);
        protocolo.getSalida().println(ConfigRed.CMD_LISTA);

        recibirLista();

        protocolo.getSalida().println(ConfigRed.CMD_OK);
        logger.info(">>> " + ConfigRed.CMD_OK);

        String cmd = protocolo.getEntrada().readLine();
        logger.info("<<< " + cmd);

        if (ConfigRed.CMD_LISTA.equals(cmd)) {
            enviarLista();

            String ok = protocolo.getEntrada().readLine();
            logger.info("<<< " + ok);

            modelo.setListaCambiada(true);
        }
    }

    private void enviarLista() throws IOException {
        int cantidad = 0;
        for (Figura f : modelo.getListaFiguras()) {
            if (!f.esRemota()) cantidad++;
        }
        logger.info(">>> " + cantidad);
        protocolo.getSalida().println(cantidad);
        for (Figura f : modelo.getListaFiguras()) {
            if (!f.esRemota()) {
                String linea = construirLinea(f);
                logger.info(">>> " + linea);
                protocolo.getSalida().println(linea);
            }
        }
    }

        private void recibirLista() throws IOException {
        String lineaCantidad = protocolo.getEntrada().readLine();
        if (lineaCantidad == null) {
            logger.error("Conexión cerrada al recibir lista");
            return;
        }
        logger.info("<<< " + lineaCantidad);
        int cantidad = Integer.parseInt(lineaCantidad.trim());
        for (int i = 0; i < cantidad; i++) {
            String linea = protocolo.getEntrada().readLine();
            logger.info("<<< " + linea);
            Figura figura = parsearFigura(linea);
            if (figura != null) modelo.agregarFiguraRemota(figura);
        }
    }

    public static String construirLinea(Figura f) {
        if (f.getForma().equals("cuadrado")) {
            return "CUADRADO " + f.getPosicionX() + " " + f.getPosicionY()
                    + " " + f.getTamaño() + " " + f.getTamaño();
        } else {
            return "CIRCULO " + f.getPosicionX() + " " + f.getPosicionY()
                    + " " + f.getTamaño();
        }
    }

    public static Figura parsearFigura(String linea) {
        if (linea == null) return null;

        Pattern patCuadrado = Pattern.compile(ConfigRed.REGEX_CUADRADO);
        Matcher m = patCuadrado.matcher(linea);
        if (m.find()) {
            return new Figura("cuadrado",
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2)),
                    Integer.parseInt(m.group(3)));
        }

        Pattern patCirculo = Pattern.compile(ConfigRed.REGEX_CIRCULO);
        m = patCirculo.matcher(linea);
        if (m.find()) {
            return new Figura("circulo",
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2)),
                    Integer.parseInt(m.group(3)));
        }

        return null;
    }
}