// Using java Thread and Runnable
import java.util.* ;
import java.io.* ;
import java.util.concurrent.* ;

/* Stores the number of caps and different size of T shirts currently in the inventory. It also has semaphores for each item. */
class Inventory {
	Semaphore semS, semM, semL, semC, sem; 
	int countSTees, countMTees, countLTees, countCaps ;
	/* Constructor for class to initialise its local variables */
	Inventory(int s, int m, int l, int c, Semaphore semaS, Semaphore semaM, Semaphore semaL, Semaphore semaC, Semaphore sema) {
		countSTees = s ;
		countMTees = m ;
		countLTees = l ;
		countCaps = c ;
		semS = semaS;
		semM = semaM;
		semL = semaL;
		semC = semaC;
		sem = sema;
	}

	/* returns a copy of the inventory. It is synchronized. */
	synchronized Inventory getInventory() { return new Inventory(countSTees, countMTees, countLTees, countCaps, semS, semM, semL, semC, sem); }

	/* Displays contents of inventory after acquiring a semaphore */
	void dispInventory(int orderNo, int result, int cntSTees, int cntMTees, int cntLTees, int cntCaps) {
		try{ sem.acquire(); }
		catch(Exception e){ System.out.println("Exception has occured."); }
		String success ;
		if (result!=0)
			success = "failed" ;
		else
			success = "successful" ;
		System.out.println("") ;
		if(orderNo!=0)
			System.out.println("Order " + orderNo + " is " + success) ;
		System.out.println("Inventory --") ;
		System.out.println("S\tM\tL\tC") ;
		String data = String.format("%d\t%d\t%d\t%d", cntSTees, cntMTees, cntLTees, cntCaps) ;
		System.out.println(data) ;
		sem.release();
	}

 	/* Updates inventory when a small T shirt is ordred */
	void updateS(Inventory inv, int orderNo, char orderType, int quantity){
		int cntSTees = 0, cntMTees = 0, cntLTees = 0, cntCaps = 0;
		try{ semS.acquire();
			if(countSTees > inv.countSTees){
				 countSTees = inv.countSTees;
			}
			cntSTees = countSTees;
			cntMTees = countMTees;
			cntLTees = countLTees; 
			cntCaps = countCaps;
		}
		catch(Exception e){ System.out.println("Exception has occured."); }
		int result = 0 ;
		if(quantity > countSTees) result = 1 ;
		else countSTees -= quantity ;
		inv.countSTees = countSTees;
		this.dispInventory(orderNo, result, countSTees, cntMTees, cntLTees, cntCaps) ;
		semS.release();
	}

 	/* Updates inventory when a medium T shirt is ordred */
	void updateM(Inventory inv, int orderNo, char orderType, int quantity){
		int cntSTees = 0, cntMTees = 0, cntLTees = 0, cntCaps = 0;
		try{ semM.acquire();
			if(countMTees > inv.countMTees)
				 countMTees = inv.countMTees; 
			cntSTees = countSTees;
			cntMTees = countMTees;
			cntLTees = countLTees; 
			cntCaps = countCaps;
		}
		catch(Exception e){ System.out.println("Exception has occured."); }
		int result = 0 ;
		if(quantity > countMTees) result = 1 ;
		else countMTees -= quantity ;
		inv.countMTees = countMTees;
		this.dispInventory(orderNo, result, cntSTees, countMTees, cntLTees, cntCaps) ;
		semM.release();
	}

 	/* Updates inventory when a large T shirt is ordred */
	void updateL(Inventory inv, int orderNo, char orderType, int quantity){
		int cntSTees = 0, cntMTees = 0, cntLTees = 0, cntCaps = 0;
		try{ semL.acquire(); 
			if(countLTees > inv.countLTees)
				 countLTees = inv.countLTees;
			cntSTees = countSTees;
			cntMTees = countMTees;
			cntLTees = countLTees; 
			cntCaps = countCaps;
		}
		catch(Exception e){ System.out.println("Exception has occured."); }
		int result = 0 ;
		if(quantity > countLTees) result = 1 ;
		else countLTees -= quantity ;
		inv.countLTees = countLTees;
		this.dispInventory(orderNo, result, cntSTees, cntMTees, countLTees, cntCaps) ;
		semL.release();
	}

 	/* Updates inventory when a cap is ordred */
	void updateC(Inventory inv, int orderNo, char orderType, int quantity){
		int cntSTees = 0, cntMTees = 0, cntLTees = 0, cntCaps = 0;
		try{ 
			semC.acquire(); 
			if(countCaps > inv.countCaps)
				 countCaps = inv.countCaps;
			cntSTees = countSTees;
			cntMTees = countMTees;
			cntLTees = countLTees; 
			cntCaps = countCaps;
		}
		catch(Exception e){ System.out.println("Exception has occured."); }
		int result = 0 ;
		if(quantity > countCaps) result = 1 ;
		else countCaps -= quantity ;
		inv.countCaps = countCaps;
		this.dispInventory(orderNo, result, cntSTees, cntMTees, cntLTees, countCaps) ;
		semC.release();
	}

	/* Updates inventory while executing order */
	Inventory execOrder(Inventory inv, int orderNo, char orderType, int quantity) { 					//returns 0:success, 1:failure
		switch(orderType) {
			case 'S' :
				updateS(inv, orderNo, orderType, quantity);
				return new Inventory(countSTees, countMTees, countLTees, countCaps, semS, semM, semL, semC, sem) ;
			case 'M' :
				updateM(inv, orderNo, orderType, quantity);
				return new Inventory(countSTees, countMTees, countLTees, countCaps, semS, semM, semL, semC, sem) ;
			case 'L' :
				updateL(inv, orderNo, orderType, quantity);
				return new Inventory(countSTees, countMTees, countLTees, countCaps, semS, semM, semL, semC, sem) ;
			case 'C' :
				updateC(inv, orderNo, orderType, quantity);
				return new Inventory(countSTees, countMTees, countLTees, countCaps, semS, semM, semL, semC, sem) ;
		}

		return new Inventory(countSTees, countMTees, countLTees, countCaps, semS, semM, semL, semC, sem) ;
	}
}

/* contains the inventory and a particular order made at the moment */
class OrderExecutor implements Runnable {
	Inventory inventory, newInv ;
	Order order ;
	/* initialises inventory and order through constructor and starts the thread to execute order */
	OrderExecutor(Inventory inv, Order order) {
		inventory = inv ;
		newInv = inv.getInventory();
		this.order = order ;
		new Thread(this).start() ;
	}
	public void run() {
		inventory = newInv.execOrder(inventory, order.orderNo, order.orderType, order.quantity) ;
	}
}

/* contains details of an order */
class Order {
	int		orderNo ;
	char	orderType ;
	int		quantity ;
	/* Constructor for class to initialise its local variables */
	Order(int orderNo, char orderType, int quantity) {
		this.orderNo = orderNo ;
		this.orderType = orderType ;
		this.quantity = quantity ;
	}
}

/* contains details of all orders */
class OrderBatch {
	List<Order>	orders ;
	/* Constructor for class to initialise its local variables */
	OrderBatch(List<Order> orders) {
		this.orders = orders ;
	}
	/* executes each order from inventory */
	void executeBatch(Inventory inventory) {
		for(Order order : orders) {
			new OrderExecutor(inventory, order) ;
		}
	}
}


class Merchandise {
	public static Inventory globalInv ;
	public static void main(String[] args) throws IOException{
		Semaphore semS, semM, semL, semC, sem; 
		semS = new Semaphore(1); semM = new Semaphore(1); semL = new Semaphore(1); semC = new Semaphore(1); sem = new Semaphore(1);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String data ; int lineNo = 1 ; int noOfOrders = 0 ;
		System.out.println("Enter Inventory details as :- ");

		String[] merchandiseTypes = {"Small size Tshirts", "Medium size Tshirts", "Large size Tshirts", "Caps"} ;
		int[] countArray = new int[4]; 
		int i = 0;
		/* reading input */
		for(String mType : merchandiseTypes){
			System.out.print("Enter Inventory count of " + mType + " : \t");
			data = reader.readLine();
			data = data.trim();
			countArray[i++] = Integer.valueOf(data) ;
		}

		/* Creating inventory with input taken */
		Inventory inventory = new Inventory(countArray[0], countArray[1], countArray[2], countArray[3], semS, semM, semL, semC, sem);

		/* reading input */
		System.out.println("Enter No. of students ordering : ");
		List<Order>	orders = null ;
		data = reader.readLine();
		data = data.trim();
		noOfOrders = Integer.valueOf(data) ;
		orders = new ArrayList<Order>() ;

		System.out.println("Enter data as :- ") ;
		System.out.println("Student_Number	MerchandiseType	 Count") ;
		while(lineNo <= noOfOrders){
			data = reader.readLine();
			data = data.trim();
			String[] tokens = data.split("\\s+") ;
			Order order = new Order(Integer.valueOf(tokens[0]), Character.toUpperCase(tokens[1].charAt(0)), Integer.valueOf(tokens[2])) ;
			orders.add(order) ;
			lineNo++ ;
		}
		reader.close() ;

		/* displays initial inventory */
		inventory.dispInventory(0, 0, countArray[0], countArray[1], countArray[2], countArray[3]) ;

		/* creating batch of orders from input and executing batch orders */
		OrderBatch	batch = new OrderBatch(orders) ;
		batch.executeBatch(inventory) ;
		try{Thread.sleep(1000);}
		catch(Exception e){}
		inventory.dispInventory(0, 0, inventory.countSTees, inventory.countMTees, inventory.countLTees, inventory.countCaps) ;
	}
}
