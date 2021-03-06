import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CurrenciesTable {
    //считывает данные с онлайн-таблицы поддерживаемых валют
    public static void showCurrenciesTable() throws IOException {
        Document doc = Jsoup.connect("https://currencylayer.com/site_downloads/cl-currencies-table.txt").get(); //получение html-кода страницы
        Element table = doc.select("table").first(); //ищем таблицу
        Elements rows = table.select("tr"); //разбиваем таблицу на строки
        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.select("td");
            System.out.print(cols.get(0).text()); //столбец 1
            System.out.print(" - ");
            System.out.print(cols.get(1).text()); //столбец 2
            System.out.println();
        }
    }
}
