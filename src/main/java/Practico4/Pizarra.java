package Practico4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

public class Pizarra extends JPanel implements PropertyChangeListener {

    private static final Logger logger = LogManager.getRootLogger();

    private PizarraModelo modelo;
    private ProtocoloPizarra protocolo;
    private int origenX;
    private int origenY;

    public static void main(String[] args) {
        PizarraModelo modelo = new PizarraModelo();
        Pizarra vista = new Pizarra(modelo);
        modelo.addObserver(vista);
    }

    public Pizarra(PizarraModelo modelo) {
        this.modelo = modelo;

        JFrame ventana = new JFrame("Pizarra");
        ventana.setSize(800, 600);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton botonCuadrado = new JButton("Cuadrado");
        JButton botonCirculo  = new JButton("Círculo");
        JButton botonConectar = new JButton("Conectar a otra pizarra");
        JButton botonServidor = new JButton("Ser Servidor");
        JButton botonEnviar = new JButton("Enviar");
        JTextField entradaCliente = new JTextField(30);
        JLabel salidaServidor = new JLabel("Respuesta Servidor");


        botonCuadrado.addActionListener(e -> modelo.setForma("cuadrado"));
        botonCirculo.addActionListener(e -> modelo.setForma("circulo"));

        botonServidor.addActionListener(e -> {
            logger.info("Pizarra ahora es servidor");
            botonServidor.setEnabled(false);
            botonServidor.setText("Esperando...");
            botonConectar.setEnabled(false);
            new Thread(new PizarraServidor(modelo)).start();
        });

        botonConectar.addActionListener(e -> {
            try {
                protocolo = ProtocoloPizarra.crearParaCliente(modelo);
                modelo.setProtocoloPizarra(protocolo);
                logger.info("Conectado al servidor con el puerto: " + ConfigRed.PUERTO);
                botonConectar.setEnabled(false);
                botonConectar.setText("Conectado");
                botonServidor.setEnabled(false);
            } catch (IOException ex) {
                logger.error("No se pudo conectar: " + ex.getMessage());
                JOptionPane.showMessageDialog(ventana,
                        "No se pudo conectar. ¿Apretaste 'Ser Servidor' en la otra pizarra?",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        botonEnviar.addActionListener(e -> {
            ejecutarComando(entradaCliente,salidaServidor,botonConectar,botonServidor);
        });
        entradaCliente.addActionListener(e -> {
            ejecutarComando(entradaCliente, salidaServidor, botonConectar, botonServidor);
        });

        JPanel botones = new JPanel();
        botones.add(botonCuadrado);
        botones.add(botonCirculo);
        botones.add(botonConectar);
        botones.add(botonServidor);

        JPanel entradaSalida = new JPanel();
        entradaSalida.add(botonEnviar);
        entradaSalida.add(entradaCliente);
        entradaSalida.add(salidaServidor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                origenX = e.getX();
                origenY = e.getY();
                logger.info("Mouse presionado en x=" + origenX + " y=" + origenY);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int tamaño = Math.abs(e.getX() - origenX);
                if(tamaño < 10) tamaño = 10;
                logger.info("Figura agregada: " + modelo.getFormaSeleccionada()
                        + " x=" + origenX + " y=" + origenY + " tamaño=" + tamaño);
                modelo.agregarFigura(origenX, origenY, tamaño);
            }
        });

        ventana.add(botones, BorderLayout.NORTH);
        ventana.add(this, BorderLayout.CENTER);
        ventana.add(entradaSalida, BorderLayout.SOUTH);
        ventana.setVisible(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evento) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Figura figura : modelo.getListaFiguras()) {
            int tamaño = figura.getTamaño();
            if (figura.getForma().equals("cuadrado")) {
                g.setColor(Color.RED);
                g.fillRect(figura.getPosicionX(), figura.getPosicionY(), tamaño, tamaño);
            } else {
                g.setColor(Color.GREEN);
                g.fillOval(figura.getPosicionX(), figura.getPosicionY(), tamaño, tamaño);
            }
        }
    }

    private void ejecutarComando(JTextField campo, JLabel respuesta,
                                 JButton botonConectar, JButton botonServidor) {
        String texto = campo.getText().trim().toUpperCase();
        campo.setText("");

        if (protocolo == null) {
            respuesta.setText("Respuesta: No conectado");
            return;
        }

        try {
            ComandoPizarra cmd = comandoPorTipo(texto);
            if (cmd == null) {
                respuesta.setText("Respuesta: Comando desconocido");
                logger.error("Comando desconocido: " + texto);
                return;
            }
            cmd.ejecutarComoCliente(new Lista<>());
            respuesta.setText("Respuesta: OK");

            if (texto.equals(ConfigRed.CMD_CHAU)) {
                protocolo = null;
                modelo.setProtocoloPizarra(null);
                campo.setEnabled(false);
                botonConectar.setEnabled(true);  botonConectar.setText("Conectar");
                botonServidor.setEnabled(true);  botonServidor.setText("Ser Servidor");
                respuesta.setText("Respuesta: Desconectado");
            }

        } catch (IOException ex) {
            respuesta.setText("Respuesta: ERROR");
            logger.error("Error");
        }
    }

    private ComandoPizarra comandoPorTipo(String texto) {
        TipoComando tipo = null;
        for (TipoComando t : TipoComando.values()) {
            if (ConfigRed.expresiones.get(t).equals(texto)) {
                tipo = t;
                break;
            }
        }
        if (tipo == null) return null;

        return switch (tipo) {
            case Hola  -> new ComandoHola(protocolo);
            case Lista -> new ComandoLista(protocolo, modelo);
            case Chau  -> new ComandoChau(protocolo);
            default    -> null;
        };
    }
}
