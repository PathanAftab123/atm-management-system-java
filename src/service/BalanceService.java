package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BalanceService {

    public static void checkBalance(Connection con,
                                    int id) {

        try {

            String query =
                    "select balance from accounts where id=?";

            try (PreparedStatement ps =
                         con.prepareStatement(query)) {

                ps.setInt(1, id);

                ResultSet rs =
                        ps.executeQuery();

                if (rs.next()) {

                    double balance =
                            rs.getDouble("balance");

                    System.out.println(
                            "Current Balance: ₹"
                                    + balance);

                }

                else {

                    System.out.println(
                            "Account Not Found");
                }
            }

        }

        catch (Exception e) {

            System.out.println(
                    "Balance Check Failed");

            e.printStackTrace();
        }
    }
}