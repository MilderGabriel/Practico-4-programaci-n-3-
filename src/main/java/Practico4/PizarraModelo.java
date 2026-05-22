package Practico4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PizarraModelo {

    private static final Logger logger = LogManager.getRootLogger();
    private Lista<Figura> listaFiguras;
    private String formaSeleccionada;
    private PropertyChangeSupport observado;
    private ProtocoloPizarra protocoloPizarra;
    private boolean listaCambiada = false;

    public PizarraModelo() {
        listaFiguras = new Lista<>();
        formaSeleccionada = null;
        protocoloPizarra = null;
        observado = new PropertyChangeSupport(this);
    }

    public void addObserver(PropertyChangeListener listener) {
        observado.addPropertyChangeListener(listener);
    }

    public void agregarFigura(int x, int y, int tamaño) {
        if (formaSeleccionada == null) return;
        Figura figura = new Figura(formaSeleccionada,x,y,tamaño);
        listaFiguras.insertar(figura);
        observado.firePropertyChange("PIZARRA", true, false);
        if(protocoloPizarra != null && listaCambiada){
            try{
                protocoloPizarra.enviarFigura(figura);
            }
            catch(Exception e){
                logger.error("Error al enviar figura");
                protocoloPizarra = null;
            }
        }
    }

    public void agregarFiguraRemota(Figura figura) {
        figura.setEsRemota(true);
        listaFiguras.insertar(figura);
        observado.firePropertyChange("PIZARRA", true, false);
    }

    public Lista<Figura> getListaFiguras() {
        return listaFiguras;
    }

    public String getFormaSeleccionada() {
        return formaSeleccionada;
    }

    public void setForma(String formaSeleccionada) {
        this.formaSeleccionada = formaSeleccionada;
    }

    public void setProtocoloPizarra(ProtocoloPizarra protocoloPizarra) {
        this.protocoloPizarra = protocoloPizarra;
    }

    public void setListaCambiada(boolean valor) {this.listaCambiada = valor;}
}
