package ru.comebing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class App  {

    private JPanel panelMain;
    private JLabel countTitle;
    private JLabel count;
    private JButton addButton;
    private JTable dataTable;
    private JLabel topTitle;
    private JLabel maxTitle;
    private JTable tablePerformance;
    private JTable maxTable;
    private JScrollPane paneDataTable;
    private JScrollPane paneMaxTable;
    private JScrollPane paneTablePerformance;
    private JLabel discipline;
    private JLabel disciplineTitle;

    public String FSc;
    public String addId;
    public String addSem1;
    public String addSem2;

    public int maxSem1 = 0;
    public int maxSem2 = 0;
    public float maxMean = 0;

    public static int i = 0;

    static String tempData[] = {null, null, null, null, null};
    static float tempMaxData[][] = {{0, 0, 0}, {0, 0, 0}};

    static String data[][] = new String[1024][5];
    static String columns[] = {"ФИО", "№Студ. Билета", "Cеместр №1", "Семестр №2", "Среднее"};

    static String dataMaxTable[][] = new String[2][3];
    static String columnsMaxTable[] = {"Семестр #1", "Семестр #2", "Средняя"};

    static String dataTop[][] = new String[3][2];
    static String columnsDataTop[] = {"ФИО", "№ Студ. Билета"};

    public App() {
        initComponents();
        panelMain.setPreferredSize(new Dimension(900, 350));

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputDialog();
                data[i][0] = tempData[0];
                data[i][1] = tempData[1];
                data[i][2] = tempData[2];
                tempMaxData[0][0] += Integer.parseInt(tempData[2]);
                data[i][3] = tempData[3];
                tempMaxData[0][1] += Integer.parseInt(tempData[3]);
                data[i][4] = tempData[4];
                tempMaxData[0][2] += Float.parseFloat(tempData[4]);

                if (Integer.parseInt(tempData[2]) > maxSem1) {
                    maxSem1 = Integer.parseInt(tempData[2]);
                }
                if (Integer.parseInt(tempData[3]) > maxSem2) {
                    maxSem2 = Integer.parseInt(tempData[3]);
                }
                if (Float.parseFloat(tempData[4]) > maxMean) {
                    maxMean = Float.parseFloat(tempData[4]);
                }
                i++;

                reloadMaxTable();

                DefaultTableModel dataTableMode = new DefaultTableModel(data, columns);
                count.setText(Integer.toString(i));
                dataTable.setModel(dataTableMode);

                try {
                    Class.forName("org.sqlite.JDBC");
                    Connection conn = DriverManager.getConnection("jdbc:sqlite:F:/Jtable/tusur.db");
                    Statement stm = conn.createStatement();
                    stm.executeQuery("INSERT INTO tusur VALUES('"+tempData[0]+"','"+tempData[1]+"','"+tempData[2]+"','"+tempData[3]+"','"+tempData[4]+"');");
                    conn.close();
                    } catch (Exception e1){
                    System.out.println(e1.getMessage());
                }


                //PerformanceTable();
            }
        });
    }

    public void reloadMaxTable() {
        dataMaxTable[0][0] = tempMaxData[0][0] / i + "";
        dataMaxTable[0][1] = tempMaxData[0][1] / i + "";
        dataMaxTable[0][2] = tempMaxData[0][2] / i + "";

        dataMaxTable[1][0] = maxSem1+"";
        dataMaxTable[1][1] = maxSem2+"";
        dataMaxTable[1][2] = maxMean+"";

        DefaultTableModel maxTableMode = new DefaultTableModel(dataMaxTable, columnsMaxTable);
        maxTable.setModel(maxTableMode);

    }

    private void initComponents() {
        reloadTable();
        reloadMaxTable();
        maxTable.setModel(new DefaultTableModel(dataMaxTable, columnsMaxTable));

        DefaultTableModel performanceTableModel = new DefaultTableModel(dataTop, columnsDataTop);
        tablePerformance.setModel(performanceTableModel);

        PerformanceTable();

    }

    public void reloadTable() {
        try {
            Class.forName("org.sqlite.JDBC");

            Connection conn = DriverManager.getConnection("jdbc:sqlite:F:/Jtable/tusur.db");
            String query = "SELECT * FROM tusur;";

            Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery(query);

            while (res.next()) {
                int sem1 = res.getInt("Semestr1");
                int sem2 = res.getInt("Semestr2");
                float ArMean = ((float) sem1 + (float) sem2) / 2;

                data[i][0] = res.getString("FSc");
                data[i][1] = res.getInt("ID")+"";
                data[i][2] = sem1+"";
                tempMaxData[0][0]+= sem1;
                data[i][3] = sem2+"";
                tempMaxData[0][1]+= sem2;
                data[i][4] = ArMean+"";
                tempMaxData[0][2]+=ArMean;

                if(sem1>maxSem1){
                    maxSem1=sem1;
                }
                if(sem2>maxSem2){
                    maxSem2=sem2;
                }
                if(ArMean>maxMean){
                    maxMean=ArMean;
                }

                i++;
            }
            // data = (String[][])resizeArray(data, i); //resizeArray from i;

            DefaultTableModel dataTableMode = new DefaultTableModel(data, columns);
            dataTable.setModel(dataTableMode);

            count.setText(Integer.toString(i));
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void inputDialog() {
        tempData[0] = JOptionPane.showInputDialog("ФИО ученика:", JOptionPane.QUESTION_MESSAGE);
        tempData[1] = JOptionPane.showInputDialog("№Студ. Билета:", JOptionPane.QUESTION_MESSAGE);
        tempData[2] = JOptionPane.showInputDialog("Оценка за 1 семестра", JOptionPane.QUESTION_MESSAGE);
        tempData[3] = JOptionPane.showInputDialog("Оценка за 2 семестра", JOptionPane.QUESTION_MESSAGE);
        tempData[4] = ((((float)Integer.parseInt(tempData[2]))+((float)Integer.parseInt(tempData[3])))/2.0)+"";
    }

    public void PerformanceTable(){
        try {
            Class.forName("org.sqlite.JDBC");

            Connection conn2 = DriverManager.getConnection("jdbc:sqlite:F:/Jtable/tusur.db");
            String query2 = "SELECT * FROM tusur ORDER BY Amean DESC;";

            Statement stm = conn2.createStatement();
            ResultSet res = stm.executeQuery(query2);
            for (int j = 0; j < 3; j++) {
                res.next();
                dataTop[j][0] = res.getString("FSc");
                dataTop[j][1] = res.getString("ID");
                System.out.println(dataTop[j][0] + " " + dataTop[j][1]);
            }
            conn2.close();
            DefaultTableModel performanceTableModel = new DefaultTableModel(dataTop, columnsDataTop);
            tablePerformance.setModel(performanceTableModel);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



    public static void main(String[] args) throws Exception {



        JFrame frame = new JFrame("TUSUR");
        frame.setContentPane(new App().panelMain);
        frame.pack();
        frame.setVisible(true);


    }
}
