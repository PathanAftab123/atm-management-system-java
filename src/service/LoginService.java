package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class LoginService {

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

                Timestamp lastLogin =
                        rs.getTimestamp("last_login");

                // 🔴 Check locked
                if (locked) {

                    System.out.println(
                            "❌ Account Locked. Contact Bank.");

                    return false;
                }

                // ✅ Correct PIN
                if (dbPin == pin) {

                    // (leave empty) Show previous login
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

                    // (leave empty) Update new login time
                    String loginQuery =
                            "update accounts set last_login=now() where id=?";

                    PreparedStatement psLogin =
                            con.prepareStatement(loginQuery);

                    psLogin.setInt(1, id);

                    psLogin.executeUpdate();

                    return true;
                }

                // ❌ Wrong PIN
                else {

                    attempts++;

                    if (attempts >= 3) {

                        String lockQuery =
                                "update accounts set account_locked=true where id=?";

                        PreparedStatement ps3 =
                                con.prepareStatement(lockQuery);

                        ps3.setInt(1, id);

                        ps3.executeUpdate();

                        System.out.println(
                                "❌ Account Locked after 3 wrong attempts");

                    }

                    else {

                        String updateQuery =
                                "update accounts set failed_attempts=? where id=?";

                        PreparedStatement ps4 =
                                con.prepareStatement(updateQuery);

                        ps4.setInt(1, attempts);
                        ps4.setInt(2, id);

                        ps4.executeUpdate();

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
}