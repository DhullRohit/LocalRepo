


import java.util.*;
import java.io.*;
import java.time.LocalDate;
public class HotelManagement {

static class Room{
    int number;
    String type;
    double price;
    int occupied;

    Room(int n,String t,double p){
        number=n;
        type=t;
        price=p;
        occupied=0;
    }
}

static class Bill{
    String userId;

    ArrayList<Integer> rooms=new ArrayList<>();
    ArrayList<String> foodItems=new ArrayList<>();
    ArrayList<String> services=new ArrayList<>();

    double roomCost,foodCost,serviceCost,tax;

    String checkInDate;
    String checkOutDate;

    Bill(String id){
        userId=id;
    }
}

static class User{
    String u,p,a,m;
    String sessionId;

    User(String u,String p,String a,String m,String sid){
        this.u=u; this.p=p; this.a=a; this.m=m;
        this.sessionId=sid;
    }
}

    static ArrayList<Room> rooms=new ArrayList<>();
    static ArrayList<User> users=new ArrayList<>();
    static HashMap<String,Bill> bills=new HashMap<>();
    static ArrayList<String> bookingRecords=new ArrayList<>();

    static Scanner sc=new Scanner(System.in);
    static int idCounter=1000;

    static int getInt(){
        while(true){
            try{
                return sc.nextInt();
            }catch(Exception e){
                System.out.println("Invalid input!");
                sc.next();
            }
        }
    }

    static void init(){
        for(int i=101;i<=105;i++)
            rooms.add(new Room(i,"Non-Deluxe",1000));

        for(int i=201;i<=205;i++)
            rooms.add(new Room(i,"Deluxe",2000));
    }

    static boolean validAadhar(String a){ return a.matches("\\d{12}"); }
    static boolean validMobile(String m){ return m.matches("\\d{10}"); }

    static boolean isAvailable(int roomNo, LocalDate newIn, LocalDate newOut){
        for(String record:bookingRecords){
            String[] parts=record.split("\\|");

            int rNo=Integer.parseInt(parts[0]);
            LocalDate oldIn=LocalDate.parse(parts[1]);
            LocalDate oldOut=LocalDate.parse(parts[2]);

            if(rNo==roomNo){
                if(!(newOut.isBefore(oldIn) || newIn.isAfter(oldOut))){
                    return false;
                }
            }
        }
        return true;
    }

    static double getDynamicPrice(Room room) {
        int total = 0, occupied = 0;

        for (Room r : rooms) {
            if (r.type.equals(room.type)) {
                total++;
                if (r.occupied == 1) occupied++;
            }
        }

        double price = room.price;

        if ((double) occupied / total >= 0.8) {
            System.out.println("⚠ High Demand! Price increased by 20%");
            price *= 1.2;
        }

        return price;
    }

    static void signup(){
        System.out.print("Username: ");
        String u=sc.next();

        System.out.print("Password: ");
        String p=sc.next();

        String a;
        while(true){
            System.out.print("Aadhar: ");
            a=sc.next();
            if(validAadhar(a)) break;
        }

        String m;
        while(true){
            System.out.print("Mobile: ");
            m=sc.next();
            if(validMobile(m)) break;
        }

        String sid="U"+idCounter++;
        users.add(new User(u,p,a,m,sid));

        System.out.println("Signup successful");
    }

    static String login(){
        System.out.print("Username: ");
        String u=sc.next();

        System.out.print("Password: ");
        String p=sc.next();

        for(User x:users){
            if(x.u.equals(u)&&x.p.equals(p)){
                System.out.println("Login successful");
                System.out.println("USER ID: "+x.sessionId);
                return x.sessionId;
            }
        }
        System.out.println("Invalid credentials");
        return null;
    }

    static void showRooms(){
        System.out.println("\n=========== ROOMS ===========");

        for(Room r:rooms){
            System.out.println("----------------------------");
            System.out.println("Room No : "+r.number);
            System.out.println("Type    : "+r.type);
            System.out.println("Price   : "+r.price);

            boolean found=false;

            for(String record:bookingRecords){
                String[] parts=record.split("\\|");

                int rNo=Integer.parseInt(parts[0]);
                String in=parts[1];
                String out=parts[2];

                if(rNo==r.number){
                    if(!found){
                        System.out.println("Status  : Occupied");
                        System.out.println("Bookings:");
                        System.out.println("  From        To");
                        System.out.println("  ----------  ----------");
                        found=true;
                    }
                    System.out.println("  "+in+"   "+out);
                }
            }

            if(!found){
                System.out.println("Status  : Available");
            }
        }
    }
      static void showCalendar() {

    LocalDate today = LocalDate.now();

    int year = today.getYear();
    int month = today.getMonthValue();

    LocalDate firstDay = LocalDate.of(year, month, 1);

    int daysInMonth = firstDay.lengthOfMonth();

    // Java: MON=1 ... SUN=7 → convert to SUN=0
    int startDay = firstDay.getDayOfWeek().getValue() % 7;

    System.out.println("\nMonth: " + today.getMonth() + " " + year);
    System.out.println("S   M   T   W   T   F   S");

    // Print initial spaces
    for (int i = 0; i < startDay; i++) {
        System.out.print("    ");
    }

    for (int day = 1; day <= daysInMonth; day++) {

        if (day == today.getDayOfMonth()) {
            System.out.print("**  "); // replaces date
        } else {
            System.out.printf("%-4d", day);
        }

        // Move to next line after Saturday
        if ((day + startDay) % 7 == 0) {
            System.out.println();
        }
    }

    System.out.println("\n");
}
    static void book(String uid){

        System.out.print("How many rooms (1-5): ");
        int count=getInt();

        if(count<1||count>5){
            System.out.println("Invalid count");
            return;
        }

        Bill b=bills.getOrDefault(uid,new Bill(uid));

        LocalDate inDate;
        showCalendar();
        while(true){
            try{
                System.out.print("Enter Check-in date (YYYY-MM-DD): ");
                String checkIn=sc.next();
                inDate=LocalDate.parse(checkIn);
                break;
            }catch(Exception e){
                System.out.println("❌ Invalid date format!");
            }
        }

        System.out.print("Days: ");
        int days=getInt();

        LocalDate outDate=inDate.plusDays(days);

        b.checkInDate=inDate.toString();
        b.checkOutDate=outDate.toString();

        double total=0;

        for(int i=0;i<count;i++){

            Room room=null;

            while(true){
                System.out.print("Enter room no: ");
                int rn=getInt();

                for(Room r:rooms){
                    if(r.number==rn && isAvailable(rn,inDate,outDate)){
                        room=r;
                        break;
                    }
                }

                if(room!=null) break;
                System.out.println("Room not available!");
            }

            int ac;
            while(true){
                System.out.print("AC (1/0): ");
                ac=getInt();
                if(ac==0||ac==1) break;
            }

            double dynamicPrice=getDynamicPrice(room);
            double cost=dynamicPrice*days + (ac==1?500*days:0);
            total+=cost;

            room.occupied=1;
            b.rooms.add(room.number);

            bookingRecords.add(room.number+"|"+inDate+"|"+outDate);
        }

        b.roomCost+=total;
        bills.put(uid,b);

        System.out.println("Booking successful under USER ID: "+uid);
    }

    // static void food(String uid){
    //     Bill b=bills.get(uid);
    //     if(b==null){ b=new Bill(uid); bills.put(uid,b); }

    //     while(true){
    //         System.out.println("----------------------------");
    //         System.out.println("\n1.Pizza 300\n2.Burger 150\n3.Coffee 80\n4.Done");
    //         System.out.println("----------------------------");
    //         int ch=getInt();

    //         if(ch==4) break;

    //         System.out.print("Qty: ");
    //         int q=getInt();

    //         if(ch==1){ b.foodCost+=300*q; b.foodItems.add("Pizza x"+q); }
    //         else if(ch==2){ b.foodCost+=150*q; b.foodItems.add("Burger x"+q); }
    //         else if(ch==3){ b.foodCost+=80*q; b.foodItems.add("Coffee x"+q); }
    //     }
    // }
    static void food(String uid){
    Bill b=bills.get(uid);
    if(b==null){ b=new Bill(uid); bills.put(uid,b); }

    while(true){

        System.out.println("------------------------------------------------------------");
        System.out.printf("%-3s %-25s %-8s\n", "No", "Item", "Price");
        System.out.println("------------------------------------------------------------");

        System.out.printf("%-3d %-25s ₹%-8d\n",1,"Grilled Norwegian Salmon",980);
        System.out.printf("%-3d %-25s ₹%-8d\n",2,"Herb Roasted Chicken",720);
        System.out.printf("%-3d %-25s ₹%-8d\n",3,"Avocado & Kale Salad",480);
        System.out.printf("%-3d %-25s ₹%-8d\n",4,"Vegetable Au Gratin",520);
        System.out.printf("%-3d %-25s ₹%-8d\n",5,"Dark Chocolate Mousse",380);

        System.out.printf("%-3d %-25s ₹%-8d\n",6,"Margherita Pizza",350);
        System.out.printf("%-3d %-25s ₹%-8d\n",7,"Farmhouse Pizza",420);
        System.out.printf("%-3d %-25s ₹%-8d\n",8,"Veg Burger",150);
        System.out.printf("%-3d %-25s ₹%-8d\n",9,"Cheese Burger",200);
        System.out.printf("%-3d %-25s ₹%-8d\n",10,"Pasta Alfredo",300);

        System.out.printf("%-3d %-25s ₹%-8d\n",11,"Pasta Arrabiata",280);
        System.out.printf("%-3d %-25s ₹%-8d\n",12,"Paneer Tikka",260);
        System.out.printf("%-3d %-25s ₹%-8d\n",13,"Spring Rolls",180);
        System.out.printf("%-3d %-25s ₹%-8d\n",14,"French Fries",120);
        System.out.printf("%-3d %-25s ₹%-8d\n",15,"Garlic Bread",140);

        System.out.printf("%-3d %-25s ₹%-8d\n",16,"Masala Dosa",100);
        System.out.printf("%-3d %-25s ₹%-8d\n",17,"Idli Sambhar",80);
        System.out.printf("%-3d %-25s ₹%-8d\n",18,"Cold Coffee",120);
        System.out.printf("%-3d %-25s ₹%-8d\n",19,"Tea",50);
        System.out.printf("%-3d %-25s ₹%-8d\n",20,"Ice Cream",90);

        System.out.println("------------------------------------------------------------");
        System.out.println("21. Done");
        System.out.println("------------------------------------------------------------");

        int ch=getInt();

        if(ch==21) break;

        System.out.print("Qty: ");
        int q=getInt();

        switch(ch){
            case 1: b.foodCost+=980*q; b.foodItems.add("Grilled Salmon x"+q); break;
            case 2: b.foodCost+=720*q; b.foodItems.add("Roasted Chicken x"+q); break;
            case 3: b.foodCost+=480*q; b.foodItems.add("Avocado Salad x"+q); break;
            case 4: b.foodCost+=520*q; b.foodItems.add("Veg Au Gratin x"+q); break;
            case 5: b.foodCost+=380*q; b.foodItems.add("Chocolate Mousse x"+q); break;

            case 6: b.foodCost+=350*q; b.foodItems.add("Margherita Pizza x"+q); break;
            case 7: b.foodCost+=420*q; b.foodItems.add("Farmhouse Pizza x"+q); break;
            case 8: b.foodCost+=150*q; b.foodItems.add("Veg Burger x"+q); break;
            case 9: b.foodCost+=200*q; b.foodItems.add("Cheese Burger x"+q); break;
            case 10: b.foodCost+=300*q; b.foodItems.add("Pasta Alfredo x"+q); break;

            case 11: b.foodCost+=280*q; b.foodItems.add("Pasta Arrabiata x"+q); break;
            case 12: b.foodCost+=260*q; b.foodItems.add("Paneer Tikka x"+q); break;
            case 13: b.foodCost+=180*q; b.foodItems.add("Spring Rolls x"+q); break;
            case 14: b.foodCost+=120*q; b.foodItems.add("French Fries x"+q); break;
            case 15: b.foodCost+=140*q; b.foodItems.add("Garlic Bread x"+q); break;

            case 16: b.foodCost+=100*q; b.foodItems.add("Masala Dosa x"+q); break;
            case 17: b.foodCost+=80*q; b.foodItems.add("Idli Sambhar x"+q); break;
            case 18: b.foodCost+=120*q; b.foodItems.add("Cold Coffee x"+q); break;
            case 19: b.foodCost+=50*q; b.foodItems.add("Tea x"+q); break;
            case 20: b.foodCost+=90*q; b.foodItems.add("Ice Cream x"+q); break;

            default: System.out.println("Invalid choice");
        }
    }
}

    static void service(String uid){
        Bill b=bills.get(uid);
        if(b==null){ b=new Bill(uid); bills.put(uid,b); }

        while(true){
            System.out.println("\n1.Cleaning 200\n2.Electricity 500\n3.Others 300\n4.Done");
            int ch=getInt();

            if(ch==4) break;

            if(ch==1){ b.serviceCost+=200; b.services.add("Cleaning"); }
            else if(ch==2){ b.serviceCost+=500; b.services.add("Electricity"); }
            else if(ch==3){ b.serviceCost+=300; b.services.add("Others"); }
        }
    }

    static void saveInvoiceTxt(String uid, Bill b){
        try{
            FileWriter fw=new FileWriter("invoice_"+uid+".txt");

            double roomTax=0.08*b.roomCost;
            double foodTax=0.05*b.foodCost;
            double serviceTax=0.12*b.serviceCost;

            double totalTax=roomTax+foodTax+serviceTax;
            double grandTotal=b.roomCost+b.foodCost+b.serviceCost+totalTax;

            fw.write("=========== INVOICE ===========\n");
            fw.write("USER ID      : "+uid+"\n\n");

            fw.write("--- STAY DETAILS ---\n");
            fw.write("Check-in     : "+b.checkInDate+"\n");
            fw.write("Check-out    : "+b.checkOutDate+"\n\n");

            fw.write("--- ROOMS ---\n");
            fw.write("Rooms        : "+b.rooms+"\n");
            fw.write("Room Cost    : "+b.roomCost+"\n");
            fw.write("Room Tax(8%) : "+roomTax+"\n\n");

            fw.write("--- FOOD ---\n");
            fw.write("Items        : "+b.foodItems+"\n");
            fw.write("Food Cost    : "+b.foodCost+"\n");
            fw.write("Food Tax(5%) : "+foodTax+"\n\n");

            fw.write("--- SERVICES ---\n");
            fw.write("Services     : "+b.services+"\n");
            fw.write("Service Cost : "+b.serviceCost+"\n");
            fw.write("Service Tax(12%) : "+serviceTax+"\n\n");

            fw.write("------------------------------\n");
            fw.write("TOTAL TAX    : "+totalTax+"\n");
            fw.write("GRAND TOTAL  : "+grandTotal+"\n");
            fw.write("==============================\n");

            fw.close();
            System.out.println("Invoice saved as TXT file");

        }catch(Exception e){}
    }

    static void invoice(String uid){
        Bill b=bills.get(uid);
        if(b==null){
            System.out.println("No booking found");
            return;
        }

        double roomTax=0.08*b.roomCost;
        double foodTax=0.05*b.foodCost;
        double serviceTax=0.12*b.serviceCost;

        double totalTax = roomTax + foodTax + serviceTax;
        double grandTotal = b.roomCost + b.foodCost + b.serviceCost + totalTax;

        System.out.println("\n=========== INVOICE ===========");
        System.out.println("USER ID      : "+uid);

        System.out.println("\n--- STAY DETAILS ---");
        System.out.println("Check-in     : "+b.checkInDate);
        System.out.println("Check-out    : "+b.checkOutDate);

        System.out.println("\n--- ROOMS ---");
        System.out.println("Rooms        : "+b.rooms);
        System.out.println("Room Cost    : "+b.roomCost);
        System.out.println("Room Tax(8%) : "+roomTax);

        System.out.println("\n--- FOOD ---");
        System.out.println("Items        : "+b.foodItems);
        System.out.println("Food Cost    : "+b.foodCost);
        System.out.println("Food Tax(5%) : "+foodTax);

        System.out.println("\n--- SERVICES ---");
        System.out.println("Services     : "+b.services);
        System.out.println("Service Cost : "+b.serviceCost);
        System.out.println("Service Tax(12%) : "+serviceTax);

        System.out.println("\n------------------------------");
        System.out.println("TOTAL TAX    : "+totalTax);
        System.out.println("GRAND TOTAL  : "+grandTotal);
        System.out.println("==============================");

        System.out.println("\n1.Save TXT 2.Skip");
        if(getInt()==1) saveInvoiceTxt(uid,b);
    }

    static void checkout(String uid){
        Bill b=bills.get(uid);
        if(b==null) return;

        double roomTax=0.08*b.roomCost;
        double foodTax=0.05*b.foodCost;
        double serviceTax=0.12*b.serviceCost;

        double totalTax = roomTax + foodTax + serviceTax;
        double grandTotal = b.roomCost + b.foodCost + b.serviceCost + totalTax;

        System.out.print("Enter UPI ID: ");
        String upi = sc.next();

        String upiLink = "upi://pay?pa="+upi+
                         "&pn=HotelPayment"+
                         "&tn=Hotel_Bill"+
                         "&am="+String.format("%.2f",grandTotal)+
                         "&cu=INR&mode=02";

        System.out.println("\n💳 PAYMENT DETAILS");
        System.out.println("Amount to Pay: "+grandTotal);

        System.out.println("\nUPI ID: "+upi);

        System.out.println("\nPayment Link:");
        System.out.println(upiLink);

        System.out.println("\n👉 Open this link in UPI app");

        Iterator<String> it = bookingRecords.iterator();

        while(it.hasNext()){
            String record = it.next();
            String[] parts = record.split("\\|");

            int rNo = Integer.parseInt(parts[0]);
            String in = parts[1];
            String out = parts[2];

            if(b.rooms.contains(rNo) &&
               in.equals(b.checkInDate) &&
               out.equals(b.checkOutDate)){
                it.remove();
            }
        }

        for(int rn : b.rooms){
            boolean stillBooked = false;

            for(String record : bookingRecords){
                String[] parts = record.split("\\|");

                if(Integer.parseInt(parts[0]) == rn){
                    stillBooked = true;
                    break;
                }
            }

            if(!stillBooked){
                for(Room r:rooms){
                    if(r.number==rn){
                        r.occupied = 0;
                    }
                }
            }
        }

        bills.remove(uid);

        System.out.println("\n✅ checkout done successfully");
    }

    public static void main(String[] args){

        init();

        boolean logged=false;
        String uid=null;

        while(true){

            if(!logged){
            System.out.println("----------------------------");
                System.out.println("\n1.Signup\n2.Login");
            System.out.println("----------------------------");

                int c=getInt();

                if(c==1) signup();
                else if(c==2){
                    uid=login();
                    if(uid!=null) logged=true;
                }
                continue;
            }

            System.out.println("\nUSER ID: "+uid);
            System.out.println("----------------------------");
            System.out.println("1.Rooms\n2.Book\n3.Food\n4.Services\n5.Invoice\n6.Checkout\n7.Logout\n8.Exit");
            System.out.println("----------------------------");

            int ch=getInt();

            switch(ch){
                case 1: showRooms(); break;
                case 2: book(uid); break;
                case 3: food(uid); break;
                case 4: service(uid); break;
                case 5: invoice(uid); break;
                case 6: checkout(uid); break;
                case 7: logged=false; uid=null; break;
                case 8: return;
            }
        }
    }
}