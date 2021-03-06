package Main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFrame;
import configurator.*;
import DataBaseConnect.DataBase;
import DataBaseConnect.*;
import ReadWriteExcel.RWExcel;

public class Main {

    static DataBase workbase = new DataBase();
    static RWExcel rwexcel = new RWExcel();
    ReadConfigFile read = new ReadConfigFile();

    public static void main(String[] args) {
        workbase.connectionToBase();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main_JPanel().setVisible(true);

            }
        });
    }

    Main() {
        workbase.connectionToBase();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();  //размеры экрана
        int sizeWidth = 800;
        int sizeHeight = 600;
        int locationX = (screenSize.width - sizeWidth) / 2;
        int locationY = (screenSize.height - sizeHeight) / 2;
        Main_JPanel Windo_main = new Main_JPanel();
        JFrame frame = new JFrame();
        frame.setBounds(locationX, locationY, sizeWidth, sizeHeight); // Размеры и позиция
        frame.setContentPane(Windo_main); // Передаем нашу форму
        frame.setVisible(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE); // Закрытие приложения но можно более хитро с диалоговыми
    }

    static void fillDB(String patch_file) throws IOException, SQLException {//загрузка непосредственно в базу
        rwexcel.setPatchF(patch_file); // изменяем путь файла
        workbase.connectionToBase();
        Iterator<String> it_list_sheet = rwexcel.get_list_sheet().iterator(); //забираем список листов в файле и строим итератор из них
        while (it_list_sheet.hasNext()) {
            String name_table = it_list_sheet.next(); // название таблицы как имя листа из файла
            int maxlencol;
            if (name_table.equals("DO1")) {
                maxlencol = rwexcel.getMaxcColumn(name_table); //количество столбцов методом вычисления в фукции
            } else {
                maxlencol = rwexcel.getMaxcColumn(name_table); //количество столбцов методом вычисления в фукции
            }
            ArrayList<String> name_collums = new ArrayList<>(rwexcel.getDataNameTable(name_table)); // получаем массив столбцов и формируем от куда начинать считывать данные
            // так переопределим длину от куда тащим названия
            maxlencol = name_collums.size();
            workbase.createTable(name_table, maxlencol, name_collums); // передаем название таблицы и максимальное  - создание таблиц
            // берем данные с файла
            ArrayList<String[]> sheet_fromsheet_from;
            sheet_fromsheet_from = new ArrayList<>(rwexcel.getDataCell(name_table, maxlencol)); // maxlencol не верное вычисление похоже на 3 меньше в DO1 чем должно быть
            Iterator<String[]> iter_sheet = sheet_fromsheet_from.iterator();
            while (iter_sheet.hasNext()) {

                String[] dataFromFile = iter_sheet.next();
                workbase.insertRows(name_table, dataFromFile, name_collums); //Вносим данные в базу

            }

        }
    }
}
