package dispatcher;

import business.Customers;
import business.Orders;
import business.SetMenus;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import models.Customer;
import tools.Inputter;
import models.Order;
import models.SetMenu;
import java.text.SimpleDateFormat;

public class Main {

    public static void main(String[] args) {
        Inputter inputter = new Inputter();
        Customers customers = new Customers("customers.dat");
        Orders orders = new Orders("feast_order_service.dat");
        SetMenus setmenus = new SetMenus("C:\\Users\\vanad\\Downloads\\Ky 3 FPT\\LAB211\\Project 01\\FeastMenu.csv");

        Scanner scanner = new Scanner(System.in);

        int testCase = 10;
        do {
            System.out.println("\n----------MAIN MENU------------");
            System.out.println("1. Register customers");
            System.out.println("2. Update customer information");
            System.out.println("3. Seach for customer information by name");
            System.out.println("4. Display feast menu");
            System.out.println("5. Place a feast order");
            System.out.println("6. Update a feast order");
            System.out.println("7. Save data to file");
            System.out.println("8. Display all customers");
            System.out.println("9. Exit");
            System.out.print("Enter Test Case No. : ");
            testCase = Integer.parseInt(scanner.nextLine());
            switch (testCase) {
                case 1:
                    int option = 0;
                    do {
                        customers.addNew(inputter.inputCustomer(false));
                        System.out.println("1. Continue entering new customers");
                        System.out.println("2. Return to the main menu");
                        System.out.println("Enter your option: ");
                        option = Integer.parseInt(scanner.nextLine());
                    } while (option != 2);
                    break;
                case 2:
                    option = 0;
                    do {
                        System.out.print("Enter customer code: ");
                        String customerCode = scanner.nextLine();
                        Customer c = customers.searchById(customerCode);
                        if (c == null) {
                            System.out.println("This customer does not exist.");
                        } else {
                            Customer customer = inputter.inputCustomer(true);
                            customer.setCustomerCode(customerCode);
                            customers.update(customer);
                        }
                        System.out.println("1. Continue updating customer");
                        System.out.println("2. Return to the main menu");
                        System.out.println("Enter your option: ");
                    } while (option != 2);
                    break;
                case 3:
                    option = 0;
                    do {
                        System.out.print("Enter customer name: ");
                        String name = scanner.nextLine();
                        HashSet<Customer> cs = customers.filterByName(name);
                        if (cs.isEmpty()) {
                            System.out.println("No one matches the search criteria.");
                        } else {
                            customers.showAll(cs);
                        }
                        System.out.println("1. Continue search");
                        System.out.println("2. Return to the main menu");
                        System.out.println("Enter your option: ");
                    } while (option != 2);
                    break;
                case 4: {
                    try {
                        setmenus.readFromFile();
                    } catch (Exception e) {
                    }

                    setmenus.showAll();
                }
                break;
                case 5:
                    Order order = inputter.inputOrder(customers, setmenus);
                    if (orders.contains(order)) {
                        System.out.println("Dupplicate data!");
                    } else {
                        orders.addNew(order);
                        order.display(customers, setmenus);
                    }

                
                    break;
              case 6:
                    handleUpdateOrder(orders, setmenus, customers); 
                    break;
                case 7:
                    customers.saveToFile();
                    orders.saveToFile();
                    System.out.println("The data is successfully saved!");
                    break;
                case 8:
                    Customers customers_temp = new Customers("customers.dat");
                    Orders orders_temp = new Orders("feast_order_service.dat");
                    if (customers_temp.size() > 0) {
                        customers_temp.showAll();
                    } else if (orders_temp.size() > 0) {
                        orders_temp.showAll();
                    } else {
                        System.out.println("No data in the system!");
                    }
                    break;
                case 9:
                  customers.saveToFile();
                  orders.saveToFile();
                  System.out.println("Data auto-saved. Goodbye!");
                  System.exit(0);
                    break;
                default:
                    throw new AssertionError();
            }
        } while (testCase != 10);
    }

    private static void handleUpdateOrder(Orders orders, SetMenus setMenus, Customers customers) {
        Scanner scanner = new Scanner(System.in);
        Inputter inputter = new Inputter();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        int option;
        do {
            System.out.print("Enter Order ID to update: ");
            String orderId = scanner.nextLine().trim();
            
            Order existingOrder = orders.searchById(orderId);
            if (existingOrder == null) {
                System.out.println("This Order does not exist.");
            } else {
                // Display current order information
                System.out.println("\nCurrent Order Information:");
                existingOrder.display(customers, setMenus);
                
                // Get new values (all optional)
                System.out.println("\nEnter new values (press Enter to keep current value):");
                
                // Get new set menu code
                System.out.print("Enter new set menu code (5 characters): ");
                String newSetMenuCode = scanner.nextLine().trim();
                if (newSetMenuCode.isEmpty()) {
                    newSetMenuCode = null;
                }
                
                // Get new number of tables
                System.out.print("Enter new number of tables: ");
                String tablesStr = scanner.nextLine().trim();
                Integer newNumberOfTables = null;
                if (!tablesStr.isEmpty()) {
                    try {
                        newNumberOfTables = Integer.parseInt(tablesStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format. Keeping current value.");
                    }
                }
                
                // Get new event date
                System.out.print("Enter new event date (dd/MM/yyyy): ");
                String dateStr = scanner.nextLine().trim();
                Date newEventDate = null;
                if (!dateStr.isEmpty()) {
                    try {
                        newEventDate = sdf.parse(dateStr);
                        // Validate that the date is in the future
                        if (!newEventDate.after(new Date())) {
                            System.out.println("Event date must be in the future. Keeping current value.");
                            newEventDate = null;
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid date format. Keeping current value.");
                    }
                }
                
                // Attempt to update the order
                if (orders.updateOrder(orderId, newSetMenuCode, newNumberOfTables, newEventDate, setMenus)) {
                    System.out.println("\nOrder updated successfully!");
                    System.out.println("\nUpdated Order Information:");
                    orders.searchById(orderId).display(customers, setMenus);
                } else {
                    System.out.println("\nFailed to update order. Please check your input values.");
                }
            }
            
            System.out.println("\n1. Continue updating another order");
            System.out.println("2. Return to main menu");
            System.out.print("Enter your option: ");
            option = Integer.parseInt(scanner.nextLine());
        } while (option != 2);
    }
}