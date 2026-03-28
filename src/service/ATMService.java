package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ATMService {

    //  Check ATM Cash
    public static boolean checkATMCash(Connection con,
                                       double amount) {

        try {

            String query =
                    "select total_cash from atm_cash where id=1";

            PreparedStatement ps =
                    con.prepareStatement(query);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                double atmCash =
                        rs.getDouble("total_cash");

                // ⚠️ LOW CASH WARNING
                if (atmCash < 5000) {

                    System.out.println(
                            "⚠️ Warning: ATM Cash Low (₹"
                                    + atmCash + ")");
                }

                // ❌ OUT OF CASH
                if (atmCash < amount) {

                    System.out.println(
                            "❌ ATM Out of Cash");

                    return false;
                }

                return true;
            }

        }

        catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }
}