
package Objetos;


public class Arco {
    
    private int id;
    private int peso;
    private int nodoOrigen;
    private int nodoDestino;

    public Arco() {
    }

    public Arco(int id, int peso, int nodoOrigen, int nodoDestino) {
        this.id = id;
        this.peso = peso;
        this.nodoOrigen = nodoOrigen;
        this.nodoDestino = nodoDestino;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public int getNodoOrigen() {
        return nodoOrigen;
    }

    public void setNodoOrigen(int nodoOrigen) {
        this.nodoOrigen = nodoOrigen;
    }

    public int getNodoDestino() {
        return nodoDestino;
    }

    public void setNodoDestino(int nodoDestino) {
        this.nodoDestino = nodoDestino;
    }

    @Override
    public String toString() {
        return "Arco " + "id: " + id + ", peso: " + peso + ", Orig: " + nodoOrigen + ", Dest: " + nodoDestino;
    }
    
    
 
}
