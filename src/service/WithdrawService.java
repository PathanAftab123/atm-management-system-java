package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WithdrawService {

    public static void withdraw(Connection con,
                                int id,
                                double amount) {

        // Amount validation
        if (amount <= 0) {
            System.out.println("Invalid Amount");
            return;
        }

        try {

            // ================= DAILY LIMIT =================

            String limitQuery =
                    "select sum(amount) as total from transactions " +
                            "where account_id=? and type='WITHDRAW' " +
                            "and date(date)=curdate()";

            double todayWithdraw = 0;

            try (PreparedStatement psLimit =
                         con.prepareStatement(limitQuery)) {

                psLimit.setInt(1, id);

                ResultSet rsLimit =
                        psLimit.executeQuery();

                if (rsLimit.next()) {

                    todayWithdraw =
                            rsLimit.getDouble("total");
                }
            }

            if (todayWithdraw + amount > 10000) {

                System.out.println(
                        "Daily Withdrawal Limit Exceeded (₹10000)");

                return;
            }

            // ================= CHECK BALANCE =================

            String checkQuery =
                    "select balance from accounts where id=?";

            double currentBalance = 0;

            try (PreparedStatement ps1 =
                         con.prepareStatement(checkQuery)) {

                ps1.setInt(1, id);

                ResultSet rs1 =
                        ps1.executeQuery();

                if (!rs1.next()) {

                    System.out.println(
                            "Account Not Found");

                    return;
                }

                currentBalance =
                        rs1.getDouble("balance");
            }

            if (currentBalance < amount) {

                System.out.println(
                        "Insufficient Balance");

                return;
            }

            // ================= ATM CASH CHECK =================

            if (!ATMService.checkATMCash(con, amount)) {

                return;
            }

            // ================= WITHDRAW =================

            String withdrawQuery =
                    "update accounts set balance = balance - ? where id=?";

            try (PreparedStatement ps2 =
                         con.prepareStatement(withdrawQuery)) {

                ps2.setDouble(1, amount);
                ps2.setInt(2, id);

                ps2.executeUpdate();
            }

            System.out.println(
                    "Withdrawal Successful");

            // ================= SAVE TRANSACTION =================

            saveTransaction(
                    con,
                    id,
                    "WITHDRAW",
                    amount);

            // ================= GET NEW BALANCE =================

            String balQuery =
                    "select balance from accounts where id=?";

            try (PreparedStatement ps3 =
                         con.prepareStatement(balQuery)) {

                ps3.setInt(1, id);

                ResultSet rs2 =
                        ps3.executeQuery();

                if (rs2.next()) {

                    double newBalance =
                            rs2.getDouble("balance");

                    ReceiptService.generateReceipt(
                            id,
                            "WITHDRAW",
                            amount,
                            newBalance);
                }
            }

        }

        catch (Exception e) {

            System.out.println(
                    "Withdrawal Failed");

            e.printStackTrace();
        }
    }

    private static void saveTransaction(
            Connection con,
            int id,
            String type,
            double amount) {

        String query =
                "insert into transactions(account_id,type,amount,date) values(?,?,?,now())";

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