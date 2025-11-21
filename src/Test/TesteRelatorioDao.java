package Test;

import Dao.RelatoriosDao;
import java.time.LocalDateTime;

public class TesteRelatorioDao {

    public static void main(String[] args) {

        RelatoriosDao dao = new RelatoriosDao();

        LocalDateTime inicio = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2025, 12, 31, 23, 59);

        double total = dao.getTotalVendas(inicio, fim);

        System.out.println("TOTAL = R$ " + total);
    }
}
