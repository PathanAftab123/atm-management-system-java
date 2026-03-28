package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TransferService {

    public static void transfer(Connection con,
                                int fromId,
                                int toId,
                                double amount) {

        // Amount validation
        if (amount <= 0) {

            System.out.println("Invalid Amount");
            return;
        }

        // Same account check
        if (fromId == toId) {

            System.out.println(
                    "Cannot transfer to same account");

            return;
        }

        try {

            // Start transaction
            con.setAutoCommit(false);

            double senderBalance = 0;

            // ================= CHECK SENDER =================

            String senderQuery =
                    "select balance from accounts where id=?";

            try (PreparedStatement ps1 =
                         con.prepareStatement(senderQuery)) {

                ps1.setInt(1, fromId);

                ResultSet rs1 =
                        ps1.executeQuery();

                if (!rs1.next()) {

                    System.out.println(
                            "Sender Account Not Found");

                    con.rollback();
                    return;
                }

                senderBalance =
                        rs1.getDouble("balance");
            }

            // ================= CHECK RECEIVER =================

            String receiverQuery =
                    "select id from accounts where id=?";

            try (PreparedStatement ps2 =
                         con.prepareStatement(receiverQuery)) {

                ps2.setInt(1, toId);

                ResultSet rs2 =
                        ps2.executeQuery();

                if (!rs2.next()) {

                    System.out.println(
                            "Receiver Account Not Found");

                    con.rollback();
                    return;
                }
            }

            // ================= CHECK BALANCE =================

            if (senderBalance < amount) {

                System.out.println(
                        "Insufficient Balance");

                con.rollback();
                return;
            }

            // ================= DEDUCT SENDER =================

            String deductQuery =
                    "update accounts set balance = balance - ? where id=?";

            int rows1;

            try (PreparedStatement ps3 =
                         con.prepareStatement(deductQuery)) {

                ps3.setDouble(1, amount);
                ps3.setInt(2, fromId);

                rows1 =
                        ps3.executeUpdate();
            }

            // ================= ADD RECEIVER =================

            String addQuery =
                    "update accounts set balance = balance + ? where id=?";

            int rows2;

            try (PreparedStatement ps4 =
                         con.prepareStatement(addQuery)) {

                ps4.setDouble(1, amount);
                ps4.setInt(2, toId);

                rows2 =
                        ps4.executeUpdate();
            }

            // Safety check
            if (rows1 == 0 || rows2 == 0) {

                con.rollback();

                System.out.println(
                        "Transfer Failed");

                return;
            }

            // ================= SAVE TRANSACTIONS =================

            saveTransaction(con,
                    fromId,
                    "TRANSFER_SENT",
                    amount);

            saveTransaction(con,
                    toId,
                    "TRANSFER_RECEIVED",
                    amount);

            // Commit transaction
            con.commit();

            System.out.println(
                    "Transfer Successful");

            ReceiptService.generateReceipt(
                    fromId,
                    "TRANSFER",
                    amount,
                    senderBalance - amount);

        }

        catch (Exception e) {

            try {

                con.rollback();

            }

            catch (Exception ex) {

                ex.printStackTrace();
            }

            System.out.println(
                    "Transfer Failed");

            e.printStackTrace();
        }

        finally {

            try {

                con.setAutoCommit(true);

            }

            catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    // ================= SAVE TRANSACTION =================

    private static void saveTransaction(Connection con,
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