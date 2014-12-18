import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class will represent a modified linear probing hash table. 
 * The modification is specified in the comments for the put method.
 */
public class HashTable<K,V> {
	
	/**
	 * Constant determining the max load factor
	 */
	private final double MAX_LOAD_FACTOR = 0.71;
	
	/**
	 * Constant determining the initial table size
	 */
	private final int INITIAL_TABLE_SIZE = 11;
	
	/**
	 * Number of elements in the table
	 */
	private int size;
	
	/**
	 * The backing array of this hash table
	 */
	private MapEntry<K,V>[] table;
	
	/**
	 * Initialize the instance variables
	 * Initialize the backing array to the initial size given
	 */
	
	
	public HashTable() {
		size = 0;
		table =  new MapEntry[INITIAL_TABLE_SIZE];
		
	}
	
	/**
	 * Add the key value pair in the form of a MapEntry
	 * Use the default hash code function for hashing
	 * This is a linear probing hash table so put the entry in the table accordingly
	 * 
	 * Make sure to use the given max load factor for resizing
	 * Also, resize by doubling and adding one. In other words:
	 * 
	 * newSize = (oldSize * 2) + 1
	 *
	 * The load factor should never exceed maxLoadFactor at any point. So if adding this element
	 * will cause the load factor to be exceeded, you should resize BEFORE adding it. Otherwise
	 * do not resize.
	 * 
	 * IMPORTANT Modification: If the given key already exists in the table
	 * then set it as the next entry for the already existing key. This means
	 * that you will never be replacing values in the hashtable, only adding or removing.
	 * This is similar to external chaining
	 * 
	 * @param key This will never be null
	 * @param value This can be null
	 */
	public void put(K key,V value)
	{
		
		if((double)(size+1)/table.length > MAX_LOAD_FACTOR)
		{
			resize();
		}
		
		add(key, value, table);
		size++;
	}
		
	public void add(K key, V value, MapEntry<K,V>[] a)	
	{
		int index = findIndex(key, a);
		
		MapEntry<K,V> entry = new MapEntry(key, value);
		if(!containsNewTable(key, a))//linear probing
		{
			int flag= 0;
			for(int i= index; (i<a.length) && (flag==0); i++)
			{
				if((a[i] == null) || (a[i].isRemoved()))
				{
					a[i] = entry;
					flag=1;
				}
			}
			if(flag==0)
			{
				for(int i= 0; (i<index) && (flag==0); i++)
				{
					if((a[i] == null) || (a[i].isRemoved()))
					{
						a[i] = entry;
						flag=1;
					}
				}
			}
		}//

		else// external chaining
		{
			int flag=0;
			for(int i= 0; (i<a.length) && (flag==0); i++)
			{
				if((a[i]!=null)&&(!a[i].isRemoved())&&(a[i].getKey().equals(key)))
				{
					flag=1;
					MapEntry<K,V> temp = a[i];
					while(temp.getNext() != null)
					{
						temp=temp.getNext();
						
					}

					temp.setNext(entry);
				}
			}
		}
	}

	
	public void resize()
	{
		int newSize = (2*table.length)+1;
		MapEntry<K,V>[] newTable = new MapEntry[newSize];
		MapEntry<K,V>[] oldTable = table;
		for(int i=0; i<oldTable.length ; i++)
		{
			if(oldTable[i] != null) 
			{
				if (oldTable[i].getNext() == null)
				{
					MapEntry<K,V> e = new MapEntry<K,V>(oldTable[i].getKey(), oldTable[i].getValue());
					add(e.getKey(), e.getValue(), newTable);
				}
				else
				{
					MapEntry<K,V> temp = oldTable[i];
					while(temp.getNext()!= null)
					{
						MapEntry<K,V> e = new MapEntry<K,V>(temp.getKey(), temp.getValue());
						add(e.getKey(), e.getValue(), newTable);
						temp=temp.getNext();
					}
					MapEntry<K,V> e = new MapEntry<K,V>(temp.getKey(), temp.getValue());
					add(e.getKey(), e.getValue(), newTable);
				}
				
			}
		}
		
		table = newTable;
		
	}
	
	
	public int findIndex(K key, MapEntry<K,V> a[])
	{
		int hash = key.hashCode();
		int val= Math.abs(hash % (a.length));
		return val;
	}
	
	/**
	 * Remove the entry with the given key.
	 * 
	 * If there are multiple entries with the same key then remove the last one
	 * 
	 * @param key
	 * @return The value associated with the key removed
	 */
	public V remove(K key){
		V val = null;
		int flag=0;
		int index= findIndex(key, table);
		int i= index;
		while((table[i] != null)&&(flag==0))
		{
			if((table[i] !=null)&&(!table[i].isRemoved())&&(table[i].getKey().equals(key)))
			{
				flag=1;
				if(table[i].getNext() == null)
				{
					table[i].setRemoved(true);
					val= table[i].getValue();
				}
				else 
				{
					MapEntry<K,V> temp = table[i];
					while(temp.getNext().getNext() != null)
					{
						temp=temp.getNext();
					}
					MapEntry<K,V> a= temp.getNext();
					temp.setNext(null);
					val= a.getValue();
				}
			}
			i = (i+1) % table.length;
		}
		if(flag == 1){
			size--;
			return val;
		}
		else
			return null;
	}
	
	/**
	 * Checks whether an entry with the given key exists in the hash table
	 * 
	 * @param key
	 * @return
	 */
	public boolean contains(K key){
		int index= findIndex(key, table);
		int i =index;
		while(table[i] !=null)
		{
			if(table[i] == null)
				return false;
			if((!table[i].isRemoved())&&(table[i].getKey().equals(key)))
				return true;	
			i= (i+1) % (table.length);
		}
		return false;
	}
	
	public boolean containsNewTable(K key, MapEntry<K,V> a[]){
	int index= findIndex(key, a);
	int i =index;
	while(a[i] !=null)
	{
		if(a[i] == null)
			return false;
		if((!a[i].isRemoved())&&(a[i].getKey().equals(key)))
			return true;	
		i= (i+1) % (a.length);
	}
	return false;
}
	
	/**
	 * Return a collection of all the values
	 * 
	 * We recommend using an ArrayList here
	 *
	 * @return 
	 */
	public Collection<V> values(){
		ArrayList<V> arr = new ArrayList<V>(size);
		MapEntry<K,V> temp= null;
		for(int i=0; (i<table.length); i++)
		{
			if((table[i] !=null)&&(!table[i].isRemoved()))
			{
				arr.add(table[i].getValue());
				if(table[i].getNext()!=null)
				{
					temp = table[i];
					while(temp.getNext() !=null)
					{
						temp=temp.getNext();
						arr.add(temp.getValue());
					}
				}
			}
		}
		
		
		return arr;
	}
	
	/**
	 * Return a set of all the distinct keys
	 * 
	 * We recommend using a HashSet here
	 * 
	 * Note that the map can contain multiple entries with the same key
	 * 
	 * @return
	 */
	public Set<K> keySet(){
		HashSet<K> set = new HashSet<K>(size);
		for(int i=0; (i<table.length); i++)
		{
			if((table[i] !=null)&&(!table[i].isRemoved()))
				set.add(table[i].getKey());
		}
		
		
		return set;
	}
	
	/**
	 * Return the number of values associated with one key
	 * Return -1 if the key does not exist in this table
	 * @param key
	 * @return
	 */
	public int keyValues(K key){
		int index = findIndex(key, table);
		int flag=0;
		MapEntry<K,V> temp = null;
		for(int i=index; (i<table.length) && (flag==0); i++)
		{
			if((table[i] !=null)&&(!table[i].isRemoved())&&(table[i].getKey().equals(key)))
			{
				temp = table[i];
				flag=1;
			}
		}
		if(flag==0)
		{
			for(int i=0; i<index; i++)
			{
				if((table[i] !=null)&&(!table[i].isRemoved())&&(table[i].getKey().equals(key)))
				{
					temp = table[i];
					flag=1;
				}
			}
		}
		if(flag==1)
		{
			int count = 1;
			while(temp.getNext() != null)
			{
				temp = temp.getNext();
				count++;
			}
			return count;
		}
		else return -1;	
	}
	
	/**
	 * Return a set of all the unique key-value entries
	 * 
	 * Note that two map entries with both the same key and value
	 * could exist in the map.
	 * 
	 * @return
	 */
	public Set<MapEntry<K,V>> entrySet(){
		HashSet<MapEntry<K,V>> set = new HashSet<MapEntry<K,V>>(size);
		MapEntry<K,V> temp= null;
		for(int i=0; (i<table.length); i++)
		{
			if((table[i] !=null)&&(!table[i].isRemoved()))
			{
				set.add(table[i]);
				if(table[i].getNext()!=null)
				{
					temp = table[i];
					while(temp.getNext() !=null)
					{
						temp=temp.getNext();
						set.add(temp);
					}
				}
			}
		}
		return set;
	}
	
	/**
	 * Clears the hash table
	 */
	public void clear(){
		size= 0;
		MapEntry<K,V>[] newTable = new MapEntry[INITIAL_TABLE_SIZE];
		table = newTable;
		
	}
	
	/*
	 * The following methods will be used for grading purposes do not modify them
	 */
	
	public void print()
	{
		for(int i=0; i<table.length; i++)
		{
			if ((table[i]== null)|| (table[i].isRemoved()))
				System.out.println(i +"-->(null,null)");
			if ((table[i]!= null) && (!table[i].isRemoved())&& (table[i].getNext() == null))
				System.out.println(i + "-->" + "(" + table[i].getKey() + "," + table[i].getValue() +") ");
			
			if ((table[i]!= null) && (!table[i].isRemoved()) && (table[i].getNext() != null))
				{
					MapEntry<K,V> temp = table[i];
					while(temp.getNext() != null)
					{
						System.out.print(i + "-->" + "(" + temp.getKey() + "," + temp.getValue() + ") " );
						temp=temp.getNext();
					}
					System.out.print(i + "-->" + "(" + temp.getKey() + "," + temp.getValue() + ")" + "\n");
				}
		}
	}
	
	public int size(){
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public MapEntry<K, V>[] getTable() {
		return table;
	}
	
	public void setTable(MapEntry<K, V>[] table) {
		this.table = table;
	}
}
