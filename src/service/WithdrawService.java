package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WithdrawService {

    public static void withdraw(Connection con,
                                int id,
                                double amount) {

        try {

            // 🔥 DAILY LIMIT CHECK
            String limitQuery =
                    "select sum(amount) as total from transactions " +
                            "where account_id=? and type='WITHDRAW' " +
                            "and date(date)=curdate()";

            PreparedStatement psLimit =
                    con.prepareStatement(limitQuery);

            psLimit.setInt(1, id);

            ResultSet rsLimit =
                    psLimit.executeQuery();

            double todayWithdraw = 0;

            if (rsLimit.next()) {

                todayWithdraw =
                        rsLimit.getDouble("total");
            }

            if (todayWithdraw + amount > 10000) {

                System.out.println(
                        "❌ Daily Withdrawal Limit Exceeded (₹10000)");

                return;
            }

            // 🔥 CHECK BALANCE

            String checkQuery =
                    "select balance from accounts where id=?";

            PreparedStatement ps1 =
                    con.prepareStatement(checkQuery);

            ps1.setInt(1, id);

            ResultSet rs1 =
                    ps1.executeQuery();

            if (rs1.next()) {

                double currentBalance =
                        rs1.getDouble("balance");

                if (currentBalance >= amount) {

                    // 🔥 Withdraw

                    String withdrawQuery =
                            "update accounts set balance = balance - ? where id=?";

                    if (!ATMService.checkATMCash(con, amount)) {

                        return;
                    }
                    PreparedStatement ps2 =
                            con.prepareStatement(withdrawQuery);

                    ps2.setDouble(1, amount);
                    ps2.setInt(2, id);

                    ps2.executeUpdate();

                    System.out.println(
                            "Withdrawal Successful ✅");

                    // 🔥 Save transaction

                    saveTransaction(
                            con,
                            id,
                            "WITHDRAW",
                            amount);

                    // 🔥 Get updated balance

                    String balQuery =
                            "select balance from accounts where id=?";

                    PreparedStatement ps3 =
                            con.prepareStatement(balQuery);

                    ps3.setInt(1, id);

                    ResultSet rs2 =
                            ps3.executeQuery();

                    if (rs2.next()) {

                        double newBalance =
                                rs2.getDouble("balance");

                        // 🔥 Generate receipt

                        ReceiptService.generateReceipt(
                                id,
                                "WITHDRAW",
                                amount,
                                newBalance);
                    }

                }

                else {

                    System.out.println(
                            "❌ Insufficient Balance");
                }

            }

            else {

                System.out.println(
                        "Account Not Found ❌");
            }

        }

        catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static void saveTransaction(
            Connection con,
            int id,
            String type,
            double amount) {

        try {

            String query =
                    "insert into transactions(account_id,type,amount,date) " +
                            "values(?,?,?,now())";

            PreparedStatement ps =
                    con.prepareStatement(query);

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