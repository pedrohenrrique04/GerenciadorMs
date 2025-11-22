package Model;

public class VendaMensal {
    private int mes;
    private double total;

    public VendaMensal(int mes, double total) {
        this.mes = mes;
        this.total = total;
    }

    public int getMes() { return mes; }
    public double getTotal() { return total; }
}
