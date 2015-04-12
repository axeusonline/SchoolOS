package com.ies.schoolos.type;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class BankAccountType extends IndexedContainer{

	private static final long serialVersionUID = 1L;
	
	private static String[] bankAccountTypes = {"บัญชีออมทรัพย์","บัญชีกระแสรายวัน"};

	public BankAccountType() {
		initContainer();
	}
 
	@SuppressWarnings("unchecked")
	private void initContainer(){
	   addContainerProperty("name", String.class,null);
	   for (int i = 0; i < bankAccountTypes.length; i++) {
	        Item item = addItem(i);
	        item.getItemProperty("name").setValue(bankAccountTypes[i]);
	   }
	}
	
	public static String getNameTh(int index){
		return bankAccountTypes[index];
	}
	
	/*public static String getNameEn(int index){
		return bankAccountTypes[index];
	}*/
}
