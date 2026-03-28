package service;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MiniStatementService {

    public static void showMiniStatement(Connection con,
                                         int id) {

        String query =
                "select type, amount, date " +
                        "from transactions " +
                        "where account_id=? " +
                        "order by date desc limit 5";

        System.out.println("\n--- Mini Statement ---");

        try (
                PreparedStatement ps =
                        con.prepareStatement(query);

                FileWriter fw =
                        new FileWriter("mini_statement.txt", true)
        ) {

            ps.setInt(1, id);

            ResultSet rs =
                    ps.executeQuery();

            fw.write("\n------ MINI STATEMENT ------\n");

            boolean found = false;

            while (rs.next()) {

                found = true;

                String line =
                        rs.getString("type")
                                + " | ₹"
                                + rs.getDouble("amount")
                                + " | "
                                + rs.getTimestamp("date");

                System.out.println(line);

                fw.write(line + "\n");
            }

            if (!found) {

                System.out.println(
                        "No Transactions Found");

                fw.write("No Transactions Found\n");
            }

            fw.write("----------------------------\n");

            System.out.println(
                    "Mini Statement Saved");

        }

        catch (Exception e) {

            System.out.println(
                    "Mini Statement Failed");

            e.printStackTrace();
        }
    }
}