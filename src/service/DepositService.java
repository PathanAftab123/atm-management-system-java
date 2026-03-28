package service;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DepositService {

    public static void deposit(Connection con,
                               int id,
                               double amount) {

        // Amount validation
        if (amount <= 0) {
            System.out.println("Invalid Amount");
            return;
        }

        try {

            // ================= DEPOSIT =================

            String query =
                    "update accounts set balance = balance + ? where id=?";

            try (PreparedStatement ps =
                         con.prepareStatement(query)) {

                ps.setDouble(1, amount);
                ps.setInt(2, id);

                int rows =
                        ps.executeUpdate();

                if (rows > 0) {

                    System.out.println(
                            "Deposit Successful");

                    // Save transaction
                    saveTransaction(con,
                            id,
                            "DEPOSIT",
                            amount);

                }

                else {

                    System.out.println(
                            "Account Not Found");
                }
            }

        }

        catch (Exception e) {

            System.out.println(
                    "Deposit Failed");

            e.printStackTrace();
        }
    }

    // ================= SAVE TRANSACTION =================

    private static void saveTransaction(Connection con,
                                        int id,
                                        String type,
                                        double amount) {

        String query =
                "insert into transactions(account_id,type,amount,date) " +
                        "values(?,?,?,now())";

        try (PreparedStatement ps =
                     con.prepareStatement(query)) {

            ps.setInt(1, id);
            ps.setString(2, type);
            ps.setDouble(3, amount);

            ps.executeUpdate();

        }

        catch (Exception e) {

            e.printStackTrace();
        }
    }
}