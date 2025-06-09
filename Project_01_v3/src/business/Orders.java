package business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Customer;
import models.Order;
import models.SetMenu;

public class Orders extends ArrayList<Order> implements Workable<Order, String> {

    private boolean saved;
    private String pathFile;

    public Orders(String pathFile) {
        this.pathFile = pathFile;
        this.readFromFile();
    }

    @Override
    public void addNew(Order t) {
        if (!isDupplicated(t)) {
            this.add(t);
            this.saved = false;
        }
    }

    @Override
    public void showAll() {
    }

    @Override
    public Order searchById(String id) {
        for (Order order : this) {
            if (order.getOrderId().equals(id)) {
                return order;
            }
        }
        return null;
    }

    public boolean isDupplicated(Order o) {
        return this.contains(o);
    }

    public void saveToFile() {
        // -- 0. Neu da luu roi thi khong luu nua
        if (this.saved) {
            return;
        }

        FileOutputStream fos = null;
        try {
            //-- 1. Tao File object
            File f = new File(this.pathFile);
            if (!f.exists()) {
                f.createNewFile();
            }

            //-- 2. Tao FileutputStream
            fos = new FileOutputStream(f);

            //-- 3. Tao oos
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            //-- 4. Ghi file
            for (Order o : this) {
                oos.writeObject(o);
            }
            //-- 5. Dong cac objcet
            oos.close();
            //--6. Thay doi trang thai cua saved
            this.saved = true;
        } catch (Exception e) {
            Logger.getLogger(Customers.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                Logger.getLogger(Customers.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    
    public void readFromFile(){
        FileInputStream fis = null;
        try {
            // B1 - Tao doi tuong file de anh xa len tap tin
            File f = new File(this.pathFile);
            // B2 - Kiem tra su ton tai cua file
            if (!f.exists()) {
                System.out.println("Cannot read data from "+this.pathFile+". Please check it.");
                return;
            } else {
                // B3 - Tao fis
                fis = new FileInputStream(f);
                // B4 - Tap ois
                ObjectInputStream ois = new ObjectInputStream(fis);
                // B5 - Doc tung doi tuong
                try {
                    while (true) {
                        Object o = ois.readObject();
                        Order order = (Order) o;
                        this.addNew(order);
                    }
                } catch (Exception e) {
                }
            }
        } catch (FileNotFoundException e1) {
            Logger.getLogger(SetMenus.class.getName()).log(Level.SEVERE, null, e1);
        } catch (IOException e2) {
            Logger.getLogger(SetMenus.class.getName()).log(Level.SEVERE, null, e2);
        } catch (Exception e3) {
            Logger.getLogger(SetMenus.class.getName()).log(Level.SEVERE, null, e3);
        }finally {
            try {
                fis.close();
            } catch (Exception ex) {
            }
        }
    }

    private boolean validateSetMenuCode(String code, SetMenus setMenus) {
        if (code == null || code.trim().isEmpty() || code.length() != 5) {
            return false;
        }
        return setMenus.searchById(code) != null;
    }

    private boolean validateNumberOfTables(int tables) {
        return tables > 0;
    }

    private boolean validateEventDate(Date eventDate) {
        if (eventDate == null) {
            return false;
        }
        Date currentDate = new Date();
        return eventDate.after(currentDate);
    }

    public boolean updateOrder(String orderId, String newSetMenuCode, Integer newNumberOfTables, 
                             Date newEventDate, SetMenus setMenus) {
        // Find the order first
        Order orderToUpdate = null;
        for (Order order : this) {
            if (order.getOrderId().equals(orderId)) {
                orderToUpdate = order;
                break;
            }
        }

        if (orderToUpdate == null) {
            return false;
        }

        // Validate event date first since we can't update past orders
        if (newEventDate != null && !validateEventDate(newEventDate)) {
            return false;
        }

        // Create a copy of the order for validation
        Order tempOrder = new Order(
            orderToUpdate.getOrderId(),
            orderToUpdate.getCustomerCode(),
            newSetMenuCode != null ? newSetMenuCode : orderToUpdate.getCodeOfSetMenu(),
            newNumberOfTables != null ? newNumberOfTables : orderToUpdate.getNumberOfTables(),
            newEventDate != null ? newEventDate : orderToUpdate.getEventDate()
        );

        // Validate the new values
        if (newSetMenuCode != null && !validateSetMenuCode(newSetMenuCode, setMenus)) {
            return false;
        }

        if (newNumberOfTables != null && !validateNumberOfTables(newNumberOfTables)) {
            return false;
        }

        // If all validations pass, update the order
        if (newSetMenuCode != null) {
            orderToUpdate.setCodeOfSetMenu(newSetMenuCode);
        }
        if (newNumberOfTables != null) {
            orderToUpdate.setNumberOfTables(newNumberOfTables);
        }
        if (newEventDate != null) {
            orderToUpdate.setEventDate(newEventDate);
        }

        this.saved = false;
        return true;
    }

    @Override
    public void update(Order t) {
     for (int i = 0; i < this.size(); i++) {
            Order currentOrder = this.get(i);
            // So sánh orderId để tìm đơn hàng cần cập nhật
            if (currentOrder.getOrderId().equals(t.getOrderId())) {
                //currentOrder.setCustomerCode(t.getCustomerCode());
                currentOrder.setCodeOfSetMenu(t.getCodeOfSetMenu());
                currentOrder.setNumberOfTables(t.getNumberOfTables());
                currentOrder.setEventDate(t.getEventDate());
                return;
            }
        }
        this.saved = false;
    }
}