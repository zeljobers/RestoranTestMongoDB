package main;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import databinding.Contact;
import databinding.Databinding;
import databinding.Location;
import databinding.Restaurant;

public class CreateJson {

	private static List<Restaurant> ReadCsvToPojo() {
		try{
			FileReader fileReader = new FileReader("yellowpages.csv");
			CSVParser parser = new CSVParserBuilder().withSeparator(',').build();
			CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).withCSVParser(parser).build();
			
			String[] nextRecord;
			List<Restaurant> restaurants = new ArrayList<Restaurant>();
			int i = 1;
			while((nextRecord = csvReader.readNext()) != null) {
				Restaurant r = new Restaurant();
				// nested key lvl 1
				Contact c = new Contact();
				// nested key lvl 2
				Location l = new Location();
				if(nextRecord[0] != null && nextRecord[0] != "") {
					r.setUniqId(nextRecord[0]);
				}
				if(nextRecord[1] != null && nextRecord[1] != "") {
					r.setUrl(nextRecord[1]);
				}
				if(nextRecord[2] != null && nextRecord[2] != "") {
					r.setName(nextRecord[2]);			
				}
				if(nextRecord[3] != null && nextRecord[3] != "") {
					l.setStreet(nextRecord[3]);
				}
				if(nextRecord[4] != null && nextRecord[4] != "") {
					l.setZipCode(nextRecord[4]);
				}
				if(nextRecord[5] != null && nextRecord[5] != "") {
					l.setCity(nextRecord[5]);
				}
				if(nextRecord[6] != null && nextRecord[6] != "") {
					l.setState(nextRecord[6]);
				}
				if(nextRecord[7] != null && nextRecord[7] != "") {
					c.setPhone(nextRecord[7]);
				}
				if(nextRecord[8] != null && nextRecord[8] != "") {
					c.setEmail(nextRecord[8]);
				}
				
				c.setLocation(l);
				r.setContact(c);

				if(nextRecord[9] != null && nextRecord[9] != "") {
					r.setWebsite(nextRecord[9]);
				}
				List<String> categories = new ArrayList<String>();
				if(nextRecord[10] != null && nextRecord[10] != "") {
					Collections.addAll(categories, nextRecord[10].split(","));
					r.setCategories(categories);
				}
				
				for(int j = 0;  j < nextRecord.length; j++) {
					System.out.println(nextRecord[j]);
				}
				System.out.println("---"+i);
				restaurants.add(r);
				i++;
			}
			csvReader.close();
			fileReader.close();
			
			return restaurants;
		} catch (IOException e) {
			System.out.println("Exception triggered on file read.");
			e.printStackTrace();
			return null;
		}
		
	}
	
	private static Boolean WriteToDB(List<Restaurant> restaurants) {
		try {
			ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017");
//			ConnectionString connectionString = new ConnectionString("mongodb+srv://rootara:rootara@projekat.sazp8.mongodb.net/projekat?retryWrites=true&w=majority");
			CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
			CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
					pojoCodecRegistry);
			MongoClientSettings clientSettings = MongoClientSettings.builder().applyConnectionString(connectionString)
					.codecRegistry(codecRegistry).build();
			MongoClient mongoClient = MongoClients.create(clientSettings);
			MongoDatabase db = mongoClient.getDatabase("projekat");
			MongoCollection<Restaurant> col = db.getCollection("restorani", Restaurant.class);
			
			col.insertMany(restaurants);
			
			mongoClient.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Restaurant> restaurants = ReadCsvToPojo();
		
		if(restaurants != null) {
			Databinding.PojoToJson(restaurants);
//			System.out.println(restaurants.get(0).getContact().getEmail());
			List<Restaurant> tempRestaurants = Databinding.JsonToPojo("restaurant_dataBinding.json");
			if(tempRestaurants != null) {
				if(false != WriteToDB(restaurants)) {
					System.out.println("Uspeh!");
				}
			}
		}
		
	}
}
