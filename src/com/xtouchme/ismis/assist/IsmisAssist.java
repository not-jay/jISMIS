package com.xtouchme.ismis.assist;

import com.xtouchme.http.client.methods.HttpRequest;
import com.xtouchme.ismis.Ismis;

public class IsmisAssist {

	private static Ismis ismis = null;
	
	public IsmisAssist() {
		boolean result = ismis.login("10300805", "sjxl8vhrpx");
		System.out.println("Login "+((result)?"successful":"failed")+"!");
		
		if(result) {
			System.out.println("Welcome! "+ismis.getStudent().getName());
			ismis.updateYearLevel();
			ismis.checkAnnouncements();
			ismis.checkBlockList();
			ismis.logout();
		}
	}
	
	public static void main(String args[]) throws Exception {
		//Instantiate the Ismis object which contains our functions
		ismis = new Ismis(new HttpRequest());
		
		//switches handler
		if(args.length != 0) {
			//contains flags
			if(args[0].equals("--verbose") || args[0].equals("-v")) {
				ismis.isVerbose = true;
				System.out.println("Verbose: ON");
			}
		}
		
		new IsmisAssist();
	}
	
}
