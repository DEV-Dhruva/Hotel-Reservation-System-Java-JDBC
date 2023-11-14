import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "root123";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection con = DriverManager.getConnection(url, username, password);
            while (true) {
                System.out.println();
                System.out.println("Welcome to Hotel Management System");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a Room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.print("Choose an Option: ");
                int choice = sc.nextInt();
                Statement stmt = con.createStatement();
                switch (choice) {
                    case 1:
                        reserveRoom(con, sc, stmt);
                        break;
                    case 2:
                        viewReservations(con, stmt);
                        break;
                    case 3:
                        getRoomNumber(con, sc, stmt);
                        break;
                    case 4:
                        updateReservation(con, sc, stmt);
                        break;
                    case 5:
                        deleteReservation(con, sc, stmt);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid Choice !!! Try Again.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void reserveRoom(Connection con, Scanner sc, Statement stmt) {
        try {
            System.out.print("Enter Guest Name: ");
            String guestName = sc.next();
            System.out.print("Enter Room Number: ");
            int roomNumber = sc.nextInt();
            System.out.print("Enter Contact Number: ");
            String contactNumber = sc.next();

            String query = "insert into reservations(guest_name, room_number, contact_number) values('" + guestName
                    + "'," + roomNumber + ",'" + contactNumber
                    + "');";

            int affectedRows = stmt.executeUpdate(query);

            if (affectedRows > 0) {
                System.out.println("Reservation Successfull");
            } else {
                System.out.println("Reservation Failed");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection con, Statement stmt) {
        String query = "select * from reservations;";

        try (ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Current Reservations:");
            System.out.println(
                    "+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println(
                    "| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println(
                    "+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (rs.next()) {
                int reservationId = rs.getInt("reservation_id");
                String guestName = rs.getString("guest_name");
                int roomNumber = rs.getInt("room_number");
                String contactNumber = rs.getString("contact_number");
                String reservationDate = rs.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println(
                    "+----------------+-----------------+---------------+----------------------+-------------------------+");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void getRoomNumber(Connection con, Scanner sc, Statement stmt) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = sc.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = sc.next();

            String query = "select room_number from reservations where reservation_id = " + reservationId
                    + " and guest_name = '" + guestName + "';";

            try (ResultSet rs = stmt.executeQuery(query)) {

                if (rs.next()) {
                    int roomNumber = rs.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection con, Scanner sc, Statement stmt) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = sc.nextInt();
            sc.nextLine();

            if (!reservationExists(con, reservationId, stmt)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = sc.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = sc.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = sc.next();

            String query = "update reservations set guest_name = '" + newGuestName + "', room_number = " + newRoomNumber
                    + ", contact_number = '" + newContactNumber + "' where reservation_id = " + reservationId + ";";

            int affectedRows = stmt.executeUpdate(query);

            if (affectedRows > 0) {
                System.out.println("Reservation updated successfully!");
            } else {
                System.out.println("Reservation update failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection con, Scanner sc, Statement stmt) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = sc.nextInt();

            if (!reservationExists(con, reservationId, stmt)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String query = "delete from reservations where reservation_id = " + reservationId + ";";

            int affectedRows = stmt.executeUpdate(query);

            if (affectedRows > 0) {
                System.out.println("Reservation deleted successfully!");
            } else {
                System.out.println("Reservation deletion failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection con, int reservationId, Statement stmt) {
        try {
            String query = "select reservation_id from reservations where reservation_id = " + reservationId + ";";

            ResultSet resultSet = stmt.executeQuery(query);

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while (i != 0) {
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
}
