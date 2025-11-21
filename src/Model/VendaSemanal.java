package Model;

public class VendaSemanal {

    private String semana;   // Ex: "Semana 1"
    private double total;    // Total vendido na semana

    public VendaSemanal(String semana, double total) {
        this.semana = semana;
        this.total = total;
    }

    // GETTERS
    public String getSemana() {
        return semana;
    }

    public double getTotal() {
        return total;
    }

    // SETTERS (opcionais)
    public void setSemana(String semana) {
        this.semana = semana;
    }

    public void setTotal(double total) {
        this.total = total;
    }

}
