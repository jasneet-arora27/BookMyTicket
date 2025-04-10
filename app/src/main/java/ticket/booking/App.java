package ticket.booking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.services.TrainService;
import ticket.booking.services.UserBookingService;
import ticket.booking.util.UserServiceUtil;

public class App {

    public static void main(String[] args) {
        try {
            TrainService trainService = new TrainService();
            System.out.println("DEBUG: Available trains and their stations:");
            for (Train t : trainService.searchTrains("", "")) {
                System.out.println("Train ID: " + t.getTrainId());
                System.out.println("Stations: " + t.getStations());
                System.out.println("----------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Welcome to BookMyTicket!");
        Scanner sc = new Scanner(System.in);
        int option = 0;
        UserBookingService userBookingService;
        Train trainSelectedForBooking = null;
        User loggedInUser = null;

        try {
            userBookingService = new UserBookingService(null);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("OOPS! Something went wrong!");
            sc.close();
            return;
        }

        while (option != 7) {
            System.out.println("Choose option");
            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit the App");

            option = sc.nextInt();
            switch (option) {
                case 1:
                    System.out.println("Enter the username to signup");
                    String nameToSignUp = sc.next();
                    System.out.println("Enter the password to signup");
                    String passwordToSignUp = sc.next();
                    User userToSignup = new User(nameToSignUp, passwordToSignUp,
                            UserServiceUtil.hashPassword(passwordToSignUp), new ArrayList<>(),
                            UUID.randomUUID().toString());
                    userBookingService.signUp(userToSignup);
                    break;

                case 2:
                    System.out.println("Enter the username to Login");
                    String nameToLogin = sc.next();
                    System.out.println("Enter the password to login");
                    String passwordToLogin = sc.next();
                    loggedInUser = new User(nameToLogin, passwordToLogin,
                            UserServiceUtil.hashPassword(passwordToLogin), new ArrayList<>(),
                            UUID.randomUUID().toString());
                    try {
                        userBookingService = new UserBookingService(loggedInUser);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        System.out.println("Login failed.");
                        return;
                    }
                    break;

                case 3:
                    if (loggedInUser == null) {
                        System.out.println("Please login first.");
                        break;
                    }
                    System.out.println("Fetching your bookings...");
                    userBookingService.fetchBookings();
                    break;

                case 4:
                    if (loggedInUser == null) {
                        System.out.println("Please login first.");
                        break;
                    }
                    System.out.println("Type your source station");
                    String source = sc.next();
                    System.out.println("Type your destination station");
                    String dest = sc.next();
                    List<Train> trains = userBookingService.getTrains(source, dest);

                    if (trains.isEmpty()) {
                        System.out.println("No trains available for the given route.");
                        break;
                    }

                    int index = 1;
                    for (Train t : trains) {
                        System.out.println(index + " Train id : " + t.getTrainId());
                        for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
                            System.out.println("station " + entry.getKey() + " time: " + entry.getValue());
                        }
                        index++;
                    }

                    System.out.println("Select a train by typing 1,2,3...");
                    int trainChoice = sc.nextInt();

                    if (trainChoice < 1 || trainChoice > trains.size()) {
                        System.out.println("Invalid selection.");
                        break;
                    }

                    trainSelectedForBooking = trains.get(trainChoice - 1);
                    break;

                case 5:
                    if (loggedInUser == null) {
                        System.out.println("Please login first.");
                        break;
                    }
                    if (trainSelectedForBooking == null) {
                        System.out.println("Please search and select a train first (Option 4).");
                        break;
                    }
                    System.out.println("Select a seat out of these seats");
                    List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);
                    for (List<Integer> row : seats) {
                        for (Integer val : row) {
                            System.out.print(val + " ");
                        }
                        System.out.println();
                    }
                    System.out.println("Select the seat by typing the row and column");
                    System.out.println("Enter the row");
                    int row = sc.nextInt();
                    System.out.println("Enter the column");
                    int col = sc.nextInt();
                    System.out.println("Booking your seat....");
                    Boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row, col);
                    if (booked.equals(Boolean.TRUE)) {
                        System.out.println("Booked! Enjoy your journey");
                    } else {
                        System.out.println("Can't book this seat");
                    }
                    break;

                case 6:
                    if (userBookingService == null || loggedInUser == null) {
                        System.out.println("Please login first.");
                        break;
                    }

                    List<Ticket> currentBookings = loggedInUser.getTicketsBooked();
                    if (currentBookings == null || currentBookings.isEmpty()) {
                        System.out.println("You have no bookings to cancel.");
                        break;
                    }

                    System.out.println("Your current bookings:");
                    for (int i = 0; i < currentBookings.size(); i++) {
                        System.out.println((i + 1) + ". " + currentBookings.get(i).getTicketInfo());
                    }

                    System.out.println("Enter the number of the booking to cancel:");
                    int cancelChoice = sc.nextInt();
                    if (cancelChoice < 1 || cancelChoice > currentBookings.size()) {
                        System.out.println("Invalid booking selection.");
                        break;
                    }

                    Ticket ticketToCancel = currentBookings.get(cancelChoice - 1);
                    boolean cancelled = userBookingService.cancelBooking(ticketToCancel.getTicketId());
                    if (cancelled) {
                        System.out.println("Booking cancelled successfully.");
                        loggedInUser.getTicketsBooked().remove(ticketToCancel);
                    } else {
                        System.out.println("Failed to cancel booking.");
                    }
                    break;

                case 7:
                    System.out.println("Thank you for using BookMyTicket!");
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }

        sc.close();
    }
}
