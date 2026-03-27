package admin;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class AdminPanel {

    public static void main(String[] args) {


        Scanner sc = new Scanner(System.in);
        boolean isAdminLoggedIn = false;
        try (Connection con = DBConnection.getConnection()) {

            while (true) {

                System.out.println("\n====== Admin Panel ======");

                System.out.println("1 Admin Login");
                System.out.println("2 Unlock Account");
                System.out.println("3 View All Accounts");
                System.out.println("4 View User Transactions");
                System.out.println("5 Search Account by Name");
                System.out.println("6 Monthly Report");
                System.out.println("7 Dashboard Summary");
                System.out.println("8 Logout");
                System.out.println("9 Exit");

                System.out.print("Enter choice: ");
                int choice = sc.nextInt();

                // ================= ADMIN LOGIN =================
                if (choice == 1) {

                    if (isAdminLoggedIn) {

                        System.out.println("Admin already logged in ✅");
                        continue;
                    }

                    System.out.print("Enter Admin Password: ");
                    String pass = sc.next();

                    if (pass.equals("admin123")) {

                        isAdminLoggedIn = true;

                        System.out.println("Admin Login Successful ✅");

                    } else {

                        System.out.println("❌ Wrong Password");
                    }
                }

                // ================= VIEW ALL ACCOUNTS =================
                else if (choice == 2) {

                    if (!isAdminLoggedIn) {

                        System.out.println("❌ Please login first");
                        continue;
                    }

                    System.out.print("Enter Account ID: ");
                    int id = sc.nextInt();

                    String query =
                            "update accounts " +
                                    "set account_locked=false, failed_attempts=0 " +
                                    "where id=?";

                    PreparedStatement ps =
                            con.prepareStatement(query);

                    ps.setInt(1, id);

                    int rows = ps.executeUpdate();

                    if (rows > 0) {

                        System.out.println(
                                "✅ Account Unlocked Successfully");

                    } else {

                        System.out.println(
                                "❌ Account Not Found");
                    }
                }


                // ================= View All Accounts =================
                else if (choice == 3) {

                    if (!isAdminLoggedIn) {

                        System.out.println("❌ Please login first");
                        continue;
                    }

                    String query =
                            "select id,name,balance,account_locked from accounts";

                    PreparedStatement ps =
                            con.prepareStatement(query);

                    ResultSet rs =
                            ps.executeQuery();

                    System.out.println("\n------ ALL ACCOUNTS ------");

                    while (rs.next()) {

                        System.out.println(
                                rs.getInt("id") + " | "
                                        + rs.getString("name") + " | ₹"
                                        + rs.getDouble("balance") + " | "
                                        + rs.getBoolean("account_locked"));
                    }
                }


                // ================= View User Transactions =================
                else if (choice == 4) {

                    if (!isAdminLoggedIn) {

                        System.out.println("❌ Please login first");
                        continue;
                    }

                    System.out.print("Enter Account ID: ");
                    int accId = sc.nextInt();

                    String query =
                            "select type,amount,date " +
                                    "from transactions where account_id=?";

                    PreparedStatement ps =
                            con.prepareStatement(query);

                    ps.setInt(1, accId);

                    ResultSet rs =
                            ps.executeQuery();

                    System.out.println("\n------ USER TRANSACTIONS ------");

                    while (rs.next()) {

                        System.out.println(
                                rs.getString("type") + " | ₹"
                                        + rs.getDouble("amount") + " | "
                                        + rs.getTimestamp("date"));
                    }
                }


                // ================= Search Account by Name =================

                else if (choice == 5) {

                    if (!isAdminLoggedIn) {

                        System.out.println("❌ Please login first");
                        continue;
                    }

                    sc.nextLine(); // buffer clear

                    System.out.print("Enter Account Name: ");
                    String name = sc.nextLine();

                    String query =
                            "select id, name, balance " +
                                    "from accounts " +
                                    "where name like ?";

                    PreparedStatement ps =
                            con.prepareStatement(query);

                    ps.setString(1, "%" + name + "%");

                    ResultSet rs =
                            ps.executeQuery();

                    System.out.println(
                            "\n------ SEARCH RESULT ------");

                    boolean found = false;

                    while (rs.next()) {

                        found = true;

                        System.out.println(
                                "ID: " + rs.getInt("id")
                                        + " | Name: " + rs.getString("name")
                                        + " | Balance: ₹" + rs.getDouble("balance"));
                    }

                    if (!found) {

                        System.out.println(
                                "No Account Found ❌");
                    }
                }


                // ================================== Monthly Report ======================================
                else if (choice == 6) {

                    System.out.print("Enter Month (1-12): ");
                    int month = sc.nextInt();

                    System.out.print("Enter Year: ");
                    int year = sc.nextInt();

                    String depositQuery =
                            "select sum(amount) from transactions " +
                                    "where type='DEPOSIT' " +
                                    "and month(date)=? and year(date)=?";

                    PreparedStatement ps1 =
                            con.prepareStatement(depositQuery);

                    ps1.setInt(1, month);
                    ps1.setInt(2, year);

                    ResultSet rs1 =
                            ps1.executeQuery();

                    double totalDeposit = 0;

                    if (rs1.next())
                        totalDeposit = rs1.getDouble(1);


                    String withdrawQuery =
                            "select sum(amount) from transactions " +
                                    "where type='WITHDRAW' " +
                                    "and month(date)=? and year(date)=?";

                    PreparedStatement ps2 =
                            con.prepareStatement(withdrawQuery);

                    ps2.setInt(1, month);
                    ps2.setInt(2, year);

                    ResultSet rs2 =
                            ps2.executeQuery();

                    double totalWithdraw = 0;

                    if (rs2.next())
                        totalWithdraw = rs2.getDouble(1);


                    String transferQuery =
                            "select sum(amount) from transactions " +
                                    "where type like 'TRANSFER%' " +
                                    "and month(date)=? and year(date)=?";

                    PreparedStatement ps3 =
                            con.prepareStatement(transferQuery);

                    ps3.setInt(1, month);
                    ps3.setInt(2, year);

                    ResultSet rs3 =
                            ps3.executeQuery();

                    double totalTransfer = 0;

                    if (rs3.next())
                        totalTransfer = rs3.getDouble(1);


                    System.out.println("\n------ MONTHLY REPORT ------");

                    System.out.println(
                            "Total Deposits   : ₹" + totalDeposit);

                    System.out.println(
                            "Total Withdrawals: ₹" + totalWithdraw);

                    System.out.println(
                            "Total Transfers  : ₹" + totalTransfer);
                }

                // ================= Dashboard Summary  =================
                else if (choice == 7) {

                    if (!isAdminLoggedIn) {

                        System.out.println("❌ Please login first");
                        continue;
                    }

                    System.out.println(
                            "\n------ BANK SUMMARY ------");

                    // 🔥 Total Accounts
                    String accQuery =
                            "select count(*) as total_accounts from accounts";

                    PreparedStatement ps1 =
                            con.prepareStatement(accQuery);

                    ResultSet rs1 =
                            ps1.executeQuery();

                    if (rs1.next()) {

                        System.out.println(
                                "Total Accounts: "
                                        + rs1.getInt("total_accounts"));
                    }

                    // 🔥 Total Transactions
                    String transQuery =
                            "select count(*) as total_transactions from transactions";

                    PreparedStatement ps2 =
                            con.prepareStatement(transQuery);

                    ResultSet rs2 =
                            ps2.executeQuery();

                    if (rs2.next()) {

                        System.out.println(
                                "Total Transactions: "
                                        + rs2.getInt("total_transactions"));
                    }

                    // 🔥 Locked Accounts
                    String lockQuery =
                            "select count(*) as locked_accounts " +
                                    "from accounts where account_locked=true";

                    PreparedStatement ps3 =
                            con.prepareStatement(lockQuery);

                    ResultSet rs3 =
                            ps3.executeQuery();

                    if (rs3.next()) {

                        System.out.println(
                                "Locked Accounts: "
                                        + rs3.getInt("locked_accounts"));
                    }

                    // 🔥 Total Bank Balance
                    String balanceQuery =
                            "select sum(balance) as total_balance from accounts";

                    PreparedStatement ps4 =
                            con.prepareStatement(balanceQuery);

                    ResultSet rs4 =
                            ps4.executeQuery();

                    if (rs4.next()) {

                        System.out.println(
                                "Total Bank Balance: ₹"
                                        + rs4.getDouble("total_balance"));
                    }
                }



                // ================= Admin Logout =================
                else if (choice == 8) {

                    isAdminLoggedIn = false;

                    System.out.println("Admin Logged Out ✅");
                }


                // ================= EXIT =================
                else if (choice == 9) {

                    System.out.println("Exiting Admin Panel...");
                    break;
                }

                else {

                    System.out.println("Invalid Choice");
                }
            }

        }

        catch (Exception e) {

            e.printStackTrace();
        }
    }
}