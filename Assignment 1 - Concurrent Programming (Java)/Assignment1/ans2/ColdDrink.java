import java.util.concurrent.* ;
import java.util.* ;
import java.io.* ;
import java.util.concurrent.locks.*;

class Godown {
	int b1, b2, totalB1, totalB2;
	ReentrantLock reentLock;

	// Constructor of class to initialise local variables 
	Godown(int count1, int count2, int cntB1, int cntB2, ReentrantLock rLock) {
		b1 = count1 ;
		b2 = count2 ;
		totalB1 = cntB1 ;
		totalB2 = cntB2 ;
		reentLock = rLock;
	}

	// increments the count of bottles of type 1 in godown
	void incB1Cnt(){
		reentLock.lock();
		try{ this.b1++; }
		catch (Exception e) { System.out.println("Exception has occurred"); } 
		finally { reentLock.unlock(); }

		return;
	}

	// increments the count of bottles of type 2 in godown
	void incB2Cnt(){
		reentLock.lock();
		try{ this.b2++; }
		catch (Exception e) { System.out.println("Exception has occurred"); } 
		finally { reentLock.unlock(); }

		return;
	}	
}


class Bottle {
	/* type:1 -> B1, type:2 -> B2 */
	int type ;

	/*	status:0 -> uprocessed, status:1 -> packaged, status:2 -> sealed, status:3 ->packaged and sealed
		LSD bit -> packaging status, MSD bit -> sealing status
	*/
	int status ;

	/* Constructor of class to initialise local variables */
	Bottle(int type) {
		this.type = type;
		this.status = 0 ;
	}
}


class UnfinishedTray{
	List<Bottle>	bottlesB1 ;
	List<Bottle>	bottlesB2 ;
	/*  Type of last Unfinished Bottle for Sealing Unit  */ 
	int lastBottleTypePck;
	/*  Type of last Unfinished Bottle for Packing Unit  */
	int lastBottleTypeSeal;	
	/*  Reentrant lock : member */
	ReentrantLock reentLock;

	// Constructor of class to initialise local variables & both bottle array lists 
	UnfinishedTray(int count1, int count2, ReentrantLock rLock) {
		lastBottleTypeSeal = 1 ;
		lastBottleTypePck = 2 ;
		reentLock = rLock;

		bottlesB1 = new ArrayList<Bottle>(count1) ;
		for(int i=0;i<count1; i++)
			bottlesB1.add(new Bottle(1)) ;

		bottlesB2 = new ArrayList<Bottle>(count2) ;
		for(int i=0;i<count2; i++)
			bottlesB2.add(new Bottle(2)) ;
	}

	/* returns size of B1 Array List */
	int getB1ListSize() {
		int sz = 0;

		reentLock.lock();
		try{ sz = bottlesB1.size(); }
		catch (Exception e) { System.out.println("Exception has occurred"); } 
		finally { reentLock.unlock(); }

		return sz;
	}

	/* returns size of B2 Array List */
	int getB2ListSize(){
		int sz = 0;

		reentLock.lock();
		try{ sz = bottlesB2.size(); }
		catch (Exception e) { System.out.println("Exception has occurred"); } 
		finally { reentLock.unlock(); }

		return sz;
	}

	/* removes front Bottle from B1 Buffer Tray & returns it */
	Bottle popB1Bottle(){
		Bottle b = new Bottle(0);

		reentLock.lock();
		try{ b = bottlesB1.remove(0); }
		catch (Exception e) { System.out.println("Exception has occurred"); } 
		finally { reentLock.unlock(); }

		return b;
	}

	/* removes front Bottle from B1 Buffer Tray & returns it */
	Bottle popB2Bottle(){
		Bottle b = new Bottle(0);
		
		reentLock.lock();
		try{ b = bottlesB2.remove(0); }
		catch (Exception e) { System.out.println("Exception has occurred"); } 
		finally { reentLock.unlock(); }

		return b;
	}
} 

class BufferTray{
	/* number of bottles of type 1 and type 2 respectively */
	int b1, b2 ;
	int capacity ;
	/* type:1 -> B1, type:2 -> B2 */
	int type ; 			
	/* List of Bottles in Tray */ 		
	List<Bottle>	tray ;
	/*  Reentrant lock : member */
	ReentrantLock reentLock;

	/* Constructor of class BufferTray to initialise local variables, bottle tray list and reentrant lock */
	BufferTray(int count1, int count2, int capacity, int type, ReentrantLock rLock){
		b1 = count1 ;
		b2 = count2 ;
		this.capacity = capacity ;
		this.type = type ;
		reentLock = rLock;
		tray = new ArrayList<Bottle>() ;
	}

	/* returns size of bottle tray list and unlocks the reentrant lock */
	int getListSize(){
		int sz = 0;
		
		reentLock.lock();
		try{ sz = this.tray.size(); }
		catch (Exception e) { System.out.println("Exception has occurred"); } 
		finally { reentLock.unlock(); }

		return sz;
	}

	/* pushes the Bottle b to the current Buffer Tray list */
	void pushBottle(Bottle b){
		
		reentLock.lock();
		try{ this.tray.add(b); }
		catch (Exception e) { System.out.println("Exception has occurred"); } 
		finally { reentLock.unlock(); }

		return;
	}

	/* pops a Bottle b from the current Buffer Tray list */
	Bottle popBottle(){
		Bottle b = new Bottle(0);
		
		reentLock.lock();
		try{ b = this.tray.remove(0); }
		catch (Exception e) { System.out.println("Exception has occurred"); } 
		finally { reentLock.unlock(); }

		return b;
	}
}


class Sealer{
	UnfinishedTray unfinished ;
	BufferTray	sealTray, packB1Tray, packB2Tray ;
	Godown godown ;
	Bottle lastBottle;
	/*  Type of last Unfinished Bottle for Sealing Unit  */ 
	int lastBottleType;	
	/* isOccupied:true -> sealing unit still has a bottle,  isOccupied:false -> sealing unit has no bottle at the moment */ 
	boolean isOccupied ;
	/* time remaining to completely seal bottle */
	int timeLeft;
	/* total number of bottles of type 1 sealed till now */
	int sealedcntB1;
	/* total number of bottles of type 2 sealed till now */
	int sealedcntB2;

	/* Constructor of class to initialise local variables */
	Sealer(UnfinishedTray unfinished, BufferTray packB1Tray, BufferTray packB2Tray, BufferTray sealTray, Godown godown) {
		this.unfinished = unfinished ;
		this.packB1Tray = packB1Tray ;
		this.packB2Tray = packB2Tray ;
		this.sealTray = sealTray ;
		this.godown = godown ;
		this.isOccupied = false;
		this.timeLeft = 0;
		this.lastBottleType=2;
		this.lastBottle = new Bottle(0);
		this.sealedcntB1 = 0;
		this.sealedcntB2 = 0;
	}

	/* sends sealed bottle of type 1 to Packaging unit if possible */
	void toPackerB1(Bottle bottle) {
		if (packB1Tray.getListSize() < packB1Tray.capacity)
			packB1Tray.pushBottle(bottle);
		else
			isOccupied = true;
	}

	/* sends sealed bottle of type 2 to Packaging unit if possible */
	void toPackerB2(Bottle bottle) {
		if (packB2Tray.getListSize() < packB2Tray.capacity)
			packB2Tray.pushBottle(bottle);
		else
			isOccupied = true;												
	}

	/* sends sealed bottle to godown if it is also packaged */
	void toGodown(Bottle bottle) {
		if (bottle.type == 1)
			godown.incB1Cnt();
		else
			godown.incB2Cnt();
	}

	/* returns bottle popped from the required Tray (buffer tray/ unfinished tray) */
	Bottle getFromTray() {
		Bottle b = new Bottle(0);

		if (sealTray.getListSize()>0){
			b = sealTray.popBottle();
			this.lastBottleType = b.type;
		}
		else if (unfinished.getB2ListSize()>0 || unfinished.getB1ListSize()>0){
			if (unfinished.lastBottleTypeSeal == 1 && unfinished.getB2ListSize()>0){
				unfinished.lastBottleTypeSeal = 2; 
				this.lastBottleType = 2;
				return unfinished.popB2Bottle();
			}
			else if (unfinished.getB1ListSize()>0){				
				unfinished.lastBottleTypeSeal = 1; 
				this.lastBottleType = 1;
				return unfinished.popB1Bottle();
			}
			unfinished.lastBottleTypeSeal = 2; 
			this.lastBottleType = 2;
			return unfinished.popB2Bottle();
		}
		
		return b;
	}

	/* stores bottle for atleast 3 minutes for sealing and then sends it to either a buffer Tray for packaging unit or to godown or keeps it for more time if required */
	void processBottle(UnfinishedTray unfinished, BufferTray packB1Tray, BufferTray packB2Tray, BufferTray sealTray, Godown godown, Packer packagingUnit) {
		String name = Thread.currentThread().getName();
		
		Bottle bottle = lastBottle;

		if(this.timeLeft != 0){
			this.timeLeft--;
			if(this.timeLeft == 0){
				if(bottle.type==1) sealedcntB1++;
				else sealedcntB2++;
				bottle.status |= 2 ;
				if(bottle.status == 2){ 		// only sealed

					if (bottle.type == 1) {
						toPackerB1(bottle) ;
					}
					else{
						toPackerB2(bottle) ;
					}
				}
				else if(bottle.status == 3) { 	// packaged and sealed
					toGodown(bottle) ;
				}
			}
			else return;
		}
		
		if(!isOccupied){
			if((unfinished.getB2ListSize()>0 || unfinished.getB1ListSize()>0) || (sealTray.getListSize() > 0)){
				bottle = getFromTray() ;
				lastBottle = bottle;
				this.timeLeft+=3;
			}
			return;
		}
		else{
			if (this.lastBottleType == 1 && packB1Tray.getListSize() < packB1Tray.capacity){
				isOccupied = false;
			}
			else if (this.lastBottleType == 2 && packB2Tray.getListSize() < packB2Tray.capacity){
				isOccupied = false;
			}

			if (!isOccupied){
				if(bottle.status == 2) { 		// only sealed
					if (bottle.type == 1) {
						toPackerB1(bottle) ;
					}
					else{
						toPackerB2(bottle) ;
					}
				}
				if((unfinished.getB2ListSize()>0 || unfinished.getB1ListSize()>0) || (sealTray.getListSize() > 0)) {
					bottle = getFromTray() ;
					lastBottle = bottle;
					this.timeLeft+=3;
				}
			}
			return;
		}
	}
}

class Packer{
	UnfinishedTray unfinished ;
	BufferTray	sealTray, packB1Tray, packB2Tray ;
	Godown godown ;
	Bottle lastBottle;
	/*  Type of last Unfinished Bottle for Packaging Unit  */ 
	int lastBottleType;	
	/* isOccupied:true -> packaging unit still has a bottle,  isOccupied:false -> packaging unit has no bottle at the moment */ 
	boolean isOccupied ;
	/* time remaining to completely pack bottle */
	int timeLeft;
	/* total number of bottles of type 1 packed till now */
	int sealedcntB1;
	/* total number of bottles of type 2 packed till now */
	int sealedcntB2;

	/* Constructor of class to initialise local variables */
	Packer(UnfinishedTray unfinished, BufferTray packB1Tray, BufferTray packB2Tray, BufferTray sealTray, Godown godown) {
		this.unfinished = unfinished ;
		this.packB1Tray = packB1Tray ;
		this.packB2Tray = packB2Tray ;
		this.sealTray = sealTray ;
		this.godown = godown ;
		this.isOccupied = false;
		this.timeLeft = 0;
		this.lastBottleType=2;
		this.lastBottle = new Bottle(0);
		this.packedcntB1 = 0;
		this.packedcntB2 = 0;
	}

	/* sends packed bottle to Sealing unit if possible */
	void toSealer(Bottle bottle) {
		if (sealTray.getListSize() < sealTray.capacity)
			sealTray.pushBottle(bottle);
		else
			isOccupied = true;
	}

	/* sends packed bottle to godown if it is also sealed */
	void toGodown(Bottle bottle) {
		if (bottle.type == 1)
			godown.incB1Cnt();
		else
			godown.incB2Cnt();
	}

	/* returns bottle popped from the required Tray (B1 buffer tray/ B2 buffer tray/ unfinished tray) */
	Bottle getFromTray() {
		Bottle b = new Bottle(0);
		if(packB1Tray.getListSize() > 0 || packB2Tray.getListSize() > 0){
			if (lastBottleType == 2){
				if (packB1Tray.getListSize() > 0)
					b = packB1Tray.popBottle();
				else if (packB2Tray.getListSize() > 0)
					b = packB2Tray.popBottle();

				this.lastBottleType = b.type;
				return b;
			}
			else if (lastBottleType == 1){
				if (packB2Tray.getListSize() > 0)
					b = packB2Tray.popBottle();
				else if (packB1Tray.getListSize()  > 0)
					b = packB1Tray.popBottle();

				this.lastBottleType = b.type;
				return b;
			}
		}

		if (unfinished.getB1ListSize()>0 || unfinished.getB2ListSize()>0){
			if (unfinished.lastBottleTypePck == 1 && unfinished.getB2ListSize()>0){
				b = unfinished.popB2Bottle();
			}
			else{
				if (unfinished.getB1ListSize()>0){	
					b = unfinished.popB1Bottle();
				}
				else{
					b = unfinished.popB2Bottle();
				}
			}
		}
		unfinished.lastBottleTypePck = b.type; 
		return b;
	}

	/* stores bottle for atleast 2 minutes for packaging and then sends it to either buffer tray for sealing unit or to godown or keeps it for more time if required */
	void processBottle(UnfinishedTray unfinished, BufferTray packB1Tray, BufferTray packB2Tray, BufferTray sealTray, Godown godown, Sealer sealingUnit) {
		String name = Thread.currentThread().getName();
		Bottle bottle = lastBottle;
	 
		if(timeLeft != 0){
			timeLeft--;
			if(timeLeft == 0){
				if(bottle.type==1) packedcntB1++;
				else packedcntB2++;
				bottle.status |= 1 ;
				if(bottle.status == 1){  // only packaged
					toSealer(bottle) ;
				}
				else if(bottle.status == 3){  //packaged and sealed
					toGodown(bottle) ;
				}
			}
			else return;
		}

		if(!isOccupied){
			if((unfinished.getB2ListSize()>0 || unfinished.getB1ListSize()>0) || (packB1Tray.getListSize() > 0 || packB2Tray.getListSize() > 0)){
				bottle = getFromTray() ;
				lastBottle = bottle;
				timeLeft+=2;
			}
			return;
		}
		else{
			if(sealTray.getListSize() < sealTray.capacity)
				isOccupied = false;

			if(!isOccupied){
				if(bottle.status == 1){  // only sealed
					toSealer(bottle) ;
				}

			if((unfinished.getB2ListSize()>0 || unfinished.getB1ListSize()>0) || (packB1Tray.getListSize() > 0 || packB2Tray.getListSize() > 0)){					
					bottle = getFromTray() ;
					lastBottle = bottle;
					timeLeft+=2;
				}
			}
			return;
		}
	}
}


class ProcessThread implements Runnable{
	UnfinishedTray unfinished;
	BufferTray packB1Tray;
	BufferTray packB2Tray;
	BufferTray sealTray;
	Godown godown;
	Sealer sealingUnit;
	Packer packagingUnit;
	boolean flag;

	/* Constructor of class to initialise local variables */
	ProcessThread(boolean flg, UnfinishedTray unfinished, BufferTray packB1Tray, BufferTray packB2Tray, BufferTray sealTray, Godown godown, Sealer sUnit, Packer pUnit) {
		this.unfinished = unfinished ;
		this.packB1Tray = packB1Tray ;
		this.packB2Tray = packB2Tray ;
		this.sealTray = sealTray ;
		this.godown = godown;
		this.sealingUnit = sUnit;
		this.packagingUnit = pUnit;
		this.flag = flg;
	}

	/* thread either seals bottle or packages it */
	public void run() {
		try {
            if(!flag) {
                sealingUnit.processBottle(unfinished, packB1Tray, packB2Tray, sealTray, godown, packagingUnit);
            }
            else{
                packagingUnit.processBottle(unfinished, packB1Tray, packB2Tray, sealTray, godown, sealingUnit);
            }
        } 
        catch (final Exception e) {
            System.out.println("Exception has occurred");
        }
	}
}

class ColdDrink{
	public static void main(String[] args) throws Exception {
		System.out.println("Enter data as given :-");
		System.out.println("countB1\tcountB2\tobsTime");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String data = reader.readLine() ;
		String[] tokens = data.split("\\s") ;
		int countB1 = Integer.valueOf(tokens[0]) ;
		int countB2 = Integer.valueOf(tokens[1]) ;
		int obsTime = Integer.valueOf(tokens[2]) ;

		/* lock for each buffer tray, unfinished tray and godown */
		ReentrantLock reentLockG, reentLockB1, reentLockB2, reentLockS, reentLockU;		
		reentLockG = new ReentrantLock(); reentLockB1 = new ReentrantLock(); reentLockB2 = new ReentrantLock(); reentLockS = new ReentrantLock(); reentLockU = new ReentrantLock();

		Godown godown = new Godown(0,0,countB1,countB2, reentLockG);
		UnfinishedTray unfinished = new UnfinishedTray(countB1, countB2, reentLockU) ;
		BufferTray packB1Tray = new BufferTray(0, 0, 2, 1, reentLockB1) ; //ip : 'unfinished' and 'sealer', op : 'sealer' and godown
		BufferTray packB2Tray = new BufferTray(0, 0, 3, 2, reentLockB2) ; //ip : 'unfinished' and 'sealer', op : 'sealer' and godown
		BufferTray sealTray = new BufferTray(0, 0, 2, 0, reentLockS) ; //ip : 'unfinished' and 'packer', op : 'packer' and godown

		Sealer sealingUnit = new Sealer(unfinished, packB1Tray, packB2Tray, sealTray, godown) ;
		Packer packagingUnit = new Packer(unfinished, packB1Tray, packB2Tray, sealTray, godown) ;

		ProcessThread sUnit, pUnit;

		for (int i=0; i<=obsTime; i++) {
			if(godown.b1 + godown.b2 < godown.totalB1 + godown.totalB2) {
				/* two threads initialised, one each for packaging unit and sealing unit */
				sUnit = new ProcessThread(false, unfinished, packB1Tray, packB2Tray, sealTray, godown, sealingUnit, packagingUnit) ;
				pUnit = new ProcessThread(true, unfinished, packB1Tray, packB2Tray, sealTray, godown, sealingUnit, packagingUnit) ;
				Thread tSeal = new Thread(sUnit, "Thread_Seal");
				Thread tPack = new Thread(pUnit, "Thread_Pack");

				/* 
				   if both Packaging & Sealing Unit have just completed the processing,
				   then let the Sealing Unit to process first and update the respective trays. 
				   Following the completion of this thread only, the Packaging Unit thread too could process 
				*/
				if(packagingUnit.timeLeft == 1 && sealingUnit.timeLeft == 1){
					tSeal.start();
	                tSeal.join();
	                tPack.start();
	                tPack.join();
				}
				/* Else let both the threads to process simultaneously */
				else{
					tSeal.start();
                	tPack.start();
                	tSeal.join();
                	tPack.join();
				}
			}
		}

		System.out.println("");
		System.out.println("B1\tPackaged :\t" + packagingUnit.packedcntB1);
        System.out.println("B1\tSealed :\t" + sealingUnit.sealedcntB1);
        System.out.println("B1\tIn Godown :\t" + godown.b1);
        System.out.println("B2\tPackaged :\t" + packagingUnit.packedcntB2);
        System.out.println("B2\tSealed :\t" + sealingUnit.sealedcntB2);
        System.out.println("B2\tIn Godown :\t" + godown.b2);
	}
}
