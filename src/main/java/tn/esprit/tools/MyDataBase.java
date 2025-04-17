package tn.esprit.tools;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    public static final String URL="jdbc:mysql://localhost:3306/pidev";
    public static final String USER="root";
    public static final String PWD="";
    private Connection cnx;
    static MyDataBase myDataBase;
    private MyDataBase(){
        try {
            cnx= DriverManager.getConnection(URL,USER,PWD);
            System.out.println("cnx etablie !!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());;
        }
    }
    public static MyDataBase getInstance(){
        if(myDataBase==null)
            myDataBase=new MyDataBase();
        return myDataBase;
    }

    public Connection getCnx() {
        return cnx;
    }
}

