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

        String query =
                "insert into accounts(name,balance,pin) values(?,?,?)";

        try (PreparedStatement ps =
                     con.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setDouble(2, balance);
            ps.setInt(3, pin);

            ps.executeUpdate();

            System.out.println(
                    "Account Created Successfully");

        }

        catch (Exception e) {

            System.out.println(
                    "Account Creation Failed");

            e.printStackTrace();
        }
    }

    // ================= LOGIN =================

    public static boolean login(Connection con,
                                int id,
                                int pin) {

        String query =
                "select pin, failed_attempts, account_locked, last_login " +
                        "from accounts where id=?";

        try (PreparedStatement ps =
                     con.prepareStatement(query)) {

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

                if (locked) {

                    System.out.println(
                            "Account Locked. Contact Bank.");

                    return false;
                }

                if (dbPin == pin) {

                    if (lastLogin != null) {

                        System.out.println(
                                "Last Login: "
                                        + lastLogin);
                    }

                    resetAttempts(con, id);

                    updateLoginTime(con, id);

                    return true;
                }

                else {

                    attempts++;

                    updateAttempts(con,
                            id,
                            attempts);

                    return false;
                }

            }

            else {

                System.out.println(
                        "Account Not Found");

                return false;
            }

        }

        catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    // ================= HELPER METHODS =================

    private static void resetAttempts(Connection con,
                                      int id) {

        String query =
                "update accounts set failed_attempts=0 where id=?";

        try (PreparedStatement ps =
                     con.prepareStatement(query)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        }

        catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static void updateLoginTime(Connection con,
                                        int id) {

        String query =
                "update accounts set last_login=now() where id=?";

        try (PreparedStatement ps =
                     con.prepareStatement(query)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        }

        catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static void updateAttempts(Connection con,
                                       int id,
                                       int attempts) {

        try {

            if (attempts >= 3) {

                String lockQuery =
                        "update accounts set account_locked=true where id=?";

                try (PreparedStatement ps =
                             con.prepareStatement(lockQuery)) {

                    ps.setInt(1, id);

                    ps.executeUpdate();
                }

                System.out.println(
                        "Account Locked after 3 wrong attempts");

            }

            else {

                String updateQuery =
                        "update accounts set failed_attempts=? where id=?";

                try (PreparedStatement ps =
                             con.prepareStatement(updateQuery)) {

                    ps.setInt(1, attempts);
                    ps.setInt(2, id);

                    ps.executeUpdate();
                }

                System.out.println(
                        "Wrong PIN (" + attempts + "/3)");
            }

        }

        catch (Exception e) {

            e.printStackTrace();
        }
    }

    // ================= CHANGE PIN =================

    public static void changePin(Connection con,
                                 int id,
                                 int newPin) {

        if (newPin < 1000 || newPin > 9999) {

            System.out.println(
                    "PIN must be 4 digits");

            return;
        }

        String query =
                "update accounts set pin=? where id=?";

        try (PreparedStatement ps =
                     con.prepareStatement(query)) {

            ps.setInt(1, newPin);
            ps.setInt(2, id);

            int rows =
                    ps.executeUpdate();

            if (rows > 0)

                System.out.println(
                        "PIN Updated Successfully");

            else

                System.out.println(
                        "Account Not Found");

        }

        catch (Exception e) {

            e.printStackTrace();
        }
    }
}