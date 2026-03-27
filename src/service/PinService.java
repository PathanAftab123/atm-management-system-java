package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PinService {

    // ================= CREATE ACCOUNT =================
    public static void createAccount(Connection con,
                                     String name,
                                     double balance,
                                     int pin) {

        try {

            String query =
                    "insert into accounts(name,balance,pin) values(?,?,?)";

            PreparedStatement ps =
                    con.prepareStatement(query);

            ps.setString(1, name);
            ps.setDouble(2, balance);
            ps.setInt(3, pin);

            ps.executeUpdate();

            System.out.println("Account Created Successfully ✅");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= LOGIN WITH LOCK + LAST LOGIN =================
    public static boolean login(Connection con,
                                int id,
                                int pin) {

        try {

            String query =
                    "select pin, failed_attempts, account_locked, last_login " +
                            "from accounts where id=?";

            PreparedStatement ps =
                    con.prepareStatement(query);

            ps.setInt(1, id);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                int dbPin =
                        rs.getInt("pin");

                int attempts =
                        rs.getInt("failed_attempts");

                boolean locked =
                        rs.getBoolean("account_locked");

                java.sql.Timestamp lastLogin =
                        rs.getTimestamp("last_login");

                // 🔴 Locked Check
                if (locked) {

                    System.out.println(
                            "❌ Account Locked. Contact Bank.");

                    return false;
                }

                // ✅ Correct PIN
                if (dbPin == pin) {

                    // 🔥 SHOW OLD LOGIN TIME
                    if (lastLogin != null) {

                        System.out.println(
                                "Last Login: "
                                        + lastLogin);
                    }

                    // Reset attempts
                    String resetQuery =
                            "update accounts set failed_attempts=0 where id=?";

                    PreparedStatement ps2 =
                            con.prepareStatement(resetQuery);

                    ps2.setInt(1, id);

                    ps2.executeUpdate();

                    // 🔥 UPDATE NEW LOGIN TIME
                    String updateLogin =
                            "update accounts set last_login=now() where id=?";

                    PreparedStatement ps3 =
                            con.prepareStatement(updateLogin);

                    ps3.setInt(1, id);

                    ps3.executeUpdate();

                    return true;
                }

                // ❌ Wrong PIN
                else {

                    attempts++;

                    if (attempts >= 3) {

                        String lockQuery =
                                "update accounts set account_locked=true where id=?";

                        PreparedStatement ps4 =
                                con.prepareStatement(lockQuery);

                        ps4.setInt(1, id);

                        ps4.executeUpdate();

                        System.out.println(
                                "❌ Account Locked after 3 wrong attempts");

                    }

                    else {

                        String updateQuery =
                                "update accounts set failed_attempts=? where id=?";

                        PreparedStatement ps5 =
                                con.prepareStatement(updateQuery);

                        ps5.setInt(1, attempts);
                        ps5.setInt(2, id);

                        ps5.executeUpdate();

                        System.out.println(
                                "❌ Wrong PIN (" + attempts + "/3)");
                    }

                    return false;
                }

            }

            else {

                System.out.println(
                        "Account Not Found ❌");

                return false;
            }

        }

        catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    // ================= CHANGE PIN =================
    public static void changePin(Connection con,
                                 int id,
                                 int newPin) {

        try {

            String query =
                    "update accounts set pin=? where id=?";

            PreparedStatement ps =
                    con.prepareStatement(query);

            ps.setInt(1, newPin);
            ps.setInt(2, id);

            int rows =
                    ps.executeUpdate();

            if (rows > 0)

                System.out.println(
                        "PIN Updated Successfully ✅");

            else

                System.out.println(
                        "Account Not Found ❌");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}