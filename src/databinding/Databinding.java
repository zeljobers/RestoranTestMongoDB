package databinding;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Databinding {
	public static void PojoToJson(List<Restaurant> restaurants) {
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> restaurantDataMapList = 
				mapper.convertValue(restaurants, new TypeReference<List<Map<String, Object>>>() {});
		
		try {
			mapper.writerWithDefaultPrettyPrinter()
				  .writeValue(new File("restaurant_dataBinding.json"), 
						      restaurantDataMapList);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<Restaurant> JsonToPojo(String jsonPath) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<Restaurant> mapRead = mapper.readValue(new File(jsonPath), new TypeReference<List<Restaurant>>(){});
			// ispise prvu vrednost
//			System.out.println("Ispis prve vrednosti---\n"+
//				mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapRead.get(0))
//			);
			return mapRead;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
