package Practico4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PizarraServidor implements Runnable {

    private static final Logger logger = LogManager.getRootLogger();
    private PizarraModelo modelo;

    public PizarraServidor(PizarraModelo modelo) {
        this.modelo = modelo;
    }

    @Override
    public void run() {
        try {
            ServerSocket socketServer = new ServerSocket(ConfigRed.PUERTO);
            logger.info("Servidor esperando en puerto " + ConfigRed.PUERTO);
            while (true) {
                Socket clt = socketServer.accept();
                logger.info("Se conectó una pizarra: " + clt.getInetAddress());
                Thread t = new Thread(
                        ProtocoloPizarra.crearParaServidor(clt, modelo)
                );
                t.start();
            }
        } catch (IOException e) {
            logger.error("Error en servidor: " + e.getMessage());
        }
    }
}