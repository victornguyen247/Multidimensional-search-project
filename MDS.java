/** Starter code for P3
 *  @author
 */

// Change to your net id
package vqn240001;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

// If you want to create additional classes, place them in this file as subclasses of MDS

public class MDS {
    // Add fields of MDS here
    // hash map to store Item: key = Id, value = it's Item;
    HashMap<Integer, Item> IdTable;

    // Hash map store distinct description: key = description; value = list of Items
    HashMap<Integer, TreeSet<Item>> DescTable;

    Integer size; // total number of items


    // Item class include ID, price and descriptions of Item
    // Item class store the information of Item: (Integer) Id, (Integer) Price, (Linked List) Descriptions of item, and it's method
    static class Item implements Comparable<Item>{ 
        private Integer Id;
        private Integer Price;
        private LinkedList<Integer> Descriptions; // linked list allows duplication and no order

        public Item(int id, int price, List<Integer> list){
            this.Id = id;
            this.Price = price;
            this.Descriptions = new LinkedList<>(list); 
        }
        // compare 2 Items base on it's price, if same price, compare base on id number
        @Override
        public int compareTo(Item other){
            int comp = this.Price.compareTo(other.Price);
            if(comp != 0) return comp;
            return this.Id.compareTo(other.Id); 
        }
        // method get id
        public Integer getId(){
            return this.Id;
        }
        // method set id
        public void setId(Integer id){
            this.Id = id;
        }
        // method get price
        public int getPrice(){
            return this.Price;
        }
        // method set Price
        public void setPrice(Integer newPrice){
            this.Price = newPrice;
        }
        // method get descriptions
        public LinkedList<Integer> getDescription(){
            return this.Descriptions;
        }
        // method set Description
        public void setDescription(List<Integer> list){
            this.Descriptions = new LinkedList<>(list);
        }
    }
    // Constructors
    public MDS() {
        IdTable = new HashMap<>();
        DescTable = new HashMap<>(10000, 0.8f); // capacity is 10000 and a load factor is 80%
        size = 0;
    }
    
    /* Public methods of MDS. Do not change their signatures.
       __________________________________________________________________
       a. Insert(id,price,list): insert a new item whose description is given
       in the list.  If an entry with the same id already exists, then its
       description and price are replaced by the new values, unless list
       is null or empty, in which case, just the price is updated. 
       Returns 1 if the item is new, and 0 otherwise.
    */
    public int insert(int id, int price, java.util.List<Integer> list) {
        // check item same id exist or not
        if(IdTable.containsKey(id)){
            // get the exist item contain id
            Item exist_item = IdTable.get(id);
            
            // remove all connection of old description with item
            updateDescTable(exist_item, false);
            
            //check the list description is not null and not empty
            if(!(list == null || list.isEmpty())){   
                // set new list of description into item
                exist_item.setDescription(list);
                // set new price
                exist_item.setPrice(price); 
            }else {
            	// list is null or empty, just update price
            	// set new price for item
            	exist_item.setPrice(price);   	
            }
            
            // update new connection of description with item
        	updateDescTable(exist_item, true);
            return 0; // return 0 if item has exist
        }
        
        // create new item 
        Item newitem = new Item(id, price, list);

        // put new item into Id table
        IdTable.put(id, newitem);

        // add new item into Description table
        updateDescTable(newitem, true);

        size ++; // increasing number items
        return 1; // return 1 if item is new
    }

    /* help method for update Description table
     if isAdding true, then add item into relative descriptions
     if isAdding false, then remove item from relative descriptions */
    private void updateDescTable(Item item, boolean isAdding){
        // traversal each description in item descriptions
        for(int d : item.getDescription()){
            // adding item into description table
            if(isAdding){
                // check is new description?
                if(!(DescTable.containsKey(d))){
                    // create tree set, then add item into it
                    TreeSet<Item> Itree = new TreeSet<>();  
                    Itree.add(item);
                    DescTable.put(d, Itree); // store description into DescTable
                }else{
                // add item into exist description 
                    DescTable.get(d).add(item);
                }
            }
            // remove item from description table 
            else{
                TreeSet<Item> Itree = DescTable.get(d);
                // remove item from description if it is exist
                if(Itree != null){
                    Itree.remove(item);
                    // remove description if it not contain any item.
                    if(Itree.isEmpty()) DescTable.remove(d);
                }
            }
        }
    }

    // b. Find(id): return price of item with given id (or 0, if not found).
    public int find(int id) {
        if(id < 0) return 0;
        // return item's price if it is exist
    return (IdTable.containsKey(id))? IdTable.get(id).getPrice(): 0;
    }

    /* 
       c. Delete(id): delete item from storage.  Returns the sum of the
       ints that are in the description of the item deleted,
       or 0, if such an id did not exist.
    */
    public int delete(int id) {
	    Item item = IdTable.get(id);         // get the item from Id Table
        
        // item of this id not exist
        if( item == null ) return 0;

        int sum = 0; // total of descriptions
        // calculate the total of descriptions
        for(int d : item.getDescription()) sum += d;   // update sum value

        // remove all connection of old description with item
        updateDescTable(item, false);

        // Remove the item from Id Table
        IdTable.remove(id);
    return sum;
    }

    /* 
       d. FindMinPrice(n): given an integer, find items whose description
       contains that number (exact match with one of the ints in the
       item's description), and return lowest price of those items.
       Return 0 if there is no such item.
    */
    public int findMinPrice(int n) {
	    // get tree set item relate description n
        TreeSet<Item> itemTree = DescTable.get(n);
        
        // return 0 if description n not exist
        if( itemTree == null) return 0;
        
        // find the lowest item
        Item item = itemTree.first();
	return item.getPrice(); 	// return price of lowest item
    }

    /* 
       e. FindMaxPrice(n): given an integer, find items whose description
       contains that number, and return highest price of those items.
       Return 0 if there is no such item.
    */
    public int findMaxPrice(int n) {
	// get tree set item relate description n
        TreeSet<Item> itemTree = DescTable.get(n);
        
        // return 0 if description n not exist
        if( itemTree == null) return 0;
        
        // find the highest item
        Item item = itemTree.last();
	return item.getPrice();		// return price of highest item
    }

    /* 
       f. FindPriceRange(n,low,high): given int n, find the number
       of items whose description contains n, and in addition,
       their prices fall within the given range, [low, high].
    */
    public int findPriceRange(int n, int low, int high) {
    	// check low > high
    	if( low > high) return 0;
    	
	    // get tree set item relate description
        TreeSet<Item> itemTree = DescTable.get(n);
        
        // return 0 if description n not exist
        if( itemTree == null) return 0;
    
        // find the item in range (low, high)
        	// Create dummy items for range search
        Item low_bound = new Item(-1, low, new LinkedList<>());						
        Item high_bound = new Item(Integer.MAX_VALUE, high, new LinkedList<>());
        	// sub tree contains item from price low -> high
        TreeSet<Item> sub_TreeSet =(TreeSet<Item>) itemTree.subSet(low_bound, false, high_bound, false); 
        return sub_TreeSet.size();
    }

    /*
      g. RemoveNames(id, list): Remove elements of list from the description of id.
      It is possible that some of the items in the list are not in the
      id's description.  Return the sum of the numbers that are actually
      deleted from the description of id.  Return 0 if there is no such id.
    */
    public int removeNames(int id, java.util.List<Integer> list) {
        // get the exist item by id
	    Item exist_item = IdTable.get(id);
        // the item not exist
        if(exist_item == null) return 0;

        // set of item's descriptions
        LinkedList<Integer> item_desc = exist_item.getDescription();
        
        // calculate the sum of the numbers that are actually
        // deleted from the description of id
        int sum = 0;		// total of removal description 
        for(Integer d: list) {
            // check d is in item's descriptions
            if(item_desc.contains(d)) {
                DescTable.get(d).remove(exist_item); // remove connect of description with item in Desc table
                // remove description if it is empty
                if(DescTable.get(d).isEmpty()) DescTable.remove(d);
                
                // calculate sum and remove description d from item's descriptions
                sum += d; // update sum values
                // remove description which can be duplication
                while(item_desc.contains(d)) {
                	item_desc.remove(d);
                }
            }            
        }
        return sum;
    }
}

