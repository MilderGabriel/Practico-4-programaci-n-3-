package Practico4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProtocoloPizarra implements Runnable {

    private static final Logger logger = LogManager.getRootLogger();

    private final Socket         socket;
    private final PrintWriter    salida;
    private final BufferedReader entrada;
    private final PizarraModelo  modelo;

    private ProtocoloPizarra(Socket socket, PizarraModelo modelo) throws IOException {
        this.socket  = socket;
        this.modelo  = modelo;
        this.salida  = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())), true);
        this.entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
    }

    public static ProtocoloPizarra crearParaServidor(Socket socket, PizarraModelo modelo)
            throws IOException {
        return new ProtocoloPizarra(socket, modelo);
    }

    public static ProtocoloPizarra crearParaCliente(PizarraModelo modelo)
            throws IOException {
        Socket socket = new Socket(InetAddress.getLocalHost(), ConfigRed.PUERTO);
        return new ProtocoloPizarra(socket, modelo);
    }

    @Override
    public void run() {
        try {
            ComandoPizarra primercmd = comandoRecibido();
            if(!(primercmd instanceof ComandoHola)) {
                logger.error("El cliente no conoce el protocolo, debe comenzar con HOLA");
                salida.println("ERROR");
                cerrarConexion();
                return;
            }
            primercmd.atenderComandoSegunProtocolo();
            boolean sesionTerminada = false;
            while (!sesionTerminada) {
                ComandoPizarra cmd = comandoRecibido();
                if (cmd == null) {
                    logger.error("Comando desconocido");
                    continue;
                }
                sesionTerminada = cmd.atenderComandoSegunProtocolo();
            }
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
        } finally {
            cerrarConexion();
            logger.info("Cerrando conexión");
        }
    }

    public ComandoPizarra comandoRecibido() throws IOException {
        String linea = entrada.readLine();
        if (linea == null) return null;
        logger.info("<<< " + linea);

        for (TipoComando tipo : TipoComando.values()) {
            Pattern pattern = Pattern.compile(ConfigRed.expresiones.get(tipo));
            Matcher matcher = pattern.matcher(linea.trim());
            if (matcher.find()) {
                ComandoPizarra cmd = switch (tipo) {
                    case Hola   -> new ComandoHola(this);
                    case Lista  -> new ComandoLista(this, modelo);
                    case Figura -> new ComandoFigura(this, modelo);
                    case Chau   -> new ComandoChau(this);
                    default     -> null;
                };
                return cmd;
            }
        }
        return null;
    }

    public void enviarFigura(Figura fig) throws IOException {
        Lista<Object> args = new Lista<>();
        args.insertar(fig);
        new ComandoFigura(this, modelo).ejecutarComoCliente(args);
    }

    public PrintWriter    getSalida()  { return salida;  }
    public BufferedReader getEntrada() { return entrada; }

    private void cerrarConexion() {
        try { socket.close(); } catch (IOException e) { logger.error(e.getMessage()); }
    }
}
