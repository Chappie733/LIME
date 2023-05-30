package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONUtils {

	// salva l'oggetto json passato in un file .json 
	public static void writeJSONFile(JSONObject obj, String filepath) {
		try {
			String project_dir = System.getProperty("user.dir");
			FileWriter writer = new FileWriter(project_dir + filepath);
			writer.write(obj.toJSONString());
			writer.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	// salva l'array json passato in un file .json 
	public static void writeJSONFile(JSONArray obj, String filepath) {
		try {
			String project_dir = System.getProperty("user.dir");
			FileWriter writer = new FileWriter(project_dir + filepath);
			writer.write(obj.toJSONString());
			writer.close();
		} catch (IOException e) { e.printStackTrace(); }
	}

	// prova a caricare un oggetto json da un file .json
	public static JSONObject readJSONObject(String filepath) {
        JSONParser jsonParser = new JSONParser();
        try  {
    		String project_dir = System.getProperty("user.dir") + "\\";
        	FileReader reader = new FileReader(project_dir + filepath);
        	return (JSONObject) jsonParser.parse(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	// prova a caricare un oggetto json da un file .json
	public static JSONObject readJSONObject(File file) {
        JSONParser jsonParser = new JSONParser();
        try  {
        	FileReader reader = new FileReader(file);
        	return (JSONObject) jsonParser.parse(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	// prova a caricare un oggetto json da un file .json
	public static JSONArray readJSONArray(String filepath) {
		JSONParser jsonParser = new JSONParser();
		try  {
			String project_dir = System.getProperty("user.dir");
			FileReader reader = new FileReader(project_dir + filepath);
			return (JSONArray) jsonParser.parse(reader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
		
	@SuppressWarnings("unchecked")
	public static <T> JSONArray toJSONArray(T[] arr) {
		JSONArray j_arr = new JSONArray();
		for (T obj : arr)
			j_arr.add(obj);
		return j_arr;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> JSONArray toJSONArray(List<T> arr) {
		JSONArray j_arr = new JSONArray();
		for (T obj : arr)
			j_arr.add(obj);
		return j_arr;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> toList(JSONArray arr) {
		ArrayList<T> list = new ArrayList<T>();
		for (Object obj : arr)
			list.add((T) obj);
		return list;
	}
}
